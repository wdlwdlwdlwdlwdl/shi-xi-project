package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 主订单操作
 *
 * @author tiansong
 */
@ApiModel("主订单操作")
@Data
public class PrimaryOrderRestCommand extends LoginRestCommand {
    @ApiModelProperty("主订单id")
    private Long primaryOrderId;

    private String reasonCode;

    private String reasonName;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(primaryOrderId, I18NMessageUtils.getMessage("main.order")+"ID"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "主订单ID不能为空"
        ParamUtil.expectTrue(primaryOrderId > 0L, I18NMessageUtils.getMessage("main.order")+"ID"+I18NMessageUtils.getMessage("must.be.pos.int"));  //# "主订单ID必须为正整数"
    }
}
