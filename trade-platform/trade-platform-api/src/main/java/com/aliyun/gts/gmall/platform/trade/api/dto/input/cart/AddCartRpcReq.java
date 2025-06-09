package com.aliyun.gts.gmall.platform.trade.api.dto.input.cart;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.TradeCommandRpcRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "添加购物车入参")
public class AddCartRpcReq extends TradeCommandRpcRequest {


    @ApiModelProperty(value = "登录会员ID", required = true)
    private Long custId;

    @ApiModelProperty(value = "商品ID", required = true)
    private Long itemId;

    @ApiModelProperty(value = "商品SKU-ID，对应ic_item_sku表中的id字段", required = true)
    private Long skuId;

    @ApiModelProperty(value = "本次加购的商品数量", required = true)
    private Integer itemQty;

    @ApiModelProperty(value = "购物车业务类型")
    private Integer cartType;

    @ApiModelProperty("支付方式 epay loan_期数 installment_期数")
    private String payMode;

    @ApiModelProperty("加入购物车选择的商家")
    private Long sellerId;

    @ApiModelProperty("渠道, OrderChannelEnum")
    private String channel;

    @ApiModelProperty("商家-商品关联ID")
    private Long skuQuoteId;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(custId, I18NMessageUtils.getMessage("login.member") + " [ID] " + I18NMessageUtils.getMessage("cannot.empty"));  //# "登录会员ID不能为空"
        ParamUtil.nonNull(itemId, I18NMessageUtils.getMessage("product") + " [Id] " + I18NMessageUtils.getMessage("cannot.empty"));  //# "商品Id不能为空"
        ParamUtil.nonNull(skuId, I18NMessageUtils.getMessage("product") + " [SKU-ID] " + I18NMessageUtils.getMessage("cannot.empty"));  //# "商品SKU-ID不能为空"
        ParamUtil.nonNull(itemQty, I18NMessageUtils.getMessage("add.cart.empty"));  //# "本次加购的商品数量不能为空"
        ParamUtil.nonNull(this.payMode, I18NMessageUtils.getMessage("product") + " [payMode] " + I18NMessageUtils.getMessage("cannot.be.empty"));  //# "商品Id不能为空"
        ParamUtil.nonNull(this.sellerId, I18NMessageUtils.getMessage("product") + " [selectedSellerId] " + I18NMessageUtils.getMessage("cannot.be.empty"));  //# "商品Id不能为空"
    }
}
