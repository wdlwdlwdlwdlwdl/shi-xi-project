package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.trade.api.constant.PayModeCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.ReceiverDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.promotion.PromotionOptionDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.OrderGroupInfo;
import com.aliyun.gts.gmall.platform.trade.common.constants.BizCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Data
@ApiModel(value = "订单确认入参")
public class ConfirmOrderInfoRpcReq extends AbstractQueryRpcRequest {

    @ApiModelProperty(value = "会员ID", required = true)
    private Long custId;

    @ApiModelProperty(value = "下单渠道, OrderChannelEnum", required = true)
    private String orderChannel;

    @ApiModelProperty(value = "订购商品信息", required = true)
    private List<ConfirmItemInfo> orderItems;

    @ApiModelProperty(value = "业务身份")
    private List<String> bizCode;

    @ApiModelProperty(value = "收件人信息")
    private ReceiverDTO receiver;

    @ApiModelProperty(value = "支付方式")
    private String payType;

    @ApiModelProperty(value = "是否通过购物车下单")
    private Boolean isFromCart;

    @ApiModelProperty(value = "购物车业务类型")
    private Integer cartType;

    @ApiModelProperty(value = "优惠选择")
    private List<PromotionOptionDTO> promotionSelection;

    @ApiModelProperty(value = "使用积分抵扣的数量(原子积分), 0不抵扣, 大于最大可用积分数的取最大可用积分数")
    private Long usePointCount;

    @ApiModelProperty(value = "多阶段信息, 正常不用传, 由center自己计算")
    private ConfirmStepOrderInfo confirmStepOrderInfo;

    @ApiModelProperty("下单的营销参数, 从前端透传到营销")
    private String promotionSource;

    @ApiModelProperty("确认订单扩展参数")
    private Map<String, Object> params;

    @ApiModelProperty("支付方式 epay loan_期数 installment_期数")
    private String payMode;

    @ApiModelProperty(value = "支付渠道", required = true)
    private String payChannel;

    @ApiModelProperty(value = "分组信息")
    private List<OrderGroupInfo> orderInfos;

    @ApiModelProperty("购物车ID")
    private Long cartId;

    @ApiModelProperty("贷款周期")
    private Integer loanCycle;

//    @ApiModelProperty("物流方式, LogisticsTypeEnum")
//    private Integer logisticsType;

    @ApiModelProperty("订单确认token")
    private String confirmOrderToken;

    @ApiModelProperty("默认收货地地址 支持送货上门且没有指定物流方式的时候 默认设置")
    private Long receiverId;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(custId, I18NMessageUtils.getMessage("member")+" [ID] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "会员ID不能为空"
        ParamUtil.notBlank(orderChannel, I18NMessageUtils.getMessage("order.channel.empty"));  //# "订购渠道不能为空"
        ParamUtil.notEmpty(orderItems, I18NMessageUtils.getMessage("product.list.empty"));  //# "商品列表不能为空"
        for (ConfirmItemInfo item : orderItems) {
            item.checkInput();
        }
        if (bizCode == null || bizCode.isEmpty()) {
            bizCode = new ArrayList<>();
            bizCode.add(BizCodeEnum.NORMAL_TRADE.getCode());
        }
        if(params != null && params.size() > 0) {
            for(Map.Entry<String, Object> entry: params.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                ParamUtil.nonNull(value, String.format("扩展参数key=[%s]的值不能为空", key));
            }
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
