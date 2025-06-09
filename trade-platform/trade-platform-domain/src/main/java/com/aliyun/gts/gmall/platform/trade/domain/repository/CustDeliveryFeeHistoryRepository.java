package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCustDeliveryFeeHistoryDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcDeliveryFeeHistoryDO;

import java.util.List;

public interface CustDeliveryFeeHistoryRepository {

    List<TcCustDeliveryFeeHistoryDO> queryCustDeliveryFeeList(String custFeeCode);

    TcCustDeliveryFeeHistoryDO saveCustDeliveryFeeHistory(TcCustDeliveryFeeHistoryDO deliveryFeeDO);
}
