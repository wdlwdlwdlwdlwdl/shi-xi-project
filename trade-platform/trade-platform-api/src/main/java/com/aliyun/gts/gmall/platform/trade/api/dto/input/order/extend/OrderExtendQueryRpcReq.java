package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.extend;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderExtendQueryRpcReq extends AbstractQueryRpcRequest {

    @ApiModelProperty(value = "主订单ID", required = true)
    private Long primaryOrderId;

    @ApiModelProperty("子订单ID")
    private Long orderId;

    @ApiModelProperty("扩展属性类型")
    private String extendType;

    @ApiModelProperty("扩展属性key")
    private String extendKey;

    @Override
    public void checkInput() {
        ParamUtil.nonNull(primaryOrderId, I18NMessageUtils.getMessage("main.order")+" [ID] "+I18NMessageUtils.getMessage("cannot.empty"));  //# "主订单ID不能为空"
    }
}
