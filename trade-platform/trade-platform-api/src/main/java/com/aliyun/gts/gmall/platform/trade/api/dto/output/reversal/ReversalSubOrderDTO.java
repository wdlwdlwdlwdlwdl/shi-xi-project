package com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal;

import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
@ApiModel(value = "可申请售后数量查询结果")
public class ReversalSubOrderDTO extends AbstractOutputInfo {

    @ApiModelProperty(value = "子订单ID", required = true)
    private Long orderId;

    @ApiModelProperty(value = "可申请售后的商品数量")
    private Integer maxCancelQty;

    @ApiModelProperty(value = "可申请退款金额")
    private Long maxCancelAmt;

    @ApiModelProperty(value = "当前是否可申请售后, 子单售后进行中、超过售后申请时间、可退金额为0等时候不可申请")
    private boolean allowReversal;

    @ApiModelProperty(value = "额外扩展信息")
    private Map<String,String> feature;
}
