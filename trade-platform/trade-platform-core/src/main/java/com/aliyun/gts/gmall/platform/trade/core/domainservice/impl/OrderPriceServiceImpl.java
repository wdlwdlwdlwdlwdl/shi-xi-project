package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.core.ability.price.PriceAdjustAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.price.PriceCalcAbility;
import com.aliyun.gts.gmall.platform.trade.core.convertor.PromotionConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderPriceService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeNotify;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.AdjustPrice;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PromotionRepository.QueryFrom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class OrderPriceServiceImpl implements OrderPriceService {

    @Autowired
    private PromotionConverter promotionConverter;
    @Autowired
    private PriceCalcAbility priceCalcAbility;
    @Autowired
    private PriceAdjustAbility priceAdjustAbility;

    /**
     * 计算订单优惠与价格, 确认订单使用
     * @param  creatingOrder
     */
    @Override
    public void calcOrderPrice(CreatingOrder creatingOrder) {
        /**
         * 计算订单金额方法 扩展点
         */
        priceCalcAbility.calcOrderPrices(creatingOrder, QueryFrom.CONFIRM_ORDER);
    }

    /**
     * 计算订单优惠与价格, 确认订单使用
     * @param  creatingOrder
     */
    @Override
    public void calcOrderPriceNew(CreatingOrder creatingOrder) {
        /**
         * 计算订单金额方法 扩展点
         * 基础实现类 DefaultPriceCalcExt
         * 扩展实现类 CommonPriceCalcExt
         */
        priceCalcAbility.calcOrderPriceNew(creatingOrder, QueryFrom.CONFIRM_ORDER);
    }

    @Override
    public void recalcOrderPrice(CreatingOrder order) {
        /**
         * 入参金额和计算出来的金额比较 必须相同 否则报错
         * orderRealAmt 实付金额
         * freightAmt 运费
         * promotionAmt 实际支付金额
         */
        long orderRealAmt = order.getOrderPrice().getOrderRealAmt();
        //long pointAmt = order.getOrderPrice().getPointAmt();
        //long pointCount = order.getOrderPrice().getPointCount();
        Long freightAmt = order.getOrderPrice().getFreightAmt();
        //免运费
        if (order.getFreightFeeFree()) {
            freightAmt = 0L;
        }
        Long promotionAmt = order.getOrderPrice().getOrderPromotionAmt();
        /**重新计算 价格计算扩展点*/
        priceCalcAbility.calcOrderPriceNew(order, QueryFrom.CREATE_ORDER);
        /**对比前后两次结果一致性*/
        if (!Objects.equals(freightAmt, order.getOrderPrice().getFreightAmt().longValue()) ||
            !Objects.equals(orderRealAmt, order.getOrderPrice().getOrderRealAmt().longValue()) ||
            !Objects.equals(promotionAmt, order.getOrderPrice().getOrderPromotionAmt().longValue())) {
            throw new GmallException(OrderErrorCode.PRICE_CALC_DISCORD);
        }
    }

    @Override
    public List<OrderChangeNotify> adjustPrice(MainOrder mainOrder, AdjustPrice adj) {
        return priceAdjustAbility.adjustPrice(mainOrder, adj);
    }
}
