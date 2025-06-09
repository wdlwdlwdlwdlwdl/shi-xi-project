package com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.TradeCommandRpcRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "售后单操作")
public class ReversalModifyRpcReq extends TradeCommandRpcRequest {

    @ApiModelProperty(value = "售后主单ID", required = true)
    private Long primaryReversalId;

    @ApiModelProperty("操作顾客ID")
    private Long custId;

    @ApiModelProperty("操作卖家ID")
    private Long sellerId;

    @ApiModelProperty("是否系统操作")
    private boolean systemOperate = false;

    @ApiModelProperty("售后状态")
    private Integer reversalStatus;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(primaryReversalId, I18NMessageUtils.getMessage("aftersales.main.order")+" [ID] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "售后主单ID不能为空"

    }
}
