package com.aliyun.gts.gmall.center.trade.core.enums;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.platform.trade.common.constants.BizCodeEnum;

import java.util.Arrays;

public enum SplitFlowTypeEnum  implements I18NEnum {

    FORWARD_PAY(1, "|order.payment.split|"),   //# "下单支付分账流水"
    REFUND_PAY(2, "|refund.payment.split|");  //# 退款分账流水

    private int code;
    
    private String script;


    SplitFlowTypeEnum(int code,  String script) {
        this.code = code;
        this.script = script;
    }


    public int getCode() {
        return code;
    }

    public String getName() {
        return getMessage();
    }
    public String getScript() {
        return script;
    }
}
