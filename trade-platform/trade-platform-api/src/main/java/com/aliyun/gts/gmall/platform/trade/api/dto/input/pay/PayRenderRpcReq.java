package com.aliyun.gts.gmall.platform.trade.api.dto.input.pay;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xinchen
 */
@Data
@ApiModel("收银台展示入参")
public class PayRenderRpcReq extends AbstractQueryRpcRequest {

    @ApiModelProperty(value = "买家id", required = true)
    private Long custId;

    @ApiModelProperty(value = "主订单号", required = true)
    private Long primaryOrderId;

    @ApiModelProperty(value = "下单渠道", required = true)
    private String orderChannel;

    @Override
    public void checkInput() {
        super.checkInput();
        ParamUtil.expectTrue(custId != null, I18NMessageUtils.getMessage("buyer")+" [ID] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "买家ID不能为空"
        ParamUtil.expectTrue(primaryOrderId > 0, I18NMessageUtils.getMessage("main.order.empty"));  //# "主订单号不能为空"
        ParamUtil.notBlank(orderChannel, I18NMessageUtils.getMessage("place.channel.empty"));  //# "下单渠道不能为空"
    }
}
