package com.aliyun.gts.gmall.test.platform.trade.integrate.mock;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.promotion.api.dto.account.AcPointConfigDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.model.ControlPointConfigDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.model.PromotionConfigDTO;
import com.aliyun.gts.gmall.platform.promotion.api.facade.admin.PromotionConfigFacade;
import com.aliyun.gts.gmall.platform.promotion.common.query.PromConfigQuery;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class MockPromotionConfigFacade implements PromotionConfigFacade {
    @Override
    public RpcResponse<Long> create(PromotionConfigDTO promotionConfigDTO) {
        return null;
    }

    @Override
    public RpcResponse<Boolean> updateById(PromotionConfigDTO promotionConfigDTO) {
        return null;
    }

    @Override
    public RpcResponse<PromotionConfigDTO> queryByKey(PromConfigQuery promConfigQuery) {
        return null;
    }

    @Override
    public RpcResponse<AcPointConfigDTO> queryAccountPointConfig() {
        AcPointConfigDTO conf = new AcPointConfigDTO();
        conf.setDeductPointValue(new BigDecimal("0.01"));
        conf.setGrantPointValue(new BigDecimal("1"));
        conf.setTradeDeductPoint(true);
        conf.setTradeGrantPoint(true);
        return RpcResponse.ok(conf);
    }

    @Override
    public RpcResponse<ControlPointConfigDTO> queryControlPoint() {
        return null;
    }
}
