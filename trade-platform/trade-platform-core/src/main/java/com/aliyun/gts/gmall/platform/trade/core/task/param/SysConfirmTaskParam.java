package com.aliyun.gts.gmall.platform.trade.core.task.param;

import lombok.Data;

@Data
public class SysConfirmTaskParam extends TaskParam {

    private boolean logisticsDelay;   // 是否为因物流延后 创建的二次确认收货
}
