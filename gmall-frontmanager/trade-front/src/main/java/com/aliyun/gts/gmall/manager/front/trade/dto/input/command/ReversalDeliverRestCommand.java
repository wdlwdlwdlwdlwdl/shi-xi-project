package com.aliyun.gts.gmall.manager.front.trade.dto.input.command;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import java.util.List;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.param.LogisticsInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 售后邮寄
 *
 * @author tiansong
 */
@Data
@ApiModel("售后邮寄")
public class ReversalDeliverRestCommand extends ModifyReversalRestCommand {

    @ApiModelProperty(value = "邮寄物流信息", required = true)
    private List<LogisticsInfo> logisticsList;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.notEmpty(this.logisticsList, I18NMessageUtils.getMessage("logistics.info.required"));  //# "物流信息不能为空"
        this.logisticsList.forEach(logisticsInfo -> logisticsInfo.checkInput());
    }
}
