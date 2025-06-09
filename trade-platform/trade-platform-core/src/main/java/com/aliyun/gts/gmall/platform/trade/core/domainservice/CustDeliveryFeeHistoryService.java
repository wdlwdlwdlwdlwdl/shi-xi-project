package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CustDeliveryFeeHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CustDeliveryFeeHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.DeliveryFeeHistoryDTO;

import java.util.List;

public interface CustDeliveryFeeHistoryService {

    List<CustDeliveryFeeHistoryDTO> queryCustDeliveryFeeHistory(CustDeliveryFeeHistoryRpcReq req);

    CustDeliveryFeeHistoryDTO saveCustDeliveryFeeHistory(CustDeliveryFeeHistoryRpcReq req);
}
