package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;

import com.aliyun.gts.gmall.center.pay.api.enums.PayChannelEnum;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * 订单支付
 *
 * @author tiansong
 */
@ApiModel("订单支付")
@Data
public class OrderPayRestCommand extends LoginRestCommand {
    @ApiModelProperty(value = "主订单号", required = true)
    private Long    primaryOrderId;
    @ApiModelProperty(value = "主订单列表（合并支付）, 透传 OrderCreateResultVO.primaryOrderList")
    private List<String> primaryOrderList;
    @ApiModelProperty(value = "下单渠道")
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
    @ApiModelProperty("阶段序号, 多阶段交易必传")
    private Integer    stepNo;
    @ApiModelProperty(value = "账期支付备注", required = false)
    private String  accountPeriod;

    @ApiModelProperty(value = "卡的主键id", required = false)
    private Long id;

    @ApiModelProperty(value = "epay 账号id", required = false)
    private String  accountId;
    @ApiModelProperty(value = "卡ID")
    private String cardId;
    @ApiModelProperty(value = "持卡人")
    private String cardPersonName;
    @ApiModelProperty(value = "支付方式")
    private String payment;
    private String invoiceId;

    private String scope;

    private String email;
    private String phone;
    @ApiModelProperty("支付描述")
    private String description;


    @ApiModelProperty("其他扩展支付信息, 可传递到支付渠道对接处")
    private Map<String, Object> extraPayInfo;

    @ApiModelProperty("银行卡号")
    private String bankCardNbr;

    @Override
    public void checkInput() {
        super.checkInput();
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

        if (CollectionUtils.isEmpty(this.getPrimaryOrderList())) {
            ParamUtil.nonNull(this.primaryOrderList, I18NMessageUtils.getMessage("main.order.number.required"));  //# "主订单号不能为空"
        }
         // 合并支付check
        //if (CollectionUtils.isNotEmpty(this.getPrimaryOrderList())) {
        //    boolean found = this.getPrimaryOrderList().stream()
        //            .filter(s -> PrimaryOrderVO.isMergeParamMatch(s, primaryOrderId))
        //            .findFirst().isPresent();
        //    ParamUtil.expectTrue(found, I18NMessageUtils.getMessage("merge.payment.main.order.mismatch"));  //# "合并支付主订单号不匹配"
        //}
    }
}
