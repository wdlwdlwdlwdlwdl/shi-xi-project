package com.aliyun.gts.gmall.platform.trade.api.dto.input.cart;

import com.aliyun.gts.gmall.framework.api.dto.AbstractInputParam;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel
@NoArgsConstructor
@AllArgsConstructor
public class ItemSkuId extends AbstractInputParam {

    @ApiModelProperty(value = "商品ID", required = true)
    private Long itemId;

    @ApiModelProperty(value = "商品SKU-ID", required = true)
    private Long skuId;

    @ApiModelProperty("加入购物车选择的商家")
    private Long sellerId;

    @ApiModelProperty("支付方式 epay loan_期数 installment_期数")
    private String payMode;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @ApiModelProperty("加入购物车选择的商家")
    private Long skuQuoteId;

    @ApiModelProperty("购物车卡片id")
    private Long cartId;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(itemId, I18NMessageUtils.getMessage("product")+" [ID] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "商品ID不能为空"
        ParamUtil.nonNull(skuId, I18NMessageUtils.getMessage("product")+" [SKU-ID] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "商品SKU-ID不能为空"
        ParamUtil.nonNull(sellerId, I18NMessageUtils.getMessage("product") + " [selected SellerId] " + I18NMessageUtils.getMessage("cannot.be.empty"));  //# "商家Id不能为空"
        ParamUtil.nonNull(payMode, I18NMessageUtils.getMessage("product") + " [payMode]" + I18NMessageUtils.getMessage("cannot.be.empty"));  //# "商品Id不能为空"
    }
}
