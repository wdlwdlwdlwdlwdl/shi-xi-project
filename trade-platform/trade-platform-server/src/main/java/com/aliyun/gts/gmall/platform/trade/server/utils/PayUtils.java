package com.aliyun.gts.gmall.platform.trade.server.utils;

import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.PayPrice;

public class PayUtils {

    public static boolean needAsynchronousNotification(boolean isRedirectCallBack, MainOrder mainOrder, boolean isPointFullDeductInvokeGateWay) {

        // 0元支付, 直接成功, 不异步callback
        PayPrice price = mainOrder.getCurrentPayInfo().getPayPrice();
        boolean isZeroPay = price.getTotalAmt().longValue() == 0L;
        if (isZeroPay) {
            return false;
        }

        //关闭全额抵扣调用网关开关、是全额抵扣、不需要异步通知
        if (!isPointFullDeductInvokeGateWay && isFullPointDeduct(mainOrder)) {
            return false;
        }
        //直接回调、不需要异步通知
        return !isRedirectCallBack;
    }

    private static boolean isFullPointDeduct(MainOrder mainOrder) {
        PayPrice price = mainOrder.getCurrentPayInfo().getPayPrice();
        return price.getPointAmt() > 0
                && price.getOrderRealAmt().longValue() == 0L;
    }

}
