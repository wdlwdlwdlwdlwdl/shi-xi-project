package com.aliyun.gts.gmall.center.trade.domain.entity.notice;

import lombok.Data;

import java.util.Map;

@Data
public class NoticeMessage {

    /**
     *幂等ID
     */
    private String bizId;

    /**
     * 发给卖家, sellerId/custId 二选一
     */
    private Long sellerId;

    /**
     * 发给买家, sellerId/custId 二选一
     */
    private Long custId;

    /**
     *模板code
     */
    private String templateCode;

    /**
     * 消息模版参数
     */
    private Map<String, Object> templateArgs;
}
