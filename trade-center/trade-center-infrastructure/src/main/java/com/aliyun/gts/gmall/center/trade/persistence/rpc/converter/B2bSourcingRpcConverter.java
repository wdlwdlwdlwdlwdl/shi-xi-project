package com.aliyun.gts.gmall.center.trade.persistence.rpc.converter;

import com.alibaba.fastjson.JSONObject;
//import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.AwardQuoteDetailDTO;
//import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.PricingBillDTO;
//import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.QuoteDTO;
//import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.QuoteDetailDTO;
//import com.aliyun.gts.gcai.platform.sourcing.common.constant.QuoteDetailExtras;
//import com.aliyun.gts.gcai.platform.sourcing.common.type.PricingBillStatus;
import com.aliyun.gts.gmall.center.trade.domain.entity.b2b.SourcingBillInfo;
import com.aliyun.gts.gmall.center.trade.domain.entity.b2b.SourcingBillInfo.SourcingItem;
import com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

//@Mapper(componentModel = "spring")
//public abstract class B2bSourcingRpcConverter {

    //@Mappings({
    //        @Mapping(target = "billId", source = "bill.id"),
    //        @Mapping(target = "custId", source = "bill.purchaserId"),
    //        @Mapping(target = "items", expression = "java(getItems(bill, quotes))"),
    //        @Mapping(target = "waitOrder", source = "bill", qualifiedByName = "isWaitOrder"),
    //        @Mapping(target = "passBack", source = "bill"),
    //})
    //public abstract SourcingBillInfo convert(PricingBillDTO bill, List<QuoteDTO> quotes);
    //
    //@Named("isWaitOrder")
    //protected boolean isWaitOrder(PricingBillDTO bill) {
    //    return PricingBillStatus.pass_approve.getValue().equals(bill.getStatus())
    //            || PricingBillStatus.part_ordered.getValue().equals(bill.getStatus());
    //}
    //
    //protected List<SourcingItem> getItems(PricingBillDTO bill, List<QuoteDTO> quotes) {
    //    List<SourcingItem> list = new ArrayList<>();
    //    if (CollectionUtils.isEmpty(quotes) || CollectionUtils.isEmpty(bill.getAwardQuote())) {
    //        return list;
    //    }
    //    Map<Long, AwardQuoteDetailDTO> awaMap = bill.getAwardQuote().stream()
    //            .collect(Collectors.toMap(AwardQuoteDetailDTO::getQuoteDetailId, Function.identity()));
    //    for (QuoteDTO quote : quotes) {
    //        if (CollectionUtils.isEmpty(quote.getDetails())) {
    //            continue;
    //        }
    //        for (QuoteDetailDTO detail : quote.getDetails()) {
    //            AwardQuoteDetailDTO ad = awaMap.get(detail.getId());
    //            if (ad == null) {
    //                continue;   // 未授标的丢弃
    //            }
    //            SourcingItem item = convert(bill, quote, detail, ad);
    //            if (item != null) {
    //                list.add(item);
    //            }
    //        }
    //    }
    //    return list;
    //}
    //
    //@Mappings({
    //        @Mapping(target = "detailId", source = "detail.id"),
    //        @Mapping(target = "itemId", source = "detail.quoteFeature.extend", qualifiedByName = "getItemId"),
    //        @Mapping(target = "skuId", source = "detail.quoteFeature.extend", qualifiedByName = "getSkuId"),
    //        @Mapping(target = "unitPrice", source = "detail.price"),
    //        @Mapping(target = "freightAmt", source = "detail.freightCost"),
    //        @Mapping(target = "totalAmt", source = "detail", qualifiedByName = "getTotalAmt"),
    //        @Mapping(target = "awardNum", source = "ad.awardNum"),
    //        @Mapping(target = "ordered", expression = "java(ad.getPrimaryOrderId() != null)"),
    //        @Mapping(target = "priceStartTime", source = "quote.priceStartTime"),
    //        @Mapping(target = "priceEndTime", source = "quote.priceEndTime"),
    //})
    //protected abstract SourcingItem convert(PricingBillDTO bill, QuoteDTO quote, QuoteDetailDTO detail, AwardQuoteDetailDTO ad);
    //
    //@Named("getTotalAmt")
    //protected Long getTotalAmt(QuoteDetailDTO detail) {
    //    return NumUtils.getNullZero(detail.getPrice()) * NumUtils.getNullZero(detail.getAwardNum())
    //            + NumUtils.getNullZero(detail.getFreightCost());
    //}
    //
    //@Named("getItemId")
    //protected Long getItemId(JSONObject detailExtend) {
    //    return QuoteDetailExtras.getItemId(detailExtend);
    //}
    //
    //@Named("getSkuId")
    //protected Long getSkuId(JSONObject detailExtend) {
    //    return QuoteDetailExtras.getSkuId(detailExtend);
    //}
//}
