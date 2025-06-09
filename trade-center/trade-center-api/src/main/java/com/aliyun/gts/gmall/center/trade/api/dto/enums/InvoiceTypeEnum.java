package com.aliyun.gts.gmall.center.trade.api.dto.enums;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import java.util.Arrays;

/**
 * @author : jalin
 * @date : 2022/12/1 16:17
 **/

public enum InvoiceTypeEnum  implements I18NEnum {
    GENERAL(0, "|e.invoice|"),   //# "电子普票"
    SPECIAL(1, "|vat.invoice|"),   //# "专票"
    ;

    private Integer code;
    
    private String script;


    InvoiceTypeEnum(Integer code, String script) {
        this.code = code;
        this.script = script;
    }

    public static InvoiceTypeEnum codeOf(Integer code) {
        return Arrays.stream(InvoiceTypeEnum.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }

    public Integer getCode() {
        return code;
    }

    @Override
    public String getScript() {
        return script;
    }

    public String getName() {
        return getMessage();
    }

}
