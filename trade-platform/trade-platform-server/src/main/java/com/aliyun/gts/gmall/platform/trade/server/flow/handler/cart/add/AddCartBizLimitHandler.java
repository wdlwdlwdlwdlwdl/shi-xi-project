package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.add;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.LoanPeriodDTO;
import com.aliyun.gts.gmall.platform.promotion.common.constant.PromotionToolCodes;
import com.aliyun.gts.gmall.platform.promotion.common.type.AssetsType;
import com.aliyun.gts.gmall.platform.trade.api.constant.CartErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.PayModeCode;
import com.aliyun.gts.gmall.platform.trade.core.ability.price.PriceCalcAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ItemService;
import com.aliyun.gts.gmall.platform.trade.core.util.StatusUtils;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.ItemPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemDivideDetail;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.OrderPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.SellerPromotion;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartAdd;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 添加购物车 业务校验类
 *     校验营销活动
 *     计算加车价格字段
 */
@Component
public class AddCartBizLimitHandler extends AdapterHandler<TCartAdd> {

    @Autowired
    private ItemService itemService;

    @Autowired
    private PriceCalcAbility priceCalcAbility;

    // 年龄
    @NacosValue(value = "${trade.cart.age-limit:21}", autoRefreshed = true)
    private Integer age;

    @Override
    public void handle(TCartAdd inbound) {
        // 业务限制，例如虚拟商品不允许加购
        CartItem cart = inbound.getDomain();
        // 加车商品
        List<ItemSkuId> itemSkuIds = new ArrayList<>();
        ItemSkuId itemSkuId = new ItemSkuId(
            cart.getItemId(),
            cart.getSkuId(),
            cart.getSellerId(),
            cart.getSkuQuoteId(),
            cart.getCityCode(),
            cart.getPayMode()
        );
        itemSkuIds.add(itemSkuId);
        // 查询商品信息
        Map<ItemSkuId, ItemSku> itemMap = itemService.queryItemsToCartFromCache(itemSkuIds);
        // 商品信息不能为空 ， 为空提示
        if (itemMap == null || MapUtils.isEmpty(itemMap)) {
            inbound.setError(CartErrorCode.CART_ITEM_NOT_EXISTS);
        }
        // 处理商品数据 理论上只能找到一个商品
        for(ItemSku itemSku: itemMap.values()){
            // 21岁限制校验！ 不能加车 不能买
            if (Boolean.TRUE.equals(itemSku.getAgeCategory()) && cart.getAge() < age) {
                throw new GmallException(OrderErrorCode.CART_AGE_LIMIT);
            }
            // SKU&卖家&商品状态check 必须是正常状态否则不允许加车
            if(StatusUtils.checkSkuStatus(itemSku.getSkuStatus()) ||
                StatusUtils.checkItemStatus(itemSku.getItemStatus())) {
                inbound.setError(CartErrorCode.CART_ITEM_STATUS_INVALID);
                return;
            }
            //卖家状态 check , 必须是正常状态否则不允许加车
            if(StatusUtils.checkSellerStatus(itemSku.getSeller().getSellerStatus())) {
                inbound.setError(CartErrorCode.CART_SELL_STATUS_INVALID);
                return;
            }
            // 价格信息不能为空
            if (CollectionUtils.isEmpty(itemSku.getPriceList())) {
                inbound.setError(CartErrorCode.CART_ITEM_NOT_EXISTS);
                return;
            }
            // 获取支付方式枚举
            PayModeCode payModeCode = PayModeCode.codeOf(cart.getPayMode());
            if (payModeCode == null) {
                inbound.setError(CartErrorCode.CART_ITEM_NOT_EXISTS);
                return;
            }
            //价格匹配 根据支付方式 获取对应分期的价格
            LoanPeriodDTO loanPeriodDTO = itemSku.getPriceList().stream().filter(price -> price.getType() == payModeCode.getPeriodNumber()).findFirst().orElse(null);
            if (loanPeriodDTO == null) {
                inbound.setError(CartErrorCode.CART_ITEM_NOT_EXISTS);
                return;
            }
            // 过滤获取价格
            ItemPrice itemPrice = new ItemPrice();
            itemPrice.setItemPrice(loanPeriodDTO.getValue());
            itemPrice.setOriginPrice(loanPeriodDTO.getValue());
            itemSku.setItemPrice(itemPrice);
            // 加车金额 = 商品数量*单价
            cart.setItemSku(itemSku);
            cart.setAddCartPrice(cart.getQuantity() * itemSku.getItemPrice().getOriginPrice());
        }
        // 查询营销
        OrderPromotion orderPromotion = priceCalcAbility.queryCartPromotions(cart, inbound.getReq().getChannel());
        if (orderPromotion == null) {
            return;
        }
        // 营销价格设置，如果没有营销活动命中 则返回 商品数量*单价的原价 加车价格用原价比较 不用营销价格
        if (orderPromotion.getPromotionPrice() != null) {
            cart.setAddCartPrice(orderPromotion.getPromotionPrice());
        }
        // 遍历每个营销活动，如果是预售，秒杀，奖品 不可以购买
        if (CollUtil.isEmpty(orderPromotion.getSellers())) {
            return;
        }
        for (SellerPromotion sellerPromotion : orderPromotion.getSellers()) {
            if (inbound.isError()) {
                break;
            }
            //商品为空  跳过
            if (CollUtil.isEmpty(sellerPromotion.getItems())) {
                continue;
            }
            // 遍历每商品
            for (ItemPromotion itemPromotion : sellerPromotion.getItems()) {
                // 遍历每商品的营销信息
                if (CollUtil.isEmpty(itemPromotion.getItemDivideDetails())) {
                    continue;
                }
                for (ItemDivideDetail detail : itemPromotion.getItemDivideDetails()) {
                    // 营销商品check， 预售，秒杀，奖品 不可以购买
                    if (detail != null && AssetsType.AWARD.getCode().equals(detail.getAssetType())) {
                        //说明是奖品
                        inbound.setError(CartErrorCode.CART_NOT_SUPPORT_AWARD);
                        break;
                    }
                    if (detail != null && PromotionToolCodes.YUSHOU.equals(detail.getToolCode())) {
                        //说明是预售
                        inbound.setError(CartErrorCode.CART_NOT_SUPPORT_PRESALE);
                        break;
                    }
                    if (detail != null && PromotionToolCodes.MIAOSHA.equals(detail.getToolCode())) {
                        //秒杀也不给加购物车
                        inbound.setError(CartErrorCode.CART_NOT_SUPPORT_SECKILL);
                        break;
                    }
                }
            }
        }
    }
}
