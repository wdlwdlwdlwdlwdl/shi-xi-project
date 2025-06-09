package com.aliyun.gts.gmall.center.trade.api.dto.enums;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import java.util.Arrays;

/**
 * 支付单对公支付关闭原因
 */
public enum PayCloseTypeEnum  implements I18NEnum {


    CANCEL_SELLER_REFUNDED(21, "|merchant.refunded|"),  //# "商家已退款"
    CANCEL_NOT_NEED_REFUNDED(22, "|no.refund.required|"), //# "无需退款"
    TIMEOUT(1, "|overdue.not.paid|"),   //# "逾期未支付"
    CANCEL(2, "|agreement.cancelled|");  //# 协议取消

    private Integer code;
    
    private String script;


    PayCloseTypeEnum(Integer code,  String script) {
        this.code = code;
        this.script = script;
    }

    public static PayCloseTypeEnum codeOf(Integer code) {
        return Arrays.stream(PayCloseTypeEnum.values())
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
