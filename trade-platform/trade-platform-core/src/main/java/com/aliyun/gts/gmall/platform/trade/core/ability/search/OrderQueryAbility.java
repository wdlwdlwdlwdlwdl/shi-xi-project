package com.aliyun.gts.gmall.platform.trade.core.ability.search;

import java.util.Objects;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.search.OrderQueryExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ListOrder;
import com.aliyun.gts.gmall.platform.trade.domain.wrapper.OrderQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Ability(
    code = OrderQueryAbility.ORDER_QUERY_ABILITY,
    fallback = OrderQueryExt.class,
    description = "执行数据库查询的能力"
)
public class OrderQueryAbility extends BaseAbility<BizCodeEntity, OrderQueryExt> {

    public static final String ORDER_QUERY_ABILITY =
        "com.aliyun.gts.gmall.platform.trade.core.ability.search.OrderQueryAbility";

    public TradeBizResult<ListOrder> query(OrderQueryWrapper request){
        TradeBizResult<ListOrder>  result = this.executeExt(
            BizCodeEntity.getFromThreadLocal(),
            extension -> extension.query(request),
            OrderQueryExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
        return result;
    }

}
