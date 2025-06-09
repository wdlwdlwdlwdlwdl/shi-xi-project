package com.aliyun.gts.gmall.platform.trade.api.facade.cart;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.AddCartRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.ClearCartRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.DeleteCartRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.ModifyCartRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.modify.CartModifyResultDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("购物车写接口")
public interface CartWriteFacade {

    @ApiOperation("添加购物车接口")
    RpcResponse addCart(AddCartRpcReq req);

    @ApiOperation("修改购物车接口")
    RpcResponse<CartModifyResultDTO> modifyCart(ModifyCartRpcReq req);

    @ApiOperation("删除购物车接口")
    RpcResponse deleteCart(DeleteCartRpcReq req);

    @ApiOperation("清空购物车")
    RpcResponse clearCart(ClearCartRpcReq req);
}
