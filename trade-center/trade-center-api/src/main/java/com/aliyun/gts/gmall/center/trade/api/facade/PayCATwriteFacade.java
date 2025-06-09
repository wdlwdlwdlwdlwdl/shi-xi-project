package com.aliyun.gts.gmall.center.trade.api.facade;

import com.aliyun.gts.gmall.center.trade.api.dto.input.PaymentIdRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.input.PaymentSellerReceivedRpcReq;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;

public interface PayCATwriteFacade {

    /**
     * 卖家确认收款
     * @param req
     * @return
     */
    RpcResponse sellerConfirmReveived(PaymentSellerReceivedRpcReq req);

    /**
     * 买家确认打款
     * @param req
     * @return
     */
    RpcResponse buyerConfirmTransfer(PaymentIdRpcReq req);

}
