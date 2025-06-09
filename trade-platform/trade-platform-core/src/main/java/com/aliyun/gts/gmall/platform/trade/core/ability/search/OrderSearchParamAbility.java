package com.aliyun.gts.gmall.platform.trade.core.ability.search;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.CustomerOrderQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.OrderSearchRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.SellerOrderQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.search.OrderSearchParamExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.searcher.common.domain.request.SimpleSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@Ability(
    code = OrderSearchParamAbility.ORDER_SEARCH_PARAM_ABILITY,
    fallback = OrderSearchParamExt.class,
    description = "预处理搜索条件的能力"

)
public class OrderSearchParamAbility  extends BaseAbility<BizCodeEntity, OrderSearchParamExt> {

    public static final String ORDER_SEARCH_PARAM_ABILITY =
        "com.aliyun.gts.gmall.platform.trade.core.ability.search.OrderSearchParamAbility";

    public TradeBizResult<SimpleSearchRequest> preProcess(CustomerOrderQueryRpcReq req){

        TradeBizResult<SimpleSearchRequest> result = this.executeExt(BizCodeEntity.getFromThreadLocal(),
            extension -> extension.preProcess(req),
            OrderSearchParamExt.class,
            Reducers.firstOf(Objects::nonNull));
        return result;
    }

    public TradeBizResult<SimpleSearchRequest> preProcess(SellerOrderQueryRpcReq req){
        TradeBizResult<SimpleSearchRequest> result = this.executeExt(BizCodeEntity.getFromThreadLocal(),
            extension -> extension.preProcess(req),
            OrderSearchParamExt.class,
            Reducers.firstOf(Objects::nonNull));
        return result;
    }

    public TradeBizResult<SimpleSearchRequest> preProcess(OrderSearchRpcReq req){
        TradeBizResult<SimpleSearchRequest> result = this.executeExt(BizCodeEntity.getFromThreadLocal(),
                extension -> extension.preProcess(req),
                OrderSearchParamExt.class,
                Reducers.firstOf(Objects::nonNull));
        return result;
    }

    public TradeBizResult<SearchRequest> preEsProcess(CustomerOrderQueryRpcReq req) {
        TradeBizResult<SearchRequest> result = this.executeExt(BizCodeEntity.getFromThreadLocal(),
                extension -> extension.preEsProcess(req),
                OrderSearchParamExt.class,
                Reducers.firstOf(Objects::nonNull));
        return result;
    }

    public TradeBizResult<SearchRequest> preEsProcess(SellerOrderQueryRpcReq req) {
        TradeBizResult<SearchRequest> result =
            this.executeExt(BizCodeEntity.getFromThreadLocal(),
                extension -> extension.preEsProcess(req),
                OrderSearchParamExt.class,
                Reducers.firstOf(Objects::nonNull)
            );
        return result;
    }

    public TradeBizResult<SearchRequest> preEsProcess(OrderSearchRpcReq req) {
        TradeBizResult<SearchRequest> result = this.executeExt(BizCodeEntity.getFromThreadLocal(),
                extension -> extension.preEsProcess(req),
                OrderSearchParamExt.class,
                Reducers.firstOf(Objects::nonNull));
        return result;
    }
}
