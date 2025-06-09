package com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "售后原因对象")
public class ReversalReasonDTO extends AbstractOutputInfo {

    private Integer reasonCode;
    private String reasonMessage;
}
