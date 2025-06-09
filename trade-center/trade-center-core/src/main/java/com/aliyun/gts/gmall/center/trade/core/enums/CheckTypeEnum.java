package com.aliyun.gts.gmall.center.trade.core.enums;
import com.aliyun.gts.gmall.framework.i18n.I18NEnum;

import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

public enum CheckTypeEnum  implements I18NEnum {

    PAY_STATE_NOT_MATCH(1, "|payment.status.mismatch|"),   //# "支付平台支付单状态同系统订单状态不一致"
    PAY_TOTAL_AMOUNT_NOT_MATCH(2, "|payment.amount.mismatch|"),   //# "支付平台支付单同系统订单支付总金额不一致"
    PAY_SEPARATE_AMT_NOT_MATCH(3, "|split.amount.mismatch|"),   //# "支付平台支付单同系统订单分账金额不一致"
    PAY_REFUND_AMT_NOT_MATCH(4, "|refund.amount.mismatch|"),   //# "支付平台支付单同系统订单退款金额不一致"
    ORDER_AND_PAY_STATUS_NOT_MATCH(5, "|order.status.mismatch|"),   //# "订单同支付单状态不一致"
    ORDER_REVERSAL_STATUS_NOT_MATCH(6, "|after.sale.status.mismatch|"),   //# "订单同售后单状态不一致"
    ORDER_AMT_CALCULATE_ERROR(7, "|main.order.amount.error|"),   //# "主订单金额计算错误"
    ORDER_SPLIT_AMT_ERROR(8, "|sub.order.split.error|"),   //# "子单分摊金额错误"
    PAY_AMT_CALCULATE_ERROR(9, "|payment.amount.error|"),   //# "支付单金额计算错误"
    ORDER_AND_PAY_AMT_NOT_MATCH(10, "|main.order.payment.mismatch|"),   //# "主订单金额同支付单金额不匹配"
    PAY_AND_FLOW_AMT_NOT_MATCH(11, "|payment.and.transaction.mismatch|"),   //# "支付单和支付流水金额不匹配"
    MAIN_REVERSAL_FEE_CALCULATE_ERROR(12, "|after.sale.amount.error|"),   //# "售后单金额计算错误"
    REVERSAL_REFUND_CALCULATE_ERROR(13, "|refund.amount.error|"),   //# "退款单金额计算错误"
    MAIN_REVERSAL_REFUND_FEE_NOT_MATCH(14, "|after.sale.refund.mismatch|"),   //# "售后单和退款单金额不匹配"
    REFUND_AND_DETAIL_FEE_NOT_MATCH(15, "|refund.detail.mismatch|"),   //# "退款单和退款明细金额不匹配"
    ORDER_DEDUCT_POINT_CHECK_ERROR(16, "|points.deduction.error|"),   //# "积分抵扣校验出错"
    MAIN_REVERSAL_POINT_CHECK_ERROR(17, "|after.sale.points.error|");  //# 售后退积分校验出错

    private int code;
    
    private String script;


    CheckTypeEnum(int code,  String script) {
        this.code = code;
        this.script = script;
    }


    public int getCode() {
        return code;
    }

    public String getName() {
        return getMessage();
    }
    public String getScript() {
        return script;
    }
}
