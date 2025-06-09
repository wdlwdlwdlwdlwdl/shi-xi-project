package com.aliyun.gts.gmall.platform.trade.api.dto.input.cart;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.TradeCommandRpcRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "修改购物车入参")
public class ModifyCartRpcReq extends TradeCommandRpcRequest {

    @ApiModelProperty(value = "登录会员ID", required = true)
    private Long custId;

    @ApiModelProperty(value = "商品ID", required = true)
    private Long itemId;

    @ApiModelProperty(value = "商品SKU-ID", required = true)
    private Long skuId;

    @ApiModelProperty(value = "购物车业务类型")
    private Integer cartType;

    @ApiModelProperty(value = "修改后的总数")
    private Integer newItemQty;

    @ApiModelProperty(value = "修改后的skuId")
    private Long newSkuId;

    @ApiModelProperty("支付方式 epay loan_期数 installment_期数")
    private String payMode;

    @ApiModelProperty("加入购物车选择的商家")
    private Long sellerId;

    @ApiModelProperty("渠道, OrderChannelEnum")
    private String channel;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @ApiModelProperty("商家-商品关联ID")
    private Long skuQuoteId;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(custId, I18NMessageUtils.getMessage("login.member")+" [ID] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "登录会员ID不能为空"
        ParamUtil.nonNull(itemId, I18NMessageUtils.getMessage("product")+" [ID] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "商品ID不能为空"
        ParamUtil.nonNull(skuId, I18NMessageUtils.getMessage("product")+" [SKU-ID] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "商品SKU-ID不能为空"
        ParamUtil.nonNull(payMode, I18NMessageUtils.getMessage("product")+" [payMode] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "支付方式不能为空"
        ParamUtil.nonNull(sellerId, I18NMessageUtils.getMessage("product")+" [selectedSellerId] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "商家Id不能为空"
    }
}
