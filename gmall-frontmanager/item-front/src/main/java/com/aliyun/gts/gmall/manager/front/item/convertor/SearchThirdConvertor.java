package com.aliyun.gts.gmall.manager.front.item.convertor;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.aliyun.gts.gmall.framework.convert.MultiLangConverter;
import com.aliyun.gts.gmall.framework.i18n.LangEnum;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.RelatedSkuSuggestExtendThirdRestQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.SearchActionSuggestThirdRestQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.SkuSearchPageBrandExtendThirdRestQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.SkuSearchPageCategoryExtendThirdRestQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.SkuSearchPageSellerExtendThirdRestQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.SkuSearchPageSkuExtendThirdRestQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.input.query.SkuSearchPageThirdRestQuery;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ActionSuggestVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.RelatedSkuSearchThirdVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.SearchThirdItemVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.SkuSearchPageThirdVO;
import com.aliyun.gts.gmall.searcher.api.dto.input.RelatedSkuSuggestExtendThirdQuery;
import com.aliyun.gts.gmall.searcher.api.dto.input.SearchActionSuggestThirdQuery;
import com.aliyun.gts.gmall.searcher.api.dto.input.SkuSearchPageBrandExtendThirdQuery;
import com.aliyun.gts.gmall.searcher.api.dto.input.SkuSearchPageCategoryExtendThirdQuery;
import com.aliyun.gts.gmall.searcher.api.dto.input.SkuSearchPageSellerExtendThirdQuery;
import com.aliyun.gts.gmall.searcher.api.dto.input.SkuSearchPageSkuExtendThirdQuery;
import com.aliyun.gts.gmall.searcher.api.dto.input.SkuSearchPageThirdQuery;
import com.aliyun.gts.gmall.searcher.api.dto.output.item.ActionSuggestDTO;
import com.aliyun.gts.gmall.searcher.api.dto.output.item.RelatedSkuSearchThirdDTO;
import com.aliyun.gts.gmall.searcher.api.dto.output.item.SearchThirdItemDTO;
import com.aliyun.gts.gmall.searcher.api.dto.output.item.SkuSearchPageThirdDTO;

@Mapper(componentModel = "spring", imports = {CustDTO.class})
public abstract class SearchThirdConvertor {
    @Autowired
    protected MultiLangConverter multiLangConverter;

    // SkuSearchPageThirdQuery <---> SkuSearchPageThirdRestQuery
    @Mapping(target = "userId", source = "user.custId")
    public abstract SkuSearchPageThirdQuery toSkuSearchPageThirdQuery(SkuSearchPageThirdRestQuery source, CustDTO user);

    // SearchActionSuggestThirdQuery <---> SearchActionSuggestThirdRestQuery
    public abstract SearchActionSuggestThirdQuery toSearchActionSuggestThirdQuery(SearchActionSuggestThirdRestQuery source);

    // RelatedSkuSuggestExtendThirdQuery <---> RelatedSkuSuggestExtendThirdRestQuery
    @Mapping(target = "userId", source = "user.custId")
    public abstract RelatedSkuSuggestExtendThirdQuery toRelatedSkuSuggestExtendThirdQuery(RelatedSkuSuggestExtendThirdRestQuery source, CustDTO user);

    // SkuSearchPageCategoryExtendThirdQuery <---> SkuSearchPageCategoryExtendThirdRestQuery
    @Mapping(target = "userId", source = "user.custId")
    public abstract SkuSearchPageCategoryExtendThirdQuery toSkuSearchPageCategoryExtendThirdQuery(SkuSearchPageCategoryExtendThirdRestQuery source,
            CustDTO user);

    // SkuSearchPageBrandExtendThirdQuery <---> SkuSearchPageBrandExtendThirdRestQuery
    @Mapping(target = "userId", source = "user.custId")
    public abstract SkuSearchPageBrandExtendThirdQuery toSkuSearchPageBrandExtendThirdQuery(SkuSearchPageBrandExtendThirdRestQuery source, CustDTO user);

    // SkuSearchPageSellerExtendThirdQuery <---> SkuSearchPageSellerExtendThirdRestQuery
    @Mapping(target = "userId", source = "user.custId")
    public abstract SkuSearchPageSellerExtendThirdQuery toSkuSearchPageSellerExtendThirdQuery(SkuSearchPageSellerExtendThirdRestQuery source, CustDTO user);

    // SkuSearchPageSkuExtendThirdQuery <---> SkuSearchPageSkuExtendThirdRestQuery
    @Mapping(target = "userId", source = "user.custId")
    public abstract SkuSearchPageSkuExtendThirdQuery toSkuSearchPageSkuExtendThirdQuery(SkuSearchPageSkuExtendThirdRestQuery target, CustDTO user);

    // RelatedSkuSearchThirdVO <---> SkuSearchPageThirdDTO
    public abstract SkuSearchPageThirdVO toSkuSearchPageThirdVo(SkuSearchPageThirdDTO source);

    // SearchThirdItemVO <---> SearchThirdItemDTO
    @Mapping(target = "titleHighlight", expression = "java(toHighLight(source))")
    public abstract SearchThirdItemVO toSearchThirdItemVo(SearchThirdItemDTO source);

    // RelatedSkuSearchThirdVO <---> RelatedSkuSearchThirdDTO
    public abstract RelatedSkuSearchThirdVO toRelatedSkuSearchThirdVo(RelatedSkuSearchThirdDTO source);

    String toHighLight(SearchThirdItemDTO source) {
        String defaultLang = multiLangConverter.getDefaultLang();
        if (LangEnum.RU.getName().equals(defaultLang)) {
            return source.getTitleHighlightRu().getValue();
        } else if (LangEnum.KK.getName().equals(defaultLang)) {
            return source.getTitleHighlightKk().getValue();
        } else if (LangEnum.EN.getName().equals(defaultLang)) {
            return source.getTitleHighlightEn().getValue();
        } else {
            return null;
        }
    }

    // ActionSuggestVO <---> ActionSuggestDTO
    public abstract ActionSuggestVO toActionSuggestVo(ActionSuggestDTO source);
}
