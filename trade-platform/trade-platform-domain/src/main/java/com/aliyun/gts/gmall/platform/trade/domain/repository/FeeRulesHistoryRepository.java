package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcDeliveryFeeHistoryDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcFeeRulesHistoryDO;

import java.util.List;

public interface FeeRulesHistoryRepository {

    List<TcFeeRulesHistoryDO> queryFeeRulesList(String feeCode);

    TcFeeRulesHistoryDO saveFeeRulesHistory(TcFeeRulesHistoryDO feeRulesDO);
}
