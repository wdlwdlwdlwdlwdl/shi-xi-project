package com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderSeparateFeeAbility;
import com.aliyun.gts.gmall.platform.trade.core.convertor.OrderConverter;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.OrderSuccessExt;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcStepOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderFeeAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.StepOrderFeeDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcStepOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultOrderSuccessExt implements OrderSuccessExt {

    @Autowired
    private TcOrderRepository tcOrderRepository;
    @Autowired
    private OrderSeparateFeeAbility orderSeparateFeeAbility;
    @Autowired
    private OrderConverter orderConverter;
    @Autowired
    private TcStepOrderRepository tcStepOrderRepository;

    @Override
    public void processOrderSuccess(MainOrder mainOrder) {
        // 计算OrderPrice.ConfirmPrice
        orderSeparateFeeAbility.storeConfirmPrice(mainOrder);
        // 保存主单
        OrderFeeAttrDO mainFee = orderConverter.toOrderFeeAttrDO(mainOrder.getOrderPrice());
        TcOrderDO mainUp = new TcOrderDO();
        mainUp.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        mainUp.setOrderId(mainOrder.getPrimaryOrderId());
        mainUp.setVersion(mainOrder.getVersion());
        mainUp.setOrderFeeAttr(mainFee);
        boolean success = tcOrderRepository.updateByOrderIdVersion(mainUp);
        if (!success) {
            throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
        }
        mainOrder.setVersion(mainUp.getVersion());
        // 保存子单
        for (SubOrder subOrder : mainOrder.getSubOrders()) {
            OrderFeeAttrDO subFee = orderConverter.toOrderFeeAttrDO(subOrder.getOrderPrice());
            TcOrderDO subUp = new TcOrderDO();
            subUp.setPrimaryOrderId(subOrder.getPrimaryOrderId());
            subUp.setOrderId(subOrder.getOrderId());
            subUp.setVersion(subOrder.getVersion());
            subUp.setOrderFeeAttr(subFee);
            boolean subSuccess = tcOrderRepository.updateByOrderIdVersion(subUp);
            if (!subSuccess) {
                throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
            }
            subOrder.setVersion(subUp.getVersion());
        }
        // 保存阶段单
        if (mainOrder.getStepOrders() != null) {
            for (StepOrder stepOrder : mainOrder.getStepOrders()) {
                StepOrderFeeDO stepFee = orderConverter.toStepOrderFeeDO(stepOrder.getPrice());
                TcStepOrderDO stepUp = new TcStepOrderDO();
                stepUp.setPrimaryOrderId(stepOrder.getPrimaryOrderId());
                stepUp.setStepNo(stepOrder.getStepNo());
                stepUp.setVersion(stepOrder.getVersion());
                stepUp.setPriceAttr(stepFee);
                boolean stepSuccess = tcStepOrderRepository.updateByUkVersion(stepUp);
                if (!stepSuccess) {
                    throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
                }
                stepOrder.setVersion(stepUp.getVersion());
            }
        }
    }
}
