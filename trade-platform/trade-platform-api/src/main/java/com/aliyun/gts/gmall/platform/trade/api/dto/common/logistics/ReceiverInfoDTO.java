package com.aliyun.gts.gmall.platform.trade.api.dto.common.logistics;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("收货人信息")
public class ReceiverInfoDTO extends AbstractOutputInfo {

    @ApiModelProperty("收货人姓名")
    String receiverName;
    @ApiModelProperty("收货人电话")
    String receiverPhone;
    @ApiModelProperty("收货人地址")
    String receiverAddr;

}
