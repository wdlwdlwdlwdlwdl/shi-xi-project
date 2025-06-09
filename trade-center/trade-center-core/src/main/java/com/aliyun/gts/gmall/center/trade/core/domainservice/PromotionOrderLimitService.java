package com.aliyun.gts.gmall.center.trade.core.domainservice;

import com.aliyun.gts.gmall.center.trade.domain.entity.promotion.BuyOrdsLimit;

public interface PromotionOrderLimitService {

    Long queryBuyOrdCnt(BuyOrdsLimit uk);

    /**
     * 校验下单单数
     */
    boolean checkBuyOrds(BuyOrdsLimit limit);

    /**
     * 校验并记录下单单数 +1
     */
    boolean checkAndIncrBuyOrds(BuyOrdsLimit limit);

    /**
     * 下单单数 -1
     */
    void decrBuyOrds(BuyOrdsLimit limit);
}
