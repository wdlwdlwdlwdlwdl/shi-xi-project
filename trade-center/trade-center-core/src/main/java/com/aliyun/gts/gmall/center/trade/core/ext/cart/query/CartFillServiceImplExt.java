package com.aliyun.gts.gmall.center.trade.core.ext.cart.query;

import com.aliyun.gts.gmall.platform.item.api.dto.output.item.LoanPeriodDTO;
import com.aliyun.gts.gmall.platform.trade.api.constant.PayModeCode;
import com.aliyun.gts.gmall.platform.trade.core.ability.price.PriceCalcAbility;
import com.aliyun.gts.gmall.platform.trade.core.config.TradeLimitConfiguration;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ItemService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.impl.CartFillServiceImpl;
import com.aliyun.gts.gmall.platform.trade.core.util.StatusUtils;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartGroup;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.ItemPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.OrderPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.PromotionOption;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.SellerPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Primary
@Service
@Slf4j
public class CartFillServiceImplExt extends CartFillServiceImpl {

    @Autowired
    private TradeLimitConfiguration tradeLimitConfiguration;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PriceCalcAbility priceCalcAbility;

    /**
     * 填充商品信息
     * @param cart
     */
    public void fillItemInfo(Cart cart) {
        // 转换参数
        List<CartItem> cartItemList = cart.getGroups().stream()
            .flatMap((groupx) -> groupx.getCartItems().stream())
            .collect(Collectors.toList());
        // 入参不能为空
        if (CollectionUtils.isEmpty(cartItemList)) {
            return;
        }
        // 根据itemid skuid sellerid
        List<ItemSkuId> itemSkuIds = getItemIds(cartItemList);
        // 查询商品
        Map<ItemSkuId, ItemSku> itemMap = itemService.queryItemsToCartFromCache(itemSkuIds);
        // 结果
        if (MapUtils.isEmpty(itemMap)) {
            return;
        }
        /**
         * 遍历每个商品 根据支付方式获取售价
         * 比较加入购物车的价格和现在的价格
         * 判断卖家SKU状态
         * 判断卖家状态
         * 判断商品状态
         */
        for (CartGroup cartGroup : cart.getGroups()) {
            if (CollectionUtils.isEmpty(cartGroup.getCartItems())) {
                continue;
            }
            for (CartItem cartItem : cartGroup.getCartItems()) {
                // 根据卖家+sku+item 分组商品
                ItemSku itemOriginal = itemMap.get(new ItemSkuId(
                    cartItem.getItemId(),
                    cartItem.getSkuId(),
                    cartItem.getSellerId()
                ));
                ItemSku item = new ItemSku();
                try {
                    PropertyUtils.copyProperties(item, itemOriginal);
                } catch (Exception e) {
                    cartItem.setItemNotFound(true);
                    continue;
                }
                // 过滤获取价格
                ItemPrice itemPrice = new ItemPrice();
                item.setItemPrice(itemPrice);
                // 价格信息不能为空
                List<LoanPeriodDTO> priceList = item.getPriceList();
                if (CollectionUtils.isEmpty(priceList)) {
                    //获取价格失败 算作卖家不卖了
                    cartItem.setSkuChangeStatus(Boolean.TRUE);
                    if (item.getSeller() != null) {
                        cartItem.getFeatures().setSellerName(item.getSeller().getSellerName());
                    }
                    continue;
                }
                // 根据支付方式获取枚举
                PayModeCode payModeCode = PayModeCode.codeOf(cartItem.getPayMode());
                if (Objects.isNull(payModeCode)) {
                    continue;
                }
                //价格匹配
                LoanPeriodDTO loanPeriodDTO = priceList
                    .stream()
                    .filter(price -> price.getType().equals(payModeCode.getPeriodNumber()))
                    .findFirst()
                    .orElse(null);
                // 支付方式是epay或者loan价格不能获取失败
                if (Objects.isNull(loanPeriodDTO) &&
                    (PayModeCode.isEpay(payModeCode) || PayModeCode.isLoan(payModeCode))) {
                    //获取价格失败 算作卖家不卖了
                    cartItem.setSkuChangeStatus(Boolean.FALSE);
                    continue;
                }
                //installment 如果获取失败了 就获取最大的分期
                if(PayModeCode.isInstallment(payModeCode) && Objects.isNull(loanPeriodDTO)) {
                    // 按照分期排序 获取最大的
                    priceList.sort(Comparator.comparing(LoanPeriodDTO::getType).reversed());
                    loanPeriodDTO = priceList.get(0);
                    if (Objects.isNull(loanPeriodDTO)) {
                        continue;
                    }
                    PayModeCode pCode = PayModeCode.getInstallment(loanPeriodDTO.getType());
                    if (Objects.isNull(pCode)) {
                        continue;
                    }
                    cartItem.setPayMode(pCode.getCode());
                    itemPrice.setItemPrice(loanPeriodDTO.getValue());
                    itemPrice.setOriginPrice(loanPeriodDTO.getValue());
                    // 分期变化
                    cartItem.setInstallChangeStatus(Boolean.TRUE);
                    // 分期数变化
                    // 旧的分期
                    cartItem.setOriginInstallment(payModeCode.getPeriodNumber());
                    // 新的分期
                    cartItem.setCurrentInstallment(pCode.getPeriodNumber());
                    // 商品单价*商品数量 原价价格
                    cartItem.setCartTotalPrice(cartItem.getQuantity() * loanPeriodDTO.getValue());
                    // 营销家 先设置原价 后面营销计算替换替换
                    cartItem.setCartPromotionPrice(cartItem.getQuantity() * loanPeriodDTO.getValue());
                    // 价格变化
                    cartItem.setPriceChangeStatus(Boolean.FALSE);
                } else {
                    itemPrice.setItemPrice(loanPeriodDTO.getValue());
                    itemPrice.setOriginPrice(loanPeriodDTO.getValue());
                    // 分期不可能变化
                    cartItem.setInstallChangeStatus(Boolean.FALSE);
                    // 商品单价*商品数量 价格
                    cartItem.setCartTotalPrice(cartItem.getQuantity() * loanPeriodDTO.getValue());
                    // 营销家 先设置原价 后面营销计算替换替换
                    cartItem.setCartPromotionPrice(cartItem.getQuantity() * loanPeriodDTO.getValue());
                    // 旧的分期
                    cartItem.setOriginInstallment(payModeCode.getPeriodNumber());
                    // 新的分期
                    cartItem.setCurrentInstallment(payModeCode.getPeriodNumber());
                    // 价格变化
                    cartItem.setPriceChangeStatus(Boolean.FALSE);
                }
                cartItem.setItemSku(item);
                //SKU状态
                cartItem.setSkuChangeStatus(StatusUtils.checkSkuStatus(item.getSkuStatus()));
                // 如果没有价格 也算做失效 卖家不卖了
                //卖家状态
                cartItem.setSellerChangeStatus(StatusUtils.checkSellerStatus(item.getSeller().getSellerStatus()));
                //商品状态
                cartItem.setItemChangeStatus(StatusUtils.checkItemStatus(item.getItemStatus()));
                if (item.getSeller() != null) {
                    cartItem.getFeatures().setSellerName(item.getSeller().getSellerName());
                }
            }
        }
    }

    /**
     * 获取全部
     * @param cartItemList
     * @return
     */
    private List<ItemSkuId> getItemIds(List<CartItem> cartItemList) {
        List<ItemSkuId> itemIdSkuIdList = new ArrayList<>();
        List<String> itemIdSkuIdKeyList = new ArrayList<>();
        for (CartItem cartItem : cartItemList) {
            if (itemIdSkuIdKeyList.contains(String.format("%s_%s_%s_%s", cartItem.getSellerId(), cartItem.getItemId(), cartItem.getSkuId(), cartItem.getSkuQuoteId()))) {
                continue;
            }
            itemIdSkuIdKeyList.add(String.format("%s_%s_%s_%s", cartItem.getSellerId(), cartItem.getItemId(), cartItem.getSkuId(), cartItem.getSkuQuoteId()));
            itemIdSkuIdList.add(
                new ItemSkuId(
                    cartItem.getItemId(),
                    cartItem.getSkuId(),
                    cartItem.getSellerId(),
                    cartItem.getSkuQuoteId(),
                    cartItem.getCityCode()
                )
            );
        }
        return itemIdSkuIdList;
    }

    /**
     * 营销计算
     * TODO 先一个个算 再改成批量
     * @param cart
     * @return
     * 2024-12-4 18:02:48
     */
    @Override
    public void fillItemPromotions(Cart cart) {
        // 转换参数
        List<CartItem> cartItemList = cart.getGroups()
            .stream()
            .flatMap((groupx) -> groupx.getCartItems().stream())
            .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(cartItemList)) {
            return;
        }
        for (CartItem cartItem : cartItemList) {
            // 异常的商品 不计算营销
            if (Boolean.FALSE.equals(cartItem.getItemSku().isEnabled()) || cartItem.getItemSku().getItemPrice().getOriginPrice() == null) {
                continue;
            }
            cartItem.setCustId(cart.getCustId());
            // 营销计算
            OrderPromotion orderPromotion = priceCalcAbility.queryCartPromotions(cartItem, cart.getChannel());
            // 回填
            if (orderPromotion != null && Objects.nonNull(orderPromotion.getPromotionPrice())) {
                Long promotionPrice = orderPromotion.getPromotionPrice();
                cartItem.setCartPromotionPrice(promotionPrice);
                cartItem.setPriceChangeStatus(!Objects.equals(cartItem.getCartTotalPrice(), cartItem.getCartTotalPrice()));
                if (PayModeCode.isInstallment(PayModeCode.codeOf(cartItem.getPayMode()))) {
                    cartItem.setSelectEnable(promotionPrice > tradeLimitConfiguration.getInstallmentLimit());
                }
                if (PayModeCode.isLoan(PayModeCode.codeOf(cartItem.getPayMode()))) {
                    cartItem.setSelectEnable(promotionPrice > tradeLimitConfiguration.getLoanLimit());
                }
                cartItem.setItemUnitPrice(Math.round((double) promotionPrice / (cartItem.getQuantity() * 1L)));
            }
        }
    }

    /**
     * 计算营销优惠信息
     * @param cart
     */
    @Override
    public void fillOrderPromotions(Cart cart) {
        List<OrderPromotion> orderPromotionList = new ArrayList<>();
        for(CartGroup cartGroup :cart.getGroups())
        {
            for(CartItem cartItem :cartGroup.getCartItems())
            {
                // 查询营销
                OrderPromotion orderPromotion = priceCalcAbility.queryCartPromotions(cartItem, cart.getChannel());
                if (orderPromotion == null) {
                    log.warn("single orderPromotion is null");
                    continue;
                }
                orderPromotionList.add(orderPromotion);
            }
        }
        OrderPromotion  mixOrderPromotion = buldOrderPromotion(orderPromotionList);
        log.info("mixOrderPromotion={}",mixOrderPromotion);
        cart.setPromotions(mixOrderPromotion);
    }


    private OrderPromotion buldOrderPromotion(List<OrderPromotion> orderPromotionList)
    {
        if(CollectionUtils.isEmpty(orderPromotionList))
        {
            return null;
        }
        if(orderPromotionList.size()==1) {
            return orderPromotionList.get(0);
        }
        OrderPromotion mixOrderPromotion = new OrderPromotion();
        Long promotionPrice =0L;
        List<SellerPromotion> sellers = new ArrayList<>();
        List<PromotionOption> options = new ArrayList<>();
        for(OrderPromotion orderPromotion: orderPromotionList)
        {
            promotionPrice+=orderPromotion.getPromotionPrice();
            sellers.addAll(orderPromotion.getSellers());
            if(CollectionUtils.isNotEmpty(orderPromotion.getOptions()))
            {
                options.addAll(orderPromotion.getOptions());
            }
        }
        mixOrderPromotion.setPromotionPrice(promotionPrice);
        mixOrderPromotion.setSellers(sellers);
        mixOrderPromotion.setOptions(options);
        return mixOrderPromotion;
    }
}
