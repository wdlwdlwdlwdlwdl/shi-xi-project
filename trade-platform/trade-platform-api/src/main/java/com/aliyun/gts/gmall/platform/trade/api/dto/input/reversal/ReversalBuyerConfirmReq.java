package com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("买家确认收到退款参数")
public class ReversalBuyerConfirmReq extends ReversalModifyRpcReq {

    @ApiModelProperty("买家收到退款的单号")
    private String bcrNumber;

    @ApiModelProperty("买家收到退款的留言")
    private String bcrMemo;

}
