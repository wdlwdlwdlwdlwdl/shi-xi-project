package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query;

import com.aliyun.gts.gmall.framework.api.dto.AbstractInputParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderStatusInfo extends AbstractInputParam {

    @ApiModelProperty(value = "订单状态")
    private Integer orderStatus;

    @ApiModelProperty(value = "阶段号")
    private Integer stepNo;

    @ApiModelProperty(value = "阶段状态")
    private Integer stepStatus;


    public OrderStatusInfo(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }
}
