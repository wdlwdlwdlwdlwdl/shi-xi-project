package com.aliyun.gts.gmall.platform.trade.api.facade.cart;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.CartPayQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.*;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.query.CartDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.query.CartItemDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("购物车读接口")
public interface CartReadFacade {

    @ApiOperation("购物车查看接口")
    RpcResponse<CartDTO> queryCart(CartQueryRpcReq req);

    @ApiOperation("购物车单商品查询接口")
    RpcResponse<CartItemDTO> querySingleItem(CartSingleQueryRpcReq req);

    @ApiOperation("购物车费用计算接口")
    RpcResponse<CartDTO> calculateCartPrice(CalCartPriceRpcReq req);

    @ApiOperation("可加购商品校验接口")
    RpcResponse checkAddCart(CheckAddCartRpcReq req);

    @ApiOperation("购物车商品数量查询接口")
    RpcResponse<Integer> queryCartItemQuantity(QueryCartItemQuantityRpcReq req);

    @ApiOperation("购物车商品数量V2查询接口,商品数量")
    RpcResponse<Integer> queryCartItemV2Quantity(QueryCartItemQuantityRpcReq req);

    @ApiOperation("购物车查看接口--根据支付方式")
    RpcResponse<CartDTO> queryCarPayMode(CartPayQueryRpcReq req);

}
