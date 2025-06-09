package com.aliyun.gts.gmall.platform.trade.domain.wrapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaySplitFlowQueryWrapper {

    private Long id;
    /**
     * 顾客编号
     */
    private Long custId;

    /**
     * 主订单号
     */
    private Long primaryOrderId;

    /**
     * 分账关联的支付流水、可对应正向或者退款流水
     */
    private String relationFlowId;

    /**
     * 分账流水ID
     */
    private String splitFlowId;

    /**
     * 分账流水类型：1正向支付 2逆向退款
     */
    private Integer splitFlowType;

}
