package com.aliyun.gts.gmall.platform.trade.core.extension.search.defaultimpl;

import java.util.ArrayList;
import java.util.List;

import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.OrderDetailQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.common.constants.PrimaryOrderFlagEnum;
import com.aliyun.gts.gmall.platform.trade.core.convertor.OrderConverter;
import com.aliyun.gts.gmall.platform.trade.core.extension.search.OrderDetailExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultOrderDetailExt implements OrderDetailExt {

    @Autowired
    TcOrderRepository tcOrderRepository;

    @Autowired
    OrderConverter orderConverter;

    @Override
    public TradeBizResult<MainOrder> queryOrder(OrderDetailQueryRpcReq req) {
        List<TcOrderDO> list = tcOrderRepository.queryBoughtDetailByPrimaryId(req.getPrimaryOrderId());
        if(list == null || list.size() == 0){
            return TradeBizResult.fail(OrderErrorCode.ORDER_NOT_EXISTS.getCode(),
                OrderErrorCode.ORDER_NOT_EXISTS.getMessage());
        }
        List<SubOrder> subOrders = new ArrayList<>();
        MainOrder mainOrder = new MainOrder();
        for(TcOrderDO tcOrderDO : list){
            if(tcOrderDO.getPrimaryOrderFlag().intValue() == PrimaryOrderFlagEnum.PRIMARY_ORDER.getCode()){
                mainOrder = orderConverter.convertMainOrder(tcOrderDO);
            }else{
                SubOrder subOrder = orderConverter.convertSubOrder(tcOrderDO);
                subOrders.add(subOrder);
            }
        }

        mainOrder.setSubOrders(subOrders);
        return TradeBizResult.ok(mainOrder);
    }
}
