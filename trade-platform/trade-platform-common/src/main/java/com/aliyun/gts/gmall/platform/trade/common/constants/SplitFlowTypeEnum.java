package com.aliyun.gts.gmall.platform.trade.common.constants;

public enum SplitFlowTypeEnum {

    FORWARD_PAY(1, "下单支付分账流水"),
    REFUND_PAY(2, "退款分账流水");

    private final int code;
    private final String name;

    SplitFlowTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }


    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
