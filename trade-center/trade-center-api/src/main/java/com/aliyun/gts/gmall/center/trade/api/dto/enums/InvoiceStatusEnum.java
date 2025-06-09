package com.aliyun.gts.gmall.center.trade.api.dto.enums;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import java.util.Arrays;

/**
 * @author : jalin
 * @date : 2022/12/1 16:17
 **/
public enum InvoiceStatusEnum  implements I18NEnum {
    APPLYING(1, "|not.invoiced|"),   //# "未开票"
    APPLY_SUCCESS(2, "|invoiced|"),   //# "已开票"

    PART_APPLY_SUCCESS(3, "|partial.invoiced|"),   //# "部分开票"

    RETURN_SUCCESS(4, "|revoke.completed|"),   //# "已撤销"
    REJECT_SUCCESS(7, "|void.completed|"),   //# "已作废"

    APPLY_FAILURE(10, "|application.failed|"),   //# "申请失败"
    ;

    private Integer code;
    
    private String script;


    InvoiceStatusEnum(Integer code, String script) {
        this.code = code;
        this.script = script;
    }
    public static InvoiceStatusEnum codeOf(Integer code) {
        return Arrays.stream(InvoiceStatusEnum.values())
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
