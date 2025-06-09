package com.aliyun.gts.gmall.platform.trade.common.domain.step;

import com.aliyun.gts.gmall.platform.trade.common.constants.StepOrderStatusEnum;

import java.util.List;
import java.util.Map;

// 当前阶段的卖家操作
public class SellerOperate extends BaseStepOperate {

    public List<FormData> getFormData() {
        return getDynamicObjects("formData", FormData::new);
    }

    public List<FormAction> getAction() {
        return getDynamicObjects("action", FormAction::new);
    }

    @Override
    public Integer getStatus() {
        return StepOrderStatusEnum.STEP_WAIT_SELLER_HANDLE.getCode();
    }
}
