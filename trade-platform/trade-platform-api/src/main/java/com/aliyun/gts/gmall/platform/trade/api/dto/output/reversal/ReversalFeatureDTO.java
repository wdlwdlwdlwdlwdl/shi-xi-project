package com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.ReceiverDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class ReversalFeatureDTO extends AbstractOutputInfo {

    @ApiModelProperty("订单是否收到货物")
    private Boolean itemReceived;

    @ApiModelProperty("卖家收货地址")
    private ReceiverDTO receiver;

    @ApiModelProperty("创建售后单时的订单状态, OrderStatusEnum")
    private Integer orderStatus;

    @ApiModelProperty("取消积分金额")
    private Long cancelPointAmt;

    @ApiModelProperty("取消积分数量")
    private Long cancelPointCount;

    @ApiModelProperty("取消现金金额")
    private Long cancelRealAmt;

    @ApiModelProperty("退货物流单号")
    private List<String> logisticsNos;

    @ApiModelProperty("是否需要退货物流")
    private Boolean needLogistics;

    @ApiModelProperty("退运费金额, 主售后单上")
    private Long cancelFreightAmt;

    // ==== 各种时间记录 ====

    @ApiModelProperty("卖家同意时间")
    private Date agreeTime;

    @ApiModelProperty("买家发货时间")
    private Date sendTime;

    @ApiModelProperty("卖家确认收货时间")
    private Date confirmReceiveTime;

    @ApiModelProperty("卖家拒绝时间")
    private Date refuseTime;

    @ApiModelProperty("买家取消时间")
    private Date cancelTime;

    @ApiModelProperty("卖家自动同意时间")
    private Date autoAgreeTime;

    @ApiModelProperty("卖家自动确认收货时间")
    private Date autoConfirmDeliverTime;

    @ApiModelProperty("买家确认收到退款 ")
    private Boolean buyerConfirmRefund;
    @ApiModelProperty("buyerConfirmRefund 买家确认退款的单号")
    private String bcrNumber;
    @ApiModelProperty("buyerConfirmRefund 买家确认退款的备注")
    private String bcrMemo;

    /**
     * 扩展字段
     */
    @ApiModelProperty("扩展字段")
    private Map<String, String> feature;
}
