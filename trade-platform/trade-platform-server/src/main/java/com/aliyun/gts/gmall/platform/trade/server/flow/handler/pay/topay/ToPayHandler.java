package com.aliyun.gts.gmall.platform.trade.server.flow.handler.pay.topay;

import com.aliyun.gts.gmall.framework.processengine.core.model.ProcessFlowNodeHandler;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.PayErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.OrderPayRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.OrderPayRpcResp;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.PayWriteService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.ToPayData;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.PayPrice;
import com.aliyun.gts.gmall.platform.trade.domain.repository.OrderPayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * 发起支付 处理类
 *    不是支付操作
 */
@Component
public class ToPayHandler implements ProcessFlowNodeHandler<OrderPayRpcReq, OrderPayRpcResp> {

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private OrderPayRepository orderPayRepository;

    @Autowired
    private PayWriteService payWriteService;

    @Override
    public OrderPayRpcResp handleBiz(Map<String, Object> map, OrderPayRpcReq req) {
        // 查一下订单
        MainOrder mainOrder = orderQueryAbility.getMainOrder(req.getPrimaryOrderId());
        if(Objects.isNull(mainOrder)) {
            throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
        }
        // 必须是自己的订单
        if (req.getCustId() != null && !req.getCustId().equals(mainOrder.getCustomer().getCustId())) {
            throw new GmallException(CommonErrorCode.NOT_DATA_OWNER);
        }
        // 校验订单状态
        payWriteService.checkToPay(mainOrder);
        // 金额校验
        checkFee(mainOrder, req);
        // 校验并变成支付渠道
        payWriteService.checkSavePayChannel(mainOrder, req.getPayChannel());
        // 扩展字段
        mainOrder.putExtra(OrderPayRepository.EXT_PAY_INFO, req.getExtraPayInfo());
        if(req.getExtra() != null) {
            mainOrder.putAllExtra(req.getExtra());
        }
        // 支付渠道
        mainOrder.setOrderChannel(req.getOrderChannel());
        // 发起支付 获取支付凭证 cartId
        ToPayData data = orderPayRepository.toPay(mainOrder);
        OrderPayRpcResp resp = new OrderPayRpcResp();
        resp.setPayData(data.getPayData());
        resp.setPayFlowId(data.getPayInfos().get(0).getPayFlowId());
        resp.setCartId(data.getCartId());
        // 生成支付token
        String token = payWriteService.generatePayToken(req.getCustId(), data.getCartId());
        resp.setPayToken(token);
        return resp;
    }


    /**
     * 支付金额比较
     *    总价
     *    实付金额
     *    积分
     * @param mainOrder
     * @param req
     */
    protected void checkFee(MainOrder mainOrder, OrderPayRpcReq req) {
        PayPrice payPrice = mainOrder.getCurrentPayInfo().getPayPrice();
        if (req.getTotalOrderFee() != null && !Objects.equals(req.getTotalOrderFee(), payPrice.getTotalAmt())) {
            throw new GmallException(PayErrorCode.PAY_FEE_CHECK_ERROR);
        }
        if (req.getRealPayFee() != null && !Objects.equals(req.getRealPayFee(), payPrice.getOrderRealAmt())) {
            throw new GmallException(PayErrorCode.PAY_FEE_CHECK_ERROR);
        }
        if (req.getPointCount() != null && !Objects.equals(req.getPointCount(), payPrice.getPointCount())) {
            throw new GmallException(PayErrorCode.PAY_FEE_CHECK_ERROR);
        }
    }
}
