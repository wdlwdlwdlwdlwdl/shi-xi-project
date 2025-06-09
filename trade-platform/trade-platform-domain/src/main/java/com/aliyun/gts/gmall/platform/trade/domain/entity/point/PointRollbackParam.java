package com.aliyun.gts.gmall.platform.trade.domain.entity.point;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PointRollbackParam {

    @ApiModelProperty("主订单ID")
    private Long mainOrderId;

    @ApiModelProperty("阶段序号")
    private Integer stepNo;

    @ApiModelProperty("逆向主单ID, 作为幂等ID")
    private Long mainReversalId;

    @ApiModelProperty("用户ID")
    private Long custId;

    @ApiModelProperty("积分金额")
    private Long amt;

    @ApiModelProperty("积分数量(原子积分)")
    private Long count;

    @ApiModelProperty("卖家id 店铺积分时用到")
    private Long sellerId;
}
