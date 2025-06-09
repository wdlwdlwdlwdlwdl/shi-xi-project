package com.aliyun.gts.gmall.center.trade.core.domainservice.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.center.trade.core.constants.ExtOrderErrorCode;
import com.aliyun.gts.gmall.center.trade.core.domainservice.ManzengGiftService;
import com.aliyun.gts.gmall.center.trade.core.domainservice.OrderPriceRiskService;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.promotion.common.type.AssetsType;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderConfigService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemDivideDetail;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.SellerTradeConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OrderPriceRiskServiceImpl implements OrderPriceRiskService {

    private static final String KEY_MIN_UNIT_PRICE = "priceRiskRule.%s.minUnitPrice";
    private static final String KEY_MIN_DISCOUNT_PERCENT = "priceRiskRule.%s.minDiscountPercent";

    private static final String CODE_DEFAULT = "default";
    private static final String CODE_MIAOSHA = "miaosha";


    @Autowired
    private ManzengGiftService manzengGiftService;
    @Autowired
    private OrderConfigService orderConfigService;


    @Override
    public void check(CreatingOrder order) {
        for (MainOrder mainOrder : order.getMainOrders()) {
            SellerTradeConfig conf = orderConfigService.getSellerConfig(mainOrder.getSeller().getSellerId());
            if (MapUtils.isEmpty(conf.getExtendConfigs())) {
                continue;
            }
            for (SubOrder subOrder : mainOrder.getSubOrders()) {
                // 赠品、奖品 价格不校验
                if (isGiftItem(subOrder, order)) {
                    continue;
                }
                if (!check(subOrder, conf)) {
                    log.error("create order failed by PriceRisk, order: {}", JSON.toJSONString(order));
                    throw new GmallException(ExtOrderErrorCode.REFUSE_ORDER_PRICE_RISK);
                }
            }
        }
    }

    private boolean check(SubOrder subOrder, SellerTradeConfig conf) {
        String code = getRuleCode(subOrder);
        long originAmt = subOrder.getOrderPrice().getItemOriginAmt();
        long promotionAmt = subOrder.getOrderPrice().getOrderPromotionAmt();
        int qty = subOrder.getOrderQty();

        // 最低折扣
        String minDiscountPercent = conf.getExtendConfigs().get(String.format(KEY_MIN_DISCOUNT_PERCENT, code));
        if (StringUtils.isNotBlank(minDiscountPercent)) {
            long value = Long.parseLong(minDiscountPercent);
            if (100L * promotionAmt / originAmt < value) {
                return false;
            }
        }

        // 最低单价
        String minUnitPrice = conf.getExtendConfigs().get(String.format(KEY_MIN_UNIT_PRICE, code));
        if (StringUtils.isNotBlank(minUnitPrice)) {
            long value = Long.parseLong(minUnitPrice);
            if (promotionAmt < value * qty) {
                return false;
            }
        }

        return true;
    }

    private boolean isGiftItem(SubOrder subOrder, CreatingOrder order) {
        // 满赠赠品
        if (manzengGiftService.isGiftOrder(order, subOrder)) {
            return true;
        }
        // 抽奖奖品
        if (subOrder.getPromotions() != null &&
                !CollectionUtils.isEmpty(subOrder.getPromotions().getItemDivideDetails())) {
            for (ItemDivideDetail div : subOrder.getPromotions().getItemDivideDetails()) {
                if (div != null && AssetsType.AWARD.getCode().equals(div.getAssetType())) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getRuleCode(SubOrder subOrder) {
        if (subOrder.getPromotions() != null &&
                !CollectionUtils.isEmpty(subOrder.getPromotions().getItemDivideDetails())) {
            for (ItemDivideDetail div : subOrder.getPromotions().getItemDivideDetails()) {
                if (div != null && CODE_MIAOSHA.equals(div.getToolCode())) {
                    return CODE_MIAOSHA;
                }
            }
        }
        return CODE_DEFAULT;
    }
}
