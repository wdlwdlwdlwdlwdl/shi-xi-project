package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonHistoryDTO;

import java.util.List;

public interface CancelReasonHistoryService {

    List<CancelReasonHistoryDTO> queryCancelReasonHistory(CancelReasonHistoryRpcReq req);

    CancelReasonHistoryDTO saveCancelReasonHistory(CancelReasonHistoryRpcReq req);
}
