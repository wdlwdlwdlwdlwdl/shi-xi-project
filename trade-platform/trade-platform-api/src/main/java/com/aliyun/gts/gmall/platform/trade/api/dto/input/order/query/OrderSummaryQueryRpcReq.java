package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OrderSummaryQueryRpcReq extends AbstractQueryRpcRequest {

    /** 发送状态 */
    private int isSend;
}
