package com.aliyun.gts.gmall.center.trade.common.constants;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;


import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ExtOrderStatus implements GenericEnum {
    /**
     *
     */
    NOTING(-999, "|placeholder|")  //# 占位
    ;

    private Integer code;
    
    private String script;


    ExtOrderStatus(Integer code,  String script) {
        this.code = code;
        this.script = script;
    }

    public static ExtOrderStatus codeOf(Integer code) {
        return Arrays.stream(ExtOrderStatus.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }

}
