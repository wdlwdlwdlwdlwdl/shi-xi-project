package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcStepOrderDO;

import java.util.List;

public interface TcStepOrderRepository {

    void insert(TcStepOrderDO stepOrder);

    List<TcStepOrderDO> queryByPrimaryId(Long primaryOrderId);

    /**
     * 根据 primaryOrderId + stepNo + version 更新
     */
    boolean updateByUkVersion(TcStepOrderDO up);
}
