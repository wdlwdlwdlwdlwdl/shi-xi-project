package com.aliyun.gts.gmall.center.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.center.trade.api.dto.input.EvoucherModifyRpcReq;
import com.aliyun.gts.gmall.center.trade.common.constants.EvoucherStatusEnum;
import com.aliyun.gts.gmall.center.trade.core.constants.EvoucherErrorCode;
import com.aliyun.gts.gmall.center.trade.core.domainservice.EvoucherProcessService;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.evoucher.EvoucherFeatureDO;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.evoucher.TcEvoucherDO;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.TcEvoucherRepository;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.PrimaryOrderFlagEnum;
import com.aliyun.gts.gmall.platform.trade.common.util.OrderIdUtils;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderChangedNotifyService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeNotify;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeOperateEnum;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class EvoucherProcessServiceImpl implements EvoucherProcessService {

    @Autowired
    private TcEvoucherRepository tcEvoucherRepository;
    @Autowired
    private TcOrderRepository tcOrderRepository;
    @Autowired
    private OrderChangedNotifyService orderChangedNotifyService;

    @Override
    @Transactional
    public void writeOff(EvoucherModifyRpcReq req) {
        TcEvoucherDO ev = tcEvoucherRepository.queryByEvCode(req.getEvCode());
        WriteOffContext c = new WriteOffContext();
        c.operatorId = req.getOperatorId();
        c.operatorName = req.getOperatorName();
        checkForWriteOff(ev, c);
        doWriteOff(ev, c);
    }

    private void checkForWriteOff(TcEvoucherDO ev, WriteOffContext c) {
        // 不存在
        if (ev == null) {
            throw new GmallException(EvoucherErrorCode.EV_NOT_EXIST);
        }
        // 状态
        EvoucherStatusEnum status = EvoucherStatusEnum.codeOf(ev.getStatus());
        if (status == EvoucherStatusEnum.WRITE_OFF) {
            throw new GmallException(EvoucherErrorCode.ALREADY_WRITE_OFF);
        } else if (status != EvoucherStatusEnum.NOT_USED) {
            throw new GmallException(EvoucherErrorCode.STATUS_ILLEGAL);
        }
        // 有效期
        long now = System.currentTimeMillis();
        if (ev.getStartTime() != null && ev.getStartTime().getTime() > now) {
            throw new GmallException(EvoucherErrorCode.TIME_NOT_AVAILABLE);
        }
        if (ev.getEndTime() != null && ev.getEndTime().getTime() < now) {
            throw new GmallException(EvoucherErrorCode.TIME_NOT_AVAILABLE);
        }
        // 订单校验
        Long primaryOrderId = OrderIdUtils.getPrimaryOrderIdByOrderId(ev.getOrderId());
        TcOrderDO order = tcOrderRepository.querySubByOrderId(primaryOrderId, ev.getOrderId());
        c.order = order;
        if (order == null) {
            throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
        }
        // 订单状态
        OrderStatusEnum orderStatus = OrderStatusEnum.codeOf(order.getOrderStatus());
        if (orderStatus != OrderStatusEnum.ORDER_SENDED) {
            throw new GmallException(OrderErrorCode.ORDER_STATUS_ILLEGAL);
        }
    }

    private void doWriteOff(TcEvoucherDO ev, WriteOffContext c) {
        // 更新电子凭证状态
        EvoucherFeatureDO features = ev.getFeaturesDO();
        features.setWriteOffTime(new Date());
        features.setWriteOffOpId(c.operatorId);
        features.setWriteOffOpName(c.operatorName);

        TcEvoucherDO up = new TcEvoucherDO();
        up.setEvCode(ev.getEvCode());
        up.setVersion(ev.getVersion());
        up.setStatus(EvoucherStatusEnum.WRITE_OFF.getCode());
        up.setFeaturesDO(features);
        boolean success = tcEvoucherRepository.updateByCodeVersion(up);
        if (!success) {
            throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
        }

        // 处理全部用完
        afterUsed(c.order);
    }

    private void afterUsed(TcOrderDO order) {
        // 是否全部已核销
        boolean allEvFinish = true;
        List<TcEvoucherDO> evList = tcEvoucherRepository.queryByOrderId(order.getOrderId());
        for (TcEvoucherDO otherEv : evList) {
            EvoucherStatusEnum status = EvoucherStatusEnum.codeOf(otherEv.getStatus());
            if (status == EvoucherStatusEnum.NOT_USED) {
                allEvFinish = false;
            }
        }
        if (!allEvFinish) {
            return;
        }

        // 子单状态更新
        confirmReceiveSubOrder(order);

        // 是否所有子单完成
        boolean allOrderFinish = true;
        List<TcOrderDO> orders = tcOrderRepository.queryOrdersByPrimaryId(order.getPrimaryOrderId());
        for (TcOrderDO otherOrder : orders) {
            OrderStatusEnum status = OrderStatusEnum.codeOf(otherOrder.getOrderStatus());
            PrimaryOrderFlagEnum flag = PrimaryOrderFlagEnum.codeOf(otherOrder.getPrimaryOrderFlag());
            if (flag == PrimaryOrderFlagEnum.SUB_ORDER &&
                    (status == OrderStatusEnum.ORDER_SENDED || status == OrderStatusEnum.REVERSAL_DOING)) {
                allOrderFinish = false;
            }
        }
        if (allOrderFinish) {
            confirmReceiveMainOrder(orders, order.getPrimaryOrderId());
        }
    }

    // 子单所有凭证 核销完毕
    private void confirmReceiveSubOrder(TcOrderDO order) {
        TcOrderDO orderUp = new TcOrderDO();
        orderUp.setPrimaryOrderId(order.getPrimaryOrderId());
        orderUp.setOrderId(order.getOrderId());
        orderUp.setVersion(order.getVersion());
        orderUp.setOrderStatus(OrderStatusEnum.ORDER_CONFIRM.getCode());
        orderUp.setOrderAttr(order.getOrderAttr());
        orderUp.getOrderAttr().setConfirmReceiveTime(new Date());
        boolean success = tcOrderRepository.updateByOrderIdVersion(orderUp);
        if (!success) {
            throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
        }

        // 订单变更通知
        TcOrderDO after = tcOrderRepository.querySubByOrderId(order.getPrimaryOrderId(), order.getOrderId());
        orderChangedNotifyService.afterStatusChange(OrderChangeNotify.builder()
                .orderList(Arrays.asList(after))
                .op(OrderChangeOperateEnum.CUST_CONFIRM_RECEIVE)
                .build());
    }

    // 所有子单 核销完毕
    private void confirmReceiveMainOrder(List<TcOrderDO> orders, Long primaryOrderId) {
        for (TcOrderDO order : orders) {
            TcOrderDO orderUp = new TcOrderDO();
            orderUp.setPrimaryOrderId(order.getPrimaryOrderId());
            orderUp.setOrderId(order.getOrderId());
            orderUp.setVersion(order.getVersion());
            orderUp.setPrimaryOrderStatus(OrderStatusEnum.ORDER_CONFIRM.getCode());

            PrimaryOrderFlagEnum flag = PrimaryOrderFlagEnum.codeOf(order.getPrimaryOrderFlag());
            if (flag == PrimaryOrderFlagEnum.PRIMARY_ORDER) {
                orderUp.setOrderStatus(OrderStatusEnum.ORDER_CONFIRM.getCode());
                orderUp.setOrderAttr(order.getOrderAttr());
                orderUp.getOrderAttr().setConfirmReceiveTime(new Date());
            }

            boolean success = tcOrderRepository.updateByOrderIdVersion(orderUp);
            if (!success) {
                throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
            }

        }

        // 订单变更通知
        TcOrderDO after = tcOrderRepository.queryPrimaryByOrderId(primaryOrderId);
        orderChangedNotifyService.afterStatusChange(OrderChangeNotify.builder()
                .orderList(Arrays.asList(after))
                .op(OrderChangeOperateEnum.CUST_CONFIRM_RECEIVE)
                .build());
    }

    private static class WriteOffContext {
        private TcOrderDO order;
        private Long operatorId;
        private String operatorName;
    }

    @Override
    @Transactional
    public void makeDisabled(Long evCode) {
        // 校验
        TcEvoucherDO ev = tcEvoucherRepository.queryByEvCode(evCode);
        if (ev == null) {
            throw new GmallException(EvoucherErrorCode.EV_NOT_EXIST);
        }
        EvoucherStatusEnum status = EvoucherStatusEnum.codeOf(ev.getStatus());
        if (status != EvoucherStatusEnum.NOT_USED) {
            throw new GmallException(EvoucherErrorCode.STATUS_ILLEGAL);
        }
        Long primaryOrderId = OrderIdUtils.getPrimaryOrderIdByOrderId(ev.getOrderId());
        TcOrderDO order = tcOrderRepository.querySubByOrderId(primaryOrderId, ev.getOrderId());
        if (order == null) {
            throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
        }

        // 保存
        EvoucherFeatureDO features = ev.getFeaturesDO();
        features.setDisabledTime(new Date());
        TcEvoucherDO up = new TcEvoucherDO();
        up.setEvCode(ev.getEvCode());
        up.setVersion(ev.getVersion());
        up.setStatus(EvoucherStatusEnum.DISABLED.getCode());
        up.setFeaturesDO(features);
        boolean success = tcEvoucherRepository.updateByCodeVersion(up);
        if (!success) {
            throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
        }
    }
}
