package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 售后单修改
 *
 * @author tiansong
 */
@Data
@ApiModel("售后单修改")
public class ModifyReversalRestCommand extends LoginRestCommand {
    @ApiModelProperty(value = "售后主单ID",required = true)
    private Long primaryReversalId;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(this.primaryReversalId, I18NMessageUtils.getMessage("after.sales.main.order")+"ID"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "售后主单ID不能为空"
    }
}
