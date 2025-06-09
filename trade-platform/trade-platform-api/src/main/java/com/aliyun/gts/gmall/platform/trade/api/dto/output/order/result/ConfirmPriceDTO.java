package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result;

import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.platform.trade.common.util.NumUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class ConfirmPriceDTO extends AbstractOutputInfo {

    @ApiModelProperty("确认收货实付金额, 减去了售中退款")
    private Long confirmRealAmt;

    @ApiModelProperty("确认收货积分金额, 减去了售中退款")
    private Long confirmPointAmt;

    @ApiModelProperty("确认收货打款总金额, 减去了售中退款")
    @JSONField(serialize = false)
    public Long getConfirmTotalAmt() {
        return NumUtils.getNullZero(confirmRealAmt) + NumUtils.getNullZero(confirmPointAmt);
    }

    @ApiModelProperty("分账金额, 合计等于 confirmPointAmt, 分账规则: SeparateRule")
    private Map<String, Long> separatePointAmt;

    @ApiModelProperty("分账金额, 合计等于 confirmRealAmt, 分账规则: SeparateRule")
    private Map<String, Long> separateRealAmt;
}
