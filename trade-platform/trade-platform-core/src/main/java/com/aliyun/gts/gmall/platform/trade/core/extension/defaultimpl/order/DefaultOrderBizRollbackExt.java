package com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order;

import com.aliyun.gts.gmall.platform.trade.core.extension.order.create.OrderBizRollbackExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultOrderBizRollbackExt implements OrderBizRollbackExt {

    @Override
    public void rollbackBizResource(CreatingOrder order) {

    }
}
