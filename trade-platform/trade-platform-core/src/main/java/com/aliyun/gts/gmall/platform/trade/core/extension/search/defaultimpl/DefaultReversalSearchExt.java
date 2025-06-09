package com.aliyun.gts.gmall.platform.trade.core.extension.search.defaultimpl;

import com.aliyun.gts.gmall.center.misc.common.utils.JsonUtils;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonConstant;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.core.convertor.ReversalConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ReversalQueryService;
import com.aliyun.gts.gmall.platform.trade.core.extension.search.ReversalSearchExt;
import com.aliyun.gts.gmall.platform.trade.core.util.SearchUtils;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcReversalDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.*;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcReversalRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.CommUtils;
import com.aliyun.gts.gmall.platform.trade.domain.util.ErrorUtils;
import com.aliyun.gts.gmall.platform.trade.domain.util.SearchConverter;
import com.aliyun.gts.gmall.searcher.common.SearchClient;
import com.aliyun.gts.gmall.searcher.common.domain.request.Distinct;
import com.aliyun.gts.gmall.searcher.common.domain.request.SimpleSearchRequest;
import com.aliyun.gts.gmall.searcher.common.domain.result.DefaultSearchResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.*;
import org.elasticsearch.join.query.HasChildQueryBuilder;
import org.elasticsearch.join.query.HasParentQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DefaultReversalSearchExt implements ReversalSearchExt {


    private static final int MAX_PAGE_SIZE = 50;
    private static final int MAX_SUB_SIZE = DefaultOrderSearchExt.MAX_SEARCH_SIZE;
    private static final String FIELD_PRIMARY_ID = "primary_reversal_id";

    @Autowired
    private SearchClient searchClient;
    @Autowired
    private ReversalConverter reversalConverter;
    @Autowired
    private ReversalQueryService reversalQueryService;
    @Autowired
    private TcReversalRepository tcReversalRepository;

    @Value("${opensearch.app.tcReversal}")
    String appName;

    @Value("${elasticsearch.name.tcReversal:reversal}")
    private String esIndexName;

    // 开关: 是否优先走DB查询(支持的搜索条件下), false:全部走搜索
    @Value("${trade.reversal.search.fromDB:false}")
    private boolean searchFromDB;
    // 开关: 是否从DB补全搜索结果, false:从搜索补全
    @Value("${trade.reversal.search.fillByDB:true}")
    private boolean fillByDB;

    @Value("${search.type:opensearch}")
    private String searchType;

    @Override
    public ReversalSearchResult search(ReversalSearchQuery query) {
        //临时注释ReversalDbQuery.isDbSupport(query)  后续还是默认走ES
        if (searchFromDB) {
            ReversalDbQuery dbQuery = ReversalDbQuery.toDbQuery(query);
            return queryFromDB(dbQuery);
        } else {
            if(CommonConstant.OPENSEARCH.equals(searchType)) {
                return queryFromSearch(query);
            } else {
                return queryFromEsSearch(query);
            }
        }
    }

    protected ReversalSearchResult queryFromDB(ReversalDbQuery query) {
        Page<TcReversalDO> page = tcReversalRepository.queryPrimaryByCondition(query);
        List<Long> idList = page.getRecords()
                .stream().map(re -> re.getPrimaryReversalId())
                .collect(Collectors.toList());
        List<MainReversal> dataList = reversalQueryService.batchQueryReversal(idList, null);
        dataList = sort(dataList, idList);
        return new ReversalSearchResult(dataList, (int) page.getTotal());
    }

    protected ReversalSearchResult queryFromSearch(ReversalSearchQuery query) {
        SimpleSearchRequest request = buildSearchQuery(query);
        PrimaryIdResult primaryIdResult = queryDistinctPrimaryId(request);
        List<MainReversal> mainReversals;
        if (fillByDB) {
            mainReversals = reversalQueryService.batchQueryReversal(primaryIdResult.primaryReversalIds, null);
        } else {
            mainReversals = fillBySearch(primaryIdResult.primaryReversalIds);
        }
        mainReversals = sort(mainReversals, primaryIdResult.primaryReversalIds);
        return new ReversalSearchResult(mainReversals, primaryIdResult.getTotalCount());
    }

    protected ReversalSearchResult queryFromEsSearch(ReversalSearchQuery query) {
        log.info("ReversalSearchResult queryFromEsSearch query={}", JsonUtils.toJSONString(query));
        SearchRequest request = buildEsSearchQuery(query);
        log.info("ReversalSearchResult buildRequest={}", JsonUtils.toJSONString(request.source()));
        SearchResponse response = searchClient.search(request);

        List<Map<String, Object>> docs = Arrays.stream(response.getHits().getHits())
                .map(SearchHit::getSourceAsMap)
                .collect(Collectors.toList());

        List<Long> primaryReversalIds = docs.stream()
                .map(map-> SearchUtils.obj2Long(map.get("reversal_id")))
                .collect(Collectors.toList());

        PrimaryIdResult primaryIdResult = new PrimaryIdResult(primaryReversalIds, (int)response.getHits().getTotalHits().value);

        List<MainReversal> mainReversals;
        if(fillByDB) {
            mainReversals = reversalQueryService.batchQueryReversal(primaryIdResult.primaryReversalIds, null);
        } else {
            mainReversals =  toReversalList(docs);
        }
        mainReversals = sort(mainReversals, primaryIdResult.primaryReversalIds);
        return new ReversalSearchResult(mainReversals, primaryIdResult.getTotalCount());
    }

    protected SearchRequest buildEsSearchQuery(ReversalSearchQuery query) {
        checkReversalSearchQuery(query);

        SearchRequest searchRequest = new SearchRequest(esIndexName);
        SearchSourceBuilder ssBuilder = new SearchSourceBuilder();
        ssBuilder.size(query.getPageSize());
        ssBuilder.from((query.getPageNum()-1)*query.getPageSize());

        ssBuilder.sort("gmt_create", SortOrder.DESC);

        BoolQueryBuilder childBoolQueryBuilder = new BoolQueryBuilder();
        boolean hasChildQuery = false;
        if(StringUtils.isNotBlank(query.getItemTitle())) {
            childBoolQueryBuilder.must(new MatchQueryBuilder("item_title", query.getItemTitle()));
            hasChildQuery = true;
        }
        if(Objects.nonNull(query.getReversalType())) {
            childBoolQueryBuilder.filter(new TermQueryBuilder("reversal_type", query.getReversalType()));
            hasChildQuery = true;
        }


        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        if(hasChildQuery) {
            HasChildQueryBuilder hasChildQueryBuilder = new HasChildQueryBuilder("reversal_child", childBoolQueryBuilder, ScoreMode.None);
            boolQueryBuilder.filter(hasChildQueryBuilder);
        }
        boolQueryBuilder.filter(new TermQueryBuilder("relation_type", "reversal_primary"));

        //订单号模糊查询
        if (Objects.nonNull(query.getReversalId())) {
            boolQueryBuilder.filter(new MatchQueryBuilder("reversal_id", query.getReversalId()));
        }
//
//        if (Objects.nonNull(query.getReversalId())) {
//            boolQueryBuilder.filter(new MatchQueryBuilder("reversal_id", query.getReversalId()));
//        }

        //客户ID模糊查询  Customer No.
        if (Objects.nonNull(query.getCustId())) {
            boolQueryBuilder.filter(new MatchQueryBuilder("cust_id", query.getCustId()));
        }

        //查询customerLastName
        if (StringUtils.isNotBlank(query.getCustomerLastName())) {
            boolQueryBuilder.must(new WildcardQueryBuilder("customer_last_name", "*" + query.getCustomerLastName() + "*"));
        }

        //查询customerFirstName
        if (StringUtils.isNotBlank(query.getCustomerFirstName())) {
            boolQueryBuilder.must(new WildcardQueryBuilder("customer_first_name", "*" + query.getCustomerFirstName() + "*"));
        }

        //
        //商家BIN模糊查询
        if (StringUtils.isNotBlank(query.getSellerBin())) {
            boolQueryBuilder.must(new WildcardQueryBuilder("seller_bin", "*" + query.getSellerBin() + "*"));
        }

        //商家名字模糊查询
        if (StringUtils.isNotBlank(query.getSellerName())) {
            boolQueryBuilder.must(new WildcardQueryBuilder("seller_name", "*" + query.getSellerName() + "*"));
        }

        if(CollectionUtils.isNotEmpty(query.getFromOrderStatus())) {
            boolQueryBuilder.filter(new TermsQueryBuilder("from_order_status", query.getFromOrderStatus()));
        }
        if(StringUtils.isNotBlank(query.getReversalLogisticsNo())) {

            boolQueryBuilder.filter(new TermQueryBuilder("reversal_logistics_no", query.getReversalLogisticsNo()));
        }

        if(Objects.nonNull(query.getPrimaryReversalId())) {
            boolQueryBuilder.filter(new TermQueryBuilder("primary_reversal_id", query.getPrimaryReversalId()));
        }

        /**
         * TODO 时间字段
         * 售后订单创建时间区间
         */
        if(Objects.nonNull(query.getCreateTime())) {
            RangeQueryBuilder createTimeRange = new RangeQueryBuilder("gmt_create");
            if(Objects.nonNull(query.getCreateTime().getStartTime())) {
                createTimeRange.gte(query.getCreateTime().getStartTime().getTime());
            }
            if(Objects.nonNull(query.getCreateTime().getEndTime())) {
                createTimeRange.lte(query.getCreateTime().getEndTime().getTime());
            }
            boolQueryBuilder.filter(createTimeRange);
        }
        //下单时间
        if(Objects.nonNull(query.getOrderTime())) {
            RangeQueryBuilder orderTimeRange = new RangeQueryBuilder("order_time");
            if(Objects.nonNull(query.getOrderTime().getStartTime())) {
                orderTimeRange.gte(query.getOrderTime().getStartTime().getTime());
            }
            if(Objects.nonNull(query.getOrderTime().getEndTime())) {
                orderTimeRange.lte(query.getOrderTime().getEndTime().getTime());
            }
            boolQueryBuilder.filter(orderTimeRange);
        }
        //退单完成时间
        if(Objects.nonNull(query.getRefundCompletedTime())) {
            RangeQueryBuilder refundCompletedTimeRange = new RangeQueryBuilder("reversal_completed_time");
            if(Objects.nonNull(query.getRefundCompletedTime().getStartTime())) {
                refundCompletedTimeRange.gte(query.getRefundCompletedTime().getStartTime().getTime());
            }
            if(Objects.nonNull(query.getRefundCompletedTime().getEndTime())) {
                refundCompletedTimeRange.lte(query.getRefundCompletedTime().getEndTime().getTime());
            }
            boolQueryBuilder.filter(refundCompletedTimeRange);
        }

        //退回发货时间
        if(Objects.nonNull(query.getReversalSendTime())) {
            RangeQueryBuilder reversalSendRange = new RangeQueryBuilder("reversal_send_time");
            if(Objects.nonNull(query.getReversalSendTime().getStartTime())) {
                reversalSendRange.gte(query.getReversalSendTime().getStartTime().getTime());
            }
            if(Objects.nonNull(query.getReversalSendTime().getEndTime())) {
                reversalSendRange.lte(query.getReversalSendTime().getEndTime().getTime());
            }
            boolQueryBuilder.filter(reversalSendRange);
        }

        //退回确认收货时间
        if(Objects.nonNull(query.getReversalConfirmReceiveTime())) {
            RangeQueryBuilder reversalConfirmRange = new RangeQueryBuilder("reversal_confirm_receive_time");
            if(Objects.nonNull(query.getReversalConfirmReceiveTime().getStartTime())) {
                reversalConfirmRange.gte(query.getReversalConfirmReceiveTime().getStartTime().getTime());
            }
            if(Objects.nonNull(query.getReversalConfirmReceiveTime().getEndTime())) {
                reversalConfirmRange.lte(query.getReversalConfirmReceiveTime().getEndTime().getTime());
            }
            boolQueryBuilder.filter(reversalConfirmRange);
        }


        if(Objects.nonNull(query.getOrderSendTime())) {
            RangeQueryBuilder orderSendRange = new RangeQueryBuilder("order_send_time");
            if(Objects.nonNull(query.getOrderSendTime().getStartTime())) {
                orderSendRange.gte(query.getOrderSendTime().getStartTime().getTime());
            }
            if(Objects.nonNull(query.getOrderSendTime().getEndTime())) {
                orderSendRange.lte(query.getOrderSendTime().getEndTime().getTime());
            }
            boolQueryBuilder.filter(orderSendRange);
        }

        if(Objects.nonNull(query.getSellerId())) {
            boolQueryBuilder.filter(new TermQueryBuilder("seller_id", query.getSellerId()));
        }

        if(Objects.nonNull(query.getCustId())) {
            boolQueryBuilder.filter(new TermQueryBuilder("cust_id", query.getCustId()));
        }
        if(Objects.nonNull(query.getPrimaryOrderId())) {
            boolQueryBuilder.filter(new WildcardQueryBuilder("order_id", "*" + query.getPrimaryOrderId() + "*"));
        }
        if(Objects.nonNull(query.getReversalStatus())) {
            log.info("start put reversal_status={}",JsonUtils.toJSONString(query.getReversalStatus()));
            boolQueryBuilder.filter(new TermsQueryBuilder("reversal_status", query.getReversalStatus()));
        }

        if(Objects.nonNull(query.getOrderConfirmReceiveTime())) {
            RangeQueryBuilder confirmRange = new RangeQueryBuilder("order_confirm_receive_time");
            if(Objects.nonNull(query.getOrderConfirmReceiveTime().getStartTime())) {
                confirmRange.gte(query.getOrderConfirmReceiveTime().getStartTime().getTime());
            }
            if(Objects.nonNull(query.getOrderConfirmReceiveTime().getEndTime())) {
                confirmRange.lte(query.getOrderConfirmReceiveTime().getEndTime().getTime());
            }
            boolQueryBuilder.filter(confirmRange);
        }

        if (StringUtils.isNotBlank(query.getOrderLogisticsNo())) {
            boolQueryBuilder.filter(new TermsQueryBuilder("order_logistics_no", query.getOrderLogisticsNo()));
        }

        log.info("buildEsSearchQuery boolQueryBuilder={}",JsonUtils.toJSONString(boolQueryBuilder));
        ssBuilder.query(boolQueryBuilder);
        searchRequest.source(ssBuilder);
        return searchRequest;

    }


    protected SimpleSearchRequest buildSearchQuery(ReversalSearchQuery query) {
        checkReversalSearchQuery(query);

        SimpleSearchRequest req = new SimpleSearchRequest();
        req.setAppName(appName);
        req.setPageNo(query.getPageNum());
        req.setPageSize(query.getPageSize());
        req.addQueryParam("is_reversal_main", "0");

        List<String> rawBuilder = new ArrayList<>();

        if (query.getSellerId() != null) {
            req.addQueryParam("seller_id", String.valueOf(query.getSellerId()));
        }
        if (query.getCustId() != null) {
            req.addQueryParam("cust_id", String.valueOf(query.getCustId()));
        }
        //客户名字
        if (query.getCustomerFirstName() !=null ) {
            req.addQueryParam("first_name", query.getCustomerFirstName());
        }
        //客户姓氏
        if (query.getCustomerLastName() !=null ) {
            req.addQueryParam("last_name", query.getCustomerLastName());
        }
        if (StringUtils.isNotBlank(query.getItemTitle())) {
            req.addQueryParam("item_title", query.getItemTitle());
        }
        if (CollectionUtils.isNotEmpty(query.getReversalStatus())) {
            req.addQueryParam("reversal_status", StringUtils.join(query.getReversalStatus(), SimpleSearchRequest.OR_DELIMITER));
        }
        //售后单创建时间范围
        if (query.getCreateTime() != null) {
            buildDateRange(query.getCreateTime(), "gmt_create", rawBuilder);
        }
        if (query.getPrimaryOrderId() != null) {
            req.addQueryParam("primary_order_id", String.valueOf(query.getPrimaryOrderId()));
        }
        if (query.getPrimaryReversalId() != null) {
            req.addQueryParam(FIELD_PRIMARY_ID, String.valueOf(query.getPrimaryReversalId()));
        }
        if (query.getReversalType() != null) {
            req.addQueryParam("reversal_type", String.valueOf(query.getReversalType()));
        }
        if (CollectionUtils.isNotEmpty(query.getFromOrderStatus())) {
            req.addQueryParam("from_order_status", StringUtils.join(query.getFromOrderStatus(), SimpleSearchRequest.OR_DELIMITER));
        }
        if (query.getOrderSendTime() != null) {
            buildDateRange(query.getOrderSendTime(), "order_send_time", rawBuilder);
        }
        if (query.getOrderConfirmReceiveTime() != null) {
            buildDateRange(query.getOrderConfirmReceiveTime(), "order_confirm_receive_time", rawBuilder);
        }
        if (StringUtils.isNotBlank(query.getOrderLogisticsNo())) {
            req.addQueryParam("order_logistics_no", query.getOrderLogisticsNo());
        }

        if (StringUtils.isNotBlank(query.getReversalLogisticsNo())) {
            req.addQueryParam("reversal_logistics_no", query.getReversalLogisticsNo());
        }
        //退回发货时间
        if (query.getReversalSendTime() != null) {
            buildDateRange(query.getReversalSendTime(), "reversal_send_time", rawBuilder);
        }
        //退回确认收货时间
        if (query.getReversalConfirmReceiveTime() != null) {
            buildDateRange(query.getReversalConfirmReceiveTime(), "reversal_confirm_receive_time", rawBuilder);
        }

        if (!rawBuilder.isEmpty()) {
            req.setRawQueryStr(StringUtils.join(rawBuilder, " AND "));
        }
        return req;
    }

    protected void checkReversalSearchQuery(ReversalSearchQuery query) {
        if (query.getPageNum() == null || query.getPageNum().intValue() < 1
                || query.getPageSize() == null || query.getPageSize().intValue() > MAX_PAGE_SIZE) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG, I18NMessageUtils.getMessage("pagination.limit.exceed"));  //# "分页参数超出限制"
        }
    }

    private void buildDateRange(DateRangeParam range, String fieldName, List<String> rawBuilder) {
        if (range.getStartTime() == null && range.getEndTime() == null) {
            return;
        }
        StringBuilder s = new StringBuilder();
        if (range != null && range.getStartTime() != null) {
            s.append('[').append(range.getStartTime().getTime());
        } else {
            s.append('(');
        }
        s.append(',');
        if (range != null && range.getEndTime() != null) {
            s.append(range.getEndTime().getTime()).append(']');
        } else {
            s.append(')');
        }
        rawBuilder.add(fieldName + ":" + s);
    }

    protected PrimaryIdResult queryDistinctPrimaryId(SimpleSearchRequest req) {
        Distinct d = new Distinct(FIELD_PRIMARY_ID);
        d.setReserved(false);
        d.setUpdateTotalHit(true);

        req.setFetchFields(List.of(FIELD_PRIMARY_ID));
        req.setDistinctParams(List.of(d));
        req.addDecreaseSort("gmt_create");
        DefaultSearchResult result = searchClient.search(req);
        if (!result.isSuccess()) {
            throw new GmallException(ErrorUtils.mapCode(
                    String.valueOf(result.getResultCode()),
                    I18NMessageUtils.getMessage("search.failed")+":" + result.getResultMsg()));  //# "搜索失败
        }

        List<Long> ids = result.getSearchItems().stream()
                .map(map -> Long.parseLong(String.valueOf(map.get("primary_reversal_id"))))
                .collect(Collectors.toList());
        return new PrimaryIdResult(ids, result.getTotalCount());
    }

    protected List<MainReversal> sort(List<MainReversal> list, List<Long> orderBy) {
        Map<Long, MainReversal> map = CommUtils.toMap(list, MainReversal::getPrimaryReversalId);
        List<MainReversal> result = new ArrayList<>();
        for (Long id : orderBy) {
            MainReversal mr = map.get(id);
            if (id != null) {
                result.add(mr);
            }
        }
        return result;
    }

    protected List<MainReversal> fillBySearch(Collection<Long> primaryReversalIds) {
        SimpleSearchRequest req = new SimpleSearchRequest();
        req.setAppName(appName);
        req.setPageNo(0);
        req.setPageSize(MAX_SUB_SIZE);
        req.addQueryParam(FIELD_PRIMARY_ID, StringUtils.join(primaryReversalIds, SimpleSearchRequest.OR_DELIMITER));
        DefaultSearchResult result = searchClient.search(req);
        if (!result.isSuccess()) {
            throw new GmallException(ErrorUtils.mapCode(
                    String.valueOf(result.getResultCode()),
                    I18NMessageUtils.getMessage("search.failed")+":" + result.getResultMsg()));  //# "搜索失败
        }
        return toReversalList(result.getSearchItems());
    }

    private List<MainReversal> toReversalList(List<Map<String, Object>> sourceList) {
        Multimap<Long, TcReversalDO> pMap = HashMultimap.create();
        for (Map<String, Object> map : sourceList) {
            TcReversalDO r = new TcReversalDO();
            SearchConverter.convertDO(r, map);
            pMap.put(r.getPrimaryReversalId(), r);
        }
        List<MainReversal> targetList = new ArrayList<>();
        for (Long pid : pMap.keySet()) {
            MainReversal main = null;
            List<SubReversal> subs = new ArrayList<>();
            for (TcReversalDO r : pMap.get(pid)) {
                if (r.getIsReversalMain() != null && r.getIsReversalMain().booleanValue()) {
                    main = reversalConverter.toMainReversal(r);
                } else {
                    subs.add(reversalConverter.toSubReversal(r));
                }
            }
            if (main != null) {
                main.setSubReversals(subs);
                targetList.add(main);
            }
        }
        return targetList;
    }

    @Getter
    @AllArgsConstructor
    protected class PrimaryIdResult {
        private List<Long> primaryReversalIds;
        private int totalCount;
    }
}
