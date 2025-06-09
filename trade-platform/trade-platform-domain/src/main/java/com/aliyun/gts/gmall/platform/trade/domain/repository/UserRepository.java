package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Customer;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Seller;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Shop;

import java.util.Collection;
import java.util.List;

public interface UserRepository {

    /**
     * 查用户, 不存在抛异常
     */
    Customer getCustomerRequired(Long custId);

    /**
     * 查卖家, 卖家不存在时返回null
     */
    Seller getSeller(Long sellerId);

    /**
     * 批量查卖家，仅返回存在的卖家，无序且去重
     */
    List<Seller> getSellers(Collection<Long> sellerIds);


    /**
     * 批量查卖家，仅返回存在的卖家，无序且去重
     */
    List<Seller> getSellerByIds(List<Long> sellerIds);

    /**
     * 查卖家店铺
     */
    Shop getSellerShop(Long sellerId);

    /**
     * 查询主账号语言
     */
    String queryMainOperatorLang(Long sellerId);
}
