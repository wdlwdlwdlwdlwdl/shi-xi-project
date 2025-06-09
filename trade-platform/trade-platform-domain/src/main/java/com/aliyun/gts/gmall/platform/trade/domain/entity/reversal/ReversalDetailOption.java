package com.aliyun.gts.gmall.platform.trade.domain.entity.reversal;

import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderQueryOption;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReversalDetailOption {

    @ApiModelProperty("是否包含操作流水")
    private boolean includeFlows;

    @ApiModelProperty("是否包含售后原因内容")
    private boolean includeReason;

    @ApiModelProperty("是否包含订单信息")
    private boolean includeOrderInfo;

    @ApiModelProperty("包含订单信息时, 订单查询选项")
    private OrderQueryOption orderOption = OrderQueryOption.builder().build();
}
