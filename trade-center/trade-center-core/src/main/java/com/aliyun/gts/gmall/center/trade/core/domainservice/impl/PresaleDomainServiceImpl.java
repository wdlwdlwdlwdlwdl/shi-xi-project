package com.aliyun.gts.gmall.center.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.center.trade.common.constants.ExtBizCode;
import com.aliyun.gts.gmall.center.trade.core.constants.PresaleErrorCode;
import com.aliyun.gts.gmall.center.trade.core.domainservice.PresaleDomainService;
import com.aliyun.gts.gmall.center.trade.domain.entity.deposit.ItemDepositInfo;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.promotion.common.constant.PromotionExtrasKey;
import com.aliyun.gts.gmall.platform.promotion.common.constant.PromotionToolCodes;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemDivideDetail;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemPromotion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@Service
@Slf4j
public class PresaleDomainServiceImpl implements PresaleDomainService {

    @Override
    public boolean isPresaleItem(ItemPromotion prom) {
        return getPresaleDetail(prom) != null;
    }

    @Override
    public ItemDepositInfo getDepositFromItems(MainOrder mainOrder) {
        long depositAmt = 0L;
        Date tailStart = null;
        Date tailEnd = null;
        Long[] subOrderPrice = new Long[mainOrder.getSubOrders().size()];
        int idx = 0;
        for (SubOrder sub : mainOrder.getSubOrders()) {
            ItemDivideDetail preSale = getPresaleDetail(sub.getPromotions());
            if (preSale == null) {
                throw new GmallException(OrderErrorCode.MERGE_ORDER_FORBIDDEN);
            }

            long amt = getLong(preSale.getExtras().get(PromotionExtrasKey.PRESALE_DEPOSIT_AMOUNT));
            Entry<Date, Date> datePair = getDatePair(
                    preSale.getExtras().get(PromotionExtrasKey.PRESALE_BALANCE_TIME));
            Date start = datePair.getKey();
            Date end = datePair.getValue();

            depositAmt += amt;
            subOrderPrice[idx] = amt;
            if (tailStart == null || tailStart.getTime() > start.getTime()) {
                tailStart = start;
            }
            if (tailEnd == null || tailEnd.getTime() < end.getTime()) {
                tailEnd = end;
            }
            idx++;
        }

        return ItemDepositInfo.builder()
                .depositAmt(depositAmt)
                .subOrderDepositAmt(subOrderPrice)
                .tailStart(tailStart)
                .tailEnd(tailEnd)
                .build();
    }

    @Override
    public boolean isPresaleOrder(MainOrder mainOrder) {
        return mainOrder.getBizCodes() != null &&
                mainOrder.getBizCodes().contains(ExtBizCode.PRE_SALE);
    }

    private ItemDivideDetail getPresaleDetail(ItemPromotion prom) {
        if (prom == null || prom.getItemDivideDetails() == null) {
            return null;
        }
        for (ItemDivideDetail detail : prom.getItemDivideDetails()) {
            if (detail != null && PromotionToolCodes.YUSHOU.equals(detail.getToolCode())) {
                return detail;
            }
        }
        return null;
    }

    private static long getLong(Object o) {
        if (o instanceof Number) {
            return ((Number) o).longValue();
        }
        if (o instanceof String) {
            return Long.parseLong((String) o);
        }
        throw new GmallException(PresaleErrorCode.PROMOTION_DATA_ERROR);
    }

    private static Entry<Date, Date> getDatePair(Object o) {
        if (o instanceof Entry) {
            Entry en = (Entry) o;
            if (en.getKey() instanceof Date
                    && en.getValue() instanceof Date) {
                return en;
            }
        }
        throw new GmallException(PresaleErrorCode.PROMOTION_DATA_ERROR);
    }
}
