package com.aliyun.gts.gmall.center.trade.domain.entity.deposit;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
@Builder
public class ItemDepositInfo {
    public static final Integer DEPOSIT_STEP_NO = 1;    // 定金的阶段号

    @ApiModelProperty("定金金额")
    private Long depositAmt;

    @ApiModelProperty("尾款天数, 确认尾款后n天")
    private Integer tailDays;

    @ApiModelProperty("尾款开始日期 (预售使用)")
    private Date tailStart;

    @ApiModelProperty("尾款结束日期 (预售使用)")
    private Date tailEnd;

    @ApiModelProperty("子订单定金金额, 此时没有subOrderId, 用index代替")
    private Long[] subOrderDepositAmt;
}
