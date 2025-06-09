package com.aliyun.gts.gmall.platform.trade.server.flow.context;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.ModifyCartRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.modify.CartModifyResultDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.CartItem;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.base.AbstractContextEntity;

public class TCartModify extends AbstractContextEntity<ModifyCartRpcReq, CartItem, CartModifyResultDTO> {
}
