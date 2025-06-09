package com.aliyun.gts.gmall.platform.trade.core.util;

import com.aliyun.gts.gmall.platform.trade.api.dto.common.PageQuery;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.CustomerOrderQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.OrderSearchRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.OrderStatusInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.SellerOrderQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.util.SearchConverter;
import com.aliyun.gts.gmall.searcher.common.domain.request.Distinct;
import com.aliyun.gts.gmall.searcher.common.domain.request.SimpleSearchRequest;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.*;
import org.elasticsearch.join.query.HasChildQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.testng.collections.Lists;
import org.testng.collections.Sets;

import java.util.*;
import java.util.stream.Collectors;

public class SearchUtils {

    public static List<MainOrder> convertSearchToOrder(List<Map<String, Object>> itemList){
        if(itemList.size() > 0){
            Map<Long , MainOrder> mainOrderMap = new HashMap<>();
            List<SubOrder> subOrders = new ArrayList<>();
            for(Map<String,Object> itemMap : itemList){
                String primaryFlag = itemMap.get("primary_order_flag").toString();
                if("1".equals(primaryFlag)){
                    MainOrder mainOrder = new MainOrder();
                    SearchConverter.convert(mainOrder,itemMap);
                    mainOrderMap.put(mainOrder.getPrimaryOrderId() , mainOrder);
                }else{
                    SubOrder subOrder = new SubOrder();
                    SearchConverter.convert(subOrder , itemMap);
                    subOrders.add(subOrder);
                }
            }
            for(SubOrder subOrder : subOrders){
                Long primaryOrderId = subOrder.getPrimaryOrderId();
                MainOrder mainOrder = mainOrderMap.get(primaryOrderId);
                if(mainOrder == null){
                    continue;
                }
                mainOrder.getSubOrders().add(subOrder);
            }
            return Lists.newArrayList(mainOrderMap.values());
        }

        return Lists.newArrayList();
    }

    public static SimpleSearchRequest convertQueryToSearchRequest(CustomerOrderQueryRpcReq req){
        List<String> rawBuilder = new ArrayList<>();
        SimpleSearchRequest request = new SimpleSearchRequest();
        putQuery("customer_id" , req.getCustId() , request);
        putQuery("status" , req.getStatus(),request);
        if(req.getEvaluate() != null) {
            putQuery("evaluate", req.getEvaluate(), request);
        }
        putQuery("item_title" , req.getItemTitle() , request);
        putQuery("cust_deleted" , 0 , request);
        putStatusList(request, req.getStatusList(), rawBuilder);
        if(req.getPrimaryOrderIds() != null && req.getPrimaryOrderIds().size() > 0) {
            request.addQueryParam("primary_order_id", StringUtils.join(req.getPrimaryOrderIds() ,
                    SimpleSearchRequest.OR_DELIMITER));
        }

        putCommons(request, req, rawBuilder);
        return request;
    }

    private static SearchSourceBuilder buildSearchSourceBuilder(PageQuery req) {
        SearchSourceBuilder ssBuilder = new SearchSourceBuilder();
        ssBuilder.size(req.getPageSize());
        ssBuilder.from((req.getCurrentPage()-1)*req.getPageSize());
        ssBuilder.sort(new FieldSortBuilder("gmt_create").order(SortOrder.DESC));
        return ssBuilder;
    }

    public static SearchRequest convertQueryToEsSearchReq(CustomerOrderQueryRpcReq req) {
        SearchRequest request = new SearchRequest();
        SearchSourceBuilder ssBuilder = buildSearchSourceBuilder(req);

        BoolQueryBuilder childBoolQueryBuilder = new BoolQueryBuilder();
        boolean hasChildQuery = false;
        if(Objects.nonNull(req.getStatus())) {
            childBoolQueryBuilder.filter(new TermQueryBuilder("order_status", req.getStatus()));
            hasChildQuery = true;
        }
        if(Objects.nonNull(req.getEvaluate())) {
            childBoolQueryBuilder.filter(new TermQueryBuilder("evaluate", req.getEvaluate()));
            hasChildQuery = true;
        }
        if(StringUtils.isNotBlank(req.getItemTitle())) {
            childBoolQueryBuilder.must(new MatchQueryBuilder("item_title", req.getItemTitle()));
            hasChildQuery = true;
        }


        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        if(hasChildQuery) {
            HasChildQueryBuilder hasChildQueryBuilder = new HasChildQueryBuilder("child", childBoolQueryBuilder, ScoreMode.None);
            boolQueryBuilder.filter(hasChildQueryBuilder);
        }

        //查询主订单
        boolQueryBuilder.filter(new TermQueryBuilder("relation_type", "primary"));
        //查询未删除订单
        boolQueryBuilder.filter(new TermQueryBuilder("cust_deleted", "0"));
        //用户id
        boolQueryBuilder.filter(new TermQueryBuilder("cust_id", req.getCustId()));

        if(CollectionUtils.isNotEmpty(req.getPrimaryOrderIds())) {
            boolQueryBuilder.filter(new TermsQueryBuilder("order_id", req.getPrimaryOrderIds()));
        }

        QueryBuilder statusQueryBuilder = getStatusQueryBuilder(req.getStatusList());
        if(Objects.nonNull(statusQueryBuilder)) {
            boolQueryBuilder.filter(statusQueryBuilder);
        }

        ssBuilder.query(boolQueryBuilder);

        request.source(ssBuilder);
        return request;
    }
    public static SearchRequest convertQueryToEsSearchReq(SellerOrderQueryRpcReq req){
        SearchRequest searchRequest = new SearchRequest();

        //交易提供用户未评价订单查询接口 查询5个
        if (req.getSearchType() == SellerOrderQueryRpcReq.SEARCH_NEVER_VALUATION) {
            req.setPageSize(5);
        }

        //交易提供用户所有未评价订单查询接口 查询10个
        if (req.getSearchType() == SellerOrderQueryRpcReq.SEARCH_ALL_NEVER_VALUATION) {
            req.setPageSize(10);
        }

        SearchSourceBuilder ssBuilder =  buildSearchSourceBuilder(req);

        /*BoolQueryBuilder childBoolQueryBuilder = new BoolQueryBuilder();
        boolean hasChildQuery = false;*/

        /*if(StringUtils.isNotEmpty(req.getStatus())) {
            childBoolQueryBuilder.filter(new TermsQueryBuilder("order_status", req.getStatus().split(",")));
            hasChildQuery = true;
        }
        if(StringUtils.isNotEmpty(req.getDeliveryType())) {
            //childBoolQueryBuilder.filter(new TermsQueryBuilder("order_status", req.getStatusList().toString().split(",")));
            childBoolQueryBuilder.filter(new TermsQueryBuilder("logistics_type", req.getDeliveryType().split(",")));
            hasChildQuery = true;
        }
        // 处理订单标题
        if (StringUtils.isNotBlank(req.getItemTitle())) {
            childBoolQueryBuilder.filter(new MatchQueryBuilder("item_title", req.getItemTitle()));
            hasChildQuery = true;
        }*/

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        /*if(hasChildQuery) {
            HasChildQueryBuilder hasChildQueryBuilder = new HasChildQueryBuilder("child", childBoolQueryBuilder, ScoreMode.None);
            boolQueryBuilder.filter(hasChildQueryBuilder);
        }*/


        //查询主订单
        boolQueryBuilder.filter(new TermQueryBuilder("relation_type", "primary"));

        if(Objects.isNull(req.getSellerId())|| req.getSellerId()<0) {
            boolQueryBuilder.filter(new RangeQueryBuilder("gmt_create").gte(0));
        } else {
            boolQueryBuilder.filter(new TermQueryBuilder("seller_id", req.getSellerId()));
        }

        //查询orderNumber
        if(Objects.nonNull(req.getOrderId())) {
            boolQueryBuilder.filter(new WildcardQueryBuilder("order_id", "*" + req.getOrderId() + "*"));
        }

        //TODO 物流编号
        if (StringUtils.isNotEmpty(req.getTrackNumber())) {
            boolQueryBuilder.filter(new WildcardQueryBuilder("order_logistics_no", "*" + req.getTrackNumber() + "*"));
        }

        //TODO  Application number  Delivery pickup number 提货号码
        if (StringUtils.isNotEmpty(req.getApplicationNumber())) {
            boolQueryBuilder.filter(new WildcardQueryBuilder("application_number", "*" + req.getApplicationNumber() + "*"));
        }

        //顾客收货地址
        if (StringUtils.isNotEmpty(req.getCustomerAddress())) {
            boolQueryBuilder.filter(new WildcardQueryBuilder("deliveryAddr", "*" + req.getCustomerAddress() + "*"));
        }

        //商家发货地址
        if (StringUtils.isNotEmpty(req.getMerchantAddress())) {
            boolQueryBuilder.filter(new WildcardQueryBuilder("merchant_address", "*" + req.getMerchantAddress() + "*"));
        }

        //发货城市
        if (StringUtils.isNotEmpty(req.getDispatchCity())) {
            boolQueryBuilder.filter(new TermQueryBuilder("dispatch_city", req.getDispatchCity()));
        }

        if (StringUtils.isNotEmpty(req.getDispatchCityCode())) {
            boolQueryBuilder.filter(new TermQueryBuilder("dispatch_city_code", req.getDispatchCityCode()));
        }

        //收货城市
        if (StringUtils.isNotEmpty(req.getDestinationCity())) {
            boolQueryBuilder.filter(new TermQueryBuilder("destination_city", req.getDestinationCity()));
        }

        if (StringUtils.isNotEmpty(req.getDestinationCityCode())) {
            boolQueryBuilder.filter(new TermQueryBuilder("destination_city_code", req.getDestinationCityCode()));
        }

        //物理投递服务名
        if (StringUtils.isNotEmpty(req.getDeliveryServiceName())) {
            boolQueryBuilder.filter(
                    new WildcardQueryBuilder("delivery_service_name", "*" + req.getDeliveryServiceName() + "*"));
        }

        //查询customerLastName
        if (StringUtils.isNotBlank(req.getLastName())) {
            boolQueryBuilder.must(new WildcardQueryBuilder("customer_last_name", "*" + req.getLastName() + "*"));
        }

        //查询customerFirstName
        if (StringUtils.isNotBlank(req.getFirstName())) {
            boolQueryBuilder.must(new WildcardQueryBuilder("customer_first_name", "*" + req.getFirstName() + "*"));
        }

        //TODO CustomerNumber 查询customerNo
        if(Objects.nonNull(req.getCustId())) {
            boolQueryBuilder.filter(new WildcardQueryBuilder("cust_id", "*" + req.getCustId() + "*"));
        }

        //查询 Merchant BIN/IIN
        if(Objects.nonNull(req.getSellerBin())) {
            boolQueryBuilder.filter(new WildcardQueryBuilder("seller_bin", "*" + req.getSellerBin() + "*"));
        }

        //查询 Merchant name
        if(Objects.nonNull(req.getSellerName())) {
            boolQueryBuilder.filter(new WildcardQueryBuilder("seller_name", "*" + req.getSellerName() + "*"));
        }

        //evaluate 是否评价
        if (SellerOrderQueryRpcReq.SEARCH_NEVER_VALUATION == req.getSearchType()) {
            boolQueryBuilder.filter(new MatchQueryBuilder("evaluate", req.getEvaluate()));
        }

        //TODO Delivery commission 货到付款手续费
        if (null != req.getDeliveryCommissionMin() || null != req.getDeliveryCommissionMax()) {
            handleRange(boolQueryBuilder, "delivery_commission", req.getDeliveryCommissionMin(), req.getDeliveryCommissionMax());
        }

        //订单创建时间区间
        if (req.getStartTime() != null || req.getEndTime() != null) {
            handleRange(boolQueryBuilder, "gmt_create", req.getStartTime(), req.getEndTime());
        }

        //订单运费区间区间
        if (req.getDeliveryFeeMin() != null || req.getDeliveryFeeMax() != null) {
            handleRange(boolQueryBuilder, "logistics_fee", req.getDeliveryFeeMin(), req.getDeliveryFeeMax());
        }
        //订单折扣区间
        if (req.getDiscountAmountMin() != null || req.getDiscountAmountMax() != null) {
            handleRange(boolQueryBuilder, "discount_amount", req.getDiscountAmountMin(), req.getDiscountAmountMax());
        }
        //订单运费区间区间
        if (req.getOrderSubtotalMin() != null || req.getOrderSubtotalMax() != null) {
            handleRange(boolQueryBuilder, "order_subtotal", req.getOrderSubtotalMin(), req.getOrderSubtotalMax());
        }
        //订单运费区间区间
        if (req.getTotalPriceMin() != null || req.getTotalPriceMax() != null) {
            handleRange(boolQueryBuilder, "total_price", req.getTotalPriceMin(), req.getTotalPriceMax());
        }

        //订单发货时间区间
        if (req.getDepartureStart() != null || req.getDepartureEnd() != null) {
            handleRange(boolQueryBuilder, "order_send_time", req.getDepartureStart(), req.getDepartureEnd());
        }


        //订单收货时间区间
        if (req.getReceivingStart() != null || req.getReceivingEnd() != null) {
            handleRange(boolQueryBuilder, "order_confirm_receive_time", req.getReceivingStart(), req.getReceivingEnd());
        }

        //订单类型
        if(Objects.nonNull(req.getOrderType())) {
            boolQueryBuilder.filter(new TermQueryBuilder("order_type", req.getOrderType()));
        }

        //多选状态查询
        QueryBuilder statusQueryBuilder = getStatusQueryBuilder(req.getStatusList());
        if(Objects.nonNull(statusQueryBuilder)) {
            boolQueryBuilder.filter(statusQueryBuilder);
        }

        // 订单状态
        if (StringUtils.isNotEmpty(req.getStatus())) {
            String[] orderStatus = req.getStatus().split(",");
            addMultiSelectFilter(boolQueryBuilder, "order_status", orderStatus);
        }

        // 处理配送方式多选
        if (StringUtils.isNotEmpty(req.getDeliveryType())) {
            String[] deliveryTypes = req.getDeliveryType().split(",");
            addMultiSelectFilter(boolQueryBuilder, "logistics_type", deliveryTypes);
        }

        // 处理付款方式多选
        if (StringUtils.isNotEmpty(req.getPayChannel())) {
            String[] payChannels = req.getPayChannel().split(",");
            addMultiSelectFilter(boolQueryBuilder, "pay_channel", payChannels);
        }

        // 处理贷款周期多选
        if (StringUtils.isNotEmpty(req.getStagesNumber())) {
            String[] stagesNumberList = req.getStagesNumber().split(",");
            addMultiSelectFilter(boolQueryBuilder, "loan_cycle", stagesNumberList);
        }

        // 处理贷款状态多选
        if (StringUtils.isNotEmpty(req.getLoanStatus())) {
            String[] loanStatusList = req.getLoanStatus().split(",");
            addMultiSelectFilter(boolQueryBuilder, "loan_status", loanStatusList);
        }

        if (CollectionUtils.isNotEmpty(req.getCustIdList())) {
            boolQueryBuilder.filter(new TermsQueryBuilder("cust_id", req.getCustIdList()));
        }
        if (CollectionUtils.isNotEmpty(req.getPrimaryOrderIdList())) {
            boolQueryBuilder.filter(new TermsQueryBuilder("order_id", req.getPrimaryOrderIdList()));
        }
        ssBuilder.query(boolQueryBuilder);

        //按购买日期 排正序
        if (SellerOrderQueryRpcReq.SORT_CREATE_TIME_DESC == req.getSortType()) {
            ssBuilder.sort(new FieldSortBuilder("gmt_create").order(SortOrder.DESC));
        }

        searchRequest.source(ssBuilder);
        return searchRequest;
    }

    // 通用处理方法 多选查询
    private static void addMultiSelectFilter(BoolQueryBuilder boolQueryBuilder, String fieldName, String[] values) {
        boolQueryBuilder.filter(new TermsQueryBuilder(fieldName, values));
    }

    public static SearchRequest convertQueryToEsSearchReq(OrderSearchRpcReq req) {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder ssBuilder = buildSearchSourceBuilder(req);

        BoolQueryBuilder childBoolQueryBuilder = new BoolQueryBuilder();
        boolean hasChildQuery = false;
        if(StringUtils.isNotBlank(req.getItemTitle())) {
            childBoolQueryBuilder.must(new MatchQueryBuilder("item_title", req.getItemTitle()));
            hasChildQuery = true;
        }
        if(Objects.nonNull(req.getEvaluate())) {
            childBoolQueryBuilder.filter(new TermQueryBuilder("evaluate", req.getEvaluate()));
            hasChildQuery = true;
        }
        if(CollectionUtils.isNotEmpty(req.getSubTags())) {
            childBoolQueryBuilder.filter(new TermsQueryBuilder("tags", req.getSubTags()));
            hasChildQuery = true;
        }


        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        //查询主订单
        boolQueryBuilder.filter(new TermQueryBuilder("relation_type", "primary"));

        if(CollectionUtils.isNotEmpty(req.getSellerId())) {
            boolQueryBuilder.filter(new TermsQueryBuilder("seller_id", req.getSellerId()));
        }
        if(CollectionUtils.isNotEmpty(req.getCustId())) {
            boolQueryBuilder.filter(new TermsQueryBuilder("cust_id", req.getCustId()));
        }

        if (hasChildQuery) {
            HasChildQueryBuilder hasChildQueryBuilder = new HasChildQueryBuilder("child", childBoolQueryBuilder, ScoreMode.None);
            boolQueryBuilder.filter(hasChildQueryBuilder);
        }

        //订单创建时间区间
        handleRange(boolQueryBuilder, "gmt_create", req.getStartTime(), req.getEndTime());

        if(CollectionUtils.isNotEmpty(req.getPrimaryOrderId())) {
            boolQueryBuilder.filter(new TermsQueryBuilder("order_id", req.getPrimaryOrderId()));
        }

        if(CollectionUtils.isNotEmpty(req.getDeliveryType())) {
            boolQueryBuilder.filter(new TermQueryBuilder("logistics_type", req.getDeliveryType()));
        }

        if(CollectionUtils.isNotEmpty(req.getPayChannel())) {
            boolQueryBuilder.filter(new TermsQueryBuilder("pay_channel", req.getPayChannel()));
        }

        if(CollectionUtils.isNotEmpty(req.getMainTags())) {
            boolQueryBuilder.filter(new TermsQueryBuilder("tags", req.getMainTags()));
        }

        QueryBuilder statusQueryBuilder = getStatusQueryBuilder(req.getStatus());

        if(Objects.nonNull(statusQueryBuilder)) {
            boolQueryBuilder.filter(statusQueryBuilder);
        }

        ssBuilder.query(boolQueryBuilder);
        searchRequest.source(ssBuilder);
        return searchRequest;
    }

    public static void handleRange(BoolQueryBuilder queryBuilder, String fieldName, Object start, Object end) {
        if(Objects.nonNull(start) || Objects.nonNull(end)) {
            RangeQueryBuilder range = new RangeQueryBuilder(fieldName);
            if(Objects.nonNull(start)) {
                range.gte(start);
            }
            if(Objects.nonNull(end)) {
                range.lte(end);
            }
            queryBuilder.filter(range);
        }
    }


    public static SimpleSearchRequest convertQueryToSearchRequest(SellerOrderQueryRpcReq req){
        List<String> rawBuilder = new ArrayList<>();
        SimpleSearchRequest request = new SimpleSearchRequest();
        //小二运营后台传入负数卖家id表示查询所有卖家订单
        if(req.getSellerId() == null || req.getSellerId() < 0){
            rawBuilder.add("gmt_create:[0,)");
        }else {
            putQuery("seller_id", req.getSellerId(), request);
        }
        putQuery("status" , req.getStatus(),request);
        putQuery("primary_order_id" , req.getPrimaryOrderId(), request);
        putQuery("item_title" , req.getItemTitle() , request);
        putQuery("logistics_type",req.getDeliveryType(),request);
        putQuery("order_type",req.getOrderType() , request);
        putQuery("pay_channel" , req.getPayChannel() , request);
        putStatusList(request, req.getStatusList(), rawBuilder);
        if(req.getStartTime() != null){
            request.addGreaterAndEqualFilter("gmt_create", req.getStartTime().getTime());
        }
        if(req.getEndTime() != null){
            request.addLessFilter("gmt_create", req.getEndTime().getTime());
        }

        putCommons(request, req, rawBuilder);
        return request;
    }

    public static SimpleSearchRequest convertQueryToSearchRequest(OrderSearchRpcReq req){
        List<String> rawBuilder = new ArrayList<>();
        SimpleSearchRequest request = new SimpleSearchRequest();
        StringBuilder rawQuery = new StringBuilder();

        putMatchAny("seller_id", req.getSellerId(), request);
        putMatchAny("customer_id", req.getCustId(), request);
        putQuery("item_title" , req.getItemTitle() , request);
        putStatusList(request, req.getStatus(), rawBuilder);
        putRange("gmt_create", req.getStartTime(), req.getEndTime(), rawBuilder);
        putMatchAny("primary_order_id", req.getPrimaryOrderId(), request);
        putMatchAny("order_type", req.getOrderType(), request);
        putMatchAny("logistics_type", req.getDeliveryType(), request);
        putMatchAny("pay_channel" , req.getPayChannel() , request);
        putQuery("evaluate", req.getEvaluate(), request);
        putMatchAll("main_tags", req.getMainTags(), request);
        putMatchAll("tags", req.getSubTags(), request);

        putCommons(request, req, rawBuilder);
        return request;
    }



    public static void putQuery(String pName , Object pValue , SimpleSearchRequest request){
        if(pValue != null){
            String sValue;
            if (pValue instanceof Boolean) {
                sValue = ((Boolean) pValue).booleanValue() ? "1" : "0";
            } else {
                sValue = String.valueOf(pValue);
            }
            if(StringUtils.isNotBlank(sValue)){
                request.addQueryParam(pName, sValue);
            }
        }
    }

    public static void putMatchAny(String pName, List pValues, SimpleSearchRequest request) {
        if (CollectionUtils.isNotEmpty(pValues)) {
            request.addQueryParam(pName, StringUtils.join(pValues, SimpleSearchRequest.OR_DELIMITER));
        }
    }

    public static void putMatchAll(String pName, List pValues, SimpleSearchRequest request) {
        if (CollectionUtils.isNotEmpty(pValues)) {
            for (Object pValue : pValues) {
                request.addQueryParam(pName, String.valueOf(pValue));
            }
        }
    }

    public static void putRange(String pName, Date startValue, Date endValue, List<String> rawBuilder) {
        putRange(pName,
                startValue == null ? null : startValue.getTime(),
                endValue == null ? null : endValue.getTime(),
                rawBuilder);
    }

    public static void putRange(String pName, Long startValue, Long endValue, List<String> rawBuilder) {
        if (startValue == null && endValue == null) {
            return;
        }
        StringBuilder s = new StringBuilder();
        s.append(pName).append(':');
        if (startValue == null) {
            s.append("(,");
        } else {
            s.append('[').append(startValue).append(',');
        }
        if (endValue == null) {
            s.append(')');
        } else {
            s.append(endValue).append(']');
        }
        rawBuilder.add(s.toString());
    }

    public static QueryBuilder getStatusQueryBuilder(List<OrderStatusInfo> statusInfos) {
        if (CollectionUtils.isEmpty(statusInfos)) {
            return null;
        }
        Set<Integer> simpleStatus = Sets.newHashSet();
        List<OrderStatusInfo> mixStatus = new ArrayList<>();

        handleStatusInfo(simpleStatus, mixStatus, statusInfos);

        if (simpleStatus.isEmpty() && mixStatus.isEmpty()) {
            return null;
        }
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.minimumShouldMatch(1);

        if(CollectionUtils.isNotEmpty(simpleStatus)) {
            boolQueryBuilder.should(new TermsQueryBuilder("order_status", simpleStatus));
        }

        if(CollectionUtils.isNotEmpty(mixStatus)) {
            for(OrderStatusInfo orderStatusInfo: mixStatus) {

                HasChildQueryBuilder hasChildQueryBuilder = new HasChildQueryBuilder("child",
                        new TermQueryBuilder("order_status", orderStatusInfo.getOrderStatus()), ScoreMode.None);

                BoolQueryBuilder mixBoolQB = new BoolQueryBuilder();
                mixBoolQB.filter(hasChildQueryBuilder);
                mixBoolQB.filter(new TermQueryBuilder("step_no", orderStatusInfo.getStepNo()));
                mixBoolQB.filter(new TermQueryBuilder("step_status", orderStatusInfo.getStepStatus()));

                boolQueryBuilder.should(mixBoolQB);
            }
        }
        return boolQueryBuilder;
    }

    public static void putStatusList(SimpleSearchRequest request, List<OrderStatusInfo> statusList, List<String> rawBuilder) {
        if (statusList == null || statusList.isEmpty()) {
            return;
        }
        Set<Integer> simpleStatus = Sets.newHashSet();
        List<OrderStatusInfo> mixStatus = new ArrayList<>();

        handleStatusInfo(simpleStatus, mixStatus, statusList);

        if (simpleStatus.isEmpty() && mixStatus.isEmpty()) {
            return;
        }
        if (mixStatus.isEmpty()) {
            request.addQueryParam("status", StringUtils.join(simpleStatus , SimpleSearchRequest.OR_DELIMITER));
            return;
        }

        // 组装如下条件
        // (status: '1'|'2' OR ( status:'3' AND main_step_no:'1' AND main_step_status:'98' ) OR (status:'3' AND main_step_no:'1' AND main_step_status:'99' ))

        StringBuilder sb = new StringBuilder();
        sb.append('(');
        if (!simpleStatus.isEmpty()) {
            sb.append("status:");
            boolean head = true;
            for (Integer status : simpleStatus) {
                if (head) {
                    head = false;
                } else {
                    sb.append('|');
                }
                sb.append('\'').append(status).append('\'');
            }
            sb.append(" OR ");
        }
        boolean head = true;
        for (OrderStatusInfo status : mixStatus) {
            if (head) {
                head = false;
            } else {
                sb.append(" OR ");
            }
            sb.append("(status:'").append(status.getOrderStatus())
                    .append("' AND main_step_no:'").append(status.getStepNo())
                    .append("' AND main_step_status:'").append(status.getStepStatus())
                    .append("')");
        }
        sb.append(')');
        rawBuilder.add(sb.toString());
    }

    private static void handleStatusInfo(Set<Integer> simpleStatus , List<OrderStatusInfo> mixStatus,
                                         List<OrderStatusInfo> statusList) {
        for (OrderStatusInfo status : statusList) {
            if (status.getOrderStatus() != null) {
                if (status.getStepStatus() != null) {
                    mixStatus.add(status);
                } else {
                    simpleStatus.add(status.getOrderStatus());
                }
            }
        }
    }

    public static void putCommons(SimpleSearchRequest request, PageQuery page, List<String> rawBuilder) {
        putCommons(request, page , rawBuilder , "primary_order_id");
    }

    public static void putCommons(SimpleSearchRequest request, PageQuery page,
        List<String> rawBuilder , String distinctStr) {
        if(StringUtils.isNotEmpty(distinctStr)) {
            Distinct distinct = new Distinct(distinctStr);
            request.setDistinctParams(Lists.newArrayList(distinct));
            distinct.setReserved(false);
            distinct.setUpdateTotalHit(true);
        }
        request.setPageNo(page.getCurrentPage());
        request.setPageSize(page.getPageSize());
        request.addDecreaseSort("gmt_create");

        if (!rawBuilder.isEmpty()) {
            request.setRawQueryStr(StringUtils.join(rawBuilder, " AND "));
        }
    }

    public static long obj2Long(Object obj) {
        if(obj instanceof Number) {
            return (long) obj;
        }
        return Long.parseLong(obj.toString());
    }
}
