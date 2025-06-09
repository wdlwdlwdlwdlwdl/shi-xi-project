package com.aliyun.gts.gmall.platform.trade.core.ability;

import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;

public interface OrderFeatureAbility {

    /**
     * 下单打 feature
     */
    void addFeatureOnCrete(CreatingOrder order);

    /**
     * 下单打 tag (tag 进搜索)
     */
    void addTagsOnCrete(CreatingOrder order);

    /**
     * 商品featuer信息写入
     */
    void addItemFeature(CreatingOrder order);
}
