package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;

import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BuyerConfirmPayRestCommand extends LoginRestCommand {

    Long primaryOrderId;

    String paymentId;
    @ApiModelProperty("是否支付")
    Boolean paid;
}
