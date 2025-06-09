package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.confirm;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.promotion.common.constant.PromotionToolCodes;
import com.aliyun.gts.gmall.platform.promotion.common.type.AssetsType;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.promotion.PromotionOptionDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmOrderInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.common.constants.PromotionTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderBizCodeAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.price.PriceCalcAbility;
import com.aliyun.gts.gmall.platform.trade.core.convertor.PromotionConverter;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.OrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemDivideDetail;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.OrderPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.SellerPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PromotionRepository.QueryFrom;
import com.aliyun.gts.gmall.platform.trade.domain.util.CommUtils;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import com.aliyun.gts.gmall.platform.trade.server.utils.BizCodeHandlerUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;

@Component
public class ConfirmOrderPromotionHandler extends AdapterHandler<TOrderConfirm> {

    @Autowired
    private PromotionConverter promotionConverter;
    @Autowired
    private PriceCalcAbility priceCalcAbility;
    @Autowired
    private OrderBizCodeAbility orderBizCodeAbility;

    @Override
    public void handle(TOrderConfirm inbound) {
        ConfirmOrderInfoRpcReq req = inbound.getReq();
        CreatingOrder order = inbound.getDomain();

        OrderPrice price = new OrderPrice();
        price.setPointCount(req.getUsePointCount());
        order.setOrderPrice(price);

        // 优惠选择
        if (CollectionUtils.isNotEmpty(req.getPromotionSelection())) {
            Multimap<Long, PromotionOptionDTO> map = HashMultimap.create();
            for (PromotionOptionDTO opt : req.getPromotionSelection()) {
                long sellerId = opt.getSellerId() == null ? 0L : opt.getSellerId();
                map.put(sellerId, opt);

                // 兼容逻辑
                if (opt.getPromotionType() == null && opt.getIsCoupon() != null) {
                    PromotionTypeEnum type = opt.getIsCoupon().booleanValue() ? PromotionTypeEnum.COUPON : PromotionTypeEnum.NORMAL;
                    opt.setPromotionType(type.getCode());
                }
            }

            // 跨店优惠
            Collection<PromotionOptionDTO> crossOpts = map.removeAll(0L);
            if (CollectionUtils.isNotEmpty(crossOpts)) {
                order.setPromotions(new OrderPromotion());
                order.getPromotions().setOptions(promotionConverter.toOrderPromotions(crossOpts));
            }

            // 店铺优惠
            Map<Long, MainOrder> ordMap = CommUtils.toMap(order.getMainOrders(), ord -> ord.getSeller().getSellerId());
            for (Long sellerId : map.keySet()) {
                MainOrder mainOrder = ordMap.get(sellerId);
                if (mainOrder == null) {
                    throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG, "优惠选项中的卖家ID不存在");
                }
                Collection<PromotionOptionDTO> options = map.get(sellerId);
                mainOrder.setPromotions(new SellerPromotion());
                mainOrder.getPromotions().setOptions(promotionConverter.toOrderPromotions(options));
            }
        }

        // 查询优惠，填入订单
        order.setPromotionSource(req.getPromotionSource());
        OrderPromotion result = priceCalcAbility.queryOrderPromotions(order, QueryFrom.CONFIRM_ORDER);

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
        priceCalcAbility.afterOrderPromotions(order, QueryFrom.CONFIRM_ORDER);

        // 营销业务身份
        BizCodeHandlerUtils.fillBizCode(inbound, orderBizCodeAbility::getBizCodesFromPromotion);
    }
}
