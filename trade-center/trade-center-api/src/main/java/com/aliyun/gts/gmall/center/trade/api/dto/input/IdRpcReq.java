package com.aliyun.gts.gmall.center.trade.api.dto.input;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IdRpcReq extends AbstractQueryRpcRequest {
    private Long id;
}
