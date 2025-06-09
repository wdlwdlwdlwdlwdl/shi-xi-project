package com.aliyun.gts.gmall.platform.trade.core.message.consumer;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.middleware.api.mq.anns.MQConsumer;
import com.aliyun.gts.gmall.middleware.api.mq.consumer.ConsumeEventProcessor;
import com.aliyun.gts.gmall.middleware.api.mq.model.StandardEvent;
import com.aliyun.gts.gmall.platform.pay.api.dto.message.PaySuccessMessage;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.PaySuccessDomainService;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 订阅paycenter 支付成功消息
 */
@Slf4j
@MQConsumer(
    groupId = "${trade.pay.success.groupId}",
    topic = "${trade.pay.success.topic}",
    tag = "${trade.pay.success.tag}"
)
public class PaySuccessConsumer implements ConsumeEventProcessor {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private PaySuccessDomainService paySuccessExecDomainService;

    @Override
    public boolean process(StandardEvent event) {
        PaySuccessMessage message = (PaySuccessMessage) event.getPayload().getData();
        //如果转换消息出错或信息返回字段不全、则记录日志并直接return true；防止错误消息不断重试
        if (!checkMsg(message)) {
            return true;
        }
        try {
            TradeBizResult tradeBizResult = paySuccessExecDomainService.paySuccessExecute(message);
            if (!tradeBizResult.isSuccess()) {
                log.error("PaySuccessExecutionConsumer@paySuccessMessage consume occurred exception! result {} ",JSONObject.toJSONString(tradeBizResult));
                return false;
            }
        } catch (Exception e) {
            log.error("PaySuccessExecutionConsumer@paySuccessMessage consume occurred exception!", e);
            return false;
        }
        return true;
    }


    private static boolean checkMsg(PaySuccessMessage paySuccessMessage) {
        if (paySuccessMessage == null) {
            log.error("PaySuccessExecutionConsumer@paySuccessMessage return null");
            return false;
        }
        if (paySuccessMessage.getPrimaryOrderId() == null ||
                StringUtils.isEmpty(paySuccessMessage.getCustId())) {
            log.error("PaySuccessExecutionConsumer@paySuccessMessage illagel custId or primaryOrderId is empty");
            return false;
        }
        return true;
    }
}