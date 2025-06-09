package com.aliyun.gts.gmall.test.platform.trade.integrate.mock;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.promotion.api.dto.input.OrderCreateReq;
import com.aliyun.gts.gmall.platform.promotion.api.facade.cust.PromotionAssetsWriteFacade;
import org.springframework.stereotype.Component;

@Component
public class MockPromotionAssetsWriteFacade implements PromotionAssetsWriteFacade {
    @Override
    public RpcResponse<Boolean> deductAssets(OrderCreateReq orderCreateReq) {
        return RpcResponse.ok(true);
    }

    @Override
    public RpcResponse<Boolean> returnAssets(OrderCreateReq orderCreateReq) {
        return RpcResponse.ok(true);
    }
}
