package com.aliyun.gts.gmall.platform.trade.common.domain.step;

import com.aliyun.gts.gmall.platform.trade.common.constants.StepOrderStatusEnum;
import lombok.Data;

// 当前阶段的用户支付
@Data
public class CustomerPay extends BaseStepOperate {

    @Override
    public Integer getStatus() {
        return StepOrderStatusEnum.STEP_WAIT_PAY.getCode();
    }
}
