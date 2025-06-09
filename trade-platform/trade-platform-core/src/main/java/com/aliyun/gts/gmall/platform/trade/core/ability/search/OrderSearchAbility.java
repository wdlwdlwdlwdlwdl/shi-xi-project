package com.aliyun.gts.gmall.platform.trade.core.ability.search;

import java.util.Objects;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.search.OrderSearchExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ListOrder;
import com.aliyun.gts.gmall.searcher.common.domain.request.SimpleSearchRequest;
import com.aliyun.gts.gmall.searcher.common.domain.request.SkuSalesSearchRequest;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Ability(
    code = OrderSearchAbility.ORDER_SEARCH_ABILITY,
    fallback = OrderSearchExt.class,
    description = "执行搜索的能力"
)
public class OrderSearchAbility extends BaseAbility<BizCodeEntity, OrderSearchExt> {

    public static final String ORDER_SEARCH_ABILITY =
        "com.aliyun.gts.gmall.platform.trade.core.ability.search.OrderSearchAbility";

    public TradeBizResult<ListOrder> search(SimpleSearchRequest request){
        TradeBizResult<ListOrder>  result =
            this.executeExt(BizCodeEntity.getFromThreadLocal(),
                extension -> extension.search(request),
                OrderSearchExt.class,
                Reducers.firstOf(Objects::nonNull)
            );
        return result;
    }

    public TradeBizResult<ListOrder> search(SearchRequest request) {
        TradeBizResult<ListOrder>  result =
            this.executeExt(
                BizCodeEntity.getFromThreadLocal(),
                extension -> extension.search(request),
                OrderSearchExt.class,
                Reducers.firstOf(Objects::nonNull)
            );
        return result;
    }


    /**
     * 根据sku查询订单信息
     * @param request
     * @return
     */
    public TradeBizResult<Double> searchForSkuSales(SkuSalesSearchRequest request) {
        TradeBizResult<Double> result =
                this.executeExt(
                        BizCodeEntity.getFromThreadLocal(),
                        extension -> extension.searchForSkuSales(request),
                        OrderSearchExt.class,
                        Reducers.firstOf(Objects::nonNull)
                );
        return result;
    }


}
