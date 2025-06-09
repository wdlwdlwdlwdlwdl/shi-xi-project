package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.convert.MultiLangConverter;
import com.aliyun.gts.gmall.framework.i18n.MultiLangText;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.promotion.common.util.JsonUtils;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonConstant;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.TradeExtendKeyConstants;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.*;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.*;
import com.aliyun.gts.gmall.platform.trade.common.constants.EvaluationApproveStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.convertor.EvaluationConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.EvaluationQueryService;
import com.aliyun.gts.gmall.platform.trade.core.util.SearchUtils;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcEvaluationDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.extra.ItemEvaluationExtra;
import com.aliyun.gts.gmall.platform.trade.domain.entity.evaluation.Evaluation;
import com.aliyun.gts.gmall.platform.trade.domain.entity.evaluation.EvaluationRate;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Customer;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Seller;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcEvaluationRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.UserRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.ErrorUtils;
import com.aliyun.gts.gmall.platform.trade.domain.util.SearchConverter;
import com.aliyun.gts.gmall.searcher.common.SearchClient;
import com.aliyun.gts.gmall.searcher.common.domain.request.SimpleSearchRequest;
import com.aliyun.gts.gmall.searcher.common.domain.result.DefaultSearchResult;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Cardinality;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.collapse.CollapseBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Service
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "trade", name = "evaluationQueryService", havingValue = "default", matchIfMissing = true)
public class EvaluationQueryServiceImpl implements EvaluationQueryService {

    @Autowired
    private SearchClient searchClient;
    @Value("${opensearch.app.tcEvaluation}")
    private String appName;
    @Value("${elasticsearch.name.tcEvaluation}")
    private String esIndexName;
    @Value("${search.type:opensearch}")
    private String searchType;
    @Autowired
    private TcEvaluationRepository tcEvaluationRepository;
    @Autowired
    private EvaluationConverter evaluationConverter;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TcOrderRepository tcOrderRepository;

    private final static String DISTINCT_TOTAL_COUNT = "dtc";

    private Map<Long, Object> custMap = new HashMap<Long, Object>();

    private final static String APPROVE_STATUS = "evaluationApproveStatus";

    private final static String APPROVE_CONTENT= "evaluationApproveContent";

    private final static String EV_TYPE = "evType";
    private final static String DELIVERY_RATE = "logisticsRate";
    private final static String DELIVERY_RATE_DESC = "logisticsDesc";


    @Override
    public PageInfo<ItemEvaluationDTO> queryEvaluation(EvaluationQueryRpcReq req) {
        // 从es获取数据
        ItemEvaluationExtra itemEvaluationExtra = getItemListFromEs(req);
        List<TcEvaluationDO> itemList = itemEvaluationExtra.getItemList();
        if (CollectionUtils.isEmpty(itemList)) {
            return PageInfo.empty();
        }
        // db补全
        Set<Long> primaryOrderIds = new HashSet<>();
        for (TcEvaluationDO item : itemList) {
            primaryOrderIds.add(item.getPrimaryOrderId());
        }
        List<TcEvaluationDO> fillList = tcEvaluationRepository.batchQueryByPrimaryOrderId(primaryOrderIds);

        List<ItemEvaluationDTO> result = converterResult(itemList, fillList);
        PageInfo<ItemEvaluationDTO> page = new PageInfo();
        page.setList(result);
        page.setTotal(itemEvaluationExtra.getTotal());
        return page;
    }

    @Override
    public PageInfo<ItemEvaluationV2DTO> queryEvaluationV2(EvaluationQueryRpcReq req) {
        // 根据产品名称、产品score获取orderList
        setPrimaryOrderListFromEs(req);
        // 从es获取数据
        ItemEvaluationExtra itemEvaluationExtra = getItemListFromEs(req);
        List<TcEvaluationDO> itemList = itemEvaluationExtra.getItemList();
        if (CollectionUtils.isEmpty(itemList)) {
            return PageInfo.empty();
        }
        List<Long> orderNumber = itemList.stream().map(TcEvaluationDO::getPrimaryOrderId).toList();
        List<TcEvaluationDO> fillList = tcEvaluationRepository.batchQueryByPrimaryOrderId(orderNumber);
        List<ItemEvaluationV2DTO> result = formattedItemDataAndDbData(itemList, fillList);
        PageInfo<ItemEvaluationV2DTO> page = new PageInfo();
        page.setList(result);
        page.setTotal(itemEvaluationExtra.getTotal());
        return page;
    }


    /**
     * 从es根据item信息获取primaryOrderId
     *
     * @param req
     * @return
     */
    private void setPrimaryOrderListFromEs(EvaluationQueryRpcReq req) {
        boolean orderSearch = false;
        String termGroup = "group_by_order_id";
        SearchRequest searchRequest = new SearchRequest(esIndexName);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        if (StringUtils.isNotBlank(req.getItemTitle())) {
            orderSearch = true;
            queryBuilder.must(QueryBuilders.matchQuery("item_title", req.getItemTitle()));
        }

        if (req.getProductRatingArrays()!= null && req.getProductRatingArrays().length > 0) {
            orderSearch = true;
            queryBuilder.must(QueryBuilders.termsQuery("rate_score", req.getProductRatingArrays()))
                    .must(QueryBuilders.termsQuery("has_item_id", true));
        }

        req.setOrderSearch(orderSearch);
        // 如果没有涉及到搜索 则直接二跳出
        if (!orderSearch) {
            req.setPrimaryOrderList(new ArrayList<>());
        }
        // 1. 构建 terms 聚合
        sourceBuilder.aggregation(
                AggregationBuilders.terms(termGroup)
                        .field("primary_order_id")  // 聚合字段
                        .size(100)                 // 获取前100个分组
        );
        sourceBuilder.query(queryBuilder).size(0);
        // 2. 执行查询
        searchRequest.source(sourceBuilder);
        SearchResponse response = searchClient.search(searchRequest);
        // 3. 提取聚合结果中的ID列表
        Terms terms = response.getAggregations().get(termGroup);
        req.setPrimaryOrderList(terms.getBuckets().stream().map(a -> Long.valueOf(a.getKeyAsString())).collect(Collectors.toList()));
    }

    /**
     * 从es获取itemList数据
     *
     * @param req
     * @return
     */
    private ItemEvaluationExtra getItemListFromEs(EvaluationQueryRpcReq req) {
        List<TcEvaluationDO> itemList;
        long total;
        if (CommonConstant.OPENSEARCH.equals(searchType)) {
            SimpleSearchRequest searchReq = buildRequest(req);
            DefaultSearchResult searchResult = searchClient.search(searchReq);
            total = searchResult.getTotalCount();
            itemList = convertSearchResult(searchResult);
        } else {
            SearchRequest searchRequest = buildEsRequest(req);
            log.info("Evaluation SearchRequest={}", JsonUtils.toJSONString(searchRequest));
            SearchResponse response = searchClient.search(searchRequest);
            Cardinality cardinality = response.getAggregations().get(DISTINCT_TOTAL_COUNT);
            total = cardinality.getValue();
            List<Map<String, Object>> items = Arrays.stream(response.getHits().getHits())
                    .map(SearchHit::getSourceAsMap)
                    .collect(Collectors.toList());
            itemList = helpConvert(items);
        }
        return new ItemEvaluationExtra(itemList, total);
    }

    @Override
    public Integer queryEvaluationCount(EvaluationQueryRpcReq req) {
        SimpleSearchRequest searchReq = buildRequest(req);
        searchReq.setPageNo(1);
        searchReq.setPageSize(1);
        DefaultSearchResult searchResult = searchClient.search(searchReq);
        if (!searchResult.isSuccess()) {
            throw new GmallException(ErrorUtils.mapCode(String.valueOf(searchResult.getResultCode()),
                    I18NMessageUtils.getMessage("search.failed") + ":" + searchResult.getResultMsg()));  //# "搜索失败
        }
        return searchResult.getTotalCount();
    }

    @Override
    public List<Integer> batchQueryEvaluationCount(EvaluationBatchQueryRpcReq req) {
        List<Integer> result = new ArrayList<>();
        for (EvaluationQueryRpcReq condition : req.getBatchConditions()) {
            result.add(queryEvaluationCount(condition));
        }
        return result;
    }

    @Override
    public EvaluationDTO querySingleById(EvaluationIdReq req) {
        TcEvaluationDO exist = tcEvaluationRepository.queryById(req.getEvaluationId(), req.getPrimaryOrderId());
        if (exist == null) {
            return null;
        }
        if (req.getCustId() != null && !req.getCustId().equals(exist.getCustId())) {
            throw new GmallException(CommonErrorCode.NOT_DATA_OWNER);
        }
        if (req.getSellerId() != null && !req.getSellerId().equals(exist.getSellerId())) {
            throw new GmallException(CommonErrorCode.NOT_DATA_OWNER);
        }
        if (null != exist.getExtend()) {
            Integer evaluationApproveStatus = (Integer) exist.getExtend().get("evaluationApproveStatus");
            if (null != evaluationApproveStatus) {
                EvaluationApproveStatusEnum evaluationApproveStatusEnum = EvaluationApproveStatusEnum.codeOf(evaluationApproveStatus);
                if (null != evaluationApproveStatusEnum) {
                    exist.getExtend().put("evaluationApproveStatusName", evaluationApproveStatusEnum.getMessage());
                }
            }
        }
        return evaluationConverter.toEvaluationDTO(exist);
    }

    @Override
    public List<EvaluationDTO> getEvaluationWithReplies(EvaluationIdReq req) {
        return tcEvaluationRepository.getEvaluationWithReplies(req.getPrimaryOrderId()).stream()
                .map(evaluationConverter::toEvaluationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EvaluationRateDTO rateStatistics(EvaluationRateRpcReq req) {
        Evaluation evaluation = new Evaluation();
        evaluation.setItemId(req.getItemId());
        evaluation.setSkuId(req.getSkuId());
        evaluation.setSellerId(req.getSellerId());
        EvaluationRate rate = tcEvaluationRepository.statisticsRateBySeller(evaluation);
        return evaluationConverter.toEvaluationDTO(rate);
    }

    @Override
    public List<EvaluationRatePicDTO> ratePicList(EvaluationRateRpcReq req) {
        Evaluation evaluation = new Evaluation();
        evaluation.setItemId(req.getItemId());
        evaluation.setSkuId(req.getSkuId());
        evaluation.setSellerId(req.getSellerId());
        List<TcEvaluationDO> list = tcEvaluationRepository.queryRatePicList(evaluation);
        List<EvaluationRatePicDTO> dtoList = new ArrayList<>();

        for (TcEvaluationDO tcEvaluationDO : list) {
            EvaluationRatePicDTO dto = new EvaluationRatePicDTO();
            BeanUtils.copyProperties(tcEvaluationDO, dto);
            Customer customer = (Customer) custMap.get(tcEvaluationDO.getCustId());
            if (customer == null) {
                Customer c = userRepository.getCustomerRequired(tcEvaluationDO.getCustId());
                custMap.put(tcEvaluationDO.getCustId(), c);
                dto.setFirstName(c.getFirstName());
                dto.setLastName(c.getLastName());
            } else {
                dto.setFirstName(customer.getFirstName());
                dto.setLastName(customer.getLastName());
            }
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public List<EvaluationDTO> getEvaluationList(EvaluationIdReq req) {
        Evaluation evaluation = new Evaluation();
        evaluation.setPrimaryOrderId(req.getPrimaryOrderId());
        evaluation.setOrderId(req.getOrderId());
        List<TcEvaluationDO> list = tcEvaluationRepository.getEvaluationList(evaluation);
        return evaluationConverter.evaluationDTOList(list);
    }

    protected List<ItemEvaluationDTO> converterResult(List<TcEvaluationDO> itemList, List<TcEvaluationDO> fillList) {
        Map<Long, TcEvaluationDO> mainMap = new HashMap<>();               // 主评价
        Multimap<Long, TcEvaluationDO> otherMap = HashMultimap.create();    // 其他
        Map<Long, TcEvaluationDO> idMap = new HashMap<>();                // all

        for (TcEvaluationDO fill : fillList) {
            idMap.put(fill.getId(), fill);
            if (fill.getExtend() == null) {
                continue;
            }
            Integer type = (Integer) fill.getExtend().get(TradeExtendKeyConstants.EVALUATION_TYPE);
            if (type == null) {
                continue;
            }
            if (TradeExtendKeyConstants.EVALUATION_TYPE_MAIN == type.intValue()) {
                mainMap.put(fill.getOrderId(), fill);
            } else {
                otherMap.put(fill.getOrderId(), fill);
            }
        }

        List<ItemEvaluationDTO> result = new ArrayList<>();
        for (TcEvaluationDO item : itemList) {
            TcEvaluationDO mainItem = idMap.get(item.getId());
            if (mainItem == null) {
                continue;
            }
            TcEvaluationDO mainSeller = mainMap.get(mainItem.getPrimaryOrderId());
            Collection<TcEvaluationDO> otherList = otherMap.get(mainItem.getOrderId());

            ItemEvaluationDTO itemDTO = new ItemEvaluationDTO();
            itemDTO.setItemEvaluation(evaluationConverter.toEvaluationDTO(mainItem));
            itemDTO.setSellerEvaluation(evaluationConverter.toEvaluationDTO(mainSeller));
            itemDTO.setExtraEvaluation(evaluationConverter.toEvaluationDTOList(otherList));
            itemDTO.getExtraEvaluation().sort(Comparator.comparing(EvaluationDTO::getGmtCreate));
            //买家名称
            Customer customer = getCustomerByCusId(mainItem.getCustId());
            itemDTO.getItemEvaluation().setCustName(customer.getCustName());
            itemDTO.getItemEvaluation().setFirstName(customer.getFirstName());
            itemDTO.getItemEvaluation().setLastName(customer.getLastName());

            Seller seller = userRepository.getSeller(mainItem.getSellerId());
            //卖家名称
            itemDTO.getItemEvaluation().setSellerName(seller.getSellerName());
            //卖家bin/iin
            itemDTO.getItemEvaluation().setBinOrIin(seller.getBinOrIin());

//            //评价状态-extraEvaluation
//            for(EvaluationDTO extraEvalution:itemDTO.getExtraEvaluation()){
//                if(extraEvalution.getExtend().get("evaluationApproveStatus")!=null){
//                    Integer evaluationApproveStatus= (Integer) extraEvalution.getExtend().get("evaluationApproveStatus");
//                    if(evaluationApproveStatus==0){
//                        extraEvalution.getExtend().put("evaluationApproveStatusName",EvaluationApproveStatusEnum.NEED_APPROVE.getScript());
//                    }
//                    if(evaluationApproveStatus==1){
//                        extraEvalution.getExtend().put("evaluationApproveStatusName",EvaluationApproveStatusEnum.APPROVING.getScript());
//                    }
//                    if(evaluationApproveStatus==2){
//                        extraEvalution.getExtend().put("evaluationApproveStatusName",EvaluationApproveStatusEnum.PASSED.getScript());
//                    }
//                    if(evaluationApproveStatus==3){
//                        extraEvalution.getExtend().put("evaluationApproveStatusName",EvaluationApproveStatusEnum.REFUSED.getScript());
//                    }
//                }
//            }
//
//            //评价状态-itemEvaluation
//            if(itemDTO.getItemEvaluation().getExtend().get("evaluationApproveStatus")!=null){
//                Integer evaluationApproveStatus= (Integer) itemDTO.getItemEvaluation().getExtend().get("evaluationApproveStatus");
//                if(evaluationApproveStatus==0){
//                    itemDTO.getItemEvaluation().getExtend().put("evaluationApproveStatusName",EvaluationApproveStatusEnum.NEED_APPROVE.getScript());
//                }
//                if(evaluationApproveStatus==1){
//                    itemDTO.getItemEvaluation().getExtend().put("evaluationApproveStatusName",EvaluationApproveStatusEnum.APPROVING.getScript());
//                }
//                if(evaluationApproveStatus==2){
//                    itemDTO.getItemEvaluation().getExtend().put("evaluationApproveStatusName",EvaluationApproveStatusEnum.PASSED.getScript());
//                }
//                if(evaluationApproveStatus==3){
//                    itemDTO.getItemEvaluation().getExtend().put("evaluationApproveStatusName",EvaluationApproveStatusEnum.REFUSED.getScript());
//                }
//            }
//
//            //评价状态-sellerEvaluation
//            if(itemDTO.getSellerEvaluation().getExtend().get("evaluationApproveStatus")!=null){
//                Integer evaluationApproveStatus= (Integer) itemDTO.getSellerEvaluation().getExtend().get("evaluationApproveStatus");
//                if(evaluationApproveStatus==0){
//                    itemDTO.getSellerEvaluation().getExtend().put("evaluationApproveStatusName",EvaluationApproveStatusEnum.NEED_APPROVE.getScript());
//                }
//                if(evaluationApproveStatus==1){
//                    itemDTO.getSellerEvaluation().getExtend().put("evaluationApproveStatusName",EvaluationApproveStatusEnum.APPROVING.getScript());
//                }
//                if(evaluationApproveStatus==2){
//                    itemDTO.getSellerEvaluation().getExtend().put("evaluationApproveStatusName",EvaluationApproveStatusEnum.PASSED.getScript());
//                }
//                if(evaluationApproveStatus==3){
//                    itemDTO.getSellerEvaluation().getExtend().put("evaluationApproveStatusName",EvaluationApproveStatusEnum.REFUSED.getScript());
//                }
//            }


            //评价状态-extraEvaluation
            for (EvaluationDTO extraEvalution : itemDTO.getExtraEvaluation()) {
                getEvaluation(extraEvalution);
            }

            //评价状态-itemEvaluation
            getEvaluation(itemDTO.getItemEvaluation());

            //评价状态-sellerEvaluation
            getEvaluation(itemDTO.getSellerEvaluation());

            result.add(itemDTO);
        }
        return result;
    }

    /**
     * 数据格式整理
     *
     * @param itemList
     * @param fillList
     * @return
     */
    protected List<ItemEvaluationV2DTO> formattedItemDataAndDbData(List<TcEvaluationDO> itemList, List<TcEvaluationDO> fillList) {
        List<Long> orderNumberList = itemList.stream().map(TcEvaluationDO::getPrimaryOrderId).toList();
        // 商品评价订单
        Map<Long, List<TcEvaluationDO>> orderMapList = fillList.stream()
                .filter(a -> a.getItemId() > 0)
                .sorted(Comparator.comparing(TcEvaluationDO::getGmtCreate))
                .collect(Collectors.groupingBy(TcEvaluationDO::getPrimaryOrderId));
        // 物流评价订单
        Map<Long, TcEvaluationDO> deliverObj = fillList.stream()
                .filter(a -> a.getItemId() == 0)
                .collect(Collectors.toMap(TcEvaluationDO::getPrimaryOrderId, a -> a));
        List<ItemEvaluationV2DTO> retList = new ArrayList<>();
        // 商品名称
        Map<Long, TcOrderDO> orderDOMap = getTcOrderDOMapByOrderNumber(orderNumberList);
        itemList.forEach(a -> {
            ItemEvaluationV2DTO itemEvaluationV2DTO = new ItemEvaluationV2DTO();
            itemEvaluationV2DTO.setId(a.getId());
            itemEvaluationV2DTO.setOrderNumber(a.getPrimaryOrderId());
            itemEvaluationV2DTO.setMerchantRating(a.getRateScore());
            itemEvaluationV2DTO.setMerchantRateDesc(deliverObj.get(a.getPrimaryOrderId()).getRateDesc());
            // 获取用户信息
            Customer customer = getCustomerByCusId(a.getCustId());
            itemEvaluationV2DTO.setCustomer(customer.getCustName());
            // 获取卖家信息
            Seller seller = userRepository.getSeller(a.getSellerId());
            //卖家名称
            itemEvaluationV2DTO.setMerchantName(seller.getSellerName());
            // 物流extend
            Map deliverMap = deliverObj.get(a.getPrimaryOrderId()).getExtend();
            itemEvaluationV2DTO.setDeliveryRating(getIntValueFromMap(deliverMap, DELIVERY_RATE));
            itemEvaluationV2DTO.setDeliveryRateDesc(getStrValueFromMap(deliverMap, DELIVERY_RATE_DESC));
            itemEvaluationV2DTO.setApproveStatus(getIntValueFromMap(deliverMap, APPROVE_STATUS));
            itemEvaluationV2DTO.setApproveStatusDisplay(EvaluationApproveStatusEnum.codeOf(itemEvaluationV2DTO.getApproveStatus()).getScript());
            itemEvaluationV2DTO.setEvaluationDate(a.getGmtCreate());
            //卖家bin/iin
            itemEvaluationV2DTO.setMerchantBinIIn(seller.getBinOrIin());
            // 回复的信息
            List<TcEvaluationDO> itemEvaluationDOList = orderMapList.get(a.getPrimaryOrderId());
            List<ItemEvaluationV2DTO.EvaluationData> dataList = new ArrayList<>();
            Map<Long, List<TcEvaluationDO>> listMap = itemEvaluationDOList.stream().collect(Collectors.groupingBy(TcEvaluationDO::getOrderId));
            listMap.keySet().forEach(b -> {
                ItemEvaluationV2DTO.EvaluationData data = new ItemEvaluationV2DTO.EvaluationData();
                List<ItemEvaluationV2DTO.EvaluationInfo> replyList = new ArrayList<>();
                listMap.get(b).forEach(c-> {
                    Map extendMap = c.getExtend();
                    Integer evType = Integer.valueOf(extendMap.get(EV_TYPE).toString());
                    ItemEvaluationV2DTO.EvaluationInfo info = new ItemEvaluationV2DTO.EvaluationInfo();
                    info.setApproveStatus(getIntValueFromMap(extendMap, APPROVE_STATUS));
                    info.setApproveStatusDisplay(EvaluationApproveStatusEnum.codeOf(info.getApproveStatus()).getScript());
                    info.setRate(c.getRateScore());
                    info.setRateDesc(c.getRateDesc());
                    info.setRatePic(c.getRatePic());
                    info.setEvType(evType);
                    info.setRateDate(c.getGmtCreate());
                    info.setOrderId(c.getOrderId());
                    info.setItemId(c.getItemId());
                    info.setSkuId(c.getSkuId());
                    info.setId(c.getId());
                    info.setApproveContent(getStrValueFromMap(extendMap, APPROVE_CONTENT));
                    info.setProductName(MultiLangConverter.to_multiLangText(orderDOMap.get(info.getOrderId()).getItemTitle()));
                    // 主评论
                    if (TradeExtendKeyConstants.EVALUATION_TYPE_MAIN == evType) {
                        data.setEvaluationInfo(info);
                    } else {
                        replyList.add(info);
                    }
                });
                data.setReplayList(replyList);
                // 列表中需要展示回复最后一次的审批结果
                if (!replyList.isEmpty()) {
                    ItemEvaluationV2DTO.EvaluationInfo evaluationInfo = replyList.get(replyList.size() - 1);
                    itemEvaluationV2DTO.setApproveStatus(evaluationInfo.getApproveStatus());
                    itemEvaluationV2DTO.setApproveStatusDisplay(evaluationInfo.getApproveStatusDisplay());
                }
                dataList.add(data);
            });
            // productName
            itemEvaluationV2DTO.setEvaluationData(dataList);
            retList.add(itemEvaluationV2DTO);
        });
        return retList;
    }

    private String getStrValueFromMap(Map map, String key) {
        return map.get(key) == null ? null : map.get(key).toString();
    }

    private Integer getIntValueFromMap(Map map, String key) {
        String value = getStrValueFromMap(map, key);
        return value == null ? null : Integer.valueOf(value);
    }

    /**
     * 根据用户id获取用户对象
     *
     * @param id
     * @return
     */
    private Customer getCustomerByCusId(Long id) {
        //买家名称
        Customer customer = (Customer) custMap.get(id);
        if (customer == null) {
            customer = userRepository.getCustomerRequired(id);
            custMap.put(id, customer);
        }
        return customer;
    }


    protected void getEvaluation(EvaluationDTO evaluation) {
        if (evaluation.getExtend().get("evaluationApproveStatus") != null) {
            Integer evaluationApproveStatus = (Integer) evaluation.getExtend().get("evaluationApproveStatus");
            if (evaluationApproveStatus == 0) {
                evaluation.getExtend().put("evaluationApproveStatusName", EvaluationApproveStatusEnum.NEED_APPROVE.getScript());
            }
            if (evaluationApproveStatus == 1) {
                evaluation.getExtend().put("evaluationApproveStatusName", EvaluationApproveStatusEnum.APPROVING.getScript());
            }
            if (evaluationApproveStatus == 2) {
                evaluation.getExtend().put("evaluationApproveStatusName", EvaluationApproveStatusEnum.PASSED.getScript());
            }
            if (evaluationApproveStatus == 3) {
                evaluation.getExtend().put("evaluationApproveStatusName", EvaluationApproveStatusEnum.REFUSED.getScript());
            }
        }


    }

    protected List<TcEvaluationDO> convertSearchResult(DefaultSearchResult searchResult) {
        if (!searchResult.isSuccess()) {
            throw new GmallException(ErrorUtils.mapCode(String.valueOf(searchResult.getResultCode()),
                    I18NMessageUtils.getMessage("search.failed") + ":" + searchResult.getResultMsg()));  //# "搜索失败
        }

        return helpConvert(searchResult.getSearchItems());
    }

    protected List<TcEvaluationDO> helpConvert(List<Map<String, Object>> items) {
        List<TcEvaluationDO> list = new ArrayList<>();
        for (Map<String, Object> item : items) {
            TcEvaluationDO eval = new TcEvaluationDO();
            SearchConverter.convertDO(eval, item);
            list.add(eval);
        }
        return list;
    }

    protected SearchRequest buildEsRequest(EvaluationQueryRpcReq query) {
        SearchRequest request = new SearchRequest(esIndexName);
        SearchSourceBuilder ssBuilder = new SearchSourceBuilder();
        ssBuilder.from((query.getPage().getPageNo() - 1) * query.getPage().getPageSize());
        ssBuilder.size(query.getPage().getPageSize());

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        if (query.isOrderSearch()) {
            boolQueryBuilder.must(QueryBuilders.termsQuery("primary_order_id", query.getPrimaryOrderList()));
        }
        if (query.isHasItemIdSearch() || query.isHasItemId()) {
            boolQueryBuilder.filter(new TermQueryBuilder("has_item_id", query.isHasItemId()));
        }
        boolQueryBuilder.filter(new TermQueryBuilder("evaluation_type", String.valueOf(TradeExtendKeyConstants.EVALUATION_TYPE_MAIN)));
        if (Objects.nonNull(query.getItemId())) {
            boolQueryBuilder.filter(new TermQueryBuilder("item_id", query.getItemId()));
        }
        if (Objects.nonNull(query.getSkuId())) {
            boolQueryBuilder.filter(new TermQueryBuilder("sku_id", query.getSkuId()));
        }
        if (Objects.nonNull(query.getSellerId())) {
            boolQueryBuilder.filter(new TermQueryBuilder("seller_id", query.getSellerId()));
        }

        if (query.getHasMedia() != null) {
            if (query.getHasMedia()) {
                boolQueryBuilder.minimumShouldMatch(1);
                boolQueryBuilder.should(new TermQueryBuilder("has_rate_pic", true));
                boolQueryBuilder.should(new TermQueryBuilder("has_rate_video", true));
            } else {
                boolQueryBuilder.filter(new TermQueryBuilder("has_rate_pic", false));
                boolQueryBuilder.filter(new TermQueryBuilder("has_rate_video", false));
            }
        }

        if (query.getHasAddition() != null) {
            boolQueryBuilder.filter(new TermQueryBuilder("has_addition", query.getHasAddition()));
        }

//        SearchUtils.handleRange(boolQueryBuilder, "rate_score", query.getRateScoreMin(), query.getRateScoreMax());

        if (Objects.nonNull(query.getSystemEvaluation())) {
            boolQueryBuilder.filter(new TermQueryBuilder("is_system", query.getSystemEvaluation()));
        }
        if (Objects.nonNull(query.getPrimaryOrderId())) {
            boolQueryBuilder.filter(new TermQueryBuilder("primary_order_id", query.getPrimaryOrderId()));
        }
        //订单ID
        /*if(Objects.nonNull(query.getSubOrderId())) {
            boolQueryBuilder.filter(new TermQueryBuilder("order_id", query.getSubOrderId()));
        }*/
        //Merchant BIN/IIN 卖家ID
        if (Objects.nonNull(query.getSellerBIN())) {
            //boolQueryBuilder.filter(new TermQueryBuilder("seller_bin", query.getSellerBIN()));
            boolQueryBuilder.must(new WildcardQueryBuilder("seller_bin", "*" + query.getSellerBIN() + "*"));
        }
        //Merchant BIN/IIN 卖家名称
        if (Objects.nonNull(query.getSellerName())) {
            boolQueryBuilder.must(new WildcardQueryBuilder("seller_name", "*" + query.getSellerName() + "*"));
        }
        if (Objects.nonNull(query.getCustId())) {
            boolQueryBuilder.filter(new TermQueryBuilder("cust_id", query.getCustId()));
        }
        if (StringUtils.isNotBlank(query.getCustName())) {
            boolQueryBuilder.must(new MatchQueryBuilder("cust_name", query.getCustName()));
        }

        // 评论状态的多选查询
        if (MapUtils.isNotEmpty(query.getExtraFilters())) {
            for (Entry<String, List<Object>> en : query.getExtraFilters().entrySet()) {
                if (CollectionUtils.isEmpty(en.getValue())) {
                    continue;
                }
                boolQueryBuilder.filter(new TermsQueryBuilder(en.getKey(), en.getValue()));
            }
        }

        //评论时间区间
        SearchUtils.handleRange(boolQueryBuilder, "gmt_create", query.getEvaluateTimeStart(), query.getEvaluateTimeEnd());

        //查询好评
        if (EvaluationQueryRpcReq.GRADE_CLASSIFY_GOOD == query.getGradeClassify()) {
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("rate_score").gt(4).includeLower(true));
        }
        //查询差评
        if (EvaluationQueryRpcReq.GRADE_CLASSIFY_BAD == query.getGradeClassify()) {
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("rate_score").lte(4).includeUpper(false));
        }

//        // 评论等级的多选查询
//        if (query.getProductRatingArrays() != null && query.getProductRatingArrays().length >= 0) {
//            String[] productRatingArrays = query.getProductRatingArrays();
//            addMultiSelectFilter(boolQueryBuilder, productRatingArrays, "rate_score");
//        }

        // 评论状态的多选查询
        if (query.getApprovalStatus() != null && query.getApprovalStatus().length >= 0) {
            String[] approvalStatusArrays = query.getApprovalStatus();
            addMultiSelectFilter(boolQueryBuilder, approvalStatusArrays, "evaluation_approve_status");
        }

        ssBuilder.query(boolQueryBuilder);

        //去重
        ssBuilder.collapse(new CollapseBuilder("order_id"));
        ssBuilder.aggregation(AggregationBuilders.cardinality(DISTINCT_TOTAL_COUNT).field("order_id"));

        if (EvaluationQueryRpcReq.SORT_TYPE_TIME_DSC == query.getSortType()) {
            ssBuilder.sort(new FieldSortBuilder("gmt_create").order(SortOrder.DESC));
        } else if (EvaluationQueryRpcReq.SORT_TYPE_SCORE_TIME_DSC == query.getSortType()) {
            ssBuilder.sort(new FieldSortBuilder("rate_score").order(SortOrder.DESC));
            ssBuilder.sort(new FieldSortBuilder("gmt_create").order(SortOrder.DESC));

        }

        //按购买日期 排正序  查询10个
        if (EvaluationQueryRpcReq.SORT_TYPE_TIME_ASC == query.getSortType()) {
            ssBuilder.sort(new FieldSortBuilder("gmt_create").order(SortOrder.ASC));
        }

        //通过商品SKU查询商品评价 按照时间排倒序
        if (EvaluationQueryRpcReq.SORT_TYPE_TIME_ASC == query.getSortType()) {
            ssBuilder.sort(new FieldSortBuilder("gmt_create").order(SortOrder.DESC));
        }


        request.source(ssBuilder);

        return request;
    }

    // 通用方法：处理多选字段
    private static void addMultiSelectFilter(BoolQueryBuilder parentQuery, String[] values, String field) {
        if (values != null && values.length > 0) {
            parentQuery.should(new TermsQueryBuilder(field, values)).minimumShouldMatch(1);
        }
    }


    protected SimpleSearchRequest buildRequest(EvaluationQueryRpcReq query) {
        List<String> rawBuilder = new ArrayList<>();

        SimpleSearchRequest req = new SimpleSearchRequest();
        req.setAppName(appName);
        req.setPageNo(query.getPage().getPageNo());
        req.setPageSize(query.getPage().getPageSize());
        req.addDeDuplication("order_id");

        // 商品评价记录 (非店铺、非追评、非回复)
        req.addQueryParam("has_item_id", "1");
        req.addQueryParam("evaluation_type", String.valueOf(TradeExtendKeyConstants.EVALUATION_TYPE_MAIN));

        if (query.getItemId() != null) {
            req.addQueryParam("item_id", String.valueOf(query.getItemId()));
        }
        if (StringUtils.isNotBlank(query.getItemTitle())) {
            req.addQueryParam("item_title", query.getItemTitle());
        }
        if (query.getSellerId() != null) {
            req.addQueryParam("seller_id", String.valueOf(query.getSellerId()));
        }
        if (query.getHasMedia() != null) {
            if (query.getHasMedia().booleanValue()) {
                rawBuilder.add("(has_rate_pic:'1' OR has_rate_video:'1')");
            } else {
                req.addQueryParam("has_rate_pic", "0");
                req.addQueryParam("has_rate_video", "0");
            }
        }
        if (query.getHasAddition() != null) {
            req.addQueryParam("has_addition", query.getHasAddition().booleanValue() ? "1" : "0");
        }
        if (query.getRateScoreMin() != null || query.getRateScoreMax() != null) {
            addRange("rate_score", query.getRateScoreMin(), query.getRateScoreMax(), rawBuilder);
        }
        if (query.getSystemEvaluation() != null) {
            req.addQueryParam("is_system", query.getSystemEvaluation().booleanValue() ? "1" : "0");
        }
        if (query.getPrimaryOrderId() != null) {
            req.addQueryParam("primary_order_id", String.valueOf(query.getPrimaryOrderId()));
        }
        if (query.getSubOrderId() != null) {
            req.addQueryParam("order_id", String.valueOf(query.getSubOrderId()));
        }
        if (query.getEvaluateTimeStart() != null || query.getEvaluateTimeEnd() != null) {
            addRange("gmt_create", query.getEvaluateTimeStart(), query.getEvaluateTimeEnd(), rawBuilder);
        }
        if (query.getCustId() != null) {
            req.addQueryParam("cust_id", String.valueOf(query.getCustId()));
        }
        if (StringUtils.isNotBlank(query.getCustName())) {
            req.addQueryParam("cust_name", query.getCustName());
        }
        // 评论等级的多选查询
        if (query.getProductRatingArrays().length != 0) {
            Set<String> uniqueRatings = new HashSet<>();
            Arrays.stream(query.getProductRatingArrays())
                    .forEach(uniqueRatings::add);
            uniqueRatings.forEach(rating -> req.addQueryParam("rate_score", rating));
        }

        // 评论状态的多选查询
        if (query.getApprovalStatus().length != 0) {
            Set<String> uniqueStatuses = new HashSet<>();
            Arrays.stream(query.getApprovalStatus())
                    .forEach(uniqueStatuses::add);
            uniqueStatuses.forEach(status -> req.addQueryParam("approve_status", status));
        }

        if (!rawBuilder.isEmpty()) {
            req.setRawQueryStr(StringUtils.join(rawBuilder, " AND "));
        }

        if (EvaluationQueryRpcReq.SORT_TYPE_TIME_DSC == query.getSortType()) {
            req.addDecreaseSort("gmt_create");
        } else if (EvaluationQueryRpcReq.SORT_TYPE_SCORE_TIME_DSC == query.getSortType()) {
            req.addDecreaseSort("rate_score");
            req.addDecreaseSort("gmt_create");
        }
        return req;
    }

    protected void addRange(String fieldName, Date start, Date end, List<String> rawBuilder) {
        addRange(fieldName,
                start == null ? null : start.getTime(),
                end == null ? null : end.getTime(),
                rawBuilder);
    }

    protected void addRange(String fieldName, Number start, Number end, List<String> rawBuilder) {
        if (start == null && end == null) {
            return;
        }
        StringBuilder s = new StringBuilder();
        if (start != null) {
            s.append('[').append(start);
        } else {
            s.append('(');
        }
        s.append(',');
        if (end != null) {
            s.append(end).append(']');
        } else {
            s.append(')');
        }
        rawBuilder.add(fieldName + ":" + s);
    }

    /**
     * 根据主订单id获取旗下所有订单的商品名称
     *
     * @param orderNumberList
     * @return
     */
    private Map<Long, TcOrderDO> getTcOrderDOMapByOrderNumber(List<Long> orderNumberList) {
        Map<Long, TcOrderDO> retMap = new HashMap<>();
        List<TcOrderDO> list = tcOrderRepository.queryOrdersByPrimaryIds(orderNumberList);
        list.forEach( a-> {
             retMap.put(a.getOrderId(), a);
        });
        return retMap;
   }
}
