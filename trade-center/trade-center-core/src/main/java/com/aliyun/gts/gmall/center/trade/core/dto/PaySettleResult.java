package com.aliyun.gts.gmall.center.trade.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaySettleResult {

    /**
     * 网关返回码
     */
    private String code;

    /**
     * 	网关返回码描述
     */
    private String msg;

    /**
     * 业务返回码
     */
    private String subCode;

    /**
     * 业务返回码描述
     */
    private String subMsg;

    /**
     * 支付宝交易号
     */
    private String tradeNo;

    /**
     * 业务类型
     */
    private String bizType;

    /**
     * 分账相关账号数据
     */
    private PaySettleCommissionAccount account;


    public boolean isSuccess() {
        return StringUtils.isEmpty(subCode);
    }
}
