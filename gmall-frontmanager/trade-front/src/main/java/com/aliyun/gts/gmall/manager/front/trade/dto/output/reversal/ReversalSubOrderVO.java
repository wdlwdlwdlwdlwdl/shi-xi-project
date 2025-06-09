package com.aliyun.gts.gmall.manager.front.trade.dto.output.reversal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/10/25 17:33
 */
@Data
public class ReversalSubOrderVO {
    @ApiModelProperty(value = "子订单ID", required = true)
    private Long orderId;

    @ApiModelProperty(value = "可申请售后的商品数量")
    private Integer maxCancelQty;

    @ApiModelProperty(value = "可申请退款金额")
    private Long maxCancelAmt;

    @ApiModelProperty(value = "当前是否可申请售后, 子单售后进行中、超过售后申请时间、可退金额为0等时候不可申请")
    private boolean allowReversal;

    @ApiModelProperty(value = "是否为运费子单")
    private Boolean freight;

    @ApiModelProperty(value = "含运费金额")
    private Long maxFreightAmt;
}
