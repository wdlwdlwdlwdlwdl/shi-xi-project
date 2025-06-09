package com.aliyun.gts.gmall.test.platform.trade.integrate.mock;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CommonByIdAndCustByIdQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerAddressQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerAddressDTO;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerAddressReadFacade;
import com.aliyun.gts.gmall.test.platform.trade.integrate.cases.base.TestConstants;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

@Component
public class MockCustomerAddressReadFacade implements CustomerAddressReadFacade {
    @Override
    public RpcResponse<CustomerAddressDTO> query(CommonByIdAndCustByIdQuery commonByIdAndCustByIdQuery) {
        return RpcResponse.ok(mockAddr());
    }

    @Override
    public RpcResponse<PageInfo<CustomerAddressDTO>> pageQuery(CustomerAddressQuery customerAddressQuery) {
        return RpcResponse.ok(new PageInfo<>(1L, Lists.newArrayList(mockAddr())));
    }

    private CustomerAddressDTO mockAddr() {
        CustomerAddressDTO addr = new CustomerAddressDTO();
        addr.setId(123L);
        addr.setProvinceId(1L);
        addr.setCityId(11L);
        addr.setAreaId(111L);
        addr.setProvince("XX");
        addr.setCity("XX");
        addr.setArea("XX");
        addr.setCompleteAddr("XXXX");
        addr.setPhone("11111111111");
        addr.setName("Name");
        addr.setAddressDetail("XXX");
        addr.setCustId(TestConstants.CUST_ID);
        return addr;
    }
}
