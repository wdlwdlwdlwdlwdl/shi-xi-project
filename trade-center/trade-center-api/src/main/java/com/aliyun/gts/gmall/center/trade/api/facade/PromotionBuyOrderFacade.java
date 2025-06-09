package com.aliyun.gts.gmall.center.trade.api.facade;

import com.aliyun.gts.gmall.center.trade.api.dto.input.BuyOrderCntReq;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;

public interface PromotionBuyOrderFacade {

    /**
     * 营销限购, 查用户已下订单数
     */
    RpcResponse<Long> queryBuyOrdCnt(BuyOrderCntReq req);
}
