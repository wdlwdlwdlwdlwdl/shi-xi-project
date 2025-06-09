package com.aliyun.gts.gmall.platform.trade.server.flow.handler.reversal.check;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.CheckReversalRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.CreateReversalRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.ability.reversal.ReversalCreateAbility;
import com.aliyun.gts.gmall.platform.trade.core.convertor.ReversalConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ReversalCreateService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.SubReversal;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TReversalCheck;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CheckReversalFillupHandler extends AdapterHandler<TReversalCheck> {

    @Autowired
    private ReversalCreateService reversalCreateService;
    @Autowired
    private ReversalConverter reversalConverter;
    @Autowired
    private ReversalCreateAbility reversalCreateAbility;

    @Override
    public void handle(TReversalCheck inbound) {
        CheckReversalRpcReq req = inbound.getReq();
        MainReversal reversal = reversalConverter.toMainReversal(req);

        // 补全订单信息
        reversalCreateService.fillOrderInfo(reversal);
        if (req.getSubOrderIds() != null) {
            // 退指定的子订单
            Set<Long> filter = new HashSet<>(req.getSubOrderIds());
            List<SubReversal> filtered = reversal.getSubReversals().stream()
                    .filter(sr -> filter.contains(sr.getSubOrder().getOrderId()))
                    .collect(Collectors.toList());
            reversal.setSubReversals(filtered);
        }
        //补全信息
        reversalCreateAbility.fillReversalInfo(buildCreate(req),reversal);
        BeanUtils.copyProperties(reversal, inbound.getDomain());
    }

    CreateReversalRpcReq buildCreate(CheckReversalRpcReq req){
        CreateReversalRpcReq create = new CreateReversalRpcReq();
        create.setCustId(req.getCustId());
        create.setPrimaryOrderId(req.getPrimaryOrderId());
        return create;
    }
}
