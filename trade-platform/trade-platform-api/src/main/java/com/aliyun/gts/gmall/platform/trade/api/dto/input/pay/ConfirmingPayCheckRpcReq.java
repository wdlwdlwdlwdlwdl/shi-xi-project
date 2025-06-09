package com.aliyun.gts.gmall.platform.trade.api.dto.input.pay;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author xinchen
 */
@Data
@ApiModel("支付中确认入参")
public class ConfirmingPayCheckRpcReq extends AbstractQueryRpcRequest {

    @ApiModelProperty(value = "主订单号", required = true)
    private Long primaryOrderId;
    @ApiModelProperty(value = "主订单号")
    private List<Long> primaryOrderIdList;
    @ApiModelProperty(value = "支付流水号")
    private String payFlowId;
    @ApiModelProperty(value = "买家ID", required = true)
    private Long custId;
    @ApiModelProperty(value = "统一购物车标识")
    private Long cartId;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.expectTrue(custId != null, I18NMessageUtils.getMessage("buyer") + " [ID] " + I18NMessageUtils.getMessage("cannot.empty"));  //# "买家ID不能为空"
    }
}
