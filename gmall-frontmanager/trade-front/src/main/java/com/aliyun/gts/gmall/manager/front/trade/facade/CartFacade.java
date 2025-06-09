package com.aliyun.gts.gmall.manager.front.trade.facade;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
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

import java.util.List;

/**
 * 购物车相关操作
 *
 * @author tiansong
 */
public interface CartFacade {
    /**
     * 添加购物车
     *
     * @param addCartRestCommand
     * @return
     */
    Integer addCart(AddCartRestCommand addCartRestCommand);

    /**
     * 批量添加购物车
     *
     * @param batchAddCartRestCommand
     * @return
     */
    Integer batchAddCart(BatchAddCartRestCommand batchAddCartRestCommand);

    /**
     * 修改购物车商品
     *
     * @param modifyCartRestCommand 修改购物车请求
     * @return
     */
    CartModifyVO modifyCart(ModifyCartRestCommand modifyCartRestCommand);

    /**
     * 修改购物车中商品数量
     *
     * @param modifyCartItemQtyRestCommand
     * @return
     */
    CartModifyVO modifyCartItemQty(ModifyCartItemQtyRestCommand modifyCartItemQtyRestCommand);

    /**
     * 删除购物车商品
     *
     * @param delCartRestCommand
     * @return
     */
    Integer deleteCart(DelCartRestCommand delCartRestCommand);

    /**
     * 购物车列表查询
     *
     * @param cartRestQuery
     * @return
     */
    PageInfo<CartGroupVO> queryCart(QueryCartRestCommand cartRestQuery);

    /**
     * 添加购物车按钮是否展示校验【默认展示:true】
     *
     * @param checkAddCartRestQuery
     * @return
     */
    Boolean checkAddCart(CheckAddCartRestQuery checkAddCartRestQuery);

    /**
     * 查询购物车商品数量【弱依赖】
     *
     * @param emptyRestQuery
     * @return
     */
    Integer queryCartQty(EmptyRestQuery emptyRestQuery);

    /**
     * 购物车计算价格【强依赖】
     *
     * @param calCartPriceRestQuery
     * @return
     */
    List<CartGroupVO> calCartPrice(CalCartPriceRestQuery calCartPriceRestQuery);

    /**
     * 查询购物车商品数量【弱依赖】
     *
     * @param emptyRestQuery
     * @return
     */
    Integer queryCartItemQty(CartItemQtyRestQuery emptyRestQuery);

    /**
     * 查询支付方式下面的全部商品信息
     * @param cartPayModeQuery
     * @return
     */
    List<CartGroupVO> queryCartPayMode(CartPayModeQuery cartPayModeQuery);

}
