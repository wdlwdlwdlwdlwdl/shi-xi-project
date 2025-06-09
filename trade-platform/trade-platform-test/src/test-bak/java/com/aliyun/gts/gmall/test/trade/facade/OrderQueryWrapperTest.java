package com.aliyun.gts.gmall.test.platform.facade;

import java.util.Map;

import com.alibaba.fastjson.JSON;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.*;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDetailDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderReadFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderEvaluateEnum;
import com.aliyun.gts.gmall.test.platform.base.BaseTest;
import com.google.common.collect.Sets;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class OrderQueryWrapperTest extends BaseTest {

    @Autowired
    OrderReadFacade orderReadFacade;

    @Test
    public void testCustomerQuery(){
        CustomerOrderQueryRpcReq customerOrderQueryRpcReq = new CustomerOrderQueryRpcReq();
        customerOrderQueryRpcReq.setCustId(100543L);
        customerOrderQueryRpcReq.setStatusList(Lists.newArrayList(
                new OrderStatusInfo(25),
                new OrderStatusInfo(27),
                new OrderStatusInfo(30)
        ));
        customerOrderQueryRpcReq.setEvaluate(OrderEvaluateEnum.NOT_EVALUATE.getCode());
        //customerOrderQueryRpcReq.setPageSize(100);
        for(int i = 0 ; i < 10 ; i++){
            RpcResponse<PageInfo<MainOrderDTO>> response = orderReadFacade.queryCustOrderList(customerOrderQueryRpcReq);
            log.info(JSON.toJSONString(response));
            System.out.println(JSON.toJSONString(response));
            customerOrderQueryRpcReq.setCurrentPage(i+2);
        }


    }

    @Test
    public void testQueryByPrimaryId(){
        OrderDetailQueryRpcReq orderDetailQueryRpcReq = new OrderDetailQueryRpcReq();
        orderDetailQueryRpcReq.setPrimaryOrderId(5630010000001L);
        orderDetailQueryRpcReq.setCustId(100001L);
        RpcResponse<MainOrderDetailDTO>  order = orderReadFacade.queryOrderDetail(orderDetailQueryRpcReq);
        System.out.println(JSON.toJSONString(order));
    }

    @Test
    public void testCount(){
        //CountOrderQueryRpcReq req = new CountOrderQueryRpcReq();
        //req.setStatus(Lists.newArrayList( 35, 25, 27, 30));
        //req.setCustId(100005L);
        //RpcResponse<Map<Integer , Integer>>  result = orderReadFacade.countOrderByStatus(req);
        //System.out.println(JSON.toJSONString(result));

        CustomerOrderQueryRpcReq customerOrderQueryRpcReq = new CustomerOrderQueryRpcReq();
        customerOrderQueryRpcReq.setCustId(100005L);
        customerOrderQueryRpcReq.setStatusList(Lists.newArrayList(
                new OrderStatusInfo(35),
                new OrderStatusInfo(25),
                new OrderStatusInfo(27),
                new OrderStatusInfo(30)));
        //customerOrderQueryRpcReq.setPageSize(100);
        RpcResponse<PageInfo<MainOrderDTO>> response = orderReadFacade.queryCustOrderList(customerOrderQueryRpcReq);
        log.info(JSON.toJSONString(response));
    }

    @Test
    public void testSearchSoldOrders(){
        SellerOrderQueryRpcReq req = new SellerOrderQueryRpcReq();
        req.setSellerId(-1L);
        RpcResponse<PageInfo<MainOrderDTO>>  response = orderReadFacade.querySellerOrderList(req);
        System.out.println(JSON.toJSONString(response));
    }

}
