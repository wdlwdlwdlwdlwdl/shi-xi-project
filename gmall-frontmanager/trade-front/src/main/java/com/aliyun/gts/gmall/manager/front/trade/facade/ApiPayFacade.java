package com.aliyun.gts.gmall.manager.front.trade.facade;

import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.AddBankCardCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.OrderPayRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.BindedCardListQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.pay.OrderPayVO;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.output.epay.EpayCardInfoDto;

import java.util.List;

/**
 * api接口支付
 *
 * @author tiansong
 */
public interface ApiPayFacade {

    boolean payment(OrderPayRestCommand req,OrderPayVO vo);

    boolean addCard(AddBankCardCommand req);

    List<EpayCardInfoDto> listUserBindCard(BindedCardListQuery req);
}
