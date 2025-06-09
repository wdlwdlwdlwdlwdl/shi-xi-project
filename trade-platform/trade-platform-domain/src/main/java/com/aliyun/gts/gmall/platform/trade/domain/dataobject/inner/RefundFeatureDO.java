package com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner;

import com.aliyun.gts.gmall.framework.domain.extend.ExtendComponent;
import lombok.Data;

@Data
public class RefundFeatureDO extends ExtendComponent {

    /**
     * 退现金金额
     */
    private Long refundRealAmount;
    /**
     * 退积分金额
     */
    private Long refundPointAmount;
    /**
     * 退积分数量
     */
    private Long refundPointCount;
}
