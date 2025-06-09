package com.aliyun.gts.gmall.platform.trade.api.dto.input;

import com.aliyun.gts.gmall.framework.api.dto.ClientInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractCommandRpcRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TradeCommandRpcRequest extends AbstractCommandRpcRequest {

    @ApiModelProperty(value = "客户端信息", name = "clientInfo")
    private ClientInfo clientInfo;

    @Override
    public void checkInput() {
        super.checkInput();
         //ParamUtil.nonNull(clientInfo, "客户端信息不能为空");
         //clientInfo.checkInput();
    }
}
