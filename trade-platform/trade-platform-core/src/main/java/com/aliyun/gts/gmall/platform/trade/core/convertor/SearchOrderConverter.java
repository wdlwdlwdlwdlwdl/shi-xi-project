package com.aliyun.gts.gmall.platform.trade.core.convertor;

import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.*;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.StepOrderDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderChannelEnum;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcAsyncTaskDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.StepOrderFeatureDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.OrderPrice;
import org.mapstruct.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Mapper(componentModel = "spring")
public abstract class SearchOrderConverter {

    @Mappings({
        @Mapping(target = "custName", source = "customer.custName"),
        @Mapping(target = "custId", source = "customer.custId"),
        @Mapping(target = "firstName", source = "customer.firstName"),
        @Mapping(target = "lastName", source = "customer.lastName"),
        @Mapping(target = "sellerName", source = "seller.sellerName"),
        @Mapping(target = "sellerId", source = "seller.sellerId"),
        @Mapping(target = "sellerBin", source = "seller.binOrIin"),
        @Mapping(target = "orderStatus", source = "primaryOrderStatus"),
        @Mapping(target = "orderStage", source = "orderAttr.orderStage"),
        @Mapping(target = "orderId", source = "primaryOrderId"),
        @Mapping(target = "createTime", source = "gmtCreate"),
        @Mapping(target = "logisticsType", source = "orderAttr.logisticsType"),
        @Mapping(target = "sellerMemo", source = "orderAttr.sellerMemo"),
        @Mapping(target = "orderChannel", source = "orderChannel", qualifiedByName = "getOrderChannelName"),
        @Mapping(target = "receiverName", source = "receiver.receiverName"),
        @Mapping(target = "receiverPhone", source = "receiver.phone"),
        @Mapping(target = "receiverAddr", source = "receiver.deliveryAddr"),
        @Mapping(target = "evaluate" , source = "evaluate"),
        @Mapping(target = "sendTime", source = "orderAttr.sendTime"),
        @Mapping(target = "price", source = "orderPrice"),
        @Mapping(target = "extras", source = "orderAttr.extras"),
        @Mapping(target = "orderType", source = "orderAttr.orderType"),
        @Mapping(target = "reversalOrderStatus", source = "orderAttr.reversalOrderStatus"),
        @Mapping(target = "mergeOrderIds", source = "orderAttr.mergeOrderIds"),
        @Mapping(target = "tags", source = "orderAttr.tags"),
        @Mapping(target = "stepOrders", source = "stepOrders"),
        @Mapping(target = "stepTemplate", source = "stepTemplate"),
        @Mapping(target = "currentStepNo", source = "orderAttr.currentStepNo"),
        @Mapping(target = "stepContextProps", source = "orderAttr.stepContextProps"),
        @Mapping(target = "bizCodes", source = "bizCodes"),
        @Mapping(target = "accountPeriodMemo", source = "orderAttr.accountPeriodMemo"),
        @Mapping(target = "overSale", source = "orderAttr.overSale"),
        @Mapping(target = "payChannel", source = "orderAttr.payChannel"),
        @Mapping(target = "payTypeStr", source = "orderAttr.payType"),
        @Mapping(target = "additFreightPrice", source = "orderAttr.freightPrice"),
        @Mapping(target = "loanCycle", source = "loanCycle"),
        @Mapping(target = "loanStatus", source = "loanStatus"),
    })
    public abstract MainOrderDTO convertMainOrder(MainOrder mainOrder);

    public abstract List<OrderTaskDTO> convertOrderTasks(List<TcAsyncTaskDO> list);

    @Named("getOrderChannelName")
    protected String getOrderChannelName(String orderChannel) {
        OrderChannelEnum en = OrderChannelEnum.codeOf(orderChannel);
        return en == null ? orderChannel : en.getName();
    }


    @Mappings({
        @Mapping(target = "primaryOrderId", source = "primaryOrderId"),
        @Mapping(target = "orderStatus", source = "orderStatus"),
        @Mapping(target = "orderStage", source = "orderAttr.orderStage"),
        @Mapping(target = "price",expression = "java(mergePrice(subOrder.getOrderPrice()))"),
        @Mapping(target = "orderId", source = "orderId"),
        @Mapping(target = "itemTitle", source = "itemSku.itemTitle"),
        @Mapping(target = "itemQuantity", source = "orderQty"),
        @Mapping(target = "skuDesc", source = "itemSku.skuDesc"),
        @Mapping(target = "itemPic", source = "itemSku.itemPic"),
        @Mapping(target = "skuPic", source = "itemSku.skuPic"),
        @Mapping(target = "itemId", source = "itemSku.itemId"),
            @Mapping(target = "itemType", source = "itemFeature.itemType"),
        @Mapping(target = "skuId", source = "itemSku.skuId"),
        @Mapping(target = "createTime" , source = "gmtCreate"),
        @Mapping(target = "evaluate" , source = "evaluate"),
        @Mapping(target = "weight" , source = "itemSku.weight"),
        @Mapping(target = "extras", source = "orderAttr.extras"),
        @Mapping(target = "reversalOrderStatus", source = "orderAttr.reversalOrderStatus"),
        @Mapping(target = "itemDividePromotions", source = "promotions.itemDivideDetails"),
        @Mapping(target = "tags", source = "orderAttr.tags"),
        @Mapping(target = "feature", expression = "java(toFeature(subOrder))"),
    })
    public abstract SubOrderDTO convertSubOrder(SubOrder subOrder);

    public Map<String,String> toFeature(SubOrder subOrder){
        Map<String,String> map = new HashMap<>();
        if(subOrder.getItemSku().getStoredExt() != null){
            map.putAll(subOrder.getItemSku().getStoredExt());
        }
        return map;
    }
    @Mappings({
        @Mapping(target = "originPrice", source = "orderPrice.itemPrice.originPrice"),
        @Mapping(target = "itemPrice", source = "orderPrice.itemPrice.itemPrice"),
    })
    public abstract OrderDisplayPriceDTO mergePrice(OrderPrice orderPrice);

    @Mappings({
        @Mapping(target = "sellerMemo", source = "orderAttr.sellerMemo"),
        @Mapping(target = "sellerId", source = "seller.sellerId"),
        @Mapping(target = "sellerName", source = "seller.sellerName"),
        @Mapping(target = "orderId", source = "primaryOrderId"),
        @Mapping(target = "displayOrderId", source = "displayOrderId"),
        @Mapping(target = "receiverName", source = "receiver.receiverName"),
        @Mapping(target = "receiverPhone", source = "receiver.phone"),
        @Mapping(target = "receiverAddr", source = "receiver.deliveryAddr"),
        @Mapping(target = "sendTime", source = "orderAttr.sendTime"),
        @Mapping(target = "receivedTime", source = "orderAttr.confirmReceiveTime"),
        @Mapping(target = "evaluatedTime", source = "orderAttr.evaluateTime"),
        @Mapping(target = "logisticsType", source = "orderAttr.logisticsType"),
        @Mapping(target = "reversalStartTime", source = "orderAttr.reversalStartTime"),
        @Mapping(target = "reversalEndTime", source = "orderAttr.reversalEndTime"),
        @Mapping(target = "createTime", source = "gmtCreate"),
        @Mapping(target = "customerMemo", source = "custMemo"),
        @Mapping(target = "custName", source = "customer.custName"),
        @Mapping(target = "custId", source = "customer.custId"),
        @Mapping(target = "phone", source = "customer.phone"),
        @Mapping(target = "orderStatus", source = "primaryOrderStatus"),
        @Mapping(target = "orderStage", source = "orderAttr.orderStage"),
            @Mapping(target = "remark", source = "orderAttr.remark"),
        @Mapping(target = "evaluate" , source = "evaluate"),
        @Mapping(target = "price", source = "orderPrice"),
        @Mapping(target = "extras", source = "orderAttr.extras"),
        @Mapping(target = "orderChannel", source = "orderChannel", qualifiedByName = "getOrderChannelName"),
        @Mapping(target = "orderType", source = "orderAttr.orderType"),
        @Mapping(target = "orderExtends", source = "orderExtendList"),
        @Mapping(target = "reversalOrderStatus", source = "orderAttr.reversalOrderStatus"),
        @Mapping(target = "mergeOrderIds", source = "orderAttr.mergeOrderIds"),
        @Mapping(target = "tags", source = "orderAttr.tags"),
        @Mapping(target = "stepOrders", source = "stepOrders"),
        @Mapping(target = "receiver", source = "receiver"),
        @Mapping(target = "sales", source = "sales"),
        @Mapping(target = "stepTemplate", source = "stepTemplate"),
        @Mapping(target = "currentStepNo", source = "orderAttr.currentStepNo"),
        @Mapping(target = "stepContextProps", source = "orderAttr.stepContextProps"),
        @Mapping(target = "bizCodes", source = "bizCodes"),
        @Mapping(target = "accountPeriodMemo", source = "orderAttr.accountPeriodMemo"),
        @Mapping(target = "reasonCode", source = "orderAttr.reasonCode"),
        @Mapping(target = "payType", ignore = true),
        @Mapping(target = "payTime", ignore = true),
        @Mapping(target = "payChannel", source = "orderAttr.payChannel"),
        @Mapping(target = "overSale", source = "orderAttr.overSale"),
        @Mapping(target = "sellerPhone", source = "orderAttr.sellerPhone"),
        @Mapping(target = "seatNum", source = "sales.seatNum"),
        @Mapping(target = "bankCardNbr", source = "orderAttr.bankCardNbr"),
        @Mapping(target = "additFreightPrice", source = "orderAttr.freightPrice"),
    })
    protected abstract MainOrderDetailDTO convertOrderDetailDTO(MainOrder mainOrder);


    public MainOrderDetailDTO convertOrderDetailDTO(MainOrder mainOrder, OrderPay orderPay) {
        MainOrderDetailDTO d = convertOrderDetailDTO(mainOrder);
        if (d != null && orderPay != null) {
            d.setPayType(orderPay.getPayType());
            d.setPayTime(orderPay.getPayTime());
            d.setPayChannel(orderPay.getPayChannel());
            d.setPayBizFeature(orderPay.getBizFeature());
            d.setPayBizTags(orderPay.getBizTags());
        }
        return d;
    }

    @Mappings({
        @Mapping(target = "primaryOrderId", source = "primaryOrderId"),
        @Mapping(target = "orderStatus", source = "orderStatus"),
        @Mapping(target = "orderStage", source = "orderAttr.orderStage"),
        @Mapping(target = "reversalStatus", source = "reversalStatus"),
        @Mapping(target = "price",expression = "java(mergePrice(subOrder.getOrderPrice()))"),
        @Mapping(target = "orderId", source = "orderId"),
        @Mapping(target = "itemTitle", source = "itemSku.itemTitle"),
        @Mapping(target = "itemQuantity", source = "orderQty"),
        @Mapping(target = "skuDesc", source = "itemSku.skuDesc"),
        @Mapping(target = "itemPic", source = "itemSku.itemPic"),
        @Mapping(target = "skuPic", source = "itemSku.skuPic"),
        @Mapping(target = "itemId", source = "itemSku.itemId"),
        @Mapping(target = "skuId", source = "itemSku.skuId"),
        @Mapping(target = "createTime" , source = "gmtCreate"),
        @Mapping(target = "evaluate" , source = "evaluate"),
        @Mapping(target = "weight" , source = "itemSku.weight"),
        @Mapping(target = "extras", source = "orderAttr.extras"),
        @Mapping(target = "merchantSkuCode", source = "orderAttr.merchantSkuCode"),
        @Mapping(target = "orderExtends", source = "orderExtendList"),
        @Mapping(target = "reversalOrderStatus", source = "orderAttr.reversalOrderStatus"),
        @Mapping(target = "itemDividePromotions", source = "promotions.itemDivideDetails"),
        @Mapping(target = "tags", source = "orderAttr.tags"),
        @Mapping(target = "feature", expression = "java(toFeature(subOrder))"),
        @Mapping(target = "skuQuoteId", source = "orderAttr.skuQuoteId"),
    })
    public abstract SubOrderDetailDTO convertSubDetailOrder(SubOrder subOrder);


    // ======== stepOrder ========

    public StepOrderDTO toStepOrderDTO(StepOrder stepOrder) {
        if (stepOrder == null) {
            return null;
        }
        StepOrderDTO dst = new StepOrderDTO();
        fillStepOrderDTO(dst, stepOrder);
        fillStepOrderDTO(dst, stepOrder.getPrice());
        fillStepOrderDTO(dst, stepOrder.getFeatures());
        return dst;
    }

    public abstract void fillStepOrderDTO(@MappingTarget StepOrderDTO dst, StepOrder src);

    public abstract void fillStepOrderDTO(@MappingTarget StepOrderDTO dst, StepOrderPrice src);

    public abstract void fillStepOrderDTO(@MappingTarget StepOrderDTO dst, StepOrderFeatureDO src);
}
