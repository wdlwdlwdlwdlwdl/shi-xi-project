package com.aliyun.gts.gmall.manager.front.customer.adaptor;

import static com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode.ITEM_CENTER_ERROR;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.api.util.MagicgreekUtils;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.framework.common.DubboBuilder;
import com.aliyun.gts.gmall.manager.framework.common.DubboDataSource;
import com.aliyun.gts.gmall.manager.front.common.config.DatasourceConfig;
import com.aliyun.gts.gmall.manager.front.common.consts.DsIdConst;
import com.aliyun.gts.gmall.manager.utils.CacheUtils;
import com.aliyun.gts.gmall.platform.promotion.api.dto.input.PromotionQueryReq;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.display.PromotionSummation;
import com.aliyun.gts.gmall.platform.promotion.api.facade.cust.PromotionReadFacade;
import com.aliyun.gts.gmall.platform.promotion.common.model.SellerItem;
import com.aliyun.gts.gmall.platform.promotion.common.model.TargetCust;
import com.aliyun.gts.gmall.platform.promotion.common.model.TargetItem;
import com.aliyun.gts.gmall.platform.promotion.common.model.TargetItemCluster;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CommonByBatchIdsQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.input.SellerAvgScoreQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.input.ShopConfigBatchQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.input.ShopConfigQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerAvgScoreDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.ShopConfigDTO;
import com.aliyun.gts.gmall.platform.user.api.facade.SellerReadFacade;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Title: SellerAdapter.java
 * @Description: 商家适配层
 * @author zhao.qi
 * @date 2024年11月13日 22:31:41
 * @version V1.0
 */
@Slf4j
@Component
public class SellerAdapter {
    private static final Cache<String, ?> sellerCache = CacheUtils.defaultLocalCache(15);

    @Autowired
    private SellerReadFacade sellerReadFacade;

    @Autowired
    private PromotionReadFacade promotionReadFacade;

    @Autowired
    private DatasourceConfig datasourceConfig;

    private final DubboBuilder builder = DubboBuilder.builder().logger(log).sysCode(ITEM_CENTER_ERROR).build();

    /**
     * 批量查询商家基本信息
     * 
     * @param sellerIds 商家id
     * @return
     */
    public List<SellerDTO> queryBaseInfoBySellerIds(List<Long> sellerIds) {
        // 1. RPC 请求生成器
        Function<List<Long>, CommonByBatchIdsQuery> rpcReqCreator = keys -> {
            CommonByBatchIdsQuery rpcReq = new CommonByBatchIdsQuery();
            rpcReq.setIds(sellerIds);
            return rpcReq;
        };

        // 2. 查询函数
        Function<CommonByBatchIdsQuery, RpcResponse<List<SellerDTO>>> rpcFunc = sellerReadFacade::queryByIds;

        // 3. 键提供者
        Function<SellerDTO, Long> keyProvider = SellerDTO::getId;

        // 4. dubbo的数据源
        DubboDataSource dubboDataSource = builder.create(datasourceConfig).id(DsIdConst.customer_seller_base_query).queryFunc(rpcFunc).strong(Boolean.FALSE);

        // 8. 调用 getNullableQuietly 方法
        List<SellerDTO> list = CacheUtils.getNullableQuietly(sellerCache, "SELLER_", sellerIds, rpcReqCreator, keyProvider, dubboDataSource);
        log.info("[SellerAdapter]批量查询商家基本信息, sellerIds({})={}, 结果={}", JSON.toJSONString(sellerIds), sellerIds.size(), JSON.toJSONString(list));
        return list;
    }

    /**
     * 批量查询商家店铺信息
     * 
     * @param sellerIds 商家id
     * @return
     */
    public List<ShopConfigDTO> queryShopBySellerIds(List<Long> sellerIds) {
        // 1. RPC 请求生成器
        Function<List<Long>, ShopConfigBatchQuery> rpcReqCreator = keys -> {
            ShopConfigBatchQuery rpcReq = new ShopConfigBatchQuery();
            List<ShopConfigQuery> shopConfigQueryList = keys.stream().map(sellerId -> {
                ShopConfigQuery temp = new ShopConfigQuery();
                temp.setSellerId(sellerId);
                return temp;
            }).collect(Collectors.toList());
            rpcReq.setKeys(shopConfigQueryList);
            return rpcReq;
        };

        // 2. 查询函数
        Function<ShopConfigBatchQuery, RpcResponse<List<ShopConfigDTO>>> rpcFunc = sellerReadFacade::queryShops;

        // 3. 键提供者
        Function<ShopConfigDTO, Long> keyProvider = ShopConfigDTO::getSellerId;

        // 4. dubbo的数据源
        DubboDataSource dubboDataSource = builder.create(datasourceConfig).id(DsIdConst.customer_seller_shop_query).queryFunc(rpcFunc).strong(Boolean.FALSE);

        // 8. 调用 getNullableQuietly 方法
        List<ShopConfigDTO> list = CacheUtils.getNullableQuietly(sellerCache, "SHOP_", sellerIds, rpcReqCreator, keyProvider, dubboDataSource);
        log.info("[SellerAdapter]批量查询商家店铺信息, sellerIds({})={}, 结果={}", JSON.toJSONString(sellerIds), sellerIds.size(), JSON.toJSONString(list));
        return list;
    }

    /**
     * 批量查询商家店铺评分
     *
     * @param sellerIds 商家ids
     * @return
     */
    public List<SellerAvgScoreDTO> queryScoreBySellerIds(List<Long> sellerIds) {
        // 1. RPC 请求生成器
        Function<List<Long>, SellerAvgScoreQuery> rpcReqCreator = keys -> {
            SellerAvgScoreQuery rpcReq = new SellerAvgScoreQuery();
            rpcReq.setSellerIds(keys);
            return rpcReq;
        };

        // 2. 查询函数
        Function<SellerAvgScoreQuery, RpcResponse<List<SellerAvgScoreDTO>>> rpcFunc = sellerReadFacade::querySellerAvgScore;

        // 3. 键提供者
        Function<SellerAvgScoreDTO, Long> keyProvider = SellerAvgScoreDTO::getSellerId;

        // 4. dubbo的数据源
        DubboDataSource dubboDataSource = builder.create(datasourceConfig).id(DsIdConst.customer_seller_score_query).queryFunc(rpcFunc).strong(Boolean.FALSE);

        // 8. 调用 getNullableQuietly 方法
        List<SellerAvgScoreDTO> list = CacheUtils.getNullableQuietly(sellerCache, "SHOP_SCORE_", sellerIds, rpcReqCreator, keyProvider, dubboDataSource);
        log.info("[SellerAdapter]批量查询商家店铺评分, sellerIds({})={}, 结果={}", JSON.toJSONString(sellerIds), sellerIds.size(), JSON.toJSONString(list));
        return list;
    }

    /**
     * 查询折扣价
     * 
     * @param cityCode cityCode 城市code
     * @param itemId itemId
     * @param skuId skuId
     * @param period
     * @param map <sellerId, price> 商家ID，价格
     * @param user user 当前登录人
     * @return PromotionSummation
     */
    public PromotionSummation queryPromotionSummation(String cityCode, Long itemId, Long skuId, Integer period, Map<Long, Long> map, CustDTO user) {
        if (map == null) {
            return null;
        }
        Callable<PromotionSummation> loader = () -> {
            PromotionQueryReq req = buildPromotionQueryReq(itemId, skuId, map, user);
            return builder.create(datasourceConfig).id(DsIdConst.item_promotion_query)
                    .queryFunc((Function<PromotionQueryReq, ?>) promotionReadFacade::queryPromotionSummation).strong(Boolean.FALSE).query(req);
        };
        return CacheUtils.getNullableQuietly(sellerCache, MagicgreekUtils.strJoin("SELLER_PROMOTION_PRICE", "_", cityCode, itemId, skuId, period), loader);
    }

    /**
     * 单个商家 折扣价获取
     * 
     * @param cityCode cityCode
     * @param itemId itemId
     * @param skuId skuId
     * @param sellerId sellerId
     * @param price price
     * @return PromotionSummation
     */
    public PromotionSummation queryPromotionSummationLowest(String cityCode, Long itemId, Long skuId, Long sellerId, Long price, CustDTO user) {
        if (sellerId == null || price == null) {
            return null;
        }
        Map<Long, Long> map = new HashMap<>();
        map.put(sellerId, price);
        Callable<PromotionSummation> loader = () -> {
            PromotionQueryReq req = buildPromotionQueryReq(itemId, skuId, map, user);
            return builder.create(datasourceConfig).id(DsIdConst.item_promotion_query)
                    .queryFunc((Function<PromotionQueryReq, ?>) promotionReadFacade::queryPromotionSummation).strong(Boolean.FALSE).query(req);
        };
        return CacheUtils.getNullableQuietly(sellerCache,
                MagicgreekUtils.strJoin("SELLER_PROMOTION_PRICE_SELLER_ID", "_", cityCode, itemId, skuId, sellerId, price), loader);
    }

    private PromotionQueryReq buildPromotionQueryReq(Long itemId, Long skuId, Map<Long, Long> map, CustDTO custDto) {
        PromotionQueryReq promotionQueryReq = new PromotionQueryReq();
        promotionQueryReq.setSkuSellerIds(map.keySet());
        List<TargetItemCluster> itemClusters = new ArrayList<>();
        TargetItemCluster targetItemCluster = new TargetItemCluster();
        List<TargetItem> targetItems = new ArrayList<>();
        TargetItem targetItem = new TargetItem();
        targetItem.setItemId(itemId);
        targetItem.setSkuId(skuId);
        targetItem.setItemQty(1);

        List<SellerItem> sellerItems = new ArrayList<>();
        for (Long sellerId : map.keySet()) {
            Long price = map.get(sellerId);
            SellerItem sellerItem = new SellerItem();
            sellerItem.setSellerId(sellerId);
            sellerItem.setOrigPrice(price);
            sellerItems.add(sellerItem);
        }
        targetItem.setSellerItems(sellerItems);

        targetItems.add(targetItem);
        targetItemCluster.setTargetItems(targetItems);
        itemClusters.add(targetItemCluster);
        promotionQueryReq.setItemClusters(itemClusters);
        promotionQueryReq.setBizType("itemDetail");
        if (Objects.nonNull(custDto)) {
            TargetCust targetCust = new TargetCust();
            targetCust.setCustId(custDto.getCustId());
            promotionQueryReq.setCust(targetCust);
        }
        return promotionQueryReq;
    }
}
