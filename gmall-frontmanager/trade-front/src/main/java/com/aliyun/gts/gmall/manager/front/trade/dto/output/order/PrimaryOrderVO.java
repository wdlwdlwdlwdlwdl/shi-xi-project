package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import com.aliyun.gts.gmall.platform.trade.common.util.NumUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 创建主订单结果
 *
 * @author tiansong
 */
@Data
@ApiModel("创建主订单结果")
public class PrimaryOrderVO {
    @ApiModelProperty("主订单ID")
    private Long   primaryOrderId;

    @ApiModelProperty("支付单ID")
    private String paymentId;

    @ApiModelProperty("支付金额--实付 (多阶段首笔)")
    private Long payRealAmt;

    @ApiModelProperty("支付金额--现金 (多阶段首笔)")
    private Long payPointAmt;

    public long getPayTotalAmt() {
        return NumUtils.getNullZero(payRealAmt) + NumUtils.getNullZero(payPointAmt);
    }

    public boolean isFullDeduct() {
        return NumUtils.getNullZero(payRealAmt) == 0L;
    }


    public String toMergeParam() {
        return primaryOrderId
                + ":" + NumUtils.getNullZero(payRealAmt)
                + ":" + NumUtils.getNullZero(payPointAmt);
    }

    public static PrimaryOrderVO fromMergeParam(String s) {
        String[] sp = StringUtils.split(s, ':');
        PrimaryOrderVO r = new PrimaryOrderVO();
        r.primaryOrderId = Long.parseLong(sp[0]);
        r.payRealAmt = Long.parseLong(sp[1]);
        r.payPointAmt = Long.parseLong(sp[2]);
        return r;
    }

    public static boolean isMergeParamMatch(String merge, Long primaryOrderId) {
        return merge != null && merge.startsWith(primaryOrderId + ":");
    }
}
