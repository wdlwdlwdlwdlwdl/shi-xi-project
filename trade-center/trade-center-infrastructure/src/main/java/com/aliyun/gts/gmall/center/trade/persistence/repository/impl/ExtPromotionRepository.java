package com.aliyun.gts.gmall.center.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.center.trade.domain.constant.TradeInnerExtendKeys;
import com.aliyun.gts.gmall.center.trade.domain.entity.promotion.BuyOrdsLimit;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.promotion.api.dto.input.PromotionQueryReq;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.display.PromotionSummation;
import com.aliyun.gts.gmall.platform.promotion.common.constant.AttributesKey;
import com.aliyun.gts.gmall.platform.promotion.common.model.ItemDividePriceDTO;
import com.aliyun.gts.gmall.platform.promotion.common.model.PromDivideDTO;
import com.aliyun.gts.gmall.platform.promotion.common.model.TargetItemCluster;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemPromotionQuery;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.OrderPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.SellerPromotion;
import com.aliyun.gts.gmall.platform.trade.persistence.repository.impl.PromotionRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Configuration
@ConditionalOnProperty(prefix = "trade", name = "promotionRepository", havingValue = "ext")
public class ExtPromotionRepository extends PromotionRepositoryImpl {
    private static final String CODE_MIAOSHA = "miaosha";

    @Override
    protected PromotionQueryReq convertItemPromotionRequest(ItemPromotionQuery query) {
        PromotionQueryReq req = super.convertItemPromotionRequest(query);

        // 协议价商品
        Set<Long> agreementSkus = query.getList().stream().filter(this::isAgreement)
                .map(ItemSku::getSkuId).collect(Collectors.toSet());
        if (!agreementSkus.isEmpty()) {
            fillAgreementReq(agreementSkus, req);
        }

        return req;
    }

    @Override
    protected PromotionQueryReq convertOrderPromotionRequest(OrderPromotion query, QueryFrom from) {
        PromotionQueryReq req = super.convertOrderPromotionRequest(query, from);

        if(CollectionUtils.isEmpty(query.getSellers()))
        {
            log.warn("query.getSellers() is null");
            return req;
        }
        // 协议价商品
        Set<Long> agreementSkus = query.getSellers().stream()
                .map(SellerPromotion::getItems).flatMap(List::stream).map(ItemPromotion::getItemSku)
                .filter(this::isAgreement).map(ItemSku::getSkuId).collect(Collectors.toSet());
        if (!agreementSkus.isEmpty()) {
            fillAgreementReq(agreementSkus, req);
        }
        return req;
    }

    @Override
    protected OrderPromotion convertOrderPromotionResponse(RpcResponse<PromotionSummation> resp,
                                                           OrderPromotion query,
                                                           QueryFrom from,
                                                           PromotionQueryReq req) {
        OrderPromotion result = super.convertOrderPromotionResponse(resp, query, from, req);
        fillBuyLimit(result, resp.getData());
        return result;
    }

    private void fillBuyLimit(OrderPromotion target, PromotionSummation source) {
        Map<Long, Integer> limitMap = new HashMap<>();
        Map<Long, BuyOrdsLimit> ordsMap = new HashMap<>();
        for (ItemDividePriceDTO price : source.getItemDivide().values()) {
            Integer limit = price.getBuyLimitNum();
            if (limit != null) {
                limitMap.put(price.getSkuId(), limit);
            }
            BuyOrdsLimit ordsLimit = buildOrdsLimit(price);
            if (ordsLimit != null) {
                ordsMap.put(price.getSkuId(), ordsLimit);
            }

        }
        if (limitMap.isEmpty() && ordsMap.isEmpty()) {
            return;
        }

        for (SellerPromotion seller : target.getSellers()) {
            for (ItemPromotion item : seller.getItems()) {
                Integer limit = limitMap.get(item.getItemSkuId().getSkuId());
                if (limit != null) {
                    item.putExtra(TradeInnerExtendKeys.PROMOTION_BUY_LIMIT, limit);
                }
                BuyOrdsLimit ordsLimit = ordsMap.get(item.getItemSkuId().getSkuId());
                if (ordsLimit != null) {
                    item.putExtra(TradeInnerExtendKeys.PROMOTION_BUY_ORDS_LIMIT, ordsLimit);
                }

            }
        }
    }

    private BuyOrdsLimit buildOrdsLimit(ItemDividePriceDTO price) {
        Integer limit = price.getBuyLimitOrds();
        if (limit == null) {
            return null;
        }
        if (CollectionUtils.isEmpty(price.getDivides())) {
            return null;
        }
        PromDivideDTO prom = price.getDivides().stream()
                .filter(d -> CODE_MIAOSHA.equals(d.getToolCode()))
                .findFirst().orElse(null);
        if (prom == null) {
            return null;
        }

        BuyOrdsLimit t = new BuyOrdsLimit();
        t.setItemId(price.getItemId());
        t.setSkuId(price.getSkuId());
        t.setOrdsLimit(limit.longValue());
        t.setCampId(prom.getCampaignId());
        t.setCustId(null);  // 后面再填
        return t;
    }


    private void fillAgreementReq(Set<Long> agreementSkus, PromotionQueryReq req) {
        req.getItemClusters().stream()
                .map(TargetItemCluster::getTargetItems)
                .flatMap(List::stream).forEach(targetItem -> {
            if (!agreementSkus.contains(targetItem.getSkuId())) {
                return;
            }
            targetItem.putAttribute(AttributesKey.TARGET_ITEM_AGREEMENT_PRICE, true);
        });
    }

    /**
     * TODO 代客下单价复用协议价查询营销的逻辑，后续跟营销中心确认后需要进行拆分(原因是协议价跟一口价的优先级可配置，代客下单价的优先级一直比一口价高)
     * @param sku
     * @return
     */
    private boolean isAgreement(ItemSku sku) {
        Boolean flag = (Boolean) sku.getExtra(TradeInnerExtendKeys.ITEM_IS_AGREEMENT);
        Boolean isHelpOrderPrice = (Boolean) sku.getExtra(TradeInnerExtendKeys.ITEM_IS_HELP_ORDER_PRICE);
        return flag != null && flag || (isHelpOrderPrice!=null && isHelpOrderPrice);
    }
}
