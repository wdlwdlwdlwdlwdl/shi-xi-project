package com.aliyun.gts.gmall.platform.trade.persistence.rpc.converter;

import com.aliyun.gts.gmall.platform.promotion.api.dto.input.PromotionQueryReq;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.PromotionDetailDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.display.PromotionSelection;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.display.PromotionSummation;
import com.aliyun.gts.gmall.platform.promotion.common.model.*;
import com.aliyun.gts.gmall.platform.trade.api.constant.PayModeCode;
import com.aliyun.gts.gmall.platform.trade.common.constants.PromotionTypeEnum;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.*;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemPromotion;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.aliyun.gts.gmall.platform.promotion.common.type.AssetsType.NONE;

@Mapper(componentModel = "spring")
public interface PromotionRpcConverter {

    // ================ domain --> req ================
    @Mappings({
        @Mapping(target = "cust.custId", source = "custId"),
        @Mapping(target = "channel", source = "orderChannel", qualifiedByName = "getChannel"),
        @Mapping(target = "itemClusters", source = "sellers"),
        @Mapping(target = "selectedProms", source = "options"),
        @Mapping(target = "promotionSource", source = "promotionSource"),
    })
    PromotionQueryReq toPromotionQueryReq(OrderPromotion p);

    @Mappings({
        @Mapping(target = "sellerId", source = "sellerId"),
        @Mapping(target = "targetItems", expression = "java(getItemList(seller))"),
        @Mapping(target = "selectedCampaigns", source = "options", qualifiedByName = "getCampaigns"),
        @Mapping(target = "selectedCoupons", source = "options", qualifiedByName = "getCoupons"),
        @Mapping(target = "outId", source = "sellerId"),
    })
    TargetItemCluster toTargetItemCluster(SellerPromotion seller);

    @Mappings({
        @Mapping(target = "key", source = "optionId"),
        @Mapping(target = "selected", source = "selected"),
        @Mapping(target = "selectable", source = "selectable"),
    })
    SelectedProm toSelectedProm(PromotionOption p);

    @Mappings({
        @Mapping(target = "sellerId", source = "item.itemSkuId.sellerId"),
        @Mapping(target = "skuId", source = "item.itemSkuId.skuId"),
        @Mapping(target = "itemId", source = "item.itemSkuId.itemId"),
        @Mapping(target = "quoteId", source = "item.itemSkuId.skuQuoteId"),
        @Mapping(target = "itemQty", source = "item.skuQty"),
        @Mapping(target = "skuOriginPrice", source = "item.originPrice"),
        @Mapping(target = "paymentItems", source = "item.payModePrices"),
    })
    TargetItem toTargetItem(ItemPromotion item, SellerPromotion seller);

    //商品价格计算
    PaymentItem toPaymentItemPrice(PayModeItemPrice payModeItemPrice);

    @Named("getChannel")
    default Integer getChannel(String orderChannel) {
        return 0;
    }

    @Named("getCampaigns")
    default List<SelectedProm> getCampaigns(List<PromotionOption> list) {
        if (list == null) {
            return null;
        }
        return list.stream()
            .filter(PromotionOption::isCampaign)
            .map(this::toSelectedProm)
            .collect(Collectors.toList());
    }

    @Named("getCoupons")
    default List<SelectedProm> getCoupons(List<PromotionOption> list) {
        if (list == null) {
            return null;
        }
        return list.stream()
            .filter(PromotionOption::isCoupon)
            .map(this::toSelectedProm)
            .collect(Collectors.toList());
    }

    @Named("getItemList")
    default List<TargetItem> getItemList(SellerPromotion seller) {
        if (CollectionUtils.isEmpty(seller.getItems())) {
            return new ArrayList<>();
        }
        return seller.getItems().stream()
            .map(item -> toTargetItem(item, seller))
            .collect(Collectors.toList());
    }


    // ================ resp --> domain ================

    /**
     * 解析营销返回结果
     * @param summation
     * @param query
     * @return
     */
    default OrderPromotion toOrderPromotion(PromotionSummation summation, OrderPromotion query) {
        long totalProPrice = 0L;
        List<SellerPromotion> sellerResult = new ArrayList<>();
        for (SellerPromotion seller : query.getSellers()) {
            long sellerProPrice = 0L;
            SellerPromotion sp = new SellerPromotion();
            sp.setSellerId(seller.getSellerId());
            sellerResult.add(sp);
            List<ItemPromotion> itemResult = new ArrayList<>();
            sp.setItems(itemResult);
            for (ItemPromotion item : seller.getItems()) {
                ItemDividePriceDTO itemDiv = summation.getItemDivide().get(item.getItemSkuId().getSkuId());
                ItemPromotion ip = toItemPromotion(itemDiv, item);
                itemResult.add(ip);
                sellerProPrice += itemDiv.getTotalPromPrice();
            }
            sp.setPromotionPrice(sellerProPrice);
            totalProPrice += sellerProPrice;
            List<PromotionOption> sellerOptions = new ArrayList<>();
            sp.setOptions(sellerOptions);
            final Map<Long, PromotionSelection> sellerSelections = summation.getSellerSelections();
            if (sellerSelections != null) {
                PromotionSelection shopPromOutput = sellerSelections.get(seller.getSellerId());
                fillOptions(shopPromOutput, sellerOptions);
            }
        }
        List<PromotionOption> crossOptions = new ArrayList<>();
        fillOptions(summation.getPlatformSelection(), crossOptions);
        OrderPromotion result = new OrderPromotion();
        result.setOptions(crossOptions);
        result.setSellers(sellerResult);
        result.setPromotionPrice(totalProPrice);
        result.setDeductUserAssets(summation.getDeductUserAssets());
        return result;
    }

    /**
     * 营销结果解析 -- 购物车
     * @param summation
     * @param query
     * @return
     */
    default OrderPromotion toCartPromotionBySeller(PromotionSummation summation, OrderPromotion query) {
        long totalProPrice = 0L;
        // 卖家优惠
        List<SellerPromotion> sellerResult = new ArrayList<>();
        // 抵扣券对象
        Map<Long, List<PromDivideDTO>> deductUserAssets = new HashMap();
        for (SellerPromotion seller : query.getSellers()) {
            long sellerProPrice = 0L;
            // 构建对象
            SellerPromotion sellerPromotion = new SellerPromotion();
            sellerPromotion.setSellerId(seller.getSellerId());
            sellerResult.add(sellerPromotion);
            // 构建抵扣资产对象
            final List<PromDivideDTO> promDivideDTOMap = deductUserAssets.computeIfAbsent(seller.getSellerId(), k -> new ArrayList<>());
            // 获取卖家的营销信息
            List<ItemDividePriceDTO> itemDividePrice = summation.getCarItemDivide().get(seller.getSellerId());
            if (CollectionUtils.isEmpty(itemDividePrice)) {
                continue;
            }
            List<ItemPromotion> itemResult = new ArrayList<>();
            sellerPromotion.setItems(itemResult);
            for (ItemPromotion item : seller.getItems()) {
                // 遍历每个商品 获取卖家的商品营销信息
                ItemDividePriceDTO itemDiv = itemDividePrice
                    .stream()
                    .filter(itemDividePriceDTO ->
                        item.getItemSkuId().getItemId().equals(itemDividePriceDTO.getItemId()) &&
                        item.getItemSkuId().getSkuId().equals(itemDividePriceDTO.getSkuId())
                    ).findFirst().orElse(null);
                if (Objects.isNull(itemDiv)) {
                    continue;
                }
                ItemPromotion itemPromotion = toItemPromotion(itemDiv, item);
                //itemPromotion.getItemSkuId().setSellerId(seller.getSellerId());
                // 商品元素
                itemResult.add(itemPromotion);
                if (Objects.nonNull(itemDiv) && CollectionUtils.isNotEmpty(itemDiv.getDivides())) {
                    // 抵扣元素
                    itemDiv.getDivides()
                        .stream()
                        .filter(d ->
                            Objects.nonNull(d.getAssetType()) && d.getAssetType() > NONE.getCode() &&
                            promDivideDTOMap.stream().noneMatch(div -> div.getCampId().equals(d.getCampId()))
                        )
                        .forEach(promDivideDTOMap::add);
                }
                sellerProPrice += itemDiv.getTotalPromPrice();
            }
            sellerPromotion.setPromotionPrice(sellerProPrice);
            totalProPrice += sellerProPrice;
            List<PromotionOption> sellerOptions = new ArrayList<>();
            sellerPromotion.setOptions(sellerOptions);
            final Map<Long, PromotionSelection> sellerSelections = summation.getSellerSelections();
            if (sellerSelections != null) {
                PromotionSelection shopPromOutput = sellerSelections.get(seller.getSellerId());
                fillOptions(shopPromOutput, sellerOptions);
            }
        }
        List<PromotionOption> crossOptions = new ArrayList<>();
        fillOptions(summation.getPlatformSelection(), crossOptions);
        OrderPromotion result = new OrderPromotion();
        result.setOptions(crossOptions);
        result.setSellers(sellerResult);
        result.setPromotionPrice(totalProPrice);
        result.setDeductUserAssets(deductUserAssets);
        return result;
    }

    /**
     * 营销结果解析 -- 订单用
     * @param summation
     * @param query
     * @return
     */
    default OrderPromotion toOrderPromotionByQuoteId(PromotionSummation summation, OrderPromotion query) {
        long totalProPrice = 0L;
        // 支付方式支付总金额
        Map<String, Long> installTotal = new HashMap<>();
        // 支付方式优惠总金额
        Map<String, Long> installPromotion = new HashMap<>();
        // 优惠券
        List<SellerPromotion> sellerResult = new ArrayList<>();
        // 抵扣券对象
        Map<Long, List<PromDivideDTO>> deductUserAssets = new HashMap();
        for (SellerPromotion seller : query.getSellers()) {
            long sellerProPrice = 0L;
            // 构建对象
            SellerPromotion sellerPromotion = new SellerPromotion();
            sellerPromotion.setSellerId(seller.getSellerId());
            sellerResult.add(sellerPromotion);
            List<ItemPromotion> itemResult = new ArrayList<>();
            sellerPromotion.setItems(itemResult);
            // 遍历商品 通过 QuoteId 匹配
            for (ItemPromotion item : seller.getItems()) {
                ItemPromotion itemPromotion = new ItemPromotion();
                //  获取单品的优惠信息  每个对象是一个支付方式的数据
                List<ItemDividePriceDTO> itemDividePrice = summation.getCarItemDivide().get(item.getItemSkuId().getSkuQuoteId());
                if (CollectionUtils.isEmpty(itemDividePrice)) {
                    continue;
                }
                // 全部价格
                List<PayModeItemPrice> payModePrices = itemDividePrice.stream()
                    .filter(Objects::nonNull)
                    .map(itemDividePriceDTO -> {
                        // 当前支付方式获取营销信息 支付方式比较 应该取得价格
                        if (PayModeCode.isPayMode(query.getOriginPayMode(), itemDividePriceDTO.getPayMethod())) {
                            itemPromotion.setOriginPrice(itemDividePriceDTO.getOrigPrice());
                            itemPromotion.setPromotionPrice(itemDividePriceDTO.getTotalPromPrice());
                            itemPromotion.setItemPriceName(itemDividePriceDTO.getName());
                            itemPromotion.setItemPrice(itemDividePriceDTO.getItemPriceNotNull());
                            itemPromotion.setItemDivideDetails(getItemDivideDetailList(itemDividePriceDTO.getDivides()));
                        }
                        // 所有分期营销价格
                        PayModeItemPrice payModeItemPrice = new PayModeItemPrice();
                        payModeItemPrice.setSkuOrigPrice(itemDividePriceDTO.getOrigPrice());
                        payModeItemPrice.setPromotionPrice(itemDividePriceDTO.getTotalPromPrice());
                        payModeItemPrice.setSellerId(itemDividePriceDTO.getSellerId());
                        payModeItemPrice.setPayMethod(itemDividePriceDTO.getPayMethod());
                        // 总金额计算
                        Long total = installTotal.getOrDefault(itemDividePriceDTO.getPayMethod(), 0L);
                        total += itemDividePriceDTO.getTotalItemPrice();
                        installTotal.put(itemDividePriceDTO.getPayMethod(), total);
                        // 分期优惠金额计算
                        Long promotion = installPromotion.getOrDefault(itemDividePriceDTO.getPayMethod(), 0L);
                        promotion += itemDividePriceDTO.getTotalPromPrice();
                        installPromotion.put(itemDividePriceDTO.getPayMethod(), promotion);
                        return payModeItemPrice;
                    }
                ).collect(Collectors.toList());
                itemPromotion.setSkuQty(item.getSkuQty());
                itemPromotion.setItemSku(item.getItemSku());
                itemPromotion.setPayModePrices(payModePrices);
                // 商品ID参数对象
                ItemSkuId itemSkuId = new ItemSkuId();
                itemSkuId.setItemId(item.getItemSkuId().getItemId());
                itemSkuId.setSkuId(item.getItemSkuId().getSkuId());
                itemPromotion.setItemSkuId(itemSkuId);
                // 设置
                itemResult.add(itemPromotion);
                sellerProPrice += (Objects.nonNull(itemPromotion.getPromotionPrice()) ? itemPromotion.getPromotionPrice() : 0L);
            }
            sellerPromotion.setPromotionPrice(sellerProPrice);
            totalProPrice += sellerProPrice;
            List<PromotionOption> sellerOptions = new ArrayList<>();
            sellerPromotion.setOptions(sellerOptions);
            final Map<Long, PromotionSelection> sellerSelections = summation.getSellerSelections();
            if (sellerSelections != null) {
                PromotionSelection shopPromOutput = sellerSelections.get(seller.getSellerId());
                fillOptions(shopPromOutput, sellerOptions);
            }
        }
        List<PromotionOption> crossOptions = new ArrayList<>();
        fillOptions(summation.getPlatformSelection(), crossOptions);
        OrderPromotion result = new OrderPromotion();
        result.setOptions(crossOptions);
        result.setSellers(sellerResult);
        result.setPromotionPrice(totalProPrice);
        result.setInstallmentTotal(installTotal);
        result.setInstallmentPromotion(installPromotion);

        result.setDeductUserAssets(summation.getDeductUserAssets());
        return result;
    }

    default void fillOptions(PromotionSelection sel, List<PromotionOption> options) {
        if (null == sel) {
            return;
        }
        if (sel.getNormalOptions() != null) {
            sel.getNormalOptions().forEach(prom -> options.add(toPromotionOption(
                    prom, PromotionTypeEnum.NORMAL.getCode())));
        }
        if (sel.getAssetOptions() != null) {
            for (Entry<Integer, List<SelectedProm>> en : sel.getAssetOptions().entrySet()) {
                List<SelectedProm> assetList = en.getValue();
                Integer assetType = en.getKey();
                if (CollectionUtils.isNotEmpty(assetList)) {
                    assetList.forEach(prom -> options.add(toPromotionOption(prom, assetType)));
                }
            }
        }
    }

    @Mappings({
        @Mapping(target = "itemSkuId.itemId", source = "item.itemId"),
        @Mapping(target = "itemSkuId.skuId", source = "item.skuId"),
        @Mapping(target = "itemPrice", source = "item.itemPriceNotNull"),   // 营销一口价 (单价)
        @Mapping(target = "promotionPrice", source = "item.totalPromPrice"),   // 优惠分摊价 (x件数)
        @Mapping(target = "originPrice", source = "item.origPrice"),
        @Mapping(target = "itemDivideDetails", source = "item.divides"),
        @Mapping(target = "skuQty", source = "query.skuQty"),
        @Mapping(target = "itemPriceName", source = "item.name"),
    })
    ItemPromotion toItemPromotion(ItemDividePriceDTO item, ItemPromotion query);

    ItemDivideDetail getItemDivideDetail(PromDivideDTO div);

    List<ItemDivideDetail> getItemDivideDetailList(List<PromDivideDTO> div);

    @Mappings({
        @Mapping(target = "toolCode", source = "promotionToolCode"),
        @Mapping(target = "campId", source = "campaignId"),
        @Mapping(target = "extras", source = "extras"),
        @Mapping(target = "name", source = "name"),
    })
    ItemDivideDetail campToItemDivideDetail(PromotionDetailDTO camp);

    @Mappings({
        @Mapping(target = "optionId", source = "p.key"),
        @Mapping(target = "promotionName", source = "p.name"),
        @Mapping(target = "reduceFee", source = "p.reduce"),
        @Mapping(target = "selected", source = "p.selected"),
        @Mapping(target = "selectable", source = "p.selectable"),
        @Mapping(target = "promotionType", source = "promotionType"),
        @Mapping(target = "toolCode", source = "p.toolCode"),
    })
    PromotionOption toPromotionOption(SelectedProm p, Integer promotionType);

}
