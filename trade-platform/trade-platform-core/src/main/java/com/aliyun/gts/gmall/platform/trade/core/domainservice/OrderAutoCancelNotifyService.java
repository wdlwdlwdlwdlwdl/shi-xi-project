package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeNotify;

/**
 * 订单自动取消通知服务
 */
public interface OrderAutoCancelNotifyService {

    /**
     * 发消息、记录操作日志
     */
    void afterStatusChange(OrderChangeNotify change);

}
