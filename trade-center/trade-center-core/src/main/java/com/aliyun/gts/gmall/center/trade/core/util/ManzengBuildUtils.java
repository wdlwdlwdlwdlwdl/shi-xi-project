package com.aliyun.gts.gmall.center.trade.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.aliyun.gts.gmall.center.promotion.api.dto.input.OrderGiftRequest;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.GiftDTO;
import com.aliyun.gts.gmall.platform.promotion.common.constant.PromotionToolCodes;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemDivideDetail;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemPromotion;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/10/12 11:32
 */
public class ManzengBuildUtils {
    public static final int GIFT_TYPE_ITEM = 2;
    public static final String EXTRA_KEY_REWARDS = "rewards";
    /**
     * 排序
     *
     * @param campId
     * @param mergeOrderIds
     * @return
     */
    public static String buildBizId(Long campId, List<Long> mergeOrderIds) {
        Collections.sort(mergeOrderIds);
        String str = StringUtils.join(mergeOrderIds, ",");
        String bizId = Md5Encrypt.md5(str, "utf-8");
        return bizId + "-" + campId;
    }

    /**
     * 按活动进行分类;
     *
     * @param subOrders
     * @return
     */
    public static Map<Long, List<SubOrder>> partitionSubOrder(List<SubOrder> subOrders) {
        Map<Long, List<SubOrder>> subOrderMap = new HashMap<>();
        for (SubOrder subOrder : subOrders) {
            //解析所有的主订单
            Set<Long> campIds = ManzengBuildUtils.parseByToolCode(subOrder.getPromotions(), PromotionToolCodes.MANZENG);
            if (campIds.size() <= 0) {
                continue;
            }
            //根据活动ID分类
            for (Long campId : campIds) {
                List<SubOrder> list = subOrderMap.get(campId);
                if (list == null) {
                    list = new ArrayList<>();
                    subOrderMap.put(campId, list);
                }
                list.add(subOrder);
            }
        }
        return subOrderMap;
    }

    /**
     * 校验所有订单是否确认收货
     *
     * @param orders
     * @return
     */
    public static boolean checkAllOrderSuccess(List<SubOrder> orders) {
        if (CollectionUtils.isEmpty(orders)) {
            return false;
        }
        for (SubOrder subOrder : orders) {
            if (!orderSuccess(subOrder)) {
                return false;
            }
        }
        return true;
    }

    private static boolean orderSuccess(SubOrder subOrder) {
        if (OrderStatusEnum.COMPLETED.getCode().equals(subOrder.getOrderStatus())) {
            return true;
        }
        /*if (OrderStatusEnum.SYSTEM_CONFIRM.getCode().equals(subOrder.getOrderStatus())) {
            return true;
        }*/
        return false;
    }

    /**
     * @param promotion
     * @param toolCode
     * @return
     */
    public static Set<Long> parseByToolCode(ItemPromotion promotion, String toolCode) {
        Set<Long> campIds = new HashSet<Long>();
        if (promotion == null) {
            return campIds;
        }
        List<ItemDivideDetail> divides = promotion.getItemDivideDetails();
        if (CollectionUtils.isEmpty(divides)) {
            return campIds;
        }
        for (ItemDivideDetail detail : divides) {
            List<GiftDTO> giftDTOS = parseGift(detail);
            if(CollectionUtils.isEmpty(giftDTOS)){
                continue;
            }
//            //如果是商品就返回
//            if(isItemGift(giftDTOS)){
//                continue;
//            }
            if (toolCode.equals(detail.getToolCode())) {
                campIds.add(detail.getCampId());
            }
        }
        return campIds;
    }

    private static boolean isItemGift(List<GiftDTO> items){
        for(GiftDTO dto : items){
            if(GIFT_TYPE_ITEM == dto.getType()){
                return true;
            }
        }
        return false;
    }
    /**
     *
     * @param div
     * @return
     */
    private static List<GiftDTO> parseGift(ItemDivideDetail div) {
        if(Objects.isNull(div.getExtras()))
        {
            return new ArrayList();
        }
        String rewardsValue = (String) div.getExtras().get(EXTRA_KEY_REWARDS);
        if (StringUtils.isBlank(rewardsValue)) {
            return new ArrayList();
        }
        List<GiftDTO> gifts = JSON.parseObject(rewardsValue, new TypeReference<List<GiftDTO>>() {
        });
        return gifts;
    }

    /**
     * 构建请求参数
     * @param custId
     * @param campId
     * @param bizId
     * @param refOrders
     * @return
     */
    public static OrderGiftRequest buildRequest(Long custId, long campId, String bizId, List<Long> refOrders){
        OrderGiftRequest request = new OrderGiftRequest();
        request.setCampId(campId);
        request.setCustId(custId);
        request.setBizId(bizId);
        JSONObject feature = new JSONObject();
        feature.put("refOrders", StringUtils.join(refOrders, ","));
        request.setFeature(feature);
        return request;
    }
}
