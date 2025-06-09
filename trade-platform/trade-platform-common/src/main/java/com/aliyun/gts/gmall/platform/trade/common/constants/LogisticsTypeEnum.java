package com.aliyun.gts.gmall.platform.trade.common.constants;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;

import java.util.Arrays;

/**
 * 配送方式
 */
public enum LogisticsTypeEnum implements GenericEnum {

    //
    REALITY(1, "|express|","Drop-off Point to Door" ),   //# "快递"
    VIRTUAL(2, "|no.logistics|","no.logistics"),   //# "不需物流"
    //下面是原来的

    SELF(3, "|Pick Up|","SELF"),

    //上门自提
    PVZ(4, "|PVZ|","PVZ"), //PVZ

    POSTAMAT(5, "|Postamat|","Postamat"), //Postamat

    //courier to door 根据商品类目判断是商家物流还是HM物流
    COURIER_LODOOR_HM(6, "|Courier to Door|","Courier to Door"),
    COURIER_LODOOR(7, "|Self Delivery|","Self Delivery"),

    POINT_LODOOR(8, "|Pick Up|","Pick Up"),  // 自提

    SELLER_KA(9, "|Ka Self Delivery|","Ka Self Delivery");//selfDelivery and sellerKa

    // ========================

    private final Integer code;
    
    private final String script;

    private final String text;

    LogisticsTypeEnum(Integer code,  String script,String text) {
        this.code = code;
        this.script = script;
        this.text = text;
    }

    public static LogisticsTypeEnum codeOf(Integer code) {
        return Arrays.stream(LogisticsTypeEnum.values())
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


    public String getText(){
        return text;
    }

}
