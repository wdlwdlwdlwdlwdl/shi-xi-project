package com.aliyun.gts.gmall.manager.front.trade.adaptor;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.framework.common.DubboBuilder;
import com.aliyun.gts.gmall.manager.front.common.config.DatasourceConfig;
import com.aliyun.gts.gmall.manager.front.common.consts.DsIdConst;
import com.aliyun.gts.gmall.manager.front.common.dto.PageLoginRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.convertor.TradeRequestConvertor;
import com.aliyun.gts.gmall.manager.front.trade.convertor.TradeResponseConvertor;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.*;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.CalCartPriceRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.CheckAddCartRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.cart.CartGroupVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.cart.CartModifyVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.cart.CartPriceVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.utils.TradeFrontResponseCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.CalCartPriceRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.CartPayQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.CartQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.QueryCartItemQuantityRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.calc.CartPriceDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.modify.CartModifyResultDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.query.CartDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.query.CartItemDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.cart.CartReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.cart.CartWriteFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

import static com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode.TRADE_CENTER_ERROR;
import static com.aliyun.gts.gmall.manager.front.trade.dto.utils.TradeFrontResponseCode.*;

/**
 * 购物车依赖
 * @author tiansong
 */
@Service
@Slf4j
public class CartAdapter {
    @Autowired
    private CartReadFacade         cartReadFacade;
    @Autowired
    private CartWriteFacade        cartWriteFacade;
    @Autowired
    private TradeRequestConvertor  tradeRequestConvertor;
    @Autowired
    private TradeResponseConvertor tradeResponseConvertor;
    @Autowired
    private DatasourceConfig       datasourceConfig;

    /**
     * 购物车分页加载，默认
     */
    private static final Integer DEFAULT_CART_SIZE = 0;

    DubboBuilder cartBuilder = DubboBuilder.builder().logger(log).sysCode(TRADE_CENTER_ERROR).build();

    /**
     * 添加购物车【强依赖】
     * @param addCartRestCommand 添加购物车请求
     */
    public void addCart(AddCartRestCommand addCartRestCommand) {
        cartBuilder.create()
            .id(DsIdConst.trade_cart_add)
            .queryFunc((Function<AddCartRestCommand, RpcResponse>) request ->
                cartWriteFacade.addCart(tradeRequestConvertor.convertAddCart(request))
            )
            .bizCode(CART_ADD_FAILED)
            .query(addCartRestCommand);
    }

    /**
     * 修改购物车商品【强依赖】
     *
     * @param modifyCartRestCommand 修改购物车请求
     */
    public CartModifyVO modifyCart(ModifyCartRestCommand modifyCartRestCommand) {
        CartModifyResultDTO modify = cartBuilder
            .create()
            .id(DsIdConst.trade_cart_modify)
            .queryFunc((Function<ModifyCartRestCommand, RpcResponse<CartModifyResultDTO>>) request ->
                    cartWriteFacade.modifyCart(tradeRequestConvertor.convertModifyCart(request))
            )
            .bizCode(CART_MODIFY_FAILED)
            .query(modifyCartRestCommand);

        // 反查一次, 用于阶梯价变化等
        CartItemDTO item = cartBuilder
            .create()
            .id(DsIdConst.trade_cart_singleQuery)
            .queryFunc((Function<ModifyCartRestCommand, RpcResponse<CartItemDTO>>) request ->
                cartReadFacade.querySingleItem(tradeRequestConvertor.convertSingleQuery(request)))
            .bizCode(CART_SINGLE_QUERY_FAILED)
            .query(modifyCartRestCommand);

        CartModifyVO result = new CartModifyVO();
        result.setSkuMerged(modify.isSkuMerged());
        result.setCartItem(tradeResponseConvertor.convertCartItemVO(item));
        return result;
    }

    /**
     * 修改购物车商品数量
     *
     * @param command 修改购物车请求
     */
    public CartModifyVO modifyCartItemQty(ModifyCartItemQtyRestCommand command) {
        // 修改购物车数量 并计算新的独立营销价格
        CartModifyResultDTO modify = cartBuilder
            .create().id(DsIdConst.trade_cart_modify)
            .queryFunc((Function<ModifyCartItemQtyRestCommand, RpcResponse<CartModifyResultDTO>>) request ->
                cartWriteFacade.modifyCart(tradeRequestConvertor.convertModifyQtyCart(request)))
            .bizCode(CART_MODIFY_FAILED)
            .query(command);

        // 反查一次, 用于阶梯价变化等
        //CartItemDTO item = cartBuilder
        //    .create().id(DsIdConst.trade_cart_singleQuery)
        //    .queryFunc((Function<ModifyCartItemQtyRestCommand, RpcResponse<CartItemDTO>>) request ->
        //        cartReadFacade.querySingleItem(tradeRequestConvertor.convertSingleQuery(request)))
        //    .bizCode(CART_SINGLE_QUERY_FAILED).query(command);

        // 根据支付方式查询支付卡片下面的所有支付信息
        //CartPayQueryRpcReq cartQueryRpcReq = new CartPayQueryRpcReq();
        //cartQueryRpcReq.setCustId(command.getCustId());
        //cartQueryRpcReq.setPayMode(command.getPayMode());
        //cartQueryRpcReq.setCityCode(command.getCityCode());
        //CartDTO cartDTO = cartBuilder.create()
        //    .id(DsIdConst.trade_cart_query_payMode)
        //    .queryFunc((Function<CartPayQueryRpcReq, RpcResponse<CartDTO>>) request -> cartReadFacade.queryCarPayMode(cartQueryRpcReq))
        //    .bizCode(CART_SINGLE_QUERY_FAILED)
        //    .query(cartQueryRpcReq);

        // 返回结果
        CartModifyVO result = new CartModifyVO();
        result.setSkuMerged(modify.isSkuMerged());
        // result.setCartItem(tradeResponseConvertor.convertCartItemVO(cartDTO));
        // result.setCartGroupVOS(tradeResponseConvertor.convertCart(cartDTO.getGroups()));
        return result;
    }

    /**
     * 删除购物车商品【强依赖】
     *
     * @param delCartRestCommand 删除购物车请求
     */
    public void deleteCart(DelCartRestCommand delCartRestCommand) {
        cartBuilder.create()
            .id(DsIdConst.trade_cart_delete)
            .queryFunc((Function<DelCartRestCommand, RpcResponse>) request ->
                cartWriteFacade.deleteCart(tradeRequestConvertor.convertDelCart(request))
            )
            .bizCode(CART_DEL_FAILED)
            .query(delCartRestCommand);
    }

    /**
     * 购物车列表查询【强依赖】
     *
     * @param cartRestQuery 购物车列表查询
     * @return 购物车列表
     */
    public PageInfo<CartGroupVO> queryCart(QueryCartRestCommand cartRestQuery) {
        CartDTO cartDTO = cartBuilder.create()
            .id(DsIdConst.trade_cart_queryList)
            .queryFunc((Function<QueryCartRestCommand, RpcResponse<CartDTO>>) request ->
                cartReadFacade.queryCart(tradeRequestConvertor.convertCartList(request))
            )
            .bizCode(TradeFrontResponseCode.CART_QUERY_FAILED)
            .query(cartRestQuery);
        return new PageInfo(cartDTO.getTotalItemCount().longValue(), tradeResponseConvertor.convertCart(cartDTO.getGroups()));
    }

    /**
     * 添加购物车按钮是否展示校验【弱依赖，默认展示:true】
     *
     * @param checkAddCartRestQuery 添加购物车检查请求
     * @return 是否通过
     */
    public Boolean checkAddCart(CheckAddCartRestQuery checkAddCartRestQuery) {
        return cartBuilder.create(datasourceConfig)
            .id(DsIdConst.trade_cart_checkAdd)
            .queryFunc((Function<CheckAddCartRestQuery, RpcResponse>) request ->
                cartReadFacade.checkAddCart(tradeRequestConvertor.convertCheckAddCart(request))
            )
            .strong(Boolean.FALSE)
            .query(checkAddCartRestQuery);
    }

    /**
     * 查询购物车商品数量【弱依赖】
     *
     * @param custId 登录用户ID
     * @return 购物车商品数量
     */
    public Integer queryCartQty(Long custId) {
        QueryCartItemQuantityRpcReq queryCartItemQuantityRpcReq = new QueryCartItemQuantityRpcReq();
        queryCartItemQuantityRpcReq.setCustId(custId);
        return cartBuilder.create(datasourceConfig)
            .id(DsIdConst.trade_cart_queryQty)
            .queryFunc((Function<QueryCartItemQuantityRpcReq, RpcResponse<Integer>>) request -> {
                RpcResponse<Integer> rpcResponse = cartReadFacade.queryCartItemQuantity(request);
                if (rpcResponse.isSuccess() && rpcResponse.getData() != null) {
                    return rpcResponse;
                }
                return RpcResponse.ok(DEFAULT_CART_SIZE);
            })
            .strong(Boolean.FALSE)
            .query(queryCartItemQuantityRpcReq);
    }


    /**
     * 查询购物车商品数量【弱依赖】
     *
     * @param custId 登录用户ID
     * @return 购物车商品数量
     */
    public Integer queryCartItemQty(Long custId, Integer cartType) {
        QueryCartItemQuantityRpcReq queryCartItemQuantityRpcReq = new QueryCartItemQuantityRpcReq();
        queryCartItemQuantityRpcReq.setCustId(custId);
        queryCartItemQuantityRpcReq.setCartType(cartType);
        return cartBuilder.create(datasourceConfig)
            .id(DsIdConst.trade_cart_queryQty)
            .queryFunc((Function<QueryCartItemQuantityRpcReq, RpcResponse<Integer>>) request -> {
                RpcResponse<Integer> rpcResponse = cartReadFacade.queryCartItemV2Quantity(request);
                if (rpcResponse.isSuccess() && rpcResponse.getData() != null) {
                    return rpcResponse;
                }
                return RpcResponse.ok(DEFAULT_CART_SIZE);
            })
            .strong(Boolean.FALSE)
            .query(queryCartItemQuantityRpcReq);
    }

    /**
     * 购物车计算价格【强依赖】
     *
     * @param calCartPriceRestQuery 优惠计算请求
     * @return 优惠价格
     */
    public List<CartGroupVO> calCartPrice(CalCartPriceRestQuery calCartPriceRestQuery) {
         CartDTO cartDTO = cartBuilder.create()
            .id(DsIdConst.trade_cart_calPrice)
            .queryFunc((Function<CalCartPriceRestQuery, RpcResponse<CartDTO>>) request ->
                cartReadFacade.calculateCartPrice(tradeRequestConvertor.convertCartPrice(request))
            )
            .bizCode(CART_CAL_PRICE_FAILED)
            .query(calCartPriceRestQuery);
        return tradeResponseConvertor.convertCart(cartDTO.getGroups());
    }


    /**
     * 查询购物车商品数量【弱依赖】
     * @param custId 登录用户ID
     * @return 购物车商品数量
     */
    public List<CartGroupVO> queryCartPayMode(Long custId, String cityCode, String payMode) {
        CartPayQueryRpcReq cartQueryRpcReq = new CartPayQueryRpcReq();
        cartQueryRpcReq.setCustId(custId);
        cartQueryRpcReq.setPayMode(payMode);
        cartQueryRpcReq.setCityCode(cityCode);
        CartDTO cartDTO = cartBuilder.create(datasourceConfig)
            .id(DsIdConst.trade_cart_query_payMode)
            .queryFunc((Function<CartPayQueryRpcReq, RpcResponse<CartDTO>>) request -> cartReadFacade.queryCarPayMode(request))
            .strong(Boolean.FALSE)
            .query(cartQueryRpcReq);
        return tradeResponseConvertor.convertCart(cartDTO.getGroups());
    }
}