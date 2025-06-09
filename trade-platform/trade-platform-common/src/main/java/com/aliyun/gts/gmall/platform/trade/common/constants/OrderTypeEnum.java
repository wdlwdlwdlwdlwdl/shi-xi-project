package com.aliyun.gts.gmall.platform.trade.common.constants;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import java.util.Arrays;

/**
 *
 */
public enum OrderTypeEnum  implements I18NEnum {

    PHYSICAL_GOODS(1, "|normal.order|"),   //# "普通实物订单"

    MULTI_STEP_ORDER(2, "|multi.stage.order|");  //# 多阶段订单


    // ========================


    private final Integer code;
    
    private final String script;


    OrderTypeEnum(Integer code,  String script) {
        this.code = code;
        this.script = script;
    }

    public static OrderTypeEnum codeOf(Integer code) {
        return Arrays.stream(OrderTypeEnum.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }

    public Integer getCode() {
        return code;
    }

    public String getName(){
        return getMessage();
    }
    public String getScript() {
        return script;
    }
}
