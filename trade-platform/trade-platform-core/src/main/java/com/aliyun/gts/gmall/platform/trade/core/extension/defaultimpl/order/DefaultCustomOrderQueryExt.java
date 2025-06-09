package com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order;

import com.aliyun.gts.gmall.platform.trade.core.extension.order.CustomOrderQueryExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultCustomOrderQueryExt implements CustomOrderQueryExt {
    @Override
    public TradeBizResult enrichMainOrder(MainOrder mainOrder) {
        return TradeBizResult.ok();
    }
}
