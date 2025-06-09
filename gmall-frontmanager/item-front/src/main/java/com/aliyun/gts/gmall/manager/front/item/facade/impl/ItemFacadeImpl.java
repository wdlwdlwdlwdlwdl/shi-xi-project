package com.aliyun.gts.gmall.manager.front.item.facade.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.util.GmallBeanUtils;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.SellerAdapter;
import com.aliyun.gts.gmall.manager.front.item.adaptor.ItemAdaptor;
import com.aliyun.gts.gmall.manager.front.item.adaptor.ItemCardEsAdaptor;
import com.aliyun.gts.gmall.manager.front.item.adaptor.ItemCardEsAdaptor.SaleSellerSortEnum;
import com.aliyun.gts.gmall.manager.front.item.constants.PayModeEnum;
import com.aliyun.gts.gmall.manager.front.item.constants.PromotionToolCodeEnum;
import com.aliyun.gts.gmall.manager.front.item.constants.TopFlagEnum;
import com.aliyun.gts.gmall.manager.front.item.dto.ItemPageQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemDetailV2VO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemPageVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemSkuPriceVO;
import com.aliyun.gts.gmall.manager.front.item.dto.temp.ItemSaleSellerQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.temp.ItemSaleSellerTempVO;
import com.aliyun.gts.gmall.manager.front.item.facade.ItemFacade;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.LoanPeriodDTO;
import com.aliyun.gts.gmall.platform.item.common.enums.ItemPriceloanPeriodTypeEnum;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.display.PromotionSummation;
import com.aliyun.gts.gmall.platform.promotion.common.model.ItemDividePriceDTO;
import com.aliyun.gts.gmall.platform.promotion.common.model.PromDivideDTO;
import com.aliyun.gts.gmall.platform.promotion.common.model.PromItemDivide;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ItemFacadeImpl implements ItemFacade, GmallBeanUtils {
    @Autowired
    private ItemAdaptor itemAdaptor;

    @Autowired
    private ItemCardEsAdaptor itemCardEsAdaptor;

    @Autowired
    private SellerAdapter sellerAdaptor;

    @Override
    public List<ItemPageVO> queryPage(ItemPageQuery query) {
        return itemAdaptor.queryPage(query);
    }

    @Override
    public ItemDetailV2VO query(ItemSaleSellerQuery query, CustDTO custDto) {
        log.info("[查询商家]入参={}", JSON.toJSONString(query));
        boolean firstQuery = query.hasNoOrderCondition() && query.hasNoFilterCondition();
        int initPeriod = Optional.ofNullable(query.getPeriod()).orElse(-1);

        int reducePeriod = -1;

        if (PayModeEnum.LOAN.getScript().equals(query.getPayMode()) && ItemPriceloanPeriodTypeEnum.THREE.getValue() != query.getPeriod()) {
            reducePeriod = query.getPeriod();
        }

        // 输入分期数
        if (Objects.isNull(query.getPeriod()) || PayModeEnum.LOAN.getScript().equals(query.getPayMode())) {
            query.setPeriod(ItemPriceloanPeriodTypeEnum.THREE.getValue());
        }

        // 查询售卖商家
        List<ItemSaleSellerTempVO> itemSaleSellerTempVoList = itemCardEsAdaptor.queryItemSaleSeller(query);
        if (CollectionUtils.isEmpty(itemSaleSellerTempVoList)) {
            return null;
        }

        // 查询优惠
        PromotionSummation promotionInfo = sellerAdaptor.queryPromotionSummation(query.getCityCode(), query.getItemId(), query.getSkuId(), query.getPeriod(),
                itemSaleSellerTempVoList.stream().collect(
                        Collectors.toMap(ItemSaleSellerTempVO::getSellerId, ItemSaleSellerTempVO::getOriginPrice, (existing, replacement) -> existing)),
                custDto);
        supplementPromotionSummationPrice(itemSaleSellerTempVoList, promotionInfo, query.getSeckillFilter(), query.getPresaleFilter(), true);

        // 增加分期的值
        ArrayList<Integer> loanPeriodTypes =
                new ArrayList<>(itemSaleSellerTempVoList.stream().map(ItemSaleSellerTempVO::getLoanPeriodType).collect(Collectors.toSet()));
        Collections.sort(loanPeriodTypes);

        // 查询top商家
        Pair<ItemSaleSellerTempVO, Integer> topPricePair = tagTop(firstQuery, initPeriod, itemSaleSellerTempVoList);

        // 查看是否有需要重新计算价格的商家
        itemSaleSellerTempVoList.forEach(n -> {
            if (n.isReducePromotion()) {
                log.info("[查询商家]重新计算商家优惠, sellerId={}, 原价={}", n.getSellerId(), n.getOriginPrice());
                PromotionSummation promotionInfoReduce = sellerAdaptor.queryPromotionSummationLowest(query.getCityCode(), query.getItemId(), query.getSkuId(),
                        n.getSellerId(), n.getOriginPrice(), custDto);
                supplementPromotionSummationPrice(List.of(n), promotionInfoReduce, query.getSeckillFilter(), query.getPresaleFilter(), false);
            }
        });

        // 排序
        sort(itemSaleSellerTempVoList, query, topPricePair.getRight());

        // 构造结果
        ItemDetailV2VO itemDetailV2Vo =
                toItemDetailV2Vo(itemSaleSellerTempVoList, promotionInfo, topPricePair.getLeft(), query.getPayMode(), query.getPeriod());
        itemDetailV2Vo.setIntallmentsPeriodTypes(loanPeriodTypes);

        // 是否重新计算价格
        reducePeriodForLond(reducePeriod, itemDetailV2Vo);
        return itemDetailV2Vo;
    }

    /**
     * top商家打标记
     * 
     * @param firstQuery 是否初次查询
     * @param initPeriod 分期入参
     * @param list
     * @return <3期最低价, 排除排序的数据数量>
     */
    private static Pair<ItemSaleSellerTempVO, Integer> tagTop(boolean firstQuery, int initPeriod, List<ItemSaleSellerTempVO> list) {
        ItemSaleSellerTempVO lowest3 = ItemCardEsAdaptor.calculatePrice_pickLowest3(list);
        if (!firstQuery || initPeriod == ItemPriceloanPeriodTypeEnum.SIX.getValue() || initPeriod == ItemPriceloanPeriodTypeEnum.TWELVE.getValue()) {
            return Pair.of(lowest3, 0);
        }

        ItemSaleSellerTempVO lowest24 = ItemCardEsAdaptor.calculatePrice_pickLowest24(list);
        List<ItemSaleSellerTempVO> topPrices = new ArrayList<>();

        if (initPeriod == -1) {
            // 置顶 24 期和 3 期
            if (Objects.nonNull(lowest24)) {
                // 24期有点特殊, 需要把3期数据改成24期
                LoanPeriodDTO loanPeriodDto =
                        lowest24.getPriceList().stream().filter(x -> x.getType() == ItemPriceloanPeriodTypeEnum.TWENTY_FOUR.getValue()).findFirst().get();
                lowest24.setLoanPeriodPrice(loanPeriodDto.getValue());
                lowest24.setOriginPrice(loanPeriodDto.getValue());
                lowest24.setLoanPeriodType(loanPeriodDto.getType());
                lowest24.setTopFlag(TopFlagEnum.TOP_ONE.getScript());
                lowest24.setReducePromotion(true);
                topPrices.add(lowest24);
                lowest3.setTopFlag(TopFlagEnum.TOP_TWO.getScript());
            } else {
                lowest3.setTopFlag(TopFlagEnum.TOP_ONE.getScript());
            }
            topPrices.add(lowest3);
            log.info("[findTopPrice]首次查询,置顶3={},24={}", Objects.nonNull(lowest3), Objects.nonNull(lowest24));
        } else if (initPeriod == ItemPriceloanPeriodTypeEnum.THREE.getValue()) {
            // 3 期时,置顶 3 期
            topPrices.add(lowest3);
            lowest3.setTopFlag(TopFlagEnum.TOP_ONE.getScript());
            log.info("[findTopPrice]3期查询,置顶3={}", Objects.nonNull(lowest3));
        } else if (initPeriod == ItemPriceloanPeriodTypeEnum.TWENTY_FOUR.getValue()) {
            // 24 期时,置顶 24 期
            if (Objects.nonNull(lowest24)) {
                lowest24.setTopFlag(TopFlagEnum.TOP_ONE.getScript());
                topPrices.add(lowest24);
            }
            log.info("[findTopPrice]24期查询,24={}", Objects.nonNull(lowest24));
        }

        log.info("[findTopPrice]topPrices={}", JSON.toJSONString(topPrices));

        // 获取置顶元素的 sellerId 列表
        List<Long> sellerIds = topPrices.stream().map(ItemSaleSellerTempVO::getSellerId).collect(Collectors.toList());

        // 过滤掉已置顶的元素
        List<ItemSaleSellerTempVO> filteredList = list.stream().filter(x -> !sellerIds.contains(x.getSellerId())).collect(Collectors.toList());

        // 插入置顶元素到列表前面
        filteredList.addAll(0, topPrices);

        // 替换原列表
        list.clear();
        list.addAll(filteredList);
        return Pair.of(lowest3, topPrices.size());
    }

    /**
     * 补充商品优惠价格
     * 
     * @param list
     * @param promotion 营销活动查询结果
     * @param seckillFilter 过滤秒杀
     * @param presaleFilter 过滤预售
     * @param reFilterList 重新组装list
     * @param
     */
    private static void supplementPromotionSummationPrice(List<ItemSaleSellerTempVO> list, PromotionSummation promotion, Boolean seckillFilter,
            Boolean presaleFilter, boolean reFilterList) {
        if (CollectionUtils.isEmpty(list) || Objects.isNull(promotion)) {
            return;
        }

        // 优惠分组
        Map<Long, ItemDividePriceDTO> promItemDivideMap = promotion.getItemDetailInfoDivides().stream()
                .collect(Collectors.toMap(PromItemDivide::getSellerId, PromItemDivide::getItemDividePrice, (existing, replacement) -> existing));

        list.forEach(n -> {
            if (promItemDivideMap.containsKey(n.getSellerId())) {
                ItemDividePriceDTO itemDividePriceDto = promItemDivideMap.get(n.getSellerId());
                n.setPromPrice(itemDividePriceDto.getTotalPromPrice());
                List<PromDivideDTO> divides = itemDividePriceDto.getDivides();
                if (CollectionUtils.isNotEmpty(divides)) {
                    Map<String, PromDivideDTO> promDivideDtoMap =
                            divides.stream().collect(Collectors.toMap(PromDivideDTO::getToolCode, Function.identity(), (existing, replacement) -> existing));

                    // 预售商品
                    PromDivideDTO yuShouPromDivideDto = promDivideDtoMap.get(PromotionToolCodeEnum.YU_SHOU.getCode());
                    if (Objects.nonNull(yuShouPromDivideDto)) {
                        Map<String, Object> extras = yuShouPromDivideDto.getExtras();
                        String pred = extras.get("pred").toString();
                        long promPrice = Long.parseLong(pred);
                        n.setPromToolCode(yuShouPromDivideDto.getToolCode());
                        n.setPresalePrice(promPrice);
                        n.setPresaleFinalPrice(itemDividePriceDto.getTotalPromPrice() - promPrice);
                    }

                    // 秒杀商品
                    PromDivideDTO miaoShaPromDivideDto = promDivideDtoMap.get(PromotionToolCodeEnum.MIAO_SHA.getCode());
                    if (Objects.nonNull(miaoShaPromDivideDto)) {
                        n.setPromToolCode(miaoShaPromDivideDto.getToolCode());
                    }
                }
            } else {
                n.setPromPrice(n.getLoanPeriodPrice());
            }
        });

        if (!reFilterList) {
            return;
        }

        log.info("[过滤秒杀or预售商家]商家数量={}, sellerId={}", list.size(),
                list.stream().map(n -> n.getSellerId().toString()).collect(Collectors.joining(",", "[", "]")));

        if (Boolean.TRUE.equals(seckillFilter) || Boolean.TRUE.equals(presaleFilter)) {
            List<ItemSaleSellerTempVO> filterList = null;
            if (Boolean.TRUE.equals(seckillFilter)) {
                filterList = list.stream().filter(n -> PromotionToolCodeEnum.MIAO_SHA.getCode().equals(n.getPromToolCode())).collect(Collectors.toList());
            }

            if (Boolean.TRUE.equals(presaleFilter)) {
                filterList = list.stream().filter(n -> PromotionToolCodeEnum.YU_SHOU.getCode().equals(n.getPromToolCode())).collect(Collectors.toList());
            }

            if (CollectionUtils.isNotEmpty(filterList)) {
                list.clear();
                list.addAll(filterList);
            }
        }
        log.info("[过滤秒杀or预售商家]结果={}, sellerId={}", list.size(), list.stream().map(n -> n.getSellerId().toString()).collect(Collectors.joining(",", "[", "]")));
    }

    /**
     * 选择lond的场景重新计算分期价格
     * 
     * @param reducePeriod
     * @param itemDetailV2Vo
     */
    private static void reducePeriodForLond(int reducePeriod, ItemDetailV2VO itemDetailV2Vo) {
        if (reducePeriod == -1) {
            return;
        }

        itemDetailV2Vo.getSaleSellerList().forEach(n -> n.setLoanPeriodType(reducePeriod));
    }

    // -----------------------------------------------------------------构造结果数据-----------------------------------------------------------------
    /**
     * 模型转换: vo对象
     * 
     * @param list 售卖商家
     * @param promotion 优惠
     * @param lowestPrice 最低价
     * @param payMode 支付方式(入参)
     * @param initLoanPeriodType 分期数(入参)
     * @return
     */
    private static ItemDetailV2VO toItemDetailV2Vo(List<ItemSaleSellerTempVO> list, PromotionSummation promotion, ItemSaleSellerTempVO lowestPrice,
            String payMode, int initLoanPeriodType) {
        ItemDetailV2VO vo = new ItemDetailV2VO();
        vo.setItemSkuPrice(toItemSkuPriceVo(lowestPrice));
        vo.setSaleSellerList(list);

        list.forEach(x -> {
            if (PayModeEnum.EAPY.getScript().equals(payMode)) {
                x.setLoanPeriodPrice(null);
                x.setLoanPeriodType(null);
                x.setTopFlag(null);
            } else if (PayModeEnum.LOAN.getScript().equals(payMode)) {
                x.setLoanPeriodType(initLoanPeriodType);
            }
        });
        return vo;
    }

    /**
     * 模型转换: 最低价
     * 
     * @param target
     * @return
     */
    private static ItemSkuPriceVO toItemSkuPriceVo(ItemSaleSellerTempVO target) {
        ItemSkuPriceVO source = new ItemSkuPriceVO();
        source.setSellerId(target.getSellerId());
        source.setSellerSkuCode(target.getSellerSkuCode());
        source.setSkuCode(target.getSkuCode());

        List<LoanPeriodDTO> priceList = target.getPriceList();
        LoanPeriodDTO minPrice = priceList.stream().min(Comparator.comparing(LoanPeriodDTO::getValue)).get();
        source.setPromPrice(target.getPromPrice());
        source.setOriginPrice(minPrice.getValue());
        source.setMinType(minPrice.getType());
        long t = target.getPromPrice() > minPrice.getValue() ? minPrice.getValue() : target.getPromPrice();
        source.setMaxPrice(Math.round((double) t / minPrice.getType()));

        source.setPresalePrice(target.getPresalePrice());
        source.setPresaleFinalPrice(target.getPresaleFinalPrice());
        source.setPromToolCode(target.getPromToolCode());
        return source;
    }

    // -----------------------------------------------------------------数组排序-----------------------------------------------------------------
    /**
     * 数组排序
     * 
     * @param list 元素集合
     * @param sortEnum 排序规则
     * @param excludeIndex 排除top元素的下标
     */
    private static void sort(List<ItemSaleSellerTempVO> list, ItemSaleSellerQuery query, int excludeIndex) {
        SaleSellerSortEnum sortEnum = getSortEnum(query);
        if (CollectionUtils.isEmpty(list) || Objects.isNull(sortEnum)) {
            return;
        }

        // 检查 excludeIndex 的安全性
        if (excludeIndex < 0 || excludeIndex > list.size()) {
            throw new IllegalArgumentException("excludeIndex must be between 0 and the size of the list.");
        }

        // 如果 excludeIndex 为 0,直接对整个列表进行排序
        if (excludeIndex == 0) {
            sortAct(list, sortEnum);
            return;
        }

        // 提取需要排序的元素,排除指定下标前的元素
        List<ItemSaleSellerTempVO> toSort = new ArrayList<>();
        if (excludeIndex < list.size()) {
            toSort.addAll(list.subList(excludeIndex, list.size())); // 复制排除下标之后的元素
        }

        // 使用 Comparator 进行排序
        sortAct(toSort, sortEnum);

        // 将排序后的元素放回原列表
        for (int i = excludeIndex; i < list.size(); i++) {
            list.set(i, toSort.get(i - excludeIndex)); // 从排序后的列表中获取元素
        }
    }

    /**
     * 获取排序
     * 
     * @param query
     * @return
     */
    private static SaleSellerSortEnum getSortEnum(ItemSaleSellerQuery query) {
        if (Boolean.TRUE.equals(query.getOrderByPriceAsc())) {
            return SaleSellerSortEnum.PRICE_ASC;
        }

        if (Boolean.TRUE.equals(query.getOrderByPriceDesc())) {
            return SaleSellerSortEnum.PIRCE_DESC;
        }
        if (Boolean.TRUE.equals(query.getOrderBySalesVolumeDesc())) {
            return SaleSellerSortEnum.TOTLE_SALE_DESC;
        }

        if (Boolean.TRUE.equals(query.getOrderByEvaluateDesc())) {
            return SaleSellerSortEnum.EVALUATE_DESC;
        }
        return SaleSellerSortEnum.PRICE_ASC;
    }

    /**
     * 真实的排序
     * 
     * @param list
     * @param sortEnum
     */
    private static void sortAct(List<ItemSaleSellerTempVO> list, SaleSellerSortEnum sortEnum) {
//        二级排序 优先评分排序 然后名称
        Comparator<ItemSaleSellerTempVO> comparator = Comparator.comparing(ItemSaleSellerTempVO::getAvgScore).thenComparing(ItemSaleSellerTempVO::getShopName);
        switch (sortEnum) {
            case PRICE_ASC:
                Collections.sort(list, Comparator.comparing(ItemSaleSellerTempVO::getPromPrice).thenComparing(comparator));
                return;
            case PIRCE_DESC:
                Collections.sort(list, Comparator.comparing(ItemSaleSellerTempVO::getPromPrice).reversed().thenComparing(comparator));
                return;
            case TOTLE_SALE_DESC:
                Collections.sort(list, Comparator.comparing(ItemSaleSellerTempVO::getTotalSaleCount).reversed().thenComparing(comparator));
                return;
            default:
                return;
        }
    }
}
