package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm;

import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.platform.trade.common.util.NumUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PayPriceDTO extends AbstractOutputInfo {

    @ApiModelProperty("实付现金")
    private Long realAmt;

    @ApiModelProperty("积分金额")
    private Long pointAmt;

    @ApiModelProperty("积分个数(原子积分)")
    private Long pointCount;

    @ApiModelProperty("最大可用积分数(原子积分)")
    private Long maxAvailablePoint;

    @ApiModelProperty("总金额")
    @JSONField(serialize = false)
    public Long getTotalAmt() {
        return NumUtils.getNullZero(realAmt) + NumUtils.getNullZero(pointAmt);
    }
}
