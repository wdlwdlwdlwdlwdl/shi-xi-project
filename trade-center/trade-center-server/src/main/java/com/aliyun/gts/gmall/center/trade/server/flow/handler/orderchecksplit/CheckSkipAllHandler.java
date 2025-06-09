package com.aliyun.gts.gmall.center.trade.server.flow.handler.orderchecksplit;

import com.aliyun.gts.gmall.framework.processengine.core.model.ProcessFlowNodeHandler;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.base.AbstractContextEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CheckSkipAllHandler implements ProcessFlowNodeHandler<Object, Object> {
    private static final String SKIP_KEY = "orderchecksplit.SKIP_ALL";

    @Override
    public Object handleBiz(Map<String, Object> map, Object o) {
        TOrderConfirm in = (TOrderConfirm) map.get(AbstractContextEntity.CONTEXT_KEY);
        return Boolean.TRUE.equals(in.getExtra(SKIP_KEY));
    }

    static void setSkipAll(TOrderConfirm inbound) {
        inbound.putExtra(CheckSkipAllHandler.SKIP_KEY, true);
    }
}
