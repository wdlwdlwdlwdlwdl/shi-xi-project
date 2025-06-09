package com.aliyun.gts.gmall.center.trade.domain.dataobject;

import lombok.Data;

@Data
public class OrderCheckErrorParam {

    /**
     * 对账主订单
     */
    private Long primaryOrderId;

    /**
     * 对账售后主单、如无售后、为空
     */
    private Long primaryReversalId;

    /**
     * 对账类型
     */
    private Integer checkType;
}
