package com.aliyun.gts.gmall.platform.trade.common.constants;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.platform.trade.common.GenericEnum;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PromotionTypeEnum implements GenericEnum {


    NORMAL(-1, "|normal.activity|（|non.asset|）"),   //# "普通活动（非资产类
    NONE(0, "|asset.activity|--|none|"),   //# "资产类活动--无"
    COUPON(1, "|asset.activity|--|coupon|");  //# 资产类活动--券


    // ========================


    private final Integer code;
    
    private final String script;


    PromotionTypeEnum(Integer code,  String script) {
        this.code = code;
        this.script = script;
    }

    public static PromotionTypeEnum codeOf(Integer code) {
        return Arrays.stream(PromotionTypeEnum.values())
                .filter(en -> en.code.equals(code))
                .findFirst().orElse(null);
    }
    public String getScript() {
        return script;
    }
}
