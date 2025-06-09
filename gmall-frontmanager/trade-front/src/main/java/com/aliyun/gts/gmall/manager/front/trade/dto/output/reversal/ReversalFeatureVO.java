package com.aliyun.gts.gmall.manager.front.trade.dto.output.reversal;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.AddressVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 订单过程字段
 *
 * @author tiansong
 */
@Data
@ApiModel("订单过程字段")
public class ReversalFeatureVO {
    @ApiModelProperty("订单是否收到货物")
    private Boolean      itemReceived;
    @ApiModelProperty("卖家收货地址")
    private AddressVO    receiver;
    @ApiModelProperty("创建售后单时的订单状态, OrderStatusEnum")
    private Integer      orderStatus;
    @ApiModelProperty("取消积分金额")
    private Long         cancelPointAmt;
    @ApiModelProperty("取消积分数量")
    private Long         cancelPointCount;
    @ApiModelProperty("取消现金金额")
    private Long         cancelRealAmt;
    @ApiModelProperty("退货物流单号")
    private List<String> logisticsNos;
    @ApiModelProperty("退运费金额, 主售后单上")
    private Long cancelFreightAmt;
    @ApiModelProperty("买家取消时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date         cancelTime;
    @ApiModelProperty("卖家拒绝时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date         refuseTime;
    @ApiModelProperty("卖家同意时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date         agreeTime;
    @ApiModelProperty("卖家自动同意时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date         autoAgreeTime;
    @ApiModelProperty("买家发货时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date         sendTime;
    @ApiModelProperty("卖家确认收货时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date         confirmReceiveTime;
    @ApiModelProperty("卖家自动确认收货时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date         autoConfirmDeliverTime;
    @ApiModelProperty("买家确认收到退款 ")
    private Boolean buyerConfirmRefund;
    @ApiModelProperty("buyerConfirmRefund 买家确认退款的单号")
    private String bcrNumber;
    @ApiModelProperty("buyerConfirmRefund 买家确认退款的备注")
    private String bcrMemo;

    @ApiModelProperty("扩展字段feature")
    private Map<String, String> feature;
    @ApiModelProperty("扩展字段extra")
    private Map<String, String> extra;
}
