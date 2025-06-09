package com.aliyun.gts.gmall.manager.front.item.convertor;

import java.util.List;

import com.aliyun.gts.gmall.platform.item.api.dto.output.item.ItemSellerInfoEsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.BeanUtils;
import com.aliyun.gts.gmall.framework.convert.CommonConvert;
import com.aliyun.gts.gmall.framework.convert.MultiLangConverter;
import com.aliyun.gts.gmall.manager.front.item.dto.temp.ItemSaleSellerTempVO;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuQuoteEsDTO;


@Mapper(componentModel = "spring", imports = {MultiLangConverter.class, CommonConvert.class, BeanUtils.class})
public abstract class ItemCardConvertor {
    // ItemSaleSellerTempVO <---> SkuQuoteEsDTO
    @Mapping(target = "skuQuoteId", source = "id")
    @Mapping(target = "totalSaleCount", source = "saleStatisticsInfo.totalSaleCount")
    @Mapping(target = "op", expression = "java(getSellerOp(target))")
    public abstract ItemSaleSellerTempVO toItemSaleSellerTempVo(SkuQuoteEsDTO target);

    public abstract List<ItemSaleSellerTempVO> toItemSaleSellerTempVos(List<SkuQuoteEsDTO> list);

    public static ItemSaleSellerTempVO copy(ItemSaleSellerTempVO source) {
        if (source == null) {
            return null;
        }
        ItemSaleSellerTempVO newItem = new ItemSaleSellerTempVO();
        BeanUtils.copyProperties(source, newItem); // 使用 Spring 的 BeanUtils 进行属性复制
        return newItem;
    }

    public Boolean getSellerOp(SkuQuoteEsDTO target) {
        ItemSellerInfoEsDTO sellerInfo = target.getSellerInfo();
        if (sellerInfo == null) {
            return false;
        }
        Integer op = sellerInfo.getOp();
        return op != null && op == 1;
    }

}
