package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcReversalDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.ReversalDbQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface TcReversalRepository {

    List<TcReversalDO> queryByPrimaryOrderId(Long primaryOrderId);

    List<TcReversalDO> queryByPrimaryReversalId(Long primaryReversalId);

    List<TcReversalDO> batchQueryByPrimaryReversalId(List<Long> primaryReversalIds);

    TcReversalDO queryByReversalId(Long reversalId);

    // 只查出主售后单
    List<TcReversalDO> queryMainReversalsByPrimaryOrderId(Long primaryOrderId);

    void insert(TcReversalDO reversal);

    boolean updateByPrimaryReversalId(TcReversalDO up, int fromStatus);

    // 只查主售后单的记录
    Page<TcReversalDO> queryPrimaryByCondition(ReversalDbQuery query);

    /**
     * 根据 reversalId + version 通用更新, 更新后version+1
     */
    boolean updateByReversalIdVersion(TcReversalDO update);
}
