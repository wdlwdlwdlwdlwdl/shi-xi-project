package com.aliyun.gts.gmall.manager.front.trade.dto.input.query;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EvaluationReq {

    @ApiModelProperty("卖家ID")
    private Long sellerId;

    private Long itemId;

    private Long skuId;

    /**
     * 4分以上好评：2 低于4分属于差评：3  查询所有：1
     */
    private int level;

    /**
     * 订单号
     */
    private Long primaryOrderId;

}
