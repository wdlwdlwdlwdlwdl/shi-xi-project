package com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order;

import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.CustomerDeleteOrderPreCheckExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefalutCustomerDeleteOrderPreCheckExt implements CustomerDeleteOrderPreCheckExt {

    @Autowired
    TcOrderRepository tcOrderRepository;

    /**
     * 订单终态的最小值
     * @see OrderStatusEnum
     */
    static final int ORDER_FINAL_STATUS  =30;

    @Override
    public TradeBizResult<Boolean> check(Long primaryOrderId) {
        TcOrderDO tcOrderDO = tcOrderRepository.queryPrimaryByOrderId(primaryOrderId);
        if(tcOrderDO != null && tcOrderDO.getOrderStatus() > ORDER_FINAL_STATUS){
            return TradeBizResult.ok(true);
        }else{
            return TradeBizResult.fail(OrderErrorCode.ORDER_STATUS_ILLEGAL.getCode(),
                OrderErrorCode.ORDER_STATUS_ILLEGAL.getMessage());
        }
    }

    @Override
    public TradeBizResult<Boolean> check(Long primaryOrderId, Long custId) {
        TcOrderDO tcOrderDO = tcOrderRepository.queryPrimaryByOrderId(primaryOrderId);
        if(tcOrderDO != null && tcOrderDO.getOrderStatus() > ORDER_FINAL_STATUS){
            if (custId != null && !custId.equals(tcOrderDO.getCustId())) {
                return TradeBizResult.fail(CommonErrorCode.NOT_DATA_OWNER);
            }
            return TradeBizResult.ok(true);
        }else{
            return TradeBizResult.fail(OrderErrorCode.ORDER_STATUS_ILLEGAL.getCode(),
                    OrderErrorCode.ORDER_STATUS_ILLEGAL.getMessage());
        }
    }
}
