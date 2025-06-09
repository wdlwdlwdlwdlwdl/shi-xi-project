package com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.calc;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.promotion.PromotionExtendDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.promotion.PromotionOptionDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class CartPriceDTO extends AbstractOutputInfo {

    @ApiModelProperty("优惠选项")
    private List<PromotionOptionDTO> options;

    @ApiModelProperty("优惠扩展信息")
    private PromotionExtendDTO promotionExtend;

    @ApiModelProperty("营销返回的最终价格")
    private Long promotionPrice;

    @ApiModelProperty("店铺明细")
    private List<SellerPriceDTO> sellers;

    @ApiModelProperty("是否超过支付上限")
    private Boolean overPayLimit;

    @ApiModelProperty("支付上限多少钱")
    private Long payLimit;
}
