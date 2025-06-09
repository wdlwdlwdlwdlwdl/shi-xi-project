package com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order;

import com.aliyun.gts.gmall.platform.trade.core.extension.order.create.OrderBizCheckExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultOrderBizCheckExt implements OrderBizCheckExt {

    @Override
    public TradeBizResult checkOnConfirmOrder(CreatingOrder order) {
        return TradeBizResult.ok();
    }

    @Override
    public TradeBizResult checkOnCreateOrder(CreatingOrder order) {
        return TradeBizResult.ok();
    }
}
