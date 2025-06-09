package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.trade.api.constant.PayModeCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.promotion.PromotionOptionDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.TradeCommandRpcRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@ApiModel(value = "创建订单入参")
public class CreateOrderRpcReq extends TradeCommandRpcRequest {

    @ApiModelProperty(value = "登录会员ID", required = true)
    private Long custId;

    @ApiModelProperty(value = "分组信息")
    private List<OrderGroupInfo> orderInfos;
    @ApiModelProperty(value = "支付渠道", required = true)
    private String payChannel;

    @ApiModelProperty(value = "订单确认信息", required = true)
    private String confirmOrderToken;

    @ApiModelProperty("下单渠道，同head中的channel，默认h5")
    private String orderChannel;
    @ApiModelProperty(value = "账期支付备注", required = false)
    private String accountPeriod;
    @ApiModelProperty(value = "订单总金额", required = true)
    private Long totalOrderFee;
    @ApiModelProperty(value = "实付金额", required = true)
    private Long realPayFee;
    @ApiModelProperty(value = "优惠总金额")
    private Long promotionFee;
    @ApiModelProperty(value = "运费总金额")
    private Long freightFee;
    @ApiModelProperty("使用的积分金额")
    private Long pointAmount = 0L;
    @ApiModelProperty("使用的积分数量")
    private Long pointCount = 0L;
    @ApiModelProperty(value = "appId", notes = "微信支付时必传")
    private String appId;
    @ApiModelProperty(value = "openId", notes = "微信支付时必传")
    private String openId;
    @ApiModelProperty("阶段序号, 多阶段交易必传")
    private Integer stepNo;

    @ApiModelProperty("下单的营销参数, 从前端透传到营销")
    private String promotionSource;

    @ApiModelProperty("订购商品信息")
    private List<CreateItemInfo> orderItems;
    @ApiModelProperty("支付方式")
    private Integer payType;
    @ApiModelProperty("是否通过购物车下单")
    private Boolean isFromCart;
    @ApiModelProperty("优惠选择")
    private List<PromotionOptionDTO> promotionSelection;
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
    @ApiModelProperty("用户ID")
    private String accountId;
    @ApiModelProperty("cardId")
    private String cardId;
    @ApiModelProperty("贷款周期")
    private Integer loanCycle;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(custId, I18NMessageUtils.getMessage("member")+" [ID] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "会员ID不能为空"
        ParamUtil.nonNull(payChannel, I18NMessageUtils.getMessage("pay.channel.empty"));  //# "支付渠道不能为空"
        ParamUtil.nonNull(confirmOrderToken, I18NMessageUtils.getMessage("order.confirm.empty"));  //# "订单确认信息不能为空"
        ParamUtil.notEmpty(orderItems, I18NMessageUtils.getMessage("product.list.empty"));  //# "商品列表不能为空"
        for (CreateItemInfo item : orderItems) {
            item.checkInput();
        }
        //初始化loanCycle
        PayModeCode payModeCode = PayModeCode.codeOf(payMode);
        if (Objects.isNull(payModeCode) || PayModeCode.isEpay(payModeCode)) {
            loanCycle = null;
        } else {
            loanCycle = payModeCode.getLoanNumber();
        }

    }
}

