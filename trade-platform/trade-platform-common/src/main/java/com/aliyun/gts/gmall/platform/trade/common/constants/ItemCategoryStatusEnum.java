package com.aliyun.gts.gmall.platform.trade.common.constants;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ItemCategoryStatusEnum implements GenericEnum {

    ENABLE(1, "|enable|"),   //# "启用"
    DISABLE(0, "|block|");  //# 屏蔽

    // ========================

    private final Integer code;
    
    private final String script;


    ItemCategoryStatusEnum(Integer code,  String script) {
        this.code = code;
        this.script = script;
    }

    public static ItemCategoryStatusEnum codeOf(Integer code) {
        return Arrays.stream(ItemCategoryStatusEnum.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }
    public String getScript() {
        return script;
    }
}
