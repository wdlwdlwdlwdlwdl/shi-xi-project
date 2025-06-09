package com.aliyun.gts.gmall.platform.trade.core.convertor;

import com.aliyun.gts.gmall.platform.trade.common.constants.DeliveryTypeEnum;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcStepOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderFeeAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderItemFeatureDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.StepOrderFeeDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.OrderPrice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface OrderConverter {

    // ========  主订单 =========

    @Mappings({
        @Mapping(target = "primaryOrderId", source = "order.primaryOrderId"),
        @Mapping(target = "orderId", source = "order.primaryOrderId"),
        @Mapping(target = "primaryOrderStatus", source = "order.primaryOrderStatus"),
        @Mapping(target = "orderStatus", source = "order.primaryOrderStatus"),
        @Mapping(target = "snapshotPath", source = "order.snapshotPath"),
        @Mapping(target = "orderChannel", source = "order.orderChannel"),
        @Mapping(target = "evaluate", source = "order.evaluate"),
        @Mapping(target = "custId", source = "order.customer.custId"),
        @Mapping(target = "custName", source = "order.customer.custName"),
        @Mapping(target = "firstName", source = "order.customer.firstName"),
        @Mapping(target = "lastName", source = "order.customer.lastName"),
        @Mapping(target = "custMemo", source = "order.custMemo"),
        @Mapping(target = "orderPrice", source = "order.orderPrice.itemOriginAmt"),
        @Mapping(target = "realPrice", source = "order.orderPrice.orderRealAmt"),
        @Mapping(target = "gmtCreate", source = "order.gmtCreate"),
        @Mapping(target = "gmtModified", source = "order.gmtModified"),
        @Mapping(target = "sellerId", source = "order.seller.sellerId"),
        @Mapping(target = "sellerName", source = "order.seller.sellerName"),
        @Mapping(target = "binOrIin", source = "order.seller.binOrIin"),
        @Mapping(target = "bizCode", source = "order.bizCodes"),
        @Mapping(target = "promotionAttr", source = "order.promotions"),
        @Mapping(target = "receiveInfo", source = "order.receiver"),
        @Mapping(target = "salesInfo", source = "c.sales"),
        @Mapping(target = "orderFeeAttr", source = "order.orderPrice"),
        @Mapping(target = "primaryOrderFlag", expression = "java(com.aliyun.gts.gmall.platform.trade.common.constants.PrimaryOrderFlagEnum.PRIMARY_ORDER.getCode())"),
        @Mapping(target = "version", source = "order.version"),
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "reversalType", ignore = true),
        @Mapping(target = "itemId", ignore = true),
        @Mapping(target = "skuId", ignore = true),
        @Mapping(target = "skuDesc", ignore = true),
        @Mapping(target = "itemTitle", ignore = true),
        @Mapping(target = "itemQuantity", ignore = true),
        @Mapping(target = "itemPic", ignore = true),
        @Mapping(target = "salePrice", ignore = true),
        @Mapping(target = "itemFeature", ignore = true),
        @Mapping(target = "orderAttr", expression = "java(getMainOrderAttrDO(order))"),
        @Mapping(target = "displayOrderId", source = "order.displayOrderId"),
        /*
        @Mapping(target = "orderAttr", source = "order.orderAttr"),
        @Mapping(target = "orderAttr.inventoryReduceType", source = "order.inventoryReduceType"),
        @Mapping(target = "orderAttr.orderType", source = "order.orderType"),
        @Mapping(target = "orderAttr.stepTemplateName", source = "order.stepTemplate.templateName"),
        @Mapping(target = "orderAttr.stepContextProps", source = "order.stepTemplate.contextProps"),
        */
    })
    TcOrderDO convertMainOrder(MainOrder order, CreatingOrder c);

    default OrderAttrDO getMainOrderAttrDO(MainOrder main) {
        OrderAttrDO attr = new OrderAttrDO();
        if (main != null) {
            copyOrderAttrDO(main.getOrderAttr(), attr);
        }
        attr.setInventoryReduceType(main.getInventoryReduceType());
        attr.setOrderType(main.getOrderType());
        if (main.getStepTemplate() != null) {
            attr.setStepTemplateName(main.getStepTemplate().getTemplateName());
        }
        return attr;
    }

    @Mappings({
        @Mapping(target = "primaryOrderId", source = "primaryOrderId"),
        @Mapping(target = "primaryOrderStatus", source = "primaryOrderStatus"),
        @Mapping(target = "displayOrderId", source = "displayOrderId"),
        @Mapping(target = "snapshotPath", source = "snapshotPath"),
        @Mapping(target = "orderChannel", source = "orderChannel"),
        @Mapping(target = "evaluate", source = "evaluate"),
        @Mapping(target = "custMemo", source = "custMemo"),
        @Mapping(target = "payCartId", source = "payCartId"),
        @Mapping(target = "gmtCreate", source = "gmtCreate"),
        @Mapping(target = "gmtModified", source = "gmtModified"),
        @Mapping(target = "seller.sellerId", source = "sellerId"),
        @Mapping(target = "seller.sellerName", source = "sellerName"),
        @Mapping(target = "seller.binOrIin", source = "binOrIin"),
        @Mapping(target = "seller.sellerAccountInfo", expression = "java(com.aliyun.gts.gmall.platform.trade.core.util.FeatureParseUtil.parseSellerAccountInfo(tcOrderDO))"),
        @Mapping(target = "customer.custId", source = "custId"),
        @Mapping(target = "customer.custName", source = "custName"),
        @Mapping(target = "customer.firstName", source = "firstName"),
        @Mapping(target = "customer.lastName", source = "lastName"),
        @Mapping(target = "bizCodes", source = "bizCode"),
        @Mapping(target = "receiver", source = "receiveInfo"),
        @Mapping(target = "sales", source = "salesInfo"),
        @Mapping(target = "promotions", source = "promotionAttr"),
        @Mapping(target = "orderPrice", source = "orderFeeAttr"),
        @Mapping(target = "inventoryReduceType", source = "orderAttr.inventoryReduceType"),
        @Mapping(target = "orderAttr", source = "orderAttr"),
        @Mapping(target = "orderType", source = "orderAttr.orderType"),
        @Mapping(target = "orderAttr.logisticsType", source = "orderAttr.logisticsType"),
        @Mapping(target = "orderAttr.payChannel", source = "orderAttr.payChannel"),
        @Mapping(target = "version", source = "version"),
        @Mapping(target = "loanCycle", source = "loanCycle"),
        @Mapping(target = "stepTemplate.templateName", source = "orderAttr.stepTemplateName"),
        @Mapping(target = "subOrders", ignore = true),
    })
    MainOrder convertMainOrder(TcOrderDO tcOrder);

    // ========  子订单 =========

    @Mappings({
        @Mapping(target = "primaryOrderId", source = "main.primaryOrderId"),
        @Mapping(target = "orderId", source = "order.orderId"),
        @Mapping(target = "orderStatus", source = "order.orderStatus"),
        @Mapping(target = "primaryOrderStatus", source = "main.primaryOrderStatus"),
        @Mapping(target = "orderChannel", source = "main.orderChannel"),
        @Mapping(target = "custId", source = "main.customer.custId"),
        @Mapping(target = "custName", source = "main.customer.custName"),
        @Mapping(target = "itemId", source = "order.itemSku.itemId"),
        @Mapping(target = "skuId", source = "order.itemSku.skuId"),
        @Mapping(target = "skuDesc", source = "order.itemSku.skuDesc"),
        @Mapping(target = "itemTitle", source = "order.itemSku.itemTitle"),
        @Mapping(target = "itemQuantity", source = "order.orderQty"),
        @Mapping(target = "itemPic", source = "order.itemSku.itemPic"),
        @Mapping(target = "orderPrice", source = "order.orderPrice.itemOriginAmt"),
        @Mapping(target = "salePrice", source = "order.itemSku.itemPrice.originPrice"),
        @Mapping(target = "realPrice", source = "order.orderPrice.orderRealAmt"),
        @Mapping(target = "gmtCreate", source = "order.gmtCreate"),
        @Mapping(target = "gmtModified", source = "order.gmtModified"),
        @Mapping(target = "sellerId", source = "main.seller.sellerId"),
        @Mapping(target = "sellerName", source = "main.seller.sellerName"),
        @Mapping(target = "bizCode", source = "main.bizCodes"),
        @Mapping(target = "loanCycle", source = "main.loanCycle"),
        @Mapping(target = "firstName", source = "main.customer.firstName"),
        @Mapping(target = "lastName", source = "main.customer.lastName"),
        @Mapping(target = "binOrIin", source = "main.seller.binOrIin"),
        @Mapping(target = "receiveInfo", source = "main.receiver"),
        @Mapping(target = "salesInfo", source = "main.sales"),
        @Mapping(target = "orderFeeAttr", source = "order.orderPrice"),
        @Mapping(target = "version", source = "order.version"),
        @Mapping(target = "primaryOrderFlag", expression = "java(com.aliyun.gts.gmall.platform.trade.common.constants.PrimaryOrderFlagEnum.SUB_ORDER.getCode())"),
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "snapshotPath", ignore = true),
        @Mapping(target = "reversalType", ignore = true),
        @Mapping(target = "custMemo", ignore = true),
        @Mapping(target = "payCartId", ignore = true),
        @Mapping(target = "promotionAttr.itemDivideDetails", source = "order.promotions.itemDivideDetails"),
        @Mapping(target = "evaluate", source = "order.evaluate"),
        @Mapping(target = "itemFeature", source = "order.itemSku"),
        @Mapping(target = "orderAttr", expression = "java(getSubOrderAttrDO(order, main))"),
        @Mapping(target = "displayOrderId", source = "order.displayOrderId"),
        @Mapping(target = "categoryCommissionRate", source = "order.categoryCommissionRate"),
        /*
        @Mapping(target = "orderAttr", source = "order.orderAttr"),
        @Mapping(target = "orderAttr.orderType", source = "main.orderType"),
        */
    })
    TcOrderDO convertSubOrder(SubOrder order, MainOrder main, CreatingOrder c);

    default OrderAttrDO getSubOrderAttrDO(SubOrder order, MainOrder main) {
        OrderAttrDO attr = new OrderAttrDO();
        if (order != null) {
            copyOrderAttrDO(order.getOrderAttr(), attr);
        }
        if (main != null) {
            attr.setOrderType(main.getOrderType());
        }
        return attr;
    }

    @Mappings({
            @Mapping(target = "logisticsType", expression  = "java(changeDeliveryType(from))"),
    })
    void copyOrderAttrDO(OrderAttrDO from, @MappingTarget OrderAttrDO to);

    /**
     * 存库ka类型还是保存到
     * @param from
     * @return
     */
    default Integer  changeDeliveryType(OrderAttrDO from) {
        return DeliveryTypeEnum.SELLER_KA.getCode().equals(from.getLogisticsType()) ? DeliveryTypeEnum.SELF_SERVICE.getCode() : from.getLogisticsType();
    }

    @Mappings({
        @Mapping(target = "orderId", source = "orderId"),
        @Mapping(target = "orderStatus", source = "orderStatus"),
        @Mapping(target = "orderQty", source = "itemQuantity"),
        @Mapping(target = "payCartId", source = "payCartId"),
        @Mapping(target = "gmtCreate", source = "gmtCreate"),
        @Mapping(target = "gmtModified", source = "gmtModified"),
        @Mapping(target = "itemSku", source = "itemFeature"),
        @Mapping(target = "itemSku.skuId", source = "skuId"),
        @Mapping(target = "itemSku.itemId", source = "itemId"),
        @Mapping(target = "itemSku.skuDesc", source = "skuDesc"),
        @Mapping(target = "itemSku.itemTitle", source = "itemTitle"),
        @Mapping(target = "itemSku.skuQty", source = "itemQuantity"),
        @Mapping(target = "itemSku.itemPic", source = "itemPic"),
        @Mapping(target = "itemSku.itemPrice", source = "orderFeeAttr.itemPrice"),
        @Mapping(target = "orderPrice", source = "orderFeeAttr"),
        @Mapping(target = "promotions.itemDivideDetails", source = "promotionAttr.itemDivideDetails"),
        @Mapping(target = "evaluate", source = "evaluate"),
        @Mapping(target = "orderAttr", source = "orderAttr"),
        @Mapping(target = "version", source = "version"),
        @Mapping(target = "categoryId", source = "itemFeature.categoryId"),
    })
    SubOrder convertSubOrder(TcOrderDO tcOrder);

    @Mappings({
        @Mapping(target = "categoryName", source = "itemCategory.name"),
        @Mapping(target = "categoryFeatures", source = "itemCategory.featureMap"),
    })
    OrderItemFeatureDO toOrderItemFeatureDO(ItemSku itemSku);


    OrderFeeAttrDO toOrderFeeAttrDO(OrderPrice orderPrice);


    // =======================================================
    // 多阶段
    // =======================================================

    @Mappings({
        @Mapping(target = "priceAttr", source = "price"),
    })
    TcStepOrderDO toTcStepOrderDO(StepOrder stepOrder);

    @Mappings({
        @Mapping(target = "price", source = "priceAttr"),
    })
    StepOrder toStepOrder(TcStepOrderDO stepOrder);

    StepOrderFeeDO toStepOrderFeeDO(StepOrderPrice price);
}
