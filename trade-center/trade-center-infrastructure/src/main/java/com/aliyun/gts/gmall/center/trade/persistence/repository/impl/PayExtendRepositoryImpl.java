package com.aliyun.gts.gmall.center.trade.persistence.repository.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.center.pay.api.enums.PayChannelEnum;
import com.aliyun.gts.gmall.center.trade.common.constants.CATPayFeatureKeys;
import com.aliyun.gts.gmall.center.trade.domain.entity.pay.PayExtendModify;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.PayExtendRepository;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.pay.api.dto.input.OrderPayRpcReq;
import com.aliyun.gts.gmall.platform.pay.api.dto.input.PayExtendModifyRpcReq;
import com.aliyun.gts.gmall.platform.pay.api.dto.input.PayFlowIdQueryRpcReq;
import com.aliyun.gts.gmall.platform.pay.api.dto.input.PayQueryRpcReq;
import com.aliyun.gts.gmall.platform.pay.api.dto.input.inner.BizExtendModifyInfo;
import com.aliyun.gts.gmall.platform.pay.api.dto.output.dto.OrderPayDTO;
import com.aliyun.gts.gmall.platform.pay.api.dto.output.dto.PayFlowDTO;
import com.aliyun.gts.gmall.platform.pay.api.facade.OrderPayReadFacade;
import com.aliyun.gts.gmall.platform.pay.api.facade.OrderPayWriteFacade;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;
import com.aliyun.gts.gmall.platform.trade.persistence.repository.impl.OrderPayRepositoryImpl;
import com.aliyun.gts.gmall.platform.trade.persistence.rpc.util.RpcUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Configuration
@ConditionalOnProperty(
        prefix = "trade",
        name = {"orderPayRepository"},
        havingValue = "ext"
)
public class PayExtendRepositoryImpl extends OrderPayRepositoryImpl implements PayExtendRepository {

    @Autowired
    private OrderPayWriteFacade orderPayWriteFacade;
    @Autowired
    private OrderPayReadFacade orderPayReadFacade;

    @Override
    public void updatePayExtend(PayExtendModify modify) {
        BizExtendModifyInfo m = new BizExtendModifyInfo();
        m.setAddBizTags(modify.getAddBizTags());
        m.setRemoveBizTags(modify.getRemoveBizTags());
        m.setPutBizFeature(modify.getPutBizFeature());

        PayExtendModifyRpcReq req = new PayExtendModifyRpcReq();
        req.setPayId(modify.getPayId());
        req.setPayExtendModify(m);

        RpcUtils.invokeRpc(() -> orderPayWriteFacade.updatePayBizExtend(req),
                "updatePayExtend",
                "updatePayExtend",
                JSON.toJSONString(modify));
    }

    @Override
    public OrderPay queryByPayId(String payId) {
        PayQueryRpcReq req = new PayQueryRpcReq();
        req.setPayId(payId);
        req.setIncludePayFlows(true);
        RpcResponse<OrderPayDTO> resp = RpcUtils.invokeRpc(
                () -> orderPayReadFacade.queryPayInfo(req),
                "queryByPayId", "queryByPayId", payId);
        return convertOrderPay(resp.getData());
    }


    @Override
    protected OrderPayRpcReq buildToPayRequest(MainOrder mainOrder) {
        OrderPayRpcReq req = super.buildToPayRequest(mainOrder);

        // 对公、账期支付的打标
        String payChannel = mainOrder.getCurrentPayInfo().getPayChannel();
        if (PayChannelEnum.ACCOUNT_PERIOD.getCode().equals(payChannel)
                || PayChannelEnum.CAT.getCode().equals(payChannel)) {
            if (req.getBizFeature() == null) {
                req.setBizFeature(new HashMap<>());
            }
            // 待线下打款
            req.getBizFeature().put(CATPayFeatureKeys.BuyerConfirmTransfered, "0");
        }
        return req;
    }

    @Override
    public List<Map<String, String>> queryPayFlowByCartId(Long custId, Long cartId) {
        PayFlowIdQueryRpcReq req = new PayFlowIdQueryRpcReq();
        req.setCustId(custId.toString());
        req.setCartId(cartId);
        RpcResponse<List<PayFlowDTO>> resp = RpcUtils.invokeRpc(
                () -> orderPayReadFacade.queryPayFlowByCartId(req),
                "queryPayFlowByCartId", "queryPayFlowByCartId", cartId);
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        for(PayFlowDTO payFlowDTO : resp.getData()) {
            Map<String, String> payFlowMap = new HashMap<>();
            payFlowMap.put("primaryOrderId", payFlowDTO.getPrimaryOrderId());
            payFlowMap.put("cartId", payFlowDTO.getCartId());
            payFlowMap.put("payFlowId", payFlowDTO.getPayFlowId());
            payFlowMap.put("paId", payFlowDTO.getPayId());
            list.add(payFlowMap);
        }
        return list;
    }

}
