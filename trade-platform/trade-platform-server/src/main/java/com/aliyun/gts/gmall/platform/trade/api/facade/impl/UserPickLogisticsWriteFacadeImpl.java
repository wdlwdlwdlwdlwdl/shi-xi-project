package com.aliyun.gts.gmall.platform.trade.api.facade.impl;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.UserPickLogisticsRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.facade.UserPickLogisticsWriteFacade;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.UserPickLogisticsService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserPickLogisticsWriteFacadeImpl implements UserPickLogisticsWriteFacade {

    @Autowired
    private UserPickLogisticsService userPickLogisticsService;

    /**
     * 写入用户确认
     * @param userPickLogisticsRpcReq
     * @return
     */
    @ApiOperation("写入用户物流方式确认")
    public RpcResponse<Boolean> saveUserPickLogistics(UserPickLogisticsRpcReq userPickLogisticsRpcReq) {
        userPickLogisticsService.insert(userPickLogisticsRpcReq);
        return RpcResponse.ok(Boolean.TRUE);
    }
}
