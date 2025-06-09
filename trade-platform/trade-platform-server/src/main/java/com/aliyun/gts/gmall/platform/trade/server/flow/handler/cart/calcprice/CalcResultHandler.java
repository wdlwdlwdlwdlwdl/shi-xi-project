package com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.calcprice;

import com.aliyun.gts.gmall.platform.trade.api.dto.common.promotion.PromotionOptionDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.calc.SellerPriceDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.cart.query.CartDTO;
import com.aliyun.gts.gmall.platform.trade.core.config.TradeLimitConfiguration;
import com.aliyun.gts.gmall.platform.trade.domain.entity.cart.Cart;
import com.aliyun.gts.gmall.platform.trade.server.converter.CartRpcConverter;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TCartCalc;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 购物车计算结果转换
 */
@Component
public class CalcResultHandler extends AdapterHandler<TCartCalc> {

    @Autowired
    private CartRpcConverter cartRpcConverter;

    // 金额限制
    @Autowired
    private TradeLimitConfiguration tradeLimitConfiguration;

    @Override
    public void handle(TCartCalc inbound) {
        Cart cart = inbound.getDomain();
        CartDTO cartDTO = cartRpcConverter.toCartDTO(cart);
        inbound.setResult(cartDTO);

//        CartPriceDTO result = cartRpcConverter.toCartPriceDTO(cart);
//        //根据前面代码，其实只有一个分组
//        Long saleAmt = getSaleAmt(result.getSellers());
//        Long totalReducePrice = getTotalReducePrice(result.getOptions());
//        /**
//         * 根据支付方式判断
//         * 总价= 商品价格*数量
//         * 总价- 优惠金额 = 最终支付金额
//         * 最终支付金额必须大于限制
//         */
//        // loan
//        if (PayModeCode.isLoan(PayModeCode.codeOf(cart.getPayMode()))) {
//            //售卖价格减去优化价格  收购超过支付上线
//            if (saleAmt - totalReducePrice > tradeLimitConfiguration.getLoanLimit()) {
//                result.setOverPayLimit(Boolean.TRUE);
//                result.setPayLimit(tradeLimitConfiguration.getLoanLimit());
//            }
//        }
//        // installment
//        else if (PayModeCode.isInstallment(PayModeCode.codeOf(cart.getPayMode()))) {
//            //售卖价格减去优化价格 收购超过支付上线
//            if (saleAmt - totalReducePrice > tradeLimitConfiguration.getInstallmentLimit()) {
//                result.setOverPayLimit(Boolean.TRUE);
//                result.setPayLimit(tradeLimitConfiguration.getInstallmentLimit());
//            }
//        }
//        inbound.setResult(result);
    }

    /**
     * 获取商品总价
     * @param sellerPriceDTOList
     * @return
     */
    private Long getSaleAmt(List<SellerPriceDTO> sellerPriceDTOList) {
        // 商品总价; itemPrice * skuQty 加和
        return sellerPriceDTOList.stream()
            .flatMap(sellerPriceVO -> sellerPriceVO.getItems().stream())
            .mapToLong(itemPrice -> itemPrice.getItemPrice() * itemPrice.getSkuQty())
            .sum();
    }

    /**
     * 获取优惠信息总金额
     * @param promotionOptionDTOList
     * @return
     */
    private Long getTotalReducePrice(List<PromotionOptionDTO> promotionOptionDTOList) {
        if(CollectionUtils.isEmpty(promotionOptionDTOList)){
            return 0L;
        }
        List<Long> reducePrices = promotionOptionDTOList.stream()
            .map(PromotionOptionDTO::getReduceFee)
            .filter(x -> x != null)
            .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(reducePrices)) {
            return 0L;
        }
        return reducePrices.stream().reduce((aLong, aLong2) -> aLong + aLong2).get();
    }
}