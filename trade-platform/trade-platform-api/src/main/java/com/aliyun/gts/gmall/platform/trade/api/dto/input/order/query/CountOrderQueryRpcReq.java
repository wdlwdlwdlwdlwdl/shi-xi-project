package com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query;

import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("已买到列表查询")
public class CountOrderQueryRpcReq extends AbstractQueryRpcRequest {

    @ApiModelProperty(value = "买家ID", required = true)
    @NotNull
    private Long custId;

    @ApiModelProperty(value = "订单状态列表", required = true)
    @NotEmpty
    private List<Integer> status;

}
