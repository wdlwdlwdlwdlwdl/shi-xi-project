package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonHistoryDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CancelReason;

import java.util.List;

public interface CancelReasonHistoryRepository {

    List<TcCancelReasonHistoryDO> queryCancelReasonList(String cancelReasonCode);

    TcCancelReasonHistoryDO saveCancelReasonHistory(TcCancelReasonHistoryDO tcCancelReasonHistoryDO);
}
