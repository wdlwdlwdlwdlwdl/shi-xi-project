package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.TradeCommandRpcRequest;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 卖家备注
 */
@Data
public class SellerMemoWriteRpcReq extends TradeCommandRpcRequest {

    @NotNull
    Long sellerId;

    @NotNull
    Long primaryOrderId;

    @NotEmpty
    @Size(max = 512)
    String memo;

}
