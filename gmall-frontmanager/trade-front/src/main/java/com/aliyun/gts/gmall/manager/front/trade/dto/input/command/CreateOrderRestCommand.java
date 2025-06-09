package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.center.pay.api.enums.PayChannelEnum;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.param.OrderInvoice;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.PromotionOptionVO;
import com.aliyun.gts.gmall.platform.pay.api.enums.PayTypeEnum;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateItemInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.OrderGroupInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品添加购物车
 *
 * @author tiansong
 */
@ApiModel(description = "商品添加购物车")
@Data
public class CreateOrderRestCommand extends LoginRestCommand {
    @ApiModelProperty(value = "订单确认信息", required = true)
    private String  confirmOrderToken;
    @ApiModelProperty("下单渠道，同head中的channel，默认h5")
    private String  orderChannel;
    @ApiModelProperty(value = "支付渠道", required = true)
    private String payChannel;
    @ApiModelProperty(value = "账期支付备注", required = false)
    private String accountPeriod;
    @ApiModelProperty("分组信息")
    private List<OrderGroupInfo> orderGroupInfoList;
    @ApiModelProperty(value = "订单总金额", required = true)
    private Long totalOrderFee;
    @ApiModelProperty(value = "实付金额", required = true)
    private Long realPayFee;
    @ApiModelProperty(value = "优惠总金额")
    private Long promotionFee;
    @ApiModelProperty(value = "运费总金额")
    private Long freightFee;
    @ApiModelProperty("使用的积分金额")
    private Long  pointAmount = 0L;
    @ApiModelProperty("使用的积分数量")
    private Long pointCount = 0L;
    @ApiModelProperty(value = "appId", notes = "微信支付时必传")
    private String appId;
    @ApiModelProperty(value = "openId", notes = "微信支付时必传")
    private String openId;
    @ApiModelProperty("发票信息")
    private OrderInvoice  orderInvoice;
    @ApiModelProperty("阶段序号, 多阶段交易必传")
    private Integer stepNo;
    @ApiModelProperty("下单的营销参数, 从前端透传到营销")
    private String  promotionSource;
    @ApiModelProperty("订购商品信息")
    private List<CreateItemInfo> orderItems;
    @ApiModelProperty("支付方式")
    private Integer payType;
    @ApiModelProperty("是否通过购物车下单")
    private Boolean isFromCart;
    @ApiModelProperty("优惠选择")
    private List<PromotionOptionVO> promotionSelection;
    @ApiModelProperty("支付方式 epay loan_期数 installment_期数")
    private String payMode;
    @ApiModelProperty("使用第三方积分金额")
    private Integer bonusAmount;
    @ApiModelProperty("使用第三方积分数量")
    private Integer bonusCount;
    @ApiModelProperty("确认订单扩展参数")
    private Map<String, Object> params;
    @ApiModelProperty("银行卡id")
    private Long bankCardId;
    @ApiModelProperty("银行卡号")
    private String bankCardNbr;
    @ApiModelProperty("购物车卡片ID集合")
    private List<Long> deleteCartIds;
    @ApiModelProperty("贷款周期")
    private Integer loanCycle;
    @ApiModelProperty("cardId")
    private String cardId;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.notEmpty(orderItems, I18NMessageUtils.getMessage("order.product.info.required"));  //# "订购商品信息不能为空"
        ParamUtil.nonNull(payMode, I18NMessageUtils.getMessage("product")+" [payMode] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "支付方式不能为空"
        orderItems.forEach(orderItem -> orderItem.checkInput());
        ParamUtil.notBlank(this.confirmOrderToken, I18NMessageUtils.getMessage("order.confirmation.info.required"));  //# "订单确认信息不能为空"
        ParamUtil.notBlank(this.payChannel, I18NMessageUtils.getMessage("payment.channel.required"));  //# "支付渠道不能为空"
        ParamUtil.nonNull(totalOrderFee, I18NMessageUtils.getMessage("order.total.amount.required"));  //# "订单总金额不能为空"
        ParamUtil.nonNull(realPayFee, I18NMessageUtils.getMessage("actual.payment.required"));  //# "实付金额不能为空"
        ParamUtil.nonNull(pointAmount, I18NMessageUtils.getMessage("points.deduction.amount.required"));  //# "积分抵扣金额不能为空"
        ParamUtil.nonNull(pointCount, I18NMessageUtils.getMessage("points.deduction.qty.required"));  //# "积分抵扣数量不能为空"
        ParamUtil.expectTrue(totalOrderFee >= 0L, I18NMessageUtils.getMessage("order.total.amount.pos.int"));  //# "订单总金额必须为正整数"
        ParamUtil.expectTrue(realPayFee >= 0L, I18NMessageUtils.getMessage("actual.payment.pos.int"));  //# "实付金额必须为正整数"
        ParamUtil.expectTrue(pointAmount >= 0L, I18NMessageUtils.getMessage("points.deduction.amount.pos.int"));  //# "积分抵扣金额必须为正整数"
        ParamUtil.expectTrue(pointCount >= 0L, I18NMessageUtils.getMessage("points.deduction.qty.pos.int"));  //# "积分抵扣数量必须为正整数"
        ParamUtil.expectTrue(totalOrderFee == (realPayFee + pointAmount), I18NMessageUtils.getMessage("payment.amount.exception"));  //# "支付金额异常"
        ParamUtil.notEmpty(orderGroupInfoList, I18NMessageUtils.getMessage("seller.info.list.required"));  //# "商家信息列表不能为空"
        orderGroupInfoList.forEach(orderGroupInfo -> ParamUtil.nonNull(orderGroupInfo.getSellerId(), I18NMessageUtils.getMessage("merchants")+"ID"+I18NMessageUtils.getMessage("cannot.be.empty")));  //# "商家ID不能为空"
        if (orderInvoice != null) {
            orderInvoice.checkInput();
        }
//        if (Boolean.TRUE.equals(isFromCart)) {
//            ParamUtil.notEmpty(deleteCartIds, I18NMessageUtils.getMessage("order.product.info.required"));  //# "订购商品信息不能为空"
//        }
        // reset
        this.setOrderChannel(this.getChannel());
        this.setPayType(PayTypeEnum.ONLINE_PAY.getCode());
        // 全部资产抵扣，重设payChannel
        if (realPayFee.longValue() == 0L) {
            if (pointCount.longValue() == 0L) {
                // 0元支付
                this.setPayChannel(PayChannelEnum.ZERO.getCode());
            } else {
                // 纯积分
                this.setPayChannel(PayChannelEnum.POINT_ASSERTS.getCode());
            }
        }
        // 优惠列表只传递选中的数据
        if (CollectionUtils.isNotEmpty(promotionSelection)) {
            promotionSelection = promotionSelection.stream().filter(v -> StringUtils.isNotBlank(v.getOptionId()))
                .collect(Collectors.toList());
            promotionSelection.forEach(v -> v.setSelected(Boolean.TRUE));
        }
        // 目前1:1转换率
        this.setBonusAmount(this.getBonusCount());
    }
}