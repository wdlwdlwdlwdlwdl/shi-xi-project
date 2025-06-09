package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.TradeCommandRpcRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class StepOrderHandleRpcReq extends TradeCommandRpcRequest {

    @ApiModelProperty(value = "主订单ID", required = true)
    private Long primaryOrderId;

    @ApiModelProperty(value = "卖家ID")
    private Long sellerId;

    @ApiModelProperty(value = "用户ID")
    private Long custId;

    @ApiModelProperty(value = "阶段号", required = true)
    private Integer stepNo;

    @ApiModelProperty(value = "表单数据")
    private Map<String, String> formData;

    @ApiModelProperty(value = "是否为修改数据")
    private Boolean modify;

    @ApiModelProperty(value = "校验订单版本号")
    private Long checkVersion;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(primaryOrderId, I18NMessageUtils.getMessage("main.order")+" [ID] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "主订单ID不能为空"
        ParamUtil.nonNull(stepNo, I18NMessageUtils.getMessage("stage.num.empty"));  //# "阶段号不能为空"
        ParamUtil.expectTrue(sellerId != null || custId != null, "卖家ID和用户ID 不能都为空");
    }

    public boolean isModify() {
        return modify != null && modify.booleanValue();
    }
}
