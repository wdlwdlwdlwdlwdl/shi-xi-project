package com.aliyun.gts.gmall.center.trade.api.dto.enums;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import java.util.Arrays;

/**
 * @author : jalin
 * @date : 2022/12/1 16:17
 **/
public enum InvoiceRejectEnum  implements I18NEnum {
    NO_APPLY(0, "\"待申请退票\""),
    APPLYING(1, "\"已申请退票\""),

    APPLY_AGREE(2, "|refund.agree|"),   //# "同意退票"

    APPLY_REJECT(3, "|refund.reject|"),   //# "拒绝退票"

    NORMAL_RED(4, "|normal.redo.invoice|"),   //# "普通的冲红"

    EXCHANGE_RED(5, "|refund.redo.invoice|"),   //# "退票导致的冲红"

    NORMAL_VOIDED(6, "|normal.void.invoice|"),   //# "普通的作废"

    EXCHANGE_VOIDED(7, "|refund.void.invoice|"),   //# "退票导致的的作废"

    REISSUE_RED(8, "|redo.invoice|"),   //# "冲红重开"

    REISSUE_VOIDED(9, "|void.invoice|"),   //# "作废重开"

    APPLY_FAILURE(10, "|application.failed|"),   //# "申请失败"
    ;

    private Integer code;
    
    private String script;


    InvoiceRejectEnum(Integer code, String script) {
        this.code = code;
        this.script = script;
    }
    public static InvoiceRejectEnum codeOf(Integer code) {
        return Arrays.stream(InvoiceRejectEnum.values())
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
