package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.UserPickLogisticsRpcReq;

public interface UserPickLogisticsService {

    /**
     * 保存用户地点信息
     * @param userPickLogisticsRpcReq
     */
    void insert(UserPickLogisticsRpcReq userPickLogisticsRpcReq);

    /**
     * 存在确认
     * @param custId
     * @param deliveryType
     * @return
     */
    Boolean existUserPick(Long custId, String deliveryType);

}
