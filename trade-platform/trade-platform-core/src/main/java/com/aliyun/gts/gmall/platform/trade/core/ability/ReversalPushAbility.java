package com.aliyun.gts.gmall.platform.trade.core.ability;

import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;

public interface ReversalPushAbility {

    void send(MainReversal reversal, Integer type);
}
