package com.aliyun.gts.gmall.test.platform.trade.integrate.mock;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CommonByIdQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.input.SellerAddressQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerAddressDTO;
import com.aliyun.gts.gmall.platform.user.api.facade.SellerAddressReadFacade;
import org.springframework.stereotype.Component;

@Component
public class MockSellerAddressReadFacade implements SellerAddressReadFacade {
    @Override
    public RpcResponse<SellerAddressDTO> query(CommonByIdQuery commonByIdQuery) {
        return null;
    }

    @Override
    public RpcResponse<PageInfo<SellerAddressDTO>> pageQuery(SellerAddressQuery sellerAddressQuery) {
        return null;
    }
}
