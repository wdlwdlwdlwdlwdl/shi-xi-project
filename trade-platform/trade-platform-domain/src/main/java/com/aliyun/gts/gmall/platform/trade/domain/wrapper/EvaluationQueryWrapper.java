package com.aliyun.gts.gmall.platform.trade.domain.wrapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationQueryWrapper {


    private Long sellerId;

    private Long skuId;

    private Long itemId;

    private Long primaryOrderId;

    private Long orderId;

}
