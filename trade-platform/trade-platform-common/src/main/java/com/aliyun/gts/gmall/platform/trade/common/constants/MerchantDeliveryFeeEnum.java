package com.aliyun.gts.gmall.platform.trade.common.constants;

public enum MerchantDeliveryFeeEnum {

    ALL("1"),
    SINGLE("0");

    private final String code;

    MerchantDeliveryFeeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
