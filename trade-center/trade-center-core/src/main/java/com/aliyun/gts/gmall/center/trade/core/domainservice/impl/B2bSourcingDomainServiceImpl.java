package com.aliyun.gts.gmall.center.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.center.trade.common.constants.OrderFeatureKey;
import com.aliyun.gts.gmall.center.trade.core.constants.ExtOrderErrorCode;
import com.aliyun.gts.gmall.center.trade.core.domainservice.B2bSourcingDomainService;
import com.aliyun.gts.gmall.center.trade.domain.entity.b2b.SourcingBillInfo;
import com.aliyun.gts.gmall.center.trade.domain.entity.b2b.SourcingBillInfo.SourcingItem;
import com.aliyun.gts.gmall.center.trade.domain.entity.b2b.SourcingBillOrder;
import com.aliyun.gts.gmall.center.trade.domain.entity.b2b.SourcingBillOrder.OrderInfo;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.B2bSourcingRepository;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.middleware.api.cache.lock.DistributedLock;
import com.aliyun.gts.gmall.platform.trade.core.convertor.PromotionConverter;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.OrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.OrderPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.SellerPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class B2bSourcingDomainServiceImpl implements B2bSourcingDomainService {
    // 前端参数
    private static final String PARAM_SOURCING_BILL_ID = "pricingBillId";

    // 订单自定义extra
    private static final String KEY_SOURCING_BILL = "b2b_sourcingBill";
    private static final String KEY_SUB_DETAIL = "b2b_sourcingBillDetail";

    // redis锁
    private static final String LOCK_KEY_PREFIX = "b2b_sourcingBill_";

    @Autowired
    private B2bSourcingRepository b2bSourcingRepository;

    @Autowired
    private PromotionConverter promotionConverter;

    @Autowired
    private CacheManager tradeCacheManager;

    @Value("${trade.b2b.sourcing.maxLockTime:10000}")
    private Long maxLockTime;
    @Value("${trade.b2b.sourcing.maxLockTime:200}")
    private Long maxWaitTime;

    @Override
    public boolean isSourcing(CreatingOrder order) {
        // 已经判断过
        if (order.getExtra(KEY_SOURCING_BILL) != null) {
            return true;
        }
        String billId = getParamBillId(order);
        if (StringUtils.isBlank(billId)) {
            return false;
        }

        // 查询
        SourcingBillInfo billInfo = b2bSourcingRepository.querySourcingBill(billId);
        checkToOrder(billInfo, order.getCustomer().getCustId());

        // 匹配order
        List<OrderInfo> orders = match(billInfo, order);
        SourcingBillOrder bo = new SourcingBillOrder();
        bo.setBill(billInfo);
        bo.setOrders(orders);
        order.putExtra(KEY_SOURCING_BILL, bo);
        return true;
    }

    // 前端传的 billId
    private String getParamBillId(CreatingOrder order) {
        if (order.getParams() == null) {
            return null;
        }
        return Optional.ofNullable(order.getParams().get(PARAM_SOURCING_BILL_ID))
                .map(String::valueOf).orElse(null);
    }

    private List<OrderInfo> match(SourcingBillInfo billInfo, CreatingOrder order) {
        // skuId 唯一
        Map<Long, SourcingItem> skuMap = billInfo.getItems().stream()
                .collect(Collectors.toMap(SourcingItem::getSkuId, Function.identity()));

        List<OrderInfo> list = new ArrayList<>();
        for (MainOrder mainOrder : order.getMainOrders()) {
            for (SubOrder subOrder : mainOrder.getSubOrders()) {
                SourcingItem item = skuMap.get(subOrder.getItemSku().getSkuId());
                checkToOrder(item, subOrder);

                subOrder.putExtra(KEY_SUB_DETAIL, item);
                OrderInfo result = new OrderInfo();
                result.setItem(item);
                result.setSubOrder(subOrder);
                list.add(result);
            }
        }
        return list;
    }

    private void checkToOrder(SourcingBillInfo billInfo, Long custId) {
        if (billInfo == null || !billInfo.isWaitOrder()) {
            throw new GmallException(ExtOrderErrorCode.B2B_SOURCING_BILL_NOT_ORD);
        }
        if (!Objects.equals(billInfo.getCustId(), custId)) {
            throw new GmallException(ExtOrderErrorCode.B2B_SOURCING_BILL_NOT_ORD);
        }
    }

    private void checkToOrder(SourcingItem item, SubOrder subOrder) {
        if (item == null) {
            throw new GmallException(ExtOrderErrorCode.B2B_SOURCING_BILL_ITEM_ILLEGAL);
        }
        if (!Objects.equals(subOrder.getOrderQty(), item.getAwardNum())) {
            throw new GmallException(ExtOrderErrorCode.B2B_SOURCING_BILL_QTY_ILLEGAL);
        }
        if (item.isOrdered()) {
            throw new GmallException(ExtOrderErrorCode.B2B_SOURCING_BILL_DUP_ORD);
        }
        long now = System.currentTimeMillis();
        if (item.getPriceStartTime() != null && item.getPriceStartTime().getTime() > now) {
            throw new GmallException(ExtOrderErrorCode.B2B_SOURCING_PRICE_BEFORE_START);
        }
        if (item.getPriceEndTime() != null && item.getPriceEndTime().getTime() < now) {
            throw new GmallException(ExtOrderErrorCode.B2B_SOURCING_PRICE_AFTER_END);
        }
    }

    @Override
    public OrderPromotion getSourcingPromotion(CreatingOrder order) {
        SourcingBillOrder bo = (SourcingBillOrder) order.getExtra(KEY_SOURCING_BILL);
        Map<Long, SourcingItem> skuMap = bo.getOrders().stream().map(OrderInfo::getItem)
                .collect(Collectors.toMap(SourcingItem::getSkuId, Function.identity()));
        OrderPromotion query = promotionConverter.orderToPromotionQuery(order);
        long totalProPrice = 0L;
        List<SellerPromotion> sellerResult = Lists.newArrayList();
        for (SellerPromotion seller : query.getSellers()) {
            long sellerProPrice = 0L;
            List<ItemPromotion> itemResult = Lists.newArrayList();
            for (ItemPromotion item : seller.getItems()) {
                SourcingItem sItem = skuMap.get(item.getItemSkuId().getSkuId());
                ItemPromotion ip = new ItemPromotion();
                BeanUtils.copyProperties(item, ip);
                // 营销一口价(单价)
                ip.setItemPrice(NumUtils.getNullZero(sItem.getUnitPrice()));
                // 一口价优惠名称
                ip.setItemPriceName(null);
                // 营销返回的最终分摊价格(非单价)
                ip.setPromotionPrice(NumUtils.getNullZero(sItem.getTotalAmt())
                        - NumUtils.getNullZero(sItem.getFreightAmt()));
                // IC原价(单价)
                ip.setOriginPrice(NumUtils.getNullZero(sItem.getUnitPrice()));
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
    public void fillFreight(CreatingOrder order) {
        long sumAll = 0L;
        for (MainOrder mainOrder : order.getMainOrders()) {
            long sumMain = 0L;
            for (SubOrder subOrder : mainOrder.getSubOrders()) {
                SourcingItem item = (SourcingItem) subOrder.getExtra(KEY_SUB_DETAIL);
                long freightAmt = NumUtils.getNullZero(item.getFreightAmt());
                subOrder.getOrderPrice().setFreightAmt(0L); // 运费不分摊到子单
                sumMain += freightAmt;
            }
            mainOrder.getOrderPrice().setFreightAmt(sumMain);
            OrderPrice freightPrice = new OrderPrice();
            freightPrice.setFreightAmt(sumMain);
            mainOrder.orderAttr().setFreightPrice(freightPrice);
            sumAll += sumMain;
        }
        order.getOrderPrice().setFreightAmt(sumAll);
    }

    @Override
    public void getOrderFeature(CreatingOrder order, MainOrder main, Map<String, String> map) {
        SourcingBillOrder bo = (SourcingBillOrder) order.getExtra(KEY_SOURCING_BILL);
        map.put(OrderFeatureKey.B2B_SOURCING_BILL_ID, bo.getBill().getBillId());
    }

    @Override
    public void getOrderFeature(CreatingOrder order, MainOrder main, SubOrder sub, Map<String, String> map) {
        SourcingItem item = (SourcingItem) sub.getExtra(KEY_SUB_DETAIL);
        map.put(OrderFeatureKey.B2B_SOURCING_DETAIL_ID, item.getDetailId());
    }

    @Override
    public void beginCreateOrder(CreatingOrder order) {
        String billId = getParamBillId(order);
        if (StringUtils.isBlank(billId)) {
            return;
        }
        lock(billId);
    }

    @Override
    public void endCreateOrder(CreatingOrder order, boolean success) {
//        String billId = getParamBillId(order);
//        if (StringUtils.isBlank(billId)) {
//            return;
//        }
//        SourcingBillOrder bo = (SourcingBillOrder) order.getExtra(KEY_SOURCING_BILL);
//        if (success && bo != null) {
//            try {
//                b2bSourcingRepository.saveOrder(bo);
//            } catch (Exception e) {
//                log.error("", e); // 后面订单创建消息中补偿, 这里忽略异常
//            }
//        }
//        unlock(billId);
    }

    @Override
    public boolean isSourcing(MainOrder mainOrder) {
        String billId = mainOrder.orderAttr().extras().get(OrderFeatureKey.B2B_SOURCING_BILL_ID);
        return StringUtils.isNotBlank(billId);
    }

    @Override
    public void consumeOrderCreated(MainOrder mainOrder) {
        String billId = mainOrder.orderAttr().extras().get(OrderFeatureKey.B2B_SOURCING_BILL_ID);
        if (StringUtils.isBlank(billId)) {
            return;
        }
        lock(billId);
        try {
            SourcingBillInfo bill = b2bSourcingRepository.querySourcingBill(billId);
            Map<String, SourcingItem> dMap = bill.getItems().stream()
                    .collect(Collectors.toMap(SourcingItem::getDetailId, Function.identity()));
            List<OrderInfo> billOrders = new ArrayList<>();
            for (SubOrder subOrder : mainOrder.getSubOrders()) {
                String detailId = subOrder.orderAttr().extras().get(OrderFeatureKey.B2B_SOURCING_DETAIL_ID);
                if (StringUtils.isBlank(detailId)) {
                    continue;
                }
                SourcingItem sItem = dMap.get(detailId);
                if (sItem == null || sItem.isOrdered()) {
                    continue;
                }
                OrderInfo o = new OrderInfo();
                o.setSubOrder(subOrder);
                o.setItem(sItem);
                billOrders.add(o);
            }
            SourcingBillOrder bo = new SourcingBillOrder();
            bo.setBill(bill);
            bo.setOrders(billOrders);
            b2bSourcingRepository.saveOrder(bo);
        } finally {
            unlock(billId);
        }
    }

    private void lock(String billId) {
        String key = LOCK_KEY_PREFIX + billId;
        DistributedLock lock = tradeCacheManager.getLock(key);
        boolean locked;
        try {
            locked = lock.tryLock(maxWaitTime, maxLockTime);
        } catch (InterruptedException e) {
            throw new GmallException(ExtOrderErrorCode.B2B_SOURCING_CONCURRENT_ORDER);
        }
        if (!locked) {
            throw new GmallException(ExtOrderErrorCode.B2B_SOURCING_CONCURRENT_ORDER);
        }
    }

    private void unlock(String billId) {
        String key = LOCK_KEY_PREFIX + billId;
        DistributedLock lock = tradeCacheManager.getLock(key);
        lock.unLock();
    }
}
