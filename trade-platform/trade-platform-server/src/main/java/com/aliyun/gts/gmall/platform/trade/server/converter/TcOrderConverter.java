package com.aliyun.gts.gmall.platform.trade.server.converter;

import com.aliyun.gts.gmall.framework.domain.extend.ExtendComponent;
import com.aliyun.gts.gmall.platform.trade.api.constant.TradeExtendKeyConstants;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.ReceiverDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.promotion.PromotionOptionDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.*;
import com.aliyun.gts.gmall.platform.trade.domain.entity.AbstractBusinessEntity;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ReceiveAddr;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.PayChannel;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.PromotionOption;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.*;

@Mapper(componentModel = "spring")
public interface TcOrderConverter {

    // ===============================
    // 确认订单结果
    // ===============================

    @Mappings({
        @Mapping(target = "custId", source = "t.req.custId"),
        @Mapping(target = "receiver", source = "order.receiver"),
        @Mapping(target = "orderGroups", source = "order.mainOrders"),
        @Mapping(target = "confirmOrderToken", expression = "java((String)t.getExtra(\"token\"))"),
        @Mapping(target = "promotionOptions", source = "order.promotions.options"),
        @Mapping(target = "promotionExtend", source = "order.promotions.promotionExtend"),
        @Mapping(target = "realAmt", source = "order.orderPrice.orderRealAmt"),
        @Mapping(target = "realItemAmt", source = "order.orderPrice.orderRealItemAmt"),
        @Mapping(target = "pointAmt", source = "order.orderPrice.pointAmt"),
        @Mapping(target = "totalAmt", source = "order.orderPrice.orderTotalAmt"),
        @Mapping(target = "freight", source = "order.orderPrice.freightAmt"),
        @Mapping(target = "maxAvailablePoint", source = "order.orderPrice", qualifiedByName = "convertMaxAvailablePoint"),
        @Mapping(target = "usePointCount", source = "order.orderPrice.pointCount"),
        @Mapping(target = "itemOriginAmt", source = "order.orderPrice.itemOriginAmt"),
        @Mapping(target = "stepExtend", source = "order", qualifiedByName = "convertConfirmStepExtend"),
        @Mapping(target = "payTypes", ignore = true),
        @Mapping(target = "installment", source = "order.installment"),
        @Mapping(target = "sumPriceList", source = "order.sumPriceList"),
        @Mapping(target = "sumLoanPriceList", source = "order.sumLoanPriceList"),
        @Mapping(target = "installPriceList", source = "order.installPriceList"),
        @Mapping(target = "loanPriceList", source = "order.loanPriceList"),
        @Mapping(target = "loan", source = "order.loan"),
        @Mapping(target = "orderType", source = "order.mainOrders", qualifiedByName = "convertOrderType"),
        @Mapping(target = "itemDisCountAmt", source = "order.orderPrice.itemDisCountAmt"),
        @Mapping(target = "confirmSuccess", source = "order.confirmSuccess"),
        @Mapping(target = "noCityPriceSkuIds", source = "order.noCityPriceSkuIds"),
        @Mapping(target = "deliveryMerchantFee", source = "order.orderPrice.deliveryMerchantFee"),
        @Mapping(target = "pvzPick", source = "order.pvzPick"),
        @Mapping(target = "postamatPick", source = "order.postamatPick"),
        @Mapping(target = "overPriceLimit", source = "order.overPriceLimit"),
        @Mapping(target = "errMsg", source = "order.errMsg"),
    })
    ConfirmOrderDTO toConfirmOrderDTO(CreatingOrder order, TOrderConfirm t);

    @Named("convertOrderType")
    default Integer convertOrderType(List<MainOrder> mainOrders) {
        if (CollectionUtils.isEmpty(mainOrders)) {
            return null;
        }
        return mainOrders.get(0).getOrderType();
    }

    @Named("convertMaxAvailablePoint")
    default Long convertMaxAvailablePoint(ExtendComponent ext) {
        String extend = ext.getExtend(TradeExtendKeyConstants.MAX_AVAILABLE_POINT);
        return StringUtils.isBlank(extend) ? 0 : Long.parseLong(extend);
    }

    @Named("convertConfirmStepExtend")
    default ConfirmStepExtendDTO convertConfirmStepExtend(AbstractBusinessEntity ext) {
        ConfirmStepExtendDTO dto = (ConfirmStepExtendDTO) ext.getExtra("ConfirmStepExtendDTO");
        return dto;
    }

    /**
     * 主订单转换为DTO
     * @param order
     * @return
     */
    @Mappings({
        @Mapping(target = "sellerId", source = "seller.sellerId"),
        @Mapping(target = "orderItems", source = "subOrders"),
        @Mapping(target = "promotionOptions", source = "promotions.options"),
        @Mapping(target = "promotionExtend", source = "promotions.promotionExtend"),
        @Mapping(target = "realAmt", source = "orderPrice.orderRealAmt"),
        @Mapping(target = "realItemAmt", source = "orderPrice.orderRealItemAmt"),
        @Mapping(target = "totalAmt", source = "orderPrice.orderTotalAmt"),
        @Mapping(target = "freight", source = "orderPrice.freightAmt"),
        @Mapping(target = "itemOriginAmt", source = "orderPrice.itemOriginAmt"),
        @Mapping(target = "bizCodes", source = "bizCodes"),
        @Mapping(target = "orderType", source = "orderType"),
        @Mapping(target = "stepOrders", source = "stepOrders"),
        @Mapping(target = "stepContextProps", source = "orderAttr.stepContextProps"),
        @Mapping(target = "deliveryType", source = "deliveryType"),
        @Mapping(target = "receiver", source = "receiver"),
        @Mapping(target = "supportDeliveryList", source = "supportDeliveryList"),
        @Mapping(target = "deliveryMerchantFee", source = "orderPrice.deliveryMerchantFee"),
    })
    OrderGroupDTO toOrderGroupDTO(MainOrder order);

    /**
     * 子订单转换为DTO
     * @param order
     * @return
     */
    @Mappings({
        @Mapping(target = "itemId", source = "itemSku.itemId"),
        @Mapping(target = "skuId", source = "itemSku.skuId"),
        @Mapping(target = "itemTitle", source = "itemSku.itemTitle"),
        @Mapping(target = "skuName", source = "itemSku.skuName"),
        @Mapping(target = "skuDesc", source = "itemSku.skuDesc"),
        @Mapping(target = "sellerId", source = "itemSku.seller.sellerId"),
        @Mapping(target = "sellerName", source = "itemSku.seller.sellerName"),
        @Mapping(target = "picUrl", source = "itemSku.itemPic"),
        @Mapping(target = "skuPic", source = "itemSku.skuPic"),
        @Mapping(target = "orderQty", source = "orderQty"),
        @Mapping(target = "maxSellableQty", source = "itemSku.skuQty"),
        @Mapping(target = "originPrice", source = "itemSku.itemPrice.originPrice"),
        @Mapping(target = "itemPrice", source = "itemSku.itemPrice.itemPrice"),
        @Mapping(target = "weight", source = "itemSku.weight"),
        @Mapping(target = "supportDeliveryList", source = "itemSku.supportDeliveryList"),
        @Mapping(target = "deliveryType", source = "itemSku.deliveryType"),
        @Mapping(target = "itemType", source = "itemSku.itemType"),
        @Mapping(target = "orderId", source = "orderId"),
        @Mapping(target = "primaryOrderId", source = "primaryOrderId"),
        @Mapping(target = "feature",expression = "java(toFeature(order))")
    })
    OrderItemDTO toOrderItemDTO(SubOrder order);

    default Map<String,String> toFeature(SubOrder order){
        Map<String,String> feature = new HashMap<>();
        if(order.getItemSku().getStoredExt() != null){
            feature.putAll(order.getItemSku().getStoredExt());
        }
        return feature;
    }

    ReceiverDTO toReceiverDTO(ReceiveAddr receiver);

    ReceiveAddr toReceiveAddr(ReceiverDTO receiver);

    @Mappings({
            @Mapping(target = "payType", source = "channelCode"),
            @Mapping(target = "payTypeName", source = "channelName"),
    })
    PayTypeDTO toPayTypeDTO(PayChannel pay);

    @Mappings({
            @Mapping(target = "isCoupon", source = "coupon"),
    })
    PromotionOptionDTO toPromotionOptionDTO(PromotionOption po);



    // ===============================
    // 多阶段对象
    // ===============================

    default ConfirmStepOrderDTO toStepOrderDTO(StepOrder stepOrder) {
        if (stepOrder == null) {
            return null;
        }
        ConfirmStepOrderDTO dst = new ConfirmStepOrderDTO();
        fillStepOrderInfo(dst, stepOrder);
        fillStepOrderPrice(dst, stepOrder.getPrice());
        return dst;
    }

    void fillStepOrderInfo(@MappingTarget ConfirmStepOrderDTO dst, StepOrder src);

    void fillStepOrderPrice(@MappingTarget ConfirmStepOrderDTO dst, StepOrderPrice src);
}
