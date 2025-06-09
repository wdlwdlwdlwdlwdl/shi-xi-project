package com.aliyun.gts.gmall.platform.trade.core.ability.search;

import java.util.Objects;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.search.OrderQueryResultExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.search.OrderSearchResultExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.search.defaultimpl.DefaultOrderSearchResultExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ListOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Ability(
    code = OrderQueryResultAbility.ORDER_QUERY_RESULT_ABILITY,
    fallback = OrderQueryResultExt.class,
    description = "处理数据库结果的能力"
)
public class OrderQueryResultAbility extends BaseAbility<BizCodeEntity, OrderQueryResultExt> {

    public static final String ORDER_QUERY_RESULT_ABILITY =
        "com.aliyun.gts.gmall.platform.trade.core.ability.search.OrderQueryResultAbility";

    public TradeBizResult<PageInfo<MainOrderDTO>> processResult(ListOrder listOrder){
        TradeBizResult<PageInfo<MainOrderDTO>>  result = this.executeExt(
            BizCodeEntity.getFromThreadLocal(),
            extension -> extension.processResult(listOrder),
            OrderQueryResultExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
        return result;
    }

}
