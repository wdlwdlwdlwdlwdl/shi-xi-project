package com.aliyun.gts.gmall.center.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.center.trade.core.domainservice.SellerSkuService;
import com.aliyun.gts.gmall.center.trade.core.dto.SkuSalesCount;
import com.aliyun.gts.gmall.platform.item.api.dto.input.skuquote.SaleInfoModifyReq;
import com.aliyun.gts.gmall.platform.item.api.facade.skuquote.OutModelChangeNoticeWriteFacade;
import com.aliyun.gts.gmall.platform.promotion.common.util.DateUtils;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.extension.search.defaultimpl.DefaultOrderSearchExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ListOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderQueryOption;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Seller;
import com.aliyun.gts.gmall.searcher.common.domain.request.SkuSalesSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;


@Slf4j
@Service
public class SellerSkuServiceImpl implements SellerSkuService {


    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private DefaultOrderSearchExt defaultOrderSearchExt;

    @Autowired
    private OutModelChangeNoticeWriteFacade outModelChangeNoticeWriteFacade;


    private final static List ORDER_STATUS = List.of(
//            OrderStatusEnum.PAYMENT_CONFIRMED.getCode(),
//            OrderStatusEnum.ACCEPTED_BY_MERCHANT.getCode(),
//            OrderStatusEnum.DELIVERY_TO_DC.getCode(),
//            OrderStatusEnum.WAITING_FOR_COURIER.getCode(),
//            OrderStatusEnum.DELIVERY.getCode(),
//            OrderStatusEnum.READY_FOR_PICKUP.getCode(),
            OrderStatusEnum.COMPLETED.getCode()
//            OrderStatusEnum.RETURNING_TO_MERCHANT.getCode(),
//            OrderStatusEnum.CANCEL_REQUESTED.getCode(),
//            OrderStatusEnum.CANCELLED.getCode(),
//            OrderStatusEnum.CANCEL_FAILED.getCode()
    );


    @Override
    public Boolean syncUpdateSkuSales(Long primaryOrderId) {
        MainOrder mainOrder = orderQueryAbility.getMainOrder(primaryOrderId, OrderQueryOption.builder().build());
        if (null != mainOrder) {
            List<SubOrder> subOrders = mainOrder.getSubOrders();
            for (SubOrder subOrder : subOrders) {
                SkuSalesCount skuSales = calculateSkuSales(subOrder, mainOrder);
                syncUpdate(skuSales);
            }
            return true;
        }
        return false;
    }


    /**
     * 根据时间范围自动同步
     * @param begin
     * @param end
     * @return
     */
    @Override
    public Boolean autoSync(Date begin, Date end, Long sellerId) {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder queryBuilder = new SearchSourceBuilder();
        queryBuilder.sort(new FieldSortBuilder("gmt_create").order(SortOrder.DESC));
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.filter(new TermQueryBuilder("primary_order_flag", "1"));
        if (Objects.nonNull(begin) || Objects.nonNull(end)) {
            RangeQueryBuilder range = new RangeQueryBuilder("gmt_create");
            if (Objects.nonNull(begin)) {
                range.gte(begin);
            }
            if (Objects.nonNull(end)) {
                range.lte(end);
            }
            boolQueryBuilder.filter(range);
        }
        if (null != sellerId && sellerId != 0) {
            boolQueryBuilder.filter(new TermQueryBuilder("seller_id", sellerId));
        }
        boolQueryBuilder.filter(new TermsQueryBuilder("order_status", ORDER_STATUS));
        queryBuilder.query(boolQueryBuilder);
        searchRequest.source(queryBuilder);
        Integer fromIndex = 0;
        Integer size = 20 ;
        while (true) {
            queryBuilder.from(fromIndex);
            queryBuilder.size(size);
            TradeBizResult<ListOrder> searchResult = defaultOrderSearchExt.search(searchRequest);
            ListOrder data = searchResult.getData();
            List<MainOrder> orderList = data.getOrderList();
            for (MainOrder mainOrder : orderList) {
                syncUpdateSkuSales(mainOrder.getPrimaryOrderId());
            }
            if (searchResult.getData().getOrderList().size() < size) {
                break;
            } else {
                fromIndex = fromIndex + searchResult.getData().getOrderList().size();
            }
        }
        return true;
    }



    private void syncUpdate(SkuSalesCount skuSales) {
        SaleInfoModifyReq saleInfoModifyReq = new SaleInfoModifyReq();
        saleInfoModifyReq.setSellerId(skuSales.getSellerId());
        saleInfoModifyReq.setItemId(skuSales.getItemId());
        saleInfoModifyReq.setSkuId(skuSales.getSkuId());
        saleInfoModifyReq.setSkuSalesTotalCount(skuSales.getSkuTotalSales().longValue());
        saleInfoModifyReq.setSkuLastThirtyDaySalesTotalCount(skuSales.getSkuLastThirtyDaySales().longValue());
        saleInfoModifyReq.setSkuLastThirtyDay(new Date(skuSales.getSkuLastSaleDate()));

        saleInfoModifyReq.setItemSalesTotalCount(skuSales.getItemTotalSales().longValue());
        saleInfoModifyReq.setItemLastThirtyDaySalesTotalCount(skuSales.getItemLastThirtyDaySales().longValue());
        saleInfoModifyReq.setItemLastThirtyDay(new Date(skuSales.getItemLastSaleDate()));
        outModelChangeNoticeWriteFacade.saleInfoChangeNotice(saleInfoModifyReq);
    }



    private SkuSalesCount calculateSkuSales(SubOrder subOrder, MainOrder mainOrder) {
        Seller seller = mainOrder.getSeller();
        Long sellerId = seller.getSellerId();
        ItemSku itemSku = subOrder.getItemSku();
        Long itemId = itemSku.getItemId();
        Long skuId = itemSku.getSkuId();

        Result skuResult = getResult(sellerId, itemId, skuId);
        SkuSalesSearchRequest skuSalesSearchRequest = skuResult.skuSalesSearchRequest();
        // 无时间限制买家级别的总销量
        TradeBizResult<Double> skuTotalSales = defaultOrderSearchExt.searchForSkuSales(skuSalesSearchRequest); //无时间限制
        log.info("skuTotalSales:{}", skuTotalSales.getData());
        //最近30天
        resetLastThirtyDay(skuResult);
        TradeBizResult<Double> skuLastThirtyDaySales = defaultOrderSearchExt.searchForSkuSales(skuResult.skuSalesSearchRequest());
        log.info("skuLastThirtyDaySales:{}", skuLastThirtyDaySales.getData());
        //skuLastSaleDate
        TradeBizResult<Long> skuLastSaleDate = defaultOrderSearchExt.searchLastSaleDate(skuResult.skuSalesSearchRequest());
        log.info("skuLastSaleDate:{}", skuLastSaleDate.getData());

        Result itemResult = getResult(sellerId, itemId, null);
        SkuSalesSearchRequest itemSalesSearchRequest = itemResult.skuSalesSearchRequest();
        TradeBizResult<Double> itemTotalSales = defaultOrderSearchExt.searchForSkuSales(itemSalesSearchRequest); //无时间限制
        log.info("itemTotalSales:{}", itemTotalSales.getData());

        resetLastThirtyDay(itemResult);
        TradeBizResult<Double> itemLastThirtyDaySales = defaultOrderSearchExt.searchForSkuSales(itemResult.skuSalesSearchRequest());
        log.info("itemLastThirtyDaySales:{}", itemLastThirtyDaySales.getData());

        TradeBizResult<Long> itemLastSaleDate = defaultOrderSearchExt.searchLastSaleDate(itemResult.skuSalesSearchRequest());
        log.info("itemLastSaleDate:{}", itemLastSaleDate.getData());


        // build  to item
        SkuSalesCount salesInfo = SkuSalesCount.builder()
                .sellerId(sellerId)
                .itemId(itemId)
                .skuId(skuId)
                .skuTotalSales(skuTotalSales.getData())
                .skuLastThirtyDaySales(skuLastThirtyDaySales.getData())
                .skuLastSaleDate(skuLastSaleDate.getData())
                .itemTotalSales(itemTotalSales.getData())
                .itemLastThirtyDaySales(itemLastThirtyDaySales.getData())
                .itemLastSaleDate(itemLastSaleDate.getData())
                .build();
        return salesInfo;
    }

    private static void resetLastThirtyDay(Result result) {
        result.skuSalesSearchRequest().setBeginTime(DateUtils.add(result.now(), -30)); //最近30天
        result.skuSalesSearchRequest().setEndTime(result.now());
    }

    @NotNull
    private static Result getResult(Long sellerId, Long itemId, Long skuId) {
        SkuSalesSearchRequest skuSalesSearchRequest = new SkuSalesSearchRequest();
        skuSalesSearchRequest.setSellerId(sellerId);
        skuSalesSearchRequest.setItemId(itemId);
        if (null != skuId) {
            skuSalesSearchRequest.setSkuIds(List.of(skuId));
        }
        //订单状态值
        skuSalesSearchRequest.setOrderStatus(ORDER_STATUS);
        Date now = new Date();
        Date yearLastDay = DateUtils.yearLastDay(now, -1);
        skuSalesSearchRequest.setBeginTime(yearLastDay);
        skuSalesSearchRequest.setEndTime(now);
        Result result = new Result(skuSalesSearchRequest, now);
        return result;
    }

    private record Result(SkuSalesSearchRequest skuSalesSearchRequest, Date now) {
    }
}