package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateItemInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "商品下单结算")
public class CreateCheckOutOrderRestCommand extends LoginRestCommand {

    @ApiModelProperty(value = "订单确认信息", required = true)
    private String  confirmOrderToken;
    @ApiModelProperty("下单渠道，同head中的channel，默认h5")
    private String  orderChannel;
    @ApiModelProperty(value = "支付渠道", required = true)
    private String payChannel;
    @ApiModelProperty("订购商品信息")
    private List<CreateItemInfo> orderItems;
    @ApiModelProperty("是否通过购物车下单")
    private Boolean isFromCart;
    @ApiModelProperty("支付方式 epay loan_期数 installment_期数")
    private String payMode;
    @ApiModelProperty(value = "订单总金额", required = true)
    private Long totalOrderFee;
    @ApiModelProperty(value = "实付金额", required = true)
    private Long realPayFee;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.notEmpty(orderItems, I18NMessageUtils.getMessage("order.product.info.required"));  //# "订购商品信息不能为空"
        ParamUtil.nonNull(payMode, I18NMessageUtils.getMessage("product")+"payMode"+I18NMessageUtils.getMessage("cannot.empty"));  //# "支付方式不能为空"
        orderItems.forEach(orderItem -> orderItem.checkInput());
        ParamUtil.notBlank(this.confirmOrderToken, I18NMessageUtils.getMessage("order.confirmation.info.required"));  //# "订单确认信息不能为空"
        ParamUtil.notBlank(this.payChannel, I18NMessageUtils.getMessage("payment.channel.required"));  //# "支付渠道不能为空"
        this.setOrderChannel(this.getChannel());
    }
}
