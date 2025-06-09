package com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner;

import com.alibaba.fastjson.annotation.JSONField;
import com.aliyun.gts.gmall.framework.domain.extend.ExtendComponent;
import com.aliyun.gts.gmall.platform.trade.domain.util.CommUtils;
import com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class RefundFee extends ExtendComponent {

    @ApiModelProperty("取消积分金额, cancelRealAmt+cancelPointAmt+freightAmt = cancelAmt")
    private Long cancelPointAmt;

    @ApiModelProperty("取消积分数量")
    private Long cancelPointCount;

    @ApiModelProperty("取消现金金额, cancelRealAmt+cancelPointAmt+freightAmt = cancelAmt")
    private Long cancelRealAmt;

    @ApiModelProperty("运费金额, freightAmt")
    private Long freightAmt;

    private Boolean freightRefunded = false;;

    @ApiModelProperty("退款现金分账金额, 合计等于cancelRealAmt, 分账规则根据子订单上的SeparateRule")
    private Map<String, Long> cancelSeparateRealAmt;

    @ApiModelProperty("退款积分分账金额, 合计等于cancelPointAmt, 分账规则根据子订单上的SeparateRule")
    private Map<String, Long> cancelSeparatePointAmt;

    @ApiModelProperty("退款分账总金额, cancelSeparateRealAmt + cancelSeparatePointAmt")
    @JSONField(serialize = false)
    public Map<String, Long> getCancelSeparateAmt() {
        return CommUtils.addMergeLong(cancelSeparateRealAmt, cancelSeparatePointAmt);
    }

    public Long getCancelTotalAmt() {
        return NumUtils.getNullZero(cancelPointAmt) + NumUtils.getNullZero(cancelRealAmt)+ NumUtils.getNullZero(freightAmt);
    }
}
