package com.aliyun.gts.gmall.platform.trade.core.ability.search;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.search.OrderSearchResultExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ListOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@Ability(
    code = OrderSearchResultAbility.ORDER_SEARCH_RESULT_ABILITY,
    fallback = OrderSearchResultExt.class,
    description = "处理搜索结果的能力"
)
public class OrderSearchResultAbility extends BaseAbility<BizCodeEntity, OrderSearchResultExt> {

    public static final String ORDER_SEARCH_RESULT_ABILITY =
        "com.aliyun.gts.gmall.platform.trade.core.ability.search.OrderSearchResultAbility";

    public TradeBizResult<PageInfo<MainOrderDTO>> processResult(ListOrder listOrder){
        TradeBizResult<PageInfo<MainOrderDTO>>  result =
            this.executeExt(BizCodeEntity.getFromThreadLocal(),
                extension -> extension.processResult(listOrder),
                OrderSearchResultExt.class,
                Reducers.firstOf(Objects::nonNull)
            );
        return result;
    }

    public TradeBizResult<PageInfo<MainOrderDTO>> processAdminResult(ListOrder listOrder){

        TradeBizResult<PageInfo<MainOrderDTO>>  result = this.executeExt(BizCodeEntity.getFromThreadLocal(),
                extension -> extension.processAdminResult(listOrder),
                OrderSearchResultExt.class,
                Reducers.firstOf(Objects::nonNull));
        return result;
    }


    public TradeBizResult<PageInfo<MainOrderDTO>> processCustResult(ListOrder listOrder){

        TradeBizResult<PageInfo<MainOrderDTO>>  result = this.executeExt(BizCodeEntity.getFromThreadLocal(),
                extension -> extension.processCustResult(listOrder),
                OrderSearchResultExt.class,
                Reducers.firstOf(Objects::nonNull));
        return result;
    }

//OrderSearchResultAbility
}
