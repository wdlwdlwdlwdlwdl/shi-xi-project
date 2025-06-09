package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CheckOutOrderResultVO {

    // 校验成功
    private Boolean checkSuccess;

    // 已经通过
    private Boolean createdSuccess;

    @ApiModelProperty("临时订单")
    private List<Long> originMainOrderList;
    
}
