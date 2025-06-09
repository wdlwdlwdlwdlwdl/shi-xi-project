package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.create;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.platform.trade.common.util.NumUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class PrimaryOrderResultDTO extends AbstractOutputInfo {

    @ApiModelProperty("主订单ID")
    private Long primaryOrderId;

    @ApiModelProperty("支付金额--实付 (多阶段首笔)")
    private Long payRealAmt;

    @ApiModelProperty("支付金额--现金 (多阶段首笔)")
    private Long payPointAmt;

    public Long getPayTotalAmt() {
        return NumUtils.getNullZero(payRealAmt) + NumUtils.getNullZero(payPointAmt);
    }

    public boolean isFullDeduct() {
        return NumUtils.getNullZero(payRealAmt) == 0L;
    }
}
