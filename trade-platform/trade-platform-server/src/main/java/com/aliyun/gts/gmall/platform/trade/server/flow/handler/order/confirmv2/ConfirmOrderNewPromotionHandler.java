package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.confirmv2;

import com.aliyun.gts.gmall.center.trade.common.constants.ExtBizCode;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.promotion.common.constant.PromotionToolCodes;
import com.aliyun.gts.gmall.platform.promotion.common.type.AssetsType;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.PayModeCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.promotion.PromotionOptionDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmOrderInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.OrderCheckPayModeDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.PromotionTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderBizCodeAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.price.PriceCalcAbility;
import com.aliyun.gts.gmall.platform.trade.core.convertor.PromotionConverter;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 订单确认 step 3
 *    每个商品营销信息计算
 *    营销金额计算
 *    优惠券计算
 *    满赠赠品
 *    预售 秒杀活动标记
 */
@Slf4j
@Component
public class ConfirmOrderNewPromotionHandler extends AdapterHandler<TOrderConfirm> {

    @Autowired
    private PriceCalcAbility priceCalcAbility;

    @Autowired
    private OrderBizCodeAbility orderBizCodeAbility;

    @Autowired
    private PromotionConverter promotionConverter;

    @Override
    public void handle(TOrderConfirm inbound) {
        CreatingOrder order = inbound.getDomain();
        ConfirmOrderInfoRpcReq confirmOrderInfoRpcReq = inbound.getReq();

        // 积分
        OrderPrice price = new OrderPrice();
        order.setOrderPrice(price);
        price.setPointCount(confirmOrderInfoRpcReq.getUsePointCount());

        // 优惠选择
        if (CollectionUtils.isNotEmpty(confirmOrderInfoRpcReq.getPromotionSelection())) {
            Multimap<Long, PromotionOptionDTO> map = HashMultimap.create();
            for (PromotionOptionDTO opt : confirmOrderInfoRpcReq.getPromotionSelection()) {
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
        order.setPromotionSource(confirmOrderInfoRpcReq.getPromotionSource());
        OrderPromotion result = priceCalcAbility.queryOrderPromotionsNew(order, QueryFrom.CONFIRM_ORDER);

        //抽奖奖品，预售或者秒杀，全部不给加购物车，购买时候只能一个商品，一个子订单，而且只能一个
        boolean onlyOneItem = false;
        //是否预售
        boolean isPresale = false;
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
                             order.setFreightFeeFree(true);
                             onlyOneItem = true;
                         }
                         if (PromotionToolCodes.YUSHOU.equals(detail.getToolCode())) {
                             //说明是预售
                             onlyOneItem = true;
                             isPresale = true;
                         }
                         else if (PromotionToolCodes.MIAOSHA.equals(detail.getToolCode())) {
                             //秒杀也不给加购物车
                             onlyOneItem = true;
                         }
                     }
                 }
            }
        }
        /**抽奖奖品，预售或者秒杀，全部不给加购物车，购买时候只能一个商品，一个子订单，而且只能一个*/
        if (onlyOneItem &&
            CollectionUtils.isNotEmpty(order.getMainOrders()) &&
            (order.getMainOrders().size() > 1 || order.getMainOrders().get(0).getSubOrders().size() > 1)) {
            inbound.setError(OrderErrorCode.UNABLE_ORDER_SAME_TIME);
            return;
        }
        // 数据转转换
        promotionConverter.promotionToOrder(result, order);
        //同一卖家的主单计算营销价格失败，需要叠加子单的营销价格重新计算。
        promotionConverter.convertMainPromotion(order);

        // 计算installment和loan的金额
        calcInstallmentList(result, order);

        /**
         * 营销计算后的钩子 --- 用于满赠商品的计算
         * 用PF4j扩展
         * 定义DefaultPriceCalcExt
         * 实现 CommonPriceCalcExt
         */
        priceCalcAbility.afterOrderPromotions(order, QueryFrom.CONFIRM_ORDER);
        order.getMainOrders().stream().forEach(main -> {
            log.info("start fillBizCode BizCode= {}",main.getBizCodes());
            log.info("start fillBizCode orderType= {}",main.getOrderType());
        });

        /**
         * 营销业务身份 --- 预售活动标记计算
         * 默认实现 DefaultOrderBizCodeExt
         * 扩展实现 MatchAllOrderBizCodeExt
         */
        BizCodeHandlerUtils.fillBizCode(inbound, orderBizCodeAbility::getBizCodesFromPromotion);
        order.getMainOrders().stream().forEach(main -> {
            log.info("end fillBizCode BizCode= {}",main.getBizCodes());
            log.info("start fillBizCode orderType= {}",main.getOrderType());
        });

        //预售
        if (Boolean.TRUE.equals(isPresale)) {
            order.getMainOrders().stream().forEach(main -> {
                if (!OrderTypeEnum.MULTI_STEP_ORDER.getCode().equals(main.getOrderType())) {
                    main.setBizCodes(List.of(ExtBizCode.PRE_SALE));
                    main.setOrderType(OrderTypeEnum.MULTI_STEP_ORDER.getCode());
                }
            });
        }
    }


    /**
     * 计算分期支付数组
     * @param result
     * @param order
     */
    private void calcInstallmentList(OrderPromotion result, CreatingOrder order) {
        List<OrderCheckPayModeDTO> loanPriceList = new ArrayList<>();
        List<OrderCheckPayModeDTO> installPriceList = new ArrayList<>();
        // 分期优惠计算
        if (CollectionUtils.isNotEmpty(order.getInstallment()) &&
            MapUtils.isNotEmpty(result.getInstallmentPromotion()) &&
            MapUtils.isNotEmpty(result.getInstallmentTotal())) {
            for (Integer installment : order.getInstallment()) {
                PayModeCode payModeCode = PayModeCode.getInstallment(installment);
                Long total = result.getInstallmentTotal().get(payModeCode.getCode());
                Long promotion = result.getInstallmentPromotion().get(payModeCode.getCode());
                if (Objects.isNull(payModeCode) || Objects.isNull(total) || Objects.isNull(promotion)) {
                    continue;
                }
                OrderCheckPayModeDTO orderCheckPayModeDTO = new OrderCheckPayModeDTO();
                orderCheckPayModeDTO.setType(installment);
                orderCheckPayModeDTO.setTotalAmount(total);
                orderCheckPayModeDTO.setMonthAmount(Math.round((double)total/installment));
                orderCheckPayModeDTO.setTotalPromotionAmount(promotion);
                orderCheckPayModeDTO.setMonthPromotiomAmount(Math.round((double)promotion/installment));
                orderCheckPayModeDTO.setPayMode(PayModeCode.getInstallment(installment) != null ?
                    PayModeCode.getInstallment(installment).getCode() :
                    String.format("installment_%s", installment)
                );
                installPriceList.add(orderCheckPayModeDTO);
                // loan
                if (PayModeCode.isInsallment_3(payModeCode)) {
                    loanPriceList = initLoanPriceList(total, promotion);
                }
            }
        }
        // installment 价格交集 总价的 应该用营销价格的
        order.setInstallPriceList(installPriceList);
        // loan 价格交集
        order.setLoanPriceList(loanPriceList);
    }

    /**
     * 加载贷款数据
     * @param total
     * @param promotion
     * @return
     */
    private List<OrderCheckPayModeDTO> initLoanPriceList(Long total, Long promotion) {
        // loan 价格交集 应该用营销价格的
        List<OrderCheckPayModeDTO> loanPriceList = new ArrayList<>();
        for (PayModeCode payModeCode : PayModeCode.values()) {
            if (PayModeCode.isLoan(payModeCode)) {
                OrderCheckPayModeDTO loan = new OrderCheckPayModeDTO();
                loan.setType(payModeCode.getLoanNumber());
                loan.setTotalAmount(total);
                loan.setMonthAmount(Math.round((double)total/payModeCode.getLoanNumber()));
                loan.setTotalPromotionAmount(promotion);
                loan.setMonthPromotiomAmount(Math.round((double)promotion/payModeCode.getLoanNumber()));
                loan.setPayMode(payModeCode.getCode());
                loanPriceList.add(loan);
            }
        }
        return loanPriceList;
    }
}
