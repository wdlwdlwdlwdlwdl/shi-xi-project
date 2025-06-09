package com.aliyun.gts.gmall.center.trade.server.facade.impl;

import com.aliyun.gts.gmall.center.trade.api.dto.input.ExampleRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.output.ExampleDTO;
import com.aliyun.gts.gmall.center.trade.api.facade.ExampleFacade;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class ExampleFacadeImpl implements ExampleFacade {

    @Override
    public RpcResponse<ExampleDTO> call(ExampleRpcReq req) {
        ExampleDTO result = new ExampleDTO();
        result.setName(req.getName());
        result.setRandom(ThreadLocalRandom.current().nextLong());
        return RpcResponse.ok(result);
    }
}
