package com.aliyun.gts.gmall.manager.front.item.convertor;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;

import com.aliyun.gts.gmall.platform.item.api.dto.output.item.*;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.aliyun.gts.gmall.center.item.api.dto.output.B2bItemDTO;
import com.aliyun.gts.gmall.center.item.api.dto.output.CombineItemDTO;
import com.aliyun.gts.gmall.center.item.api.dto.output.DepositConfigDTO;
import com.aliyun.gts.gmall.center.item.common.consts.ItemCenterFeatureConstant;
import com.aliyun.gts.gmall.framework.config.I18NConfig;
import com.aliyun.gts.gmall.framework.convert.MultiLangConverter;
import com.aliyun.gts.gmall.framework.i18n.LangText;
import com.aliyun.gts.gmall.framework.i18n.LangValue;
import com.aliyun.gts.gmall.framework.i18n.MultiLangText;
import com.aliyun.gts.gmall.framework.i18n.MultiLangValue;
import com.aliyun.gts.gmall.manager.front.item.dto.output.AddressVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.B2bItemVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.CombineItemVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.CombineSkuVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.DepositConfigVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemBaseVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemCatPropGroupVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemCatPropVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemCatPropValueVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemCategoryVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemCharacteristicsVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemDeadlineVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemDetailV2VO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemDetailVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemEvaluationVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemMerchantInfoVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemPageVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemProductOptionVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemProductOptionValueVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemPromotionVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemSearchVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.ItemSkuFeaturesVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.ItemSkuPropVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.ItemSkuV2VO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.ItemSkuVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.PropValueVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.SkuQuoteListVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.SkuVO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.category.CategoryDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.unit.UnitDTO;
import com.aliyun.gts.gmall.platform.item.common.constant.ItemFeatureConstant;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.CouponInstanceDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.PromotionDetailDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerAddressDTO;
import com.aliyun.gts.gmall.searcher.api.dto.output.evaluation.EvaluationSearchDTO;
import com.aliyun.gts.gmall.searcher.api.dto.output.item.ItemSearchDTO;

/**
 * 商品信息转换
 *
 * @author tiansong
 */
@Mapper(componentModel = "spring", imports = {Optional.class, UnitDTO.class})
public abstract class ItemConvertor {
    @Resource
    protected I18NConfig i18NConfig;

    @Autowired
    protected MultiLangConverter multiLangConverter;

    /**
     * 商品基础信息转换
     *
     * @param itemDTO
     * @return
     */
    @Mapping(target = "buyLimit", source = "featureMap")
    @Mapping(target = "combine", expression = "java(toCombine(itemDTO.getFeatureMap()))")
    @Mapping(target = "disableBuy", expression = "java(toDisableBuy(itemDTO.getFeatureMap()))")
    public abstract ItemBaseVO convertBase(ItemDTO itemDTO, @Context String lang);

    public abstract CombineItemVO toVO(CombineItemDTO combineItemDTO);

    public abstract CombineSkuVO toCombineSkuVO(SkuDTO skuDTO, @Context String lang);

    protected Long toBuyLimit(Map<String, String> featureMap) {
        if (featureMap == null || featureMap.get(ItemFeatureConstant.BUY_LIMIT) == null) {
            return null;
        }
        return Long.parseLong(featureMap.get(ItemFeatureConstant.BUY_LIMIT));
    }

    protected boolean toDisableBuy(Map<String, String> featureMap) {

        if (featureMap == null) {
            return false;
        }
        return ItemCenterFeatureConstant.DISABLE_BUY_TRUE.equals(featureMap.get(ItemCenterFeatureConstant.DISABLE_BUY));
    }

    protected boolean toCombine(Map<String, String> featureMap) {

        if (featureMap == null) {
            return false;
        }

        return ItemCenterFeatureConstant.COMBINE_ITEM_TRUE.equals(featureMap.get(ItemCenterFeatureConstant.COMBINE_ITEM));

    }

    /**
     * 商品SKU属性信息转换
     *
     * @param itemSkuPropList
     * @return
     */
    public abstract List<ItemSkuPropVO> convertSkuProp(List<ItemSkuPropDTO> itemSkuPropList, @Context String lang);

    public abstract List<ItemCatPropVO> convertCatProp(List<ItemCatPropDTO> itemPropList, @Context String lang);

    /**
     * 商品SKU信息转换
     *
     * @param skuDTOList
     * @return
     */
    public abstract List<ItemSkuVO> convertSkuList(List<SkuDTO> skuDTOList, @Context String lang);

    /**
     * 商品SKU信息转换
     *
     * @param skuDTO
     * @return
     */
    // @Mapping(target = "quantity", expression = "java(skuDTO.getSellableQuantity())")
    public abstract ItemSkuVO convertSku(SkuDTO skuDTO, @Context String lang);

    /**
     * 商品类目信息转换
     *
     * @param categoryDTOList
     * @return
     */
    public abstract List<ItemCategoryVO> convertCat(List<CategoryDTO> categoryDTOList, @Context String lang);

    public abstract ItemCategoryVO convert(CategoryDTO categoryDTO, @Context String lang);

    /**
     * 商品评价转换
     *
     * @param evaluationDTOList
     * @return
     */
    public abstract List<ItemEvaluationVO> convertEvaluation(List<EvaluationSearchDTO> evaluationDTOList);

    /**
     * 商品搜索信息转换
     *
     * @param itemSearchDTOList
     * @return
     */
    public abstract List<ItemSearchVO> convertSearch(List<ItemSearchDTO> itemSearchDTOList);

    /**
     * 商品优惠券转换
     *
     * @param couponInstanceDTOList
     * @return
     */
    public abstract List<ItemPromotionVO> convertCoupon(List<CouponInstanceDTO> couponInstanceDTOList);

    /**
     * 商品优惠信息转换
     *
     * @param promotionDetailDTOList
     * @return
     */
    public abstract List<ItemPromotionVO> convertCampaigns(List<PromotionDetailDTO> promotionDetailDTOList);

    /**
     * 营销活动转换
     *
     * @param promotionDetailDTO
     * @return
     */
    @Mapping(target = "toolCode", source = "promotionToolCode")
    public abstract ItemPromotionVO convertCampaign(PromotionDetailDTO promotionDetailDTO);

    /**
     * 用户收货地址转换
     *
     * @param customerAddressDTO
     * @return
     */
    public abstract AddressVO convertAddress(CustomerAddressDTO customerAddressDTO);

    /**
     * 尾款定金配置
     *
     * @param depositConfigDTO
     * @return
     */
    public abstract DepositConfigVO convertDeposit(DepositConfigDTO depositConfigDTO);

    public abstract B2bItemVO convertB2bItem(B2bItemDTO b2bItemDTO);

    protected String fenToYuan(Long price) {
        return String.valueOf(price);
    }

    public abstract ItemDetailVO copy(ItemDetailVO source);

    public abstract B2bItemVO copy(B2bItemVO source);

    // === ItemTag Name ===
    public List<String> convertToTagNameList(MultiLangValue<LangValue<List<ItemTagDTO>>, List<ItemTagDTO>> mValue, @Context String lang) {
        if (mValue == null) {
            return null;
        }
        String fallback = i18NConfig.getDefaultLang();
        List<ItemTagDTO> tagDTOS = mValue.getValueByLang(lang, fallback);
        if (CollectionUtils.isEmpty(tagDTOS)) {
            return null;
        }
        return tagDTOS.stream().map(ItemTagDTO::getName).collect(Collectors.toList());
    }


    // String <---> MultiLangText
    protected String common_map_to_str(@Context String lang, MultiLangText mText) {
        if (mText == null) {
            return null;
        }
        String fallback = i18NConfig.getDefaultLang();
        return mText.getValueByLang(lang, fallback);
    }

    // String <---> ItemPropValueDTO
    abstract List<String> map_from_item_prop_value_dtos(List<ItemCatPropValueDTO> value, @Context String lang);

    String map_from_item_prop_value_dto(ItemCatPropValueDTO value, @Context String lang) {
        if (value == null || value.getValue() == null) {
            return null;
        }
        return value.getValue().getValueByLang(lang, i18NConfig.getDefaultLang());
    }

    // String[] <---> MultiLangValue<String[]>
    protected List<String> common_map_to_str_list(MultiLangValue<LangValue<List<String>>, List<String>> mValue, @Context String lang) {
        if (mValue == null) {
            return null;
        }
        String fallback = i18NConfig.getDefaultLang();
        return mValue.getValueByLang(lang, fallback);
    }


    // List<LangText> <---> MultiLangText
    protected Set<LangText> map_to_lang_text_set(MultiLangText mText) {
        if (mText == null) {
            return null;
        }
        return mText.getLangSet();
    }


    /**
     * 转换对象
     * 
     * @param data
     * @return
     */
    public abstract ItemSkuV2VO convertItemSku(ItemSkuDTO data);

    /**
     * sku的features
     * 
     * @param data
     * @return
     */
    public abstract List<ItemSkuFeaturesVO> convertItemSkuFeatures(List<ItemSkuFeaturesDTO> data);

    public abstract List<ItemDeadlineVO> convertItemDeadline(List<ItemDeadlineDTO> data);

    public abstract List<ItemCharacteristicsVO> convertItemCharacteristics(List<ItemCharacteristicsDTO> data);

    public abstract List<ItemMerchantInfoVO> convertItemMerchantInfo(List<ItemMerchantInfoDTO> data);

    public abstract List<ItemPageVO> convertItemBaseInfo(List<ItemBaseInfoDTO> data);

    public abstract ItemDetailV2VO convertItemDetailV2(ItemDetailDTO data);


    public abstract List<ItemMerchantInfoVO> convertToItemMerchantInfoVOList(List<SkuQuoteListVO> list);

    @Mapping(target = "quoteId", source = "vo.id")
    public abstract ItemMerchantInfoVO convertToItemMerchantInfoVOList(SkuQuoteListVO vo);

    // --add by zhaoqi 20250309
    @Mapping(target = "item", source = "source")
    @Mapping(target = "skuList", source = "source.skuList")
    @Mapping(target = "skuPropSkuMap", expression = "java(toSkuPropSkuMap(source.getSkuList()))")
    @Mapping(target = "productOptions", source = "source.skuPropList")
    @Mapping(target = "itemCatPropGroups", expression = "java(toItemCatPropGroupVos(source.getCatPropInfo()))")
    public abstract ItemDetailV2VO toItemDetailV2Vo(ItemDTO source);

    @Mapping(target = "title", expression = "java(multiLangConverter.mText_to_str(data.getTitle()))")
    @Mapping(target = "itemDesc", expression = "java(multiLangConverter.mText_to_str(data.getItemDesc()))")
    @Mapping(target = "pictureList", expression = "java(multiLangConverter.mValue_to_str_list(data.getPictureList()))")
    public abstract ItemVO itemDTOToItemVO(ItemDTO data);

    @Mapping(target = "pictureList", expression = "java(multiLangConverter.mValue_to_str_list(target.getPictureList()))")
    public abstract SkuVO toSkuVo(SkuDTO target);

    public static Map<String, Long> toSkuPropSkuMap(List<SkuDTO> list) {
        Map<String, Long> map = new HashMap<>();
        if(CollectionUtils.isEmpty(list)){
            return map;
        }
        
        list.forEach(sku -> {
            String key = sku.getSkuPropList().stream().map(SkuPropDTO::getVid).collect(Collectors.joining("_"));
            map.put(key, sku.getId());
        });
        return map;
    }

    @Mapping(target = "title", expression = "java(multiLangConverter.mText_to_str(souce.getName()))")
    @Mapping(target = "list", expression = "java(toItemProductOptionValueVos(souce.getValueList()))")
    public abstract ItemProductOptionVO toItemProductOptionVo(ItemSkuPropDTO souce);

    public abstract List<ItemProductOptionVO> toItemProductOptionVo(List<ItemSkuPropDTO> list);

    @Mapping(target = "picUrl", ignore = true)
    public abstract ItemProductOptionValueVO toItemProductOptionValueVo(ItemSkuValueDTO source);

    public abstract List<ItemProductOptionValueVO> toItemProductOptionValueVos(List<ItemSkuValueDTO> list);

    public List<ItemCatPropGroupVO> toItemCatPropGroupVos(List<ItemCatPropDTO> list) {
        if(CollectionUtils.isEmpty(list)){
            return Collections.emptyList();
        }
        
        Map<String, List<ItemCatPropDTO>> propertyGroupMap =
                list.stream().collect(Collectors.groupingBy(p -> p.getPropGroupId() + "," + multiLangConverter.mText_to_str(p.getPropGroupName())));
        return propertyGroupMap.entrySet().stream().map(entity -> {
            ItemCatPropGroupVO itemCatPropGroupVo = new ItemCatPropGroupVO();
            String[] split = entity.getKey().split(",");
            Long groupId = Long.valueOf(split[0]);
            String groupName = split[1];
            itemCatPropGroupVo.setId(groupId);
            itemCatPropGroupVo.setGroupSortNo(entity.getValue().get(0).getPropGroupSortNo());
            itemCatPropGroupVo.setGroupName(groupName);
            List<ItemCatPropValueVO> itemCatPropValueVos = toItemCatPropValueVos(entity.getValue()).stream().sorted(Comparator.comparing(ItemCatPropValueVO::getPropSortNo)).collect(Collectors.toList());
            itemCatPropGroupVo.setCategoryPropList(itemCatPropValueVos);
            Optional.ofNullable(null).orElse(new UnitDTO());
            return itemCatPropGroupVo;
        }).sorted(Comparator.comparing(ItemCatPropGroupVO::getGroupSortNo)).collect(Collectors.toList());
    }

    @Mapping(target = "propName", expression = "java(multiLangConverter.mText_to_str(source.getPropName()))")
    @Mapping(target = "propValueList", source = "source.propValue")
    @Mapping(target = "unit", source = "unitDTO")
    public abstract ItemCatPropValueVO toItemCatPropValueVo(ItemCatPropDTO source);

    public abstract List<ItemCatPropValueVO> toItemCatPropValueVos(List<ItemCatPropDTO> list);

    @Mapping(target = "value", expression = "java(multiLangConverter.mText_to_str(source.getValue()))")
    public abstract PropValueVO toPropValueVo(ItemCatPropValueDTO source);

    public abstract List<PropValueVO> toPropValueVos(List<ItemCatPropValueDTO> list);
}
