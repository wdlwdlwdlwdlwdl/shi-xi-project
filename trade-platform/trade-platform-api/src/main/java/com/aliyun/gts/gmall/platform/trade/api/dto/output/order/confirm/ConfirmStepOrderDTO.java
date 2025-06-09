package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.platform.trade.common.constants.StepOrderStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ConfirmStepOrderDTO extends AbstractOutputInfo {

    @ApiModelProperty("阶段序号")
    private Integer stepNo;

    @ApiModelProperty("阶段名称")
    private String stepName;

    /** @see StepOrderStatusEnum */
    @ApiModelProperty("阶段状态, @see StepOrderStatusEnum")
    private Integer status;

    @ApiModelProperty("实付现金")
    private Long realAmt;

    @ApiModelProperty("积分金额")
    private Long pointAmt;

    @ApiModelProperty("积分个数(原子积分)")
    private Long pointCount;
}
