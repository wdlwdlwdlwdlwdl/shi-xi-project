package com.aliyun.gts.gmall.platform.trade.core.message.consumer;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.middleware.api.cache.lock.DistributedLock;
import com.aliyun.gts.gmall.middleware.api.mq.anns.MQConsumer;
import com.aliyun.gts.gmall.middleware.api.mq.consumer.ConsumeEventProcessor;
import com.aliyun.gts.gmall.middleware.api.mq.model.StandardEvent;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonConstant;
import com.aliyun.gts.gmall.platform.trade.api.constant.MessageConstant;
import com.aliyun.gts.gmall.platform.trade.api.dto.message.ToRefundMessageDTO;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.PayRefundDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

@Slf4j
@MQConsumer(
    groupId = "${trade.reversal.torefund.groupId}",
    topic = "${trade.reversal.torefund.topic}",
    tag = MessageConstant.TRADE_PAY_REFUND
)
public class ToRefundConsumer implements ConsumeEventProcessor {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private PayRefundDomainService payRefundDomainService;

    @Override
    public boolean process(StandardEvent event) {
        log.info("ToRefundConsumer ToRefund event msg = " + JSONObject.toJSONString(event));
        ToRefundMessageDTO message = (ToRefundMessageDTO) event.getPayload().getData();
        if (message == null) {
            log.error("ToRefundConsumer@process return ToRefundMessageDTO is null ");
            return false;
        }
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
            payRefundDomainService.toRefundExecute(message);
            return true;
        } catch (Exception e) {
            log.error("ToRefundConsumer.process occurred exceptions! reversalId = {} ", message.getPrimaryReversalId(), e);
            return false;
        }
        finally {
            orderLock.unLock();
        }
    }
}
