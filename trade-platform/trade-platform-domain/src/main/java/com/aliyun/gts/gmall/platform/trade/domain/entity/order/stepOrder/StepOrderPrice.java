package com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder;

import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.gts.gmall.framework.domain.extend.ExtendComponent;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.ConfirmPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.PayPrice;
import com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StepOrderPrice extends ExtendComponent implements PayPrice {

    /**
     * 实付现金
     */
    private Long realAmt;

    /**
     * 积分金额
     */
    private Long pointAmt;

    /**
     * 积分个数
     */
    private Long pointCount;

    /**
     * 总金额
     */
    @JSONField(serialize = false)
    public Long getTotalAmt() {
        return NumUtils.getNullZero(realAmt) + NumUtils.getNullZero(pointAmt);
    }

    /**
     * 确认收货金额, 用于打款、分账等
     */
    private ConfirmPrice confirmPrice;

    @Override
    public Long getOrderRealAmt() {
        return realAmt;
    }


    // ============= 改价 =============

    @ApiModelProperty(value = "pointAmt 金额改动, 正改大、负改小")
    private Long adjustPointAmt;

    @ApiModelProperty(value = "realAmt 金额改动, 正改大、负改小")
    private Long adjustRealAmt;

    @ApiModelProperty(value = "pointCount 数量改动, 正改大、负改小")
    private Long adjustPointCount;
}
