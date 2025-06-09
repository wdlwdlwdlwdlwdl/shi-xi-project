package com.aliyun.gts.gmall.platform.trade.domain.repository;


import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcFeeRulesDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.FeeRules;

public interface FeeRulesRepository {

    TcFeeRulesDO queryTcFeeRules(long id);

    PageInfo<TcFeeRulesDO> queryFeeRulesList(FeeRules req);

    TcFeeRulesDO saveFeeRules(TcFeeRulesDO tcFeeRulesDO);

    TcFeeRulesDO updateFeeRules(TcFeeRulesDO tcFeeRulesDO);

    TcFeeRulesDO queryFeeRules();
}
