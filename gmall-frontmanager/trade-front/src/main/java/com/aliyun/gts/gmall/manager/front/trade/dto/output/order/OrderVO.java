package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class OrderVO {

    @ApiModelProperty("订单ID")
    private Long orderId;

    @ApiModelProperty("订单状态")
    private Integer orderStatus;

    @ApiModelProperty("订单状态")
    private String orderStatusName;

    @ApiModelProperty("是否评价, OrderEvaluateEnum")
    private Integer evaluate;

    @ApiModelProperty("订单创建时间")
    Date createTime;

    @ApiModelProperty("价格")
    OrderPriceVO price;

    @ApiModelProperty("订单自定义标, 进搜索 (orderAttr.tags)")
    List<String> tags;
}
