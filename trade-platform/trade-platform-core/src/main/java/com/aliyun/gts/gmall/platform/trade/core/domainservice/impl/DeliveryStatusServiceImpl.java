package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.common.constants.LogisticsTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderTaskAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.CustDelOrderCheckAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.DeliveryStatusAbility;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.DeliveryStatusService;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcTimeoutSettingDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SalesAddr;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.TimeoutSettingQuery;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TimeoutSettingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class DeliveryStatusServiceImpl implements DeliveryStatusService {

    @Autowired
    private DeliveryStatusAbility deliveryStatusAbility;

    @Autowired
    private TcOrderRepository tcOrderRepository;

    @Autowired
    private CustDelOrderCheckAbility custDelOrderCheckAbility;

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private TimeoutSettingRepository timeoutSettingRepository;

    @Autowired
    private OrderTaskAbility orderTaskAbility;

    @Override
    @Transactional
    public TradeBizResult<List<TcOrderDO>> changeOrderStatus(OrderStatus orderStatus) {
        MainOrder mainOrder = orderQueryAbility.getMainOrder(orderStatus.getPrimaryOrderId());
        if (null == mainOrder) {
            log.info("Error input PrimaryOrderId " + orderStatus.getPrimaryOrderId().toString() + ",no data was found.");
            throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
        }
        //判断物流方式
        OrderAttrDO orderAttrDO = mainOrder.getOrderAttr();
        SalesAddr salesAddr = mainOrder.getSales();
        //PVZ Postamat 物流方式
        if(Objects.equals(orderAttrDO.getLogisticsType(), LogisticsTypeEnum.PVZ.getCode()) ||
            Objects.equals(orderAttrDO.getLogisticsType(), LogisticsTypeEnum.POSTAMAT.getCode())){
            if (Objects.equals(orderStatus.getStatus(), OrderStatusEnum.DELIVERY_TO_DC) ||
                Objects.equals(orderStatus.getStatus(), OrderStatusEnum.WAITING_FOR_COURIER)){
                orderStatus.setCheckStatus(OrderStatusEnum.ACCEPTED_BY_MERCHANT);
            }
            else if (Objects.equals(orderStatus.getStatus(), OrderStatusEnum.DELIVERY)){
               /* if(salesAddr.getIsDc().equals(SalesAddr.IS_DC)){
                    orderStatus.setCheckStatus(OrderStatusEnum.DELIVERY_TO_DC);
                } else {
                    orderStatus.setCheckStatus(OrderStatusEnum.WAITING_FOR_COURIER);
                }*/
                //新的物流逻辑
                orderStatus.setCheckStatus(OrderStatusEnum.WAITING_FOR_COURIER);
            }
            else if (Objects.equals(orderStatus.getStatus(),OrderStatusEnum.READY_FOR_PICKUP)){
                orderStatus.setCheckStatus(OrderStatusEnum.DELIVERY);
                Date scheduleTime = getScheduleTime(orderStatus);
                orderAttrDO.setPickUpDeadTime(scheduleTime);
            }
            else if (Objects.equals(orderStatus.getStatus(),OrderStatusEnum.COMPLETED)){
                orderStatus.setCheckStatus(OrderStatusEnum.READY_FOR_PICKUP);
                orderAttrDO.setConfirmReceiveTime(new Date());
            }
            else if (Objects.equals(orderStatus.getStatus(),OrderStatusEnum.RETURNING_TO_MERCHANT)){
                orderStatus.setCheckStatus(OrderStatusEnum.READY_FOR_PICKUP);
            }
            else if (Objects.equals(orderStatus.getStatus(), OrderStatusEnum.CANCEL_REQUESTED)){
                orderStatus.setCheckStatus(OrderStatusEnum.RETURNING_TO_MERCHANT);
                //营业厅 自提柜超时 商品返回商家，物流状态变成CANCELLED 自动退款
                orderStatus.setRefund(true);
            }
        }
        // courier to door 物流方式
        if(Objects.equals(orderAttrDO.getLogisticsType(), LogisticsTypeEnum.COURIER_LODOOR_HM.getCode())) {
            if(Objects.equals(orderStatus.getStatus(),OrderStatusEnum.DELIVERY_TO_DC) ||
                Objects.equals(orderStatus.getStatus(),OrderStatusEnum.WAITING_FOR_COURIER)){
                orderStatus.setCheckStatus(OrderStatusEnum.ACCEPTED_BY_MERCHANT);
            } else {
                // 状态校验
                checkStatus(orderStatus, salesAddr, orderAttrDO);
            }
        }
        //添加验证 如果和checkstatus不一致就返回异常
        if(Objects.equals(orderStatus.getCheckStatus(), mainOrder.getPrimaryOrderStatus())){
            throw new GmallException(OrderErrorCode.ORDER_STATUS_ERROR);
        }
        // 更新订单扩展字符安
        updateOrderAttr(orderAttrDO,orderStatus.getPrimaryOrderId());
        // 状态修改
        return deliveryStatusAbility.changeStatus(orderStatus, BizCodeEntity.buildWithDefaultBizCode(mainOrder));
    }

    /**
     *
     * @param orderStatus
     * @return
     */
    private Date getScheduleTime(OrderStatus orderStatus) {
        TimeoutSettingQuery setting = new TimeoutSettingQuery();
        setting.setOrderStatus(orderStatus.getStatus().getCode());
        TcTimeoutSettingDO tcTimeoutSettingDO = timeoutSettingRepository.queryTimeoutSetting(setting);
        Date scheduleTime =null;
        if(tcTimeoutSettingDO != null ){
            if(tcTimeoutSettingDO.getTimeType() == 0){
                //分
                scheduleTime = new Date(System.currentTimeMillis() + 60*1000L * Long.parseLong(tcTimeoutSettingDO.getTimeRule()));
            }else if(tcTimeoutSettingDO.getTimeType() == 1){
                //时
                scheduleTime = new Date(System.currentTimeMillis() + 60*60*1000L * Long.parseLong(tcTimeoutSettingDO.getTimeRule()));
            }else{
                //天
                scheduleTime = new Date(System.currentTimeMillis() + 60*60*24*1000L * Long.parseLong(tcTimeoutSettingDO.getTimeRule()));
            }
        }
        return scheduleTime;
    }

    private void updateOrderAttr(OrderAttrDO orderAttrDO,Long primaryOrderId){
        TcOrderDO tcOrderDO = new TcOrderDO();
        tcOrderDO.setOrderAttr(orderAttrDO);
        tcOrderDO.setPrimaryOrderId(primaryOrderId);
        tcOrderDO.setOrderId(primaryOrderId);
        tcOrderDO.setCancelCode(orderAttrDO.getReasonCode());
        tcOrderRepository.updateByOrderIdVersion(tcOrderDO);
    }

    /**
     * 状态校验
     * @param orderStatus
     * @param salesAddr
     * @param orderAttrDO
     */
    private void checkStatus(OrderStatus orderStatus, SalesAddr salesAddr, OrderAttrDO orderAttrDO) {
        if(Objects.equals(orderStatus.getStatus(), OrderStatusEnum.DELIVERY)){
           /* if(salesAddr.getIsDc().equals(SalesAddr.IS_DC)){
                orderStatus.setCheckStatus(OrderStatusEnum.DELIVERY_TO_DC);
            } else {
                orderStatus.setCheckStatus(OrderStatusEnum.WAITING_FOR_COURIER);
            }*/
            orderStatus.setCheckStatus(OrderStatusEnum.WAITING_FOR_COURIER);
        }else if(Objects.equals(orderStatus.getStatus(),OrderStatusEnum.COMPLETED)){
            orderAttrDO.setConfirmReceiveTime(new Date());
            orderStatus.setCheckStatus(OrderStatusEnum.DELIVERY);
        }else if(Objects.equals(orderStatus.getStatus(),OrderStatusEnum.RETURNING_TO_MERCHANT)){
            orderStatus.setCheckStatus(OrderStatusEnum.DELIVERY);
        }else if(Objects.equals(orderStatus.getStatus(),OrderStatusEnum.CANCEL_REQUESTED)){
            orderStatus.setCheckStatus(OrderStatusEnum.RETURNING_TO_MERCHANT);
            //顾客拒绝商品 商品返回商家，物流状态变成CANCEL_REQUESTED 自动退款
            orderStatus.setRefund(true);
        }
    }

}
