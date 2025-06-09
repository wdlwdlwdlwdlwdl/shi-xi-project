package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcLogisticsDO;

/**
 * 获取外部物流状态
 */
public interface LogisticsOuterStatusService {

    /**
     * 订单的签收状态
     * 已签收: 订单全部包裹已签收
     * 未签收: 订单有任一包裹未签收
     * 未知: 1、没有未签收包裹 且有未知的包裹  2、订单无包裹
     */
    ReceiveStatus getOrderReceiveStatus(Long primaryOrderId);

    /**
     * 单个包裹的签收状态
     */
    ReceiveStatus getLogistReceiveStatus(TcLogisticsDO logistics);

    enum ReceiveStatus {
        RECEIVED,       // 已签收
        NOT_RECEIVED,   // 未签收
        UNKNOW,         // 未知
    }
}
