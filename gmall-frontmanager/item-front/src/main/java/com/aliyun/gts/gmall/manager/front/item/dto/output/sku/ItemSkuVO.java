package com.aliyun.gts.gmall.manager.front.item.dto.output.sku;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * SKU信息
 *
 * @author tiansong
 */
@Data
@ApiModel("SKU信息")
public class ItemSkuVO extends AbstractOutputInfo {
    @ApiModelProperty("skuId")
    private Long            id;
    @ApiModelProperty("商品id")
    private Long            itemId;
    @NotNull
    @ApiModelProperty("库存")
    private Long            quantity;
    @NotNull
    @ApiModelProperty("价格,分")
    private Long            price;
    @NotNull
    @ApiModelProperty("重量,kg")
    private Long            weight;
    @ApiModelProperty("sku属性信息")
    private List<SkuPropVO> skuPropList;
    @NotNull
    @ApiModelProperty("sku展示的名称")
    private String          skuName;
    @NotNull
    @ApiModelProperty("优惠展示价格，分")
    private Long            promPrice;

    @ApiModelProperty("定金价格，分")
    private Long            depositPrice;

    @ApiModelProperty("商品图片，优先使用SKU图片")
    private String          picUrl;

    @ApiModelProperty("积分商品，对应的价格")
    private String          pointPrice;

    @ApiModelProperty("积分商品，对应的积分数量")
    private String          pointNum;
    @ApiModelProperty("折扣")
    private Double discount;

    public String getWeightUnit() {
        return ItemUtils.showWeight(this.weight, Boolean.TRUE);
    }
}
