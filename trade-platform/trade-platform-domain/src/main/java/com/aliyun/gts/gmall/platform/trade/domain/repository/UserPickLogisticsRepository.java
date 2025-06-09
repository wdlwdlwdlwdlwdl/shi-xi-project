package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcUserPickLogisticsDO;

import java.util.List;

/**
 *
 */
public interface UserPickLogisticsRepository {

    /**
     * 保存用户地点信息
     * @param userPickLogisticsDO
     */
    void insert(TcUserPickLogisticsDO userPickLogisticsDO);

    /**
     * 查询用户接受的地点信息
     * @param userPickLogisticsDO
     */
    List<TcUserPickLogisticsDO> query(TcUserPickLogisticsDO userPickLogisticsDO);

}
