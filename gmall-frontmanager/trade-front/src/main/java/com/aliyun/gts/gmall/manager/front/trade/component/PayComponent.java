package com.aliyun.gts.gmall.manager.front.trade.component;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.BuyerConfirmPayRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.PayQueryReq;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.pay.PaySearchVO;

public interface PayComponent {

    PageInfo<PaySearchVO> query(PayQueryReq req);

    void buyerConfirmPay(BuyerConfirmPayRestCommand req);

}
