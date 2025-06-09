package com.aliyun.gts.gmall.platform.trade.api.constant;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;

public enum ReversalErrorCode implements ResponseCode {

    CREATE_REVERSAL_ILLEGAL_ORDER_STATUS("20200001", "|apply.aftersale.forbid|",  0),  //# "当前订单状态不能申请售后"
    CREATE_REVERSAL_OUT_OF_TIME("20200002", "|aftersale.time.exceed|",  0),  //# "超出申请售后时间"
    CREATE_REVERSAL_OUT_OF_QTY("20200003", "|aftersale.qty.exceed|",  0),  //# "申请售后的商品数量超出范围"
    CREATE_REVERSAL_OUT_OF_FEE("20200004", "|refund.amount.exceed|",  0),  //# "申请退款金额超出范围"
    CREATE_REVERSAL_DIVIDE_FEE_ERROR("20200005", "|refund.amount.allocate.fail|, |modify.amount|", 0),   //# "申请的退款金额无法分摊,请修改金额"
    CREATE_REVERSAL_REASON_ILLEGAL("20200006", "|invalid.refund.reason|",  0),  //# "申请售后的原因不合法"
    CREATE_REVERSAL_REFUND_ALL_ONLY("20200007", "|only.full.refund|",  0),  //# "当前订单只允许全额仅退款"
    CREATE_REVERSAL_ILLEGAL_ITEM_STATUS("20200008", "|apply.aftersale.item.forbid|",  0),
    REVERSAL_NOT_EXIST("20200020", "|aftersale.not.exist|",  0),  //# "售后单不存在"
    REVERSAL_STATUS_ILLEGAL("20200021", "|aftersale.status.incorrect|",  0),  //# "售后单状态不正确"
    REVERSAL_REFUND_NOT_EXIST("20200022", "|refund.order.not.exist|",  0),  //# "退款单不存在"
    REVERSAL_REBACK_POINT_FAIL("20200023", "|refund.points.failed|",  0),  //# "售后退积分失败"
    QUERY_REVERSAL_INFO_ERROR("20200024", "|query.aftersale.info.failed|",  0),  //# "查询售后单信息失败"
    REVERSAL_ORDER_LOCKED("20200025", "|reversal.order.locked|", 0),   //# 退单加锁
    ;

    // =========================

    String code;
    String script;
    int args;

    ReversalErrorCode(String code, String script,  int args) {
        this.code = code;
        this.script = script;
        this.args = args;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public int getArgs() {
        return args;
    }
    public String getScript() {
        return script;
    }
}
