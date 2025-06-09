package com.aliyun.gts.gmall.platform.trade.domain.repository;


import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonFeeDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CancelReasonFee;

public interface CancelReasonFeeRepository {

    TcCancelReasonFeeDO queryTcCancelReasonFee(TcCancelReasonFeeDO req);

    PageInfo<TcCancelReasonFeeDO> queryCancelReasonFeeList(CancelReasonFee req);

    TcCancelReasonFeeDO saveCancelReasonFee(TcCancelReasonFeeDO tcCancelReasonFeeDO);

    TcCancelReasonFeeDO updateCancelReasonFee(TcCancelReasonFeeDO tcCancelReasonFeeDO);

    boolean exist(String cancelReasonCode);
}
