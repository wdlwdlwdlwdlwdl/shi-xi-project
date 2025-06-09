package com.aliyun.gts.gmall.platform.trade.core.ability.price;


import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.price.DefaultPriceAdjustExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.price.PriceAdjustExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeNotify;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.AdjustPrice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@Ability(
    code = "com.aliyun.gts.gmall.platform.trade.core.ability.price.PriceAdjustAbility",
    fallback = DefaultPriceAdjustExt.class,
    description = "改价扩展能力"
)
public class PriceAdjustAbility extends BaseAbility<BizCodeEntity, PriceAdjustExt> {

    public List<OrderChangeNotify> adjustPrice(MainOrder mainOrder, AdjustPrice adj) {
        // 取第一个bizCode
        BizCodeEntity bizCode = BizCodeEntity.buildWithDefaultBizCode(mainOrder);
        return this.executeExt(bizCode,
                extension -> extension.adjustPrice(mainOrder, adj),
                PriceAdjustExt.class, Reducers.firstOf(Objects::nonNull));
    }
}
