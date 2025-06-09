package com.aliyun.gts.gmall.platform.trade.core.task.param;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReversalCancelTaskParam extends TaskParam {

    private Integer orderStatus;
    private Integer payType;
    private Long primaryReversalId;
    private String timeRule;
    private Integer timeType;
}
