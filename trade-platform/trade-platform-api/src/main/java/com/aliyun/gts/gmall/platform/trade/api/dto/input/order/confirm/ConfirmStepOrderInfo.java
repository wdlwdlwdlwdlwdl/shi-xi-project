package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm;

import com.aliyun.gts.gmall.framework.api.dto.AbstractInputParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class ConfirmStepOrderInfo extends AbstractInputParam {

    @ApiModelProperty(value = "模版名称")
    private String stepTemplateName;

    @ApiModelProperty(value = "多阶段参数")
    private Map<String, String> stepContextProps;
}
