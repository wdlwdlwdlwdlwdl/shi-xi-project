package com.aliyun.gts.gmall.center.trade.common.constants;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.platform.trade.common.constants.OrderTypeEnum;

import java.util.Arrays;

public enum ExtOrderType  implements I18NEnum {

    PHYSICAL_GOODS(OrderTypeEnum.PHYSICAL_GOODS.getCode(), "|physical.goods.order|"),   //# "普通实物订单"
    EVOUCHER(10, "|e.voucher.order|");  //# 电子凭证订单


    // ========================


    private Integer code;
    
    private String script;


    ExtOrderType(Integer code,  String script) {
        this.code = code;
        this.script = script;
    }

    public static ExtOrderType codeOf(Integer code) {
        return Arrays.stream(ExtOrderType.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }

    public Integer getCode() {
        return code;
    }

    public String getName(){
        return getMessage();
    }
    public String getScript() {
        return script;
    }
}
