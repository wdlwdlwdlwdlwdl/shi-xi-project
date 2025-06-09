package com.aliyun.gts.gmall.center.trade.core.domainservice.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.aliyun.gts.gmall.center.pay.api.enums.PayChannelEnum;
import com.aliyun.gts.gmall.center.trade.common.constants.OrderFeatureKey;
import com.aliyun.gts.gmall.center.trade.common.constants.OrderTagPrefix;
import com.aliyun.gts.gmall.center.trade.core.domainservice.ManzengGiftService;
import com.aliyun.gts.gmall.center.trade.core.util.ManzengBuildUtils;
import com.aliyun.gts.gmall.center.trade.domain.entity.promotion.ManzengGift;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.GiftDTO;
import com.aliyun.gts.gmall.platform.promotion.common.constant.PromotionToolCodes;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.OrderPayRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.message.OrderMessageDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.OrderPayRpcResp;
import com.aliyun.gts.gmall.platform.trade.api.facade.pay.OrderPayWriteFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.BizCodeEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderChannelEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.util.OrderIdUtils;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ItemService;
import com.aliyun.gts.gmall.platform.trade.core.task.AbstractScheduledTask.ExecuteResult;
import com.aliyun.gts.gmall.platform.trade.core.task.biz.CloseUnpaidOrderTask;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.ItemPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.OrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemDivideDetail;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
import com.aliyun.gts.gmall.platform.trade.domain.repository.ItemRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.CommUtils;
import com.aliyun.gts.gmall.platform.trade.domain.util.ErrorUtils;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ManzengGiftServiceImpl implements ManzengGiftService {

    private static final String SUB_GIFT_INFO = "MZ_SUB_GIFT_INFO";
    private static final String SUB_IS_GIFT = "MZ_SUB_IS_GIFT";
    private static final String CREATE_HAS_GIFT = "MZ_CREATE_HAS_GIFT";

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderPayWriteFacade orderPayWriteFacade;

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private CloseUnpaidOrderTask closeUnpaidOrderTask;

    /**
     * 下单处理钩子, 营销查询之后, 如有赠品则添加赠品
     */
    @Override
    public void onOrderCreate(CreatingOrder order) {
        // 获取营销赠品
        List<GiftInfo> gifts = getGifts(order);
        if (CollectionUtils.isEmpty(gifts)) {
            return;
        }
        Multimap<Long, GiftInfo> orderGiftMap = ArrayListMultimap.create();
        // 填赠品订单
        for (GiftInfo gift : gifts) {
            for (Long orderId : gift.refOrderIds) {
                orderGiftMap.put(orderId, gift);
            }
            MainOrder giftOrder = generateGiftOrder(gift.itemSku, gift.quantity, order);
            order.getMainOrders().add(giftOrder);
            gift.giftOrderId = getTempOrderId(order.getMainOrders().size() - 1, 0);
            ManzengGift feature = new ManzengGift();
            feature.setRefOrders(gift.refOrderIds);
            feature.setTempId(true);
            feature.setCampId(gift.campId);
            giftOrder.getSubOrders().get(0).putExtra(SUB_GIFT_INFO, Arrays.asList(feature));
            giftOrder.getSubOrders().get(0).putExtra(SUB_IS_GIFT, true);
        }
        // 主商品订单打标
        for (Long tempId : orderGiftMap.keySet()) {
            Collection<GiftInfo> orderGifts = orderGiftMap.get(tempId);
            List<ManzengGift> featureArr = new ArrayList<>();
            for (GiftInfo gift : orderGifts) {
                ManzengGift feature = new ManzengGift();
                feature.setGiftOrder(gift.giftOrderId);
                feature.setRefOrders(gift.refOrderIds);
                feature.setCampId(gift.campId);
                feature.setTempId(true);
                featureArr.add(feature);
            }
            SubOrder subOrder = getFromTempOrderId(tempId, order);
            subOrder.putExtra(SUB_GIFT_INFO, featureArr);
        }
        order.putExtra(CREATE_HAS_GIFT, true);
    }

    @Override
    public void withoutGiftOrder(CreatingOrder order, Consumer<CreatingOrder> withoutGiftOrder) {
        // 无赠品
        if (!hasGift(order)) {
            withoutGiftOrder.accept(order);
            return;
        }

        List<MainOrder> tempMainList = new ArrayList<>();
        List<MainOrder> allMainList = order.getMainOrders();
        Map<Integer, List<SubOrder>> allSubMap = new HashMap<>();

        int index = 0;
        for (MainOrder main : allMainList) {
            allSubMap.put(index++, main.getSubOrders());
            List<SubOrder> tempSubList = new ArrayList<>();

            for (SubOrder sub : main.getSubOrders()) {
                if (!isGiftOrder(sub)) {
                    tempSubList.add(sub);
                }
            }
            if (!tempSubList.isEmpty()) {
                main.setSubOrders(tempSubList);
                tempMainList.add(main);
            }
        }
        order.setMainOrders(tempMainList);
        try {
            withoutGiftOrder.accept(order);
        } finally {
            index = 0;
            for (MainOrder main : allMainList) {
                main.setSubOrders(allSubMap.get(index++));
            }
            order.setMainOrders(allMainList);
        }
    }

    @Override
    public boolean isGiftOrder(CreatingOrder order, SubOrder sub) {
        return hasGift(order) && isGiftOrder(sub);
    }

    @Override
    public void beforeSave(CreatingOrder order) {
        // 无赠品
        if (!hasGift(order)) {
            return;
        }
        for (MainOrder mainOrder : order.getMainOrders()) {
            for (SubOrder subOrder : mainOrder.getSubOrders()) {
                List<ManzengGift> gifts = (List) subOrder.getExtra(SUB_GIFT_INFO);
                if (gifts == null) {
                    continue;
                }
                for (ManzengGift gift : gifts) {
                    if (!gift.isTempId()) {
                        continue;
                    }
                    if (gift.getGiftOrder() != null) {
                        SubOrder giftOrder = getFromTempOrderId(gift.getGiftOrder(), order);
                        gift.setGiftOrder(giftOrder.getOrderId());
                    }
                    List<Long> refOrders = new ArrayList<>();
                    for (Long tempId : gift.getRefOrders()) {
                        SubOrder refOrder = getFromTempOrderId(tempId, order);
                        refOrders.add(refOrder.getOrderId());
                    }
                    gift.setRefOrders(refOrders);
                    gift.setTempId(null);
                    gift.setCampId(gift.getCampId());
                }
                if (isGiftOrder(subOrder)) {
                    subOrder.orderAttr().putExtend(OrderFeatureKey.MANZENG_GIFT, JSON.toJSONString(gifts.get(0)));
                    subOrder.orderAttr().tags().add(OrderTagPrefix.MANZENG_GIFT);
                } else {
                    subOrder.orderAttr().putExtend(OrderFeatureKey.MANZENG_ORDER, JSON.toJSONString(gifts));
                    subOrder.orderAttr().tags().add(OrderTagPrefix.MANZENG_ORDER);
                }
            }
        }
    }

    @Override
    public void onOrderPaid(MainOrder mainOrder) {
        Map<Long, ManzengGift> allGift = new HashMap<>();
        for (SubOrder subOrder : mainOrder.getSubOrders()) {
            String value = subOrder.orderAttr().getExtend(OrderFeatureKey.MANZENG_ORDER);
            if (StringUtils.isBlank(value)) {
                continue;
            }

            List<ManzengGift> list = JSON.parseObject(value,
                    new TypeReference<List<ManzengGift>>() {
                    });
            for (ManzengGift gift : list) {
                // 去个重
                allGift.put(gift.getGiftOrder(), gift);
            }
        }
        for (ManzengGift gift : allGift.values()) {
            checkSendGift(gift, mainOrder);
        }
    }

    @Override
    public void onOrderCancel(OrderMessageDTO message) {
        if (message.getOrderFeatures() == null) {
            return;
        }
        String value = message.getOrderFeatures().get(OrderFeatureKey.MANZENG_ORDER);
        if (StringUtils.isBlank(value)) {
            return;
        }
        List<ManzengGift> list = JSON.parseObject(value, new TypeReference<>() {});
        for (ManzengGift gift : list) {
            autoCloseGift(gift.getGiftOrder());
        }
    }

    /**
     * 获取满赠赠品信息
     * @param order
     * @return
     */
    private List<GiftInfo> getGifts(CreatingOrder order) {
        final String EXTRA_KEY_REWARDS = ManzengBuildUtils.EXTRA_KEY_REWARDS;
        final int GIFT_TYPE_ITEM = ManzengBuildUtils.GIFT_TYPE_ITEM;
        Multimap<Long, GiftDTO> campGiftMap = ArrayListMultimap.create();
        Multimap<Long, Long> campSkuMap = HashMultimap.create();
        for (MainOrder mainOrder : order.getMainOrders()) {
            for (SubOrder subOrder : mainOrder.getSubOrders()) {
                ItemPromotion prom = subOrder.getPromotions();
                if (prom == null || CollectionUtils.isEmpty(prom.getItemDivideDetails())) {
                    continue;
                }
                for (ItemDivideDetail div : prom.getItemDivideDetails()) {
                    if (!PromotionToolCodes.MANZENG.equals(div.getToolCode())
                        || div.getExtras() == null) {
                        continue;
                    }
                    String rewardsValue = (String) div.getExtras().get(EXTRA_KEY_REWARDS);
                    if (StringUtils.isBlank(rewardsValue)) {
                        continue;
                    }
                    List<GiftDTO> gifts = JSON.parseObject(rewardsValue, new TypeReference<>() {});
                    for (GiftDTO gift : gifts) {
                        if (gift.getType() == null || gift.getType().intValue() != GIFT_TYPE_ITEM) {
                            continue;
                        }
                        // 商品赠品
                        campGiftMap.put(div.getCampId(), gift);
                        campSkuMap.put(div.getCampId(), subOrder.getItemSku().getSkuId());
                    }
                }
            }
        }
        List<GiftInfo> result = new ArrayList<>();
        for (Long campId : campGiftMap.keySet()) {
            Collection<GiftDTO> gifts = campGiftMap.get(campId);
            Collection<Long> skuIds = campSkuMap.get(campId);
            List<Long> refOrderIds = skuIds.stream()
                .map(skuId -> findOrder(skuId, order))
                .collect(Collectors.toList());
            for (GiftDTO gift : gifts) {
                ItemSku giftItem = getGiftItem(gift.getItemId(), gift.getUnitNum());
                if (giftItem == null) {
                    continue;
                }
                GiftInfo g = new GiftInfo();
                g.itemSku = giftItem;
                g.quantity = gift.getUnitNum();
                g.refOrderIds = refOrderIds;
                g.campId = campId;
                result.add(g);
            }
        }
        return result;
    }

    /**
     * 解析礼物商品信息
     * @param itemId
     * @param qty
     * @return
     */
    private ItemSku getGiftItem(long itemId, int qty) {
        List<ItemSku> itemSkus = itemRepository.queryItemsByItemId(itemId);
        if (CollectionUtils.isEmpty(itemSkus)) {
            return null;
        }
        Collection<ItemSku> withSeller = itemService.fillSeller(itemSkus).values();
        List<ItemSku> enables = withSeller.stream()
            .filter(ItemSku::isEnabled)
//            .filter(sku -> sku.getSkuQty().intValue() >= qty)
            .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(enables)) {
            return null;
        }
//        // 按数量从多到少
//        if (enables.size() > 1) {
//            enables.sort(Comparator.comparing(ItemSku::getSkuQty).reversed());
//        }
        return enables.get(0);
    }


    private void autoCloseGift(Long giftOrder) {
        Long mainId = OrderIdUtils.getPrimaryOrderIdByOrderId(giftOrder);
        ScheduleTask t = new ScheduleTask();
        t.setPrimaryOrderId(mainId);
        try {
            ExecuteResult result = closeUnpaidOrderTask.execute(t);
            if (!result.isSuccess()) {
                log.error("fail close gift order: {}", result.getMessage());
                throw new GmallException(CommonErrorCode.SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("fail close gift order", e);
            throw new GmallException(CommonErrorCode.SERVER_ERROR);
        }
    }

    private void checkSendGift(ManzengGift gift, MainOrder mainOrder) {
        Set<Long> refMainIds = new HashSet<>();
        for (Long refSubId : gift.getRefOrders()) {
            Long refMainId = OrderIdUtils.getPrimaryOrderIdByOrderId(refSubId);
            refMainIds.add(refMainId);
        }
        refMainIds.remove(mainOrder.getPrimaryOrderId());
        List<MainOrder> mainOrders = orderQueryAbility.batchGetMainOrder(new ArrayList<>(refMainIds));
        mainOrders.add(mainOrder);
        Map<Long, MainOrder> mainOrderMap = CommUtils.toMap(mainOrders, MainOrder::getPrimaryOrderId);
        boolean hasNoPay = false;
        for (Long refSubId : gift.getRefOrders()) {
            Long refMainId = OrderIdUtils.getPrimaryOrderIdByOrderId(refSubId);
            MainOrder refMainOrder = mainOrderMap.get(refMainId);
            SubOrder refSubOrder = refMainOrder.getSubOrders().stream()
                .filter(x -> Objects.equals(x.getOrderId(), refSubId))
                .findFirst().get();
            if (!isGiftOrderPaid(refMainOrder.getPrimaryOrderStatus()) ||
                !isGiftOrderPaid(refSubOrder.getOrderStatus())) {
                log.info("gift order not paid, {}", refSubId);
                hasNoPay = true;
            }
        }
        if (!hasNoPay) {
            autoPayGift(gift.getGiftOrder());
        }
    }

    private void autoPayGift(Long giftOrderId) {
        Long mainOrderId = OrderIdUtils.getPrimaryOrderIdByOrderId(giftOrderId);
        MainOrder mainOrder = orderQueryAbility.getMainOrder(mainOrderId);
        if (!OrderStatusEnum.ORDER_WAIT_PAY.getCode().equals(mainOrder.getPrimaryOrderStatus())) {
            log.warn("gift not wait pay, {}", giftOrderId);
            return;
        }
        OrderPayRpcReq req = new OrderPayRpcReq();
        req.setPrimaryOrderId(mainOrderId);
        req.setCustId(mainOrder.getCustomer().getCustId());
        req.setOrderChannel(OrderChannelEnum.H5.getCode());
        req.setPayChannel(PayChannelEnum.ZERO.getCode());
        req.setTotalOrderFee(0L);
        req.setRealPayFee(0L);
        req.setPointCount(0L);
        RpcResponse<OrderPayRpcResp> resp = orderPayWriteFacade.toPay(req);
        if (!resp.isSuccess()) {
            throw new GmallException(ErrorUtils.mapCode(resp.getFail()), null);
        }
    }

    private boolean isGiftOrderPaid(Integer orderStatus) {
        return !OrderStatusEnum.ORDER_WAIT_PAY.getCode().equals(orderStatus)
            && !OrderStatusEnum.ORDER_SELLER_CLOSE.getCode().equals(orderStatus)
            && !OrderStatusEnum.SYSTEM_CLOSE.getCode().equals(orderStatus)
            && !OrderStatusEnum.ORDER_BUYER_CANCEL.getCode().equals(orderStatus)
            && !OrderStatusEnum.REVERSAL_SUCCESS.getCode().equals(orderStatus);
    }

    private MainOrder generateGiftOrder(ItemSku itemSku, int quantity, CreatingOrder order) {
        ItemPrice itemPrice = new ItemPrice();
        itemPrice.setOriginPrice(itemSku.getItemPrice().getOriginPrice());
        itemPrice.setItemPrice(0L);
        OrderPrice zero = new OrderPrice();
        zero.setOrderRealAmt(0L);
        zero.setPointAmt(0L);
        zero.setFreightAmt(0L);
        zero.setOrderPromotionAmt(0L);
        zero.setItemOriginAmt(0L);
        zero.setItemPrice(itemPrice);
        MainOrder main = new MainOrder();
        main.setCustomer(order.getCustomer());
        main.setReceiver(order.getReceiver());
        main.setOrderType(OrderTypeEnum.PHYSICAL_GOODS.getCode());
        main.setBizCodes(Arrays.asList(BizCodeEnum.NORMAL_TRADE.getCode()));
        main.setSeller(itemSku.getSeller());
        main.setOrderPrice(zero);
        main.setOrderChannel(order.getOrderChannel());
        SubOrder sub = new SubOrder();
        sub.setItemSku(itemSku);
        sub.setOrderQty(quantity);
        sub.setOrderPrice(zero);
        main.setSubOrders(Arrays.asList(sub));
        return main;
    }

    private boolean isGiftOrder(SubOrder sub) {
        Boolean b = (Boolean) sub.getExtra(SUB_IS_GIFT);
        return b != null && b.booleanValue();
    }

    private boolean hasGift(CreatingOrder order) {
        Boolean b = (Boolean) order.getExtra(CREATE_HAS_GIFT);
        return b != null && b.booleanValue();
    }

    private static long getTempOrderId(int mainOrderIndex, int subOrderIndex) {
        return mainOrderIndex * 1000 + subOrderIndex;
    }

    private static long findOrder(long skuId, CreatingOrder order) {
        int mainOrderIndex = 0;
        int subOrderIndex = 0;
        for (MainOrder mainOrder : order.getMainOrders()) {
            for (SubOrder subOrder : mainOrder.getSubOrders()) {
                if (subOrder.getItemSku().getSkuId().longValue() == skuId) {
                    return getTempOrderId(mainOrderIndex, subOrderIndex);
                }
                subOrderIndex++;
            }
            mainOrderIndex++;
        }
        throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG, I18NMessageUtils.getMessage("gift.info.error"));  //# "赠品信息错误"
    }

    private static SubOrder getFromTempOrderId(long tempOrderId, CreatingOrder order) {
        int mainOrderIndex = (int) (tempOrderId / 1000);
        int subOrderIndex = (int) (tempOrderId % 1000);
        return order.getMainOrders().get(mainOrderIndex).getSubOrders().get(subOrderIndex);
    }

    private static class GiftInfo {
        private ItemSku itemSku;
        private int quantity;
        private Long campId;
        private Long giftOrderId;     // 赠品订单 tempOrderId
        private List<Long> refOrderIds; // 商品订单 tempOrderId
    }

}
