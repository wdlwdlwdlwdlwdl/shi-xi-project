package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonFeeHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonFeeHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonHistoryDTO;

import java.util.List;

public interface CancelReasonFeeHistoryService {

    List<CancelReasonFeeHistoryDTO> queryCancelReasonFeeHistory(CancelReasonFeeHistoryRpcReq req);

    CancelReasonFeeHistoryDTO saveCancelReasonFeeHistory(CancelReasonFeeHistoryRpcReq req);
}
