package com.aliyun.gts.gmall.platform.trade.core.convertor;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.common.ReceiverDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics.LogisticsInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.CheckReversalRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.CreateReversalRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.ReversalQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.ReversalSubOrderInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.OrderDisplayPriceDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.StepOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.SubOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.MainReversalDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.ReversalFeatureDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.ReversalReasonDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.SubReversalDTO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcLogisticsDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcReversalDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcReversalReasonDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.ReceiverInfoDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.ReversalFeatureDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.StepOrderFeatureDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.OrderPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.ReversalSearchQuery;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.ReversalSearchResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.SubReversal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;


@Mapper(componentModel = "spring")
public interface ReversalConverter {

    ReversalReasonDTO toReversalReasonDTO(TcReversalReasonDO reason);

    // ============ do --> domain ============

    @Mappings({
        @Mapping(target = "mainOrder.primaryOrderId", source = "primaryOrderId"),
        @Mapping(target = "reversalReasonCode", source = "reversalReason"),
        @Mapping(target = "mainOrder.seller.sellerId", source = "sellerId"),
        @Mapping(target = "mainOrder.seller.sellerName", source = "sellerName"),
    })
    MainReversal toMainReversal(TcReversalDO reversal);

    @Mappings({
        @Mapping(target = "subOrder.primaryOrderId", source = "primaryOrderId"),
        @Mapping(target = "subOrder.orderId", source = "orderId"),
    })
    SubReversal toSubReversal(TcReversalDO reversal);

    @Mappings({
        @Mapping(target = "extra", source = "extras"),
    })
    ReversalFeatureDTO toReversalFeatureDTO(ReversalFeatureDO f);
    // ============ domain --> do ============

    @Mappings({
        @Mapping(target = "primaryOrderId", source = "mainOrder.primaryOrderId"),
        @Mapping(target = "orderId", source = "mainOrder.primaryOrderId"),
        @Mapping(target = "reversalId", source = "primaryReversalId"),
        @Mapping(target = "isReversalMain", constant = "true"),
        @Mapping(target = "sellerId", source = "mainOrder.seller.sellerId"),
        @Mapping(target = "sellerName", source = "mainOrder.seller.sellerName"),
        @Mapping(target = "reversalReason", source = "reversalReasonCode"),
    })
    TcReversalDO toTcReversalDO(MainReversal main);

    @Mappings({
        @Mapping(target = "primaryOrderId", source = "subOrder.primaryOrderId"),
        @Mapping(target = "orderId", source = "subOrder.orderId"),
        @Mapping(target = "isReversalMain", constant = "false"),
        @Mapping(target = "itemId", source = "subOrder.itemSku.itemId"),
        @Mapping(target = "skuId", source = "subOrder.itemSku.skuId"),
    })
    TcReversalDO toTcReversalDO(SubReversal sub);

    ReceiverInfoDO toReceiverInfoDO(ReceiverDTO receiver);

    @Mappings({
        @Mapping(target = "primaryReversalId", source = "re.primaryReversalId"),
        @Mapping(target = "reversalId", source = "re.primaryReversalId"),
        @Mapping(target = "companyType", source = "lo.companyType"),
        @Mapping(target = "logisticsId", source = "lo.logisticsId"),
        @Mapping(target = "custId", source = "re.custId"),
        @Mapping(target = "sellerId", source = "re.mainOrder.seller.sellerId"),
        @Mapping(target = "receiverName", source = "re.reversalFeatures.receiver.receiverName"),
        @Mapping(target = "receiverPhone", source = "re.reversalFeatures.receiver.phone"),
        @Mapping(target = "receiverAddr", source = "re.reversalFeatures.receiver.deliveryAddr"),
        @Mapping(target = "primaryOrderId", source = "re.mainOrder.primaryOrderId"),
        @Mapping(target = "type", constant = "1"),
    })
    TcLogisticsDO toTcLogisticsDO(MainReversal re, LogisticsInfoRpcReq lo);


    // ============ dto --> domain ============

    @Mappings({
        @Mapping(target = "subReversals", source = "subOrders"),
        @Mapping(target = "mainOrder.primaryOrderId", source = "primaryOrderId"),
        @Mapping(target = "reversalFeatures.itemReceived", source = "itemReceived"),
        @Mapping(target = "reversalFeatures.cancelFreightAmt", source = "cancelFreightAmt"),
    })
    MainReversal toMainReversal(CreateReversalRpcReq req);

    @Mappings({
        @Mapping(target = "mainOrder.primaryOrderId", source = "primaryOrderId")
    })
    MainReversal toMainReversal(CheckReversalRpcReq req);

    @Mappings({
        @Mapping(target = "subOrder.orderId", source = "orderId")
    })
    SubReversal toSubReversal(ReversalSubOrderInfo req);

    @Mappings({
        @Mapping(target = "pageNum", source = "page.pageNo"),
        @Mapping(target = "pageSize", source = "page.pageSize"),
        @Mapping(target = "customerLastName", source = "lastName"),
        @Mapping(target = "customerFirstName", source = "firstName"),
    })
    ReversalSearchQuery toReversalSearchQuery(ReversalQueryRpcReq req);


    // ============ domain --> dto ============

    PageInfo<MainReversalDTO> toMainReversalPages(ReversalSearchResult result);

    @Mappings({
        @Mapping(target = "primaryOrderId", source = "mainOrder.primaryOrderId"),
        @Mapping(target = "itemReceived", source = "reversalFeatures.itemReceived"),
        @Mapping(target = "orderInfo", source = "mainOrder"),
    })
    MainReversalDTO toMainReversalDTO(MainReversal reversal);

    @Mappings({
        @Mapping(target = "orderId", source = "subOrder.orderId"),
        @Mapping(target = "orderInfo", source = "subOrder"),
    })
    SubReversalDTO toSubReversalDTO(SubReversal reversal);

    @Mappings({
        @Mapping(target = "price", source = "orderPrice"),
        @Mapping(target = "orderStatus", source = "primaryOrderStatus"),
        @Mapping(target = "orderStage", source = "orderAttr.orderStage"),
        @Mapping(target = "sendTime", source = "orderAttr.sendTime"),
        @Mapping(target = "confirmReceiveTime", source = "orderAttr.confirmReceiveTime"),
        @Mapping(target = "currentStepNo", source = "orderAttr.currentStepNo"),
        @Mapping(target = "stepTemplate", source = "stepTemplate"),
        @Mapping(target = "stepContextProps", source = "orderAttr.stepContextProps"),
        @Mapping(target = "additFreightPrice", source = "orderAttr.freightPrice"),
    })
    MainOrderDTO toBaseOrderDTO(MainOrder order);

    @Mappings({
        @Mapping(target = "itemId", source = "itemSku.itemId"),
        @Mapping(target = "skuId", source = "itemSku.skuId"),
        @Mapping(target = "itemPic", source = "itemSku.itemPic"),
        @Mapping(target = "skuPic", source = "itemSku.skuPic"),
        @Mapping(target = "itemTitle", source = "itemSku.itemTitle"),
        @Mapping(target = "itemQuantity", source = "orderQty"),
        @Mapping(target = "skuDesc", source = "itemSku.skuDesc"),
        @Mapping(target = "price", source = "orderPrice"),
        @Mapping(target = "orderStage", source = "orderAttr.orderStage"),
    })
    SubOrderDTO toBaseOrderDTO(SubOrder order);

    @Mappings({
        @Mapping(target = "originPrice", source = "itemPrice.originPrice"),
        @Mapping(target = "itemPrice", source = "itemPrice.itemPrice"),
    })
    OrderDisplayPriceDTO toOrderDisplayPriceDTO(OrderPrice price);


    // ======== stepOrder ========

    default StepOrderDTO toStepOrderDTO(StepOrder stepOrder) {
        if (stepOrder == null) {
            return null;
        }
        StepOrderDTO dst = new StepOrderDTO();
        fillStepOrderDTO(dst, stepOrder);
        fillStepOrderDTO(dst, stepOrder.getPrice());
        fillStepOrderDTO(dst, stepOrder.getFeatures());
        return dst;
    }

    void fillStepOrderDTO(@MappingTarget StepOrderDTO dst, StepOrder src);

    void fillStepOrderDTO(@MappingTarget StepOrderDTO dst, StepOrderPrice src);

    void fillStepOrderDTO(@MappingTarget StepOrderDTO dst, StepOrderFeatureDO src);
}
