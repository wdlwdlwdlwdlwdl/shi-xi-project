package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonFeeHistoryDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonHistoryDO;

import java.util.List;

public interface CancelReasonFeeHistoryRepository {

    List<TcCancelReasonFeeHistoryDO> queryCancelReasonFeeList(String cancelReasonCode);

    TcCancelReasonFeeHistoryDO saveCancelReasonFeeHistory(TcCancelReasonFeeHistoryDO tcCancelReasonHistoryDO);
}
