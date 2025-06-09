package com.aliyun.gts.gmall.platform.trade.server.flow.handler.reversal.create;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.CreateReversalRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.ability.reversal.ReversalCreateAbility;
import com.aliyun.gts.gmall.platform.trade.core.convertor.ReversalConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ReversalCreateService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TReversalCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * step1
 * 发起退单流程
 * @anthor shifeng
 * @version 1.0.1
 * 2025-3-17 14:08:25
 */
@Component
public class CreateReversalFillupHandler extends AdapterHandler<TReversalCreate> {

    @Autowired
    private ReversalConverter reversalConverter;

    @Autowired
    private ReversalCreateService reversalCreateService;

    @Autowired
    private ReversalCreateAbility reversalCreateAbility;

    @Override
    public void handle(TReversalCreate inbound) {
        CreateReversalRpcReq req = inbound.getReq();
        // 转换参数
        MainReversal reversal = reversalConverter.toMainReversal(req);
        // 补全订单信息
        reversalCreateService.fillOrderInfo(reversal);
        // 补全退单信息扩展 扩展点 啥也没做
        reversalCreateAbility.fillReversalInfo(req,reversal);
        // 参数处理
        BeanUtils.copyProperties(reversal, inbound.getDomain());
    }
}
