package com.aliyun.gts.gmall.manager.front.trade.adaptor;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.framework.common.DubboBuilder;
import com.aliyun.gts.gmall.manager.front.common.config.DegradationConfig;
import com.aliyun.gts.gmall.manager.front.common.config.GmallFrontConfig;
import com.aliyun.gts.gmall.manager.front.common.consts.DsIdConst;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.trade.convertor.TradeRequestConvertor;
import com.aliyun.gts.gmall.manager.front.trade.convertor.TradeResponseConvertor;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.AddCardRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.OrderMergePayRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.OrderPayRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.EPayQueryReq;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.PayCheckRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.PrimaryOrderRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.epay.EPayCard;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.pay.OrderPayVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.pay.PayRenderVO;
import com.aliyun.gts.gmall.manager.front.trade.facade.EPayFacade;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.input.EPayTokenRpcReq;
import com.aliyun.gts.gmall.platform.open.customized.api.facade.EPaymentFacade;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.*;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.*;
import com.aliyun.gts.gmall.platform.trade.api.facade.pay.OrderPayReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.pay.OrderPayWriteFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.CreatingOrderParamConstants;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CreateCustomerCardInfoCommand;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerBankCardByAccountQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerBankCardInfoDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerBankCardInfoReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerBankCardInfoWriteFacade;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode.TRADE_CENTER_ERROR;
import static com.aliyun.gts.gmall.manager.front.trade.dto.utils.TradeFrontResponseCode.*;

/**
 * 订单支付操作
 *
 * @author tiansong
 */
@Service
@Slf4j
public class OrderPayAdapter {

    @Autowired
    private OrderPayReadFacade orderPayReadFacade;
    @Autowired
    private OrderPayWriteFacade orderPayWriteFacade;
    @Autowired
    private TradeResponseConvertor tradeResponseConvertor;
    @Autowired
    private TradeRequestConvertor tradeRequestConvertor;
    @Autowired
    private GmallFrontConfig gmallFrontConfig;
    @Autowired
    private DegradationConfig degradationConfig;
    @Autowired
    CustomerBankCardInfoWriteFacade customerBankCardInfoWriteFacade;
    @Autowired
    private EPayFacade ePayFacade;
    @Autowired
    private EPaymentFacade ePaymentFacade;
    @Autowired
    private CustomerBankCardInfoReadFacade customerBankCardInfoReadFacade;


    @Value("${front-manager.getbankcards:true}")
    private boolean getbankcards;

    DubboBuilder tradeBuilder = DubboBuilder.builder().logger(log).sysCode(TRADE_CENTER_ERROR).build();

    /**
     * 收银台渲染
     *
     * @param primaryOrderRestQuery 收银台请求
     * @return 收银台结果
     */
    public PayRenderVO payRender(PrimaryOrderRestQuery primaryOrderRestQuery) {
        return tradeBuilder.create().id(DsIdConst.trade_pay_render).queryFunc(
                (Function<PrimaryOrderRestQuery, RpcResponse<PayRenderVO>>) request -> {
                    PayRenderRpcReq payRenderRpcReq = tradeRequestConvertor.convertPayRender(request);
                    RpcResponse<PayRenderRpcResp> rpcResponse = orderPayReadFacade.payRender(payRenderRpcReq);
                    if (rpcResponse.isSuccess() && rpcResponse.getData() != null) {
                        return RpcResponse.ok(tradeResponseConvertor.convertPayRender(rpcResponse.getData()));
                    }
                    return RpcResponse.fail(rpcResponse.getFail());
                }).bizCode(PAY_RENDER_ERROR).query(primaryOrderRestQuery);
    }

    /**
     * 支付成功检查
     *
     * @param payCheckRestQuery 支付检查请求
     * @return 支付检查结果
     */
    public Boolean payCheck(PayCheckRestQuery payCheckRestQuery) {
        return tradeBuilder.create().id(DsIdConst.trade_pay_check).queryFunc(
                (Function<PayCheckRestQuery, RpcResponse<Boolean>>) request -> {
                    ConfirmPayCheckRpcReq confirmPayCheckRpcReq = tradeRequestConvertor.convertPayCheck(request);
                    RpcResponse<ConfirmPayCheckRpcResp> rpcResponse = orderPayReadFacade.confirmPay(
                            confirmPayCheckRpcReq);
                    if (rpcResponse.isSuccess() && rpcResponse.getData() != null) {
                        return RpcResponse.ok(rpcResponse.getData().getPaySuccess());
                    }
                    return RpcResponse.fail(rpcResponse.getFail());
                }).bizCode(PAY_CHECK_ERROR).query(payCheckRestQuery);
    }

    /**
     * 支付
     *
     * @param orderPayRestCommand 支付请求
     * @return 支付请求结果
     */
    public OrderPayVO toPay(OrderPayRestCommand orderPayRestCommand) {
        return tradeBuilder.create().id(DsIdConst.trade_pay_toPay).queryFunc(
                (Function<OrderPayRestCommand, RpcResponse<OrderPayVO>>) request -> {
                    OrderPayRpcReq orderPayRpcReq = tradeRequestConvertor.convertToPay(request);

                    if (StringUtils.isNotBlank(request.getAccountPeriod())) {
                        Map<String, String> extra = orderPayRpcReq.getExtra();
                        if (extra == null) {
                            extra = new HashMap<>();
                            orderPayRpcReq.setExtra(extra);
                        }
                        extra.put(CreatingOrderParamConstants.ACCOUNT_PERIOD_MEMO, request.getAccountPeriod());
                    }

                    RpcResponse<OrderPayRpcResp> rpcResponse = orderPayWriteFacade.toPay(orderPayRpcReq);
                    if (!rpcResponse.isSuccess()) {
                        return RpcResponse.fail(rpcResponse.getFail());
                    }
                    OrderPayVO orderPayVO = tradeResponseConvertor.convertToPay(rpcResponse.getData());
                    return RpcResponse.ok(orderPayVO);
                }).bizCode(TO_PAY_ERROR).query(orderPayRestCommand);
    }

    /**
     * 合并付款
     *
     * @param orderMergePayRestCommand 合并付款请求
     * @return 支付请求结果
     */
    public OrderPayVO toMergePay(OrderMergePayRestCommand orderMergePayRestCommand) {
        return tradeBuilder.create().id(DsIdConst.trade_pay_toMergePay).queryFunc(
                (Function<OrderMergePayRestCommand, RpcResponse<OrderPayVO>>) request -> {
                    OrderMergePayRpcReq orderMergePayRpcReq = tradeRequestConvertor.convertToMergePay(request);
                    RpcResponse<OrderMergePayRpcResp> rpcResponse = orderPayWriteFacade.toMergePay(orderMergePayRpcReq);
                    if (!rpcResponse.isSuccess()) {
                        return RpcResponse.fail(rpcResponse.getFail());
                    }
                    OrderPayVO orderPayVO = new OrderPayVO();
                    orderPayVO.setPayData(rpcResponse.getData().getPayData());
                    String firstOrderId = String.valueOf(orderMergePayRestCommand.getPrimaryOrderIds().get(0));
                    String firstFlowId = rpcResponse.getData().getMergePayFlowInfos().stream()
                            .filter(mergePayFlowInfo -> firstOrderId.equals(mergePayFlowInfo.getPrimaryOrderId()))
                            .findFirst().orElse(new MergePayFlowInfo()).getPayFlowId();
                    orderPayVO.setPayFlowId(firstFlowId);
                    orderPayVO.setCartId(rpcResponse.getData().getCartId());
                    return RpcResponse.ok(orderPayVO);
                }).bizCode(TO_PAY_ERROR).query(orderMergePayRestCommand);
    }


    public String payment(OrderPayRestCommand orderPayRestCommand, OrderPayVO orderPayVO) {
        EPayTokenRpcReq ePayTokenRpcReq = new EPayTokenRpcReq();
        ePayTokenRpcReq.setAccountId(orderPayRestCommand.getAccountId());
//        ePayTokenRpcReq.setInvoiceID(String.valueOf(orderPayVO.getPayFlowId()));
        ePayTokenRpcReq.setInvoiceID(String.valueOf(orderPayVO.getCartId()));
        ePayTokenRpcReq.setAmount(orderPayRestCommand.getRealPayFee());
        ePayTokenRpcReq.setCardId(orderPayRestCommand.getCardId());
        ePayTokenRpcReq.setScope("payment");
        ePayTokenRpcReq.setDescription(orderPayRestCommand.getDescription());
        ePayTokenRpcReq.setEmail(orderPayRestCommand.getEmail());
        ePayTokenRpcReq.setPhone(orderPayRestCommand.getPhone());
        //ePayTokenRpcReq.setLanguage("rus");
        ePayTokenRpcReq.setLanguage(orderPayRestCommand.getLang());
        ePayTokenRpcReq.setPaymentType("cardId");
        ePayTokenRpcReq.setCustId(orderPayRestCommand.getCustId().toString());
        ePayTokenRpcReq.setToken(orderPayVO.getPayToken());
        // 请求和支付结果要更新到支付流水上
        RpcResponse<String> rpcResponse = ePaymentFacade.payment(ePayTokenRpcReq);
        return rpcResponse.getData();
    }

    public  RpcResponse<Boolean> paymentV2(OrderPayRestCommand orderPayRestCommand, OrderPayVO orderPayVO) {
        OrderPayV2RpcReq orderPayRpcReq = tradeRequestConvertor.convertToPaymentV2(orderPayRestCommand);
        OrderPayRpcResp originalOrderPayRpcResp = tradeRequestConvertor.convertToOrderPayRpcResp(orderPayVO);
        return orderPayWriteFacade.paymentV2(orderPayRpcReq, originalOrderPayRpcResp);

    }

    public void addCard(AddCardRestCommand card) {
        this.saveCard(card);
    }

    /**
     * 获取用户卡信息
     * @param ePayQueryReq
     * @return
     */
    public List<JSONObject> getCardIds(EPayQueryReq ePayQueryReq) {
        // step1 查询epay的卡信息
        List<EPayCard> ePayCards = getEpayCards(ePayQueryReq);
        // step2 获取银行卡
        List<EPayCard> bankCards = getBankCards();
        // 查询我存起来的卡
        List<CustomerBankCardInfoDTO> localList = getUserAllTableCards(ePayQueryReq.getAccountId());
        //
        List<EPayCard> collect = localList.stream().map(c -> {
            EPayCard ePayCard = new EPayCard();
            ePayCard.setId(c.getCardId());
            ePayCard.setCardMask(c.getCardMask());
            ePayCard.setType(c.getType());
            ePayCard.setBankType(c.getBankCardType());
            ePayCard.setAccountID(c.getAccountId());
            return ePayCard;
        }).collect(Collectors.toList());
        bankCards.addAll(ePayCards);
        bankCards.addAll(collect);
        return bankCards.stream().collect(Collectors.toMap(EPayCard::getId, c -> c, (c1, c2) -> {
            if(StringUtils.isEmpty(c1.getBankType()) && !StringUtils.isEmpty(c2.getBankType())){
                c1.setBankType(c2.getBankType());
                c1.setType(c2.getType());
            }
            return c1;
        }))
        .values().stream()
        .sorted(new Comparator<EPayCard>() {
            @Override
            public int compare(EPayCard o1, EPayCard o2) {
                return o2.getCreatedDate().compareTo(o1.getCreatedDate());
            }
        }).map((card)->{
            JSONObject cardInfo = new JSONObject();
            cardInfo.put("cardId", card.getId());
            String cardMask = card.getCardMask();
            if(cardMask.endsWith(".")){
                cardMask = cardMask.substring(0, cardMask.lastIndexOf("."));
            }
            cardInfo.put("cardMask", cardMask);
            cardInfo.put("type", card.getType());
            cardInfo.put("bankType", card.getBankType());
            cardInfo.put("createdDate", card.getCreatedDate());
            cardInfo.put("accountId", card.getAccountID());
            return cardInfo;
        }).collect(Collectors.toList());
    }

    /**
     * 查询epay的卡信息
     * @param ePayQuery
     * @return
     */
    private List<EPayCard> getEpayCards(EPayQueryReq ePayQuery) {
        return ePayFacade.getEpayCards(ePayQuery);
    }

    /**
     * 获取银行所有的卡
     * @return
     */
    private List<EPayCard> getBankCards() {
        List<EPayCard> cardList = new ArrayList<>();
        if(!getbankcards) return cardList;
        CustDTO user = UserHolder.getUser();
        EPayTokenRpcReq ePayTokenRpcReq = new EPayTokenRpcReq();
        ePayTokenRpcReq.setToken(user.getCasLoginToken());
        RpcResponse<String> rpcResponse = ePaymentFacade.getBankCards(ePayTokenRpcReq);
        String data = rpcResponse.getData();
        log.info("getBankCards:"+data);
        if (org.apache.commons.lang.StringUtils.isNotEmpty(data)) {
            //没有返回 cards
            if(data.startsWith("{")){
                return cardList;
            }
            List<JSONObject> list = JSONObject.parseArray(data).toJavaList(JSONObject.class);
            for (JSONObject cardJson : list) {
                EPayCard card = new EPayCard();
                card.setId(cardJson.getString("ID"));
                card.setAccountID(cardJson.getString("AccountID"));
                card.setCardMask(cardJson.getString("CardMask"));
                card.setMerchantID(cardJson.getString("MerchantID"));
                card.setActive(cardJson.getBoolean("Active"));
                card.setType(cardJson.getString("Type"));
                cardList.add(card);
            }
        }
        return cardList;
    }

    // 获取已经保存的用户所有的 卡
    private List<CustomerBankCardInfoDTO> getUserAllTableCards(String accountId) {
        List<CustomerBankCardInfoDTO> list = new ArrayList<>();
        CustomerBankCardByAccountQuery customerBankCardByAccountQuery = new CustomerBankCardByAccountQuery();
        List<String> accountIdList = new ArrayList<>();
        accountIdList.add(accountId);
        customerBankCardByAccountQuery.setAccountIds(accountIdList);
        RpcResponse<List<CustomerBankCardInfoDTO>> response = customerBankCardInfoReadFacade.queryByAccountId(customerBankCardByAccountQuery);
        list = response.getData();
        return list;
    }


    public List<JSONObject> getUserCards(String accountId) {
        List<CustomerBankCardInfoDTO> list = getUserAllTableCards(accountId);
        return list.stream().sorted(Comparator.comparing(c-> !"HALYK BANK".equals(c.getBankCardType()))).map((customerBankCardInfoDTO)->{
            JSONObject cardInfo = new JSONObject();
            //cardInfo.put("id", customerBankCardInfoDTO.getId());
            cardInfo.put("cardId", customerBankCardInfoDTO.getCardId());
            //cardInfo.put("accountId", customerBankCardInfoDTO.getAccountId());
            //String cardMask = maskCharacter(customerBankCardInfoDTO);
            cardInfo.put("cardMask", customerBankCardInfoDTO.getCardMask());
            cardInfo.put("type", customerBankCardInfoDTO.getType());
            cardInfo.put("bankType", customerBankCardInfoDTO.getBankCardType());
            return cardInfo;
        }).collect(Collectors.toList());
    }



    public void saveCard(AddCardRestCommand card) {
        CreateCustomerCardInfoCommand createCustomerCardInfoCommand = new CreateCustomerCardInfoCommand();
        createCustomerCardInfoCommand.setAccountId(card.getAccountId());
//        createCustomerCardInfoCommand.setActive(card.getActive() ? trueValue : falseValue);
        createCustomerCardInfoCommand.setActive(1);
        createCustomerCardInfoCommand.setEpayCardId(card.getId());
        createCustomerCardInfoCommand.setCardId(card.getCardId());
        createCustomerCardInfoCommand.setCardMask(card.getCardMask());
        createCustomerCardInfoCommand.setCardName(card.getName());
//        createCustomerCardInfoCommand.setForeign(card.getForeign() ? trueValue : falseValue);
//        createCustomerCardInfoCommand.setMerchantId(card.getMerchantID());
//        createCustomerCardInfoCommand.setOpenwayId(card.getOpenwayID());
        createCustomerCardInfoCommand.setType(card.getCardType());
        createCustomerCardInfoCommand.setBankCardType(card.getIssuer());
        createCustomerCardInfoCommand.setCurrency(card.getCurrency());
//        createCustomerCardInfoCommand.setPayerName(card.getPayerName());
        createCustomerCardInfoCommand.setTerminal(card.getTerminal());
        createCustomerCardInfoCommand.setTransactionId(card.getTransactionId());
//        createCustomerCardInfoCommand.setToken(card.getToken());
        // 保存用户的卡
        customerBankCardInfoWriteFacade.create(createCustomerCardInfoCommand);
    }

    private String maskCharacter(CustomerBankCardInfoDTO customerBankCardInfoDTO) {
        String cardMask = customerBankCardInfoDTO.getCardMask();
        if (cardMask == null || cardMask.isEmpty()) {
            return cardMask;
        }
        cardMask = cardMask.substring(0, 4) + "..." + cardMask.substring(cardMask.length() - 4);
        return cardMask;
    }

    private String getBankName(String cardMask) {
        if (!cardMask.isEmpty()) {
            int num = Integer.parseInt(cardMask.substring(0, 6));
            if(num/100 == 5610 || (num >= 560221 && num <= 560225)){
                //return "bankcard";
            }
            if(num/1000 >= 300 && num/1000 <= 305){
                return "dalaika";
            }
            if(num/100 == 2014 || num/100 == 2149){
                //return "dalaika";
            }
            if(num/10000 == 36){
                return "dalaika";
            }
            if(num/10000 == 54 || num/10000 == 55){
                return "dalaika";
            }
            if(num/100 == 6011 || (num >= 622126 && num <= 622925) || (num/1000 >= 644 && num/1000 <= 649) ||num/10000 == 65){
                return "faxianka";
            }
            if(num/1000 >= 637 && num/1000 <= 639){
                return "insta";
            }
            if(num/100 >= 3528 && num/100 <= 3589){
                return "jcb";
            }
            if(num/100 == 6304 ||num/100 == 6706 ||num/100 == 6771 ||num/100 == 6709 ){
                //return "leisheka";
            }
            if(num/100 == 5018 ||num/100 == 5020 ||num/100 == 5038 ||num/100 == 6304 ||
                    num/100 == 6759 ||num/100 == 6761 ||num/100 == 6762 ||num/100 == 6763
            ){
                return "maestro";
            }
            if(num/10000 == 34||num/10000 == 37){
                return "meiguoyuntong";
            }
            if(num/100 == 6334||num/100 == 6767){
                //return "solo";
            }
            if(num/100 == 4903 ||num/100 == 4905 ||num/100 == 4911 ||num/100 == 4936 ||
                    num/100 == 6333 ||num/100 == 6759 ||num == 564182 ||num == 633110
            ){
                //return "switch";
            }
            if(num/100000 ==1){
                return "huanqiuhangkong";
            }
            if(num/100000 ==1){
                return "visa";
            }
            if(num/100 == 4026 ||num == 417500 ||num/100 == 4508 ||num/100 == 4844 ||
                    num/100 == 4913 ||num/100 == 4917
            ){
                return "visa";
            }
            if(num/10000 >= 51 && num/10000 <= 55){
                return "maestro";
            }
            if(num/10000 == 20 ||num/10000 == 31){
                return "zhongguojiaotong";
            }
            if(num/10000 == 62){
                return "zhongguoyinlian";
            }

        }
        return "unknown";
    }

    public Long getBonusesByUser() {
        Long bonuses = 0l;
        CustDTO user = UserHolder.getUser();
        EPayTokenRpcReq ePayTokenRpcReq = new EPayTokenRpcReq();
        ePayTokenRpcReq.setToken(user.getCasLoginToken());
        RpcResponse<String> rpcResponse = ePaymentFacade.getBonuses(ePayTokenRpcReq);
        String data = rpcResponse.getData();
        log.info("getBonuses:"+data);
        if (StringUtils.isNotEmpty(data)) {
            try{
                List<JSONObject> list = JSONObject.parseArray(data).toJavaList(JSONObject.class);
                if(CollectionUtils.isNotEmpty(list)) {
                    JSONObject jsonObject = list.get(0);
                    if(jsonObject.getJSONObject("status").getInteger("status") == 0){
                        bonuses = jsonObject.getJSONObject("bonusInfo")
                                .getJSONObject("clients")
                                .getJSONObject("client")
                                .getLong("amount");
                    }
                }
            }catch(Exception e){
                log.error("data parse error");
            }
        }
        return bonuses;
    }

    public Boolean removeCard(EPayQueryReq ePayQueryReqd) {
        Boolean b = false;
        CustDTO user = UserHolder.getUser();
        EPayTokenRpcReq ePayTokenRpcReq = new EPayTokenRpcReq();
        ePayTokenRpcReq.setToken(user.getCasLoginToken());
        ePayTokenRpcReq.setCardId(ePayQueryReqd.getId());
        RpcResponse<String> rpcResponse = ePaymentFacade.removeCard(ePayTokenRpcReq);
        String data = rpcResponse.getData();
        log.info("removeCard:" + data);
        if (StringUtils.isNotEmpty(data)) {
            try{
                if(JSONObject.parseObject(data).getInteger("code") == 2149){
                    b = true;
                }
            }catch(Exception e){
                log.error("data parse error");
            }
        }
        return b;
    }

    /**
     * 读取添加卡的配置信息
     * @return
     */
    public JSONObject addPay() {
        JSONObject jsonObject = new JSONObject();
        // 调用epay 参数
        RpcResponse<JSONObject> payInfo = ePaymentFacade.getPayInfo();
        if(payInfo.isSuccess()){
            jsonObject = payInfo.getData();
        }
        return jsonObject;
    }

    /**
     * 支付失败取消订单
     * @param primaryOrderIds
     * @return
     */
    public RpcResponse<Boolean> cancelOrders(List<Long> primaryOrderIds) {
        return orderPayWriteFacade.cancelOrders(primaryOrderIds);
    }
}