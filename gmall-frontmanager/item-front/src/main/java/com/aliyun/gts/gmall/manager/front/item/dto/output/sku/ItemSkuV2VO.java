package com.aliyun.gts.gmall.manager.front.item.dto.output.sku;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemSaleSellerVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemSkuPriceVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * SKU信息
 *
 * @author tiansong
 */
@Data
@ApiModel("SKU信息")
public class ItemSkuV2VO extends AbstractOutputInfo {
    @ApiModelProperty(value = "所有分期")
    private ItemSkuPeriodVO allPeriods;
    @ApiModelProperty(value = "banner图地址")
    private String bannerImageUrl;
    @ApiModelProperty(value = "banner地址")
    private String bannerUrl;
    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "折扣比例")
    private Integer discountPercent;
    @ApiModelProperty(value = "折扣价格")
    private Long discountPrice;
    @ApiModelProperty(value = "是否促销")
    private Boolean hasPromotion;
    @ApiModelProperty(value = "图片地址")
    private List<String> imageUrls;
    @ApiModelProperty(value = "loan分期")
    private Integer loanPeriod;
    @ApiModelProperty(value = "loan价格")
    private Long loanPrice;
    @ApiModelProperty(value = "loan描述")
    private String longDescription;
    @ApiModelProperty(value = "最大折扣比例")
    private Integer maxDiscountPercent;
    @ApiModelProperty(value = "最大分期周期")
    private Integer maxInstallmentPeriod;
    @ApiModelProperty(value = "付款类型")
    private String offerType;
    @ApiModelProperty(value = "付款限制")
    private Integer offersLimit;
    @ApiModelProperty(value = "价格")
    private Long price;
    @ApiModelProperty(value = "商品属性")
    private List<ItemSkuProductOptionVO> productOptions;
    @ApiModelProperty(value = "等级")
    private ItemSkuRatingVO rating;
    @ApiModelProperty(value = "sku编码")
    private String skuCode;
    @ApiModelProperty(value = "sku是否提供")
    private Boolean skuHasOffer;
    @ApiModelProperty(value = "sku主键")
    private Long skuId;
    @ApiModelProperty(value = "sku主键")
    private String skuName;
    @ApiModelProperty(value = "商品价格数据")
    private ItemSkuPriceVO itemSkuPrice;
    @ApiModelProperty(value = "商家信息")
    private List<ItemSaleSellerVO> itemSaleSellerList;
}
