package com.aliyun.gts.gmall.center.trade.domain.entity.b2b;

import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemPriceInput {

    /**
     * SKU
     */
    private ItemSku itemSku;

    /**
     * 计算价格的数量
     */
    private Integer quantity;

    /**
     * 买家ID
     */
    private Long custId;


    // 非入参, 内部计算使用
    private boolean agreementPrice;

    // 非入参，是否代客下单价，覆盖阶梯价/协议价/一口价等逻辑
    private boolean isHelpOrderPrice;
    private Integer agreementMinCount;
}
