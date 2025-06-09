package com.aliyun.gts.gmall.center.trade.core.message.consumer;


import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.center.trade.core.domainservice.EvoucherCreateService;
import com.aliyun.gts.gmall.center.trade.core.domainservice.EvoucherJudgementService;
import com.aliyun.gts.gmall.center.trade.core.domainservice.ManzengGiftService;
import com.aliyun.gts.gmall.center.trade.core.domainservice.ManzengGrantService;
import com.aliyun.gts.gmall.center.trade.domain.entity.evoucher.EvoucherInstance;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.middleware.api.cache.lock.DistributedLock;
import com.aliyun.gts.gmall.middleware.api.mq.anns.MQConsumer;
import com.aliyun.gts.gmall.middleware.api.mq.consumer.ConsumeEventProcessor;
import com.aliyun.gts.gmall.middleware.api.mq.model.StandardEvent;
import com.aliyun.gts.gmall.platform.trade.api.dto.message.OrderMessageDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@MQConsumer(
    groupId = "${trade.order.statuschange.groupId}",
    topic = "${trade.order.statuschange.topic}",
    tag = OrderWaitSendConsumer.STATUS
)
public class OrderWaitSendConsumer implements ConsumeEventProcessor {

    static final String STATUS = "64"; //String.valueOf(OrderStatusEnum.ORDER_WAIT_DELIVERY.getCode());

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private EvoucherJudgementService evoucherJudgementService;

    @Autowired
    private TcOrderRepository tcOrderRepository;

    @Autowired
    private EvoucherCreateService evoucherCreateService;

    @Autowired
    private ManzengGiftService manzengGiftService;

    @Autowired
    private ManzengGrantService manzengGrantService;

    @Autowired
    private CacheManager cacheManager;

    @Override
    public boolean process(StandardEvent event) {
        log.info("OrderWaitSendConsumer event msg = " + JSONObject.toJSONString(event));
        OrderMessageDTO message = (OrderMessageDTO) event.getPayload().getData();
        if (message.getPrimaryOrderId().longValue() != message.getOrderId().longValue()) {
            // 忽略子订单消息
            return true;
        }

        MainOrder mainOrder = orderQueryAbility.getMainOrder(message.getPrimaryOrderId());
        if (mainOrder == null) {
            log.info("no order: " + message.getPrimaryOrderId());
            return true;
        }
        log.info("current mainOrder status: {}" ,mainOrder.getPrimaryOrderStatus());
        if (!OrderStatusEnum.ACCEPTED_BY_MERCHANT.getCode().equals(mainOrder.getPrimaryOrderStatus()) && !OrderStatusEnum.ACCEPTED_BY_MERCHANT.getCode().equals(message.getPrimaryOrderStatus())) {
            log.info("not ORDER_WAIT_DELIVERY: {},mainOrder:{},message status:{}", message.getPrimaryOrderId(),mainOrder.getPrimaryOrderStatus(),message.getPrimaryOrderStatus());
            return true;
        }

        // 赠品活动
        manzengGiftService.onOrderPaid(mainOrder);
        manzengGrantService.giftOrderGrantItem(mainOrder);
        manzengGrantService.onManzengPaid(message);

        if (evoucherJudgementService.isEvOrder(mainOrder)) {
            // 防止重发, 加锁后判断
            processInLock(mainOrder.getPrimaryOrderId(), this::sendEv);
        }
        return true;
    }

    private boolean processInLock(Long primaryOrderId, Consumer<Long> c) {
        // 加锁
        String key = "OrderWaitSendConsumer_" + primaryOrderId;
        DistributedLock lock = cacheManager.getLock(key);
        try {
            boolean b = lock.tryLock(2000, 20000);
            if (!b) {
                return false;  // 重试
            }
            c.accept(primaryOrderId);
            return true;
        } catch (Exception e) {
            log.error("OrderWaitSendConsumer exception, primaryOrderId = {}", primaryOrderId, e);
            return false;
        } finally {
            lock.unLock();
        }
    }

    private void sendEv(Long primaryOrderId) {
        MainOrder mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
        if (!OrderStatusEnum.ORDER_WAIT_DELIVERY.getCode().equals(mainOrder.getPrimaryOrderStatus())) {
            return;
        }
        List<EvoucherInstance> list = new ArrayList<>();
        for (SubOrder subOrder : mainOrder.getSubOrders()) {
            List<EvoucherInstance> orderEv = evoucherCreateService.createEvInstance(subOrder, mainOrder);
            list.addAll(orderEv);
        }
        log.info("send evoucher: " + mainOrder.getPrimaryOrderId());
        evoucherCreateService.saveEv(mainOrder, list);
    }
}
