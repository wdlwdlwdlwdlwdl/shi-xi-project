package com.aliyun.gts.gmall.platform.trade.api.dto.common.logistics;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("物流进度")
@Data
public class LogisticsFlowDTO extends AbstractOutputInfo {

    @ApiModelProperty("物流处理时间")
    String processTime;

    @ApiModelProperty("物流处理描述")
    String processDesc;

    @ApiModelProperty("备注")
    String remark;

}
