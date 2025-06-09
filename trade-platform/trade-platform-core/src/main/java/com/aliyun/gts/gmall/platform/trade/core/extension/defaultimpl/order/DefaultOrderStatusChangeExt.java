package com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order;

import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.OrderStatusChangeExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DefaultOrderStatusChangeExt implements OrderStatusChangeExt {

    @Autowired
    private  TcOrderRepository tcOrderRepository;

    @Override
    public TradeBizResult<List<TcOrderDO>> orderStatusChange(OrderStatus orderStatus) {
        /**
         * 更新主订单和子订单状态、stage
         * checkStatus、stage 可空
         */
        tcOrderRepository.updateStatusAndStageByPrimaryId(
            orderStatus.getPrimaryOrderId(),
            orderStatus.getStatus().getCode(),
            orderStatus.getOrderStage(),
            orderStatus.getCheckStatus().getCode()
        );
        List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryId(orderStatus.getPrimaryOrderId());
        return TradeBizResult.ok(list);
    }

    @Override
    public Integer getOrderStatusOnPaySuccess(MainOrder mainOrder, OrderPay orderPay) {
        return OrderStatusEnum.PAYMENT_CONFIRMED.getCode();
    }
}
