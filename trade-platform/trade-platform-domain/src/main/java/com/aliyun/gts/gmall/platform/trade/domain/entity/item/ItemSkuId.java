package com.aliyun.gts.gmall.platform.trade.domain.entity.item;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemSkuId {

    @ApiModelProperty(value = "商品ID", required = true)
    private Long itemId;

    @ApiModelProperty(value = "商品SKU-ID", required = true)
    private Long skuId;

    @ApiModelProperty("确认订单时选择的商家")
    private Long sellerId;

    @ApiModelProperty("支付方式 epay loan_期数 installment_期数")
    private String payMode;

    @ApiModelProperty("商家-商品关联ID")
    private Long skuQuoteId;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @ApiModelProperty("物流方式, DeliveryTypeEnum")
    private Integer deliveryType;

    /**
     * 构造方法
     * @param itemId
     * @param skuId
     */
    public ItemSkuId(Long itemId, Long skuId) {
        this.itemId = itemId;
        this.skuId = skuId;
    }

    /**
     * 构造方法
     * @param itemId
     * @param skuId
     * @param sellerId
     */
    public ItemSkuId(Long itemId, Long skuId, Long sellerId) {
        this.itemId = itemId;
        this.skuId = skuId;
        this.sellerId = sellerId;
    }

    /**
     * 构造方法
     * @param itemId
     * @param skuId
     * @param sellerId
     * @param skuQuoteId
     */
    public ItemSkuId(Long itemId, Long skuId, Long sellerId, Long skuQuoteId) {
        this.itemId = itemId;
        this.skuId = skuId;
        this.sellerId = sellerId;
        this.skuQuoteId = skuQuoteId;
    }

    /**
     * 构造方法
     * @param itemId
     * @param skuId
     * @param sellerId
     * @param skuQuoteId
     * @param cityCode
     */
    public ItemSkuId(Long itemId, Long skuId, Long sellerId, Long skuQuoteId, String cityCode) {
        this.itemId = itemId;
        this.skuId = skuId;
        this.sellerId = sellerId;
        this.skuQuoteId = skuQuoteId;
        this.cityCode = cityCode;
    }

    /**
     * 构造方法
     * @param itemId
     * @param skuId
     * @param sellerId
     * @param skuQuoteId
     * @param cityCode
     * @param payMode
     */
    public ItemSkuId(Long itemId, Long skuId, Long sellerId,Long skuQuoteId,String cityCode, String payMode) {
        this.itemId = itemId;
        this.skuId = skuId;
        this.sellerId = sellerId;
        this.skuQuoteId = skuQuoteId;
        this.cityCode = cityCode;
        this.payMode = payMode;
    }

    /**
     * 构造方法
     * @param itemId
     * @param skuId
     * @param sellerId
     * @param skuQuoteId
     * @param cityCode
     * @param deliveryType
     */
    public ItemSkuId(Long itemId, Long skuId, Long sellerId,Long skuQuoteId,String cityCode,Integer deliveryType) {
        this.itemId = itemId;
        this.skuId = skuId;
        this.sellerId = sellerId;
        this.skuQuoteId = skuQuoteId;
        this.cityCode = cityCode;
        this.deliveryType = deliveryType;
    }

    /**
     * 构造方法
     * @param itemId
     * @param skuId
     * @param sellerId
     * @param skuQuoteId
     * @param cityCode
     * @param deliveryType
     * @param payMode
     */
    public ItemSkuId(Long itemId, Long skuId, Long sellerId,Long skuQuoteId,String cityCode,Integer deliveryType, String payMode) {
        this.itemId = itemId;
        this.skuId = skuId;
        this.sellerId = sellerId;
        this.skuQuoteId = skuQuoteId;
        this.cityCode = cityCode;
        this.deliveryType = deliveryType;
        this.payMode = payMode;
    }
}
