package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonQueryNoPageRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonDTO;

import java.util.List;

public interface CancelReasonService {

    PageInfo<CancelReasonDTO> queryCancelReason(CancelReasonQueryRpcReq req);

    List<CancelReasonDTO> queryCancelReasonAll(CancelReasonQueryNoPageRpcReq req);

    CancelReasonDTO saveCancelReason(CancelReasonRpcReq req);

    CancelReasonDTO updateCancelReason(CancelReasonRpcReq req);

    CancelReasonDTO cancelReasonDetail(CancelReasonRpcReq req);

    CancelReasonDTO cancelReasonDetailByCode(CancelReasonRpcReq req);
}
