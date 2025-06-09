package com.aliyun.gts.gmall.platform.trade.common.constants;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;
import java.util.Arrays;

public enum CartGroupTypeEnum  implements I18NEnum {

    SELLER_GROUP(0, "|seller.group|"),   //# "卖家分组"
    PAY_MODE_GROUP(1, "|payment.group|"),   //# 支付方式分组
    INVALID_ITEM_GROUP(-1, "|expired.group|");  //# 失效商品分组


    // ========================

    private final Integer code;
    
    private final String script;


    CartGroupTypeEnum(Integer code,  String script) {
        this.code = code;
        this.script = script;
    }

    public static CartGroupTypeEnum codeOf(Integer code) {
        return Arrays.stream(CartGroupTypeEnum.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }

    public Integer getCode() {
        return code;
    }
    public String getScript() {
        return script;
    }

    // 获取解析
    public String getDesc() { return getMessage(); }

}
