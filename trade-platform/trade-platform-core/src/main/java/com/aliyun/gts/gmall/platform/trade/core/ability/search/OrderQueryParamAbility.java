package com.aliyun.gts.gmall.platform.trade.core.ability.search;

import java.util.Objects;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.CustomerOrderQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.search.OrderQueryParamExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.wrapper.OrderQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Ability(
    code = OrderQueryParamAbility.ORDER_QUERY_PARAM_ABILITY,
    fallback = OrderQueryParamExt.class,
    description = "预处理DB条件的能力"

)
public class OrderQueryParamAbility extends BaseAbility<BizCodeEntity, OrderQueryParamExt> {

    public static final String ORDER_QUERY_PARAM_ABILITY =
        "com.aliyun.gts.gmall.platform.trade.core.ability.search.OrderQueryParamAbility";

    public TradeBizResult<OrderQueryWrapper> preProcess(CustomerOrderQueryRpcReq req){
        TradeBizResult<OrderQueryWrapper> result = this.executeExt(
            BizCodeEntity.getFromThreadLocal(),
            extension -> extension.preProcess(req),
            OrderQueryParamExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
        return result;
    }

}
