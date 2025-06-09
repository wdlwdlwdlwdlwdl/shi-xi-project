package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 多阶段订单操作
 *
 * @author tiansong
 */
@ApiModel("多阶段订单操作")
@Data
public class StepOrderRestCommand extends PrimaryOrderRestCommand {

    @ApiModelProperty("阶段号")
    private Integer stepNo;

    @ApiModelProperty("校验订单版本号")
    private Long checkVersion;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(stepNo, I18NMessageUtils.getMessage("stage.number.required"));  //# "阶段号不能为空"
        ParamUtil.expectTrue(stepNo > 0L, "stage.number.positive");
    }
}
