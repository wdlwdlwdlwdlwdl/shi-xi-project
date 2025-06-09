package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderExtendDO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by auto-generated on 2021/03/19.
 */
public interface TcOrderExtendRepository {


    TcOrderExtendDO queryTcOrderExtend(long id);

    List<TcOrderExtendDO> queryExtendsByPrimaryOrderId(long primaryOrderId);

    List<TcOrderExtendDO> queryExtendsByPrimaryOrderIds(Long custId, List<Long> primaryOrderIds);

    /**
     * 可根据 primaryOrderId、orderId、extendType、extendKey 查询, 其中primaryOrderId 必填
     */
    List<TcOrderExtendDO> queryByParams(TcOrderExtendDO params);

    void create(TcOrderExtendDO tcOrderExtendDO);

    void insertOrUpdate(TcOrderExtendDO ext);

    int delete(Long id);

    int deleteByTypes(Long primaryOrderId, Long orderId, Collection<String> types);

    int deleteByKeys(Long primaryOrderId, Long orderId, Map<String, ? extends Collection<String>> typeKeys);
}
