package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderAutoCancelConfigDO;

public interface OrderAutoCancelConfigRepository {

    /**
     * 通过订单状态获取原因
     * @param orderStatus
     * @return
     */
    public TcOrderAutoCancelConfigDO getCancelCodeByOrderStatus(Integer orderStatus);

}
