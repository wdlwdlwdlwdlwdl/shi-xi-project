package com.aliyun.gts.gmall.manager.front.item.constants;

import java.util.Arrays;

public enum PromotionToolCodeEnum {

    /**
     * 预售
     */
    YU_SHOU("yushou", "预售"),
    /**
     * 固定价（）
     */
    GU_DING_JIA("gudingjia", "固定价"),

    MIAO_SHA("miaosha", "秒杀"),



    ;

    private String code;

    private String script;


    PromotionToolCodeEnum(String code, String script) {
        this.code = code;
        this.script = script;
    }

    public static PromotionToolCodeEnum codeOf(String code) {
        return Arrays.stream(PromotionToolCodeEnum.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }

    public String getCode() {
        return code;
    }
    public String getScript() {
        return script;
    }
}
