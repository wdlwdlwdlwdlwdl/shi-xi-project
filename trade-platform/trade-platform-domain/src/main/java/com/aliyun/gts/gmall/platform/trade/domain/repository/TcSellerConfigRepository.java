package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcSellerConfigDO;

import java.util.List;

public interface TcSellerConfigRepository {

    /**
     * 查询出卖家的配置及平台默认的配置
     */
    List<TcSellerConfigDO> queryBySeller(Long sellerId);

    /**
     * insert or update
     */
    void save(List<TcSellerConfigDO> list);
}
