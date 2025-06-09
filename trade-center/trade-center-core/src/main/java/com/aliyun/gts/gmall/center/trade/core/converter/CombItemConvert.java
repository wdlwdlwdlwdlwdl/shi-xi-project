package com.aliyun.gts.gmall.center.trade.core.converter;

import com.aliyun.gts.gmall.center.trade.api.dto.output.CombineItemDTO;
import com.aliyun.gts.gmall.center.trade.api.dto.output.ReversalCombItemDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/10/21 18:58
 */
@Mapper(componentModel = "spring")
public interface CombItemConvert {
    @Mappings({
            @Mapping(target = "itemPrice", ignore = true),
    })
    CombineItemDTO convert(ItemSku dto);

    ReversalCombItemDTO convert(CombineItemDTO sku);

    default CombineItemDTO defConvert(ItemSku itemSku) {
        CombineItemDTO combineItemDTO = convert(itemSku);
        if (combineItemDTO == null) {
            return null;
        }
        combineItemDTO.setItemPrice(itemSku.getItemPrice().getOriginPrice());
        if (itemSku.getSkuPic() != null) {
            combineItemDTO.setItemPic(itemSku.getSkuPic());
        }
        return combineItemDTO;
    }
}
