package com.aliyun.gts.gmall.center.trade.api.dto.input;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.TradeCommandRpcRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EvoucherModifyRpcReq extends TradeCommandRpcRequest {

    @ApiModelProperty(value = "电子凭证码", required = true)
    private Long evCode;

    @ApiModelProperty("卖家ID, 传入时仅用于数据越权校验")
    private Long sellerId;

    @ApiModelProperty("顾客ID, 传入时仅用于数据越权校验")
    private Long custId;

    @ApiModelProperty("操作人ID")
    private Long operatorId;

    @ApiModelProperty("操作人名称")
    private String operatorName;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.nonNull(evCode, I18NMessageUtils.getMessage("voucher.code.cannot.be.empty"));  //# "电子凭证码不能为空"
    }
}
