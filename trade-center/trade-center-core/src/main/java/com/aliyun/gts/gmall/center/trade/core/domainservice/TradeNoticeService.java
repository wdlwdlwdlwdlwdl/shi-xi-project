package com.aliyun.gts.gmall.center.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.pay.api.dto.message.PaySuccessMessage;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;

/**
 * 交易事件提醒
 */
public interface TradeNoticeService {

    void onOrderCreate(MainOrder mainOrder);

    void onOrderPaid(PaySuccessMessage message);

    void onReversalCreate(long primaryReversalId);
}
