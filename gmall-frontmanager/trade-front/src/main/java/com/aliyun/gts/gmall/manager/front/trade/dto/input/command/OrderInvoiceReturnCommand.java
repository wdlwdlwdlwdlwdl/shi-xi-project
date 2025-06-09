package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "申请发票")
@Data
public class OrderInvoiceReturnCommand extends LoginRestCommand {
    @ApiModelProperty(value = "主订单id", required = true)
    private Long primaryOrderId;

    @ApiModelProperty(value = "发票id", required = true)
    private Long id;

    @Override
    public void checkInput() {
        ParamUtil.nonNull(this.primaryOrderId, I18NMessageUtils.getMessage("main.order.number.required"));  //# "主订单号不能为空"
    }
}
