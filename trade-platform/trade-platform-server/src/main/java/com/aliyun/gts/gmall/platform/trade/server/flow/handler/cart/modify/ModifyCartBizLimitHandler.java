package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.modify;

import cn.hutool.core.collection.CollUtil;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.LoanPeriodDTO;
import com.aliyun.gts.gmall.platform.promotion.common.constant.PromotionToolCodes;
import com.aliyun.gts.gmall.platform.promotion.common.type.AssetsType;
import com.aliyun.gts.gmall.platform.trade.api.constant.CartErrorCode;
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
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartModify;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 修改购物车数量 业务校验类
 *     校验营销活动
 *     计算加车价格字段
 */
@Component
public class ModifyCartBizLimitHandler extends AdapterHandler<TCartModify> {

    @Autowired
    private PriceCalcAbility priceCalcAbility;

    @Autowired
    private ItemService itemService;
    @Override
    public void handle(TCartModify inbound) {
        // 业务限制，例如虚拟商品不允许加购
        //预购的产品不准加购物车
        CartItem cart = inbound.getDomain();
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
        Map<ItemSkuId, ItemSku> itemMap = itemService.queryItemsToCartFromCache(itemSkuIds);
        // 商品信息不能为空 ， 为空提示
        if (itemMap == null || MapUtils.isEmpty(itemMap)) {
            inbound.setError(CartErrorCode.CART_ITEM_NOT_EXISTS);
            return;
        }
        // 处理商品数据 理论上只能找到一个商品
        for (ItemSku itemSku : itemMap.values()) {
            //        itemMap.values().forEach(itemSku -> {
            // SKU&卖家&商品状态check
            if (StatusUtils.checkSkuStatus(itemSku.getSkuStatus()) ||
                StatusUtils.checkSellerStatus(itemSku.getSeller().getSellerStatus()) ||
                StatusUtils.checkItemStatus(itemSku.getItemStatus())) {
                inbound.setError(CartErrorCode.CART_ITEM_STATUS_INVALID);
                return;
            }
            // 价格信息不能为空
            if (CollectionUtils.isEmpty(itemSku.getPriceList())) {
                inbound.setError(CartErrorCode.CART_ITEM_NOT_EXISTS);
                return;
            }
            // 根据支付方式
            PayModeCode payModeCode = PayModeCode.codeOf(cart.getPayMode());
            if (payModeCode == null) {
                inbound.setError(CartErrorCode.CART_ITEM_NOT_EXISTS);
                return;
            }
            //价格匹配
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
            cart.setAddCartPrice(cart.getQuantity() * itemSku.getItemPrice().getOriginPrice());
            cart.setItemSku(itemSku);
        }
        // 查询营销
        OrderPromotion orderPromotion = priceCalcAbility.queryCartPromotions(cart, inbound.getReq().getChannel());
        if (orderPromotion == null) {
            return;
        }
        // 营销价格设置，如果没有营销活动命中 则返回 商品数量*单价的原价
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
