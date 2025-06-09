package com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner;

import com.aliyun.gts.gmall.framework.domain.extend.ExtendComponent;
import lombok.Data;

@Data
public class StepOrderFeatureDO extends ExtendComponent {

    /**
     * 支付时间
     */
    private Long payTime;
    /**
     * 卖家处理时间
     */
    private Long sellerHandleTime;
    /**
     * 买家确认时间
     */
    private Long customerConfirmTime;

    /**
     * 支付渠道
     */
    private String payChannel;

    /**
     * 支付cartId
     */
    private String payCartId;
}
