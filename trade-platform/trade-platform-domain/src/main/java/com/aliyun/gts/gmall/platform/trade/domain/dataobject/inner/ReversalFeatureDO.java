package com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.*;

@Data
public class ReversalFeatureDO extends StepRefundFee {

    @ApiModelProperty("订单是否收到货物")
    private Boolean itemReceived;

    @ApiModelProperty("卖家收货地址")
    private ReceiverInfoDO receiver;

    @ApiModelProperty("创建售后单时的订单状态, OrderStatusEnum")
    private Integer orderStatus;

    @ApiModelProperty("创建售后单时的订单阶段, OrderStageEnum")
    private Integer orderStage;

    @ApiModelProperty("退货物流单号")
    private List<String> logisticsNos;

    @ApiModelProperty("是否需要退货物流")
    private Boolean needLogistics;

    @ApiModelProperty("退运费金额, 主售后单上")
    private StepRefundFee cancelFreightFee = new StepRefundFee();

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


    @ApiModelProperty("退款成功的阶段号, 异步通知的")
    private List<Integer> successSteps;

    public List<Integer> successSteps() {
        if (successSteps == null) {
            successSteps = new ArrayList<>();
        }
        return successSteps;
    }

    /**
     * @param key
     * @param value
     */
    public void addFeature(String key, String value) {
        if (feature == null) {
            feature = new HashMap<>();
        }
        this.feature.put(key, value);
    }

    public String getFeature(String key) {
        if (feature == null) {
            return null;
        }
        return this.feature.get(key);
    }

}
