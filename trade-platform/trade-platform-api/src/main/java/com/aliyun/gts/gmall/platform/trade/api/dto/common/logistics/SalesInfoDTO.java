package com.aliyun.gts.gmall.platform.trade.api.dto.common.logistics;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("发货人信息")
public class SalesInfoDTO extends AbstractOutputInfo {

    @ApiModelProperty("发货人姓名")
    String salesName;
    @ApiModelProperty("发货人电话")
    String salesPhone;
    @ApiModelProperty("发货人地址")
    String salesAddr;

}
