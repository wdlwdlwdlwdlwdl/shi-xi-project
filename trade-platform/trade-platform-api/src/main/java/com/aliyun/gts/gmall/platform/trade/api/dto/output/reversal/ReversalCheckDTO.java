package com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ReversalCheckDTO extends AbstractOutputInfo {

    private List<ReversalSubOrderDTO> subOrders;

    @ApiModelProperty(value = "可退运费金额")
    private Long maxCancelFreightAmt;
}
