package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.pay.api.dto.message.PaySuccessMessage;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.PayChannel;

import java.util.Map;

public interface PayQueryService {

    OrderPay getOrderPay(PaySuccessMessage message);

    OrderPay queryByOrder(MainOrder mainOrder);

    OrderPay queryByOrder(Long primaryOrderId, Long custId, Integer stepNo);

    Map<String, PayChannel> getPayChannels();
}
