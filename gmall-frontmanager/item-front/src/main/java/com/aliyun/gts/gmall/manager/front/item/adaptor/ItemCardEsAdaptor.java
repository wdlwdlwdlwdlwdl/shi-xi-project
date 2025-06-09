package com.aliyun.gts.gmall.manager.front.item.adaptor;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.manager.framework.common.DubboBuilder;
import com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.SellerAdapter;
import com.aliyun.gts.gmall.manager.front.item.convertor.ItemCardConvertor;
import com.aliyun.gts.gmall.manager.front.item.dto.temp.ItemSaleSellerQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.temp.ItemSaleSellerTempVO;
import com.aliyun.gts.gmall.manager.utils.CacheUtils;
import com.aliyun.gts.gmall.platform.item.api.dto.output.category.CategoryDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.commercial.DeliveryTypeFullInfoDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.LoanPeriodDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuQuoteCityPriceEsDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuQuoteEsDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuQuoteWarehourseStockDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuQuoteWarehourseStockDTO2;
import com.aliyun.gts.gmall.platform.item.common.constant.ItemConstant;
import com.aliyun.gts.gmall.platform.item.common.enums.ItemPriceloanPeriodTypeEnum;
import com.aliyun.gts.gmall.platform.item.common.enums.ItemStatus;
import com.aliyun.gts.gmall.platform.item.common.enums.SkuQuoteMapEnum;
import com.aliyun.gts.gmall.platform.item.common.utils.EveDataFormatter;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerAvgScoreDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.ShopConfigDTO;
import com.aliyun.gts.gmall.searcher.common.SearchClient;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Title: ItemCardEsAdaptor.java
 * @Description: 商品卡片适配
 * @author zhao.qi
 * @date 2024年11月15日 09:55:45
 * @version V1.0
 */
@Slf4j
@Component
public class ItemCardEsAdaptor {
    private static final Cache<String, ?> itemCache = CacheUtils.defaultLocalCache(30);
    private static final String SKU_QUOTE_INDEX = "sku_quote_";

    public enum SaleSellerSortEnum {
        PRICE_ASC, PIRCE_DESC, TOTLE_SALE_DESC, EVALUATE_DESC;
    }

    @Value("${env}")
    protected String env;

    @Autowired
    private ItemCardConvertor itemCardConvertor;

    @Autowired
    private SearchClient searchClient;

    @Autowired
    private SellerAdapter sellerAdaptor;

    @Autowired
    private CommercialAdaptor commercialAdaptor;

    @Autowired
    private SkuQuoteAdaptor skuQuoteAdaptor;

    @Autowired
    private CategoryAdaptor categoryAdaptor;

    DubboBuilder itemBuilder = DubboBuilder.builder().logger(log).sysCode(FrontCommonResponseCode.ITEM_CENTER_ERROR).build();

    /**
     * 批量查询售卖商品的商家信息
     * 
     * @param query
     * @return
     */
    public List<ItemSaleSellerTempVO> queryItemSaleSeller(ItemSaleSellerQuery query) {
        log.info("[ItemCardEsAdaptor]查询售卖商家, 参数={}", JSON.toJSONString(query));

        // es数据获取
        List<SkuQuoteEsDTO> skuQuoteEsDtoList = CacheUtils.getNullableQuietly(itemCache, "SKU_SALE_SELLER_" + query.getSkuId(), loader(query));
        skuQuoteEsDtoList = filter_first(query, skuQuoteEsDtoList);
        if (CollectionUtils.isEmpty(skuQuoteEsDtoList)) {
            log.info("[ItemCardEsAdaptor]没有任何售卖商家, 退出");
            return new ArrayList<>();
        }

        List<ItemSaleSellerTempVO> voList = itemCardConvertor.toItemSaleSellerTempVos(skuQuoteEsDtoList);
        List<Long> sellerIds = voList.stream().map(ItemSaleSellerTempVO::getSellerId).collect(Collectors.toList());

        // 查询商家基本信息
        List<SellerDTO> sellerBaseInfoDtoList = sellerAdaptor.queryBaseInfoBySellerIds(sellerIds);
        supplementSellerBaseInfo(voList, sellerBaseInfoDtoList);

        // 查询店铺
        List<ShopConfigDTO> shopConfigDtoList = sellerAdaptor.queryShopBySellerIds(sellerIds);
        supplementSellerStoreName(voList, shopConfigDtoList);

        // 查询评分
        List<SellerAvgScoreDTO> sellerAvgScoreDtoList = sellerAdaptor.queryScoreBySellerIds(sellerIds);
        supplementSellerScore(voList, sellerAvgScoreDtoList);

        List<Long> skuQuoteIds = voList.stream().map(ItemSaleSellerTempVO::getSkuQuoteId).collect(Collectors.toList());
        // 查询仓储
        List<SkuQuoteWarehourseStockDTO2> skuQuoteWarehourseStockDto2List = skuQuoteAdaptor.queryStocks(skuQuoteIds);
        supplementQuoteWareHouse(voList, skuQuoteWarehourseStockDto2List);

        // 查询物流
        Long categoryId = skuQuoteEsDtoList.get(0).getCategoryId();
        CategoryDTO categoryDto = categoryAdaptor.queryCategoryById(categoryId);
        List<DeliveryTypeFullInfoDTO> deliveryTypeFullInfoDtoList =
                commercialAdaptor.querySelfDeliveryType(categoryDto.getId(), query.getSkuId(), sellerIds, skuQuoteIds, query.getCityCode());
        supplementDelivery(voList, categoryDto, deliveryTypeFullInfoDtoList);

        // 查询基本价格
        Map<Long, Pair<Long, List<LoanPeriodDTO>>> priceMap = calculatePrice(query, skuQuoteEsDtoList);

        // 组合价格 & 初始化其他属性
        voList.forEach(n -> {
            Pair<Long, List<LoanPeriodDTO>> pair = priceMap.get(n.getSkuQuoteId());
            LoanPeriodDTO loanPeriodDto = pair.getRight().stream().filter(x -> x.getType() == query.getPeriod()).findFirst().get();
            n.setLoanPeriodPrice(loanPeriodDto.getValue());
            n.setOriginPrice(loanPeriodDto.getValue());
            n.setLoanPeriodType(loanPeriodDto.getType());
            n.setPriceList(pair.getRight());
        });

        List<ItemSaleSellerTempVO> rvtList = filter_second(query, voList);
        log.info("[ItemCardEsAdaptor详]查询售卖商家, 数量={}, ids={}", rvtList.size(),
                rvtList.stream().map(n -> n.getSellerId().toString()).collect(Collectors.joining(",")));
        log.debug("[格式化输出]\n<-----商家(输出)----->\n{}", EveDataFormatter.tableDataFormat(rvtList, List.of("商家id", "价格"),
                List.of(ItemSaleSellerTempVO::getSellerId, ItemSaleSellerTempVO::getOriginPrice)));
        return rvtList;
    }

    /**
     * 查询构造器
     * 
     * @param query
     * @return
     */
    private Callable<List<SkuQuoteEsDTO>> loader(ItemSaleSellerQuery query) {
        Callable<List<SkuQuoteEsDTO>> loader = () -> {
            SearchRequest searchRequest = new SearchRequest(SKU_QUOTE_INDEX + env);
            SearchSourceBuilder ssBuilder = new SearchSourceBuilder();
            ssBuilder.size(1000);
            BoolQueryBuilder boolQuery = new BoolQueryBuilder();

            // skuId必须
            boolQuery.must(QueryBuilders.termQuery("sku_id", query.getSkuId()));

            // 商品必须是上线状态
            boolQuery.must(QueryBuilders.termQuery("item_status", ItemStatus.ENABLE.getStatus()));

            // 引用必须是map状态
            boolQuery.must(QueryBuilders.termQuery("sku_quote_map_status", SkuQuoteMapEnum.MAP.getStatus()));

            // 商家必须是正常状态
            NestedQueryBuilder sellerStatusQuery = QueryBuilders.nestedQuery("seller_info", // 嵌套字段路径
                    QueryBuilders.termQuery("seller_info.status", ItemConstant.Y), // 嵌套查询条件
                    ScoreMode.None // 在此选择合适的得分模式
            );
            boolQuery.must(sellerStatusQuery);

            // 第一个City条件: city_price_info.cityCode = query.getCityCode() 并且 onSale = 1
            BoolQueryBuilder cityCodeCondition1 = QueryBuilders.boolQuery()
                    .must(QueryBuilders.nestedQuery("city_price_info",
                            QueryBuilders.boolQuery().must(QueryBuilders.termQuery("city_price_info.cityCode", query.getCityCode()))
                                    .must(QueryBuilders.termQuery("city_price_info.onSale", ItemConstant.Y)),
                            ScoreMode.None));

            // 第二个City条件: city_price_info.cityCode = "all" 并且 onSale = 1
            BoolQueryBuilder cityCodeCondition2 = QueryBuilders.boolQuery()
                    .must(QueryBuilders.nestedQuery("city_price_info",
                            QueryBuilders.boolQuery().must(QueryBuilders.termQuery("city_price_info.cityCode", ItemConstant.CITY_CODE_ALL))
                                    .must(QueryBuilders.termQuery("city_price_info.onSale", ItemConstant.Y)),
                            ScoreMode.None));

            // 将两个条件以 OR 的方式结合起来
            boolQuery.should(cityCodeCondition1).should(cityCodeCondition2);

            ssBuilder.query(boolQuery);
            searchRequest.source(ssBuilder);
            SearchResponse result = searchClient.search(searchRequest);
            List<SkuQuoteEsDTO> skuQuoteListVoList = Arrays.stream(result.getHits().getHits()).map(SearchHit::getSourceAsString)
                    .map(n -> JSON.parseObject(n, SkuQuoteEsDTO.class)).filter(vo -> Objects.nonNull(vo.getId())).collect(Collectors.toList());
            return skuQuoteListVoList;
        };
        return loader;
    }

    // -------------------------------------------补充商家基本信息-------------------------------------------
    /**
     * 补充商家基本信息
     * 
     * @param list
     * @param sellerList
     */
    private static void supplementSellerBaseInfo(List<ItemSaleSellerTempVO> list, List<SellerDTO> sellerList) {
        log.info("[ItemCardEsAdaptor]补充商家基本信息, sellerIds={}, 目标对象有{}个", list.stream().map(n -> n.getSellerId().toString()).collect(Collectors.joining(",")),
                sellerList.size());
        log.debug("[格式化输出]\n<-----supplementSellerBaseInfo(前)----->\n{}",
                EveDataFormatter.tableDataFormat(sellerList, List.of("商家id", "ka", "op"), List.of(SellerDTO::getId, SellerDTO::getKa, SellerDTO::getOp)));
        if (CollectionUtils.isEmpty(list) || CollectionUtils.isEmpty(sellerList)) {
            return;
        }

        Map<Long, SellerDTO> sellerMap =
                sellerList.stream().collect(Collectors.toMap(SellerDTO::getId, Function.identity(), (existing, replacement) -> existing));
        list.forEach(n -> {
            if (sellerMap.containsKey(n.getSellerId())) {
                SellerDTO sellerDto = sellerMap.get(n.getSellerId());
                n.setOp(sellerDto.getOp() == ItemConstant.Y);
                n.setKa(sellerDto.getKa() == ItemConstant.Y);
                n.setBrandIds(sellerDto.getBrandIds());
            }
        });
        log.debug("[格式化输出]\n<-----supplementSellerBaseInfo(后)----->\n{}", EveDataFormatter.tableDataFormat(list, List.of("商家id", "ka", "op"),
                List.of(ItemSaleSellerTempVO::getSellerId, ItemSaleSellerTempVO::getKa, ItemSaleSellerTempVO::getOp)));
    }

    /**
     * 补充店铺名称
     * 
     * @param list
     * @param shopList
     */
    private static void supplementSellerStoreName(List<ItemSaleSellerTempVO> list, List<ShopConfigDTO> shopList) {
        log.info("[ItemCardEsAdaptor]补充商家评分, sellerIds={}, 目标对象有{}个", list.stream().map(n -> n.getSellerId().toString()).collect(Collectors.joining(",")),
                shopList.size());
        log.debug("[格式化输出]\n<-----supplementSellerStoreName(前)----->\n{}",
                EveDataFormatter.tableDataFormat(shopList, List.of("商家id", "店铺名称"), List.of(ShopConfigDTO::getSellerId, ShopConfigDTO::getName)));

        if (CollectionUtils.isEmpty(list) || CollectionUtils.isEmpty(shopList)) {
            return;
        }

        Map<Long, ShopConfigDTO> shopMap =
                shopList.stream().collect(Collectors.toMap(ShopConfigDTO::getSellerId, Function.identity(), (existing, replacement) -> existing));
        list.forEach(n -> {
            if (shopMap.containsKey(n.getSellerId())) {
                ShopConfigDTO shopConfigDto = shopMap.get(n.getSellerId());
                n.setShopName(shopConfigDto.getName());
            }
        });
        log.debug("[格式化输出]\n<-----supplementSellerStoreName(后)----->\n{}",
                EveDataFormatter.tableDataFormat(list, List.of("商家id", "店铺名称"), List.of(ItemSaleSellerTempVO::getSellerId, ItemSaleSellerTempVO::getShopName)));

    }

    /**
     * 补充店铺评分
     * 
     * @param list
     * @param scoreList
     */
    private static void supplementSellerScore(List<ItemSaleSellerTempVO> list, List<SellerAvgScoreDTO> scoreList) {
        log.info("[ItemCardEsAdaptor]补充商家评分, sellerIds={}, 目标对象有{}个", list.stream().map(n -> n.getSellerId().toString()).collect(Collectors.joining(",")),
                scoreList.size());
        log.debug("[格式化输出]\n<-----supplementSellerScore(前)----->\n{}",
                EveDataFormatter.tableDataFormat(scoreList, List.of("sellerId", "平均分", "评论总数", "订单总数", "可靠商家"),
                        List.of(SellerAvgScoreDTO::getSellerId, SellerAvgScoreDTO::getAvgScore, SellerAvgScoreDTO::getCommentCount,
                                SellerAvgScoreDTO::getOrderCount, SellerAvgScoreDTO::getHasReliableSeller)));

        if (CollectionUtils.isEmpty(list) || CollectionUtils.isEmpty(scoreList)) {
            return;
        }

        Map<Long, SellerAvgScoreDTO> scoreMap =
                scoreList.stream().collect(Collectors.toMap(SellerAvgScoreDTO::getSellerId, Function.identity(), (existing, replacement) -> existing));
        list.forEach(n -> {
            if (scoreMap.containsKey(n.getSellerId())) {
                SellerAvgScoreDTO sellerAvgScoreDto = scoreMap.get(n.getSellerId());
                if (Objects.nonNull(sellerAvgScoreDto.getAvgScore())) {
                    n.setAvgScore(sellerAvgScoreDto.getAvgScore());
                }
                if (Objects.nonNull(sellerAvgScoreDto.getCommentCount())) {
                    n.setCommentCoun(Long.valueOf(sellerAvgScoreDto.getCommentCount()));
                }
                if (Objects.nonNull(sellerAvgScoreDto.getOrderCount())) {
                    n.setOrderCount(Long.valueOf(sellerAvgScoreDto.getOrderCount()));
                }
                if (Objects.nonNull(sellerAvgScoreDto.getHasReliableSeller())) {
                    n.setHasReliableSeller(sellerAvgScoreDto.getHasReliableSeller());
                }
            }
            log.info("[ItemCardEsAdaptor]补充商家评分, sellerId={}, 平均分={}, 评论数={}, 订单数量={}", n.getSellerId(), n.getAvgScore(), n.getCommentCoun(),
                    n.getOrderCount());
        });

        log.debug("[格式化输出]\n<-----supplementSellerScore(后)----->\n{}",
                EveDataFormatter.tableDataFormat(list, List.of("sellerId", "平均分", "评论总数", "订单总数", "可靠商家"),
                        List.of(ItemSaleSellerTempVO::getSellerId, ItemSaleSellerTempVO::getAvgScore, ItemSaleSellerTempVO::getCommentCoun,
                                ItemSaleSellerTempVO::getOrderCount, ItemSaleSellerTempVO::getHasReliableSeller)));
    }

    // -------------------------------------------补充商品库存信息-------------------------------------------
    /**
     * 补充商品库存信息
     * 
     * @param list
     * @param sellerList
     */
    private static void supplementQuoteWareHouse(List<ItemSaleSellerTempVO> list, List<SkuQuoteWarehourseStockDTO2> wareHouseList) {
        log.info("[ItemCardEsAdaptor]补充商品引用库存信息, skuQuoteIds={}, 目标对象有{}个",
                list.stream().map(n -> n.getSkuQuoteId().toString()).collect(Collectors.joining(",")), wareHouseList.size());
        wareHouseList.forEach(n -> {
            log.debug("[格式化输出]\n<-----supplementQuoteWareHouse(前)----->\n{}",
                    EveDataFormatter.tableDataFormat(n.getList(), List.of("offerId", "库存id", "仓库id", "是否有库存"),
                            List.of(SkuQuoteWarehourseStockDTO::getSkuQuoteId, SkuQuoteWarehourseStockDTO::getId, SkuQuoteWarehourseStockDTO::getStockId,
                                    SkuQuoteWarehourseStockDTO::getEnable)));
        });

        if (CollectionUtils.isEmpty(list) || CollectionUtils.isEmpty(wareHouseList)) {
            return;
        }

        Map<Long, SkuQuoteWarehourseStockDTO2> wareHouseMap = wareHouseList.stream()
                .collect(Collectors.toMap(SkuQuoteWarehourseStockDTO2::getSkuQuoteId, Function.identity(), (existing, replacement) -> existing));
        list.forEach(n -> {
            if (wareHouseMap.containsKey(n.getSkuQuoteId())) {
                SkuQuoteWarehourseStockDTO2 skuQuoteWarehourseStockDto2 = wareHouseMap.get(n.getSkuQuoteId());
                n.setWarehourseStocks(skuQuoteWarehourseStockDto2.getList());
                n.setHaveInventory(skuQuoteWarehourseStockDto2.getList().stream().filter(stock -> stock.getEnable().equals(ItemConstant.Y)).count() > 0);
            }
        });

        log.debug("[格式化输出]\n<-----supplementQuoteWareHouse(后)----->\n{}", EveDataFormatter.tableDataFormat(list, List.of("offerId", "仓库数量", "是否有库存"),
                List.of(ItemSaleSellerTempVO::getSkuQuoteId, n -> n.getWarehourseStocks().size(), ItemSaleSellerTempVO::getHaveInventory)));
    }

    // -------------------------------------------补充物流信息-------------------------------------------
    /**
     * 补充物流信息
     * 
     * @param list
     * @param categoryDto 类目信息
     * @param selfDeliveryList 物流详情
     */
    private static void supplementDelivery(List<ItemSaleSellerTempVO> list, CategoryDTO categoryDto, List<DeliveryTypeFullInfoDTO> deliveryTypeFullInfoDtos) {
        log.info("[ItemCardEsAdaptor]补充物流信息, sellerIds={}, 目标对象有{}个", list.stream().map(n -> n.getSellerId().toString()).collect(Collectors.joining(",")),
                list.size());
        log.debug("[格式化输出]\n<-----supplementDelivery(前)----->\n{}",
                EveDataFormatter.tableDataFormat(deliveryTypeFullInfoDtos, List.of("sellerId", "物流覆盖", "物流时效"),
                        List.of(DeliveryTypeFullInfoDTO::getSellerId, DeliveryTypeFullInfoDTO::isDeliveryCover, DeliveryTypeFullInfoDTO::geRealTimeliness)));

        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        Map<Long, DeliveryTypeFullInfoDTO> deliveryTypeFullInfoDtoMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(deliveryTypeFullInfoDtos)) {
            deliveryTypeFullInfoDtoMap = deliveryTypeFullInfoDtos.stream()
                    .collect(Collectors.toMap(DeliveryTypeFullInfoDTO::getSellerId, Function.identity(), (existing, replacement) -> existing));
        }

        for (ItemSaleSellerTempVO vo : list) {
            DeliveryTypeFullInfoDTO deliveryTypeFullInfoDto = deliveryTypeFullInfoDtoMap.get(vo.getSellerId());
            if (Objects.nonNull(deliveryTypeFullInfoDto)) {
                vo.setHasThridHoursToReach(deliveryTypeFullInfoDto.isHoursLimit3());
                vo.setHasHomeDeliveryService(deliveryTypeFullInfoDto.getHomeDelivery());
                vo.setHasPvzPickup(deliveryTypeFullInfoDto.getHmDeliveryDto().isPvz());
                vo.setHasPostamatPickup(deliveryTypeFullInfoDto.getHmDeliveryDto().isPostamat());
                vo.setHasWarehousePickup(deliveryTypeFullInfoDto.getRealPickUp());
                vo.setDeliverDate(deliveryTypeFullInfoDto.geRealTimeliness() > 0 ? String.valueOf(deliveryTypeFullInfoDto.geRealTimeliness()) : "");
                vo.setDeliveryTimeliness(deliveryTypeFullInfoDto.geRealTimeliness());
                vo.setHasDeliveryCover(deliveryTypeFullInfoDto.isDeliveryCover());
                vo.setDeliveryType(deliveryTypeFullInfoDto.getRealDeliveryType());
                vo.setFreeDeliveryThreshold(deliveryTypeFullInfoDto.getRealShippingFreeThreshold());
                vo.setShippingFee(deliveryTypeFullInfoDto.getRealShippingFreeThreshold());
            }
        }

        log.debug("[格式化输出]\n<-----supplementDelivery(后)----->\n{}", EveDataFormatter.tableDataFormat(list, List.of("sellerId", "物流覆盖", "物流时效"),
                List.of(ItemSaleSellerTempVO::getSellerId, ItemSaleSellerTempVO::getHasDeliveryCover, ItemSaleSellerTempVO::getDeliveryTimeliness)));
    }

    // -------------------------------------------计算价格-------------------------------------------
    /**
     * 计算价格
     * 
     * @param query
     * @param list
     * @return <skuQuoteId, <基础价格, 分期价格列表>>
     */
    private static Map<Long, Pair<Long, List<LoanPeriodDTO>>> calculatePrice(ItemSaleSellerQuery query, List<SkuQuoteEsDTO> list) {
        if (list.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Long, Pair<Long, List<LoanPeriodDTO>>> priceMap = new HashMap<>();
        list.forEach(
                skuQuote -> priceMap.put(skuQuote.getId(), calculatePrice_getBasePrice(query.getPeriod(), query.getCityCode(), skuQuote.getCityPriceInfo())));
        return priceMap;
    }

    /**
     * 获取基础价格
     * 
     * @param loanPeriodType 分期数
     * @param cityPriceInfoList 分期价格列表
     * @return <基础价格, 分期价格列表>
     */
    private static Pair<Long, List<LoanPeriodDTO>> calculatePrice_getBasePrice(Integer loanPeriodType, String cityCode,
            List<SkuQuoteCityPriceEsDTO> cityPriceInfoList) {
        Optional<SkuQuoteCityPriceEsDTO> priceOpt = findPriceOption(cityCode, cityPriceInfoList);

        if (!priceOpt.isPresent()) {
            // 如果没有找到符合条件的价格,直接返回 null
            log.info("[ItemCardEsAdaptor]分期基础价格计算, 没有找到onSale价格信息.");
            return Pair.of(null, null);
        }

        SkuQuoteCityPriceEsDTO cityPrice = priceOpt.get();
        List<LoanPeriodDTO> priceList = cityPrice.getPriceList();
        Long price = null;

        if (loanPeriodType <= 0) {
            // 查找type最大的价格信息
            price = priceList.stream().max(Comparator.comparingInt(LoanPeriodDTO::getType)).map(LoanPeriodDTO::getValue).orElse(null); // 如果没有价格,则为 null
        } else {
            // 查找指定分期的价格信息
            price = priceList.stream().filter(n -> loanPeriodType.equals(n.getType())).map(LoanPeriodDTO::getValue).findFirst().orElse(null); // 如果没有命中,价格为 null
        }

        log.info("[ItemCardEsAdaptor]({})分期基础价格计算, priceOpt={}, 价格={}", loanPeriodType <= 0 ? "未指定" : "指定", JSON.toJSONString(cityPrice), price);
        return Pair.of(price, priceList);
    }

    /**
     * 查询X期最低价
     * 
     * @param list list
     * @return vo
     */
    public static ItemSaleSellerTempVO calculatePrice_pickLowest3(List<ItemSaleSellerTempVO> list) {
        return calculatePrice_pickLowest(list, ItemPriceloanPeriodTypeEnum.THREE.getValue());
    }

    public static ItemSaleSellerTempVO calculatePrice_pickLowest6(List<ItemSaleSellerTempVO> list) {
        return calculatePrice_pickLowest(list, ItemPriceloanPeriodTypeEnum.SIX.getValue());
    }

    public static ItemSaleSellerTempVO calculatePrice_pickLowest12(List<ItemSaleSellerTempVO> list) {
        return calculatePrice_pickLowest(list, ItemPriceloanPeriodTypeEnum.TWELVE.getValue());
    }

    public static ItemSaleSellerTempVO calculatePrice_pickLowest24(List<ItemSaleSellerTempVO> list) {
        return calculatePrice_pickLowest(list, ItemPriceloanPeriodTypeEnum.TWENTY_FOUR.getValue());
    }

    private static ItemSaleSellerTempVO calculatePrice_pickLowest(List<ItemSaleSellerTempVO> list, Integer loanPeriodType) {
        if (list.isEmpty()) {
            return null;
        }
        Optional<ItemSaleSellerTempVO> lowestOpt = list.stream()
                // 展开每个 ItemSaleSellerTempVO 的 priceList
                .flatMap(n -> n.getPriceList().stream().filter(price -> loanPeriodType == price.getType()) // 过滤出type
                        .map(price -> new AbstractMap.SimpleEntry<>(n, price))) // 映射为entry包含ItemSaleSellerTempVO和对应的LoanPeriodDTO
                .min((entry1, entry2) -> {
                    Long v1 = entry1.getValue().getValue(); // 获取第一个LoanPeriodDTO 的值
                    Long v2 = entry2.getValue().getValue(); // 获取第二个LoanPeriodDTO 的值
                    return Long.compare(v1, v2); // 返回比较结果
                }).map(Map.Entry::getKey);// 提取最小条目的ItemSaleSellerTempVO
        ItemSaleSellerTempVO lowest = null;
        if (lowestOpt.isPresent()) {
            lowest = ItemCardConvertor.copy(lowestOpt.get());
        }
        calculatePrice_setLoanPeriodTypeAndOriginPrice(lowest, loanPeriodType);
        log.info("[ItemCardEsAdaptor]查询基础定价最低价,loanPeriodType={}, lowest={}", loanPeriodType, JSON.toJSONString(lowest));
        return lowest;
    }

    /**
     * 设置top数据原价,分期价,分期
     * 
     * @param lowest vo
     * @param periodTyp 分期
     */
    public static void calculatePrice_setLoanPeriodTypeAndOriginPrice(ItemSaleSellerTempVO lowest, Integer periodTyp) {
        if (lowest == null || periodTyp == null) {
            return;
        }
        List<LoanPeriodDTO> priceList = lowest.getPriceList();
        List<LoanPeriodDTO> collect = priceList.stream().filter(x -> Objects.equals(x.getType(), periodTyp)).toList();
        if (CollectionUtils.isEmpty(collect)) {
            return;
        }
        LoanPeriodDTO loanPeriodDTO = collect.get(0);
        lowest.setLoanPeriodType(loanPeriodDTO.getType());
        lowest.setOriginPrice(loanPeriodDTO.getValue());
        lowest.setLoanPeriodPrice(loanPeriodDTO.getValue());
    }

    /**
     * 首次过滤, 这里只能过滤分期的数据和命中销售城市
     * 
     * @param query
     * @param list
     * @return
     */
    private static List<SkuQuoteEsDTO> filter_first(ItemSaleSellerQuery query, List<SkuQuoteEsDTO> list) {
        log.debug("[格式化输出]\n<-----filter_first.商家(过滤前)----->\n{}",
                EveDataFormatter.tableDataFormat(list, List.of("商家id"), List.of(SkuQuoteEsDTO::getSellerId)));

        List<SkuQuoteEsDTO> filterList = list;
        if (CollectionUtils.isNotEmpty(list) && query.getPeriod() > 0) {
            filterList = list.stream().filter(skuQuote -> {
                // 分期数过滤
                boolean loanPeriodCondition = true;
                if (query.getPeriod() > 0) {
                    loanPeriodCondition = filter_period(query.getPeriod(), query.getCityCode(), skuQuote.getCityPriceInfo());
                }

                // 城市价格过滤
                boolean cityPriceCondition = true;
                cityPriceCondition = filter_cityPrice(query.getCityCode(), skuQuote.getCityPriceInfo());
                return loanPeriodCondition && cityPriceCondition; // 组合条件
            }).collect(Collectors.toList());
        }
        log.info("[ItemCardEsAdaptor]首次过滤, 过滤前数据={}条, 过滤后数据={}条", list.size(), filterList.size());
        log.debug("[格式化输出]\n<-----filter_first.商家(过滤后)----->\n{}",
                EveDataFormatter.tableDataFormat(filterList, List.of("商家id"), List.of(SkuQuoteEsDTO::getSellerId)));
        return filterList;
    }

    /**
     * 首次过滤: 分期数
     * 
     * @param loanPeriod 分期数
     * @param cityCode 城市编码
     * @param cityPriceInfoList 城市价格
     * @return
     */
    private static boolean filter_period(Integer loanPeriod, String cityCode, List<SkuQuoteCityPriceEsDTO> cityPriceInfoList) {
        Optional<SkuQuoteCityPriceEsDTO> priceOpt = findPriceOption(cityCode, cityPriceInfoList);
        // 检查priceOpt是否存在并且在其priceList中是否存在符 loanPeriodType的记录
        return priceOpt.map(price -> price.getPriceList() != null && price.getPriceList().stream().anyMatch(lp -> loanPeriod.equals(lp.getType())))
                .orElse(false); // 如果finalPriceOpt不存在则返回false
    }

    /**
     * 首次过滤: 城市价 不存在, return true 存在 && OnSale=1, return true
     * 
     * @param cityCode
     * @param cityPriceInfoList
     * @return
     */
    public static boolean filter_cityPrice(String cityCode, List<SkuQuoteCityPriceEsDTO> cityPriceInfoList) {
        Optional<SkuQuoteCityPriceEsDTO> priceOpt = findPriceOption(cityCode, cityPriceInfoList);
        return priceOpt.isPresent();
    }

    /**
     * 二次过滤
     * 
     * @param query
     * @param list
     * @return
     */
    private static List<ItemSaleSellerTempVO> filter_second(ItemSaleSellerQuery query, List<ItemSaleSellerTempVO> list) {
        log.info("[ItemCardEsAdaptor]过滤, op={}, deliveredToday={}, deliveredTomorrow={}", query.getOp(), query.getDeliveredToday(),
                query.getDeliveredTomorrow());

        // 先把无库存和无物流的商家都过滤掉
        List<ItemSaleSellerTempVO> filterList = list.stream().filter(n -> Boolean.TRUE.equals(n.getHaveInventory()))
                .filter(n -> Boolean.TRUE.equals(n.getHasDeliveryCover())).collect(Collectors.toList());
        log.info("[ItemCardEsAdaptor]二次过滤, 过滤掉无库存,无物流商家={}", list.size() - filterList.size());

        if (CollectionUtils.isNotEmpty(list) && (Boolean.TRUE.equals(query.getOp()) || Boolean.TRUE.equals(query.getDeliveredToday())
                || Boolean.TRUE.equals(query.getDeliveredTomorrow()))) {
            filterList = filterList.stream().filter(vo -> {
                // op过滤
                boolean opCondition = true;
                if (Boolean.TRUE.equals(query.getOp())) {
                    opCondition = vo.getOp() && vo.getBrandIds().stream().filter(it -> it.equals(query.getBrandId())).count() > 0;
                }

                // 1天达过滤
                boolean deliveredTodayCondition = true;
                if (Boolean.TRUE.equals(query.getDeliveredToday())) {
                    deliveredTodayCondition = 0 < vo.getDeliveryTimeliness() && vo.getDeliveryTimeliness() <= 24;
                }

                // 2天达过滤
                boolean deliveredTomorrowCondition = true;
                if (Boolean.TRUE.equals(query.getDeliveredTomorrow())) {
                    deliveredTomorrowCondition = 24 < vo.getDeliveryTimeliness() && vo.getDeliveryTimeliness() <= 48;
                }
                return opCondition && deliveredTodayCondition && deliveredTomorrowCondition; // 组合条件
            }).collect(Collectors.toList());
        }
        log.info("[ItemCardEsAdaptor]二次过滤, 过滤前数据={}, 过滤后数据={}", list.size(), filterList.size());
        return filterList;
    }

    /**
     * 查询命中售卖城市的价格
     * 
     * @param cityCode 城市编码
     * @param cityPriceInfoList 城市价格
     * @return
     */
    private static Optional<SkuQuoteCityPriceEsDTO> findPriceOption(String cityCode, List<SkuQuoteCityPriceEsDTO> cityPriceInfoList) {
        Map<Boolean, List<SkuQuoteCityPriceEsDTO>> partitionedPrices = cityPriceInfoList.stream() //
                .filter(price -> price.getOnSale() == ItemConstant.Y) // 过滤出 onSale为1的价格信息
                .filter(price -> cityCode.equals(price.getCityCode()) || ItemConstant.CITY_CODE_ALL.equals(price.getCityCode())) // 查找对应城市的价格
                .collect(Collectors.partitioningBy(price -> ItemConstant.CITY_CODE_ALL.equals(price.getCityCode()))); // 分区
        List<SkuQuoteCityPriceEsDTO> allPrices = partitionedPrices.get(true); // cityCode为"all"
        List<SkuQuoteCityPriceEsDTO> specificPrices = partitionedPrices.get(false); // cityCode不为"all"

        // 尝试获取不为"all"的记录,若无,则获取 "all"
        return specificPrices.stream().findFirst().or(() -> allPrices.stream().findFirst());
    }

}
