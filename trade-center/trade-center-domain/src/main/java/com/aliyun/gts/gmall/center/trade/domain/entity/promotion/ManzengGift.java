package com.aliyun.gts.gmall.center.trade.domain.entity.promotion;

import lombok.Data;

import java.util.List;

@Data
public class ManzengGift {

    /**
     * 赠品订单ID (子订单)
     */
    private Long giftOrder;

    /**
     * 主商品订单ID (子订单)
     */
    private List<Long> refOrders;

    /**
     * 下单时用的临时ID
     */
    private Boolean tempId;
    /**
     * 活动ID
     */
    private Long campId;

    public boolean isTempId() {
        return tempId != null && tempId.booleanValue();
    }
}
