package com.aliyun.gts.gmall.platform.trade.common.constants;

import lombok.Getter;

/**
 * 贷款期数枚举
 */
@Getter
public enum LoanTypeEnum {

    LOAN_SIX(6, "六期"),
    LOAN_TWELVE(12, "十二期"),
    LOAN_EIGHTEEN(18, "十八期数"),
    loan_twenty_four(24, "二十四期"),
    LOAN_THIRTY_SIX(36, "三十六期"),
    LOAN_FORTY_EIGHT(48, "四十八期"),
    LOAN_SIXTY(60, "六十期"),;
    private final Integer code;

    private final String script;

    LoanTypeEnum(Integer code, String script) {
        this.code = code;
        this.script = script;
    }
}
