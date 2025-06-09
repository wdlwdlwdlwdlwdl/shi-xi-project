package com.aliyun.gts.gmall.platform.trade.server.flow.handler.pay.payrender;

import com.aliyun.gts.gmall.framework.processengine.core.model.ProcessFlowNodeHandler;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.PayRenderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.PayChannelInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.PayRenderOrderInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.PayRenderRpcResp;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderPayInfo;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.PayChannel;
import com.aliyun.gts.gmall.platform.trade.domain.repository.OrderPayRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PayRenderHandler implements ProcessFlowNodeHandler<PayRenderRpcReq, PayRenderRpcResp> {

    @Autowired
    private OrderQueryAbility orderQueryAbility;
    @Autowired
    private OrderPayRepository orderPayRepository;

    @Override
    public PayRenderRpcResp handleBiz(Map<String, Object> map, PayRenderRpcReq req) {
        MainOrder mainOrder = orderQueryAbility.getMainOrder(req.getPrimaryOrderId());
        if (mainOrder == null) {
            throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
        }
        if (req.getCustId() != null && !req.getCustId().equals(mainOrder.getCustomer().getCustId())) {
            throw new GmallException(CommonErrorCode.NOT_DATA_OWNER);
        }

        OrderPayInfo payInfo = mainOrder.getCurrentPayInfo();

        PayRenderRpcResp resp = new PayRenderRpcResp();
        resp.setCustId(mainOrder.getCustomer().getCustId());
        resp.setDefaultPayChannel(getChannelInfo(payInfo.getPayChannel()));
        resp.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        resp.setTotalOrderFee(payInfo.getPayPrice().getTotalAmt());
        resp.setRealPayFee(payInfo.getPayPrice().getOrderRealAmt());
        resp.setOrderUsedPointAmount(payInfo.getPayPrice().getPointAmt());
        resp.setOrderUsedPointCount(payInfo.getPayPrice().getPointCount());
        resp.setSupportPayChannel(getAllChannels(mainOrder));
        resp.setPayRenderOrderInfos(mainOrder.getSubOrders().stream().map(sub -> {
            PayRenderOrderInfo t = new PayRenderOrderInfo();
            t.setItemId(sub.getItemSku().getItemId().toString());
            t.setItemMainImgUrl(sub.getItemSku().getItemPic());
            t.setItemSkuId(sub.getItemSku().getSkuId().toString());
            t.setSubOrderId(sub.getOrderId().toString());
            return t;
        }).collect(Collectors.toList()));
        resp.setStepNo(mainOrder.orderAttr().getCurrentStepNo());
        resp.setPayTimeout(null);
        resp.setPayDiscount(null);
        resp.setJoinPayOnlineDiscnt(false);
        return resp;
    }

    private List<PayChannelInfo> getAllChannels(MainOrder mainOrder) {
        List<PayChannel> payChannels = orderPayRepository.queryAllPayChannel();
        return payChannels.stream().map(chl -> {
            PayChannelInfo t = new PayChannelInfo();
            t.setPayChannel(chl.getChannelCode());
            t.setPayChannelName(chl.getChannelName());
            t.setPayMethod(chl.getMethodCode());
            t.setPayMethodName(chl.getMethodName());
            return t;
        }).collect(Collectors.toList());
    }

    private PayChannelInfo getChannelInfo(String payChannel) {
        List<PayChannel> payChannels = orderPayRepository.queryAllPayChannel();
        PayChannel channel = payChannels.stream()
                .filter(chl -> StringUtils.equals(chl.getChannelCode(), payChannel))
                .findFirst().orElse(null);
        if (channel == null) {
            return null;
        }

        PayChannelInfo t = new PayChannelInfo();
        t.setPayChannel(channel.getChannelCode());
        t.setPayChannelName(channel.getChannelName());
        t.setPayMethod(channel.getMethodCode());
        t.setPayMethodName(channel.getMethodName());
        return t;
    }
}
