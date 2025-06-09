package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.CustDelOrderCheckAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderInventoryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderStatusAbility;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderChangedNotifyService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderTaskService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.PayOrderStatusService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.StepOrderDomainService;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.core.task.biz.AutoEvaluateOrderTask;
import com.aliyun.gts.gmall.platform.trade.core.task.biz.SystemConfirmOrderTask;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;
import com.aliyun.gts.gmall.platform.trade.domain.repository.OrderPayRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PayOrderStatusServiceImpl implements PayOrderStatusService {

    @Autowired
    OrderStatusAbility orderStatusAbility;
    @Autowired
    TcOrderRepository tcOrderRepository;
    @Autowired
    CustDelOrderCheckAbility custDelOrderCheckAbility;
    @Autowired
    AutoEvaluateOrderTask autoEvaluateOrderTask;
    @Autowired
    SystemConfirmOrderTask systemConfirmOrderTask;
    @Autowired
    private OrderTaskService orderTaskService;
    @Autowired
    private OrderInventoryAbility orderInventoryAbility;
    @Autowired
    private OrderQueryAbility orderQueryAbility;
    @Autowired
    private OrderChangedNotifyService orderChangedNotifyService;
    @Autowired
    private StepOrderDomainService stepOrderDomainService;
    @Autowired
    private OrderPayRepository orderPayRepository;

    public TradeBizResult<List<TcOrderDO>> changeOrderStatus(OrderStatus orderStatus) {
        //顾客取消订单实现
        MainOrder mainOrder = orderQueryAbility.getMainOrder(orderStatus.getPrimaryOrderId());
        if (null == mainOrder) {
            log.info("Error input PrimaryOrderId " + orderStatus.getPrimaryOrderId().toString() + ",no data was found.");
            throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
        }
        OrderAttrDO orderAttrDO = mainOrder.getOrderAttr();
        orderAttrDO.setReasonCode("");
        TcOrderDO newTcorderDO = new TcOrderDO();
        newTcorderDO.setOrderAttr(orderAttrDO);
        newTcorderDO.setPrimaryOrderId(orderStatus.getPrimaryOrderId());
        newTcorderDO.setOrderId(orderStatus.getPrimaryOrderId());
        newTcorderDO.setVersion(mainOrder.getVersion());
        boolean success = tcOrderRepository.updateByOrderIdVersion(newTcorderDO);
        if (!success) {
            throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
        }
        return orderStatusAbility.changeStatus(orderStatus, BizCodeEntity.buildWithDefaultBizCode(mainOrder));
    }

}
