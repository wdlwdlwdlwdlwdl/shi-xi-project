package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 订单的button展示
 *
 * @author tiansong
 */
@Data
@Builder
@ApiModel("订单的button展示")
public class OrderButtonsVO {
    @ApiModelProperty("立即付款")
    Boolean pay;
    @ApiModelProperty("立即取消")
    Boolean cancel;
    @ApiModelProperty("立即删除")
    Boolean deleted;
    @ApiModelProperty("查看物流")
    Boolean logistic;
    @ApiModelProperty("确认收货")
    Boolean receipt;
    @ApiModelProperty("立即评价")
    Boolean evaluation;
    @ApiModelProperty("立即追评")
    Boolean additionalEvaluation;
    @ApiModelProperty("待核销")
    Boolean verification;
    @ApiModelProperty("多阶段用户确认")
    Boolean confirmStepOrder;
    @ApiModelProperty("更多")
    Boolean other;
}
