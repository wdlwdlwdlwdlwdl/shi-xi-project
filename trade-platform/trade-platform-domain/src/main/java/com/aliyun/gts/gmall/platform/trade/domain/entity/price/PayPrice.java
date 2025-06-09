package com.aliyun.gts.gmall.platform.trade.domain.entity.price;

import com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils;

public interface PayPrice {

    Long getOrderRealAmt();

    Long getPointAmt();

    Long getPointCount();

    ConfirmPrice getConfirmPrice();

    default Long getTotalAmt() {
        return NumUtils.getNullZero(getOrderRealAmt()) + NumUtils.getNullZero(getPointAmt());
    }
}
