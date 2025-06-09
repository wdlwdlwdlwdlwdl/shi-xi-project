package com.aliyun.gts.gmall.platform.trade.common.constants;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import java.util.Arrays;

/**
 * 减库存方式
 */
public enum InventoryReduceType  implements I18NEnum {

    REDUCE_ON_ORDER(1, "|order.deduct|"),   //# "下单减库存"
    REDUCE_ON_PAY(2, "|payment.deduct|");  //# 付款减库存



    // ========================

    private final Integer code;
    
    private final String script;


    InventoryReduceType(Integer code,  String script) {
        this.code = code;
        this.script = script;
    }

    public static InventoryReduceType codeOf(Integer code) {
        return Arrays.stream(InventoryReduceType.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }

    public Integer getCode() {
        return code;
    }
    public String getScript() {
        return script;
    }

    // 获取解析
    public String getDesc() { return getMessage(); }

}
