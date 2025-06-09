/*
package com.aliyun.gts.gmall.center.trade.core.message.consumer;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.center.trade.core.domainservice.PointGrantService;
import com.aliyun.gts.gmall.middleware.api.mq.anns.MQConsumer;
import com.aliyun.gts.gmall.middleware.api.mq.consumer.ConsumeEventProcessor;
import com.aliyun.gts.gmall.middleware.api.mq.model.StandardEvent;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.PayRefundDomainService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ReversalQueryService;
import com.aliyun.gts.gmall.platform.trade.core.message.consumer.ToRefundConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@MQConsumer(
    groupId = "${trade.reversal.torefund.groupId}",
    topic = "${trade.reversal.torefund.topic}",
    tag = "TRADE_PAY_REFUND",
    priority = 9
)
public class ExtToRefundConsumer implements ConsumeEventProcessor {

    @Autowired
    private ToRefundConsumer toRefundConsumer;

    @Autowired
    private PointGrantService pointGrantService;

    @Autowired
    private PayRefundDomainService payRefundDomainService;

    @Autowired
    private ReversalQueryService reversalQueryService;

    @Override
    public boolean process(StandardEvent event) {
        log.info("ExtToRefundConsumer ToRefund event msg = " + JSONObject.toJSONString(event));
        //ToRefundMessageDTO message = (ToRefundMessageDTO) event.getPayload().getData();
        return toRefundConsumer.process(event);
        // 退款之前处理赠送积分退回, 积分不足的情况下从退款金额中扣
        //boolean refund = pointGrantService.rollbackBeforeRefund(message.getPrimaryReversalId());
//        if (refund) {
//            // 发起退款
//
//        } else {
//            // 直接退款成功
//            return directRefundSuccess(message.getPrimaryReversalId());
//        }
    }

//    private boolean directRefundSuccess(Long primaryReversalId) {
//        MainReversal reversal = reversalQueryService.queryReversal(primaryReversalId,
//                ReversalDetailOption.builder().build());
//
//        // 多阶段
//        if (MapUtils.isNotEmpty(reversal.reversalFeatures().getStepRefundFee())) {
//            for (Integer stepNo : reversal.reversalFeatures().getStepRefundFee().keySet()) {
//                RefundSuccessMessage msg = new RefundSuccessMessage();
//                msg.setPrimaryReversalId(String.valueOf(primaryReversalId));
//                msg.setStepNo(stepNo);
//                boolean succ;
//                try {
//                    succ = payRefundDomainService.payRefundExecute(msg);
//                } catch (Exception e) {
//                    throw new RuntimeException("failed, param:" + JSON.toJSONString(msg), e);
//                }
//                if (!succ) {
//                    return false;
//                }
//            }
//            return true;
//        }
//
//        // 非多阶段
//        RefundSuccessMessage msg = new RefundSuccessMessage();
//        msg.setPrimaryReversalId(String.valueOf(primaryReversalId));
//        try {
//            return payRefundDomainService.payRefundExecute(msg);
//        } catch (Exception e) {
//            throw new RuntimeException("failed, param:" + JSON.toJSONString(msg), e);
//        }
//    }
}
*/
