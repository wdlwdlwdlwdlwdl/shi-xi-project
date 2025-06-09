package com.aliyun.gts.gmall.platform.trade.server.flow.handler.pay.topay;

import com.aliyun.gts.gmall.framework.processengine.core.model.ProcessFlowNodeHandler;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.PayErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.OrderMergePayRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.MergePayFlowInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.OrderMergePayRpcResp;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.PayWriteService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.ToPayData;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.PayPrice;
import com.aliyun.gts.gmall.platform.trade.domain.repository.OrderPayRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 多单合并支付
 */
@Component
public class ToMergePayHandler implements ProcessFlowNodeHandler<OrderMergePayRpcReq, OrderMergePayRpcResp> {

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private OrderPayRepository orderPayRepository;

    @Autowired
    private PayWriteService payWriteService;

    @Override
    public OrderMergePayRpcResp handleBiz(Map<String, Object> map, OrderMergePayRpcReq req) {
        List<MainOrder> mainOrders = new ArrayList<>();
        // 遍历每个订单
        for (Long primaryOrderId : req.getPrimaryOrderIds()) {
            MainOrder mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
            if(Objects.isNull(mainOrder)) {
                throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
            }
            // 必须是自己的订单
            if (req.getCustId() != null && !req.getCustId().equals(mainOrder.getCustomer().getCustId())) {
                throw new GmallException(CommonErrorCode.NOT_DATA_OWNER);
            }
            // 校验订单状态
            payWriteService.checkToPay(mainOrder);
            mainOrders.add(mainOrder);
            mainOrder.putExtra(OrderPayRepository.EXT_PAY_INFO, req.getExtraPayInfo());
        }
        // 金额校验
        checkFee(mainOrders, req);

        // 每个订单校验 修改支付渠道
        for (MainOrder mainOrder : mainOrders) {
            payWriteService.checkSavePayChannel(mainOrder, req.getPayChannel());
            mainOrder.setOrderChannel(req.getOrderChannel());
        }

        // 发起支付 多个订单一起 获取支付凭证 cartId
        ToPayData data = orderPayRepository.toMergePay(mainOrders);
        OrderMergePayRpcResp resp = new OrderMergePayRpcResp();
        resp.setPayData(data.getPayData());
        resp.setCartId(data.getCartId());

        // 生成支付token
        String token = payWriteService.generatePayToken(req.getCustId(), data.getCartId());
        resp.setPayToken(token);
        resp.setMergePayFlowInfos(
            data.getPayInfos().stream().map(p -> {
                MergePayFlowInfo target = new MergePayFlowInfo();
                target.setPayFlowId(p.getPayFlowId());
                target.setPrimaryOrderId(p.getPrimaryOrderId());
                return target;
            }
        ).collect(Collectors.toList()));
        return resp;
    }

    /**
     * 支付金额比较
     *    总价
     *    实付金额
     *    积分
     * @param mainOrders
     * @param req
     */
    protected void checkFee(List<MainOrder> mainOrders, OrderMergePayRpcReq req) {
        // 请和后在比较
        long sumRealAmt = 0L;
        long sumTotalAmt = 0L;
        long sumPointCount = 0L;
        for (MainOrder mainOrder : mainOrders) {
            PayPrice payPrice = mainOrder.getCurrentPayInfo().getPayPrice();
            sumRealAmt += NumUtils.getNullZero(payPrice.getOrderRealAmt());
            sumTotalAmt += NumUtils.getNullZero(payPrice.getTotalAmt());
            sumPointCount += NumUtils.getNullZero(payPrice.getPointCount());
        }
        if (req.getTotalOrderFee() != null && !Objects.equals(req.getTotalOrderFee(), sumTotalAmt)) {
            throw new GmallException(PayErrorCode.PAY_FEE_CHECK_ERROR);
        }
        if (req.getRealPayFee() != null && !Objects.equals(req.getRealPayFee(), sumRealAmt)) {
            throw new GmallException(PayErrorCode.PAY_FEE_CHECK_ERROR);
        }
        if (req.getPointCount() != null && !Objects.equals(req.getPointCount(), sumPointCount)) {
            throw new GmallException(PayErrorCode.PAY_FEE_CHECK_ERROR);
        }
    }
}
