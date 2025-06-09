package com.aliyun.gts.gmall.center.trade.core.domainservice;

import com.aliyun.gts.gmall.center.trade.domain.entity.b2b.ItemPriceInput;

import java.util.List;

public interface ItemPriceDomainService {

    /**
     * 处理商品本身的 阶梯价、协议价等, 合并计价
     */
    void calcItemPrice(List<ItemPriceInput> input);

    /**
     * 处理商品本身的 阶梯价、协议价等, 单独计价
     */
    void calcItemPrice(ItemPriceInput input);
}
