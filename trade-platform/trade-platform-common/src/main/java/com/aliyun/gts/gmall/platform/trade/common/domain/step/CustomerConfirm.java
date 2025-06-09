package com.aliyun.gts.gmall.platform.trade.common.domain.step;

import com.aliyun.gts.gmall.platform.trade.common.constants.StepOrderStatusEnum;

// 当前阶段用户确认
public class CustomerConfirm extends BaseStepOperate {

    @Override
    public Integer getStatus() {
        return StepOrderStatusEnum.STEP_WAIT_CONFIRM.getCode();
    }
}