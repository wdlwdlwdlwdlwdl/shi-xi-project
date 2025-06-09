package com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order;

import com.aliyun.gts.gmall.platform.trade.core.extension.order.OrderExtendsExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultOrderExtendsExt implements OrderExtendsExt {
    @Override
    public TradeBizResult addExtendOnCrete(MainOrder mainOrder, CreatingOrder creatingOrder) {
        return TradeBizResult.ok();
    }
}
