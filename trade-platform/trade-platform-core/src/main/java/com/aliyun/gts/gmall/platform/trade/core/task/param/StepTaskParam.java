package com.aliyun.gts.gmall.platform.trade.core.task.param;

import com.aliyun.gts.gmall.platform.trade.common.domain.step.BaseStepOperate;
import com.aliyun.gts.gmall.platform.trade.common.domain.step.StepMeta;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import lombok.Data;

@Data
public class StepTaskParam extends TaskParam {

    protected StepMeta step;
    protected StepOrder stepOrder;
    protected BaseStepOperate op;
}
