package com.aliyun.gts.gmall.platform.trade.domain.wrapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderPayQueryWrapper {

    /**
     * 顾客编号
     */
    private String custId;
    /**
     * 支付单ID-支付业务主键
     */
    private String payId;
}
