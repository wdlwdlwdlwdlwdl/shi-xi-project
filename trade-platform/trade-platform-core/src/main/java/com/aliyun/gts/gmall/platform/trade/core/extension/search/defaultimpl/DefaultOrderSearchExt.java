package com.aliyun.gts.gmall.platform.trade.core.extension.search.defaultimpl;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.promotion.common.util.JsonUtils;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.convertor.OrderConverter;
import com.aliyun.gts.gmall.platform.trade.core.extension.search.OrderSearchExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ListOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.ErrorUtils;
import com.aliyun.gts.gmall.platform.trade.domain.wrapper.OrderQueryWrapper;
import com.aliyun.gts.gmall.searcher.common.SearchClient;
import com.aliyun.gts.gmall.searcher.common.consts.AggsType;
import com.aliyun.gts.gmall.searcher.common.domain.request.SimpleSearchRequest;
import com.aliyun.gts.gmall.searcher.common.domain.request.SkuSalesSearchRequest;
import com.aliyun.gts.gmall.searcher.common.domain.result.DefaultSearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.testng.collections.Lists;

import java.util.*;
import java.util.stream.Collectors;

import static com.aliyun.gts.gmall.platform.trade.core.util.SearchUtils.obj2Long;

@Slf4j
@Component
public class DefaultOrderSearchExt implements OrderSearchExt {
    public static final int MAX_SEARCH_SIZE = 500;

    @Autowired
    private SearchClient searchClient;

    @Value("${opensearch.app.tcOrder}")
    String appName;
    @Value("${elasticsearch.name.tcOrder:order}")
    private String esIndexName;

    @Autowired
    TcOrderRepository tcOrderRepository;

    @Autowired
    OrderConverter orderConverter;

    @Autowired
    OrderQueryAbility orderQueryAbility;

    @Override
    public TradeBizResult<ListOrder> search(SimpleSearchRequest request) {
        request.setAppName(appName);
        DefaultSearchResult result = searchClient.search(request);
        if (!result.isSuccess()) {
            throw new GmallException(ErrorUtils.mapCode(
                    String.valueOf(result.getResultCode()),
                    I18NMessageUtils.getMessage("search.failed")+":" + result.getResultMsg()));  //# "搜索失败
        }
        List<Map<String, Object>> listItem = result.getSearchItems();
        List<Long> primaryIds = listItem.stream().map(m -> {
            String primaryOrderId = m.get("primary_order_id").toString();
            return Long.valueOf(primaryOrderId);
        }).collect(Collectors.toList());
        //List<Map<String,Object>> resultList = searchByPrimaryIds(primaryIdSet);
        //ListOrder listOrder = new ListOrder();
        Long sellerId = null;
        for(SimpleSearchRequest.KeyAndValue<String, String> kv : request.getQueryParams()){
            if("seller_id".equals(kv.getKey())){
                sellerId = Long.valueOf(kv.getValue());
            }
        }
        ListOrder listOrder = queryOrders(sellerId,primaryIds);
        listOrder.setTotal(result.getTotalCount());
        return TradeBizResult.ok(listOrder);
    }

    @Override
    public TradeBizResult<ListOrder> search(SearchRequest request) {
        request.indices(esIndexName);
        SearchResponse response = searchClient.search(request);
        List<Long> primaryIds = Arrays.stream(response.getHits().getHits())
                .map(SearchHit::getSourceAsMap)
                .map(map->obj2Long(map.get("order_id")))
                .collect(Collectors.toList());
        ListOrder listOrder = queryOrders(null, primaryIds);
        log.info("queryOrders primaryIds={},dbOrderList size = {}",primaryIds, listOrder.getOrderList().size());
        log.info("search listOrder={}", JsonUtils.toJSONString(listOrder));
        listOrder.setTotal((int) response.getHits().getTotalHits().value);
        return TradeBizResult.ok(listOrder);
    }





    @Override
    public TradeBizResult<Double> searchForSkuSales(SkuSalesSearchRequest request) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.filter(new TermQueryBuilder("item_id", request.getItemId()));
        List<Long> skuIds = request.getSkuIds();
        if (CollectionUtils.isNotEmpty(skuIds)) {
            boolQueryBuilder.filter(new TermsQueryBuilder("sku_id", skuIds));
        }
        if (null != request.getSellerId()) {
            boolQueryBuilder.filter(new TermQueryBuilder("seller_id", request.getSellerId()));
        }
        boolQueryBuilder.filter(new TermsQueryBuilder("order_status", request.getOrderStatus()));
        //统计的时间范围 目前以创建时间为查询条件
        if (Objects.nonNull(request.getBeginTime()) || Objects.nonNull(request.getEndTime())) {
            RangeQueryBuilder range = new RangeQueryBuilder("payment_date");
            if (Objects.nonNull(request.getBeginTime())) {
                range.gte(request.getBeginTime());
            }
            if (Objects.nonNull(request.getEndTime())) {
                range.lte(request.getEndTime());
            }
            boolQueryBuilder.filter(range);
        }
        try {
            double itemQuantity = searchClient.aggs("item_quantity", AggsType.sum, boolQueryBuilder, null, esIndexName);
            return TradeBizResult.ok(itemQuantity);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    @Override
    public TradeBizResult<Long> searchLastSaleDate(SkuSalesSearchRequest request) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.filter(new TermQueryBuilder("item_id", request.getItemId()));
        List<Long> skuIds = request.getSkuIds();
        if (CollectionUtils.isNotEmpty(skuIds)){
            boolQueryBuilder.filter(new TermsQueryBuilder("sku_id", skuIds));
        }
        if (null != request.getSellerId()) {
            boolQueryBuilder.filter(new TermQueryBuilder("seller_id", request.getSellerId()));
        }
        boolQueryBuilder.filter(new TermsQueryBuilder("order_status", request.getOrderStatus()));
        //统计的时间范围 目前以创建时间为查询条件
        if(Objects.nonNull(request.getBeginTime()) || Objects.nonNull(request.getEndTime())) {
            RangeQueryBuilder range = new RangeQueryBuilder("payment_date");
            if (Objects.nonNull(request.getBeginTime())) {
                range.gte(request.getBeginTime());
            }
            if (Objects.nonNull(request.getEndTime())) {
                range.lte(request.getEndTime());
            }
            boolQueryBuilder.filter(range);
        }
        try {
            double lastSaleDate = searchClient.aggs("payment_date", AggsType.max, boolQueryBuilder, null, esIndexName);
            return TradeBizResult.ok(((long) lastSaleDate));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private List<Map<String, Object>> searchByPrimaryIds(Set<Long> primaryIds){
        SimpleSearchRequest request = new SimpleSearchRequest();
        request.setAppName(appName);

        //request.addInFilter("primary_order_id" , primaryIds);
        request.addQueryParam("primary_order_id", StringUtils.join(primaryIds ,
            SimpleSearchRequest.OR_DELIMITER));

        request.addDecreaseSort("gmt_create");
        request.setPageSize(MAX_SEARCH_SIZE);
        DefaultSearchResult result = searchClient.search(request);
        if (!result.isSuccess()) {
            throw new GmallException(ErrorUtils.mapCode(
                    String.valueOf(result.getResultCode()),
                    I18NMessageUtils.getMessage("search.failed")+":" + result.getResultMsg()));  //# "搜索失败
        }
        List<Map<String, Object>> itemList = result.getSearchItems();
        return itemList;
    }

    private ListOrder queryOrders(Long sellerId,List<Long> primaryOrderIds){
        ListOrder listOrder = new ListOrder();
        if (CollectionUtils.isEmpty(primaryOrderIds)) {
            return listOrder;
        }
        OrderQueryWrapper query = new OrderQueryWrapper();
        if(sellerId != null && sellerId > 0){
            query.setSellerId(sellerId);
        }
        query.setPrimaryOrderIds(primaryOrderIds);
        List<TcOrderDO> list = tcOrderRepository.querySoldOrders(query);
        if(list == null || list.size() == 0){
            return listOrder;
        }

        Map<Long , MainOrder> mainMap = new LinkedHashMap<>();
        List<SubOrder> subOrders = new ArrayList<>();
        for(TcOrderDO order : list){
            Integer flag = order.getPrimaryOrderFlag();
            if(flag == 0){
                SubOrder subOrder = orderConverter.convertSubOrder(order);
                subOrders.add(subOrder);
            }else{
                MainOrder mainOrder = orderConverter.convertMainOrder(order);
                mainMap.put(mainOrder.getPrimaryOrderId() , mainOrder);
            }
        }
        for(SubOrder subOrder : subOrders){
            Long primaryOrderId = subOrder.getPrimaryOrderId();
            MainOrder mainOrder = mainMap.get(primaryOrderId);
            if(mainOrder == null){
                continue;
            }
            mainOrder.getSubOrders().add(subOrder);
        }
        List<MainOrder> mainList = Lists.newArrayList(mainMap.values());
        orderQueryAbility.fillStepOrders(mainList);
        listOrder.setOrderList(mainList);
        return listOrder;
    }
}
