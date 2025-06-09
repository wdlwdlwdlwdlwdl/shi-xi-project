package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonFeeQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonFeeRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonFeeDTO;

public interface CancelReasonFeeService {

    PageInfo<CancelReasonFeeDTO> queryCancelReasonFee(CancelReasonFeeQueryRpcReq req);

    CancelReasonFeeDTO saveCancelReasonFee(CancelReasonFeeRpcReq req);

    CancelReasonFeeDTO updateCancelReasonFee(CancelReasonFeeRpcReq req);

    CancelReasonFeeDTO cancelReasonFeeDetail(CancelReasonFeeRpcReq req);

    boolean exist(CancelReasonFeeRpcReq req);
}
