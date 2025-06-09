package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.pay.api.dto.message.PaySuccessMessage;
import com.aliyun.gts.gmall.platform.pay.api.dto.message.inner.PayFlowMessage;
import com.aliyun.gts.gmall.platform.pay.api.enums.PayStatusEnum;
import com.aliyun.gts.gmall.platform.pay.api.enums.PayTypeEnum;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.PayQueryService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.PayChannel;
import com.aliyun.gts.gmall.platform.trade.domain.repository.OrderPayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PayQueryServiceImpl implements PayQueryService {

    @Autowired
    private OrderPayRepository orderPayRepository;

    @Override
    public OrderPay getOrderPay(PaySuccessMessage message) {
        PayFlowMessage flow;
        if (message.getPayFlows().size() == 1) {
            flow = message.getPayFlows().get(0);
        } else {
            flow = message.getPayFlows().stream()
                    .filter(pf -> !PayTypeEnum.ASSERTS_PAY.getCode().equals(pf.getPayType()))
                    .findFirst().orElse(null);
        }
        if (flow == null) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG, I18NMessageUtils.getMessage("pay.channel.invalid"));  //# "支付渠道错误"
        }

        OrderPay orderPay = new OrderPay();
        orderPay.setPayId(message.getPayId());
        orderPay.setPrimaryOrderId(message.getPrimaryOrderId());
        orderPay.setCustId(message.getCustId());
        orderPay.setStepNo(message.getStepNo());
        orderPay.setPayChannel(flow.getPayChannel());
        orderPay.setPayType(flow.getPayType());
        orderPay.setPayTime(message.getPayTime());
        orderPay.setPayStatus(PayStatusEnum.PAID.getCode());
        orderPay.setBizTags(message.getPayBizTags());
        orderPay.setBizFeature(message.getPayBizFeature());
        return orderPay;
    }

    @Override
    public OrderPay queryByOrder(MainOrder mainOrder) {
        String key = "__gm_orderPay";
        OrderPay cache = (OrderPay) mainOrder.getExtra(key);
        if (cache != null) {
            return cache;
        }
        cache = queryByOrder(
            mainOrder.getPrimaryOrderId(),
            mainOrder.getCustomer().getCustId(),
            mainOrder.orderAttr().getCurrentStepNo()
        );
        mainOrder.putExtra(key, cache);
        return cache;
    }

    @Override
    public OrderPay queryByOrder(Long primaryOrderId, Long custId, Integer stepNo) {
        return orderPayRepository.queryByUk(primaryOrderId, custId, stepNo);
    }

    @Override
    public Map<String, PayChannel> getPayChannels() {
        return null;
    }
}
