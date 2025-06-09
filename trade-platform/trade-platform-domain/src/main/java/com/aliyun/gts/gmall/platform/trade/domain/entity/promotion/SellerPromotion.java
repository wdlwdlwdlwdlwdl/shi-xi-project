package com.aliyun.gts.gmall.platform.trade.domain.entity.promotion;

import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SellerPromotion extends AbstractBusinessEntity {

    @ApiModelProperty("卖家ID")
    private Long sellerId;

    @ApiModelProperty("优惠选项, 店铺的")
    private List<PromotionOption> options;

    @ApiModelProperty("优惠扩展, 店铺的")
    private PromotionExtend promotionExtend;

    @ApiModelProperty("营销返回的最终分摊价格")
    private Long promotionPrice;

    @ApiModelProperty("商品明细")
    private List<ItemPromotion> items;

}
