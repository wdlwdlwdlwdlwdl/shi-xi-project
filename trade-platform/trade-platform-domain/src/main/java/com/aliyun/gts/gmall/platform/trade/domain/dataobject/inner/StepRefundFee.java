package com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class StepRefundFee extends RefundFee {

    @ApiModelProperty("每个阶段的退款金额")
    private Map<Integer, RefundFee> stepRefundFee;
}
