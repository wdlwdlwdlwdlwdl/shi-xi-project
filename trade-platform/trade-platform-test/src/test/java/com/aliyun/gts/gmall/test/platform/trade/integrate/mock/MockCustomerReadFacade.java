package com.aliyun.gts.gmall.test.platform.trade.integrate.mock;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.user.api.dto.contants.CustomerStatusEnum;
import com.aliyun.gts.gmall.platform.user.api.dto.input.*;
import com.aliyun.gts.gmall.platform.user.api.dto.output.*;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerReadFacade;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MockCustomerReadFacade implements CustomerReadFacade {
    @Override
    public RpcResponse<CustomerDTO> query(CustomerByIdQuery customerByIdQuery) {
        CustomerDTO customer = new CustomerDTO();
        customer.setId(customerByIdQuery.getId());
        customer.setStatus(CustomerStatusEnum.NORMAL.getCode().toString());
        return RpcResponse.ok(customer);
    }

    @Override
    public RpcResponse<PageInfo<CustomerDTO>> queryByCustInfo(CustomerQuery customerQuery) {
        return null;
    }

    @Override
    public RpcResponse<List<CustomerDTO>> queryByIds(CommonByBatchIdsQuery commonByBatchIdsQuery) {
        return null;
    }

    @Override
    public RpcResponse<List<CustomerDTO>> queryByPhones(CustomerByByPhonesQuery customerByByPhonesQuery) {
        return null;
    }

    @Override
    public RpcResponse<PageInfo<CustomerDTO>> search(CustomerSearch customerSearch) {
        return null;
    }

    @Override
    public RpcResponse<CustomerDTO> queryByAccount(CustomerByAccountQuery customerByAccountQuery) {
        return null;
    }

    @Override
    public RpcResponse<Boolean> checkByPwd(CustomerPwdCheckRpcRequest customerPwdCheckRpcRequest) {
        return null;
    }

    @Override
    public RpcResponse<PageInfo<CustomerExtendDTO>> queryExtend(CustomerExtendQuery customerExtendQuery) {
        return null;
    }

    @Override
    public RpcResponse<List<CustomerExtendDTO>> batchQueryExtend(CustomerExtendBatchQuery query) {
        return null;
    }

    @Override
    public RpcResponse<PageInfo<CustomerGrowthDTO>> queryCustGrowthRecords(CustGrowthHistoryQuery custGrowthHistoryQuery) {
        return null;
    }

    @Override
    public RpcResponse<List<CustomerLevelConfigDTO>> queryCustomerLevelConfig(DummyQuery dummyQuery) {
        return null;
    }

    @Override
    public RpcResponse<CustomerLevelDTO> queryCustomerLevel(CommonByIdQuery commonByIdQuery) {
        return null;
    }

    @Override
    public RpcResponse<List<BaseCustLevelDTO>> queryCustomersLevel(CommonByBatchIdsQuery commonByBatchIdsQuery) {
        return null;
    }
}
