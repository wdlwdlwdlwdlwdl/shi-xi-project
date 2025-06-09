package com.aliyun.gts.gmall.platform.trade.domain.wrapper;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class OrderStatisticsWrapper {


    private Long sellerId;

    @ApiModelProperty("开始时间")
    private Timestamp startTime;

    @ApiModelProperty("结束时间")
    private Timestamp endTime;

    @ApiModelProperty("售后原因code")
    private Integer reasonCode;
}
