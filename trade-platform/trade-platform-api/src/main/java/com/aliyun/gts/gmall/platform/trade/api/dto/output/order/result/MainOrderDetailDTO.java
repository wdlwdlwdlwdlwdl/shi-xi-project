package com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@ApiModel("订单详情")
public class MainOrderDetailDTO extends MainOrderDTO {

    @ApiModelProperty("付款时间")
    private Date payTime;

    @ApiModelProperty("发货时间")
    private Date sendTime;

    //增加 receiveId 字段
    @ApiModelProperty(value = "收件地址ID")
    private Long receiverId;

    @ApiModelProperty("确认收货时间")
    private Date receivedTime;

    @ApiModelProperty("评价时间")
    private Date evaluatedTime;

    @ApiModelProperty("售后开始时间")
    private Date reversalStartTime;

    @ApiModelProperty("售后完成时间")
    private Date reversalEndTime;

    @ApiModelProperty("支付单扩展信息")
    private Map<String, String> payBizFeature;

    @ApiModelProperty("支付单扩展标签")
    private List<String> payBizTags;

    @ApiModelProperty("子订单列表")
    private List<SubOrderDetailDTO> subDetailOrderList = new ArrayList<>();

    @ApiModelProperty("订单的定时任务")
    private List<OrderTaskDTO> orderTasks;

    @ApiModelProperty("是否可售后")
    private boolean apply = true;

    @ApiModelProperty("取消原因code")
    private String reasonCode;

    private String remark;

}
