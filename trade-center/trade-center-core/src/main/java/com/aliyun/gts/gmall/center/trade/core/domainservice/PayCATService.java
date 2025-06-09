package com.aliyun.gts.gmall.center.trade.core.domainservice;

import com.aliyun.gts.gmall.center.trade.api.dto.input.PaymentIdRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.input.PaymentSellerReceivedRpcReq;
import com.aliyun.gts.gmall.platform.pay.api.dto.output.dto.PayFlowDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.ConfirmPayCheckRpcReq;

import java.util.List;
import java.util.Map;

public interface PayCATService {

    void sellerConfirmReveived(PaymentSellerReceivedRpcReq req);

    void buyerConfirmTransfer(PaymentIdRpcReq req);

    List<Map<String, String>> queryPayFlowByCartId(ConfirmPayCheckRpcReq req);
}
