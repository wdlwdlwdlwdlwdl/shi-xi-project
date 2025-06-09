package com.aliyun.gts.gmall.platform.trade.domain.entity.promotion;

import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class OrderPromotion extends AbstractBusinessEntity {

    @ApiModelProperty("优惠选项, 跨店的")
    private List<PromotionOption> options;

    @Deprecated
    private PromotionExtend promotionExtend;

    @ApiModelProperty("营销返回的最终价格")
    private Long promotionPrice;

    @ApiModelProperty("店铺明细")
    private List<SellerPromotion> sellers;

    @ApiModelProperty("顾客ID")
    private Long custId;

    @ApiModelProperty("渠道, OrderChannelEnum")
    private String orderChannel;

    @ApiModelProperty("下单的营销参数, 从前端透传到营销")
    private String promotionSource;

    @ApiModelProperty("营销扣除优惠券参数, 交易仅作透传")
    private Object deductUserAssets;

    @ApiModelProperty("原支付方式")
    private String originPayMode;

    @ApiModelProperty("分期优惠信息")
    private Map<String, Long> installmentTotal;

    @ApiModelProperty("分期优惠信息")
    private Map<String, Long> installmentPromotion;

}
