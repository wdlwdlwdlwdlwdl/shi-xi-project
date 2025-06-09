package com.aliyun.gts.gmall.platform.trade.domain.entity.promotion;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PayModeItemPrice implements Serializable {

    @Serial
    private static final long serialVersionUID = -7993852107348533649L;
    /**
     * 卖家ID
     */
    private Long sellerId;
    /**
     * 支付方式
     */
    private String payMethod;

    /**
     * 原价；sku_quote 原始价格
     */
    private Long skuOrigPrice;

    /**
     * 支付方式
     */
    private Long promotionPrice;

}
