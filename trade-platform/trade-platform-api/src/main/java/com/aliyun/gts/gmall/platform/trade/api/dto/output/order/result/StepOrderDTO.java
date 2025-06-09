package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result;

import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.platform.trade.common.constants.StepOrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.util.NumUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class StepOrderDTO extends AbstractOutputInfo {

    @ApiModelProperty("主订单号")
    private Long primaryOrderId;

    @ApiModelProperty("阶段序号")
    private Integer stepNo;

    @ApiModelProperty("阶段名称")
    private String stepName;

    /** @see StepOrderStatusEnum */
    @ApiModelProperty("阶段状态, StepOrderStatusEnum")
    private Integer status;

    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("修改时间")
    private Date gmtModified;

    @ApiModelProperty("版本号")
    private Long version;


    // =====================  金额  ===========================

    @ApiModelProperty("实付现金")
    private Long realAmt;

    @ApiModelProperty("积分金额")
    private Long pointAmt;

    @ApiModelProperty("积分个数")
    private Long pointCount;

    @JSONField(serialize = false)
    @ApiModelProperty("总金额")
    public Long getTotalAmt() {
        return NumUtils.getNullZero(realAmt) + NumUtils.getNullZero(pointAmt);
    }


    // =====================  features  ===========================

    @ApiModelProperty("支付时间")
    private Long payTime;

    @ApiModelProperty("卖家处理时间")
    private Long sellerHandleTime;

    @ApiModelProperty("买家确认时间")
    private Long customerConfirmTime;
}
