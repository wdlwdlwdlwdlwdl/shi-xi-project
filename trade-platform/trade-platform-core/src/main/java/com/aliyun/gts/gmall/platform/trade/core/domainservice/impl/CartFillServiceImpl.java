package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.platform.promotion.common.constant.PromotionToolCodes;
import com.aliyun.gts.gmall.platform.promotion.common.type.AssetsType;
import com.aliyun.gts.gmall.platform.trade.api.constant.PayMode;
import com.aliyun.gts.gmall.platform.trade.api.constant.PayModeCode;
import com.aliyun.gts.gmall.platform.trade.common.constants.CartGroupTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.price.PriceCalcAbility;
import com.aliyun.gts.gmall.platform.trade.core.config.TradeLimitConfiguration;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CartFillService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ItemService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartGroup;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.ItemPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemDivideDetail;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.OrderPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.SellerPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.util.CommUtils;
import com.google.common.collect.LinkedHashMultimap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartFillServiceImpl implements CartFillService {

    @Autowired
    private ItemService itemService;

    @Autowired
    private PriceCalcAbility priceCalcAbility;

    @Autowired
    private TradeLimitConfiguration tradeLimitConfiguration;

    @Override
    public void fillItemInfo(Cart cart) {
        List<CartItem> list = cart.getGroups()
            .stream()
            .flatMap(group -> group.getCartItems().stream())
            .collect(Collectors.toList());

        // 查询商品信息
        List<ItemSkuId> ids = list
            .stream()
            .map(item -> new ItemSkuId(item.getItemId(), item.getSkuId()))
            .collect(Collectors.toList());
        Map<ItemSkuId, ItemSku> itemMap = itemService.queryItemsFromCache(ids);
        // 回填
        for (CartGroup group : cart.getGroups()) {
            for (CartItem cartItem : group.getCartItems()) {
                ItemSku item = itemMap.get(new ItemSkuId(cartItem.getItemId(), cartItem.getSkuId()));
                if (item != null) {
                    cartItem.setItemSku(item);
                    if (item.getSeller() != null) {
                        group.setSellerName(item.getSeller().getSellerName());
                    }
                } else {
                    cartItem.setItemNotFound(true);
                }
            }
        }
    }

    @Override
    public void fillItemPromotions(Cart cart) {
        List<ItemPromotion> proList = priceCalcAbility.queryCartItemPromotions(cart);
        Map<ItemSkuId, ItemPromotion> proMap = CommUtils.toMap(
            proList,
            itemPromotion -> new ItemSkuId(
                itemPromotion.getItemSkuId().getItemId(),
                itemPromotion.getItemSkuId().getSkuId(),
                itemPromotion.getItemSkuId().getSellerId()
            )
        );
        // 回填
        for (CartGroup group : cart.getGroups()) {
            for (CartItem item : group.getCartItems()) {
                ItemPromotion pro = proMap.get(item.getItemSku().getItemSkuId());
                if (pro == null) {
                    continue;
                }
                if (item.getItemSku().getItemPrice() == null) {
                    item.getItemSku().setItemPrice(new ItemPrice());
                }
                item.getItemSku().getItemPrice().setItemPrice(pro.getItemPrice());
                item.setItemDivideDetails(pro.getItemDivideDetails());
            }
        }
    }

    /**
     * 补全单品营销信息
     * @param cart
     * @return OrderPromotion
     * 2025-2-14 10:58:08
     */
    @Override
    public OrderPromotion fillCartItemPromotions(Cart cart) {
        // 所有商品一起请求营销
        log.info("fillOrderPromotions, cart {}", JSON.toJSONString(cart));
        OrderPromotion orderPromotion = priceCalcAbility.queryCartPromotionsNew(cart);
        log.info("fillOrderPromotions, queryCartPromotions result {}", JSON.toJSONString(orderPromotion));
        return orderPromotion;
    }

    /**
     * 补全单品营销信息
     * @param cart
     * @return
     */
    @Override
    public void fillCartGroupItemPromotions(Cart cart) {
        // 分组
        List<CartGroup> groups =  new ArrayList<>();
        // 每个分组都查询一遍  先把排除分组找出来
        CartGroup invalidGroup = cart.getGroups()
            .stream()
            .filter(cartGroup -> PayMode.INVALID.equals(cartGroup.getPayMode()))
            .findFirst()
            .orElse(null);
        Boolean existValid = Objects.isNull(invalidGroup);
        if (Boolean.TRUE.equals(existValid)) {
            invalidGroup = new CartGroup();
            invalidGroup.setPayMode(PayMode.INVALID);
            invalidGroup.setSelectEnable(Boolean.FALSE);
            invalidGroup.setCartItems(new ArrayList<>());
            invalidGroup.setGroupType(CartGroupTypeEnum.PAY_MODE_GROUP.getCode());
        }
        for (CartGroup cartGroup : cart.getGroups()) {
            // 为空 或者 失效分组跳过
            if (Objects.isNull(cartGroup) ||
                PayMode.INVALID.equals(cartGroup.getPayMode())) {
                continue;
            }
            List<CartItem> cartItems = cartGroup.getCartItems();
            /**
             *  第一次调用营销，计算是否存在时效的商品，
             *  如果不存在，则结束计算 ，
             *  存在 则递归计算 ，处理时效商品
             */
            List<ItemSkuId> itemSkuIds = calcGroupPromotion(cartGroup, cart);
            while (CollectionUtils.isNotEmpty(itemSkuIds)) {
                // 将需要排除的商品归类到到失效商品中 剩余的商品 再算一次营销
                // 继续算商品
                List<CartItem> validItem = new ArrayList<>();
                // 踢出的商品
                List<CartItem> invalidItem = new ArrayList<>();
                for (CartItem cartItem : cartItems) {
                    Boolean exist = itemSkuIds.stream().anyMatch(itemSkuId ->
                        itemSkuId.getSkuId().equals(cartItem.getSkuId()) &&
                        itemSkuId.getItemId().equals(cartItem.getItemId()) &&
                        itemSkuId.getSellerId().equals(cartItem.getSellerId())
                    );
                    if (Boolean.TRUE.equals(exist)) {
                        // 设置为卖家下架状态
                        cartItem.setSelectEnable(Boolean.FALSE);
                        cartItem.setItemChangeStatus(Boolean.TRUE);
                        invalidItem.add(cartItem);
                        continue;
                    }
                    validItem.add(cartItem);
                }
                // 失效的加入失效分组
                invalidGroup.getCartItems().addAll(invalidItem);
                // 还有继续算的商品 在算一次营销
                // 再算一次营销
                cartGroup.setCartItems(validItem);
                if (CollectionUtils.isNotEmpty(validItem)) {
                    // 剩余的下次计算
                    cartItems = validItem;
                    itemSkuIds = calcGroupPromotion(cartGroup, cart);
                } else {
                    // 没有可用的了 直接结束
                    itemSkuIds = new ArrayList<>();
                }
            }
            // 计算后 支付方式为空了 则跳过这个支付方式 不要了
            if (CollectionUtils.isNotEmpty(cartGroup.getCartItems())) {
                groups.add(cartGroup);
            }
        }
        // 增加失效的数据
        if (CollectionUtils.isNotEmpty(invalidGroup.getCartItems())) {
            groups.add(invalidGroup);
        }
        cart.setGroups(groups);
    }

    /**
     * 计算营销信息
     * @param cartGroup
     * @param cart
     */
    private List<ItemSkuId> calcGroupPromotion(CartGroup cartGroup, Cart cart) {
        // 构建请求参数
        Cart promotionCart = new Cart();
        List<CartGroup> groups = new ArrayList<>();
        groups.add(cartGroup);
        promotionCart.setGroups(groups);
        promotionCart.setCustId(cart.getCustId());
        promotionCart.setPayMode(cart.getPayMode());
        promotionCart.setPromotions(cart.getPromotions());
        // 按照卖家分组
        promotionCart.setGroupsBySeller(sortGroupingBySellId(cartGroup.getCartItems()));
        // 调用计算
        OrderPromotion orderPromotion = fillCartItemPromotions(promotionCart);
        // 需要排除的商品信息
        List<ItemSkuId> itemSkuIdList = new ArrayList<>();
        // 营销商品信息
        List<ItemPromotion> itemPromotions = new ArrayList<>();
        // 获取秒杀 预售 赠品
        checkItemPromotion(orderPromotion, itemSkuIdList, itemPromotions);
        //  如果不存在正常计算计算营销价格
        if (CollectionUtils.isEmpty(itemSkuIdList)) {
            calcItemPromotion(cartGroup, itemPromotions);
        }
        // 返回数组
        return itemSkuIdList;
    }

    /**
     * 按照卖家分组 --- 分组排序
     * @param list
     * @return
     *
     */
    private List<CartGroup> sortGroupingBySellId(List<CartItem> list) {
        LinkedHashMultimap<Long, CartItem> multimap = LinkedHashMultimap.create();
        for (CartItem item : list) {
            multimap.put(item.getSellerId(), item);
        }
        // 转换
        List<CartGroup> groups = new ArrayList<>();
        for (Long sellerId : multimap.keySet()) {
            List<CartItem> items = new ArrayList<>();
            for (CartItem item : multimap.get(sellerId)) {
                items.add(item);
            }
            CartGroup group = new CartGroup();
            group.setGroupType(CartGroupTypeEnum.SELLER_GROUP.getCode());
            group.setSellerId(sellerId);
            group.setCartItems(items);
            groups.add(group);
        }
        return groups;
    }

    /**
     * 校验营销结果，踢出去不能下单的商品
     * @param orderPromotion
     * @param itemSkuIdList
     * @param itemPromotions
     * 2025-2-13 20:10:04
     */
    private void checkItemPromotion(OrderPromotion orderPromotion, List<ItemSkuId> itemSkuIdList, List<ItemPromotion> itemPromotions ) {
        if (Objects.isNull(orderPromotion) ||
            CollectionUtils.isEmpty(orderPromotion.getSellers())) {
            return;
        }
        // step 1 判断是否有预售或者秒杀 ， 如果有 则提出这些 再计算一次
        for (SellerPromotion sellerPromotion : orderPromotion.getSellers()) {
            //商品为空  跳过
            if (Objects.isNull(sellerPromotion) ||
                CollUtil.isEmpty(sellerPromotion.getItems())) {
                continue;
            }
            // 遍历每商品
            for (ItemPromotion itemPromotion : sellerPromotion.getItems()) {
                // 设置卖家ID
                itemPromotion.getItemSkuId().setSellerId(sellerPromotion.getSellerId());
                // 加入数组
                itemPromotions.add(itemPromotion);
                // 遍历每商品的营销信息
                if (Objects.isNull(itemPromotion) ||
                    CollUtil.isEmpty(itemPromotion.getItemDivideDetails())) {
                    continue;
                }
                for (ItemDivideDetail detail : itemPromotion.getItemDivideDetails()) {
                    if(Objects.isNull(detail)) {
                        continue;
                    }
                    // 营销商品check， 预售，秒杀，奖品 不可以购买
                    if (AssetsType.AWARD.getCode().equals(detail.getAssetType())) {
                        itemSkuIdList.add(itemPromotion.getItemSkuId());
                        break;
                    }
                    if (PromotionToolCodes.YUSHOU.equals(detail.getToolCode())) {
                        itemSkuIdList.add(itemPromotion.getItemSkuId());
                        break;
                    }
                    if (PromotionToolCodes.MIAOSHA.equals(detail.getToolCode())) {
                        itemSkuIdList.add(itemPromotion.getItemSkuId());
                        break;
                    }
                }
            }
        }
    }

    /**
     * 营销金额计算表
     * @param cartGroup
     * @param itemPromotions
     */
    private void calcItemPromotion(CartGroup cartGroup, List<ItemPromotion> itemPromotions) {
        Long totalPromotionPrice = 0L;
        for (CartItem cartItem : cartGroup.getCartItems()) {
            // 找到对应卖家 商品的营销信息 ，设置价格
            ItemPromotion promotion = itemPromotions.stream()
                .filter(itemPromotion ->
                    cartItem.getSkuId().equals(itemPromotion.getItemSkuId().getSkuId()) &&
                    cartItem.getItemId().equals(itemPromotion.getItemSkuId().getItemId()) &&
                    cartItem.getSellerId().equals(itemPromotion.getItemSkuId().getSellerId())
                )
                .findFirst().orElse(null);
            if (Objects.isNull(promotion)) {
                continue;
            }
            Long promotionPrice = promotion.getPromotionPrice();
            cartItem.setCartPromotionPrice(promotionPrice);
            cartItem.setPriceChangeStatus(!Objects.equals(cartItem.getCartPromotionPrice(), cartItem.getAddCartPrice()));
            if (PayModeCode.isInstallment(PayModeCode.codeOf(cartItem.getPayMode()))) {
                cartItem.setSelectEnable(promotionPrice > tradeLimitConfiguration.getInstallmentLimit());
            }
            if (PayModeCode.isLoan(PayModeCode.codeOf(cartItem.getPayMode()))) {
                cartItem.setSelectEnable(promotionPrice > tradeLimitConfiguration.getLoanLimit());
            }
            cartItem.setItemUnitPrice(Math.round((double) promotionPrice / (cartItem.getQuantity() * 1L)));
            totalPromotionPrice += promotionPrice;
        }
        if (PayModeCode.isInstallment(PayModeCode.codeOf(cartGroup.getPayMode()))) {
            cartGroup.setSelectEnable(totalPromotionPrice > tradeLimitConfiguration.getInstallmentLimit());
        }
        if (PayModeCode.isLoan(PayModeCode.codeOf(cartGroup.getPayMode()))) {
            cartGroup.setSelectEnable(totalPromotionPrice > tradeLimitConfiguration.getLoanLimit());
        }
    }

    /**
     * 计算营销优惠信息
     * @param cart
     */
    @Override
    public void fillOrderPromotions(Cart cart) {
        OrderPromotion orderPromotion = priceCalcAbility.queryCartPromotions(cart);
        cart.setPromotions(orderPromotion);
    }
}
