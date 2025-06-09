package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 买家确认退款
 */
@Data
@ApiModel("买家确认退款")
public class ReversalBuyerConfirmRestCommand extends ModifyReversalRestCommand {

    @ApiModelProperty("买家收到退款的单号")
    private String bcrNumber;

    @ApiModelProperty("买家收到退款的留言")
    private String bcrMemo;


    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.notBlank(this.bcrNumber, I18NMessageUtils.getMessage("refund.number.required"));  //# "退款的单号不能为空"
        ParamUtil.notBlank(this.bcrNumber, I18NMessageUtils.getMessage("refund.message.required"));  //# "退款的留言不能为空"
    }
}
