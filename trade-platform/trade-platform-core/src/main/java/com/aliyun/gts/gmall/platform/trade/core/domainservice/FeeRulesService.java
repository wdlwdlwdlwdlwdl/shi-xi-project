package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.FeeRulesQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.FeeRulesRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.FeeRulesDTO;

public interface FeeRulesService {

    PageInfo<FeeRulesDTO> queryFeeRules(FeeRulesQueryRpcReq req);

    FeeRulesDTO saveFeeRules(FeeRulesRpcReq req);

    FeeRulesDTO updateFeeRules(FeeRulesRpcReq req);

    FeeRulesDTO feeRulesDetail(FeeRulesRpcReq req);

    FeeRulesDTO queryFeeRules();
}
