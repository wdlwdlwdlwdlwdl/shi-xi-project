package com.aliyun.gts.gmall.manager.front.trade.dto.output.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author GTS
 * @date 2021/03/09
 */
@ApiModel("订单各个状态数量")
@ToString
@Data
public class OrderCountByStatusVO {
    /**
     * 等待付款数量
     */
    @ApiModelProperty("等待付款数量")
    private Integer waitPayCnt;
    /**
     * 等待收货数量
     */
    @ApiModelProperty("等待收货数量")
    private Integer waitRecptCnt;
    /**
     * 等待评价数量
     */
    @ApiModelProperty("等待评价数量")
    private Integer waitEvaluateCnt;
    /**
     * 售后中数量
     */
    @ApiModelProperty("售后中数量")
    private Integer reversalDoingCnt;
}