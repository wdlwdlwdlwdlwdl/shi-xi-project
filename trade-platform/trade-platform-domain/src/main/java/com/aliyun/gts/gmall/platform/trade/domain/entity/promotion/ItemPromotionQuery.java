package com.aliyun.gts.gmall.platform.trade.domain.entity.promotion;

import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ItemPromotionQuery {

    private List<ItemSku> list;
    private Long custId;
    private String channel;
    private String promotionSource;
}
