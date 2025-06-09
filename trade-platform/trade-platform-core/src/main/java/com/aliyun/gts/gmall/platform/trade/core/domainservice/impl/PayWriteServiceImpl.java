package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.StepOrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.PayQueryService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.PayWriteService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcStepOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderPayInfo;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CacheRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcStepOrderRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.StepOrderUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class PayWriteServiceImpl implements PayWriteService {

    @Autowired
    private TcStepOrderRepository tcStepOrderRepository;
    @Autowired
    private TcOrderRepository tcOrderRepository;
    @Autowired
    private CacheRepository cacheRepository;

    @Value("${trade.pay.pointpay.channelCode:}")
    private String pointChannelCode;

    /**
     * 旧逻辑 被 PayWriteServiceImplExt 扩展
     * @param mainOrder
     * 2025-3-22 10:38:39
     */
    @Override
    public void checkToPay(MainOrder mainOrder) {
        if (mainOrder == null) {
            throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
        }
        boolean isWaitPay = OrderStatusEnum.ORDER_WAIT_PAY.getCode().equals(mainOrder.getPrimaryOrderStatus());
        boolean isStepWaitPay = (OrderStatusEnum.STEP_ORDER_DOING.getCode().equals(mainOrder.getPrimaryOrderStatus()) ||
            OrderStatusEnum.PARTIALLY_PAID.getCode().equals(mainOrder.getPrimaryOrderStatus())) &&
            StepOrderStatusEnum.STEP_WAIT_PAY.getCode().equals(mainOrder.getCurrentStepOrder().getStatus());
        if (!isWaitPay && !isStepWaitPay) {
            throw new GmallException(OrderErrorCode.ORDER_STATUS_ILLEGAL);
        }
    }

    /**
     * 校验保存支付渠道
     * @param mainOrder
     */
    @Override
    public void checkSavePayChannel(MainOrder mainOrder, String payChannel) {
        OrderPayInfo payInfo = mainOrder.getCurrentPayInfo();
        String oldPayChannel = payInfo.getPayChannel();
        // 支付渠道相同
        if (StringUtils.isBlank(payChannel) || StringUtils.equals(payChannel, oldPayChannel)) {
            return;
        }
        // 纯积分抵扣 payChannel 不替换
        if (StringUtils.equals(pointChannelCode, oldPayChannel)) {
            return;
        }
        // 修改 payChannel
        if (StepOrderUtils.isMultiStep(mainOrder)) {
            // 多步骤订单
            StepOrder step = mainOrder.getCurrentStepOrder();
            step.features().setPayChannel(payChannel);
            TcStepOrderDO up = new TcStepOrderDO();
            up.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
            up.setStepNo(step.getStepNo());
            up.setVersion(step.getVersion());
            up.setFeatures(step.getFeatures());
            assertUpdated(tcStepOrderRepository.updateByUkVersion(up));
            step.setVersion(up.getVersion());
        } else {
            // 订单
            mainOrder.orderAttr().setPayChannel(payChannel);
            TcOrderDO up = new TcOrderDO();
            up.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
            up.setOrderId(mainOrder.getPrimaryOrderId());
            up.setVersion(mainOrder.getVersion());
            up.setOrderAttr(mainOrder.getOrderAttr());
            assertUpdated(tcOrderRepository.updateByOrderIdVersion(up));
            mainOrder.setVersion(up.getVersion());
        }
    }

    /**
     * 构建一次性token
     * @param custId
     * @param cartId
     * @return
     */
    @Override
    public String generatePayToken(Long custId, String cartId) {
        JSONObject token = new JSONObject();
        token.put("custId", custId);
        token.put("cartId", cartId);
        String uuid = UUID.randomUUID().toString();
        cacheRepository.put(uuid, token, 10, TimeUnit.MINUTES);
        return uuid;
    }

    /**
     * 验证一次性token
     * @param token
     * @return
     */
    @Override
    public String getPayToken(String token) {
        String jsonStr = cacheRepository.get(token);
        cacheRepository.delete(token);
        return jsonStr;
    }

    private void assertUpdated(boolean b) {
        if (!b) {
            throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
        }
    }
}
