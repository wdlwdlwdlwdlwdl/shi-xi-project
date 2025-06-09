package com.aliyun.gts.gmall.center.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.center.trade.api.dto.input.EvoucherQueryRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.input.EvoucherSearchRpcReq;
import com.aliyun.gts.gmall.center.trade.core.converter.EvoucherConverter;
import com.aliyun.gts.gmall.center.trade.core.domainservice.EvoucherQueryService;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.evoucher.TcEvoucherDO;
import com.aliyun.gts.gmall.center.trade.domain.entity.evoucher.EvoucherInstance;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.TcEvoucherRepository;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.common.util.OrderIdUtils;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.SearchConverter;
import com.aliyun.gts.gmall.searcher.common.SearchClient;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.testng.collections.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EvoucherQueryServiceImpl implements EvoucherQueryService {


    @Autowired
    private TcOrderRepository tcOrderRepository;
    @Autowired
    private TcEvoucherRepository tcEvoucherRepository;
    @Autowired
    private EvoucherConverter evoucherConverter;
    @Autowired
    private SearchClient searchClient;
    @Value("${opensearch.app.tcEvoucher}")
    private String searchAppName;
    @Value("${elasticsearch.name.tcEvoucher}")
    private String esIndexName;

    @Override
    public List<EvoucherInstance> queryEvouchers(EvoucherQueryRpcReq req) {
        // code 查询
        if (req.getEvCode() != null) {
            TcEvoucherDO ev = tcEvoucherRepository.queryByEvCode(req.getEvCode());
            if (ev == null) {
                return Lists.newArrayList();
            }
            checkOwner(ev.getOrderId(), req.getCustId(), req.getSellerId());
            return evoucherConverter.toEntityList(Arrays.asList(ev));
        }
        // 订单查询
        else if (req.getOrderId() != null) {
            List<TcEvoucherDO> list = tcEvoucherRepository.queryByOrderId(req.getOrderId());
            if (list == null || list.isEmpty()) {
                return Lists.newArrayList();
            }
            checkOwner(req.getOrderId(), req.getCustId(), req.getSellerId());
            return evoucherConverter.toEntityList(list);
        }
        // 无查询条件
        else {
            return Lists.newArrayList();
        }
    }

    @Override
    public PageInfo<EvoucherInstance> searchEvouchers(EvoucherSearchRpcReq req) {
        SearchRequest request = new SearchRequest(this.esIndexName);
        SearchSourceBuilder ssBuilder = new SearchSourceBuilder();
        PageParam page = req.getPage();
        ssBuilder.from((page.getPageNo()-1)*page.getPageSize());
        ssBuilder.size(page.getPageSize());
        request.source(ssBuilder);

        if (req.getSortType() == EvoucherSearchRpcReq.SORT_WRITEOFF_DESC) {
            ssBuilder.sort("writeoff_time", SortOrder.DESC);
            ssBuilder.sort("gmt_create", SortOrder.DESC);
        } else if (req.getSortType() == EvoucherSearchRpcReq.SORT_CREATE_DESC) {
            ssBuilder.sort("gmt_create", SortOrder.DESC);
        }

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        if (req.getSellerId() != null) {
            boolQueryBuilder.filter(new TermQueryBuilder("seller_id", req.getSellerId()));
        }
        if (CollectionUtils.isNotEmpty(req.getStatus())) {
            boolQueryBuilder.filter(new TermsQueryBuilder("status", req.getStatus()));
        }
        ssBuilder.query(boolQueryBuilder);

        SearchResponse searchResponse = searchClient.search(request);

        long totalCount = searchResponse.getHits().getTotalHits().value;


        List<Map<String, Object>> items = Arrays.stream(searchResponse.getHits().getHits()).map(SearchHit::getSourceAsMap).collect(Collectors.toList());

        List<TcEvoucherDO> doList = mapToDO(items);
        List<EvoucherInstance> entityList = evoucherConverter.toEntityList(doList);
        PageInfo<EvoucherInstance> result = new PageInfo<>();
        result.setList(entityList);
        result.setTotal(totalCount);
        return result;
    }

    private List<TcEvoucherDO> mapToDO(List<Map<String, Object>> searchItems) {
        List<TcEvoucherDO> list = new ArrayList<>();
        for (Map<String, Object> item : searchItems) {
            TcEvoucherDO r = new TcEvoucherDO();
            SearchConverter.convertDO(r, item);
            list.add(r);
        }
        return list;
    }

    private void checkOwner(Long orderId, Long custId, Long sellerId) {
        Long primaryOrderId = OrderIdUtils.getPrimaryOrderIdByOrderId(orderId);
        TcOrderDO order = tcOrderRepository.querySubByOrderId(primaryOrderId, orderId);
        if (order == null) {
            return;
        }
        if (custId != null && custId.longValue() != order.getCustId().longValue()) {
            throw new GmallException(CommonErrorCode.NOT_DATA_OWNER);
        }
        if (sellerId != null && sellerId.longValue() != order.getSellerId().longValue()) {
            throw new GmallException(CommonErrorCode.NOT_DATA_OWNER);
        }
    }
}
