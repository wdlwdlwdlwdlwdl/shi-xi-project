package com.aliyun.gts.gmall.platform.trade.core.ability.impl;

import com.aliyun.gts.gmall.framework.domain.extend.service.DomainExtendService;
import com.aliyun.gts.gmall.framework.domain.extend.utils.DomainExtendUtil;
import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderFeatureAbility;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultOrderFeatureExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.OrderFeatureExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.OrderInventoryExt;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Map.Entry;

@Slf4j
@Component
@Ability(
    code = OrderFeatureAbilityImpl.ABILITY_NAME,
    fallback = DefaultOrderFeatureExt.class,
    description = "订单新增扩展信息"
)
public class OrderFeatureAbilityImpl extends BaseAbility<BizCodeEntity, OrderFeatureExt> implements OrderFeatureAbility {

    public static final String ABILITY_NAME = "com.aliyun.gts.gmall.platform.trade.core.ability.impl.OrderFeatureAbilityImpl";

    @Autowired
    private DomainExtendService domainExtendService;

    @Override
    public void addFeatureOnCrete(CreatingOrder order) {
        for (MainOrder main : order.getMainOrders()) {
            List<BizCodeEntity> bizCodes = BizCodeEntity.getOrderBizCode(main);
            // 主订单打标
            Map<String, String> mainFeatures = new HashMap<>();
            for (BizCodeEntity bizCode : bizCodes) {
                List<Map<String, String>> mainMaps = executeExt(
                    bizCode,
                    extension -> extension.getFeatureOnCrete(main, order),
                    OrderFeatureExt.class,
                    Reducers.collect(Objects::nonNull)
                );
                if (CollectionUtils.isNotEmpty(mainMaps)) {
                    for (Map<String, String> map : mainMaps) {
                        mainFeatures.putAll(map);
                    }
                }
            }
            Map<String, String> mainSave = main.orderAttr().extras();
            mainSave.putAll(mainFeatures);
            checkFeature(mainSave);
            // 子订单打标
            for (SubOrder sub : main.getSubOrders()) {
                Map<String, String> subFeatures = new HashMap<>();
                for (BizCodeEntity bizCode : bizCodes) {
                    List<Map<String, String>> subMaps = executeExt(
                        bizCode,
                        extension -> extension.getSubFeatureOnCrete(sub, main, order),
                        OrderFeatureExt.class,
                        Reducers.collect(Objects::nonNull)
                    );
                    if (CollectionUtils.isNotEmpty(subMaps)) {
                        for (Map<String, String> map : subMaps) {
                            subFeatures.putAll(map);
                        }
                    }
                }
                Map<String, String> subSave = sub.orderAttr().extras();
                subSave.putAll(subFeatures);
                checkFeature(subSave);
            }
        }
    }

    @Override
    public void addTagsOnCrete(CreatingOrder order) {
        for (MainOrder main : order.getMainOrders()) {
            List<BizCodeEntity> bizCodes = BizCodeEntity.getOrderBizCode(main);
            // 主订单打标
            List<String> mainTags = new ArrayList<>();
            for (BizCodeEntity bizCode : bizCodes) {
                List<List<String>> mainResult = executeExt(
                    bizCode,
                    extension -> extension.getTagsOnCrete(main, order),
                    OrderFeatureExt.class,
                    Reducers.collect(Objects::nonNull)
                );
                if (CollectionUtils.isNotEmpty(mainResult)) {
                    for (List<String> result : mainResult) {
                        mainTags.addAll(result);
                    }
                }
            }
            OrderAttrDO attr = main.orderAttr();
            attr.setTags(mergeUnique(attr.getTags(), mainTags));
            // 子订单打标
            for (SubOrder sub : main.getSubOrders()) {
                List<String> subTags = new ArrayList<>();
                for (BizCodeEntity bizCode : bizCodes) {
                    List<List<String>> subResult = executeExt(
                        bizCode,
                        extension -> extension.getSubTagsOnCrete(sub, main, order),
                        OrderFeatureExt.class,
                        Reducers.collect(Objects::nonNull)
                    );
                    if (CollectionUtils.isNotEmpty(subResult)) {
                        for (List<String> result : subResult) {
                            subTags.addAll(result);
                        }
                    }
                }
                OrderAttrDO subAttr = sub.orderAttr();
                subAttr.setTags(mergeUnique(subAttr.getTags(), subTags));
            }
        }
    }

    @Override
    public void addItemFeature(CreatingOrder order) {
        BizCodeEntity bizCode = BizCodeEntity.buildWithDefaultBizCode(order.getMainOrders().get(0));
        executeExt(bizCode,
            extension -> {
                extension.addSubItemStoredMap(order.getMainOrders());
                return null;
            },
            OrderFeatureExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
    }

    private void checkFeature(Map<String, String> map) {
        if (map == null) {
            return;
        }
        Map<String, String> metaMap = new HashMap<>();
        for (Entry<String, String> en : map.entrySet()) {
            String key = DomainExtendUtil.buildOrderAttrKeyWithTcOrder(en.getKey());
            metaMap.put(key, en.getValue());
        }
        domainExtendService.loadAllExtendsWithCheck(metaMap);
    }

    private List<String> mergeUnique(List<String> list1, List<String> list2) {
        if (CollectionUtils.isEmpty(list1) && CollectionUtils.isEmpty(list2)) {
            return null;
        }
        Set<String> set = new LinkedHashSet<>();
        if (CollectionUtils.isNotEmpty(list1)) {
            set.addAll(list1);
        }
        if (CollectionUtils.isNotEmpty(list2)) {
            set.addAll(list2);
        }
        return new ArrayList<>(set);
    }
}
