package com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend;

import java.util.HashMap;
import java.util.Map;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderExtendContainerVO {

    @ApiModelProperty("主订单ID")
    private Long primaryOrderId;

    Map<Long,OrderExtendVO> subOrderExtendMap = new HashMap<>();

    OrderExtendVO mainOrderExtend;

    String errorMsg;

    boolean success = true;

}
