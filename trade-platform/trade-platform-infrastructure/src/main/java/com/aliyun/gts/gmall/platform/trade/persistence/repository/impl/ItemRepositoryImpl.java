
package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.config.I18NConfig;
import com.aliyun.gts.gmall.framework.convert.MultiLangConverter;
import com.aliyun.gts.gmall.framework.i18n.LangText;
import com.aliyun.gts.gmall.framework.i18n.LangValue;
import com.aliyun.gts.gmall.framework.i18n.MultiLangValue;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.item.api.dto.input.category.CategoryCommissionInfoQueryReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.category.CategoryQueryByIdReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.commercial.DeliveryTypeFullInfoQueryReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.item.ItemBatchQueryReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.item.ItemQueryReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.item.SkuQuoteQueryCacheReq;
import com.aliyun.gts.gmall.platform.item.api.dto.output.category.CategoryCommissionDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.category.CategoryDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.commercial.DeliveryTypeFullInfoDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.*;
import com.aliyun.gts.gmall.platform.item.api.enums.CategoryCommissionSourceTypeEnum;
import com.aliyun.gts.gmall.platform.item.api.enums.WarehouseStatusEnum;
import com.aliyun.gts.gmall.platform.item.api.facade.category.CategoryCommissionReadFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.category.CategoryReadFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.commercial.CommercialReadFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.item.ItemReadFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.skuquote.SkuQuoteReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonConstant;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.PayModeCode;
import com.aliyun.gts.gmall.platform.trade.common.constants.DeliveryTypeEnum;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemCategory;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemDelivery;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.ItemPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Seller;
import com.aliyun.gts.gmall.platform.trade.domain.repository.ItemRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.CommUtils;
import com.aliyun.gts.gmall.platform.trade.persistence.rpc.converter.ItemRpcConverter;
import com.aliyun.gts.gmall.platform.trade.persistence.rpc.util.RpcUtils;
import com.aliyun.gts.gmall.platform.trade.persistence.util.TradeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "trade", name = "itemRepository", havingValue = "default", matchIfMissing = true)
public class ItemRepositoryImpl implements ItemRepository {

    @Value("${trade.item.useItemCache:true}")   // 仅对商品信息走缓存, SKU不走缓存
    private boolean useItemCache;

    @Value("${trade.item.useCategoryCache:true}")
    private boolean useCategoryCache;

    @Resource
    private I18NConfig i18NConfig;

    @Autowired
    private ItemRpcConverter itemRpcConverter;
    @Autowired
    protected MultiLangConverter multiLangConverter;

    @Autowired
    private ItemReadFacade itemReadFacade;
    @Autowired
    private SkuQuoteReadFacade skuQuoteReadFacade;
    @Autowired
    private CategoryReadFacade categoryReadFacade;
    @Autowired
    private CommercialReadFacade commercialReadFacade;
    @Autowired
    private CategoryCommissionReadFacade categoryCommissionReadFacade;

    @Override
    public ItemSku queryItem(ItemSkuId id) {
        ItemDTO item = useItemCache ?
            querySimpleItemWithCache(id.getItemId(), false) :
            querySimpleItem(id.getItemId(), false) ;
        if (item == null) {
            return null;
        }
        SkuDTO sku = querySimpleSkuByItem(id.getSkuId(),item);
        if (Objects.isNull(sku)) {
            return null;
        }
        if (!checkSkuItemRel(sku, item)) {
            return null;
        }
        return itemRpcConverter.toItemSku(sku, item);
    }

    private SkuDTO querySimpleSkuByItem(Long skuId, ItemDTO item) {
        if(Objects.isNull(item) || CollectionUtils.isEmpty(item.getSkuList())){
            return null;
        }
        return item.getSkuList().stream().filter(sku -> Objects.equals(sku.getId(), skuId)).findFirst().orElse(null);

    }

    /**
     * 参数判断
     * 商品 & SKU都存在 ，且 SKU的商品ID和商品ID相同
     * @param sku
     * @param item
     * @return
     */
    private boolean checkSkuItemRel(SkuDTO sku, ItemDTO item) {
        return sku != null &&
            item != null &&
            sku.getItemId() != null &&
            item.getId() != null &&
            sku.getItemId().longValue() == item.getId().longValue();
    }

    @Override
    public ItemSku queryItemRequired(ItemSkuId id) {
        ItemSku sku = queryItem(id);
        if (sku == null) {
            throw new GmallException(OrderErrorCode.ITEM_NOT_EXISTS);
        }
        return sku;
    }

    /**
     * 批量查询商品，有商品不存在时全部报错 (不含类目信息)
     * @param itemSkuIds
     */
    @Override
    public List<ItemSku> queryItemsRequired(List<ItemSkuId> itemSkuIds) {
        // 商品为空
        if (CollectionUtils.isEmpty(itemSkuIds)) {
            return new ArrayList<>();
        }
        // 查询商品信息
        List<ItemSku> list = queryItems(itemSkuIds);
        if (!Objects.equals(list.size(), itemSkuIds.size())) {
            log.error("queryItemsRequired in: {}, out: {}", itemSkuIds, list);
            throw new GmallException(OrderErrorCode.ITEM_NOT_EXISTS);
        }
        return list;
    }

    /**
     * 查询商品信息
     *    目前是分布分多次查询 后面合并为一个接口
     * @param itemSkuIds
     * @return
     */
    @Override
    public List<ItemSku> queryItems(List<ItemSkuId> itemSkuIds) {
        List<ItemSku> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(itemSkuIds)) {
            return result;
        }
        // 获取商品ID和SKUID集合
        Set<Long> itemIds = new HashSet<>();
        Set<Long> skuIds = new HashSet<>();
        for (ItemSkuId id : itemSkuIds) {
            itemIds.add(id.getItemId());
            skuIds.add(id.getSkuId());
        }
        // 查询商品
        List<ItemDTO> itemList = useItemCache ? querySimpleItemsWithCache(itemIds) : querySimpleItems(itemIds);
        // 查询SKU
        List<SkuDTO> skuList = querySimpleSkus(itemList);
        // 数据解析
        Map<Long, ItemDTO> itemMap = CommUtils.toMap(itemList, ItemDTO::getId);
        Map<Long, SkuDTO> skuMap = CommUtils.toMap(skuList, SkuDTO::getId);
        ItemSku itemSku = null;
        ItemDTO itemDTO = null;
        SkuDTO skuDTO = null;
        // 解析每个入参数据
        for (ItemSkuId itemSkuId : itemSkuIds) {
            //商品存在
            itemDTO = itemMap.get(itemSkuId.getItemId());
            //SKU存在
            skuDTO = skuMap.get(itemSkuId.getSkuId());
            // 不可为空 且SKU和商品绑定
            if (Objects.isNull(skuDTO) || Objects.isNull(itemDTO)) {
                continue;
            }
            if (!checkSkuItemRel(skuDTO, itemDTO)) {
                continue;
            }
            // 数据转换
            itemSku = itemRpcConverter.toItemSku(skuDTO, itemDTO);
            //拿出最底层的category组装itemCategory
            if (CollectionUtils.isNotEmpty(itemDTO.getCategoryList())) {
                Optional<CategoryDTO> optionalCategoryDTO = itemDTO.getCategoryList()
                    .stream()
                    .filter(itemCategory -> itemCategory.getLeafYn())
                    .findFirst();
                if (optionalCategoryDTO.isPresent()) {
                    CategoryDTO categoryDTO = optionalCategoryDTO.get();
                    ItemCategory itemCategory = new ItemCategory();
                    itemCategory.setId(categoryDTO.getId());
                    itemCategory.setStatus(categoryDTO.getStatus());
                    itemCategory.setParentId(categoryDTO.getParentId());
                    itemCategory.setLeafYn(categoryDTO.getLeafYn());
                    itemCategory.setFeatureMap(categoryDTO.getFeatureMap());
                    if (Objects.nonNull(categoryDTO.getCategoryDelivery()) && Objects.nonNull(categoryDTO.getCategoryDelivery().getOrderMergeAble()) && 1 == categoryDTO.getCategoryDelivery().getOrderMergeAble()) {
                        itemCategory.setThirdCategorySplitOrder(true);
                        categoryDTO.setThirdCategorySplitOrder(true);
                    } else {
                        itemCategory.setThirdCategorySplitOrder(false);
                        categoryDTO.setThirdCategorySplitOrder(false);
                    }
                    Optional<LangText> optionalLangText = categoryDTO.getName().getLangTextByLang(LocaleContextHolder.getLocale().getLanguage());
                    if (optionalLangText.isPresent()) {
                        itemCategory.setName(optionalLangText.get().getValue());
                    }
                    itemSku.setItemCategory(itemCategory);
                    itemSku.setCategoryId(categoryDTO.getId());
                    // 分类年龄限制
                    itemSku.setAgeCategory(Objects.equals(categoryDTO.getAge21(), CommonConstant.AGE));
                }
            }
            Seller seller = new Seller();
            seller.setSellerId(itemSkuId.getSellerId());
            itemSku.setSeller(seller);
            // 计算后续的流程
            buildItemSkuEx(itemSkuId, itemSku);
            if(null != itemSkuId.getDeliveryType()){
                itemSku.setDeliveryType(itemSkuId.getDeliveryType());
            }
            if(null != itemSkuId.getSkuQuoteId()){
                itemSku.setSkuQuoteId(itemSkuId.getSkuQuoteId());
            }
            result.add(itemSku);
        }
        return result;
    }

    /**
     * 增加价格设置
     *   TODO 以下多个操作 最后个合并成一个批量处理接口
     * @param itemSkuId
     * @param itemSku
     * 2024-11-29 15:37:56
     */
    private void buildItemSkuEx(ItemSkuId itemSkuId, ItemSku itemSku) {
        log.info("buildItemSkuEx start itemSkuId={}，itemSku={}", itemSkuId, itemSku);
        Map<String, Integer> categoryCommissionMap = new HashMap<>();
        // 多佣金费率查询
        CategoryCommissionInfoQueryReq categoryCommissionInfoQueryReq = new CategoryCommissionInfoQueryReq();
        categoryCommissionInfoQueryReq.setSellerId(itemSkuId.getSellerId());
        categoryCommissionInfoQueryReq.setCategoryId(itemSku.getCategoryId());
        categoryCommissionInfoQueryReq.setSourceType(CategoryCommissionSourceTypeEnum.SELLER_KA.getCode());
        RpcResponse<CategoryCommissionDTO> categoryCommissionDTORpcResponse =  categoryCommissionReadFacade.queryCurrentEffect(categoryCommissionInfoQueryReq);
        if (Objects.nonNull(categoryCommissionDTORpcResponse) &&
            Boolean.TRUE.equals(categoryCommissionDTORpcResponse.isSuccess()) &&
            Objects.nonNull(categoryCommissionDTORpcResponse.getData())) {
            categoryCommissionMap.put(PayModeCode.INSTALLMENT_3.getCode() , categoryCommissionDTORpcResponse.getData().getInstallmentPlan3());
            categoryCommissionMap.put(PayModeCode.INSTALLMENT_6.getCode() , categoryCommissionDTORpcResponse.getData().getInstallmentPlan6());
            categoryCommissionMap.put(PayModeCode.INSTALLMENT_12.getCode() , categoryCommissionDTORpcResponse.getData().getInstallmentPlan12());
            categoryCommissionMap.put(PayModeCode.INSTALLMENT_24.getCode() , categoryCommissionDTORpcResponse.getData().getInstallmentPlan24());
            categoryCommissionMap.put(PayModeCode.EPAY.getCode(), categoryCommissionDTORpcResponse.getData().getCard());
            //借贷佣金只有一个
            categoryCommissionMap.put(PayModeCode.LOAN.getCode(), categoryCommissionDTORpcResponse.getData().getCredit());
        }
        itemSku.setCategoryCommissionMap(categoryCommissionMap);

        // 对象转换
        SkuQuoteQueryCacheReq req = itemRpcConverter.toSkuQuote(itemSkuId);
        // step1 查询quote数据
        RpcResponse<SkuQuoteDTO> response = skuQuoteReadFacade.queryCache(req);
        SkuQuoteDTO skuQuoteResult = response.getData();
        if (Objects.isNull(skuQuoteResult)) {
            itemSku.setCityPriceStatus(Boolean.FALSE);
            log.error("skuQuoteResult is null, itemSkuId:{}", itemSkuId);
            return;
        }
        itemSku.setMerchantSkuCode(skuQuoteResult.getSellerSkuCode());
        // 贷款周期计算
        itemSku.setSingleInstallment(skuQuoteResult.getLoanPeriodType());
        // SKU 图片
        if(Objects.nonNull(skuQuoteResult.getSkuPictureList())){
            itemSku.setSkuPic(toSkuPicture(skuQuoteResult.getSkuPictureList()));
        }
        // 发货仓库信息 正常必须存在
        List<SkuQuoteWarehourseStockDTO> stockList = skuQuoteResult.getStockList();
        if(CollectionUtils.isNotEmpty(stockList)){
            itemSku.setStockList(stockList.stream()
                .filter(skuQuoteWarehourseStockDTO -> WarehouseStatusEnum.ACTIVATE.getCode().equals(skuQuoteWarehourseStockDTO.getEnable()))
                .collect(Collectors.toList())
            );
            List<Long> warehouseIdList = stockList.stream()
                .filter(skuQuoteWarehourseStockDTO -> WarehouseStatusEnum.ACTIVATE.getCode().equals(skuQuoteWarehourseStockDTO.getEnable()))
                .map(SkuQuoteWarehourseStockDTO::getStockId)
                .toList();
            itemSku.setWarehouseIdList(warehouseIdList);
        }

        // 城市价格计算
        List<SkuQuoteCityPriceDTO> skuQuoteCityPriceDTOs = skuQuoteResult.getInstallmentPriceList();
        if (CollectionUtils.isEmpty(skuQuoteCityPriceDTOs)) {
            itemSku.setCityPriceStatus(Boolean.FALSE);
            log.error("skuQuoteCityPriceDTOs is null, itemSkuId:{}", itemSkuId);
            return;
        }
        SkuQuoteCityPriceDTO skuQuoteCityPrice = TradeUtils.converItemCityPrice(skuQuoteCityPriceDTOs, itemSkuId.getCityCode());
        // 价格获取
        if (Objects.isNull(skuQuoteCityPrice)) {
            itemSku.setCityPriceStatus(Boolean.FALSE);
            log.error("skuQuoteCityPrice is null, itemSkuId:{}", itemSkuId);
            return;
        }
        List<LoanPeriodDTO> priceList = skuQuoteCityPrice.getPriceList();
        if (CollectionUtils.isEmpty(priceList)) {
            itemSku.setCityPriceStatus(Boolean.FALSE);
            log.error("priceList is null, itemSkuId:{}", itemSkuId);
            return;
        }
        itemSku.setPriceList(priceList);
        // sku名称
        itemSku.setSkuName(multiLangConverter.mText_to_str(skuQuoteResult.getSkuName()));
        // 价格区间
        List<Integer> installment = priceList.stream().map(LoanPeriodDTO::getType).toList();
        itemSku.setInstallment(installment);
        // 获取支付方式 ，根据支付方式 读取价格
        PayModeCode payModeCode = PayModeCode.codeOf(itemSkuId.getPayMode());
        if (payModeCode != null) {
            //价格匹配,  根据支付方式 获取价格！
            LoanPeriodDTO loanPeriodDTO = itemSku.getPriceList()
                    .stream()
                    .filter(price -> payModeCode.getPeriodNumber().equals(price.getType()))
                    .findFirst()
                    .orElse(null);
            if (Objects.nonNull(loanPeriodDTO)) {
                ItemPrice itemPrice = new ItemPrice();
                itemPrice.setItemPrice(loanPeriodDTO.getValue());
                itemPrice.setOriginPrice(loanPeriodDTO.getValue());
                itemSku.setItemPrice(itemPrice);
                if(payModeCode.getCode().startsWith(PayModeCode.LOAN.getCode()))
                {
                    itemSku.setCategoryCommissionRate(categoryCommissionMap.getOrDefault(PayModeCode.LOAN.getCode(), CommonConstant.DEFAULT_RATE));
                }
                else {
                    itemSku.setCategoryCommissionRate(categoryCommissionMap.getOrDefault(payModeCode.getCode(), CommonConstant.DEFAULT_RATE));
                }
            }
        } else {
            ItemPrice itemPrice = new ItemPrice();
            priceList.stream().filter(q -> CommonConstant.DEFAULT_INSTALL.equals(q.getType()))
                    .findFirst()
                    .ifPresent(loanPeriodDTO -> itemSku.setSingleLoan(loanPeriodDTO.getValue()));
            priceList.stream().filter(q -> CommonConstant.DEFAULT_INSTALL.equals(q.getType()))
                    .findFirst()
                    .ifPresent(loanPeriodDTO -> itemPrice.setOriginPrice(loanPeriodDTO.getValue()));
            itemSku.setItemPrice(itemPrice);
        }
        if (Objects.isNull(itemSku.getItemPrice()) || Objects.isNull(itemSku.getItemPrice().getOriginPrice())) {
            itemSku.setCityPriceStatus(Boolean.FALSE);
            log.error("itemPrice is null, itemSkuId:{}", itemSkuId);
        }


        // step2 查询商品信息
//        ItemQueryReq reqEx = new ItemQueryReq();
//        reqEx.setItemId(itemSkuId.getItemId());
//        RpcResponse<ItemDTO> resp = RpcUtils.invokeRpc(
//            () -> itemReadFacade.queryCacheItem(reqEx),
//"itemReadFacade.queryCacheItems",
//            I18NMessageUtils.getMessage("product.cache.query"),
//            reqEx
//        );
//        ItemDTO resultEx = resp.getData();
//        log.info("queryCacheItem resp ItemDTO {}",resultEx);
//        itemSku.setCategoryId(resultEx.getCategoryId());

        // step3 查询商品分类信息
        CategoryQueryByIdReq categoryQueryByIdReq = new CategoryQueryByIdReq();
        categoryQueryByIdReq.setId(itemSku.getCategoryId());
        RpcResponse<CategoryDTO> categoryQueryResult = categoryReadFacade.queryById(categoryQueryByIdReq);
        CategoryDTO categoryDTO = categoryQueryResult.getData();
        if(null != categoryDTO && null != categoryDTO.getCategoryDelivery()){
            itemSku.setWeight(Long.valueOf(categoryDTO.getCategoryDelivery().getWeight()));
            itemSku.setCanRefunds(categoryDTO.getCategoryDelivery().getRefundable());
        }

        // step4 查询商品支出的物流方式
        DeliveryTypeFullInfoQueryReq deliveryTypeFullInfoQueryReq = new DeliveryTypeFullInfoQueryReq();
        deliveryTypeFullInfoQueryReq.setSkuId(itemSkuId.getSkuId());
        List<Long> sellerIds = new ArrayList<>();
        sellerIds.add(itemSkuId.getSellerId());
        deliveryTypeFullInfoQueryReq.setSellerIds(sellerIds);
        deliveryTypeFullInfoQueryReq.setCityCode(itemSkuId.getCityCode());
        deliveryTypeFullInfoQueryReq.setCategoryId(itemSku.getCategoryId());
        List<Long> skuQuoteIds = new ArrayList<>();
        skuQuoteIds.add(itemSkuId.getSkuQuoteId());
        deliveryTypeFullInfoQueryReq.setSkuQuoteIds(skuQuoteIds);
        deliveryTypeFullInfoQueryReq.setQueryTimeliness(true);
        RpcResponse<List<DeliveryTypeFullInfoDTO>> deliveryTypeResult = commercialReadFacade.queryDeliveryTypeFullInfo(deliveryTypeFullInfoQueryReq);
        // 解析结果 获取支持的物流方式和运费
        List<String> supportDeliveryList = new ArrayList<>();
        List<ItemDelivery> itemDeliveryList = new ArrayList<>();
        if (Objects.nonNull(deliveryTypeResult) &&
            deliveryTypeResult.isSuccess() &&
            CollectionUtils.isNotEmpty(deliveryTypeResult.getData())) {
            for (DeliveryTypeFullInfoDTO deliveryTypeFullInfo : deliveryTypeResult.getData()) {
                // 赋值
                if (Objects.isNull(deliveryTypeFullInfo)) {
                    continue;
                }
                List<DeliveryTypeEnum> deliveryTypeEnums = TradeUtils.converDeliveryType(deliveryTypeFullInfo);
                if (CollectionUtils.isEmpty(deliveryTypeEnums)) {
                    continue;
                }
                for(DeliveryTypeEnum deliveryTypeEnum : deliveryTypeEnums) {
                    ItemDelivery itemDelivery = new ItemDelivery();
                    itemDelivery.setDeliveryType(deliveryTypeEnum.getCode());
                    itemDelivery.setDeliveryTypeName(deliveryTypeEnum.getScript());
                    itemDelivery.setDeliverHours(deliveryTypeFullInfo.geRealTimeliness());
                    itemDelivery.setDeliverTime(DateUtils.addHours(new Date(), deliveryTypeFullInfo.geRealTimeliness()));
                    itemDeliveryList.add(itemDelivery);
                    supportDeliveryList.add(deliveryTypeEnum.getScript());
                }
                log.info("supportDeliveryList = {} , itemDeliveryList = {}", JSON.toJSONString(supportDeliveryList), JSON.toJSONString(itemDeliveryList));
            }
            itemSku.setItemDelivery(itemDeliveryList);
            itemSku.setSupportDeliveryList(supportDeliveryList);
        }

        log.info("buildItemSkuEx end temSku={}", JSON.toJSONString(itemSku));
    }


    /**
     * sku图片
     * @param picture
     * @return
     */
    protected String toSkuPicture(MultiLangValue<LangValue<List<String>>, List<String>> picture) {
        if(Objects.isNull(picture))
        {
            return null;
        }
        List<String> pics = picture.getValueByLang(LocaleContextHolder.getLocale().getLanguage(), i18NConfig.getDefaultLang());
        if (pics != null && pics.get(0) != null) {
            return pics.get(0);
        }
        return null;
    }


    @Override
    public List<ItemSku> queryItemsByItemId(Long itemId) {
        ItemDTO item = querySimpleItem(itemId, true) ;
        if (item == null || item.getSkuList() == null) {
            return null;
        }
        List<ItemSku> result = new ArrayList<>();
        for (SkuDTO sku : item.getSkuList()) {
            if (!checkSkuItemRel(sku, item)) {
                continue;
            }
            ItemSku itemSku = itemRpcConverter.toItemSku(sku, item);
            result.add(itemSku);
        }
        return result;

    }

    /**
     * 批量查询，返回存在的商品（走IC缓存的接口） (不含类目信息)
     */
    @Override
    public List<ItemSku> queryItemsToCartFromCache(List<ItemSkuId> itemSkuIds) {
        if (CollectionUtils.isEmpty(itemSkuIds)) {
            log.info("queryItemsFromCache idList is null");
            return new ArrayList<>();
        }
        ItemBatchQueryReq batchReq = new ItemBatchQueryReq();
        itemSkuIds.stream().map(id -> id.getItemId()).distinct().forEach(itemId -> {
            ItemQueryReq req = new ItemQueryReq();
            req.setItemId(itemId);
            req.setWithSku(true);
            req.setWithExtend(true);
            req.setWithCategory(true);
            batchReq.add(req);
        });
        RpcResponse<List<ItemDTO>> resp = RpcUtils.invokeRpc(
                () -> itemReadFacade.queryCacheItems(batchReq),
                "itemReadFacade.queryCacheItems",
                I18NMessageUtils.getMessage("product.cache.query"),
                batchReq
        );
        //# "商品缓存查询"
        log.info("queryCacheItems resp ItemDTO size={}", resp.getData().size());
        // 商品MAP
        Map<Long, ItemDTO> itemMap = new HashMap<>();
        // SKUMAP
        Map<Long, SkuDTO> skuMap = new HashMap<>();
        for (ItemDTO item : resp.getData()) {
            itemMap.put(item.getId(), item);
            for (SkuDTO sku : item.getSkuList()) {
                skuMap.put(sku.getId(), sku);
            }
        }
        // 遍历每个入参商品
        return itemSkuIds.stream().map(itemSkuId -> {
                    ItemDTO item = itemMap.get(itemSkuId.getItemId());
                    SkuDTO sku = skuMap.get(itemSkuId.getSkuId());
                    if (!checkSkuItemRel(sku, item)) {
                        return null;
                    }
                    ItemSku itemSku = itemRpcConverter.toItemSku(sku, item);
                    // 读取分类设置的年龄限制
                    if (CollectionUtils.isNotEmpty(item.getCategoryList())) {
                        if (Boolean.TRUE.equals(item.getCategoryList().stream()
                                .allMatch(category -> Objects.equals(category.getAge21(), CommonConstant.AGE)))) {
                            itemSku.setAgeCategory(Boolean.TRUE);
                        }
                    }
                    // 卖家信息
                    Seller seller = new Seller();
                    seller.setSellerId(itemSkuId.getSellerId());
                    itemSku.setSeller(seller);
                    // 构建缺少的信息
                    buildCartItemSkuEx(itemSkuId, itemSku);
                    //过滤 购物车物流时效只展示PVZ和Postamat两种物流时效
                    List<ItemDelivery> deliveries = itemSku.getItemDelivery();
                    List<ItemDelivery> resultDeliveries = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(deliveries)) {
                        resultDeliveries = deliveries.stream().filter(delivery -> DeliveryTypeEnum.PVZ.getCode().equals(delivery.getDeliveryType()) || DeliveryTypeEnum.POSTAMAT.getCode().equals(delivery.getDeliveryType())).collect(Collectors.toList());
                        // 将过滤后的列表设置回 itemSku 中
                        itemSku.setItemDelivery(resultDeliveries);
                    }
                    return itemSku;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 增加价格设置
     * @param itemSkuId
     * @param itemSku
     * 2024-11-29 15:37:56
     */
    private void buildCartItemSkuEx(ItemSkuId itemSkuId, ItemSku itemSku) {
        log.info("buildItemSkuEx start itemSkuId={}，itemSku={}",itemSkuId, itemSku);
        // step1 查询卖家和sku绑定关系
        SkuQuoteQueryCacheReq req = itemRpcConverter.toSkuQuote(itemSkuId);
        RpcResponse<SkuQuoteDTO> response = skuQuoteReadFacade.queryCache(req);
        SkuQuoteDTO skuQuoteResult = response.getData();
        if(response.isSuccess() && Objects.nonNull(response.getData())) {
            // 加个区间
            List<SkuQuoteCityPriceDTO> skuQuoteCityPriceDTOs = skuQuoteResult.getInstallmentPriceList();
            if (CollectionUtils.isNotEmpty(skuQuoteCityPriceDTOs)) {
                // 卖家sku售卖状态
                itemSku.setSkuStatus(skuQuoteResult.getMapStatus());
                // sku名称
                itemSku.setSkuName(multiLangConverter.mText_to_str(skuQuoteResult.getSkuName()));
                // sku图片处理 取第一个
                itemSku.setSkuPic(toSkuPicture(skuQuoteResult.getSkuPictureList()));
                // 取城市价格
                SkuQuoteCityPriceDTO skuQuoteCityPrice = TradeUtils.converItemCityPrice(skuQuoteCityPriceDTOs, itemSkuId.getCityCode());
                if (Objects.nonNull(skuQuoteCityPrice)) {
                    List<LoanPeriodDTO> priceList = skuQuoteCityPrice.getPriceList();
                    if (CollectionUtils.isNotEmpty(priceList)) {
                        itemSku.setPriceList(priceList);
                    }
                }
            }
        }
        // step2 物流对接
        // 查询商品类型支持的物流方式和物流时效
        DeliveryTypeFullInfoQueryReq deliveryTypeFullInfoQueryReq = new DeliveryTypeFullInfoQueryReq();
        deliveryTypeFullInfoQueryReq.setSkuId(itemSkuId.getSkuId());
        // 卖家信息
        List<Long> sellerIds = new ArrayList<>();
        sellerIds.add(itemSkuId.getSellerId());
        deliveryTypeFullInfoQueryReq.setSellerIds(sellerIds);
        deliveryTypeFullInfoQueryReq.setCityCode(itemSkuId.getCityCode());
        deliveryTypeFullInfoQueryReq.setCategoryId(itemSku.getCategoryId());
        List<Long> skuQuoteIds = new ArrayList<>();
        skuQuoteIds.add(itemSkuId.getSkuQuoteId());
        deliveryTypeFullInfoQueryReq.setSkuQuoteIds(skuQuoteIds);
        deliveryTypeFullInfoQueryReq.setQueryTimeliness(true);
        RpcResponse<List<DeliveryTypeFullInfoDTO>> deliveryTypeResult = commercialReadFacade.queryDeliveryTypeFullInfo(deliveryTypeFullInfoQueryReq);
        // 解析结果 获取支持的物流方式和运费
        List<String> supportDeliveryList = new ArrayList<>();
        List<ItemDelivery> itemDeliveryList = new ArrayList<>();
        if (Objects.nonNull(deliveryTypeResult) &&
            deliveryTypeResult.isSuccess() &&
            CollectionUtils.isNotEmpty(deliveryTypeResult.getData())) {
            for (DeliveryTypeFullInfoDTO deliveryTypeFullInfo : deliveryTypeResult.getData()) {
                // 赋值
                if (Objects.isNull(deliveryTypeFullInfo)) {
                    continue;
                }
                List<DeliveryTypeEnum> deliveryTypeEnums = TradeUtils.converDeliveryType(deliveryTypeFullInfo);
                if (CollectionUtils.isEmpty(deliveryTypeEnums)) {
                    continue;
                }
                for(DeliveryTypeEnum deliveryTypeEnum : deliveryTypeEnums) {
                    ItemDelivery itemDelivery = new ItemDelivery();
                    itemDelivery.setDeliveryType(deliveryTypeEnum.getCode());
                    itemDelivery.setDeliveryTypeName(deliveryTypeEnum.getScript());
                    itemDelivery.setDeliverHours(deliveryTypeFullInfo.geRealTimeliness());
                    itemDelivery.setDeliverTime(DateUtils.addHours(new Date(), deliveryTypeFullInfo.geRealTimeliness()));
                    itemDeliveryList.add(itemDelivery);
                    supportDeliveryList.add(deliveryTypeEnum.getScript());
                }
                log.info("supportDeliveryList = {} , itemDeliveryList = {}", JSON.toJSONString(supportDeliveryList), JSON.toJSONString(itemDeliveryList));
            }
            itemSku.setItemDelivery(itemDeliveryList);
            itemSku.setSupportDeliveryList(supportDeliveryList);
        }
    }


    // 该接口商品、sku都走缓存，非下单场景使用（购物车）
    @Override
    public List<ItemSku> queryItemsFromCache(List<ItemSkuId> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            log.info("queryItemsFromCache idList is null");
            return new ArrayList<>();
        }

        ItemBatchQueryReq batchReq = new ItemBatchQueryReq();
        idList.stream()
            .map(id -> id.getItemId())
            .distinct()
            .forEach(itemId -> {
                ItemQueryReq req = new ItemQueryReq();
                req.setItemId(itemId);
                req.setWithSku(true);
                req.setWithExtend(true);
                //req.setWithPayInfo(true);
                batchReq.add(req);
            });

        RpcResponse<List<ItemDTO>> resp = RpcUtils.invokeRpc(
            () -> itemReadFacade.queryCacheItems(batchReq),
            "itemReadFacade.queryCacheItems",
            I18NMessageUtils.getMessage("product.cache.query"),
            batchReq
        );  //# "商品缓存查询"

        log.info("queryCacheItems resp ItemDTO size={}",resp.getData().size());
        Map<Long, ItemDTO> itemMap = new HashMap<>();
        Map<Long, SkuDTO> skuMap = new HashMap<>();
        for (ItemDTO item : resp.getData()) {
            itemMap.put(item.getId(), item);
            for (SkuDTO sku : item.getSkuList()) {
                skuMap.put(sku.getId(), sku);
            }
        }

        return idList.stream()
            .map(id -> {
                ItemDTO item = itemMap.get(id.getItemId());
                SkuDTO sku = skuMap.get(id.getSkuId());
                if (!checkSkuItemRel(sku, item)) {
                    return null;
                }
                ItemSku itemSku =  itemRpcConverter.toItemSku(sku, item);
                Seller seller = new Seller();
                seller.setSellerId(id.getSellerId());
                itemSku.setSeller(seller);
                buildItemSkuEx(id, itemSku);

                //过滤 购物车物流时效只展示PVZ和Postamat两种物流时效
                List<ItemDelivery> deliveries = itemSku.getItemDelivery();
                if (deliveries != null) {
                    List<ItemDelivery> resultDeliveries = new ArrayList<>();
                    for (ItemDelivery delivery : deliveries) {
                        if (Objects.equals(delivery.getDeliveryType(), DeliveryTypeEnum.PVZ.getCode()) ||
                                Objects.equals(delivery.getDeliveryType(), DeliveryTypeEnum.POSTAMAT.getCode())) {
                            resultDeliveries.add(delivery);
                        }
                    }
                    // 将过滤后的列表设置回 itemSku 中
                    itemSku.setItemDelivery(resultDeliveries);
                }

                //补充每个商品的sku在每个商家能分多少期，借贷能借多少期 FaberWong
                /*if (CollectionUtils.isNotEmpty(sku.getItemSkuSellerList()))  {
                    for (ItemSkuSellerDTO seller : sku.getItemSkuSellerList()) {
                        if (seller.getSellerId().equals(id.getSellerId())) {
                            itemSku.setLoan(seller.getLoan());
                            itemSku.setInstallment(seller.getInstallment());

                            itemSku.setWarehouseIdList(seller.getWarehouseIdList());
                        }
                    }
                }*/
                return itemSku;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    }

//    /**
//     * 计算
//     * @param orders
//     * @param receiver
//     * @return
//     */
//    @Override
//    public Long calcFreightFee(List<SubOrder> orders, ReceiveAddr receiver) {
//        CustomerAddressReq addr = CustomerAddressReq.builder()
//            //.proviceId(Long.parseLong(receiver.getProvinceCode()))
//            .cityId(Long.parseLong(receiver.getCityCode()))
//            //.districtId(Long.parseLong(receiver.getDistrictCode()))
//            .build();
//        List<ItemFreightQueryParamReq> items = orders.stream()
//            .map(ord -> ItemFreightQueryParamReq.builder()
//                .skuId(ord.getItemSku().getSkuId())
//                .count(ord.getOrderQty()).build()
//            ).collect(Collectors.toList());
//        ItemFreightQueryReq req = ItemFreightQueryReq.builder()
//            .customerAddressReq(addr)
//            .queryParams(items)
//            .build();
//        RpcResponse<Long> resp = RpcUtils.invokeRpc(
//            () -> itemReadFacade.queryFreight(req),
//            "itemReadFacade.queryFreight",
//            I18NMessageUtils.getMessage("shipping.fee.query"),
//            req,  //# "运费查询"
//            (failInfo) -> ItemResponseCode.Fail_40010003.getCode().equals(failInfo.getCode())
//        );
//        if (!resp.isSuccess()) {
//            // 只有 Fail_40010003 (配送地址超出范围) 一种情况走在这里，其他会抛异常
//            return null;
//        }
//        return resp.getData();
//    }

    @Override
    public ItemCategory queryItemCategory(Long categoryId) {
        List<CategoryDTO> list = useCategoryCache ?
            queryCategoryPathWithCache(categoryId) :
            queryCategoryPath(categoryId);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        CategoryDTO curr = null;
        Map<String, String> mergeFeatures = new HashMap<>();
        for (CategoryDTO cate : list) {
            if (cate.getFeatureMap() != null) {
                mergeFeatures.putAll(cate.getFeatureMap());
            }
            if (categoryId.equals(cate.getId())) {
                curr = cate;
            }
        }
        if (curr == null) {
            return null;
        }

        ItemCategory result = itemRpcConverter.toItemCategory(curr);
        result.setFeatureMap(mergeFeatures);
        return result;
    }

    @Override
    public Map<Long, ItemCategory> batchQueryItemCategory(Collection<Long> categoryIds) {
        if (CollectionUtils.isEmpty(categoryIds)) {
            return new HashMap<>();
        }
        Set<Long> set = (categoryIds instanceof Set) ? (Set) categoryIds : new HashSet<>(categoryIds);
        Map<Long, ItemCategory> result = new HashMap<>();
        for (Long cateId : set) {
            if (cateId != null) {
                ItemCategory cate = queryItemCategory(cateId);
                result.put(cateId, cate);
            }
        }
        return result;
    }


    ///////////////////  商品 & SKU & 类目 的 原始接口  /////////////////

//    private List<SkuDTO> querySimpleSkus(Collection<Long> skuIds) {
//        List<SkuDTO> list = new ArrayList<>();
//        for (Long skuId : skuIds) {
//            SkuDTO sku = querySimpleSku(skuId);
//            if (sku != null) {
//                list.add(sku);
//            }
//        }
//        return list;
//    }
//
//    private SkuDTO querySimpleSku(Long skuId) {
//        SkuQueryReq req = new SkuQueryReq();
//        req.setSkuId(skuId);
//        req.setStatus(SkuStatus.ENABLE.getStatus());
//        req.setWithSeller(true);
//        RpcResponse<List<SkuDTO>> resp = RpcUtils.invokeRpc(
//            () -> itemReadFacade.querySku(req),
//            "itemReadFacade.querySku",
//            I18NMessageUtils.getMessage("product") + " SKU " + I18NMessageUtils.getMessage("query"),
//            req
//        );  //# "商品SKU查询"
//        return CollectionUtils.isEmpty(resp.getData()) ? null : resp.getData().get(0);
//    }

    private List<ItemDTO> querySimpleItems(Collection<Long> itemIds) {
        List<ItemDTO> list = new ArrayList<>();
        for (Long itemId : itemIds) {
            ItemDTO item = querySimpleItem(itemId, true);
            if (item != null) {
                list.add(item);
            }
        }
        return list;
    }

    private ItemDTO querySimpleItem(Long itemId, boolean withSku) {
        ItemQueryReq req = ItemQueryReq.create(itemId)
            .withCategory(true)
            .withExtend(true)
            .withFreight(false)
            .withSku(withSku);
        RpcResponse<ItemDTO> resp = RpcUtils.invokeRpc(
            () -> itemReadFacade.queryItem(req),
            "itemReadFacade.queryItem",
            I18NMessageUtils.getMessage("product.query"),
            req
        );  //# "商品查询"
        return resp.getData();
    }

    private List<ItemDTO> querySimpleItemsWithCache(Collection<Long> itemIds) {
        ItemBatchQueryReq batch = new ItemBatchQueryReq();
        for (Long itemId : itemIds) {
            ItemQueryReq req = ItemQueryReq.create(itemId)
                .withCategory(true)
                .withExtend(true)
                .withFreight(false)
                .withSku(true);
            batch.add(req);
        }
        RpcResponse<List<ItemDTO>> resp = RpcUtils.invokeRpc(
            () -> itemReadFacade.queryCacheItems(batch),
            "itemReadFacade.queryCacheItems",
            I18NMessageUtils.getMessage("product.query"),
            batch
        );  //# "商品查询"
        return resp.getData();
    }

    private ItemDTO querySimpleItemWithCache(Long itemId, boolean withSku) {
        ItemQueryReq req = ItemQueryReq.create(itemId)
            .withCategory(false)
            .withExtend(true)
            .withFreight(false)
            .withSku(withSku);
        RpcResponse<ItemDTO> resp = RpcUtils.invokeRpc(
            () -> itemReadFacade.queryCacheItem(req),
            "itemReadFacade.queryCacheItem", I18NMessageUtils.getMessage("product.query"), req);  //# "商品查询"
        return resp.getData();
    }

    private List<CategoryDTO> queryCategoryPath(Long categoryId) {
        CategoryQueryByIdReq req = new CategoryQueryByIdReq();
        req.setId(categoryId);
        RpcResponse<List<CategoryDTO>> resp = RpcUtils.invokeRpc(
            () -> categoryReadFacade.queryCategoryPathById(req),
            "categoryReadFacade.queryCategoryPathById", I18NMessageUtils.getMessage("category.query"), req);  //# "查询类目"
        return resp.getData();
    }

    private List<CategoryDTO> queryCategoryPathWithCache(Long categoryId) {
        CategoryQueryByIdReq req = new CategoryQueryByIdReq();
        req.setId(categoryId);
        RpcResponse<List<CategoryDTO>> resp = RpcUtils.invokeRpc(
            () -> categoryReadFacade.queryCacheCategoryPathById(req),
            "categoryReadFacade.queryCacheCategoryPathById", I18NMessageUtils.getMessage("category.query"), req);  //# "查询类目"
        return resp.getData();
    }


    //新的获取SkuDTO接口直接从item中获取
    private List<SkuDTO> querySimpleSkus(List<ItemDTO> itemList) {
        List<SkuDTO> listSet = new ArrayList<>();
        for (ItemDTO itemDTO : itemList) {
            List<SkuDTO>  sku = itemDTO.getSkuList();
            if (CollectionUtils.isNotEmpty(sku)) {
                listSet.addAll(sku);
            }
        }
        return listSet;
    }
}
