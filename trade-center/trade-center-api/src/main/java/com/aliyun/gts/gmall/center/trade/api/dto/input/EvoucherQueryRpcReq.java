package com.aliyun.gts.gmall.center.trade.api.dto.input;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EvoucherQueryRpcReq extends AbstractQueryRpcRequest {

    @ApiModelProperty("子订单ID")
    private Long orderId;

    @ApiModelProperty("电子凭证码 (根据码查询时, 返回结果唯一)")
    private Long evCode;

    @ApiModelProperty("卖家ID, 传入时仅用于数据越权校验")
    private Long sellerId;

    @ApiModelProperty("顾客ID, 传入时仅用于数据越权校验")
    private Long custId;

    @Override
    public void checkInput() {
        super.checkInput();
//        ParamUtil.expectTrue(orderId != null || evCode != null, "查询条件orderId/evCode 不能都为空");

        ParamUtil.expectTrue(orderId != null || evCode != null, "[orderId] or [evCode] " + I18NMessageUtils.getMessage("cannot.empty"));
//        I18NMessageUtils.getMessage("cannot.empty")
    }
}
