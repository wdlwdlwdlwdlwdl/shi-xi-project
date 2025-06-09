package com.aliyun.gts.gmall.center.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;

public interface PaySplitService {

    /**
     * 交易成功后针对主订单进行分账
     *
     * @param primaryOrderId
     * @return
     */
    TradeBizResult paySplitAfterTradeSuccess(Long primaryOrderId);

}
