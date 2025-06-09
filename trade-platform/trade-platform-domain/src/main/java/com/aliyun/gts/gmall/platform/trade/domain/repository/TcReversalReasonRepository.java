package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcReversalReasonDO;

import java.util.Collection;
import java.util.List;

public interface TcReversalReasonRepository {

    List<TcReversalReasonDO> queryByType(Integer reversalType);

    TcReversalReasonDO queryByCode(Integer reasonCode);

    List<TcReversalReasonDO> batchQueryByCode(Collection<Integer> reasonCodes);
}
