package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.createv2;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.promotion.common.constant.PromotionToolCodes;
import com.aliyun.gts.gmall.platform.promotion.common.type.AssetsType;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderBizCodeAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.price.PriceCalcAbility;
import com.aliyun.gts.gmall.platform.trade.core.convertor.PromotionConverter;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemDivideDetail;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.OrderPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.SellerPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PromotionRepository.QueryFrom;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import com.aliyun.gts.gmall.platform.trade.server.utils.BizCodeHandlerUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 创建订单 step 6
 *    商品营销计算
 * @anthor shifeng
 * @version 1.0.1
 * 2024-12-11 16:54:10
 */
@Component
public class CreateOrderNewPromotionHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private PromotionConverter promotionConverter;

    @Autowired
    private PriceCalcAbility priceCalcAbility;

    @Autowired
    private OrderBizCodeAbility orderBizCodeAbility;

    @Override
    public void handle(TOrderCreate inbound) {
        CreateOrderRpcReq createOrderRpcReq = inbound.getReq();

        CreatingOrder creatingOrder = inbound.getDomain();
        // 查询优惠，填入订单
//        createOrderRpcReq.setPromotionSource(createOrderRpcReq.getPromotionSource()); 这行代码逻辑有问题
        creatingOrder.setPromotionSource(createOrderRpcReq.getPromotionSource());
        // 查询营销计算  - 代码过了一遍后，这边没有计算，仅仅是营销查询
        OrderPromotion result = priceCalcAbility.queryOrderPromotionsNew(creatingOrder, QueryFrom.CREATE_ORDER);
        //抽奖奖品，预售或者秒杀，全部不给加购物车，购买时候只能一个商品，一个子订单，而且只能一个
        boolean onlyOneItem = false;
        //如果是抽奖的奖品，则运费免费
        if (Objects.nonNull(result) && CollectionUtils.isNotEmpty(result.getSellers())) {
            for (SellerPromotion sellerPromotion : result.getSellers()) {
                if (CollectionUtils.isEmpty(sellerPromotion.getItems())) {
                    continue;
                }
                for (ItemPromotion itemPromotion : sellerPromotion.getItems()){
                    if (CollectionUtils.isEmpty(itemPromotion.getItemDivideDetails())) {
                        continue;
                    }
                    for (ItemDivideDetail detail : itemPromotion.getItemDivideDetails()) {
                        if (AssetsType.AWARD.getCode().equals(detail.getAssetType())) {
                            //说明是抽奖奖品
                            creatingOrder.setFreightFeeFree(true);
                            onlyOneItem = true;
                        }
                        if (PromotionToolCodes.YUSHOU.equals(detail.getToolCode())) {
                            //说明是预售
                            onlyOneItem = true;
                        }
                        else if (PromotionToolCodes.MIAOSHA.equals(detail.getToolCode())) {
                            //秒杀也不给加购物车
                            onlyOneItem = true;
                        }
                    }
                }
            }
        }
        //抽奖奖品，预售或者秒杀，全部不给加购物车，购买时候只能一个商品，一个子订单，而且只能一个
        if (onlyOneItem &&
            (creatingOrder.getMainOrders().size() > 1 || creatingOrder.getMainOrders().get(0).getSubOrders().size() > 1)) {
            inbound.setError(OrderErrorCode.UNABLE_ORDER_SAME_TIME);
            return;
        }

        // 校验营销规则 营销计算结果check， 防止出现价格不一致的问题！！！
        checkPromotion(creatingOrder.getPromotions(), result);

        // 数据转转换
        promotionConverter.promotionToOrder(result, creatingOrder);
        //同一卖家的主单计算营销价格失败，需要叠加子单的营销价格重新计算。
        promotionConverter.convertMainPromotion(creatingOrder);

        /**
         * 营销计算后的钩子 --- 用于满赠商品的计算
         * 用PF4j扩展
         * 定义DefaultPriceCalcExt
         * 实现 CommonPriceCalcExt
         */
        priceCalcAbility.afterOrderPromotions(creatingOrder, QueryFrom.CREATE_ORDER);

        /**
         * 营销业务身份 --- 预售活动标记计算
         * 默认实现 DefaultOrderBizCodeExt
         * 扩展实现 MatchAllOrderBizCodeExt
         */
        BizCodeHandlerUtils.fillBizCode(inbound, orderBizCodeAbility::getBizCodesFromPromotion);
    }

    /**
     * 校验营销信息一致 否则报错
     * @param confirm
     * @param create
     * 2025-2-25 17:54:25
     */
    private void checkPromotion(OrderPromotion confirm, OrderPromotion create) {
        // 营销总价相同
        if (!Objects.equals(confirm.getPromotionPrice(), create.getPromotionPrice())) {
            throw new GmallException(OrderErrorCode.PROMOTION_DISCORD);
        }
        // 数组为空
        if (CollectionUtils.isEmpty(confirm.getSellers()) && CollectionUtils.isEmpty(create.getSellers())) {
            return;
        }
        // 新计算的数据 和旧的数据比较 获取是否完全相同
        List<ItemPromotion> confirmItem = confirm.getSellers().stream().flatMap(s -> s.getItems().stream()).collect(Collectors.toList());
        List<ItemPromotion> createItem = create.getSellers().stream().flatMap(s -> s.getItems().stream()).collect(Collectors.toList());
        for (ItemPromotion createItemPromotion : createItem) {
            // 存在营销信息
            if (Objects.nonNull(createItemPromotion) &&
                Objects.nonNull(createItemPromotion.getItemDivideDetails())) {
                // 之前缓存的数据
                ItemPromotion confirmItemPromotion = confirmItem.stream().filter(confirmPromotion -> confirmPromotion.getItemSku().getSkuQuoteId().equals(createItemPromotion.getItemSku().getSkuQuoteId())).findFirst().orElse(null);
                if (Objects.isNull(confirmItemPromotion)) {
                    throw new GmallException(OrderErrorCode.PROMOTION_DISCORD);
                }
                // 原价 营销价格必须相同 参与的活动相同
                if (!Objects.equals(confirmItemPromotion.getItemPrice(), createItemPromotion.getItemPrice()) &&
                    !Objects.equals(confirmItemPromotion.getPromotionPrice(), createItemPromotion.getPromotionPrice())) {
                    throw new GmallException(OrderErrorCode.PROMOTION_DISCORD);
                }
                // 参与营销互动的话 营销活动必须相同
                Set<Long> confirmSet = converSet(confirmItemPromotion.getItemDivideDetails());
                Set<Long> createSet = converSet(createItemPromotion.getItemDivideDetails());
                if (!Objects.equals(createSet, confirmSet)) {
                    throw new GmallException(OrderErrorCode.PROMOTION_DISCORD);
                }
            }
        }
    }

    /**
     * 获取参与活动集合
     * @param itemDivideDetails
     * @return Set<Long>
     */
    private Set<Long> converSet(List<ItemDivideDetail> itemDivideDetails) {
        return CollectionUtils.isNotEmpty(itemDivideDetails) ?
            itemDivideDetails.stream().map(itemDivideDetail -> itemDivideDetail.getCampId()).collect(Collectors.toSet()) :
            new HashSet<>();
    }
}
