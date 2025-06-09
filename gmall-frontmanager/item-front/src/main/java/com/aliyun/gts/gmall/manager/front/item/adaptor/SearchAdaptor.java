package com.aliyun.gts.gmall.manager.front.item.adaptor;

import java.util.*;
import java.util.function.Function;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.framework.common.DubboBuilder;
import com.aliyun.gts.gmall.manager.front.common.config.DatasourceConfig;
import com.aliyun.gts.gmall.manager.front.common.config.DegradationConfig;
import com.aliyun.gts.gmall.manager.front.common.consts.BizConst;
import com.aliyun.gts.gmall.manager.front.common.consts.DsIdConst;
import com.aliyun.gts.gmall.manager.front.item.convertor.ItemConvertor;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemEvaluationVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemSearchVO;
import com.aliyun.gts.gmall.platform.trade.api.constant.TradeExtendKeyConstants;
import com.aliyun.gts.gmall.platform.trade.common.constants.EvaluationApproveStatusEnum;
import com.aliyun.gts.gmall.searcher.api.dto.input.EvaluationQueryRequest;
import com.aliyun.gts.gmall.searcher.api.dto.input.ItemQueryRequest;
import com.aliyun.gts.gmall.searcher.api.dto.input.sort.EvaluationQuerySortMode;
import com.aliyun.gts.gmall.searcher.api.dto.output.evaluation.EvaluationSearchDTO;
import com.aliyun.gts.gmall.searcher.api.dto.output.item.ItemSearchDTO;
import com.aliyun.gts.gmall.searcher.api.facade.EvaluationQueryFacade;
import com.aliyun.gts.gmall.searcher.api.facade.ItemQueryFacade;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 搜索服务(商品、评价)
 *
 * @author tiansong
 */
@Slf4j
@Service
public class SearchAdaptor {
    @Autowired
    private ItemQueryFacade       itemQueryFacade;
    @Autowired
    private EvaluationQueryFacade evaluationQueryFacade;
    @Autowired
    private ItemConvertor         itemConvertor;
    @Autowired
    private DatasourceConfig      datasourceConfig;

    DubboBuilder searchBuilder = DubboBuilder.builder().logger(log).strong(Boolean.FALSE).build();

    public ItemSearchVO queryById(Long itemId) {
        List<ItemSearchVO> result = this.queryItemByIds(Arrays.asList(itemId));
        return CollectionUtils.isEmpty(result) ? null : result.get(0);
    }

    public List<ItemSearchVO> queryItemByIds(List<Long> itemIdList) {
        List<String> idStrList = new ArrayList<>(itemIdList.size());
        itemIdList.forEach(itemId -> {
            idStrList.add(String.valueOf(itemId));
        });
        ItemQueryRequest itemQueryRequest = new ItemQueryRequest();
        itemQueryRequest.setItemIds(idStrList);
        return searchBuilder.create(datasourceConfig).id(DsIdConst.item_search_queryItemBatch).queryFunc(
                (Function<ItemQueryRequest, RpcResponse<List<ItemSearchVO>>>) request -> {
                    RpcResponse<PageInfo<ItemSearchDTO>> rpcResponse = itemQueryFacade.queryItem(request);
                    if (rpcResponse.isSuccess() && rpcResponse.getData() != null && !rpcResponse.getData().isEmpty()) {
                        return RpcResponse.ok(itemConvertor.convertSearch(rpcResponse.getData().getList()));
                    }
                    return rpcResponse.isSuccess() ?
                            RpcResponse.ok(Collections.EMPTY_LIST) : RpcResponse.fail(rpcResponse.getFail());
                }).query(itemQueryRequest);
    }

    // --------------------------- evaluation --------------------

    public long queryTotalCount(Long itemId) {
        PageInfo<ItemEvaluationVO> pageInfo = this.queryEvaluationList(itemId, 1, 1,
                EvaluationQuerySortMode.DEFAULT_SORT);
        return pageInfo == null || pageInfo.isEmpty() ? 0 : pageInfo.getTotal();
    }

    public PageInfo<ItemEvaluationVO> queryList(Long itemId, Integer pageNo) {
        PageInfo<ItemEvaluationVO> pageInfo = this.queryEvaluationList(itemId, pageNo, BizConst.PAGE_SIZE,
                EvaluationQuerySortMode.DEFAULT_SORT);
        return pageInfo == null ? PageInfo.empty() : pageInfo;
    }

    public PageInfo<ItemEvaluationVO> queryHot(Long itemId) {
        PageInfo<ItemEvaluationVO> pageInfo = this.queryEvaluationList(itemId, BizConst.PAGE_NO,
                BizConst.HOT_EVALUATION_SIZE, EvaluationQuerySortMode.RATE_SCORE_DESC_SORT);
        return pageInfo == null ? PageInfo.empty() : pageInfo;
    }

    private PageInfo<ItemEvaluationVO> queryEvaluationList(Long itemId, Integer pageNo, Integer pageSize,
                                                           EvaluationQuerySortMode sortMode) {
        if (pageSize <= 0 || pageSize > BizConst.PAGE_SIZE) {
            pageSize = BizConst.PAGE_SIZE;
        }
        EvaluationQueryRequest evaluationQueryRequest = new EvaluationQueryRequest();
        evaluationQueryRequest.setItemId(String.valueOf(itemId));
        evaluationQueryRequest.setPage(new PageParam(pageNo, pageSize));
        evaluationQueryRequest.setSortMode(sortMode.getCode());
        evaluationQueryRequest.setMainReply(Boolean.TRUE);
        fillApproveStatus(evaluationQueryRequest);
        return searchBuilder.create(datasourceConfig).id(DsIdConst.item_search_queryEvaluation).queryFunc(
                (Function<EvaluationQueryRequest, RpcResponse<PageInfo<ItemEvaluationVO>>>) request -> {
                    RpcResponse<PageInfo<EvaluationSearchDTO>> rpcResponse = evaluationQueryFacade.queryEvaluation(request);
                    if (rpcResponse.isSuccess() && rpcResponse.getData() != null && !rpcResponse.getData().isEmpty()) {
                        return RpcResponse.ok(new PageInfo(rpcResponse.getData().getTotal(),
                                itemConvertor.convertEvaluation(rpcResponse.getData().getList())));
                    }
                    return rpcResponse.isSuccess() ?
                            RpcResponse.ok(PageInfo.empty()) : RpcResponse.fail(rpcResponse.getFail());
                }).query(evaluationQueryRequest);
    }

    public List<ItemEvaluationVO> queryReply(List<Long> orderIds) {
        if (CollectionUtils.isEmpty(orderIds)) {
            return Collections.EMPTY_LIST;
        }
        EvaluationQueryRequest evaluationQueryRequest = new EvaluationQueryRequest();
        evaluationQueryRequest.setMainReply(Boolean.FALSE);
        evaluationQueryRequest.setPage(new PageParam(BizConst.PAGE_NO, BizConst.MAX_SEARCH_SIZE));
        evaluationQueryRequest.setSubOrderIds(orderIds);
        fillApproveStatus(evaluationQueryRequest);
        return searchBuilder.create(datasourceConfig).id(DsIdConst.item_search_queryReply).queryFunc(
                (Function<EvaluationQueryRequest, RpcResponse<List<ItemEvaluationVO>>>) request -> {
                    RpcResponse<PageInfo<EvaluationSearchDTO>> rpcResponse = evaluationQueryFacade
                            .queryEvaluation(request);
                    if (rpcResponse.isSuccess() && rpcResponse.getData() != null && !rpcResponse.getData().isEmpty()) {
                        return RpcResponse.ok(itemConvertor.convertEvaluation(rpcResponse.getData().getList()));
                    }
                    return rpcResponse.isSuccess() ?
                            RpcResponse.ok(Collections.EMPTY_LIST) : RpcResponse.fail(rpcResponse.getFail());
                }).query(evaluationQueryRequest);
    }

    private void fillApproveStatus(EvaluationQueryRequest evaluationQueryRequest) {
        // 审核状态
        Map<String, List<Object>> extraFilters = evaluationQueryRequest.getExtraFilters();
        if (extraFilters == null) {
            extraFilters = new HashMap<>();
            evaluationQueryRequest.setExtraFilters(extraFilters);
        }
        extraFilters.put(TradeExtendKeyConstants.EVALUATION_APPROVE_STATUS_SEARCH_FIELD,
                Lists.newArrayList(EvaluationApproveStatusEnum.PASSED.getCode()));
    }
}