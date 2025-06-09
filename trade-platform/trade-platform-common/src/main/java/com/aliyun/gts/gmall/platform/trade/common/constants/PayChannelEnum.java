package com.aliyun.gts.gmall.platform.trade.common.constants;

import lombok.Getter;

/**
 * 配送方式
 */
@Getter
public enum PayChannelEnum {
    //# \"上门自提\"
//    EPAY("108", "epay"),
    EPAY("108", "Card"),

//    LOAN("109", "loan"),
    LOAN("109", "Loan"),

//    INSTALLMENT("110", "installment");  //point to door或者DC
    INSTALLMENT("110", "Installment");  //point to door或者DC

    private final String code;

    private final String script;

    PayChannelEnum(String code, String script) {
        this.code = code;
        this.script = script;
    }

    public static String getCodeByName(final String script) {
        for(PayChannelEnum deliveryTypeEnum : PayChannelEnum.values()){
            if(deliveryTypeEnum.getScript().equals(script)){
                return deliveryTypeEnum.getCode();
            }
        }
        return null;
    }

    public static String getCodeByFuzzyName(final String script) {
        if("epay".equalsIgnoreCase(script))
        {
            return PayChannelEnum.EPAY.getCode();
        }
        for(PayChannelEnum deliveryTypeEnum : PayChannelEnum.values()){
            if(deliveryTypeEnum.getScript().equalsIgnoreCase(script)){
                return deliveryTypeEnum.getCode();
            }
        }
        return null;
    }
}
