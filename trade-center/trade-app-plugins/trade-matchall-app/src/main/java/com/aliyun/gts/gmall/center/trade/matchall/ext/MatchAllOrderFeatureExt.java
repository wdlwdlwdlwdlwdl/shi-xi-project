package com.aliyun.gts.gmall.center.trade.matchall.ext;

import com.aliyun.gts.gmall.center.trade.common.constants.OrderFeatureKey;
import com.aliyun.gts.gmall.center.trade.common.constants.OrderTagPrefix;
import com.aliyun.gts.gmall.center.trade.core.converter.CombItemConvert;
import com.aliyun.gts.gmall.center.trade.core.domainservice.B2bSourcingDomainService;
import com.aliyun.gts.gmall.center.trade.core.domainservice.FixedPointPriceService;
import com.aliyun.gts.gmall.center.trade.matchall.util.SelfSellerUtils;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.ExtensionFilterContext;
import com.aliyun.gts.gmall.platform.trade.common.constants.CreatingOrderParamConstants;
import com.aliyun.gts.gmall.platform.trade.common.util.CreatingOrderParamUtils;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ItemService;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultOrderFeatureExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.OrderFeatureExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemDivideDetail;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemPromotion;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 订单打标
 * 自营商家 feature  {@link OrderFeatureKey#IS_SELF_SELLER}
 * 合并下单ID feature  {@link OrderFeatureKey#BATCH_ID}
 * 活动ID tag   {@link OrderTagPrefix#CAMP_ID}
 * 营销工具Code tag   {@link OrderTagPrefix#CAMP_TOOL}
 */
@Slf4j
@Extension(points = {OrderFeatureExt.class})
public class MatchAllOrderFeatureExt extends DefaultOrderFeatureExt {

    @Autowired
    private ItemService itemService;

    @Autowired
    private CombItemConvert convert;

    @Autowired
    private FixedPointPriceService fixedPointPriceService;

    @Autowired
    private B2bSourcingDomainService b2bSourcingDomainService;

    @Override
    public boolean filter(ExtensionFilterContext context) {
        //return MatchAllFilter.filter(context);
        return super.filter(context);   // 此扩展点不排他
    }

    @Override
    public Map<String, String> getFeatureOnCrete(MainOrder mainOrder, CreatingOrder creatingOrder) {
        return  super.getFeatureOnCrete(mainOrder, creatingOrder);
//        Map<String, String> map = super.getFeatureOnCrete(mainOrder, creatingOrder);
//        if (map == null) {
//            map = new HashMap<>();
//        }
        // 合并下单唯一ID
//        String batchId = (String) creatingOrder.getExtra(OrderFeatureKey.BATCH_ID);
//        if (batchId == null) {
//            batchId = UUID.randomUUID().toString();
//            creatingOrder.putExtra(OrderFeatureKey.BATCH_ID, batchId);
//        }
//        map.put(OrderFeatureKey.BATCH_ID, batchId);

//        // 自营卖家
//        if (SelfSellerUtils.isSelfSeller(mainOrder.getSeller())) {
//            map.put(OrderFeatureKey.IS_SELF_SELLER, String.valueOf(true));
//        }
//
//        //代客下单下单人
//        boolean isHelpOrder = CreatingOrderParamUtils.isHelpOrder(creatingOrder.getParams());
//        if(isHelpOrder) {
//            Map<String, Object> orderParams = creatingOrder.getParams();
//            map.put(OrderFeatureKey.HELP_ORDER_ID, String.valueOf(orderParams.get(CreatingOrderParamConstants.HELP_ORDER_ID)));
//            map.put(OrderFeatureKey.HELP_ORDER_NAME, String.valueOf(orderParams.get(CreatingOrderParamConstants.HELP_ORDER_NAME)));
//        }
//
//        //寻源订单
//        if (b2bSourcingDomainService.isSourcing(creatingOrder)) {
//            b2bSourcingDomainService.getOrderFeature(creatingOrder, mainOrder, map);
//        }

//        return map;
    }

    @Override
    public List<String> getTagsOnCrete(MainOrder mainOrder, CreatingOrder creatingOrder) {
        List<String> tags = super.getTagsOnCrete(mainOrder, creatingOrder);
        if (tags == null) {
            tags = new ArrayList<>();
        }
        // 营销工具Code
        Set<String> toolCodes = new HashSet<>();
        for (SubOrder subOrder : mainOrder.getSubOrders()) {
            List<ItemDivideDetail> itemDivideDetails = Optional
                .ofNullable(subOrder)
                .map(SubOrder::getPromotions)
                .map(ItemPromotion::getItemDivideDetails)
                .orElse(Collections.emptyList());
            for (ItemDivideDetail detail : itemDivideDetails) {
                if (StringUtils.isNotBlank(detail.getToolCode())) {
                    toolCodes.add(detail.getToolCode());
                }
            }
        }
        toolCodes.stream().map(code -> OrderTagPrefix.CAMP_TOOL + code).forEach(tags::add);
//        //代客下单code
//        boolean isHelpOrder = CreatingOrderParamUtils.isHelpOrder(creatingOrder.getParams());
//        if(isHelpOrder) {
//            tags.add(OrderTagPrefix.HELP_ORDER);
//        }
//
//        //积分商城订单
//        if (fixedPointPriceService.isFixedOrder(creatingOrder)) {
//            tags.add(OrderTagPrefix.FIXED_POINT_ORD);
//        }
//
//        //寻源订单
//        if (b2bSourcingDomainService.isSourcing(creatingOrder)) {
//            tags.add(OrderTagPrefix.SOURCING_ORD);
//        }
        return tags;
    }

    @Override
    public List<String> getSubTagsOnCrete(SubOrder subOrder, MainOrder mainOrder, CreatingOrder creatingOrder) {
        List<String> tags = super.getSubTagsOnCrete(subOrder, mainOrder, creatingOrder);
        if (tags == null) {
            tags = new ArrayList<>();
        }
        // 营销活动
        List<ItemDivideDetail> itemDivideDetails = Optional
            .ofNullable(subOrder)
            .map(SubOrder::getPromotions)
            .map(ItemPromotion::getItemDivideDetails)
            .orElse(Collections.emptyList());

        Set<Long> campIds = new HashSet<>();
        Set<String> toolCodes = new HashSet<>();
        for (ItemDivideDetail detail : itemDivideDetails) {
            if (detail.getCampId() != null) {
                campIds.add(detail.getCampId());
            }
            if (StringUtils.isNotBlank(detail.getToolCode())) {
                toolCodes.add(detail.getToolCode());
            }
        }
        campIds.stream()
            .map(id -> OrderTagPrefix.CAMP_ID + id)
            .forEach(tags::add);
        toolCodes.stream()
            .map(code -> OrderTagPrefix.CAMP_TOOL + code)
            .forEach(tags::add);
        return tags;
    }

    @Override
    public void addSubItemStoredMap(List<MainOrder> mainOrders) {

    }

    @Override
    public Map<String, String> getSubFeatureOnCrete(SubOrder subOrder, MainOrder mainOrder, CreatingOrder creatingOrder) {
        Map<String, String> map = super.getSubFeatureOnCrete(subOrder, mainOrder, creatingOrder);
        if (map == null) {
            map = new HashMap<>();
        }
//        //寻源订单
//        if (b2bSourcingDomainService.isSourcing(creatingOrder)) {
//            b2bSourcingDomainService.getOrderFeature(creatingOrder, mainOrder, subOrder, map);
//        }
        return map;
    }
}
