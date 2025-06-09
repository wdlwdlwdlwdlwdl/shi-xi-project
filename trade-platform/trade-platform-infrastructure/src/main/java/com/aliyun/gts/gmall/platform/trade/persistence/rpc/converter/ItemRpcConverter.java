
package com.aliyun.gts.gmall.platform.trade.persistence.rpc.converter;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.config.I18NConfig;
import com.aliyun.gts.gmall.framework.convert.MultiLangConverter;
import com.aliyun.gts.gmall.framework.i18n.LangText;
import com.aliyun.gts.gmall.platform.item.api.dto.input.item.SkuQuoteQueryCacheReq;
import com.aliyun.gts.gmall.platform.item.api.dto.output.category.CategoryDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.ItemDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.ItemExtendDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuPropDTO;
import com.aliyun.gts.gmall.platform.promotion.common.model.SelectedProm;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemCategory;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemCategoryBrief;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.PromotionOption;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class ItemRpcConverter {

    @Resource
    private I18NConfig i18NConfig;

    @Resource
    private MultiLangConverter multiLangConverter;

    @Mappings({
            @Mapping(target = "skuId", source = "sku.id"),
            @Mapping(target = "itemId", source = "sku.itemId"),
            @Mapping(target = "seller.sellerId", source = "item.sellerId"),
            @Mapping(target = "seller.sellerName", ignore = true),
            /*@Mapping(target = "skuQty", source = "sku.sellableQuantity"),*/
            @Mapping(target = "skuDesc", expression = "java(toSkuDesc(sku))"),
            @Mapping(target = "itemTitle", expression = "java(toItemTitle(item))"),
            //@Mapping(target = "itemTitle", source = "item.title"),
            @Mapping(target = "itemPic", expression = "java(toItemPic(item))"),
            //@Mapping(target = "itemPic", source = "item.pictureList", qualifiedByName = "getMainPic"),
            @Mapping(target = "skuPic", source = "sku.skuPropList", qualifiedByName = "getSkuPic"),
            /* @Mapping(target = "itemPrice.originPrice", source = "sku.price"),*/
            @Mapping(target = "itemStatus", source = "item.status"),
            @Mapping(target = "skuStatus", source = "sku.status"),
            /*@Mapping(target = "weight", source = "sku.weight"),*/
            @Mapping(target = "itemType", source = "item.itemType"),
            @Mapping(target = "itemFeatureMap", source = "item.featureMap"),
            @Mapping(target = "skuFeatureMap", source = "sku.featureMap"),
            @Mapping(target = "itemExtendMap", source = "item.extendMap", qualifiedByName = "getExtendMap"),
            @Mapping(target = "itemPrice.itemPrice", ignore = true),
            @Mapping(target = "categoryId", source = "item.categoryId"),
            @Mapping(target = "brandName", source = "item.brandName"),
            @Mapping(target = "categoryDetails", expression = "java(toCategoryDetails(item))"),
            @Mapping(target = "brandId", source = "item.brandId"),
            @Mapping(target = "merchantSkuCode", source = "sku.quote.sellerSkuCode")
            /*        @Mapping(target = "online", source = "item.online")*/
    })
    public abstract ItemSku toItemSku(SkuDTO sku, ItemDTO item);

    public abstract SkuQuoteQueryCacheReq toSkuQuote(ItemSkuId itemSkuId);

    protected String toItemPic(ItemDTO item) {
        List<String> pics = item.getPictureList().getValueByLang(LocaleContextHolder.getLocale().getLanguage(), i18NConfig.getDefaultLang());
        if (pics != null && pics.get(0) != null) {
            return pics.get(0);
        }
        return null;
    }

    protected String toItemTitle(ItemDTO item) {
        if (item.getTitle() != null) {
           return JSON.toJSONString(item.getTitle());
//            Optional<LangText> optionalLangText = item.getTitle().getLangTextByLang(LocaleContextHolder.getLocale().getLanguage());
//            if (optionalLangText.isPresent()) {
//                return optionalLangText.get().getValue();
//            }
        }
        return null;
    }

    protected String toSkuDesc(SkuDTO skuDTO) {
        if (skuDTO.getName() != null) {
            return  JSON.toJSONString(skuDTO.getName());
//            Optional<LangText> optionalLangText = item.getTitle().getLangTextByLang(LocaleContextHolder.getLocale().getLanguage());
//            if (optionalLangText.isPresent()) {
//                return optionalLangText.get().getValue();
//            }
        }
        return null;
    }

    protected String toCategoryDetails(ItemDTO item) {
        if (CollectionUtils.isEmpty(item.getCategoryList())) {
            return null;
        }
        List<ItemCategoryBrief> list = item.getCategoryList().stream()
                .map(this::toItemCategoryInfo)
                .toList();
        return JSON.toJSONString(list);
    }

    public abstract ItemCategoryBrief toItemCategoryInfo(CategoryDTO category);

    @Named("getExtendMap")
    protected Map<String, String> getExtendMap(Map<String, ItemExtendDTO> map) {
        if (map == null || map.isEmpty()) {
            return new HashMap<>();
        }
        return map.values().stream().collect(Collectors.toMap(
                ext -> ext.getAttrKey(), ext -> ext.getAttrValue()));
    }

    @Named("getSkuDesc")
    protected String getSkuDesc(List<SkuPropDTO> props) {
        String lang = LocaleContextHolder.getLocale().getLanguage();
        String fallback = i18NConfig.getDefaultLang();
        StringBuilder s = new StringBuilder();
        for (SkuPropDTO prop : props) {
            s.append(prop.getName()).append(':').append(prop.getValue()).append(';');
        }
        if (s.length() > 0) {
            s.setLength(s.length() - 1);
        }
        return s.toString();
    }

//    @Named("getSkuDesc")
//    protected String getSkuDesc(List<SkuPropDTO> props) {
//        String lang = LocaleContextHolder.getLocale().getLanguage();
//        String fallback = i18NConfig.getDefaultLang();
//
//        StringBuilder s = new StringBuilder();
//        for (SkuPropDTO prop : props) {
//            s
//                    .append(prop.getName() == null ? null : prop.getName().getValueByLang(lang, fallback))
//                    .append(':')
//                    .append(prop.getValue() == null ? null : prop.getValue().getValueByLang(lang, fallback))
//                    .append(';');
//        }
//        if (s.length() > 0) {
//            s.setLength(s.length() - 1);
//        }
//        return s.toString();
//    }


    @Named("getMainPic")
    protected String getMainPic(List<String> picList) {
        return CollectionUtils.isEmpty(picList) ? null : picList.get(0);
    }

//    @Named("getMainPic")
//    protected String getMainPic(MultiLangValue<LangValue<List<String>>, List<String>> picList) {
//        if (picList == null) {
//            return null;
//        }
//        List<String> pics = picList.getValueByLang(LocaleContextHolder.getLocale().getLanguage(), i18NConfig.getDefaultLang());
//        return CollectionUtils.isEmpty(pics) ? null : pics.get(0);
//    }

    @Named("getSkuPic")
    protected String getSkuPic(List<SkuPropDTO> props) {
        if (CollectionUtils.isEmpty(props)) {
            return null;
        }
        return props.stream()
                .map(prop -> prop.getPicUrl())
                .filter(pic -> pic != null)
                .findFirst()
                .orElse(null);
    }

//    @Named("getSkuPic")
//    protected String getSkuPic(List<SkuPropDTO> props) {
//        if (CollectionUtils.isEmpty(props)) {
//            return null;
//        }
//        return props.stream()
//                .map(prop -> prop.getPicUrl())
//                .filter(pic -> pic != null)
//                .findFirst()
//                .orElse(null);
//    }

    @Mappings({
            @Mapping(target = "name", expression = "java(toCategoryName(category))"),
    })
    public abstract ItemCategory toItemCategory(CategoryDTO category);


    protected String toCategoryName(CategoryDTO category) {
        if (category.getName() != null) {
            Optional<LangText> optionalLangText = category.getName().getLangTextByLang(LocaleContextHolder.getLocale().getLanguage());
            if (optionalLangText.isPresent()) {
                return optionalLangText.get().getValue();
            }
        }
        return null;
    }

//    @Mapping(target = "name", expression = "java(filterByLang(category.getName()))")
//    public abstract ItemCategory toItemCategory(CategoryDTO category);
//
//
//    protected String filterByLang(MultiLangText name){
//        return name.getValueByLang(LocaleContextHolder.getLocale().getLanguage());
//    }
}
