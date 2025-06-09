package com.aliyun.gts.gmall.platform.trade.domain.repository;


import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CancelReason;

import java.util.List;

public interface CancelReasonRepository {

    TcCancelReasonDO queryTcCancelReason(long id);

    PageInfo<TcCancelReasonDO> queryCancelReasonList(CancelReason req);

    List<TcCancelReasonDO> queryCancelReasonAll(CancelReason req);

    TcCancelReasonDO saveCancelReason(TcCancelReasonDO tcCancelReasonDO);

    TcCancelReasonDO updateCancelReason(TcCancelReasonDO tcCancelReasonDO);

    TcCancelReasonDO queryTcCancelReasonByCode(String code);

    /**
     * 批量查询
     * @param code
     * @return
     */
    List<TcCancelReasonDO> queryTcCancelReasonByCodeList(List<String> code);

}
