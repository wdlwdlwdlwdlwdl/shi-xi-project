package com.aliyun.gts.gmall.center.trade.server.facade.impl;

import com.aliyun.gts.gmall.center.trade.api.dto.input.BuyOrderCntReq;
import com.aliyun.gts.gmall.center.trade.api.facade.PromotionBuyOrderFacade;
import com.aliyun.gts.gmall.center.trade.core.domainservice.PromotionOrderLimitService;
import com.aliyun.gts.gmall.center.trade.domain.entity.promotion.BuyOrdsLimit;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PromotionBuyOrderFacadeImpl implements PromotionBuyOrderFacade {

    @Autowired
    private PromotionOrderLimitService promotionOrderLimitService;

    @Override
    public RpcResponse<Long> queryBuyOrdCnt(BuyOrderCntReq req) {
        BuyOrdsLimit uk = new BuyOrdsLimit();
        uk.setSkuId(req.getSkuId());
        uk.setCustId(req.getCustId());
        uk.setCampId(req.getCampId());
        uk.setItemId(req.getItemId());
        Long result = promotionOrderLimitService.queryBuyOrdCnt(uk);
        return RpcResponse.ok(result);
    }
}
