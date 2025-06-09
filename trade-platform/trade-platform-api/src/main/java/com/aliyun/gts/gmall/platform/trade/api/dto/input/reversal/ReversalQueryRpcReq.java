package com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractPageQueryRpcRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("售后列表查询")
public class ReversalQueryRpcReq extends AbstractPageQueryRpcRequest {

    @ApiModelProperty("卖家ID")
    private Long sellerId;

    @ApiModelProperty("卖家BIN")
    private String sellerBIN;

    @ApiModelProperty("卖家名称")
    private String sellerName;

    @ApiModelProperty("顾客ID")
    private String custId;

    @ApiModelProperty("客户名字")
    private String firstName;

    @ApiModelProperty("客户姓氏")
    private String lastName;

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

    @ApiModelProperty(value = "订单id")
    private Long orderId;

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
    @ApiModelProperty("售后原因code")
    private Integer reversalReasonCode;
}
