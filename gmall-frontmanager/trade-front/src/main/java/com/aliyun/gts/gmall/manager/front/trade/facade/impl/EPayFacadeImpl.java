package com.aliyun.gts.gmall.manager.front.trade.facade.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.manager.front.trade.adaptor.EPayAdapter;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.EPayQueryReq;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.epay.EPayAccess;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.epay.EPayCard;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.epay.EPayClients;
import com.aliyun.gts.gmall.manager.front.trade.facade.EPayFacade;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class EPayFacadeImpl implements EPayFacade {

    @Autowired
    private EPayAdapter EPayAdapter;



    @Override
    public EPayAccess getEpayAccessToken(EPayQueryReq EPayQueryReq) {
        String rpcResponse = EPayAdapter.getEpayAccessToken(EPayQueryReq);
        EPayAccess EPayAccess = new EPayAccess();
        if (StringUtils.isNotEmpty(rpcResponse) && JSONObject.parseObject(rpcResponse).containsKey("access_token")) {
            JSONObject jsonObject= JSONObject.parseObject(rpcResponse);
            EPayAccess.setAccessToken(jsonObject.getString("access_token"));
            EPayAccess.setExpiresIn(jsonObject.getString("expires_in"));
        }
        return EPayAccess;
    }

    @Override
    public List<EPayCard> getEpayCards(EPayQueryReq EPayQueryReq) {
        String rpcResponse = EPayAdapter.getEpayCards(EPayQueryReq);
        log.info("getEpayCards:"+rpcResponse);
        List<EPayCard> cardList = new ArrayList<>();
        if (StringUtils.isNotEmpty(rpcResponse)) {
            //没有返回 cards
            if(rpcResponse.startsWith("{")){
                return cardList;
            }
            List<JSONObject> list = JSONObject.parseArray(rpcResponse).toJavaList(JSONObject.class);
            for (JSONObject cardJson : list) {
                EPayCard card = new EPayCard();
                card.setId(cardJson.getString("ID"));
//                card.setEpayCardID(cardJson.getString("EpayCardID"));
//                card.setOpenwayID(cardJson.getString("OpenwayID"));
                card.setAccountID(cardJson.getString("AccountID"));
//                card.setCardHash(cardJson.getString("CardHash"));
                card.setCardMask(cardJson.getString("CardMask"));
                card.setMerchantID(cardJson.getString("MerchantID"));
//                card.setPayerName(cardJson.getString("PayerName"));
//                card.setCardName(cardJson.getString("CardName"));
//                card.setPaymentAvailable(cardJson.getString("PaymentAvailable"));
//                card.setToken(cardJson.getString("Token"));
//                card.setTerminal(cardJson.getString("Terminal"));
                card.setActive(cardJson.getBoolean("Active"));
//                card.setForeign(cardJson.getBoolean("Foreign"));
//                String createdDate = cardJson.getString("CreatedDate");
//                card.setCreatedDate(createdDate);
//                card.setReference(cardJson.getString("Reference"));
//                card.setIntReference(cardJson.getString("IntReference"));
                card.setType(cardJson.getString("Type"));
                card.setCreatedDate(cardJson.getString("CreatedDate"));
                cardList.add(card);
            }
        }
        return cardList;
    }

    @Override
    public EPayClients getEpayClients(EPayQueryReq EPayQueryReq) {
        String rpcResponse = EPayAdapter.getEpayClients(EPayQueryReq);
        EPayClients EPayClients = new EPayClients();
        JSONObject jsonObject= JSONObject.parseObject(rpcResponse);
        EPayClients.setClientId(jsonObject.getString("client_id"));
        EPayClients.setClientSecret(jsonObject.getString("client_secret"));
        EPayClients.setGrantType(jsonObject.getString("grant_type"));
        EPayClients.setTerminal(jsonObject.getString("terminal"));
        EPayClients.setCurrency(jsonObject.getString("currency"));
        EPayClients.setMock(jsonObject.getString("mock"));
        return EPayClients;
    }

    @Override
    public String getInvoiceIdStatu(EPayQueryReq EPayQueryReq) {
        String result = EPayAdapter.getInvoiceIdStatu(EPayQueryReq);
        return result;
    }

    @Override
    public Boolean refund(EPayQueryReq EPayQueryReq) {
        Boolean result = EPayAdapter.refund(EPayQueryReq);
        return result;
    }
}
