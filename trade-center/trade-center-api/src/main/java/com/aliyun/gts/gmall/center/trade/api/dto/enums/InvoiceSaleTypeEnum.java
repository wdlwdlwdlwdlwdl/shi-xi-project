package com.aliyun.gts.gmall.center.trade.api.dto.enums;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import java.util.Arrays;

public enum InvoiceSaleTypeEnum  implements I18NEnum {
    /**
     * 直销模式
     */
    DIRECT(0, "|direct.sales.mode|"),   //# "直销模式"
    /**
     * 代销模式
     */
    CONSIGNMENT(1, "|consignment.mode|");  //# 代销模式

    private Integer code;
    
    private String script;


    InvoiceSaleTypeEnum(Integer code,  String script) {
        this.code = code;
        this.script = script;
    }
    public static InvoiceSaleTypeEnum codeOf(Integer code) {
        return Arrays.stream(InvoiceSaleTypeEnum.values())
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

    public String getName(){
        return getMessage();
    }

}
