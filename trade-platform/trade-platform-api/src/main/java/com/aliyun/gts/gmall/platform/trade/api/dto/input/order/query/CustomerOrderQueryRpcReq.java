package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query;


import com.aliyun.gts.gmall.platform.trade.api.dto.common.PageQuery;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderEvaluateEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ApiModel("已买到列表查询")
public class CustomerOrderQueryRpcReq extends PageQuery {

    @ApiModelProperty(value = "买家ID", required = true)
    @NotNull
    private Long custId;

    @ApiModelProperty(value = "商品名称关键字", required = false)
    private String itemTitle;

    @ApiModelProperty(value = "订单状态", required = false)
    private Integer status;

    @ApiModelProperty(value = "是否评价, OrderEvaluateEnum", required = false)
    private Integer evaluate;

    @ApiModelProperty(value = "状态列表", required = false)
    private List<OrderStatusInfo> statusList;

    @ApiModelProperty(value = "主订单id", required = false)
    @Size(max = 20)
    private List<Long> primaryOrderIds;

    /**
     * 待评价订单的查询条件
     * @param custId
     * @return
     */
    public static CustomerOrderQueryRpcReq getNotEvaluatedReq(Long custId){
        CustomerOrderQueryRpcReq req = new CustomerOrderQueryRpcReq();
        req.setCustId(custId);
        req.setStatusList(Lists.newArrayList(
                new OrderStatusInfo(OrderStatusEnum.COMPLETED.getCode()
             )
        ));
        req.setEvaluate(OrderEvaluateEnum.NOT_EVALUATE.getCode());
        return req;
    }

}
