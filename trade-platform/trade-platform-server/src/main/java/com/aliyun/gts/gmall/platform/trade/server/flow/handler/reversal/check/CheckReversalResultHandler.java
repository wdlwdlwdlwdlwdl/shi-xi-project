package com.aliyun.gts.gmall.platform.trade.server.flow.handler.reversal.check;

import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.ReversalCheckDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.ReversalSubOrderDTO;
import com.aliyun.gts.gmall.platform.trade.core.convertor.ReversalConverter;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.ReversalCheckResult;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TReversalCheck;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CheckReversalResultHandler extends AdapterHandler<TReversalCheck> {

    @Autowired
    private ReversalConverter reversalConverter;

    @Override
    public void handle(TReversalCheck inbound) {
        MainReversal reversal = inbound.getDomain();
        List<ReversalCheckResult> fails = (List) inbound.getExtra(CheckReversalCheckHandler.KEY_FAIL_LIST);
        Set<Long> failSet = new HashSet<>();
        if (fails != null) {
            for (ReversalCheckResult fail : fails) {
                failSet.add(fail.getOrderId());
            }
        }
        //扩展信息
        List<ReversalSubOrderDTO> list = reversal.getSubReversals().stream().map(sr -> {
            ReversalSubOrderDTO r = new ReversalSubOrderDTO();
            r.setOrderId(sr.getSubOrder().getOrderId());
            r.setMaxCancelAmt(sr.getCancelAmt());
            r.setMaxCancelQty(sr.getCancelQty());
            r.setAllowReversal(!failSet.contains(r.getOrderId()) && r.getMaxCancelAmt() > 0);
            r.setFeature(sr.reversalFeatures().getFeature());
            return r;
        }).collect(Collectors.toList());
        ReversalCheckDTO result = new ReversalCheckDTO();
        result.setSubOrders(list);
        result.setMaxCancelFreightAmt(reversal.reversalFeatures().getCancelFreightAmt());
        inbound.setResult(result);
    }
}
