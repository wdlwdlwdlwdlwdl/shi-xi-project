package com.aliyun.gts.gmall.platform.trade.core.message.consumer;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.middleware.api.mq.anns.MQConsumer;
import com.aliyun.gts.gmall.middleware.api.mq.consumer.ConsumeEventProcessor;
import com.aliyun.gts.gmall.middleware.api.mq.model.StandardEvent;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.message.OversaleMessageDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderTags;
import com.aliyun.gts.gmall.platform.trade.common.constants.OversellProcessType;
import com.aliyun.gts.gmall.platform.trade.common.constants.SystemReversalReason;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderInventoryAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderConfigService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ReversalCreateService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderQueryOption;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.SystemRefund;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.SellerTradeConfig;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@MQConsumer(
    groupId = "${trade.order.oversale.groupId:GID_GMALL_TRADE_ORDER_OVERSALE}",
    topic = "${trade.order.oversale.topic:GMALL_TRADE_ORDER_OVERSALE}",
    tag = "OVER_SALE"
)
public class OversaleProcessConsumer implements ConsumeEventProcessor {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private TcOrderRepository tcOrderRepository;

    @Autowired
    private OrderConfigService orderConfigService;

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private OrderInventoryAbility orderInventoryAbility;

    @Autowired
    private ReversalCreateService reversalCreateService;


    @Override
    public boolean process(StandardEvent event) {
        log.info("receive Oversale msg = " + JSONObject.toJSONString(event));
        OversaleMessageDTO msg = (OversaleMessageDTO) event.getPayload().getData();
        if (msg == null) {
            return true;
        }
        MainOrder mainOrder = orderQueryAbility.getMainOrder(
            msg.getPrimaryOrderId(),
            OrderQueryOption.builder().build()
        );
        if (mainOrder == null) {
            return true;
        }
        // 待支付, 订单状态还未更新, 暂不处理
        if (mainOrder.getPrimaryOrderStatus().intValue() == OrderStatusEnum.ORDER_WAIT_PAY.getCode()) {
            return false;
        }
        // 扣库存异常, 非超卖errCode, 重试减库存
        if (msg.isInventoryException()) {
            try {
                boolean success = retryInv(mainOrder);
                if (success) {
                    log.info("retryInv success {} ", mainOrder.getPrimaryOrderId());
                    removeOversale(mainOrder);
                    return true;
                } else {
                    log.info("retryInv overSale {} ", mainOrder.getPrimaryOrderId());
                    // 超卖了，往下走
                }
            } catch (Exception e) {
                log.error("retryInv exception {} ", mainOrder.getPrimaryOrderId(), e);
                markOversale(mainOrder, false);
                return false;   // 继续重试
            }
        }

        // 超卖了，但卖家已经处理的，系统不再自动关单
        if (mainOrder.getPrimaryOrderStatus().intValue() != OrderStatusEnum.ORDER_WAIT_DELIVERY.getCode()) {
            return manualWork(mainOrder);
        }

        SellerTradeConfig conf = orderConfigService.getSellerConfig(mainOrder.getSeller().getSellerId());
        OversellProcessType type = OversellProcessType.codeOf(conf.getOversellProcessType());
        if (type == OversellProcessType.AUTO_CLOSE) {
            return autoClose(mainOrder);
        } else if (type == OversellProcessType.MANUAL_WORK) {
            return manualWork(mainOrder);
        } else { // 兜底
            return manualWork(mainOrder);
        }
    }

    private boolean manualWork(MainOrder mainOrder) {
        markOversale(mainOrder, false);
        return true;
    }

    private boolean retryInv(MainOrder mainOrder) {
        List<MainOrder> list = new ArrayList<>(Collections.singletonList(mainOrder));
        boolean success = orderInventoryAbility.lockInventory(list);
        if (success) {
            success = orderInventoryAbility.reduceInventory(list);
        }
        return success;
    }

    private boolean autoClose(MainOrder mainOrder) {
        SystemRefund sys = new SystemRefund();
        sys.setMainOrder(mainOrder);
        sys.setReasonCode(SystemReversalReason.OVER_SALE.getCode());
        sys.setReasonContent(SystemReversalReason.OVER_SALE.getName());
        reversalCreateService.createSystemRefund(sys);

        markOversale(mainOrder, true);    // 需要重新查一遍主单, version 变了
        return true;
    }

    private void removeOversale(MainOrder mainOrder) {
        if (!Boolean.TRUE.equals(mainOrder.orderAttr().getOverSale())) {
            return;
        }

        long primaryOrderId = mainOrder.getPrimaryOrderId();
        TcOrderDO up = new TcOrderDO();
        up.setPrimaryOrderId(primaryOrderId);
        up.setOrderId(primaryOrderId);
        OrderAttrDO orderAttr = mainOrder.orderAttr();
        up.setVersion(mainOrder.getVersion());
        orderAttr.setOverSale(null);
        orderAttr.tags().remove(OrderTags.OVER_SALE);
        up.setOrderAttr(orderAttr);
        boolean success = tcOrderRepository.updateByOrderIdVersion(up);
        if (!success) {
            throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
        }
    }

    // 订单打超卖标
    private void markOversale(MainOrder mainOrder, boolean query) {
        long primaryOrderId = mainOrder.getPrimaryOrderId();
        TcOrderDO up = new TcOrderDO();
        up.setPrimaryOrderId(primaryOrderId);
        up.setOrderId(primaryOrderId);

        OrderAttrDO orderAttr;
        if (query) {
            TcOrderDO order = tcOrderRepository.queryPrimaryByOrderId(primaryOrderId);
            orderAttr = order.getOrderAttr();
            if (orderAttr == null) {
                orderAttr = new OrderAttrDO();
            }
            up.setVersion(order.getVersion());
        } else {
            orderAttr = mainOrder.orderAttr();
            up.setVersion(mainOrder.getVersion());
        }

        orderAttr.setOverSale(true);
        orderAttr.tags().add(OrderTags.OVER_SALE);
        up.setOrderAttr(orderAttr);
        boolean success = tcOrderRepository.updateByOrderIdVersion(up);
        if (!success) {
            throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
        }
    }
}
