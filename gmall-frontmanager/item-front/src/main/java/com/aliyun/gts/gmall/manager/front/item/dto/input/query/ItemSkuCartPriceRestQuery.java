package com.aliyun.gts.gmall.manager.front.item.dto.input.query;

import com.aliyun.gts.gmall.framework.api.rest.dto.AbstractQueryRestRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ItemSkuCartPriceRestQuery extends AbstractQueryRestRequest {

    @ApiModelProperty(value = "商品ID", required = true)
    private Long itemId;
    @ApiModelProperty(value = "skuId", required = true)
    private Long skuId;
    @ApiModelProperty(value = "quoteId", required = true)
    private Long quoteId;
    @ApiModelProperty(value = "城市code", required = true)
    private String cityCode;
    @ApiModelProperty(value = "支付方式 epay loan_期数 installment_期数", required = true)
    private String payMode;
    @ApiModelProperty(value = "分期数", required = true)
    private Integer period;
    @ApiModelProperty(value = "加入购物车选择的商家", required = true)
    private Long sellerId;
    @ApiModelProperty(value = "数量", required = true)
    private Integer itemQty = 1;
    @ApiModelProperty(value ="单品折扣价", required = true)
    private Long itemPrice;
    @ApiModelProperty(value ="用户ID")
    private Long custId;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(cityCode, I18NMessageUtils.getMessage("sku") + " [ID] " + I18NMessageUtils.getMessage("cannot.be.empty"));  //# "城市code不能为空"
        ParamUtil.nonNull(period, I18NMessageUtils.getMessage("product") + " [payMode] " + I18NMessageUtils.getMessage("cannot.empty"));  //# "支付方式不能为空"
        ParamUtil.nonNull(payMode, I18NMessageUtils.getMessage("product") + " [payMode] " + I18NMessageUtils.getMessage("cannot.empty"));  //# "支付方式不能为空"
        // product
        ParamUtil.nonNull(itemId, I18NMessageUtils.getMessage("product") + " [ID] " + I18NMessageUtils.getMessage("cannot.be.empty"));  //# "SkuID不能为空"
        ParamUtil.expectTrue(itemId > 0L, I18NMessageUtils.getMessage("product") + " [ID] " + I18NMessageUtils.getMessage("not.correct"));  //# "商品ID不正确
        // SKU
        ParamUtil.nonNull(skuId, I18NMessageUtils.getMessage("sku") + " [ID] " + I18NMessageUtils.getMessage("cannot.be.empty"));  //# "SkuID不能为空"
        ParamUtil.expectTrue(skuId > 0L, I18NMessageUtils.getMessage("sku") + " [ID] " + I18NMessageUtils.getMessage("not.correct"));  //# "商品ID不正确
        ParamUtil.nonNull(quoteId, I18NMessageUtils.getMessage("sku") + " [ID] " + I18NMessageUtils.getMessage("cannot.be.empty"));  //# "SkuID不能为空"
        ParamUtil.expectTrue(quoteId > 0L, I18NMessageUtils.getMessage("sku")+" [ID] " + I18NMessageUtils.getMessage("not.correct"));  //# "商品ID不正确
        // 卖家
        ParamUtil.nonNull(sellerId, I18NMessageUtils.getMessage("seller") + " [ID] " + I18NMessageUtils.getMessage("cannot.be.empty"));  //# "SkuID不能为空"
        ParamUtil.expectTrue(sellerId > 0L, I18NMessageUtils.getMessage("seller") + " [ID] " + I18NMessageUtils.getMessage("not.correct"));  //# "商品ID不正确
        // 价格
        ParamUtil.nonNull(itemPrice, I18NMessageUtils.getMessage("price.required"));  //# "SkuID不能为空"
    }
}
