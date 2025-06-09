package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonHistoryDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcDeliveryFeeHistoryDO;

import java.util.List;

public interface DeliveryFeeHistoryRepository {

    List<TcDeliveryFeeHistoryDO> queryDeliveryFeeList(String feeCode);

    TcDeliveryFeeHistoryDO saveDeliveryFeeHistory(TcDeliveryFeeHistoryDO deliveryFeeDO);
}
