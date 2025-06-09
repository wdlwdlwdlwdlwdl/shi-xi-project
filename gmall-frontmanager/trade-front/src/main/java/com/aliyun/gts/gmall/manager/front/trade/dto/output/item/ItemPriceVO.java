package com.aliyun.gts.gmall.manager.front.trade.dto.output.item;

import javax.validation.constraints.NotNull;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品价格信息
 *
 * @author tiansong
 */
@Data
@ApiModel("商品价格信息")
public class ItemPriceVO extends AbstractOutputInfo {
    @ApiModelProperty("商品ID")
    private Long   itemId;
    @ApiModelProperty("sku ID")
    private Long   skuId;
    @ApiModelProperty("商品数量（购物车中）")
    private Long   skuQty;
    @NotNull
    @ApiModelProperty("单品折扣价格")
    private Long   itemPrice;
    @ApiModelProperty("单品折扣名称")
    private String itemPriceName;
    @ApiModelProperty("营销返回的最终价格")
    private Long   promotionPrice;

    @ApiModelProperty("首笔支付金额 (非单价)")
    private Long firstPayPrice;
}
