package com.aliyun.gts.gmall.center.trade.core.message.consumer;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.center.trade.core.domainservice.ManzengGrantService;
import com.aliyun.gts.gmall.center.trade.core.domainservice.PaySplitService;
import com.aliyun.gts.gmall.center.trade.core.domainservice.PointGrantService;
import com.aliyun.gts.gmall.center.trade.core.domainservice.SellerSkuService;
import com.aliyun.gts.gmall.middleware.api.mq.anns.MQConsumer;
import com.aliyun.gts.gmall.middleware.api.mq.consumer.ConsumeEventProcessor;
import com.aliyun.gts.gmall.middleware.api.mq.model.StandardEvent;
import com.aliyun.gts.gmall.platform.trade.api.dto.message.OrderMessageDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.PrimaryOrderFlagEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;


@Slf4j
@MQConsumer(
    groupId = "${trade.order.ordersuccess.groupId}",
    topic = "${trade.order.ordersuccess.topic}",
    tag = "SUCCESS"
)
public class OrderSuccessConsumer implements ConsumeEventProcessor {

    @Autowired
    private PointGrantService pointGrantService;

    @Autowired
    private PaySplitService paySplitService;

    @Autowired
    private ManzengGrantService manzengGrantService;


    @Autowired
    private SellerSkuService sellerSkuService;

    @Override
    public boolean process(StandardEvent event) {
        log.info("orderSuccessConsumer receive event msg = " + JSONObject.toJSONString(event));
        OrderMessageDTO message = (OrderMessageDTO) event.getPayload().getData();
        PrimaryOrderFlagEnum primary = PrimaryOrderFlagEnum.codeOf(message.getPrimaryOrderFlag());
        if (primary != PrimaryOrderFlagEnum.PRIMARY_ORDER) {
            // 忽略子订单消息
            return true;
        }
        Long primaryOrderId = message.getPrimaryOrderId();
        //需做到接口幂等
        pointGrantService.grantOnOrderSuccess(primaryOrderId);
        // 发满赠
        manzengGrantService.onManzengSend(message);
//        try {
//            // 交易成功处理分账逻辑
//            TradeBizResult tradeBizResult = paySplitService.paySplitAfterTradeSuccess(message.getPrimaryOrderId());
//            if (tradeBizResult.isFailed()) {
//                log.error("paySplitService.paySplitAfterTradeSuccess not success return msg = " + JSONObject.toJSONString(tradeBizResult));
//                return false;
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            return false;
//        }
        try {
            sellerSkuService.syncUpdateSkuSales(primaryOrderId);
        } catch (Exception e) {
            log.error("sellerSkuService.syncUpdateSkuSales fail " + e.getMessage(), e);
        }
        return true;
    }

}
