package com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order;

import com.aliyun.gts.gmall.platform.trade.core.extension.order.create.OrderCreateEdgeExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultOrderCreateEdgeExt implements OrderCreateEdgeExt {

    @Override
    public void beginCreate(CreatingOrder order) {

    }

    @Override
    public void orderSaved(CreatingOrder order) {

    }

    @Override
    public void failedWithoutSave(CreatingOrder order) {

    }
}
