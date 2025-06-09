package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 商品添加购物车
 *
 * @author tiansong
 */
@ApiModel(description = "商品添加购物车")
@Data
public class AddCartRestCommand extends LoginRestCommand {

    @ApiModelProperty(value = "商品ID", required = true)
    private Long itemId;
    @ApiModelProperty(value = "商品SKU-ID", required = true)
    private Long skuId;
    @ApiModelProperty(value = "商品数量", required = true)
    private Integer itemQty;
    @ApiModelProperty("支付方式 epay loan_期数 installment_期数")
    private String payMode;
    @ApiModelProperty("加入购物车选择的商家")
    private Long sellerId;
    @ApiModelProperty("商家-商品关联ID")
    private Long skuQuoteId;
    @ApiModelProperty("城市编码")
    private String cityCode;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(this.itemId, I18NMessageUtils.getMessage("product")+" [Id] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "商品Id不能为空"
        ParamUtil.nonNull(this.skuId, I18NMessageUtils.getMessage("product")+" [SKU-ID] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "商品SKU-ID不能为空"
        ParamUtil.nonNull(this.itemQty, I18NMessageUtils.getMessage("add.to.cart.qty.required"));  //# "本次加购的商品数量不能为空"
        ParamUtil.nonNull(this.payMode, I18NMessageUtils.getMessage("product")+" [payMode] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "支付方式不能为空"
        ParamUtil.nonNull(this.sellerId, I18NMessageUtils.getMessage("product")+" [selectedSellerId] "+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "商家不能为空"
        ParamUtil.nonNull(this.cityCode, I18NMessageUtils.getMessage("city.code.required"));  //# "城市校验"
    }
}