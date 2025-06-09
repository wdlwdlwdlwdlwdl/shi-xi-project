package com.aliyun.gts.gmall.platform.trade.common.constants;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ItemStatusEnum implements GenericEnum {

    ENABLE(1, "|listed|"),   //# "上架"
    DISABLE(0, "|delisted|");  //# 下架

    // ========================

    private final Integer code;
    
    private final String script;


    ItemStatusEnum(Integer code,  String script) {
        this.code = code;
        this.script = script;
    }

    public static ItemStatusEnum codeOf(Integer code) {
        return Arrays.stream(ItemStatusEnum.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }
    public String getScript() {
        return script;
    }
}
