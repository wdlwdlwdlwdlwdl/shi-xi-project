package com.aliyun.gts.gmall.manager.front.trade.adaptor;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.framework.common.DubboBuilder;
import com.aliyun.gts.gmall.manager.front.common.consts.DsIdConst;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.trade.convertor.ApiPayConvertor;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.OrderPayRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.BindedCardListQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.pay.OrderPayVO;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.input.epay.EpayCardRpcReq;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.input.epay.EpayPaymentRpcReq;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.output.epay.EpayCardInfoDto;
import com.aliyun.gts.gmall.platform.open.customized.api.facade.ApiPayFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

import static com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode.TRADE_CENTER_ERROR;
import static com.aliyun.gts.gmall.manager.front.trade.dto.utils.TradeFrontResponseCode.*;

/**
 * @author liang.ww
 * @date 2024/11/6
 */
@Slf4j
@Service
public class ApiPayAdapter {

    @Autowired
    private ApiPayFacade apiPayFacade;

    DubboBuilder apiBuilder = DubboBuilder.builder().sysCode(TRADE_CENTER_ERROR).logger(log).build();

    public boolean apiPayment(OrderPayRestCommand cmd, OrderPayVO vo) {
        apiBuilder.create().id(DsIdConst.api_pay).queryFunc(
                (Function<OrderPayRestCommand, RpcResponse>) request -> {
                    EpayPaymentRpcReq req = new EpayPaymentRpcReq();
                    req.setInvoiceId(String.valueOf(request.getInvoiceId()));
                    req.setAmount(request.getRealPayFee().toString());
                    req.setAccountId(String.valueOf(request.getAccountId()));
                    req.setDescription("payment");
                    req.setPhone(UserHolder.getUser().getPhone());
                    req.setPayChannel(request.getPayChannel());
                    req.setCardId(request.getCardId());
                    req.setCardPersonName(request.getCardPersonName());
                    req.setPayment(request.getPayment());
                    return apiPayFacade.payment(req);
                }).bizCode(TO_PAY_ERROR).query(cmd);
        return true;
    }

    public List<EpayCardInfoDto> listUserBindCard(BindedCardListQuery query){
        return apiBuilder.create().id(DsIdConst.api_card_query).queryFunc(
                (Function<BindedCardListQuery, RpcResponse<List<EpayCardInfoDto>>>) request ->
                        apiPayFacade.listUserBindCard(ApiPayConvertor.INSTANCE.toRpc(request))
                ).bizCode(QUERY_FAIL).query(query);
    }

    public boolean addCard(EpayCardRpcReq req){
        RpcResponse<Boolean> rpcResponse =apiPayFacade.addCard(req);
        return rpcResponse.getData();
    }





}
