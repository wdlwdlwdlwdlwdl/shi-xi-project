package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.center.pay.api.enums.PayChannelEnum;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 订单支付
 *
 * @author tiansong
 */
@ApiModel("订单支付")
@Data
public class OrderMergePayRestCommand extends LoginRestCommand {
    @ApiModelProperty(value = "主订单号", required = true)
    private List<Long> primaryOrderIds;
    @ApiModelProperty(value = "下单渠道", required = true)
    private String     orderChannel;
    @ApiModelProperty(value = "支付渠道", required = true)
    private String     payChannel;
    @ApiModelProperty(value = "订单总金额", required = true)
    private Long       totalOrderFee;
    @ApiModelProperty(value = "实付金额", required = true)
    private Long       realPayFee;
    @ApiModelProperty("积分金额")
    private Long       pointAmount = 0L;
    @ApiModelProperty("积分数量")
    private Long       pointCount = 0L;
    @ApiModelProperty(value = "appId", notes = "微信支付时必传")
    private String     appId;
    @ApiModelProperty(value = "openId", notes = "微信支付时必传")
    private String     openId;
    @ApiModelProperty(value = "payType", notes = "支付类型(仅为方便convert使用，无需传递)")
    private Integer    payType;

    @ApiModelProperty("其他扩展支付信息, 可传递到支付渠道对接处")
    private Map<String, Object> extraPayInfo;

    @ApiModelProperty("银行卡号")
    private String bankCardNbr;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.notEmpty(this.primaryOrderIds, I18NMessageUtils.getMessage("main.order.number.required"));  //# "主订单号不能为空"
        ParamUtil.nonNull(totalOrderFee, I18NMessageUtils.getMessage("order.total.amount.required"));  //# "订单总金额不能为空"
        ParamUtil.nonNull(realPayFee, I18NMessageUtils.getMessage("actual.payment.required"));  //# "实付金额不能为空"
        ParamUtil.nonNull(pointAmount, I18NMessageUtils.getMessage("points.deduction.amount.required"));  //# "积分抵扣金额不能为空"
        ParamUtil.nonNull(pointCount, I18NMessageUtils.getMessage("points.deduction.qty.required"));  //# "积分抵扣数量不能为空"
        ParamUtil.expectTrue(totalOrderFee >= 0L, I18NMessageUtils.getMessage("order.total.amount.pos.int"));  //# "订单总金额必须为正整数"
        ParamUtil.expectTrue(realPayFee >= 0L, I18NMessageUtils.getMessage("actual.payment.pos.int"));  //# "实付金额必须为正整数"
        ParamUtil.expectTrue(pointAmount >= 0L, I18NMessageUtils.getMessage("points.deduction.amount.pos.int"));  //# "积分抵扣金额必须为正整数"
        ParamUtil.expectTrue(pointCount >= 0L, I18NMessageUtils.getMessage("points.deduction.qty.pos.int"));  //# "积分抵扣数量必须为正整数"
        ParamUtil.expectTrue(totalOrderFee == (realPayFee + pointAmount), I18NMessageUtils.getMessage("payment.amount.exception"));  //# "支付金额异常"
        ParamUtil.nonNull(PayChannelEnum.codeOf(this.payChannel), I18NMessageUtils.getMessage("payment.channel.required"));  //# "支付渠道不能为空"
        // reset
        this.setOrderChannel(this.getChannel());
        if (realPayFee.longValue() == 0L) {
            if (pointCount.longValue() == 0L) {
                // 0元支付
                this.setPayChannel(PayChannelEnum.ZERO.getCode());
            } else {
                // 纯积分
                this.setPayChannel(PayChannelEnum.POINT_ASSERTS.getCode());
            }
        }
        this.setPayType(PayChannelEnum.codeOf(payChannel).getPayType());
    }
}
