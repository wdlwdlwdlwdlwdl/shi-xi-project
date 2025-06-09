package com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal;

import com.aliyun.gts.gmall.platform.trade.api.dto.common.ReceiverDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("同意售后参数")
public class ReversalAgreeRpcReq extends ReversalModifyRpcReq {

    @ApiModelProperty("卖家收货地址")
    private ReceiverDTO receiver;
}
