package com.aliyun.gts.gmall.platform.trade.server.flow.context;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractRpcRequest;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.base.AbstractContextEntity;

public class TCart<REQ extends AbstractRpcRequest, DTO> extends AbstractContextEntity<REQ, Cart, DTO> {
}
