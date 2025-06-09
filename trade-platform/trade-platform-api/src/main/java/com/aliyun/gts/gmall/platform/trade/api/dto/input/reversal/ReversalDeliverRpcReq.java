package com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics.LogisticsInfoRpcReq;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("售后发货参数")
public class ReversalDeliverRpcReq extends ReversalModifyRpcReq {

    @ApiModelProperty(value = "物流单号", required = true)
    private List<LogisticsInfoRpcReq> logisticsList;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.notEmpty(logisticsList, I18NMessageUtils.getMessage("logistics.num.empty"));  //# "物流单号不能为空"
        for (LogisticsInfoRpcReq logis : logisticsList) {
            logis.checkInput();
        }
    }
}
