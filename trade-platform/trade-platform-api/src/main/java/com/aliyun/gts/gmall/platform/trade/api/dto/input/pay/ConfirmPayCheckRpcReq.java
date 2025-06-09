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
@ApiModel("支付确认入参")
public class ConfirmPayCheckRpcReq extends AbstractQueryRpcRequest {

    @ApiModelProperty(value = "主订单号", required = true)
    private Long primaryOrderId;
    @ApiModelProperty(value = "主订单号", required = true)
    private List<Long> primaryOrderIdList;
    @ApiModelProperty(value = "支付流水号", required = true)
    private String payFlowId;
    @ApiModelProperty(value = "买家ID", required = true)
    private Long custId;
    @ApiModelProperty(value = "统一购物车标识", required = true)
    private Long cartId;
    private String outFlowNo;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.expectTrue(custId != null, I18NMessageUtils.getMessage("buyer") + " [ID] " + I18NMessageUtils.getMessage("cannot.empty"));  //# "买家ID不能为空"
        ParamUtil.nonNull(cartId, I18NMessageUtils.getMessage("pay.num.empty"));  //# "统一购物车标识不能为空"
    }
}
