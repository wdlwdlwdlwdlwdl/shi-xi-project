package com.aliyun.gts.gmall.center.trade.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaySettleCommissionAccount {

    /**
     * 分账付款方
     */
    private String payerId;

    /**
     * 分账收款方
     */
    private String payeeId;

}
