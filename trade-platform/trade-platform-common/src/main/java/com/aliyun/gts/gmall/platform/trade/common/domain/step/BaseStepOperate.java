package com.aliyun.gts.gmall.platform.trade.common.domain.step;

import com.aliyun.gts.gmall.platform.trade.common.domain.DynamicObject;

import java.util.List;

public abstract class BaseStepOperate extends DynamicObject {

    // 订单操作选项
    public static final String OPERATION_REVERSAL_REFUND_ONLY = "REVERSAL_REFUND_ONLY";
    public static final String OPERATION_REVERSAL_RETURN_ITEM = "REVERSAL_RETURN_ITEM";

    // 超时处理
    public static final String TIMEOUT_ACTION_NEXT = "NEXT";
    public static final String TIMEOUT_ACTION_SYS_REFUND = "CLOSE_AND_REFUND";
    public static final String TIMEOUT_ACTION_SYS_PAID = "CLOSE_AND_PAID";

    public boolean isNeed() {
        return getBoolean("need", false);
    }

    public String getName() {
        return getObject("name");
    }

    public String getStatusName() {
        return getObject("statusName");
    }

    public Long getTimeoutInSec() {
        return getLong("timeoutInSec", null);
    }

    public String getTimeoutAction() {
        return getObject("timeoutAction");
    }

    public abstract Integer getStatus();

    public List<String> getOtherOperation() {
        return getObject("otherOperation");
    }

}
