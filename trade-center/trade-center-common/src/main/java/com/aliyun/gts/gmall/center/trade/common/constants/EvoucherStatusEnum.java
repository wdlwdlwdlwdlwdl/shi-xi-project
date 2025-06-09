package com.aliyun.gts.gmall.center.trade.common.constants;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;

import java.util.Arrays;

public enum EvoucherStatusEnum implements GenericEnum {

    NOT_USED(1, "|unused|"),   //# "未使用"
    WRITE_OFF(2, "|redeemed|"),   //# "已核销"
    DISABLED(3, "\"已禁用\"");  // 退货禁用


    // ========================


    private Integer code;
    
    private String script;


    EvoucherStatusEnum(Integer code,  String script) {
        this.code = code;
        this.script = script;
    }

    public static EvoucherStatusEnum codeOf(Integer code) {
        return Arrays.stream(EvoucherStatusEnum.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getName() {
        return getMessage();
    }
    public String getScript() {
        return script;
    }
}
