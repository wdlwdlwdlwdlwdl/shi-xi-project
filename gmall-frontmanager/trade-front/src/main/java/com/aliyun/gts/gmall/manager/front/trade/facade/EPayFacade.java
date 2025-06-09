package com.aliyun.gts.gmall.manager.front.trade.facade;

import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.EPayQueryReq;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.epay.EPayAccess;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.epay.EPayCard;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.epay.EPayClients;

import java.util.List;

public interface EPayFacade {

    EPayAccess getEpayAccessToken(EPayQueryReq EPayQueryReq);

    List<EPayCard> getEpayCards(EPayQueryReq EPayQueryReq);

    EPayClients getEpayClients(EPayQueryReq EPayQueryReq);

    String getInvoiceIdStatu(EPayQueryReq EPayQueryReq);

    Boolean refund(EPayQueryReq EPayQueryReq);
}
