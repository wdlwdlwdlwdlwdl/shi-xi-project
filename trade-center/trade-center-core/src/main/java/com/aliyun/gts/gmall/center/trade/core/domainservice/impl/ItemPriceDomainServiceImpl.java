package com.aliyun.gts.gmall.center.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.center.item.api.dto.input.AgreementPriceBatchQueryReq;
import com.aliyun.gts.gmall.center.item.api.dto.input.AgreementPriceQueryReq;
import com.aliyun.gts.gmall.center.item.api.dto.output.AgreementPriceDTO;
import com.aliyun.gts.gmall.center.item.api.dto.output.B2bItemDTO;
import com.aliyun.gts.gmall.center.item.api.dto.output.StepPriceDTO;
import com.aliyun.gts.gmall.center.item.api.facade.AgreementPriceFacade;
import com.aliyun.gts.gmall.center.item.api.util.AgreementUtils;
import com.aliyun.gts.gmall.center.item.common.consts.ItemExtendConstant;
import com.aliyun.gts.gmall.center.item.common.enums.AgreementPriceType;
import com.aliyun.gts.gmall.center.trade.core.constants.ExtOrderErrorCode;
import com.aliyun.gts.gmall.center.trade.core.domainservice.ItemPriceDomainService;
import com.aliyun.gts.gmall.center.trade.core.util.ItemUtils;
import com.aliyun.gts.gmall.center.trade.domain.constant.TradeInnerExtendKeys;
import com.aliyun.gts.gmall.center.trade.domain.entity.b2b.ItemPriceInput;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.util.CommUtils;
import com.aliyun.gts.gmall.platform.trade.domain.util.ErrorUtils;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ItemPriceDomainServiceImpl implements ItemPriceDomainService {

    @Autowired
    private AgreementPriceFacade agreementPriceFacade;

    @Override
    public void calcItemPrice(List<ItemPriceInput> input) {

        //代客下单自定义价，优先级比协议价和阶梯价高
        calcHelpOrderPrice(input);
        // 协议价
        calcAgreementPrice(input);
        // 阶梯价
        calcB2bStepPrice(input);
        // 校验起购数量
        checkMinBuyNum(input);
    }

    @Override
    public void calcItemPrice(ItemPriceInput input) {
        // 协议价
        calcAgreementPrice(input);
        // 阶梯价
        calcB2bStepPrice(input);
    }

    private void calcHelpOrderPrice(List<ItemPriceInput> input) {
        for(ItemPriceInput itemPriceInput: input) {
            ItemSku itemSku = itemPriceInput.getItemSku();
            Long helpOrderPrice = itemSku.getItemPrice().getHelpOrderPrice();
            if(Objects.nonNull(helpOrderPrice) && helpOrderPrice>0) {
                itemPriceInput.setHelpOrderPrice(Boolean.TRUE);
                itemSku.putExtra(TradeInnerExtendKeys.ITEM_IS_HELP_ORDER_PRICE, Boolean.TRUE);
                itemSku.getItemPrice().setOriginPrice(helpOrderPrice);
            }
        }
    }

    private void calcAgreementPrice(ItemPriceInput input) {
        //代客下单价比协议价优先级高
        if(input.isHelpOrderPrice()) {
            return;
        }
        AgreementPriceQueryReq req = new AgreementPriceQueryReq();
        req.setSellerId(input.getItemSku().getSeller().getSellerId());
        req.setCustId(input.getCustId());
        req.setItemIds(Arrays.asList(0L, input.getItemSku().getItemId()));
        RpcResponse<List<AgreementPriceDTO>> resp = agreementPriceFacade.queryAgreementByItems(req);
        if (!resp.isSuccess()) {
            throw new GmallException(ErrorUtils.mapCode(resp.getFail()));
        }

        if (CollectionUtils.isEmpty(resp.getData())) {
            return;
        }
        Map<Long, AgreementPriceDTO> map = resp.getData().stream()
                .collect(Collectors.toMap(AgreementPriceDTO::getItemId, Function.identity()));
        AgreementPriceDTO itemAgreement = map.get(input.getItemSku().getItemId());
        AgreementPriceDTO userAgreement = map.get(0L);
        if (!AgreementUtils.isAvailable(new Date(), userAgreement, itemAgreement)) {
            return;
        }

        // 阶梯价
        if (AgreementPriceType.COUNT.getCode().equals(itemAgreement.getPriceType())) {
            StepPriceCalc c = new StepPriceCalc();
            c.quantity = input.getQuantity();
            c.setAgreeRule(itemAgreement);
            Long price = c.getResultPrice();
            if (price != null) {
                input.getItemSku().getItemPrice().setOriginPrice(price);
                input.getItemSku().putExtra(TradeInnerExtendKeys.ITEM_IS_AGREEMENT, true);
                input.setAgreementPrice(true);
            }
            input.setAgreementMinCount(itemAgreement.getMinBuyNum());
        }
        // 按规格计价
        else if (AgreementPriceType.SKU.getCode().equals(itemAgreement.getPriceType())) {
            Long price = itemAgreement.getSkuPrice().get(input.getItemSku().getSkuId());
            if (price != null) {
                input.getItemSku().getItemPrice().setOriginPrice(price);
                input.getItemSku().putExtra(TradeInnerExtendKeys.ITEM_IS_AGREEMENT, true);
                input.setAgreementPrice(true);
            }
        }
    }

    private void calcB2bStepPrice(ItemPriceInput input) {
        // 代客下单价或协议价优先
        if (input.isHelpOrderPrice() || input.isAgreementPrice()) {
            return;
        }
        B2bItemDTO b2b = getB2bInfo(input.getItemSku());
        if (isStepPrice(b2b)) {
            StepPriceCalc c = new StepPriceCalc();
            c.quantity = input.getQuantity();
            c.setB2bRule(b2b);
            Long result = c.getResultPrice();
            if (result != null) {
                input.getItemSku().getItemPrice().setOriginPrice(result);
            }
        }
    }

    private void calcAgreementPrice(List<ItemPriceInput> input) {
        Multimap<Long, ItemPriceInput> sellerMap = HashMultimap.create();
        for (ItemPriceInput itemInput : input) {
            sellerMap.put(itemInput.getItemSku().getSeller().getSellerId(), itemInput);
        }

        List<AgreementPriceQueryReq> queryList = new ArrayList<>();
        for (Long sellerId : sellerMap.keySet()) {
            for (ItemPriceInput itemInput : sellerMap.get(sellerId)) {
                AgreementPriceQueryReq query = new AgreementPriceQueryReq();
                query.setSellerId(sellerId);
                query.setCustId(itemInput.getCustId());
                query.setItemIds(Arrays.asList(0L, itemInput.getItemSku().getItemId()));
                queryList.add(query);

            }
        }
        AgreementPriceBatchQueryReq req = new AgreementPriceBatchQueryReq();
        req.setQueryList(queryList);
        RpcResponse<List<AgreementPriceDTO>> resp = agreementPriceFacade.batchQueryAgreements(req);
        if (!resp.isSuccess()) {
            throw new GmallException(ErrorUtils.mapCode(resp.getFail()));
        }

        if (CollectionUtils.isEmpty(resp.getData())) {
            return;
        }

        Map<Long, AgreementPriceDTO> itemMap = new HashMap<>();
        Map<Long, AgreementPriceDTO> userMap = new HashMap<>();
        for (AgreementPriceDTO agreement : resp.getData()) {
            if (agreement.getItemId() == 0L) {
                userMap.put(agreement.getSellerId(), agreement);
            } else {
                itemMap.put(agreement.getItemId(), agreement);
            }
        }
        Map<Long, StepPriceCalc> stepPriceMap = new HashMap<>();
        Date now = new Date();
        for (ItemPriceInput itemInput : input) {
            //代客下单价优先级比协议价高
            if(itemInput.isHelpOrderPrice()){
                continue;
            }
            AgreementPriceDTO itemAgreement = itemMap.get(itemInput.getItemSku().getItemId());
            AgreementPriceDTO userAgreement = userMap.get(itemInput.getItemSku().getSeller().getSellerId());
            if (!AgreementUtils.isAvailable(now, userAgreement, itemAgreement)) {
                continue;
            }
            // 阶梯价
            if (AgreementPriceType.COUNT.getCode().equals(itemAgreement.getPriceType())) {
                StepPriceCalc c = CommUtils.getValue(stepPriceMap, itemInput.getItemSku().getItemId(), StepPriceCalc::new);
                c.quantity += itemInput.getQuantity();
                c.setAgreeRule(itemAgreement);
            }
            // 按规格计价
            else if (AgreementPriceType.SKU.getCode().equals(itemAgreement.getPriceType())) {
                Long price = itemAgreement.getSkuPrice().get(itemInput.getItemSku().getSkuId());
                if (price != null) {
                    itemInput.getItemSku().getItemPrice().setOriginPrice(price);
                    itemInput.getItemSku().putExtra(TradeInnerExtendKeys.ITEM_IS_AGREEMENT, true);
                    itemInput.setAgreementPrice(true);
                }
            }
        }
        // 阶梯价汇总
        if (!stepPriceMap.isEmpty()) {
            for (ItemPriceInput item : input) {
                StepPriceCalc c = stepPriceMap.get(item.getItemSku().getItemId());
                if (c != null) {
                    Long result = c.getResultPrice();
                    if (result != null) {
                        item.getItemSku().getItemPrice().setOriginPrice(result);
                        item.getItemSku().putExtra(TradeInnerExtendKeys.ITEM_IS_AGREEMENT, true);
                        item.setAgreementPrice(true);
                    }
                    item.setAgreementMinCount(c.minBuyCount);
                }
            }
        }
    }

    private void calcB2bStepPrice(List<ItemPriceInput> input) {
        Map<Long, StepPriceCalc> stepPriceMap = new HashMap<>();
        for (ItemPriceInput item : input) {
            // 代客下单价和协议价优先
            if (item.isHelpOrderPrice()||item.isAgreementPrice()) {
                continue;
            }
            B2bItemDTO b2b = getB2bInfo(item.getItemSku());
            if (isStepPrice(b2b)) {
                StepPriceCalc c = CommUtils.getValue(stepPriceMap, item.getItemSku().getItemId(), StepPriceCalc::new);
                c.quantity += item.getQuantity();
                c.setB2bRule(b2b);
            }
        }
        // 阶梯价汇总
        if (!stepPriceMap.isEmpty()) {
            for (ItemPriceInput item : input) {
                StepPriceCalc c = stepPriceMap.get(item.getItemSku().getItemId());
                if (c != null) {
                    Long result = c.getResultPrice();
                    if (result != null) {
                        item.getItemSku().getItemPrice().setOriginPrice(result);
                    }
                }
            }
        }
    }

    private static void checkMinBuyNum(List<ItemPriceInput> input) {
        Map<Long, Integer> minMap = new HashMap<>();
        Map<Long, IntCounter> buyMap = new HashMap<>();
        for (ItemPriceInput item : input) {
            B2bItemDTO b2b = getB2bInfo(item.getItemSku());
            if (b2b == null) {
                continue;   // 非 b2b 商品, 不校验起购数 ( 协议阶梯价也不校验 )
            }
            Integer min = item.getAgreementMinCount();
            if (min == null) {
                min = b2b.getMinBuyNum();
            }
            if (min == null) {
                continue;
            }
            minMap.put(item.getItemSku().getItemId(), min);
            CommUtils.getValue(buyMap, item.getItemSku().getItemId(), IntCounter::new)
                    .value += item.getQuantity();
        }
        for (Entry<Long, Integer> min : minMap.entrySet()) {
            int buy = buyMap.get(min.getKey()).value;
            if (buy < min.getValue().intValue()) {
                throw new GmallException(ExtOrderErrorCode.ORDER_QTY_LOW);
            }
        }
    }

    private static boolean isStepPrice(B2bItemDTO b2b) {
        return b2b != null
                && b2b.getType() != null
                && b2b.getType().intValue() == B2bItemDTO.TYPE_1
                && CollectionUtils.isNotEmpty(b2b.getPriceList());
    }

    private static B2bItemDTO getB2bInfo(ItemSku itemSku) {
        return ItemUtils.getExtendObject(itemSku, ItemExtendConstant.B2B_ITEM, B2bItemDTO.class);
    }

    private static class StepPriceCalc {
        private int quantity;
        private Map<Integer, Long> stepRule;
        private boolean cached = false;
        private Long cachedValue = null;
        private Integer minBuyCount;

        public Long getResultPrice() {
            if (cached) {
                return cachedValue;
            }
            cachedValue = AgreementUtils.getStepPrice(stepRule, quantity);
            /*
            if (cachedValue == null) {
                Entry<Long, Long> range = AgreementUtils.getRange(stepRule.values());
                cachedValue = range == null ? null : range.getValue();
            } */
            cached = true;
            return cachedValue;
        }

        public void setB2bRule(B2bItemDTO b2b) {
            if (stepRule != null) {
                return;
            }
            Map<Integer, Long> map = new HashMap<>();
            for (StepPriceDTO price : b2b.getPriceList()) {
                map.put(price.getNum(), price.getPrice());
            }
            this.stepRule = map;
            this.minBuyCount = b2b.getMinBuyNum();
        }

        public void setAgreeRule(AgreementPriceDTO itemAgreement) {
            if (stepRule != null) {
                return;
            }
            this.stepRule = itemAgreement.getStepPrice();
            this.minBuyCount = itemAgreement.getMinBuyNum();
        }
    }

    private static class IntCounter {
        private int value;
    }
}
