package com.aliyun.gts.gmall.platform.trade.domain.wrapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayFlowQueryWrapper {

    /**
     * 顾客编号
     */
    private String custId;

    /**
     * 主订单号
     */
    private Long primaryOrderId;

    /**
     * 支付单ID-支付业务主键
     */
    private String payId;

    /**
     * 支付类型
     */
    private Integer payType;

    /**
     * 支付流水ID-支付流水业务组件
     */
    private String paymentFlowId;
}
