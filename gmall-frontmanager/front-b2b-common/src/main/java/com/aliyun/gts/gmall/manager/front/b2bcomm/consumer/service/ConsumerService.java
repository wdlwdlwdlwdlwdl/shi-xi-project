package com.aliyun.gts.gmall.manager.front.b2bcomm.consumer.service;

import com.aliyun.gts.gmall.center.misc.api.dto.output.flow.message.WorkflowInvovedMessage;

/**
 * 审核完成确认的回调类
 */
public interface ConsumerService {
    void consume(WorkflowInvovedMessage message) throws Exception;
    String getTag();
}
