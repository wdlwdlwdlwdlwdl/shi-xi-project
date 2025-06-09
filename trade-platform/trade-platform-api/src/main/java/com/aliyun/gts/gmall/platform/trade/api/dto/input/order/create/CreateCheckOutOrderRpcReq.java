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
public class CreateCheckOutOrderRpcReq extends TradeCommandRpcRequest {

    @ApiModelProperty(value = "登录会员ID", required = true)
    private Long custId;
    @ApiModelProperty(value = "支付渠道", required = true)
    private String payChannel;
    @ApiModelProperty(value = "订单确认信息", required = true)
    private String confirmOrderToken;
    @ApiModelProperty(value = "订单总金额", required = true)
    private Long totalOrderFee;
    @ApiModelProperty(value = "实付金额", required = true)
    private Long realPayFee;
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
    @ApiModelProperty("确认订单扩展参数")
    private Map<String, Object> params;

    @ApiModelProperty("贷款周期")
    private Integer loanCycle;
    
    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(custId, I18NMessageUtils.getMessage("member")+" [ID] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "会员ID不能为空"
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

