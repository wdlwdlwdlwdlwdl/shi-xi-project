package com.aliyun.gts.gmall.platform.trade.server.flow.context;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.CalCartPriceRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.calc.CartPriceDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.query.CartDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.base.AbstractContextEntity;

public class TCartCalc extends AbstractContextEntity<CalCartPriceRpcReq, Cart, CartDTO> {

}
