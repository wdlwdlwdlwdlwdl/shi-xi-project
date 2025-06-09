package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;

import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "发票查看")
@Data
public class OrderInvoiceInfoCommand extends LoginRestCommand {
    @ApiModelProperty(value = "发票id")
    private Long id;

    @ApiModelProperty(value = "主订单id")
    private Long primaryOrderId;

}
