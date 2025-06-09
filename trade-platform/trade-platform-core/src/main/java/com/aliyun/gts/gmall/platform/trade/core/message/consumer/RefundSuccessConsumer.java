package com.aliyun.gts.gmall.platform.trade.core.message.consumer;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.middleware.api.cache.lock.DistributedLock;
import com.aliyun.gts.gmall.middleware.api.mq.anns.MQConsumer;
import com.aliyun.gts.gmall.middleware.api.mq.consumer.ConsumeEventProcessor;
import com.aliyun.gts.gmall.middleware.api.mq.model.StandardEvent;
import com.aliyun.gts.gmall.platform.pay.api.dto.message.RefundSuccessMessage;
import com.aliyun.gts.gmall.platform.pay.common.constants.SysRefund;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonConstant;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.PayRefundDomainService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

@Slf4j
@MQConsumer(
    groupId = "${trade.pay.refund.groupId}",
    topic = "${trade.pay.refund.topic}",
    tag = "${trade.pay.refund.tag}"
)
public class RefundSuccessConsumer implements ConsumeEventProcessor {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private PayRefundDomainService payRefundDomainService;

    @Override
    public boolean process(StandardEvent event) {
        log.info("PayRefund event msg = " + JSONObject.toJSONString(event));
        RefundSuccessMessage message = (RefundSuccessMessage) event.getPayload().getData();
        if (message == null) {
            log.error("PayRefundConsumer@process return payRefundMessage is null ");
            return false;
        }
        if (StringUtils.isBlank(message.getRefundId())) {
            log.error("PayRefundConsumer: refundId is null ignored");
            return true;
        }
        if (message.getPrimaryReversalId().startsWith(SysRefund.VIRTUAL_REVERSAL_ID_PREFIX)) {
            log.error("PayRefundConsumer: sys refund ignored");
            return true;
        }
        // 加锁
        // 加订单状态锁， 同一时刻 只能一个 处理一个场景
        DistributedLock orderLock = cacheManager.getLock(
            String.format(CommonConstant.REFUND_CONSUMER_LOCK_KEY, event.getId())
        );
        boolean lock = Boolean.FALSE;
        try {
            lock = orderLock.tryLock(CommonConstant.ORDER_TIME_OUT, CommonConstant.ORDER_MAX_TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            // 获取锁失败 重新消费
            return false;
        }
        // 获取锁失败 同一时间 只能处理一个状态
        if (Boolean.FALSE.equals(lock)) {
            return false;
        }
        try {
            return payRefundDomainService.payRefundExecute(message);
        } catch (Exception e) {
            log.error("PayRefundConsumer.process occurred exceptions! reversalId = {} ",
                    message.getPrimaryReversalId(), e);
            return false;
        }
    }

}
