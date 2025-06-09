package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.DeliveryFeeHistoryDTO;

import java.util.List;

public interface DeliveryFeeHistoryService {

    List<DeliveryFeeHistoryDTO> queryDeliveryFeeHistory(DeliveryFeeHistoryRpcReq req);

    DeliveryFeeHistoryDTO saveDeliveryFeeHistory(DeliveryFeeHistoryRpcReq req);
}
