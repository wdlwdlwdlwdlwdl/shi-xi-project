package com.aliyun.gts.gmall.center.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.center.trade.core.constants.ExtOrderErrorCode;
import com.aliyun.gts.gmall.center.trade.core.domainservice.FixedPointPriceService;
import com.aliyun.gts.gmall.center.trade.domain.entity.point.FixedPointItem;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.FixedPointItemRepository;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.TradeExtendKeyConstants;
import com.aliyun.gts.gmall.platform.trade.core.convertor.PromotionConverter;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.point.PointExchange;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.OrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.OrderPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.SellerPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PointExchangeRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PointRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FixedPointPriceServiceImpl implements FixedPointPriceService {
    private static final String FIXED_POINT_FLAG = "pointType";
    private static final String FIXED_POINT_FLAG_VALUE = "point";
    private static final String KEY_FIXED_CONF = "fixedPointConf";

    @Autowired
    private PromotionConverter promotionConverter;
    @Autowired
    private PointExchangeRepository pointExchangeRepository;
    @Autowired
    private FixedPointItemRepository fixedPointItemRepository;
    @Autowired
    private PointRepository pointRepository;

    @Override
    public boolean isFixedOrder(CreatingOrder order) {
        // 已经判断过
        if (order.getExtra(KEY_FIXED_CONF) != null) {
            return true;
        }

        if (order.getParams() == null) {
            return false;
        }
        String flagValue = String.valueOf(order.getParams().get(FIXED_POINT_FLAG));
        if (!FIXED_POINT_FLAG_VALUE.equals(flagValue)) {
            return false;
        }
        // 积分商品不允许购物车下单
        if (Boolean.TRUE.equals(order.getIsFromCart())) {
            return false;
        }
        // 不允许合并下单
        int subOrderSize = 0;
        for (MainOrder mainOrder : order.getMainOrders()) {
            subOrderSize += mainOrder.getSubOrders().size();
        }
        if (subOrderSize > 1) {
            return false;
        }
        // 积分退换配置
        MainOrder singleMain = order.getMainOrders().get(0);
        SubOrder singleOrder = singleMain.getSubOrders().get(0);
        FixedPointItem fixed = fixedPointItemRepository.queryEnabledItem(singleMain, singleOrder);
        if (fixed == null) {
            return false;
        }
        order.putExtra(KEY_FIXED_CONF, fixed);
        return true;
    }

    @Override
    public OrderPromotion getFixedOrderPromotion(CreatingOrder order) {
        OrderPromotion query = promotionConverter.orderToPromotionQuery(order);
        PointExchange ex = pointExchangeRepository.getExchangeRate();
        FixedPointItem fixed = (FixedPointItem) order.getExtra(KEY_FIXED_CONF);

        long totalProPrice = 0L;
        List<SellerPromotion> sellerResult = Lists.newArrayList();
        for (SellerPromotion seller : query.getSellers()) {
            long sellerProPrice = 0L;
            List<ItemPromotion> itemResult = Lists.newArrayList();
            for (ItemPromotion item : seller.getItems()) {
                Long pointCount = fixed.getPointCount();
                long exchangePointAmt = ex.exCountToAmt(pointCount);
                long exchangeRealAmt = fixed.getRealPrice();
                ItemPromotion ip = new ItemPromotion();
                BeanUtils.copyProperties(item, ip);
                // 营销一口价(单价)
                ip.setItemPrice(exchangePointAmt + exchangeRealAmt);
                // 一口价优惠名称
                ip.setItemPriceName(null);
                // 营销返回的最终分摊价格(非单价)
                ip.setPromotionPrice((exchangePointAmt + exchangeRealAmt) * ip.getSkuQty());
                // IC原价(单价)
                ip.setOriginPrice(exchangePointAmt + exchangeRealAmt);
                // 营销返回的活动分摊明细
                ip.setItemDivideDetails(Lists.newArrayList());
                itemResult.add(ip);
                sellerProPrice += ip.getPromotionPrice();
            }
            SellerPromotion sp = new SellerPromotion();
            sp.setSellerId(seller.getSellerId());
            sp.setItems(itemResult);
            sp.setPromotionPrice(sellerProPrice);
            sp.setOptions(Lists.newArrayList());
            totalProPrice += sellerProPrice;
            sellerResult.add(sp);
        }
        OrderPromotion result = new OrderPromotion();
        result.setOptions(Lists.newArrayList());
        result.setPromotionSource(null);
        result.setDeductUserAssets(Maps.newHashMap());
        result.setSellers(sellerResult);
        result.setPromotionPrice(totalProPrice);
        return result;
    }

    @Override
    public void fillOrderPoints(CreatingOrder order) {
        PointExchange ex = pointExchangeRepository.getExchangeRate();
        FixedPointItem fixed = (FixedPointItem) order.getExtra(KEY_FIXED_CONF);
        OrderPrice p = order.getOrderPrice();

        // 用户积分金额
        Long custId = order.getCustomer().getCustId();
        long custPointCount = NumUtils.getNullZero(pointRepository.getUserPoint(custId));
        if (custPointCount < 0) {
            custPointCount = 0;
        }
        long custPointAmt = ex.exCountToAmt(custPointCount);

        // 订单总积分数量
        long orderTotalPointCount = 0;
        // 订单总积分金额
        long orderTotalPointAmt = 0;
        // 订单总支付金额
        long orderTotalRealAmt = 0;

        for (MainOrder main : order.getMainOrders()) {
            long mainPointCount = 0;
            long mainPointAmt = 0;
            long mainRealAmt = 0;
            for (SubOrder sub : main.getSubOrders()) {
                OrderPrice sp = sub.getOrderPrice();
                long subPointCount = fixed.getPointCount() * sub.getOrderQty();
                long subRealAmt = fixed.getRealPrice() * sub.getOrderQty();
                // 积分数量
                sp.setPointCount(subPointCount);
                // 积分金额
                long subPointAmt = ex.exCountToAmt(subPointCount);
                sp.setPointAmt(subPointAmt);
                // 现金金额
                sp.setOrderRealAmt(subRealAmt);
                // 主订单 积分数量、积分金额、实付金额
                mainPointCount += subPointCount;
                mainPointAmt += subPointAmt;
                mainRealAmt += subRealAmt;
            }
            OrderPrice mp = main.getOrderPrice();
            // 主订单 积分数量、积分金额、实付金额
            mp.setPointCount(mainPointCount);
            mp.setPointAmt(mainPointAmt);
            mp.setOrderRealAmt(mainRealAmt);
            if (mainPointCount > 0) {
                // 用了积分的 才记录积分汇率
                main.orderAttr().setPointExchange(ex);
            }
            // 订单 积分数量、积分金额、实付金额
            orderTotalPointCount += mainPointCount;
            orderTotalPointAmt += mainPointAmt;
            orderTotalRealAmt += mainRealAmt;
        }

        if (custPointAmt < orderTotalPointAmt) {
            throw new GmallException(ExtOrderErrorCode.FIXED_POINT_NOT_ENOUGH);
        }

        p.setPointCount(orderTotalPointCount);
        p.setPointAmt(orderTotalPointAmt);
        p.setOrderRealAmt(orderTotalRealAmt);

        // 最大可用积分数量, 返回给C端用
        p.putExtend(TradeExtendKeyConstants.MAX_AVAILABLE_POINT,
                String.valueOf(Math.min(orderTotalPointCount, custPointCount)));
    }

    @Override
    public void fillFreight(CreatingOrder order) {
        // 积分退款无运费
        order.getOrderPrice().setFreightAmt(0L);
        for (MainOrder main : order.getMainOrders()) {
            main.getOrderPrice().setFreightAmt(0L);
            for (SubOrder sub : main.getSubOrders()) {
                sub.getOrderPrice().setFreightAmt(0L);
            }
        }
    }
}
