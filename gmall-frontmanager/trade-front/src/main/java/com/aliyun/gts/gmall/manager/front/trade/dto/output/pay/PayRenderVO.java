package com.aliyun.gts.gmall.manager.front.trade.dto.output.pay;

import java.util.List;

import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 收银台渲染VO
 *
 * @author tiansong
 */
@ApiModel("收银台渲染VO")
@Data
public class PayRenderVO {
    @ApiModelProperty("主订单编号")
    private Long               primaryOrderId;
    @ApiModelProperty("默认支付渠道Code")
    private String             payChannelCode;
    @ApiModelProperty(value = "订单总金额（单位：分）", required = true)
    private Long               totalOrderFee;
    @ApiModelProperty(value = "实付金额（单位：分）", required = true)
    private Long               realPayFee;
    @ApiModelProperty("支付优惠（单位：分）")
    private Long               payDiscount;
    @ApiModelProperty("积分金额")
    private Long               pointAmount;
    @ApiModelProperty("积分数量")
    private Long               pointCount;
    @ApiModelProperty("是否参加在线支付立减")
    private Boolean            isJoinPayOnline;
    @ApiModelProperty("支持的支付方式")
    private List<PayChannelVO> payChannelList;
    @ApiModelProperty("去支付信息")
    private OrderPayVO         orderPayVO;
    @ApiModelProperty(value = "阶段序号")
    private Integer            stepNo;
    @ApiModelProperty(value = "原来选中的支付方式")
    private String payChannelSelected;
    @ApiModelProperty(value = "账期支付备注")
    private String               accountPeriod;

    public String getTotalOrderFeeYuan() {
        return String.valueOf(totalOrderFee);
    }

    public String getRealPayFeeYuan() {
        return String.valueOf(realPayFee);
    }

    public String getPayDiscountYuan() {
        return String.valueOf(payDiscount);
    }
}