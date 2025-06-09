package com.aliyun.gts.gmall.center.trade.api.dto.input;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractCommandRpcRequest;
import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractPageQueryRpcRequest;
import lombok.Data;

@Data
public class PaymentIdRpcReq extends AbstractCommandRpcRequest {

    Long custId;

    Long primaryOrderId;

    String paymentId;

    Boolean paid;

}
