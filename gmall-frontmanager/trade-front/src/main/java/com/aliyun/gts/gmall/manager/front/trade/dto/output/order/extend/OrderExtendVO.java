package com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OrderExtendVO {
    @ApiModelProperty("订单类型 1实物商品, 2多阶段, 10电子凭证")
    Integer orderType;
}