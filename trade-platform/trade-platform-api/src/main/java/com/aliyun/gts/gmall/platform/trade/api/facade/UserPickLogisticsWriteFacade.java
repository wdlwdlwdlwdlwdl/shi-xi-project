package com.aliyun.gts.gmall.platform.trade.api.facade;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.UserPickLogisticsRpcReq;
import io.swagger.annotations.ApiOperation;

/**
 * 用户确认物流方式接口
 * @anthor shifeng
 * @version 1.0.1
 * 2025-3-22 17:14:53
 */
public interface UserPickLogisticsWriteFacade {


    @ApiOperation("写入用户物流方式确认")
    RpcResponse<Boolean> saveUserPickLogistics(UserPickLogisticsRpcReq userPickLogisticsRpcReq);

}
