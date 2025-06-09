package com.aliyun.gts.gmall.center.trade.matchall.ext;

import com.aliyun.gts.gmall.center.trade.core.domainservice.PromotionOrderLimitService;
import com.aliyun.gts.gmall.center.trade.domain.constant.TradeInnerExtendKeys;
import com.aliyun.gts.gmall.center.trade.domain.entity.promotion.BuyOrdsLimit;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultOrderBizRollbackExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.create.OrderBizRollbackExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Extension(points = {OrderBizRollbackExt.class})
public class MatchAllBizRollbackExt extends DefaultOrderBizRollbackExt {

    @Autowired
    private PromotionOrderLimitService promotionOrderLimitService;

    @Override
    public void rollbackBizResource(CreatingOrder order) {
        super.rollbackBizResource(order);
        for (MainOrder main : order.getMainOrders()) {
            for (SubOrder sub : main.getSubOrders()) {
                processDecrBuyOrds(sub);
            }
        }
    }

    private void processDecrBuyOrds(SubOrder sub) {
        if (Boolean.TRUE.equals(sub.getExtra(TradeInnerExtendKeys.PROMOTION_IS_BUY_ORDS_INCR))) {
            try {
                BuyOrdsLimit limit = (BuyOrdsLimit) sub.getExtra(TradeInnerExtendKeys.PROMOTION_BUY_ORDS_LIMIT);
                promotionOrderLimitService.decrBuyOrds(limit);
            } catch (Exception e) {
                log.error("decrBuyOrds", e);
            }
        }
    }

}
