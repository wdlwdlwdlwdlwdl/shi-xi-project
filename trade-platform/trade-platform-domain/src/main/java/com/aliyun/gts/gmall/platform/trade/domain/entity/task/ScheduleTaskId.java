package com.aliyun.gts.gmall.platform.trade.domain.entity.task;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleTaskId {

    /**
     * 主键
     */
    private Long taskId;

    /**
     * 分库分表键
     */
    private Long primaryOrderId;
}
