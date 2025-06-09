package com.aliyun.gts.gmall.platform.trade.domain.entity.price;

import lombok.Data;

@Data
public class AdjustPrice {

    private Long adjustPromotionAmt;        // 调整 商品优惠后金额, 正调大、负调小
    private Long adjustFreightAmt;          // 调整 运费金额, 正调大、负调小
    private Long adjustRealAmt;             // 调整 现金付款金额, 正调大、负调小
    private Long adjustPointAmt;            // 调整 积分付款金额, 正调大、负调小

    private Integer stepNo;                 // 多阶段必传, 调整哪个阶段的金额
}
