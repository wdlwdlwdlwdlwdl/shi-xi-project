package com.aliyun.gts.gmall.manager.front.trade.facade.impl;

import com.aliyun.gts.gmall.manager.front.trade.adaptor.ApiPayAdapter;
import com.aliyun.gts.gmall.manager.front.trade.convertor.ApiPayConvertor;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.AddBankCardCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.OrderPayRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.BindedCardListQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.pay.OrderPayVO;
import com.aliyun.gts.gmall.manager.front.trade.facade.ApiPayFacade;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.output.epay.EpayCardInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * api接口支付
 * @author liang.ww(305643)
 * @date 2024/11/6 17:22
 */
@Service
@Slf4j
public class ApiPayFacadeImpl implements ApiPayFacade {
    @Autowired
    private ApiPayAdapter apiPayAdapter;
    @Override
    public boolean payment(OrderPayRestCommand req, OrderPayVO vo) {
            return apiPayAdapter.apiPayment(req, vo);
        }

    @Override
    public boolean addCard(AddBankCardCommand req) {
        return apiPayAdapter.addCard(ApiPayConvertor.INSTANCE.toRpc(req));
    }


    @Override
    public List<EpayCardInfoDto> listUserBindCard(BindedCardListQuery req) {
        return apiPayAdapter.listUserBindCard(req);
    }



}
