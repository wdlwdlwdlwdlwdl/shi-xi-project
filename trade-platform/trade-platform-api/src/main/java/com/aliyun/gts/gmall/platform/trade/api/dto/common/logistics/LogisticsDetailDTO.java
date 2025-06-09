package com.aliyun.gts.gmall.platform.trade.api.dto.common.logistics;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.List;

@ApiModel("详细物流信息")
@Data
public class LogisticsDetailDTO extends AbstractOutputInfo {

    @ApiModelProperty("主订单id")
    Long primaryOrderId;

    @ApiModelProperty("收货信息")
    ReceiverInfoDTO receiverInfo;

    @ApiModelProperty("物流进度")
    List<LogisticsFlowDTO> flowList;

    @ApiModelProperty("扩展信息")
    HashMap logisticsAttr;


    @ApiModelProperty("物流公司")
    String deliveryCompany;

    @ApiModelProperty("物流单号")
    String deliveryId;

    @ApiModelProperty("物流状态")
    Integer deliveryStatus;

    @ApiModelProperty("票据")
    String logisticsUrl;

    @ApiModelProperty("otp")
    String otpCode;

}
