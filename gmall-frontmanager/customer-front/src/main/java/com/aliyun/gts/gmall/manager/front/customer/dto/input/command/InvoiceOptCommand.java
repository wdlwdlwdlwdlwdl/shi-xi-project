package com.aliyun.gts.gmall.manager.front.customer.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 发票操作（删除等）
 *
 * @author tiansong
 */
@Data
@ApiModel("发票操作（删除等）")
public class InvoiceOptCommand extends LoginRestCommand {
    @ApiModelProperty("发票ID")
    private Long invoiceId;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(invoiceId, I18NMessageUtils.getMessage("invoice")+"ID"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "发票ID不能为空"
    }
}
