package com.aliyun.gts.gmall.platform.trade.common.constants;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum StepOrderStatusEnum implements GenericEnum {

    STEP_WAIT_START(1, "|not.started|"),   //# "未开始"
    STEP_WAIT_PAY(10, "|pending.payment|"),   //# "待支付"
    STEP_WAIT_SELLER_HANDLE(11, "|pending.merchant|"),   //# "待商家处理"
    STEP_WAIT_CONFIRM(12, "|pending.user|"),   //# "待用户确认"
    STEP_FINISH(30, "|complete|");  //# 完成

    // ========================

    private final Integer code;
    
    private final String script;


    StepOrderStatusEnum(Integer code,  String script) {
        this.code = code;
        this.script = script;
    }

    public static StepOrderStatusEnum codeOf(Integer code) {
        return Arrays.stream(StepOrderStatusEnum.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }
    public String getScript() {
        return script;
    }
}
