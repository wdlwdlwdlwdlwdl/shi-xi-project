package com.aliyun.gts.gmall.platform.trade.server.flow.handler.reversal.create;

import com.aliyun.gts.gmall.platform.trade.core.domainservice.ReversalCreateService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TReversalCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * step3
 * 退单流程 -- 业务校验
 * @anthor shifeng
 * @version 1.0.1
 * 2025-3-17 14:08:25
 */
@Component
public class CreateReversalSaveHandler extends AdapterHandler<TReversalCreate> {

    @Autowired
    private ReversalCreateService reversalCreateService;

    @Override
    public void handle(TReversalCreate inbound) {
        MainReversal reversal = inbound.getDomain();
        //生成ID
        reversalCreateService.generateReversalIds(reversal);
        //生成ID
        reversalCreateService.saveReversal(reversal);
        inbound.setResult(reversal.getPrimaryReversalId());
    }
}
