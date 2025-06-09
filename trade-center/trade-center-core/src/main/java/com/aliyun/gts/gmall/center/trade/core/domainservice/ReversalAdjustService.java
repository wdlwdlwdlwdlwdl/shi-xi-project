package com.aliyun.gts.gmall.center.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;

public interface ReversalAdjustService {

    void reduceFee(MainReversal reversal, long pointCount, String reason);
}
