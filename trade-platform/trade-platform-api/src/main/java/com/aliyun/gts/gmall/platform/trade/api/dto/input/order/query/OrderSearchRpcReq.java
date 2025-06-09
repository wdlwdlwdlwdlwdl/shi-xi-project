package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query;

import com.aliyun.gts.gmall.platform.trade.api.dto.common.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel("通用订单搜索接口")
public class OrderSearchRpcReq extends PageQuery {

    @ApiModelProperty(value = "卖家ID, matchAny")
    private List<Long> sellerId;

    @ApiModelProperty(value = "买家ID, matchAny")
    private List<Long> custId;

    @ApiModelProperty(value = "商品名称关键字")
    private String itemTitle;

    @ApiModelProperty(value = "订单状态, matchAny")
    private List<OrderStatusInfo> status;

    @ApiModelProperty(value = "订单创建时间开始")
    private Date startTime;

    @ApiModelProperty(value = "订单创建时间结束")
    private Date endTime;

    @ApiModelProperty(value = "主订单id, matchAny")
    private List<Long> primaryOrderId;

    @ApiModelProperty(value = "订单类型, matchAny")
    private List<Integer> orderType;

    @ApiModelProperty(value = "配送方式, matchAny")
    private List<Integer> deliveryType;

    @ApiModelProperty(value = "支付渠道, matchAny")
    private List<String> payChannel;

    @ApiModelProperty(value = "是否评价, OrderEvaluateEnum")
    private Integer evaluate;

    @ApiModelProperty(value = "主订单标, matchAll")
    private List<String> mainTags;

    @ApiModelProperty(value = "子订单标, matchAll")
    private List<String> subTags;
}
