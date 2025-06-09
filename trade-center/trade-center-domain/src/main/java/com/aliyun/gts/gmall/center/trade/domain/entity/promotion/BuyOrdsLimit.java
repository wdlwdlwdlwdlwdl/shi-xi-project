package com.aliyun.gts.gmall.center.trade.domain.entity.promotion;

import lombok.Data;

@Data
public class BuyOrdsLimit {

    private Long itemId;
    private Long skuId;
    private Long custId;
    private Long campId;
    private Long buyOrdCnt;     // 交易记录的下单数
    private Long ordsLimit;    // 营销下单单数限制规则
}
