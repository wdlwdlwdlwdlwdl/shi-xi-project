package com.aliyun.gts.gmall.manager.front.item.adaptor;

import static com.aliyun.gts.gmall.manager.front.item.dto.utils.ItemFrontResponseCode.ADDRESS_NOT_EXIST;
import static com.aliyun.gts.gmall.manager.front.item.dto.utils.ItemFrontResponseCode.ITEM_EVOUCHER_EXPIRE;
import static com.aliyun.gts.gmall.manager.front.item.dto.utils.ItemFrontResponseCode.ITEM_NOT_EXIST;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.aliyun.gts.gmall.platform.item.api.dto.output.item.*;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.display.PromotionSummation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import com.aliyun.gts.gmall.center.item.api.dto.input.AgreementPriceQueryReq;
import com.aliyun.gts.gmall.center.item.api.dto.output.AgreementPriceDTO;
import com.aliyun.gts.gmall.center.item.api.dto.output.B2bItemDTO;
import com.aliyun.gts.gmall.center.item.api.dto.output.CombineItemDTO;
import com.aliyun.gts.gmall.center.item.api.dto.output.DepositConfigDTO;
import com.aliyun.gts.gmall.center.item.api.dto.output.EvoucherPeriodDTO;
import com.aliyun.gts.gmall.center.item.api.facade.AgreementPriceFacade;
import com.aliyun.gts.gmall.center.item.common.consts.ItemExtendConstant;
import com.aliyun.gts.gmall.center.item.common.enums.ItemType;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.config.I18NConfig;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.framework.common.DubboBuilder;
import com.aliyun.gts.gmall.manager.front.common.config.DatasourceConfig;
import com.aliyun.gts.gmall.manager.front.common.consts.DsIdConst;
import com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode;
import com.aliyun.gts.gmall.manager.front.common.util.DateUtils;
import com.aliyun.gts.gmall.manager.front.item.convertor.ItemConvertor;
import com.aliyun.gts.gmall.manager.front.item.dto.ItemPageQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.ItemDeadlineRestQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.ItemDetailV2RestQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.ItemSkuRestQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.output.AddressVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.B2bItemVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.CombineItemVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.CombineSkuVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.DepositConfigVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemCharacteristicsVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemDeadlineVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemDetailV2VO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemDetailVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemMerchantInfoVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemPageVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.ItemSkuFeaturesVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.ItemSkuV2VO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.ItemSkuVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.SkuPropVO;
import com.aliyun.gts.gmall.platform.item.api.dto.input.category.CategoryQueryByIdReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.item.CustomerAddressReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.item.ItemDeadlineQueryReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.item.ItemDetailQueryByIdReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.item.ItemFreightQueryParamReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.item.ItemFreightQueryReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.item.ItemPageQueryReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.item.ItemQueryReq;
import com.aliyun.gts.gmall.platform.item.api.dto.input.item.ItemSkuQueryByIdReq;
import com.aliyun.gts.gmall.platform.item.api.dto.output.category.CategoryDTO;
import com.aliyun.gts.gmall.platform.item.api.facade.category.CategoryReadFacade;
import com.aliyun.gts.gmall.platform.item.api.facade.item.ItemReadFacade;
import com.aliyun.gts.gmall.platform.item.api.utils.ItemExtendUtil;
import com.aliyun.gts.gmall.platform.item.common.enums.ItemStatus;
import com.aliyun.gts.gmall.platform.item.common.enums.SkuStatus;
import com.aliyun.gts.gmall.platform.promotion.api.dto.input.PromotionQueryReq;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.display.PromotionInfo;
import com.aliyun.gts.gmall.platform.promotion.api.facade.cust.PromotionReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.QueryCartItemQuantityRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.facade.cart.CartReadFacade;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CommonByBatchIdsQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CommonByIdAndCustByIdQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerByIdQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerQueryOption;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerAddressDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerAddressReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerReadFacade;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

/**
 * 商品服务
 *
 * @author tiansong
 */
@Slf4j
@Service
public class ItemAdaptor {
    @Autowired
    private ItemReadFacade itemReadFacade;
    @Autowired
    private ItemConvertor itemConvertor;
    @Autowired
    private CustomerReadFacade customerReadFacade;
    @Autowired
    private PromotionReadFacade promotionReadFacade;
    @Autowired
    private CartReadFacade cartReadFacade;
    @Autowired
    private CustomerAddressReadFacade customerAddressReadFacade;
    @Autowired
    private DatasourceConfig datasourceConfig;
    @Autowired
    private AgreementPriceFacade agreementPriceFacade;
    @Autowired
    private I18NConfig i18NConfig;
    @Autowired
    private CategoryReadFacade categoryReadFacade;



    DubboBuilder itemBuilder = DubboBuilder.builder().logger(log).sysCode(FrontCommonResponseCode.ITEM_CENTER_ERROR).build();

    /**
     * 获取商品信息，包含sku、类目、运费、扩展等信息【强依赖】
     *
     * @param itemId
     * @return
     */
    public ItemDetailVO getItemDetail(Long itemId) {
        return getItemDetail(itemId, false);
    }

    public ItemDetailVO getItemDetail(Long itemId, boolean withCategory) {
        ItemQueryReq itemQueryReq = ItemQueryReq.create(itemId);
        itemQueryReq.setWithSku(Boolean.TRUE);
        itemQueryReq.setWithExtend(Boolean.TRUE);
        itemQueryReq.setWithCatProp(Boolean.TRUE);
        itemQueryReq.setWithCategory(withCategory);
        return this.queryItem(itemQueryReq, true, true);
    }

    /**
     * 获取商品和SKU信息【强依赖】
     *
     * @param itemId
     * @return
     */
    public ItemDetailVO getItemSku(Long itemId) {
        ItemQueryReq itemQueryReq = ItemQueryReq.create(itemId);
        itemQueryReq.setWithSku(Boolean.TRUE);
        itemQueryReq.setWithExtend(Boolean.TRUE);
        return this.queryItem(itemQueryReq, false, false);
    }

    /**
     * 获取商品和SKU信息【弱依赖】
     *
     * @param itemId
     * @return
     */
    public ItemDetailVO getItemSkuWeak(Long itemId) {
        try {
            return this.getItemSku(itemId);
        } catch (Exception e) {
        }
        return null;
    }

    private ItemDetailVO queryItem(ItemQueryReq itemQueryReq, boolean withTag, boolean withEVoucher) {
        final String lang = LocaleContextHolder.getLocale().getLanguage();
        final String fallback = i18NConfig.getDefaultLang();

        itemQueryReq.setSkuStatus(SkuStatus.ENABLE.getStatus());
        return itemBuilder.create().id(DsIdConst.item_detail_query).queryFunc((Function<ItemQueryReq, RpcResponse<ItemDetailVO>>) request -> {
            RpcResponse<ItemDTO> rpcResponse = itemReadFacade.queryCacheItem(request);
            if (!rpcResponse.isSuccess()) {
                return RpcResponse.fail(rpcResponse.getFail());
            }
            ItemDTO itemDTO = rpcResponse.getData();
            // 商品状态不是上架，则该商品不存在
            if (itemDTO == null || !ItemStatus.ENABLE.getStatus().equals(itemDTO.getStatus())) {
                return RpcResponse.fail(ITEM_NOT_EXIST);
            }

            // //检测当前语种是否开启
            // String effectLang = fallback;
            // if (MapUtils.isNotEmpty(itemDTO.getFeatureMap())) {
            // String status = itemDTO.getFeatureMap().get(ItemCenterFeatureConstant.getLangStatusKey(lang));
            // if (ItemCenterFeatureConstant.LANG_STATUS_TRUE.equals(status)) {
            // effectLang = lang;
            // }
            // }

            ItemDetailVO itemDetailVO = new ItemDetailVO();
            itemDetailVO.setSellerId(itemDTO.getSellerId());
            itemDetailVO.setItemBaseVO(itemConvertor.convertBase(itemDTO, lang));


            // fill sku
            if (request.isWithSku()) {
                itemDetailVO.setItemSkuPropVOList(itemConvertor.convertSkuProp(itemDTO.getSkuPropList(), lang));
                itemDetailVO.setItemSkuVOList(itemConvertor.convertSkuList(itemDTO.getSkuList(), lang));
            }
            // fill cat prop
            if (request.isWithCatProp()) {
                itemDetailVO.setCatPropInfo(itemConvertor.convertCatProp(itemDTO.getCatPropInfo(), lang));
            }
            // fill category
            if (request.isWithCategory()) {
                itemDetailVO.setItemCategoryVOList(itemConvertor.convertCat(itemDTO.getCategoryList(), lang));
            }
            // fill tagList
            if (withTag) {
                // itemDetailVO.setTagList(itemConvertor.convertToTagNameList(itemDTO.getTagList(), lang));
            }
            // fill e voucher;电子凭证
            if (withEVoucher) {
                EvoucherPeriodDTO evoucherPeriodDTO = this.getEvoucher(itemDTO);
                if (evoucherPeriodDTO != null) {
                    // 电子凭证已经过期了
                    if (!checkExpire(evoucherPeriodDTO)) {
                        return RpcResponse.fail(ITEM_EVOUCHER_EXPIRE);
                    }
                    itemDetailVO.setEVoucherDisplay(this.getEvoucherDisplay(evoucherPeriodDTO));
                }
            }

            // 定金尾款配置
            itemDetailVO.setDepositConfigVO(getDeposit(itemDTO));

            // 商贸商品
            itemDetailVO.setB2bItem(getB2bItem(itemDTO));

            // 组合商品
            fillCombineItem(itemDetailVO, itemDTO);

            // 被组合商品的状态不对,返回下架
            if (itemDetailVO.getCombineItem() != null && !itemDetailVO.getCombineItem().isEnableStatus()) {
                log.warn("被组合商品状态不对.itemId:{}", itemDetailVO.getItemBaseVO().getId());
                return RpcResponse.fail(ITEM_NOT_EXIST);
            }

            return RpcResponse.ok(itemDetailVO);

        }).bizCode(ITEM_NOT_EXIST).query(itemQueryReq);
    }

    private void fillCombineItem(ItemDetailVO itemDetailVO, ItemDTO itemDTO) {
        final String lang = LocaleContextHolder.getLocale().getLanguage();
        final String fallback = i18NConfig.getDefaultLang();

        if (!itemDetailVO.getItemBaseVO().isCombine()) {
            return;
        }

        CombineItemDTO combineItemDTO = ItemExtendUtil.getExtendObject(ItemExtendConstant.COMBINE_ITEM, itemDTO, CombineItemDTO.class);

        CombineItemVO combineItem = itemConvertor.toVO(combineItemDTO);

        if (combineItem == null || CollectionUtils.isEmpty(combineItem.getSkuList())) {
            return;
        }

        itemDetailVO.setCombineItem(combineItem);

        List<CombineSkuVO> combineSkuVOList = Lists.newArrayList();

        combineItem.getSkuList().forEach(x -> {

            RpcResponse<ItemDTO> rpcResponse = itemReadFacade.queryCacheItem(ItemQueryReq.create(x.getItemId()).withSku(true));

            if (rpcResponse != null && rpcResponse.getData() != null) {

                SkuDTO skuDTO = rpcResponse.getData().getSkuList().stream().filter(y -> y.getId().equals(x.getId())).findFirst().orElse(null);

                if (skuDTO != null) {
                    CombineSkuVO combineSkuVO = itemConvertor.toCombineSkuVO(skuDTO, lang);
                    if (rpcResponse.getData().getTitle() != null) {
                        combineSkuVO.setItemTitle(rpcResponse.getData().getTitle().getValueByLang(lang, fallback));
                    }
                    combineSkuVO.setItemStatus(rpcResponse.getData().getStatus());
                    combineSkuVO.setPerNum(x.getPerNum());

                    // 设置sku图片
                    SkuPropVO skuPropVOPic =
                            combineSkuVO.getSkuPropList().stream().filter(skuPropVO -> StringUtils.isNotBlank(skuPropVO.getPicUrl())).findFirst().orElse(null);

                    if (skuPropVOPic != null) {
                        combineSkuVO.setPicUrl(skuPropVOPic.getPicUrl());
                    } else if (rpcResponse.getData().getPictureList() != null) {
                        // combineSkuVO.setPicUrl(rpcResponse.getData().getPictureList().getValueByLang(lang,
                        // fallback).get(0));
                    }

                    combineSkuVOList.add(combineSkuVO);
                }
            }
        });

        combineItem.setSkuList(combineSkuVOList);

        // 处理组合商品自身sku库存
        if (CollectionUtils.isNotEmpty(itemDetailVO.getItemSkuVOList())) {
            ItemSkuVO itemSkuVO = itemDetailVO.getItemSkuVOList().get(0);
            // 库存取算好的最小可组合库存
            itemSkuVO.setQuantity(combineItem.getQuantity());
        }
    }

    private B2bItemVO getB2bItem(ItemDTO itemDTO) {

        if (!ItemType.B2B.getType().equals(itemDTO.getItemType())) {
            return null;
        }
        B2bItemDTO b2bItemDTO = ItemExtendUtil.getExtendObject(ItemExtendConstant.B2B_ITEM, itemDTO, B2bItemDTO.class);
        return itemConvertor.convertB2bItem(b2bItemDTO);
    }

    private DepositConfigVO getDeposit(ItemDTO itemDTO) {
        if (!ItemType.DEPOSIT.getType().equals(itemDTO.getItemType())) {
            return null;
        }
        DepositConfigDTO configDTO = ItemExtendUtil.getExtendObject(ItemExtendConstant.DEPOSIT_CONFIG, itemDTO, DepositConfigDTO.class);

        return itemConvertor.convertDeposit(configDTO);
    }

    private EvoucherPeriodDTO getEvoucher(ItemDTO itemDTO) {
        if (!ItemType.EVOUCHER.getType().equals(itemDTO.getItemType())) {
            return null;
        }
        return ItemExtendUtil.getExtendObject(ItemExtendConstant.EVOUCHER_PERIOD, itemDTO, EvoucherPeriodDTO.class);
    }

    private boolean checkExpire(EvoucherPeriodDTO evoucherPeriodDTO) {
        if (evoucherPeriodDTO == null) {
            return Boolean.TRUE;
        }
        switch (evoucherPeriodDTO.getType()) {
            case EvoucherPeriodDTO.TYPE_2:
            case EvoucherPeriodDTO.TYPE_3:
                return evoucherPeriodDTO.getEnd().getTime() > System.currentTimeMillis();
            default:
                return Boolean.TRUE;
        }
    }

    private static final String TYPE_1_DISPLAY = I18NMessageUtils.getMessage("after.purchase") + "%s天有效"; // # "购买后
    private static final String TYPE_2_DISPLAY = I18NMessageUtils.getMessage("after.purchase.to") + "%s" + I18NMessageUtils.getMessage("valid"); // # "购买后到%s有效"
    private static final String TYPE_3_DISPLAY = "%s " + I18NMessageUtils.getMessage("to") + " %s"; // # 至
    private static final String TYPE_4_DISPLAY = "长期有效";

    private String getEvoucherDisplay(EvoucherPeriodDTO evoucherPeriodDTO) {
        if (evoucherPeriodDTO == null) {
            return null;
        }
        switch (evoucherPeriodDTO.getType().intValue()) {
            case EvoucherPeriodDTO.TYPE_1:
                return String.format(TYPE_1_DISPLAY, evoucherPeriodDTO.getDay());
            case EvoucherPeriodDTO.TYPE_2:
                return String.format(TYPE_2_DISPLAY, DateUtils.formatYMD(evoucherPeriodDTO.getEnd()));
            case EvoucherPeriodDTO.TYPE_3:
                return String.format(TYPE_3_DISPLAY, DateUtils.formatYMD(evoucherPeriodDTO.getStart()), DateUtils.formatYMD(evoucherPeriodDTO.getEnd()));
            default:
                return TYPE_4_DISPLAY;
        }
    }

    public Long queryItemFreight(Long skuId, AddressVO addressVO) {
        // build parameter
        List<ItemFreightQueryParamReq> itemFreightQueryParamReqList = Lists.newArrayList();
        itemFreightQueryParamReqList.add(ItemFreightQueryParamReq.builder().skuId(skuId).count(1).build());
        ItemFreightQueryReq itemFreightQueryReq = ItemFreightQueryReq.builder().customerAddressReq(
                CustomerAddressReq.builder().proviceId(addressVO.getProvinceId()).cityId(addressVO.getCityId()).districtId(addressVO.getAreaId()).build())
                .queryParams(itemFreightQueryParamReqList).build();
        // request
        return itemBuilder.create().id(DsIdConst.item_freight_query).queryFunc((Function<ItemFreightQueryReq, RpcResponse<Long>>) request -> {
            RpcResponse<Long> rpcResponse = null; // itemReadFacade.queryFreight(request);
            if (!rpcResponse.isSuccess()) {
                return RpcResponse.fail(rpcResponse.getFail());
            }
            return rpcResponse;
        }).strong(Boolean.FALSE).query(itemFreightQueryReq);
    }

    DubboBuilder customerBuilder = DubboBuilder.builder().logger(log).strong(Boolean.FALSE).build();

    public List<CustomerDTO> queryCustomerByIds(List<Long> custIds) {
        CommonByBatchIdsQuery commonByBatchIdsQuery = new CommonByBatchIdsQuery();
        commonByBatchIdsQuery.setIds(custIds);
        return customerBuilder.create(datasourceConfig).id(DsIdConst.item_customer_queryBatch)
                .queryFunc((Function<CommonByBatchIdsQuery, RpcResponse<List<CustomerDTO>>>) request -> {
                    RpcResponse<List<CustomerDTO>> rpcResponse = customerReadFacade.queryByIds(request);
                    if (rpcResponse.isSuccess() && rpcResponse.getData() != null) {
                        return RpcResponse.ok(rpcResponse.getData());
                    }
                    return rpcResponse.isSuccess() ? RpcResponse.ok(Collections.EMPTY_LIST) : RpcResponse.fail(rpcResponse.getFail());
                }).query(commonByBatchIdsQuery);
    }

    public AddressVO queryAddressDefault(Long custId) {
        CustomerByIdQuery customerByIdQuery = CustomerByIdQuery.of(custId, CustomerQueryOption.builder().needDefaultAddress(true).build());
        return customerBuilder.create(datasourceConfig).id(DsIdConst.item_address_queryDefault)
                .queryFunc((Function<CustomerByIdQuery, RpcResponse<AddressVO>>) request -> {
                    RpcResponse<CustomerDTO> rpcResponse = customerReadFacade.query(request);
                    if (!rpcResponse.isSuccess()) {
                        return RpcResponse.fail(rpcResponse.getFail());
                    }
                    if (rpcResponse.getData() == null || rpcResponse.getData().getDefaultCustAddress() == null) {
                        return RpcResponse.fail(ADDRESS_NOT_EXIST);
                    }
                    return RpcResponse.ok(itemConvertor.convertAddress(rpcResponse.getData().getDefaultCustAddress()));
                }).query(customerByIdQuery);
    }

    public AddressVO queryAddressById(Long custId, Long addressId) {
        CommonByIdAndCustByIdQuery commonByIdAndCustByIdQuery = new CommonByIdAndCustByIdQuery();
        commonByIdAndCustByIdQuery.setId(addressId);
        commonByIdAndCustByIdQuery.setCustId(custId);
        return customerBuilder.create(datasourceConfig).id(DsIdConst.item_address_query)
                .queryFunc((Function<CommonByIdAndCustByIdQuery, RpcResponse<AddressVO>>) request -> {
                    RpcResponse<CustomerAddressDTO> rpcResponse = customerAddressReadFacade.query(request);
                    if (!rpcResponse.isSuccess()) {
                        return RpcResponse.fail(rpcResponse.getFail());
                    }
                    if (rpcResponse.getData() == null) {
                        return RpcResponse.fail(ADDRESS_NOT_EXIST);
                    }
                    return RpcResponse.ok(itemConvertor.convertAddress(rpcResponse.getData()));
                }).query(commonByIdAndCustByIdQuery);
    }

    DubboBuilder promotionBuilder = DubboBuilder.builder().logger(log).strong(Boolean.FALSE).build();

    public PromotionInfo queryPromotion(PromotionQueryReq detailPromotionQueryReq) {
        return promotionBuilder.create(datasourceConfig).id(DsIdConst.item_promotion_query)
                .queryFunc((Function<PromotionQueryReq, RpcResponse<PromotionInfo>>) request -> promotionReadFacade.queryPromotionInfo(request))
                .query(detailPromotionQueryReq);
    }

    public PromotionSummation queryPromotionSummation(PromotionQueryReq promotionQueryReq) {
        return promotionBuilder.create(datasourceConfig).id(DsIdConst.item_promotion_query)
                .queryFunc((Function<PromotionQueryReq, RpcResponse<PromotionSummation>>) request -> promotionReadFacade.queryPromotionSummation(request))
                .query(promotionQueryReq);
    }

    DubboBuilder tradeBuilder = DubboBuilder.builder().logger(log).strong(Boolean.FALSE).build();

    private static final Integer DEFAULT_CART_QUANTITY = 0;

    public Integer queryCartQuantity(Long custId) {
        if (custId == null || custId <= 0L) {
            return DEFAULT_CART_QUANTITY;
        }
        QueryCartItemQuantityRpcReq queryCartItemQuantityRpcReq = new QueryCartItemQuantityRpcReq();
        queryCartItemQuantityRpcReq.setCustId(custId);
        return tradeBuilder.create(datasourceConfig).id(DsIdConst.trade_cart_quantity)
                .queryFunc((Function<QueryCartItemQuantityRpcReq, RpcResponse<Integer>>) request -> cartReadFacade.queryCartItemQuantity(request))
                .query(queryCartItemQuantityRpcReq);
    }

    public List<AgreementPriceDTO> queryItemAgreements(Long custId, Long itemId, Long sellerId) {
        if (custId == null || custId.longValue() <= 0L) {
            return null;
        }

        AgreementPriceQueryReq req = new AgreementPriceQueryReq();
        req.setSellerId(sellerId);
        req.setCustId(custId);
        req.setItemIds(Arrays.asList(0L, itemId));

        Function<AgreementPriceQueryReq, RpcResponse<List<AgreementPriceDTO>>> f = agreementPriceFacade::queryAgreementByItems;
        return itemBuilder.create().id(DsIdConst.item_agreement_query).queryFunc(f).query(req);
    }

    public ItemSkuV2VO queryItemSkuV2(ItemSkuRestQuery req) {
        ItemSkuQueryByIdReq itemSkuQueryByIdReq = new ItemSkuQueryByIdReq();
        itemSkuQueryByIdReq.setSkuId(req.getSkuId());
        return itemBuilder.create().id(DsIdConst.item_sku_query).queryFunc((Function<ItemSkuQueryByIdReq, RpcResponse<ItemSkuV2VO>>) request -> {
            RpcResponse<ItemSkuDTO> itemSkuDTORpcResponse = itemReadFacade.queryItemSku(itemSkuQueryByIdReq);
            ItemSkuDTO data = itemSkuDTORpcResponse.getData();
            ItemSkuV2VO v2VO = itemConvertor.convertItemSku(data);
            return RpcResponse.ok(v2VO);
        }).query(itemSkuQueryByIdReq);
    }

    public List<ItemSkuFeaturesVO> queryItemSkuFeatures(ItemSkuRestQuery query) {
        ItemSkuQueryByIdReq itemSkuQueryByIdReq = new ItemSkuQueryByIdReq();
        itemSkuQueryByIdReq.setSkuId(query.getSkuId());
        return itemBuilder.create().id(DsIdConst.item_sku_features_query)
                .queryFunc((Function<ItemSkuQueryByIdReq, RpcResponse<List<ItemSkuFeaturesVO>>>) request -> {
                    RpcResponse<List<ItemSkuFeaturesDTO>> listRpcResponse = itemReadFacade.queryItemSkuFeatures(itemSkuQueryByIdReq);
                    List<ItemSkuFeaturesDTO> data = listRpcResponse.getData();
                    List<ItemSkuFeaturesVO> list = itemConvertor.convertItemSkuFeatures(data);
                    return RpcResponse.ok(list);
                }).query(itemSkuQueryByIdReq);
    }

    public ItemDetailV2VO queryItemDetailV2(ItemDetailV2RestQuery req) {
        ItemQueryReq rpcReq = new ItemQueryReq();
        rpcReq.withCategory(true);
        rpcReq.withSku(true);
        rpcReq.withCatProp(true);
        rpcReq.withExtend(true);
        rpcReq.setItemId(req.getItemId());
        return itemBuilder.create().id(DsIdConst.item_detail_v2_query).queryFunc((Function<ItemQueryReq, RpcResponse<ItemDetailV2VO>>) request -> {
            RpcResponse<ItemDTO> rpcResp = itemReadFacade.queryItem(rpcReq);
            ItemDetailV2VO itemDetailV2VO = itemConvertor.toItemDetailV2Vo(rpcResp.getData());
            return RpcResponse.ok(itemDetailV2VO);
        }).query(rpcReq);
    }

    public List<ItemDeadlineVO> queryItemDeadline(ItemDeadlineRestQuery query) {
        ItemDeadlineQueryReq req = new ItemDeadlineQueryReq();
        BeanUtils.copyProperties(query, req);
        return itemBuilder.create().id(DsIdConst.item_deadline_query).queryFunc((Function<ItemDeadlineQueryReq, RpcResponse<List<ItemDeadlineVO>>>) request -> {
            RpcResponse<List<ItemDeadlineDTO>> listRpcResponse = itemReadFacade.queryItemDeadline(req);
            List<ItemDeadlineDTO> data = listRpcResponse.getData();
            List<ItemDeadlineVO> list = itemConvertor.convertItemDeadline(data);
            return RpcResponse.ok(list);
        }).query(req);

    }

    public List<ItemCharacteristicsVO> queryItemCharacteristics(ItemDetailV2RestQuery query) {
        ItemDetailQueryByIdReq itemDetailQueryByIdReq = new ItemDetailQueryByIdReq();
        itemDetailQueryByIdReq.setItemId(query.getItemId());
        return itemBuilder.create().id(DsIdConst.item_characteristics_query)
                .queryFunc((Function<ItemDetailQueryByIdReq, RpcResponse<List<ItemCharacteristicsVO>>>) request -> {
                    RpcResponse<List<ItemCharacteristicsDTO>> listRpcResponse = itemReadFacade.queryItemCharacteristics(itemDetailQueryByIdReq);
                    List<ItemCharacteristicsDTO> data = listRpcResponse.getData();
                    List<ItemCharacteristicsVO> list = itemConvertor.convertItemCharacteristics(data);
                    return RpcResponse.ok(list);
                }).query(itemDetailQueryByIdReq);
    }

    public List<ItemMerchantInfoVO> queryItemMerchantInfo(ItemDetailV2RestQuery req) {

        return null;
    }

    public List<ItemPageVO> queryPage(ItemPageQuery query) {
        // 查询search那边，现在mock数据
        ItemPageQueryReq req = new ItemPageQueryReq();
        req.setPageIndex(query.getPageIndex());
        req.setPageSize(query.getPageSize());
        return itemBuilder.create().id(DsIdConst.item_page_query).queryFunc((Function<ItemPageQueryReq, RpcResponse<List<ItemPageVO>>>) request -> {
            RpcResponse<List<ItemBaseInfoDTO>> listRpcResponse = null;// itemReadFacade.queryItemBaseInfo(req);
            List<ItemBaseInfoDTO> data = listRpcResponse.getData();
            List<ItemPageVO> list = itemConvertor.convertItemBaseInfo(data);
            return RpcResponse.ok(list);
        }).query(req);
    }



    public CategoryDTO queryById(Long categoryId) {
        CategoryQueryByIdReq rpcReq = new CategoryQueryByIdReq();
        rpcReq.setId(categoryId);
        return itemBuilder.create().id(DsIdConst.item_page_query)
                .queryFunc((Function<CategoryQueryByIdReq, RpcResponse<CategoryDTO>>) request -> categoryReadFacade.queryById(rpcReq)).query(rpcReq);
    }
}
