package com.aliyun.gts.gmall.manager.front.customer.facade.impl;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.customer.converter.CustomerCampSubscribeConverter;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.CustomerCampSubscribeCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.CustomerCampUnSubscribeCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.CustomerCampSubscribeQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.customer.CustomerCampSubscribeVO;
import com.aliyun.gts.gmall.manager.front.customer.facade.CustomerCampSubscribeFacade;
import com.aliyun.gts.gmall.platform.promotion.api.dto.input.CustomerCampSubscribeQueryReq;
import com.aliyun.gts.gmall.platform.promotion.api.dto.model.CustomerCampSubscribeDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.model.CustomerCampUnSubscribeDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.CustomerCampSubscribeResultDTO;
import com.aliyun.gts.gmall.platform.promotion.api.facade.cust.CustomerCampSubscribeReadFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.cust.CustomerCampSubscribeWriteFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 *
 */
@Slf4j
@Service
public class CustomerCampSubscribeFacadeImpl implements CustomerCampSubscribeFacade {


    @Autowired
    private CustomerCampSubscribeWriteFacade customerCampSubscribeWriteFacade;

    @Autowired
    private CustomerCampSubscribeReadFacade  customerCampSubscribeReadFacade;



    @Autowired
    private CustomerCampSubscribeConverter customerCampSubscribeConverter;


    /**
     * 订阅
     * @param campSubscribeCommand
     * @return
     */
    @Override
    public RestResponse<Boolean> campSubscribe(CustomerCampSubscribeCommand campSubscribeCommand) {
        CustomerCampSubscribeDTO customerCampSubscribeDTO = customerCampSubscribeConverter.convertToSubDTO(campSubscribeCommand);
        RpcResponse<Boolean> subscribe = customerCampSubscribeWriteFacade.subscribe(customerCampSubscribeDTO);
        return RestResponse.ok(subscribe.getData());
    }



    @Override
    public RestResponse<Boolean> campUnSubscribe(CustomerCampUnSubscribeCommand campUnSubscribeCommand) {
        CustomerCampUnSubscribeDTO customerCampUnSubscribeDTO = customerCampSubscribeConverter.convertToUnSubDTO(campUnSubscribeCommand);
        RpcResponse<Boolean> unSubscribe = customerCampSubscribeWriteFacade.unSubscribe(customerCampUnSubscribeDTO);
        return RestResponse.ok(unSubscribe.getData());

    }

    @Override
    public RestResponse<PageInfo<CustomerCampSubscribeVO>> getSubscribes(CustomerCampSubscribeQuery subscribeQuery) {
        CustomerCampSubscribeQueryReq customerCampSubscribeQueryReq = customerCampSubscribeConverter.convertCustomerSubQueryReq(subscribeQuery);
        RpcResponse<PageInfo<CustomerCampSubscribeResultDTO>> pageInfoRpcResponse = customerCampSubscribeReadFacade.queryCampSubscribe(customerCampSubscribeQueryReq);
        PageInfo<CustomerCampSubscribeResultDTO> pageInfo = pageInfoRpcResponse.getData();
        if (null != pageInfo) {
            PageInfo<CustomerCampSubscribeVO> result = new PageInfo<>();
            result.setTotal(pageInfo.getTotal());
            result.setList(pageInfo.getList().stream().map(customerCampSubscribeConverter::convertCustomerSubVO).collect(Collectors.toList()));
            return RestResponse.ok(result);
        }
        return RestResponse.ok(null);
    }


//    @Override
//    public RestResponse<PageInfo<CustomerCampSubscribeVO>> getSubscribes(CustomerCampSubscribeQuery subscribeQuery) {
//        //todo
//        RpcResponse<PageInfo<CustomerCampSubscribeResultDTO>> pageInfoRpcResponse = customerCampSubscribeReadFacade.queryCampSubscribe(null);
//        PageInfo<CustomerCampSubscribeResultDTO> pageInfo = pageInfoRpcResponse.getData();
//        if (null != pageInfo) {
//            PageInfo<CustomerGrowthRecordVO> result = new PageInfo<>();
//            result.setTotal(pageInfo.getTotal());
//            result.setList(pageInfo.getList().stream().map(CustomerCampSubscribeConverter::convertCustomerSubVO).collect(Collectors.toList()));
//            RestResponse.ok(result);
//        }
//        return RestResponse.ok(null);
//    }


}
