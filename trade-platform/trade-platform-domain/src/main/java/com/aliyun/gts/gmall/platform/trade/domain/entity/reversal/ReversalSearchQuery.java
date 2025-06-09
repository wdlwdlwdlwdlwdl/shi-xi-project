package com.aliyun.gts.gmall.platform.trade.domain.entity.reversal;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ReversalSearchQuery {

    @ApiModelProperty(value = "分页num, 1开始")
    private Integer pageNum;

    @ApiModelProperty(value = "分页size")
    private Integer pageSize;

    @ApiModelProperty("卖家ID")
    private Long sellerId;

    @ApiModelProperty("卖家BIN")
    private String sellerBin;

    @ApiModelProperty("卖家姓名")
    private String sellerName;

    @ApiModelProperty("顾客ID")
    private String custId;

    @ApiModelProperty("客户姓氏")
    private String customerLastName;

    @ApiModelProperty("客户名字")
    private String customerFirstName;

    @ApiModelProperty(value = "商品名称关键字")
    private String itemTitle;

    @ApiModelProperty(value = "售后单状态集, ReversalStatusEnum")
    private List<Integer> reversalStatus;

    @ApiModelProperty(value = "售后单创建时间范围")
    private DateRangeParam createTime;

    @ApiModelProperty(value = "订单创建时间范围")
    private DateRangeParam orderTime;

    @ApiModelProperty(value = "售后完成时间")
    private DateRangeParam refundCompletedTime;

    @ApiModelProperty(value = "主订单id")
    private Long primaryOrderId;

    @ApiModelProperty(value = "主售后单id")
    private Long primaryReversalId;

    @ApiModelProperty(value = "售后单id")
    private Long reversalId;

    @ApiModelProperty(value = "售后单类型, ReversalTypeEnum(仅退款、退货退款)")
    private Integer reversalType;

    @ApiModelProperty(value = "发起售后的订单状态, OrderStatusEnum")
    private List<Integer> fromOrderStatus;

    @ApiModelProperty(value = "订单发货时间范围")
    private DateRangeParam orderSendTime;

    @ApiModelProperty(value = "订单确认收货时间范围")
    private DateRangeParam orderConfirmReceiveTime;

    @ApiModelProperty(value = "订单物流编号")
    private String orderLogisticsNo;

    @ApiModelProperty(value = "退货物流编号")
    private String reversalLogisticsNo;

    @ApiModelProperty("退回发货时间")
    private DateRangeParam reversalSendTime;

    @ApiModelProperty("退回确认收货时间")
    private DateRangeParam reversalConfirmReceiveTime;

    @ApiModelProperty(value = "查询结果是否包含订单数据")
    private boolean includeOrderInfo = false;

    @ApiModelProperty(value = "扩展的请求参数")
    private Map<String, String> extra;
}
