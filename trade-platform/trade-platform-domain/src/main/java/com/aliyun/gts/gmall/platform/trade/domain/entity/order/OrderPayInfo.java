package com.aliyun.gts.gmall.platform.trade.domain.entity.order;

import com.aliyun.gts.gmall.platform.trade.domain.entity.price.PayPrice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPayInfo {

    private PayPrice payPrice;
    private String payChannel;
}
