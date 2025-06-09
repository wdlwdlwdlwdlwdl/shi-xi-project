package com.aliyun.gts.gmall.manager.front.trade.controller;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.front.common.dto.CartItemQtyRestQuery;
import com.aliyun.gts.gmall.manager.front.common.dto.EmptyRestQuery;
import com.aliyun.gts.gmall.manager.front.common.dto.PageLoginRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.*;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.CalCartPriceRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.CartPayModeQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.CheckAddCartRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.cart.CartGroupVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.cart.CartModifyVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.cart.CartPriceVO;
import com.aliyun.gts.gmall.manager.front.trade.facade.CartFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * 购物车操作接口
 *
 * @author tiansong
 */
@Api(value = "购物车操作接口", tags = {"trade", "cart", "token"})
@RestController
public class CartController {

    @Autowired
    private CartFacade cartFacade;

    /**
     * 购物车列表
     * @param cartRestQuery
     * @return
     * 2024-12-3 21:30:58
     */
    @ApiOperation("购物车列表")
    @PostMapping(name = "queryCart", value = "/api/trade/cart/queryCart/token")
    public @ResponseBody
    RestResponse<PageInfo<CartGroupVO>> queryCart(@RequestBody QueryCartRestCommand cartRestQuery) {
        return RestResponse.okWithoutMsg(cartFacade.queryCart(cartRestQuery));
    }

    /**
     * 添加购物车
     * @param addCartRestCommand
     * @return
     * 2024-12-3 21:30:51
     */
    @ApiOperation("添加购物车")
    @PostMapping(name = "addCart", value = "/api/trade/cart/addCart/token")
    public @ResponseBody
    RestResponse<Integer> addCart(@RequestBody AddCartRestCommand addCartRestCommand) {
        return RestResponse.ok(cartFacade.addCart(addCartRestCommand));
    }

    /**
     * 批量添加购物车
     * @param batchAddCartRestCommand
     * @return
     */
    @ApiOperation("批量添加购物车")
    @PostMapping(name = "batchAddCart", value = "/api/trade/cart/batchAddCart/token")
    public @ResponseBody
    RestResponse<Integer> batchAddCart(@RequestBody BatchAddCartRestCommand batchAddCartRestCommand) {
        return RestResponse.ok(cartFacade.batchAddCart(batchAddCartRestCommand));
    }

    @ApiOperation("修改购物车商品")
    @PostMapping(name = "modifyCart", value = "/api/trade/cart/modifyCart/token")
    public @ResponseBody
    RestResponse<CartModifyVO> modifyCart(@RequestBody ModifyCartRestCommand modifyCartRestCommand) {
        return RestResponse.okWithoutMsg(cartFacade.modifyCart(modifyCartRestCommand));
    }

    @ApiOperation("修改购物车商品数量")
    @PostMapping(name = "modifyCartQty", value = "/api/trade/cart/modifyCartQty/token")
    public @ResponseBody
    RestResponse<CartModifyVO> modifyCartQty(@RequestBody @Validated ModifyCartItemQtyRestCommand command) {
        return RestResponse.okWithoutMsg(cartFacade.modifyCartItemQty(command));
    }

    @ApiOperation("删除购物车商品")
    @PostMapping(name = "deleteCart", value = "/api/trade/cart/deleteCart/token")
    public @ResponseBody
    RestResponse<Integer> deleteCart(@RequestBody DelCartRestCommand delCartRestCommand) {
        return RestResponse.ok(cartFacade.deleteCart(delCartRestCommand));
    }

    @ApiOperation("添加购物车按钮是否展示校验【未登录默认展示:true】")
    @PostMapping(name = "checkAddCart", value = "/api/trade/cart/checkAddCart")
    public @ResponseBody
    RestResponse<Boolean> checkAddCart(@RequestBody CheckAddCartRestQuery checkAddCartRestQuery) {
        return RestResponse.okWithoutMsg(cartFacade.checkAddCart(checkAddCartRestQuery));
    }

    @ApiOperation("查询购物车数量【未登录默认展示：0】")
    @PostMapping(name = "queryCartQty", value = "/api/trade/cart/queryCartQty")
    public @ResponseBody
    RestResponse<Integer> queryCartQty(@RequestBody EmptyRestQuery emptyRestQuery) {
        return RestResponse.okWithoutMsg(cartFacade.queryCartQty(emptyRestQuery));
    }

    @ApiOperation("查询购物车商品数量,包含每个购物车商品数量")
    @PostMapping(name = "queryCartItemQty", value = "/api/trade/cart/queryCartItemQty")
    public @ResponseBody
    RestResponse<Integer> queryCartItemQty(@RequestBody CartItemQtyRestQuery cartItemQtyRestQuery) {
        if (Objects.isNull(cartItemQtyRestQuery.getCustId()) || cartItemQtyRestQuery.getCustId() <= 0L) {
            return RestResponse.okWithoutMsg(0);
        }
        return RestResponse.okWithoutMsg(cartFacade.queryCartItemQty(cartItemQtyRestQuery));
    }


    @ApiOperation("购物车计算价格")
    @PostMapping(name = "calCartPrice", value = "/api/trade/cart/calCartPrice/token")
    public @ResponseBody
    RestResponse<List<CartGroupVO>> calCartPrice(@RequestBody CalCartPriceRestQuery calCartPriceRestQuery) {
        return RestResponse.okWithoutMsg(cartFacade.calCartPrice(calCartPriceRestQuery));
    }

    /**
     * 查询支付方式下面的全部商品信息
     *    可能返回数组
     *    正常的一个数组
     *    失效的一个数组
     * @param cartRestQuery
     * @return
     * 2024-12-3 21:30:58
     */
    @ApiOperation("购物车列表")
    @PostMapping(name = "queryCartPayMode", value = "/api/trade/cart/queryCartPayMode/token")
    public @ResponseBody
    RestResponse<List<CartGroupVO>> queryCartPayMode(@RequestBody CartPayModeQuery cartRestQuery) {
        if (Objects.isNull(cartRestQuery.getCustId()) || cartRestQuery.getCustId() <= 0L) {
            return RestResponse.okWithoutMsg(null);
        }
        return RestResponse.okWithoutMsg(cartFacade.queryCartPayMode(cartRestQuery));
    }
}