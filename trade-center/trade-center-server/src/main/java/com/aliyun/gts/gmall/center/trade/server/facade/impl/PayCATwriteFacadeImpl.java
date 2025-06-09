package com.aliyun.gts.gmall.center.trade.server.facade.impl;

import com.aliyun.gts.gmall.center.trade.api.dto.input.PaymentIdRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.input.PaymentSellerReceivedRpcReq;
import com.aliyun.gts.gmall.center.trade.api.facade.PayCATwriteFacade;
import com.aliyun.gts.gmall.center.trade.core.domainservice.PayCATService;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PayCATwriteFacadeImpl implements PayCATwriteFacade {

    @Autowired
    PayCATService payCATService;

    @Override
    public RpcResponse sellerConfirmReveived(PaymentSellerReceivedRpcReq req) {
        payCATService.sellerConfirmReveived(req);
        return RpcResponse.ok(null);
    }

    @Override
    public RpcResponse buyerConfirmTransfer(PaymentIdRpcReq req) {
        payCATService.buyerConfirmTransfer(req);
        return RpcResponse.ok(null);
    }
}
