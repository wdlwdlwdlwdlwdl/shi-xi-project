package com.aliyun.gts.gmall.manager.front.trade.facade.impl;

import com.aliyun.gts.gmall.center.item.common.enums.ItemType;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.common.dto.CartItemQtyRestQuery;
import com.aliyun.gts.gmall.manager.front.common.dto.EmptyRestQuery;
import com.aliyun.gts.gmall.manager.front.common.exception.FrontManagerException;
import com.aliyun.gts.gmall.manager.front.common.util.FrontUtils;
import com.aliyun.gts.gmall.manager.front.trade.adaptor.CartAdapter;
import com.aliyun.gts.gmall.manager.front.trade.adaptor.TradeAdapter;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.*;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.CalCartPriceRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.CartPayModeQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.CheckAddCartRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.cart.CartGroupVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.cart.CartModifyVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.cart.CartPriceVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.item.ItemPriceVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.PromotionOptionVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.PromotionPriceReductionVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.SellerPriceVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.SellerSkuPriceVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.utils.TradeFrontResponseCode;
import com.aliyun.gts.gmall.manager.front.trade.facade.CartFacade;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.ItemDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.CartQueryRpcReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.collections.Sets;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 购物车操作
 *
 * @author tiansong
 */
@Slf4j
@Service
public class CartFacadeImpl implements CartFacade {

    @Autowired
    private CartAdapter  cartAdapter;

    @Autowired
    private TradeAdapter tradeAdapter;
    
    @Override
    public Integer addCart(AddCartRestCommand addCartRestCommand) {
        // 校验商品是否可以加购
        this.checkItemForSubmitCart(addCartRestCommand.getItemId(), addCartRestCommand.getSkuId());
        // 加购物车
        cartAdapter.addCart(addCartRestCommand);
        // 查询我的购物车
        //cartAdapter.queryCartQty(addCartRestCommand.getCustId());
        return 0;
    }

    /**
     * 批量加车
     * @param batchAddCartRestCommand
     * @return
     */
    @Override
    public Integer batchAddCart(BatchAddCartRestCommand batchAddCartRestCommand) {
        Set<Long> dup = new HashSet<>();
        // 逐个处理
        for (AddCartRestCommand add : batchAddCartRestCommand.getAddList()) {
            // 校验商品是否可以加购
            if (dup.add(add.getItemId())) {
                this.checkItemForSubmitCart(add.getItemId(), add.getSkuId());
            }
            // 加购物车
            cartAdapter.addCart(add);
        }
        return cartAdapter.queryCartQty(batchAddCartRestCommand.getCustId());
    }

    /**
     * 商品&SKU信息校验
     * @param itemId
     * @param skuId
     * 2025-1-6 10:42:11
     */
    private void checkItemForSubmitCart(Long itemId, Long skuId) {
        // 查询商品信息
        List<ItemDTO> itemDTOList = tradeAdapter.queryItemDTOByIds(Sets.newHashSet(Arrays.asList(itemId)));
        // 商品校验
        if (CollectionUtils.isEmpty(itemDTOList)) {
            throw new FrontManagerException(TradeFrontResponseCode.TRADE_ITEM_NOT_EXIST);
        }
        ItemDTO itemDTO = itemDTOList.get(0);
        if (Objects.isNull(itemDTO)) {
            throw new FrontManagerException(TradeFrontResponseCode.TRADE_ITEM_NOT_EXIST);
        }
        // 校验商品类型 -- 虚拟商品不能加车
        if (ItemType.EVOUCHER.getType().equals(itemDTO.getItemType())) {
            throw new FrontManagerException(TradeFrontResponseCode.CART_CHECK_ADD_FAILED);
        }
        // 校验SKU
        if (CollectionUtils.isEmpty(itemDTO.getSkuList())) {
            throw new FrontManagerException(TradeFrontResponseCode.CART_CHECK_ADD_FAILED);
        }
        // SKU 必须存在
        SkuDTO skuCheck = itemDTO.getSkuList()
            .stream()
            .filter(skuDTO -> skuDTO != null && skuId == skuDTO.getId())
            .findFirst()
            .orElse(null);
        if (Objects.isNull(skuCheck)) {
//            throw new FrontManagerException(TradeFrontResponseCode.CART_CHECK_ADD_FAILED);
        }
    }

    @Override
    public CartModifyVO modifyCart(ModifyCartRestCommand modifyCartRestCommand) {
        return cartAdapter.modifyCart(modifyCartRestCommand);
    }

    @Override
    public CartModifyVO modifyCartItemQty(ModifyCartItemQtyRestCommand command) {
        // 参数校验
        checkItemForSubmitCart(command.getItemId(), command.getSkuId());
        // 修改sku
        return cartAdapter.modifyCartItemQty(command);
    }

    @Override
    public Integer deleteCart(DelCartRestCommand delCartRestCommand) {
        cartAdapter.deleteCart(delCartRestCommand);
        return cartAdapter.queryCartQty(delCartRestCommand.getCustId());
    }

    /**
     * 购物车列表查询
     * @param cartRestQuery
     * @return
     */
    @Override
    public PageInfo<CartGroupVO> queryCart(QueryCartRestCommand cartRestQuery) {
        // 1. 获取购物车数据
        PageInfo<CartGroupVO> cartPageInfo = cartAdapter.queryCart(cartRestQuery);
        return cartPageInfo;
    }

    @Override
    public Boolean checkAddCart(CheckAddCartRestQuery checkAddCartRestQuery) {
        // 未登录用户，默认展示加购物车按钮
        if (checkAddCartRestQuery.getCustId() <= 0L) {
            return Boolean.TRUE;
        }
        return cartAdapter.checkAddCart(checkAddCartRestQuery);
    }

    @Override
    public Integer queryCartQty(EmptyRestQuery emptyRestQuery) {
        long custId = FrontUtils.getLoginUserId(emptyRestQuery);
        if (custId <= 0L) {
            return 0;
        }
        return cartAdapter.queryCartQty(custId);
    }

    /**
     * 计算选中商品的金额
     * @param calCartPriceRestQuery
     * @return
     */
    @Override
    public List<CartGroupVO> calCartPrice(CalCartPriceRestQuery calCartPriceRestQuery) {
        // 计算选中商品的金额
        return cartAdapter.calCartPrice(calCartPriceRestQuery);
        //计算定金尾款或者预售商品的金额 （定金尾款或者预售商品 不加购物车 所以这里没用）
        //cartPriceVO.setFirstPayPrice(calcSumFirstPayPrice(cartPriceVO));
        //计算门店优惠信息
        //cartPriceVO.setSellerReducePrices(buildSellerPromotions(cartPriceVO.getSellers()));
        //计算跨店优惠信息
        //cartPriceVO.setCrossSellerReducePrices(buildCrossSellerPromitions(cartPriceVO.getOptions()));
        //计算优惠总金额
        //cartPriceVO.setTotalReducePrice(getTotalReducePrice(cartPriceVO.getSellerReducePrices(), cartPriceVO.getCrossSellerReducePrices()));
        // 计算数据
        //cartPriceVO.setSellerSkus(calcSellerSkuPrice(cartPriceVO));
        //return cartPriceVO;
    }

    @Override
    public Integer queryCartItemQty(CartItemQtyRestQuery cartItemQtyRestQuery) {
        return cartAdapter.queryCartItemQty(cartItemQtyRestQuery.getCustId(), cartItemQtyRestQuery.getCartType());
    }

    /**
     * 查询支付方式下面的全部商品信息
     * @param cartPayModeQuery
     * @return List<CartGroupVO>
     * 2025-2-14 18:12:57
     */
    @Override
    public List<CartGroupVO> queryCartPayMode(CartPayModeQuery cartPayModeQuery) {
        return cartAdapter.queryCartPayMode(
            cartPayModeQuery.getCustId(),
            cartPayModeQuery.getCityCode(),
            cartPayModeQuery.getPayMode()
        );
    }

    /**
     * 计算定金尾款或者预售商品的金额 （定金尾款或者预售商品 不加购物车 所以这里没用）
     * @param cart
     * @return
     */
    private Long calcSumFirstPayPrice(CartPriceVO cart) {
        long sum = 0L;
        if (cart.getSellers() == null) {
            return sum;
        }
        for (SellerPriceVO slr : cart.getSellers()) {
            if (slr.getItems() == null) {
                continue;
            }
            for (ItemPriceVO itm : slr.getItems()) {
                if (itm.getFirstPayPrice() != null) {
                    sum += itm.getFirstPayPrice();
                } else if (itm.getPromotionPrice() != null) {
                    sum += itm.getPromotionPrice();
                }
            }
        }
        return sum;
    }

    /**
     * 计算优惠总金额
     * @param promotionPriceReductionVOs
     * @return
     */
    private Long getTotalReducePrice(List<PromotionPriceReductionVO>... promotionPriceReductionVOs) {
        List<Long> reducePrices = Stream.of(promotionPriceReductionVOs)
            .flatMap(Collection::stream).map(PromotionPriceReductionVO::getReduceFee)
            .filter(x -> Objects.nonNull(x))
            .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(reducePrices)) {
            return 0L;
        }
        return reducePrices.stream().reduce((aLong, aLong2) -> aLong + aLong2).get();
    }

    /**
     * 计算卖家优惠信息
     * @param sellerPriceVOS
     * @return
     */
    private List<PromotionPriceReductionVO> buildSellerPromotions(List<SellerPriceVO> sellerPriceVOS) {
        List<PromotionOptionVO> promotionOptionVOS = sellerPriceVOS.stream()
            .flatMap(sellerPriceVO -> sellerPriceVO.getOptions().stream())
            .collect(Collectors.toList());
        return buildPriceReductions(promotionOptionVOS);
    }

    /**
     * 计算跨店优惠信息
     * @param promotionOptionVOS
     * @return
     */
    private List<PromotionPriceReductionVO> buildCrossSellerPromitions(List<PromotionOptionVO> promotionOptionVOS) {
        return buildPriceReductions(promotionOptionVOS);
    }

    /**
     * 优惠计算方法
     * @param promotionOptionVOs
     * @return
     */
    private List<PromotionPriceReductionVO> buildPriceReductions(List<PromotionOptionVO> promotionOptionVOs) {
        PromotionPriceReductionVO couponPromotion = PromotionPriceReductionVO.of("COUPON");
        PromotionPriceReductionVO activityPromotion = PromotionPriceReductionVO.of("ACTIVITY");
        promotionOptionVOs.stream().forEach(promotionOptionVO -> {
            if (BooleanUtils.isTrue(promotionOptionVO.getSelected())) {
                if (BooleanUtils.isTrue(promotionOptionVO.getIsCoupon())) {
                    couponPromotion.incr(promotionOptionVO.getReduceFee());
                }
                if (BooleanUtils.isFalse(promotionOptionVO.getIsCoupon())) {
                    activityPromotion.incr(promotionOptionVO.getReduceFee());
                }
            }
        });
        return Stream.of(couponPromotion, activityPromotion)
            .filter(x -> x.getReduceFee() > 0)
            .collect(Collectors.toList());
    }

    /**
     * 计算返回最新的数据
     * @param cart
     * @return List<SellerSkuPriceVO>
     * 2025-2-12 11:21:13
     */
//    private List<SellerSkuPriceVO> calcSellerSkuPrice(CartPriceVO cart) {
//        List<SellerSkuPriceVO> sellerPriceVOList = new ArrayList<>();
//        if (Objects.isNull(cart.getSellers())) {
//            return sellerPriceVOList;
//        }
//        for (SellerPriceVO sellerPriceVO : cart.getSellers()) {
//            if (Objects.nonNull(sellerPriceVO) && CollectionUtils.isNotEmpty(sellerPriceVO.getItems())) {
//                for (ItemPriceVO itemPriceVO : sellerPriceVO.getItems()) {
//                    SellerSkuPriceVO sellerSkuPriceVO = new SellerSkuPriceVO();
//                    sellerSkuPriceVO.setSellerId(sellerPriceVO.getSellerId());
//                    sellerSkuPriceVO.setItemId(itemPriceVO.getItemId());
//                    sellerSkuPriceVO.setSkuId(itemPriceVO.getSkuId());
//                    sellerSkuPriceVO.setItemPriceName(itemPriceVO.getItemPriceName());
//                    sellerSkuPriceVO.setItemPrice(itemPriceVO.getItemPrice());
//                    sellerSkuPriceVO.setPromotionPrice(itemPriceVO.getPromotionPrice());
//                    sellerSkuPriceVO.setSkuQty(itemPriceVO.getSkuQty());
//                    sellerSkuPriceVO.setFirstPayPrice(itemPriceVO.getFirstPayPrice());
//                    sellerPriceVOList.add(sellerSkuPriceVO);
//                }
//            }
//        }
//        return sellerPriceVOList;
//    }
}
