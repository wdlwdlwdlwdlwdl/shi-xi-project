package com.aliyun.gts.gmall.platform.trade.common.domain.step;

import com.aliyun.gts.gmall.platform.trade.common.domain.DynamicObject;

import java.util.Objects;

public class StepMeta extends DynamicObject {

    public Integer getStepNo() {
        return getObject("stepNo");
    }

    public String getStepName() {
        return getObject("stepName");
    }

    public CustomerPay getCustomerPay() {
        return getDynamicObject("customerPay", CustomerPay::new);
    }

    public SellerOperate getSellerOperate() {
        return getDynamicObject("sellerOperate", SellerOperate::new);
    }

    public CustomerConfirm getCustomerConfirm() {
        return getDynamicObject("customerConfirm", CustomerConfirm::new);
    }

    public Integer getNextOrderStatus() {
        return getInt("nextOrderStatus", null);
    }

    public BaseStepOperate getOperate(int status) {
        BaseStepOperate b;
        b = getCustomerConfirm();
        if (Objects.equals(status, b.getStatus())) {
            return b;
        }
        b = getCustomerPay();
        if (Objects.equals(status, b.getStatus())) {
            return b;
        }
        b = getSellerOperate();
        if (Objects.equals(status, b.getStatus())) {
            return b;
        }
        return null;
    }
}
