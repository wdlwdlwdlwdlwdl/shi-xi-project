package com.aliyun.gts.gmall.platform.trade.core.task.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelTaskParam extends TaskParam{

    private Integer orderStatus;
    private Integer payType;
    private String timeRule;
    private Integer timeType;
}
