package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.domain.entity.user.SellerTradeConfig;

public interface OrderConfigService {

    /**
     * 查询店铺交易配置
     */
    SellerTradeConfig getSellerConfig(Long sellerId);

    /**
     * 查询店铺交易配置, 实时接口
     */
    SellerTradeConfig getSellerConfigNoCache(Long sellerId);

    /**
     * 保存店铺交易配置
     */
    void saveSellerConfig(Long sellerId, SellerTradeConfig config);
}
