package com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.price;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.StepOrderCreateAbility;
import com.aliyun.gts.gmall.platform.trade.core.convertor.OrderConverter;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.StepOrderCreateExt.DivideStepPriceOption;
import com.aliyun.gts.gmall.platform.trade.core.extension.price.PriceAdjustExt;
import com.aliyun.gts.gmall.platform.trade.core.util.DivideUtils.Divider;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcStepOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.*;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.point.PointExchange;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.AdjustPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.OrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcStepOrderRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils;
import com.aliyun.gts.gmall.platform.trade.domain.util.StepOrderUtils;
import com.aliyun.gts.gmall.platform.trade.persistence.util.TransactionProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.aliyun.gts.gmall.platform.trade.common.util.NumUtils.getNullZero;

@Slf4j
@Component
public class DefaultPriceAdjustExt implements PriceAdjustExt {

    @Autowired
    private TcOrderRepository tcOrderRepository;
    @Autowired
    private OrderConverter orderConverter;
    @Autowired
    private TcStepOrderRepository tcStepOrderRepository;
    @Autowired
    private StepOrderCreateAbility stepOrderCreateAbility;
    @Autowired
    private TransactionProxy transactionProxy;

    @Override
    public List<OrderChangeNotify> adjustPrice(MainOrder mainOrder, AdjustPrice adj) {
        checkAdjPrice(mainOrder, adj);

        StepOrder stepOrder = null;
        if (StepOrderUtils.isMultiStep(mainOrder)) {
            stepOrder = StepOrderUtils.getStepOrder(mainOrder, adj.getStepNo());
        }

        // 主订单
        PointExchange ex = mainOrder.orderAttr().getPointExchange();
        setAdjPrice(mainOrder.getOrderPrice(), adj, ex);
        checkPositive(mainOrder.getOrderPrice());

        // 子订单分摊
        divideSubOrder(mainOrder, adj);

        // 阶段单
        if (stepOrder != null) {
            setAdjStepPrice(stepOrder.getPrice(), adj, ex);
            checkPositive(stepOrder.getPrice());
            // 阶段单 X 子订单 分摊
            DivideStepPriceOption opt = new DivideStepPriceOption();
            opt.setAdjFee(true);
            opt.setLastModifiedStep(true);
            stepOrderCreateAbility.divideStepPriceToSubOrder(stepOrder, mainOrder, opt);
        }

        beforeSave(mainOrder, adj);

        // 保存
        final StepOrder finalStepOrder = stepOrder;
        transactionProxy.callTx(() -> saveAdjPrice(mainOrder, finalStepOrder));

        return Collections.singletonList(OrderChangeNotify.builder()
                .mainOrder(mainOrder)
                .op(OrderChangeOperateEnum.SELLER_CHANGE_FEE)
                .build());
    }

    protected void beforeSave(MainOrder mainOrder, AdjustPrice adj) {
        // 扩展, 可处理积分变动
    }

    protected void saveAdjPrice(MainOrder mainOrder, StepOrder stepOrder) {
        // 主订单
        TcOrderDO mainUp = new TcOrderDO();
        mainUp.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        mainUp.setOrderId(mainOrder.getPrimaryOrderId());
        mainUp.setVersion(mainOrder.getVersion());
        mainUp.setOrderFeeAttr(orderConverter.toOrderFeeAttrDO(mainOrder.getOrderPrice()));
        mainUp.setRealPrice(mainOrder.getOrderPrice().getOrderRealAmt());
        mainUp.setOrderAttr(mainOrder.orderAttr());
        boolean b1 = tcOrderRepository.updateByOrderIdVersion(mainUp);
        if (!b1) {
            throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
        }
        mainOrder.setVersion(mainUp.getVersion());


        // 阶段单
        if (stepOrder != null) {
            TcStepOrderDO stepUp = new TcStepOrderDO();
            stepUp.setPrimaryOrderId(stepOrder.getPrimaryOrderId());
            stepUp.setStepNo(stepOrder.getStepNo());
            stepUp.setVersion(stepOrder.getVersion());
            stepUp.setPriceAttr(orderConverter.toStepOrderFeeDO(stepOrder.getPrice()));
            boolean b2 = tcStepOrderRepository.updateByUkVersion(stepUp);
            if (!b2) {
                throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
            }
            stepOrder.setVersion(stepUp.getVersion());
        }

        // 子订单
        for (SubOrder subOrder : mainOrder.getSubOrders()) {
            TcOrderDO up = new TcOrderDO();
            up.setPrimaryOrderId(subOrder.getPrimaryOrderId());
            up.setOrderId(subOrder.getOrderId());
            up.setVersion(subOrder.getVersion());
            up.setOrderFeeAttr(orderConverter.toOrderFeeAttrDO(subOrder.getOrderPrice()));
            up.setRealPrice(subOrder.getOrderPrice().getOrderRealAmt());
            boolean b3 = tcOrderRepository.updateByOrderIdVersion(up);
            if (!b3) {
                throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
            }
            subOrder.setVersion(up.getVersion());
        }
    }

    protected void setAdjStepPrice(StepOrderPrice orderPrice, AdjustPrice adj, PointExchange ex) {
        if (adj.getAdjustRealAmt() != null) {
            orderPrice.setAdjustRealAmt(getNullZero(orderPrice.getAdjustRealAmt()) + adj.getAdjustRealAmt());
            orderPrice.setRealAmt(getNullZero(orderPrice.getRealAmt()) + adj.getAdjustRealAmt());
        }
        if (adj.getAdjustPointAmt() != null) {
            orderPrice.setAdjustPointAmt(getNullZero(orderPrice.getAdjustPointAmt()) + adj.getAdjustPointAmt());
            orderPrice.setPointAmt(getNullZero(orderPrice.getPointAmt()) + adj.getAdjustPointAmt());

            long adjPointCount = ex.exAmtToCount(Math.abs(adj.getAdjustPointAmt()));
            if (adj.getAdjustPointAmt() < 0) {
                adjPointCount = -adjPointCount;
            }

            orderPrice.setAdjustPointCount(getNullZero(orderPrice.getAdjustPointCount()) + adjPointCount);
            orderPrice.setPointCount(getNullZero(orderPrice.getPointCount()) + adjPointCount);
        }
    }

    protected void setAdjPrice(OrderPrice orderPrice, AdjustPrice adj, PointExchange ex) {
        if (adj.getAdjustRealAmt() != null) {
            orderPrice.setAdjustRealAmt(getNullZero(orderPrice.getAdjustRealAmt()) + adj.getAdjustRealAmt());
            orderPrice.setOrderRealAmt(getNullZero(orderPrice.getOrderRealAmt()) + adj.getAdjustRealAmt());
        }
        if (adj.getAdjustPointAmt() != null) {
            orderPrice.setAdjustPointAmt(getNullZero(orderPrice.getAdjustPointAmt()) + adj.getAdjustPointAmt());
            orderPrice.setPointAmt(getNullZero(orderPrice.getPointAmt()) + adj.getAdjustPointAmt());

            long adjPointCount = ex.exAmtToCount(Math.abs(adj.getAdjustPointAmt()));
            if (adj.getAdjustPointAmt() < 0) {
                adjPointCount = -adjPointCount;
            }

            orderPrice.setAdjustPointCount(getNullZero(orderPrice.getAdjustPointCount()) + adjPointCount);
            orderPrice.setPointCount(getNullZero(orderPrice.getPointCount()) + adjPointCount);
        }
        if (adj.getAdjustPromotionAmt() != null) {
            orderPrice.setAdjustPromotionAmt(getNullZero(orderPrice.getAdjustPromotionAmt()) + adj.getAdjustPromotionAmt());
            orderPrice.setOrderPromotionAmt(getNullZero(orderPrice.getOrderPromotionAmt()) + adj.getAdjustPromotionAmt());
        }
        if (adj.getAdjustFreightAmt() != null) {
            orderPrice.setAdjustFreightAmt(getNullZero(orderPrice.getAdjustFreightAmt()) + adj.getAdjustFreightAmt());
            orderPrice.setFreightAmt(getNullZero(orderPrice.getFreightAmt()) + adj.getAdjustFreightAmt());
        }
    }

    protected void divideSubOrder(MainOrder mainOrder, AdjustPrice adj) {
        long sumPointAmt = 0L;
        long sumPromotionAmt = 0L;
        long sumFreightAmt = 0L;
        List<SubPrice> allSubPrice = mainOrder.getAllSubPrice();
        for (SubPrice subOrder : allSubPrice) {
            OrderPrice subPrice = subOrder.getOrderPrice();
            sumPointAmt += getNullZero(subPrice.getPointAmt());
            sumPromotionAmt += getNullZero(subPrice.getOrderPromotionAmt());
            sumFreightAmt += getNullZero(subPrice.getFreightAmt());
        }

        Divider divPoint = null;
        Divider divPromotion = null;
//        Divider divFreight = null;
        if (adj.getAdjustPointAmt() != null) {
            divPoint = new Divider(adj.getAdjustPointAmt(), sumPointAmt, allSubPrice.size());
        }
        if (adj.getAdjustPromotionAmt() != null) {
            divPromotion = new Divider(adj.getAdjustPromotionAmt(), sumPromotionAmt, allSubPrice.size());
        }
//        if (adj.getAdjustFreightAmt() != null) {
//            divFreight = new Divider(adj.getAdjustFreightAmt(), sumFreightAmt, allSubPrice.size());
//        }

        PointExchange ex = mainOrder.orderAttr().getPointExchange();
        for (SubPrice subOrder : allSubPrice) {
            OrderPrice subPrice = subOrder.getOrderPrice();
            AdjustPrice subAdj = new AdjustPrice();
            if (divPoint != null) {
                subAdj.setAdjustPointAmt(divPoint.next(NumUtils.getNullZero(subPrice.getPointAmt())));
            }
            if (subOrder.isFreight() && adj.getAdjustFreightAmt() != null) {
                subAdj.setAdjustFreightAmt(adj.getAdjustFreightAmt());
            }
//            if (divFreight != null) {
//                subAdj.setAdjustFreightAmt(divFreight.next(NumUtils.getNullZero(subPrice.getFreightAmt())));
//            }
            if (divPromotion != null) {
                subAdj.setAdjustPromotionAmt(divPromotion.next(NumUtils.getNullZero(subPrice.getOrderPromotionAmt())));
            }
            setAdjPrice(subPrice, subAdj, ex);
            // 实付现金减出来
            long oldRealAmt = getNullZero(subPrice.getOrderRealAmt());
            long subRealAmt = getNullZero(subPrice.getFreightAmt())
                    + getNullZero(subPrice.getOrderPromotionAmt())
                    - getNullZero(subPrice.getPointAmt());
            if (subRealAmt != oldRealAmt) {
                subPrice.setOrderRealAmt(subRealAmt);
                subPrice.setAdjustRealAmt(getNullZero(subPrice.getAdjustRealAmt()) + (subRealAmt - oldRealAmt));
            }
            checkPositive(subPrice);
        }
    }

    protected void checkAdjPrice(MainOrder mainOrder, AdjustPrice adj) {
        // 调整金额检查
        if (getNullZero(adj.getAdjustPromotionAmt()) == 0 && getNullZero(adj.getAdjustFreightAmt()) == 0) {
            throw new GmallException(OrderErrorCode.ORDER_PRICE_CHANGE_ILLEGAL);
        }
        if (getNullZero(adj.getAdjustRealAmt()) == 0 && getNullZero(adj.getAdjustPointAmt()) == 0) {
            throw new GmallException(OrderErrorCode.ORDER_PRICE_CHANGE_ILLEGAL);
        }
        if (getNullZero(adj.getAdjustPromotionAmt()) + getNullZero(adj.getAdjustFreightAmt())
                != getNullZero(adj.getAdjustRealAmt()) + getNullZero(adj.getAdjustPointAmt())) {
            throw new GmallException(OrderErrorCode.ORDER_PRICE_CHANGE_ILLEGAL);
        }

        // 订单状态检查
        OrderStatusEnum orderStatus = OrderStatusEnum.codeOf(mainOrder.getPrimaryOrderStatus());
        if (OrderTypeEnum.codeOf(mainOrder.getOrderType()) == OrderTypeEnum.MULTI_STEP_ORDER) {
            StepOrder stepOrder = StepOrderUtils.getStepOrder(mainOrder, adj.getStepNo());
            if (stepOrder == null) {
                throw new GmallException(OrderErrorCode.ORDER_PRICE_CHANGE_ILLEGAL);
            }
            if (orderStatus != OrderStatusEnum.ORDER_WAIT_PAY && orderStatus != OrderStatusEnum.STEP_ORDER_DOING &orderStatus != OrderStatusEnum.PARTIALLY_PAID) {
                throw new GmallException(OrderErrorCode.ORDER_STATUS_ILLEGAL);
            }
        } else {
            if (orderStatus != OrderStatusEnum.ORDER_WAIT_PAY) {
                throw new GmallException(OrderErrorCode.ORDER_STATUS_ILLEGAL);
            }
        }
    }

    protected void checkPositive(OrderPrice orderPrice) {
        if (getNullZero(orderPrice.getPointAmt()) < 0
                || getNullZero(orderPrice.getOrderRealAmt()) < 0
                || getNullZero(orderPrice.getOrderPromotionAmt()) < 0
                || getNullZero(orderPrice.getFreightAmt()) < 0
                || getNullZero(orderPrice.getTotalAmt()) <= 0) {
            throw new GmallException(OrderErrorCode.ORDER_PRICE_CHANGE_ILLEGAL);
        }
    }

    protected void checkPositive(StepOrderPrice orderPrice) {
        if (getNullZero(orderPrice.getPointAmt()) < 0
                || getNullZero(orderPrice.getOrderRealAmt()) < 0
                || getNullZero(orderPrice.getTotalAmt()) <= 0) {
            throw new GmallException(OrderErrorCode.ORDER_PRICE_CHANGE_ILLEGAL);
        }
    }
}
