package com.aliyun.gts.gmall.manager.front.trade.constants;

import com.aliyun.gts.gmall.framework.i18n.I18NEnum;
import java.util.Arrays;

/**
 * 对公支付 账期支付的支付状态
 */
public enum CATPayStatusEnum  implements I18NEnum {

    /**
     *
     */
    TO_BE_PAID("01", "|pending.payment|"),   //# "待支付"
    /**
     * PayStatusEnum.PAID 并且 TcOrderPay中payFeature received:1
     */
    PAID("02", "|payment.completed|"),   //# "支付完成"
    /**
     *
     */
    PAY_CANCELED("05", "|payment.closed|"),   //# "支付关闭"
    /**
     * 对公支付用
     * PayStatusEnum.PAID 并且 TcOrderPay中payFeature transfered:1
     */
    BUYER_PAID("07", "|buyer.confirmed.payment|"),   //# "买家表示已支付"

    TIMEOUT("99", "|overdue.unpaid|"),   //# "逾期未支付"
    PENDING_REFUND("08", "待退款"),

    SELLER_REFUNDED("09", "已退款"),
    ;

    private String code;

    private String script;


    CATPayStatusEnum(String code, String script) {
        this.code = code;
        this.script = script;
    }

    public static CATPayStatusEnum codeOf(String code) {
        return Arrays.stream(CATPayStatusEnum.values())
            .filter(en -> en.code.equals(code))
            .findFirst().orElse(null);
    }

    public String getCode() {
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
