package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "申请发票")
@Data
public class OrderInvoiceApplyCommand extends LoginRestCommand {
    @ApiModelProperty(value = "主订单ID", required = true)
    private Long primaryOrderId;
    @ApiModelProperty("发票抬头信息")
    private Long invoiceId;

    @Override
    public void checkInput() {
        ParamUtil.nonNull(this.primaryOrderId, I18NMessageUtils.getMessage("main.order.number.required"));  //# "主订单号不能为空"
        ParamUtil.nonNull(this.invoiceId, I18NMessageUtils.getMessage("invoice.header.info.required"));  //# "发票抬头信息不能为空"
    }
}
