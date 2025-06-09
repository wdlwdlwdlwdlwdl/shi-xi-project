package com.aliyun.gts.gmall.platform.trade.common.constants;

/**
 * 城市价格是否售卖
 * @anthor shifeng
 * @version 1.0.1
 */
public enum CityPriceEnum {

    ON_SALES(1),   //# "下单减库存"
    OFF_SALES(2);  //# 付款减库存

    private final Integer code;

    CityPriceEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
