package com.aliyun.gts.gmall.platform.trade.api.constant;

import java.util.Arrays;
import java.util.Objects;

public enum PayModeCode {

    EPAY("epay", "Card", 3, 0),
    INSTALLMENT_3("installment_3", "Installment_3", 3, 3),
    INSTALLMENT_6("installment_6", "Installment_6", 6, 6),
    INSTALLMENT_12("installment_12", "Installment_12" , 12, 12),
    INSTALLMENT_24("installment_24", "Installment_24", 24, 24),
    LOAN("loan", "Loan", 3, 3),
    LOAN_3("loan_3", "Loan_3", 3, 3),
    LOAN_6("loan_6", "Loan_6" , 3, 6),
    LOAN_12("loan_12", "Loan_12", 3, 12),
    LOAN_18("loan_18", "Loan_18", 3, 18),
    LOAN_24("loan_24", "Loan_24", 3, 24),
    LOAN_36("loan_36", "Loan_36", 3, 36),
    LOAN_48("loan_48", "Loan_48", 3, 48),
    LOAN_60("loan_60", "Loan_60", 3, 60),
    ;

    String code;
    String script;
    Integer periodNumber;
    Integer loanNumber;

    PayModeCode(String code, String script, Integer periodNumber, Integer loanNumber) {
        this.code = code;
        this.script = script;
        this.periodNumber = periodNumber;
        this.loanNumber = loanNumber;
    }

    public static PayModeCode codeOf(String code) {
        return Arrays.stream(PayModeCode.values()).filter(en -> en.code.equals(code)).findFirst().orElse(null);
    }

    public String getCode() {
        return code;
    }

    public Integer getLoanNumber() {
        return loanNumber;
    }

    public String getScript() {
        return script;
    }

    public Integer getPeriodNumber() {
        return periodNumber;
    }

    /**
     * epay支付
     * @param payModeCode
     * @return
     */
    public static Boolean isEpay(PayModeCode payModeCode) {
        return EPAY.equals(payModeCode);
    }



    /**
     * installment 支付
     * @param payModeCode
     * @return
     */
    public static Boolean isLoan(PayModeCode payModeCode) {
        PayModeCode[] loan =  new PayModeCode[] {LOAN_3, LOAN_6, LOAN_12, LOAN_18, LOAN_24, LOAN_36, LOAN_48, LOAN_60};
        return Arrays.asList(loan).contains(payModeCode);
    }

    /**
     * loan 支付
     * @param payModeCode
     * @return
     */
    public static Boolean isInstallment(PayModeCode payModeCode) {
        PayModeCode[] installment =  new PayModeCode[] {INSTALLMENT_3, INSTALLMENT_6, INSTALLMENT_12, INSTALLMENT_24};
        return Arrays.asList(installment).contains(payModeCode);
    }

    /**
     * 获取期数
     * @param periodNumber
     * @return
     */
    public static PayModeCode getInstallment(Integer periodNumber) {
        String code = "installment_" + periodNumber;
        return Arrays.stream(PayModeCode.values()).filter(en -> en.code.equals(code)).findFirst().orElse(null);
    }

    /**
     *
     * @param originPayMode
     * @param payMode
     * @return
     */
    public static Boolean isPayMode(String originPayMode, String payMode) {
        PayModeCode originPayModeCode = codeOf(originPayMode);
        PayModeCode payModeCode = codeOf(payMode);
        if (Objects.isNull(originPayModeCode) || Objects.isNull(payModeCode)) {
            return Boolean.FALSE;
        }
        // epay 三期
        if (isEpay(originPayModeCode) && INSTALLMENT_3.equals(payModeCode)) {
            return Boolean.TRUE;
        }
        // loan 三期
        if (isLoan(originPayModeCode) && INSTALLMENT_3.equals(payModeCode)) {
            return Boolean.TRUE;
        }
        // installment
        return originPayModeCode.equals(payModeCode);
    }


    /**
     * epay支付
     * @param payModeCode
     * @return
     */
    public static Boolean isInsallment_3(PayModeCode payModeCode) {
        return INSTALLMENT_3.equals(payModeCode);
    }

}
