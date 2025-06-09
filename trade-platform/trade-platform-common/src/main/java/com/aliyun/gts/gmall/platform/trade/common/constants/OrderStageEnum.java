package com.aliyun.gts.gmall.platform.trade.common.constants;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;
import lombok.Getter;

import java.util.Arrays;

/**
 * 售前、售中、售后阶段
 */
@Getter
public enum OrderStageEnum implements GenericEnum {

    BEFORE_SALE(1, "|pre.sales|"),   // 即 支付前
    ON_SALE(2, "|in.sales|"),       // 即 支付后, 商家收钱之前
    AFTER_SALE(3, "|after.sales|");    // 即 商家收钱之后


    // ========================


    private final Integer code;
    private final String script;

    OrderStageEnum(Integer code, String script) {
        this.code = code;
        this.script = script;
    }

    public static OrderStageEnum codeOf(Integer code) {
        return Arrays.stream(OrderStageEnum.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }

    @Override
    public String getScript() {
        return script;
    }
}
