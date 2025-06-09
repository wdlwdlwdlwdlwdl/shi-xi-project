package com.aliyun.gts.gmall.center.trade.api.dto.input;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BuyOrderCntReq extends AbstractQueryRpcRequest {

    private Long custId;

    private Long itemId;

    @ApiModelProperty("目前下单数限购到商品ID, skuId无用")
    private Long skuId;

    private Long campId;
}
