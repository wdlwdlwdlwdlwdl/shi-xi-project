package com.aliyun.gts.gmall.center.trade.persistence.repository.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

//import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.AwardQuoteDetailDTO;
//import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.PricingBillDTO;
//import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.QuoteDTO;
//import com.aliyun.gts.gcai.platform.sourcing.api.facade.PricingBillReadFacade;
//import com.aliyun.gts.gcai.platform.sourcing.api.facade.PricingBillWriteFacade;
//import com.aliyun.gts.gcai.platform.sourcing.api.facade.QuotePriceReadFacade;
//import com.aliyun.gts.gcai.platform.sourcing.api.facade.QuotePriceWriteFacade;
//import com.aliyun.gts.gcai.platform.sourcing.common.input.CommandRequest;
//import com.aliyun.gts.gcai.platform.sourcing.common.type.PricingBillStatus;
//import com.aliyun.gts.gcai.platform.sourcing.common.type.QuoteStatusType;
import com.aliyun.gts.gmall.center.trade.domain.entity.b2b.SourcingBillInfo;
import com.aliyun.gts.gmall.center.trade.domain.entity.b2b.SourcingBillOrder;
import com.aliyun.gts.gmall.center.trade.domain.entity.b2b.SourcingBillOrder.OrderInfo;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.B2bSourcingRepository;
//import com.aliyun.gts.gmall.center.trade.persistence.rpc.constants.ExtRemoteErrorCode;
//import com.aliyun.gts.gmall.center.trade.persistence.rpc.converter.B2bSourcingRpcConverter;
import com.aliyun.gts.gmall.framework.api.rest.dto.CommonByIdQuery;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.persistence.rpc.util.RpcUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class B2bSourcingRepositoryImpl implements B2bSourcingRepository {

    //@Autowired
    //private PricingBillReadFacade pricingBillReadFacade;
    //
    //@Autowired
    //private PricingBillWriteFacade pricingBillWriteFacade;
    //
    //@Autowired
    //private QuotePriceWriteFacade quotePriceWriteFacade;
    //
    //@Autowired
    //private QuotePriceReadFacade quotePriceReadFacade;

//    @Autowired
//    private B2bSourcingRpcConverter b2bSourcingRpcConverter;

    @Override
    public SourcingBillInfo querySourcingBill(String billId) {
        if (!StringUtils.isNumeric(billId)) {
            return null;
        }
        //PricingBillDTO bill = queryBill(Long.parseLong(billId));
        //if (bill == null) {
        //    return null;
        //}
        //Set<Long> quoteIds = bill.getAwardQuote().stream()
        //        .map(AwardQuoteDetailDTO::getQuoteId).collect(Collectors.toSet());
        //List<QuoteDTO> quoteList = new ArrayList<>();
        //for (Long quoteId : quoteIds) {
        //    QuoteDTO quote = queryQuote(quoteId);
        //    if (quote == null) {
        //        throw new GmallException(ExtRemoteErrorCode.B2B_SOURCING_BILL_ILLEGAL);
        //    }
        //    quoteList.add(quote);
        //}
        //return b2bSourcingRpcConverter.convert(bill, quoteList);
        return null;
    }

    //private PricingBillDTO queryBill(Long billId) {
    //    CommonByIdQuery q = new CommonByIdQuery();
    //    q.setId(billId);
    //    RpcResponse<PricingBillDTO> resp = RpcUtils.invokeRpc(
    //            () -> pricingBillReadFacade.queryById(q),
    //            "pricingBillReadFacade::queryById", I18NMessageUtils.getMessage("bid.document.query"), q);  //# "查决标单"
    //    return resp.getData();
    //}
    //
    //private QuoteDTO queryQuote(Long quoteId) {
    //    CommonByIdQuery q = new CommonByIdQuery();
    //    q.setId(quoteId);
    //    RpcResponse<QuoteDTO> resp = RpcUtils.invokeRpc(
    //            () -> quotePriceReadFacade.queryById(q),
    //            "quotePriceReadFacade::queryById", I18NMessageUtils.getMessage("quote.document.query"), q);  //# "查报价单"
    //    return resp.getData();
    //}

    @Override
    public void saveOrder(SourcingBillOrder order) {
        //if (CollectionUtils.isEmpty(order.getOrders())) {
        //    return;
        //}
        //
        //Map<String, OrderInfo> dMap = order.getOrders().stream()
        //        .collect(Collectors.toMap(o -> o.getItem().getDetailId(), Function.identity()));
        //PricingBillDTO bill = (PricingBillDTO) order.getBill().getPassBack();
        //boolean allOrdered = true;
        //Set<Long> orderedQuotes = new HashSet<>();
        //Set<Long> noOrderQuotes = new HashSet<>();
        //for (AwardQuoteDetailDTO detail : bill.getAwardQuote()) {
        //    OrderInfo o = dMap.get(detail.getQuoteDetailId().toString());
        //    if (o != null) {
        //        detail.setSubOrderId(o.getSubOrder().getOrderId());
        //        detail.setPrimaryOrderId(o.getSubOrder().getPrimaryOrderId());
        //    }
        //    if (detail.getPrimaryOrderId() == null) {
        //        allOrdered = false;
        //        noOrderQuotes.add(detail.getQuoteId());
        //    } else {
        //        orderedQuotes.add(detail.getQuoteId());
        //    }
        //}
        //
        //// 先更新报价单
        //for (Long quoteId : orderedQuotes) {
        //    Integer status = noOrderQuotes.contains(quoteId) ?
        //            QuoteStatusType.part_ordered.getValue() : QuoteStatusType.ordered.getValue();
        //    QuoteDTO up = new QuoteDTO();
        //    up.setId(quoteId);
        //    up.setStatus(status);
        //    CommandRequest<QuoteDTO> req = new CommandRequest<>();
        //    req.setData(up);
        //    RpcUtils.invokeRpc(() -> quotePriceWriteFacade.updateStatus(req),
        //            "quotePriceWriteFacade.updateStatus", I18NMessageUtils.getMessage("update.quote.document"), req);  //# "更新报价单"
        //}
        //
        //// 更新决标单
        //{
        //    PricingBillDTO up = new PricingBillDTO();
        //    up.setId(Long.parseLong(order.getBill().getBillId()));
        //    up.setAwardQuote(bill.getAwardQuote());
        //    if (allOrdered) {
        //        up.setStatus(PricingBillStatus.ordered.getValue());
        //    } else {
        //        up.setStatus(PricingBillStatus.part_ordered.getValue());
        //    }
        //    CommandRequest<PricingBillDTO> req = new CommandRequest<>();
        //    req.setData(up);
        //    RpcUtils.invokeRpc(() -> pricingBillWriteFacade.update(req),
        //            "pricingBillWriteFacade.update", I18NMessageUtils.getMessage("update.bid.document"), req);  //# "更新决标单"
        //}
    }
}
