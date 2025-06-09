package com.aliyun.gts.gmall.center.trade.domain.repositiry;

import com.aliyun.gts.gmall.center.trade.domain.dataobject.OrderCheckErrorParam;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.TcOrderCheckErrorDO;

import java.util.List;

/**
 * Created by auto-generated on 2021/06/01.
 */
public interface TcOrderCheckErrorRepository {


    TcOrderCheckErrorDO queryTcOrderCheckError(long id);

    List<TcOrderCheckErrorDO> queryByParam(OrderCheckErrorParam orderCheckErrorParam);

    TcOrderCheckErrorDO create(TcOrderCheckErrorDO tcOrderCheckErrorDO);


    TcOrderCheckErrorDO update(TcOrderCheckErrorDO tcOrderCheckErrorDO);

    int delete(Long id);
}
