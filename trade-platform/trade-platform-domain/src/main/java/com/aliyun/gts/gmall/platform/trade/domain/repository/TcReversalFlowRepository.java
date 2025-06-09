package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcReversalFlowDO;

import java.util.List;

public interface TcReversalFlowRepository {

    void insert(TcReversalFlowDO flow);

    List<TcReversalFlowDO> query(Long primaryReversalId);

    List<TcReversalFlowDO> batchQuery(List<Long> primaryReversalIds);

    TcReversalFlowDO queryReversalFlow(Long primaryReversalId);
}
