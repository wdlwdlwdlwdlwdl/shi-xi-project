package com.aliyun.gts.gmall.platform.trade.api.dto.output.pay;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author xinchen
 */
@ApiModel("收银台展示出参")
@Data
public class PayRenderRpcResp extends AbstractOutputInfo {

    @ApiModelProperty(value = "买家ID")
    private Long custId;

    @ApiModelProperty(value = "默认支付渠道")
    private PayChannelInfo defaultPayChannel;

    @ApiModelProperty(value="主订单编号")
    private Long primaryOrderId;

    @ApiModelProperty(value = "超时时长（分钟）")
    private Integer payTimeout;

    @ApiModelProperty(value = "订单总金额（单位：分）", required = true)
    private long totalOrderFee;

    @ApiModelProperty(value = "实付金额（单位：分）", required = true)
    private long realPayFee;

    @ApiModelProperty(value = "当前支付主订单已使用的积分金额", required = true)
    private long orderUsedPointAmount;

    @ApiModelProperty(value = "当前支付主订单已使用的积分数量", required = true)
    private long orderUsedPointCount;

    @ApiModelProperty(value = "支付优惠（单位：分）")
    private Long payDiscount = 0L;

    @ApiModelProperty(value = "是否参加在线支付立减", example = "false")
    private boolean isJoinPayOnlineDiscnt;

    @ApiModelProperty(value = "支持的支付方式")
    private List<PayChannelInfo> supportPayChannel;

    @ApiModelProperty(value = "订单列表")
    private List<PayRenderOrderInfo> payRenderOrderInfos;

    @ApiModelProperty(value = "阶段序号")
    private Integer stepNo;
}
