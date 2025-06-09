package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.FeeRulesHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.DeliveryFeeHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.FeeRulesHistoryDTO;

import java.util.List;

public interface FeeRulesHistoryService {

    List<FeeRulesHistoryDTO> queryFeeRulesHistory(FeeRulesHistoryRpcReq req);

    FeeRulesHistoryDTO saveFeeRulesHistory(FeeRulesHistoryRpcReq req);
}
