package com.aliyun.gts.gmall.platform.trade.core.extension.order;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.AbilityExtension;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;

import java.util.List;
import java.util.Map;

/**
 * 打 tag 和 feature
 *
 * tag 进搜索, 读取在 orderAttr.tags
 * feature 不进搜索, 读取在 orderAttr.extra
 *
 */
public interface OrderFeatureExt extends IExtensionPoints {

    @AbilityExtension(
        code = "FEATURE_ON_CREATE",
        name = "下单打Feature",
        description = "下单打Feature"
    )
    Map<String /*featureName*/, String /*featureValue*/>
        getFeatureOnCrete(MainOrder mainOrder, CreatingOrder creatingOrder);


    @AbilityExtension(
        code = "SUB_FEATURE_ON_CREATE",
        name = "下单打Feature(子订单)",
        description = "下单打Feature(子订单)"
    )
    Map<String /*featureName*/, String /*featureValue*/>
        getSubFeatureOnCrete(SubOrder subOrder, MainOrder mainOrder, CreatingOrder creatingOrder);

    @AbilityExtension(
        code = "TAG_ON_CREATE",
        name = "下单打标",
        description = "下单打标"
    )
    List<String> getTagsOnCrete(MainOrder mainOrder, CreatingOrder creatingOrder);

    @AbilityExtension(
        code = "SUB_TAG_ON_CREATE",
        name = "下单打标(子订单)",
        description = "下单打标(子订单)"
    )
    List<String> getSubTagsOnCrete(SubOrder subOrder, MainOrder mainOrder, CreatingOrder creatingOrder);

    /**
     * 子订单的
     * @return
     */
    @AbilityExtension(
        code = "SUB_ITEM_STORE_MAP",
        name = "下单打标",
        description = "下单打标"
    )
    void addSubItemStoredMap(List<MainOrder> mainOrders);
}
