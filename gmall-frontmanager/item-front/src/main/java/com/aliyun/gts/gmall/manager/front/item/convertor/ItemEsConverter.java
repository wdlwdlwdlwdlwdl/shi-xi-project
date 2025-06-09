package com.aliyun.gts.gmall.manager.front.item.convertor;

import java.util.List;
import java.util.Objects;
import javax.annotation.Resource;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.util.CollectionUtils;
import com.aliyun.gts.gmall.center.item.common.enums.ItemType;
import com.aliyun.gts.gmall.framework.config.I18NConfig;
import com.aliyun.gts.gmall.manager.front.item.dto.output.sku.SkuQuoteListVO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.LoanPeriodDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuQuoteCityPriceEsDTO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuQuoteEsDTO;
import com.aliyun.gts.gmall.platform.item.common.enums.ItemSourceTypeEnum;
import com.aliyun.gts.gmall.platform.item.common.enums.ItemStatus;

@Mapper(componentModel = "spring")
public abstract class ItemEsConverter {
    @Resource
    private I18NConfig i18NConfig;

    // --------------------------------------------------esToSku引用Vo--------------------------------------------------
    @Mapping(target = "title", expression = "java(toSkuOfferVO_title(target, lang))") // 标题
    @Mapping(target = "sourceTypeName", expression = "java(toSkuOfferVO_sourceTypeName(target))") // 业务来源
    @Mapping(target = "type", source = "itemType") // 商品类型
    @Mapping(target = "typeName", expression = "java(toSkuOfferVO_typeName(target))") // 商品类型名称
    @Mapping(target = "itemStatusName", expression = "java(toSkuOfferVO_statusName(target))") // 商品状态名称
    @Mapping(target = "mapStatus", source = "target.skuQuoteMapStatus") // 商品sku map状态
    @Mapping(target = "mapStatusName", expression = "java(toSkuOfferVO_mapStatusName(target))") // 商品sku map状态名称
    @Mapping(target = "sellerName", source = "target.sellerInfo.name") // 商家名称
    @Mapping(target = "saleCount30", source = "target.saleStatisticsInfo.saleCount30") // 30天销售数量
    @Mapping(target = "totalSaleCount", source = "target.saleStatisticsInfo.totalSaleCount") // 30天销售数量
    @Mapping(target = "isOfficialDistributor", expression = "java(isOfficialDistributor(target))")
    @Mapping(target = "amortizePrice", expression = "java(getAmortizePrice(target))")
    @Mapping(target = "price", source = "target.basePrice")
    @Mapping(target = "originPrice", source = "target.basePrice")
    public abstract SkuQuoteListVO toVo(SkuQuoteEsDTO target, @Context String lang);

    public String toSkuOfferVO_title(SkuQuoteEsDTO target, @Context String lang) {
        String fallback = i18NConfig.getDefaultLang();
        return target.getSkuName().getValueByLang(lang, fallback);
    }

    public String toSkuOfferVO_sourceTypeName(SkuQuoteEsDTO target) {
        return ItemSourceTypeEnum.valueOf(target.getSourceType()).getDesc();
    }

    public String toSkuOfferVO_typeName(SkuQuoteEsDTO target) {
        return ItemType.valueOf(target.getItemType()).getDesc();
    }

    public String toSkuOfferVO_statusName(SkuQuoteEsDTO target) {
        return ItemStatus.valueOf(target.getItemStatus()).getDesc();
    }

    public String toSkuOfferVO_mapStatusName(SkuQuoteEsDTO target) {
        return ItemStatus.valueOf(target.getSkuQuoteMapStatus()).getDesc();
    }

    public Boolean isOfficialDistributor(SkuQuoteEsDTO target) {
        return Objects.equals(1, target.getSellerInfo().getOp());
    }

    public Long getAmortizePrice(SkuQuoteEsDTO target) {
        List<SkuQuoteCityPriceEsDTO> cityPriceInfo = target.getCityPriceInfo();
        if (CollectionUtils.isEmpty(cityPriceInfo)) {
            return 0L;
        }
        return cityPriceInfo.get(0).getPriceList().stream().filter(it -> Objects.equals(it.getType(), target.getLoanPeriodType())).map(LoanPeriodDTO::getValue)
                .findFirst().orElse(0L);
    }
}
