package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;

import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderPickCommand extends LoginRestCommand {

    @ApiModelProperty(value = "物流方式", required = true)
    private String deliveryType;

}
