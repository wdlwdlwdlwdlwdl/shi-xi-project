package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StepOrderVO extends PayPriceVO {

    @ApiModelProperty("阶段序号")
    private Integer stepNo;
    @ApiModelProperty("阶段名称")
    private String stepName;
    @ApiModelProperty("阶段状态, @see StepOrderStatusEnum")
    private Integer status;
}
