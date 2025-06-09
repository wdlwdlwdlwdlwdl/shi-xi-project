package com.aliyun.gts.gmall.platform.trade.server.flow.handler.reversal.check;

import com.aliyun.gts.gmall.platform.trade.core.domainservice.ReversalCreateService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.ReversalCheckResult;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TReversalCheck;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CheckReversalCheckHandler extends AdapterHandler<TReversalCheck> {
    static String KEY_FAIL_LIST = "__fails";

    @Autowired
    private ReversalCreateService reversalCreateService;

    @Override
    public void handle(TReversalCheck inbound) {
        MainReversal reversal = inbound.getDomain();
        List<ReversalCheckResult> fails = new ArrayList<>();

        // 用户校验
        reversalCreateService.checkCustomer(reversal);

        // 没有可退子订单
        if (CollectionUtils.isEmpty(reversal.getSubReversals())) {
            return;
        }

        // 订单状态校验
        fails.addAll(reversalCreateService.checkStatus(reversal));
        // 有效时间校验
        fails.addAll(reversalCreateService.checkTime(reversal));
        // 商品数量校验
        reversalCreateService.checkCancelQty(reversal);
        // 取消金额校验
        reversalCreateService.checkCancelAmt(reversal);

        inbound.putExtra(KEY_FAIL_LIST, fails);
    }
}
