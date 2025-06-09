package com.aliyun.gts.gmall.platform.trade.api.constant;

public enum OverPriceLimitEnum  {

    NO_OVER_LIMIT(0, "NO_OVER_LIMIT"),   //# 没有超出价格限制
    INSTALLMENT_OVER_LIMIT(1, "INSTALLMENT_OVER_LIMIT"),//分期超出价格限制
    CREDIT_OVER_LIMIT(2, "CREDIT_OVER_LIMIT");//贷款超出价格限制
    Integer code;


    String script;

    OverPriceLimitEnum(Integer code, String script) {
        this.code = code;
        this.script = script;
    }

    public Integer getCode() {
        return code;
    }

    public String getScript() {
        return script;
    }
}
