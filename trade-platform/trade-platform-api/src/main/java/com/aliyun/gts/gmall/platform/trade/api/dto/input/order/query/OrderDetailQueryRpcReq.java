package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.QueryCommandRpcRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("订单详情查询条件")
public class OrderDetailQueryRpcReq extends QueryCommandRpcRequest {

    @ApiModelProperty(value = "主订单id", required = true)
    @NotNull
    private Long primaryOrderId;

    @ApiModelProperty(value = "主订单id-列表（传入多个primaryOrderId）", required = true)
    private List<Long> primaryOrderIdList;

    @ApiModelProperty(value = "卖家id")
    private Long sellerId;
    @ApiModelProperty(value = "买家id")
    private Long custId;

    @ApiModelProperty(value = "是否查extend, 如是true将查询订单所有extend, 推荐使用extend单独接口按需精确查")
    private boolean includeExtend = false;

    @ApiModelProperty(value = "是否查售后信息 (目前就返回售后子单状态)")
    private boolean includeReversalInfo = false;

    private boolean seller;

    private boolean isSeller;

    // 查询订单定时任务
    private List<String> includeOrderTaskTypes;

    public void setSeller(boolean value) {
        this.seller = value;
        this.isSeller = value;
    }
    public void setIsSeller(boolean value) {
        this.setSeller(value);
    }

}
