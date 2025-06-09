package com.aliyun.gts.gmall.manager.front.trade.dto.utils;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import java.util.Arrays;

/**
 * 电子凭证状态
 *
 * @author tiansong
 */
public enum EvoucherStatusDisplay  implements I18NEnum {
    NOT_USED(1, "|unused|"),   //# "未使用"
    WRITE_OFF(2, "|redeemed|"),   //# "已核销"
    DISABLED(3, "|disabled|"),   //# "已禁用"
    EXPIRED(4, "|expired|"),   //# "已过期"
    NOT_EFFECTIVE(5, "|pending|");  //# 待生效

    private Integer code;
    private String  script;

    private EvoucherStatusDisplay(Integer code,  String script) {
        this.code = code;
        this.script = script;
    }

    public static EvoucherStatusDisplay codeOf(Integer code) {
        return Arrays.stream(values()).filter((en) -> {
            return en.code.equals(code);
        }).findFirst().orElse(DISABLED);
    }

    public Integer getCode() {
        return this.code;
    }

    public String getScript() {
        return script;
    }

    public String getName() {
        return getMessage();
    }
}
