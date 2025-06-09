package com.aliyun.gts.gmall.platform.trade.common.util;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;

public class OrderIdUtils {
    private static final long CUST_NUM_MASK = 10000;
    private static final long IDX_NUM_MASK = 1000;

    /**
     * 子订单ID 转 主订单ID
     */
    public static Long getPrimaryOrderIdByOrderId(Long orderId) {
        return changeIdxNum(orderId, 0);
    }

    public static Long getFreightOrderId(Long orderId) {
        return changeIdxNum(orderId, 999);
    }

    private static Long changeIdxNum(Long orderId, Integer idx) {
        if (idx >= IDX_NUM_MASK || idx < 0) {
            throw new GmallException(CommonResponseCode.IllegalArgument, I18NMessageUtils.getMessage("order.error"));  //# "订单号错误"
        }
        long custNum = orderId % CUST_NUM_MASK;
        long seq = orderId / CUST_NUM_MASK / IDX_NUM_MASK;
        return (seq * IDX_NUM_MASK + idx) * CUST_NUM_MASK + (custNum % CUST_NUM_MASK);
    }
}
