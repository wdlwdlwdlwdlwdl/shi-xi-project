package com.aliyun.gts.gmall.center.trade.core.message.consumer;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.center.trade.core.domainservice.B2bSourcingDomainService;
import com.aliyun.gts.gmall.center.trade.core.domainservice.PointGrantService;
import com.aliyun.gts.gmall.center.trade.core.domainservice.TradeNoticeService;
import com.aliyun.gts.gmall.middleware.api.mq.anns.MQConsumer;
import com.aliyun.gts.gmall.middleware.api.mq.consumer.ConsumeEventProcessor;
import com.aliyun.gts.gmall.middleware.api.mq.model.StandardEvent;
import com.aliyun.gts.gmall.platform.trade.api.dto.message.OrderMessageDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.PrimaryOrderFlagEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@MQConsumer(
    groupId = "${trade.order.statuschange.groupId}",
    topic = "${trade.order.statuschange.topic}",
    tag = OrderCreateConsumer.STATUS
)
public class OrderCreateConsumer implements ConsumeEventProcessor {

    static final String STATUS = "60";

    @Autowired
    private PointGrantService pointGrantService;

    @Autowired
    private TradeNoticeService tradeNoticeService;

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private B2bSourcingDomainService b2bSourcingDomainService;

    @Override
    public boolean process(StandardEvent event) {
        log.info("receive event msg = " + JSONObject.toJSONString(event));
        OrderMessageDTO message = (OrderMessageDTO) event.getPayload().getData();
        PrimaryOrderFlagEnum primary = PrimaryOrderFlagEnum.codeOf(message.getPrimaryOrderFlag());
        if (primary != PrimaryOrderFlagEnum.PRIMARY_ORDER) {
            // 忽略子订单消息
            return true;
        }
        MainOrder mainOrder = orderQueryAbility.getMainOrder(message.getPrimaryOrderId());
        if (mainOrder == null) {
            return true;
        }
        // 采购寻源单处理
        //if (b2bSourcingDomainService.isSourcing(mainOrder)) {
        //    b2bSourcingDomainService.consumeOrderCreated(mainOrder);
        //}

        // 赠送积分 记录赠送多少值，多少天赠送
        pointGrantService.grantOnOrderCreate(mainOrder);

        // 提醒消息
        //tradeNoticeService.onOrderCreate(mainOrder);
        return true;
    }
}
