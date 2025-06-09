package com.aliyun.gts.gmall.manager.front.trade.component.impl;

import com.aliyun.gts.gmall.center.pay.api.dto.input.settle.OrderPayQueryRpcReq;
import com.aliyun.gts.gmall.center.pay.api.dto.output.settle.OrderPayRowRpcResp;
import com.aliyun.gts.gmall.center.pay.api.facade.SettleReadFacade;
import com.aliyun.gts.gmall.center.trade.api.dto.input.PaymentIdRpcReq;
import com.aliyun.gts.gmall.center.trade.api.facade.PayCATwriteFacade;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.trade.component.PayComponent;
import com.aliyun.gts.gmall.manager.front.trade.convertor.PayConverter;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.BuyerConfirmPayRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.PayQueryReq;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.pay.PaySearchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayComponentImpl implements PayComponent {

    @Autowired
    SettleReadFacade settleReadFacade;

    @Autowired
    PayConverter payConverter;

    @Autowired
    PayCATwriteFacade payCATwriteFacade;

    @Override
    public PageInfo<PaySearchVO> query(PayQueryReq req) {
        OrderPayQueryRpcReq orderPayQueryRpcReq = payConverter.convert(req);
        RpcResponse<PageInfo<OrderPayRowRpcResp>> response = settleReadFacade.queryOrderPay(orderPayQueryRpcReq);
        PageInfo<OrderPayRowRpcResp> respPageInfo = response.getData();
        PageInfo<PaySearchVO> resp = payConverter.convert(respPageInfo);
        return resp;
    }

    @Override
    public void buyerConfirmPay(BuyerConfirmPayRestCommand req) {
        PaymentIdRpcReq paymentIdRpcReq = payConverter.convert(req);
        payCATwriteFacade.buyerConfirmTransfer(paymentIdRpcReq);
    }
}
