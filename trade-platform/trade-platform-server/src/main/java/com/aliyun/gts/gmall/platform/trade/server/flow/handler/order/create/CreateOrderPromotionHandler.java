package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.create;

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

@Component
public class CreateOrderPromotionHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private PromotionConverter promotionConverter;
    @Autowired
    private PriceCalcAbility priceCalcAbility;
    @Autowired
    private OrderBizCodeAbility orderBizCodeAbility;

    @Override
    public void handle(TOrderCreate inbound) {
        CreatingOrder order = inbound.getDomain();
        CreateOrderRpcReq req = inbound.getReq();

        // 查询优惠，填入订单
        order.setPromotionSource(req.getPromotionSource());
        OrderPromotion result = priceCalcAbility.queryOrderPromotions(order, QueryFrom.CREATE_ORDER);
        checkPromotion(order.getPromotions(), result);

        //抽奖奖品，预售或者秒杀，全部不给加购物车，购买时候只能一个商品，一个子订单，而且只能一个
        boolean onlyOneItem = false;
        //如果是抽奖的奖品，则运费免费
        if (result != null && CollectionUtils.isNotEmpty(result.getSellers())) {
            for (SellerPromotion sellerPromotion : result.getSellers()) {
                if (CollectionUtils.isNotEmpty(sellerPromotion.getItems())) {
                    for (ItemPromotion itemPromotion : sellerPromotion.getItems()){
                        if (CollectionUtils.isNotEmpty(itemPromotion.getItemDivideDetails())) {
                            for (ItemDivideDetail detail : itemPromotion.getItemDivideDetails()) {
                                if (AssetsType.AWARD.getCode().equals(detail.getAssetType())) {
                                    //说明是抽奖奖品
                                    order.setFreightFeeFree(true);
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
            }
        }
        //抽奖奖品，预售或者秒杀，全部不给加购物车，购买时候只能一个商品，一个子订单，而且只能一个
        if (onlyOneItem && (order.getMainOrders().size() > 1 || order.getMainOrders().get(0).getSubOrders().size() > 1)) {
            inbound.setError(OrderErrorCode.UNABLE_ORDER_SAME_TIME);
            return;
        }
        
        promotionConverter.promotionToOrder(result, order);
        priceCalcAbility.afterOrderPromotions(order, QueryFrom.CREATE_ORDER);

        // 营销业务身份
        BizCodeHandlerUtils.fillBizCode(inbound, orderBizCodeAbility::getBizCodesFromPromotion);
    }

    // 校验营销信息一致
    private void checkPromotion(OrderPromotion confirm, OrderPromotion create) {
        Multimap<Long, Long> skuCampMap1 = getCampInfo(confirm);
        Multimap<Long, Long> skuCampMap2 = getCampInfo(create);
        if (!skuCampMap1.equals(skuCampMap2)) {
            throw new GmallException(OrderErrorCode.PROMOTION_DISCORD);
        }
    }

    private Multimap getCampInfo(OrderPromotion prom) {
        Multimap<Long, Long> map = HashMultimap.create();
        prom.getSellers().stream()
                .flatMap(s -> s.getItems().stream())
                .forEach(item -> {
                    if (item.getItemDivideDetails() == null) {
                        return;
                    }
                    item.getItemDivideDetails().stream().forEach(d -> {
                        if (d.getCampId() != null) {
                            map.put(item.getItemSkuId().getSkuId(), d.getCampId());
                        }
                    });
                });
        return map;
    }
}
