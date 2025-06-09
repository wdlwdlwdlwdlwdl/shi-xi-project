package com.aliyun.gts.gmall.center.trade.domain.repositiry;

import com.aliyun.gts.gmall.center.trade.domain.dataobject.evoucher.TcEvoucherDO;

import java.util.List;

public interface TcEvoucherRepository {

    void create(TcEvoucherDO ev);

    List<TcEvoucherDO> queryByOrderId(Long orderId);

    TcEvoucherDO queryByEvCode(Long evCode);

    // 根据 code + version 更新
    boolean updateByCodeVersion(TcEvoucherDO ev);
}
