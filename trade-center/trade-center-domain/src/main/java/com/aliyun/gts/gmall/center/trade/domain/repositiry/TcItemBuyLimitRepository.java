package com.aliyun.gts.gmall.center.trade.domain.repositiry;

import com.aliyun.gts.gmall.center.trade.domain.dataobject.TcItemBuyLimitDO;

public interface TcItemBuyLimitRepository {

    void create(TcItemBuyLimitDO buy);

    // custId + campId + itemId
    TcItemBuyLimitDO queryByUk(TcItemBuyLimitDO uk);

    // custId + campId + itemId + version
    int updateByUkVersion(TcItemBuyLimitDO buy);
}
