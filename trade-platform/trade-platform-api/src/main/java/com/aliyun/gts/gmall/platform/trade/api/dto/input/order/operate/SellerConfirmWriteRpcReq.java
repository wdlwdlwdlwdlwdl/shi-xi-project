package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.TradeCommandRpcRequest;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 卖家确认接单
 */
@Data
public class SellerConfirmWriteRpcReq extends TradeCommandRpcRequest {

    @NotNull
    Long sellerId;

    @NotNull
    Long primaryOrderId;

    @NotNull
    Boolean confirm;

    String reasonName;

    String reasonCode;

    String otpCode;

    IcWarehouseRpcReq warehouse;


}
