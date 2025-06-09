package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ReceiveAddr;

public interface ReceiverRepository {

    /**
     * ID查询收件信息
     */
    ReceiveAddr getReceiverById(Long custId, Long id);

    /**
     * 查询用户默认收件地址信息
     */
    ReceiveAddr getDefaultReceiver(Long custId);

    /**
     * 查询卖家默认收件地址信息
     */
    ReceiveAddr getSellerDefaultReceiver(Long sellerId);
}
