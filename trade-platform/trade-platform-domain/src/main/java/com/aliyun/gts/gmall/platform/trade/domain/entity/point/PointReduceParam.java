package com.aliyun.gts.gmall.platform.trade.domain.entity.point;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PointReduceParam {

    @ApiModelProperty("主订单ID, 作为幂等ID")
    private Long mainOrderId;

    @ApiModelProperty("阶段序号, 多阶段订单 mainOrderId + stepNo 作为幂等ID")
    private Integer stepNo;

    @ApiModelProperty("用户ID")
    private Long custId;

    @ApiModelProperty("积分数量(原子积分)")
    private Long count;

    @ApiModelProperty("积分金额")
    private Long amt;
}
