package com.aliyun.gts.gmall.center.trade.core.task.param;

import com.aliyun.gts.gmall.platform.trade.core.task.param.TaskParam;
import lombok.Data;

import java.util.Date;

@Data
public class ScheduleTimeTaskParam extends TaskParam {

    private Date scheduleTime;
}
