package com.aliyun.gts.gmall.center.trade.common.constants;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

/**
 * 下单方式枚举
 */
public enum OrderMethodEnum   implements I18NEnum {
    NORMAL_ORDER(0, "NORMAL_ORDER",  "|normal.order|"), //# 普通下单
    HELP_ORDER(1, "HELP_ORDER", "|order.on.behalf|"), //# 代客下单
    ;

    OrderMethodEnum(Integer code,  String name, String script) {
        this.code = code;
        this.name = name;
        this.script = script;
    }

    private Integer code;
    
    private String name;

    private String script;
    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return getMessage();
    }
    public static OrderMethodEnum of(Integer code) {
        for(OrderMethodEnum method: values()) {
            if(method.code.equals(code)) {
                return method;
            }
        }
        throw new UnsupportedOperationException(I18NMessageUtils.getMessage("unsupported.order.type"));  //# "不支持的下单类型"
    }
    public String getScript() {
        return script;
    }
}
