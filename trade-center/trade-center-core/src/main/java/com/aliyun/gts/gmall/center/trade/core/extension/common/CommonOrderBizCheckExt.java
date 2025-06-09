package com.aliyun.gts.gmall.center.trade.core.extension.common;

import com.aliyun.gts.gmall.center.item.common.consts.ItemCenterFeatureConstant;
import com.aliyun.gts.gmall.center.trade.core.constants.ExtOrderErrorCode;
import com.aliyun.gts.gmall.center.trade.core.domainservice.CombineItemService;
import com.aliyun.gts.gmall.center.trade.core.domainservice.PromotionOrderLimitService;
import com.aliyun.gts.gmall.center.trade.core.util.ItemUtils;
import com.aliyun.gts.gmall.center.trade.domain.constant.TradeInnerExtendKeys;
import com.aliyun.gts.gmall.center.trade.domain.entity.promotion.BuyOrdsLimit;
import com.aliyun.gts.gmall.platform.item.common.constant.ItemFeatureConstant;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultOrderBizCheckExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Objects;


/**
 * 1. 子订单(SKU) 数量全局限制
 * 2. 商品限购数量
 * 3. 营销活动限购数量
 */
@Slf4j
public class CommonOrderBizCheckExt extends DefaultOrderBizCheckExt {

    @Value("${trade.center.normal.limitSku:50}")
    private Integer limitSku;

    @Autowired
    private CombineItemService combineItemService;

    @Autowired
    private PromotionOrderLimitService promotionOrderLimitService;
    
    /**
     * 普通订单下单校验
     * @param order
     * @return
     */
    @Override
    public TradeBizResult checkOnConfirmOrder(CreatingOrder order) {
        TradeBizResult tradeBizResult = super.checkOnConfirmOrder(order);
        if (!tradeBizResult.isSuccess()) {
            return tradeBizResult;
        }
//        //组合商品校验
//        r = combineItemService.confirmCheck(order);
//        if (!r.isSuccess()) {
//            return r;
//        }
        return bizCheck(order, false);
    }

    /**
     * 创建订单
     * @param order
     * @return
     */
    @Override
    public TradeBizResult checkOnCreateOrder(CreatingOrder order) {
        TradeBizResult tradeBizResult = super.checkOnCreateOrder(order);
        if (!tradeBizResult.isSuccess()) {
            return tradeBizResult;
        }
        //组合商品校验
        //tradeBizResult = combineItemService.confirmCheck(order);
        //if (!tradeBizResult.isSuccess()) {
        //    return tradeBizResult;
        //}
        return bizCheck(order, true);
    }
    
    /**
     * 业务校验 -- 商品数量等
     * @param order
     * @param create
     * @return
     */
    private TradeBizResult bizCheck(CreatingOrder order, boolean create) {
//        int total = 0;
        if (CollectionUtils.isEmpty(order.getMainOrders())) {
            return TradeBizResult.ok();
        }
        // 遍历，每个订单商品
        for (MainOrder mainOrder : order.getMainOrders()) {
//            total += mainOrder.getSubOrders().size();
            if(Objects.isNull(mainOrder) || CollectionUtils.isEmpty(mainOrder.getSubOrders())) {
                return TradeBizResult.fail(ExtOrderErrorCode.ORDER_QTY_HIGH);
            }
            for (SubOrder subOrder : mainOrder.getSubOrders()) {
                // 商品限购数量校验
//                Integer itemLimit = getItemBuyLimit(subOrder.getItemSku());
//                if (itemLimit != null && itemLimit < subOrder.getOrderQty().intValue()) {
//                    return TradeBizResult.fail(ExtOrderErrorCode.ORDER_QTY_HIGH);
//                }
//                //商品禁售
//                if(isItemDisableBuy(subOrder.getItemSku())){
//                    return TradeBizResult.fail(ExtOrderErrorCode.ORDER_ITEM_DISABLE_BUY);
//                }
                // 营销活动限购数量
                Integer promLimit = getPromotionBuyLimit(subOrder);
                if (Objects.nonNull(promLimit) && promLimit < subOrder.getOrderQty().intValue()) {
                    return TradeBizResult.fail(ExtOrderErrorCode.ORDER_QTY_HIGH);
                }
                // 营销下单单数限制
                TradeBizResult result = checkBuyOrds(subOrder, order, create);
                if (!result.isSuccess()) {
                    return result;
                }
            }
        }
//        // SKU数量校验
//        if (total > limitSku) {
//            return TradeBizResult.fail(ExtOrderErrorCode.ORDER_SKU_COUNT_OUT_LIMIT);
//        }
        return TradeBizResult.ok();
    }

    /**
     * 购买限制check
     * @param sku
     * @return
     */
    private static Integer getItemBuyLimit(ItemSku sku) {
        String value = ItemUtils.getItemFeature(sku, ItemFeatureConstant.BUY_LIMIT);
        if (StringUtils.isEmpty(value) || !StringUtils.isNumeric(value)) {
            return null;
        }
        return Integer.parseInt(value);
    }

    /**
     * 是否禁售
     * @param sku
     * @return
     */
    private static Boolean isItemDisableBuy(ItemSku sku) {
        String value =  ItemUtils.getItemFeature(sku, ItemCenterFeatureConstant.DISABLE_BUY);
        if (ItemCenterFeatureConstant.DISABLE_BUY_TRUE.equals(value)) {
            return true;
        }
        return false;
    }

    /**
     * 营销购买数量上线check
     * @param subOrder
     * @return
     */
    private static Integer getPromotionBuyLimit(SubOrder subOrder) {
        return (Integer) subOrder.getExtra(TradeInnerExtendKeys.PROMOTION_BUY_LIMIT);
    }

    /**
     * 购买数量check
     * @param subOrder
     * @param order
     * @param create
     * @return
     */
    private TradeBizResult checkBuyOrds(SubOrder subOrder, CreatingOrder order, boolean create) {
        BuyOrdsLimit ordsLimit = (BuyOrdsLimit) subOrder.getExtra(TradeInnerExtendKeys.PROMOTION_BUY_ORDS_LIMIT);
        if (Objects.isNull(ordsLimit)) {
            return TradeBizResult.ok();
        }
        ordsLimit.setCustId(order.getCustomer().getCustId());
        if (create) {
            if (!promotionOrderLimitService.checkAndIncrBuyOrds(ordsLimit)) {
                return TradeBizResult.fail(ExtOrderErrorCode.ORDER_QTY_HIGH);
            }
            subOrder.putExtra(TradeInnerExtendKeys.PROMOTION_IS_BUY_ORDS_INCR, true);
        } else {
            if (!promotionOrderLimitService.checkBuyOrds(ordsLimit)) {
                return TradeBizResult.fail(ExtOrderErrorCode.ORDER_QTY_HIGH);
            }
        }
        return TradeBizResult.ok();
    }
}
