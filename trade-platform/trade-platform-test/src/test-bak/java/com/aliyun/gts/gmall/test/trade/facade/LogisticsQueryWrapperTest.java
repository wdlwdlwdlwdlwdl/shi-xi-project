package com.aliyun.gts.gmall.test.platform.facade;

import java.util.List;

import com.alibaba.fastjson.JSON;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.logistics.LogisticsDetailDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.CustomerOrderQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.LogisticsDetailQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.OrderDetailQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDetailDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.TcLogisticsReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderReadFacade;
import com.aliyun.gts.gmall.test.platform.base.BaseTest;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class LogisticsQueryWrapperTest extends BaseTest {

    @Autowired
    TcLogisticsReadFacade tcLogisticsReadFacade;

    @Test
    public void test(){
        LogisticsDetailQueryRpcReq req = new LogisticsDetailQueryRpcReq();
        req.setPrimaryOrderId(7294360000494L);
        RpcResponse<List<LogisticsDetailDTO>>  response = tcLogisticsReadFacade.queryLogisticsDetail(req);
        System.out.println(response.getData());
    }

}
