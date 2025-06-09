package com.aliyun.gts.gmall.platform.trade.api.constant;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public enum PayErrorCode implements ResponseCode {

    PAY_ORDER_NOT_EXISTS("20102001", "|pay.order.not.exist|"),   //# "支付单不存在"

    PAY_RELATED_ORDER_NOT_EXISTS("20102002", "|pay.order.not.found|"),   //# "支付单对应的订单不存在"

    TO_PAY_STATUS_ERROR("20102003", "|pay.order.status.error|"),   //# "发起支付的订单和支付单必须是待支付状态"

    NO_SUPPORTED_PAY_CHANNEL("20102004", "|pay.channel.unsupported|"),   //# "本次支付没有可支持的支付渠道"

    PAY_CHANNEL_SELECT_NOT_SUPPORT("20102005", "|select.pay.channel.unsupported|"),

    REPEAT_PAY("20102006", "|duplicate.payment|"),   //# "重复支付"

    REAL_PAID_FEE_NOT_CORRECT("20102007", "|pay.amount.incorrect|"),   //# "实付金额不准确"

    PAY_OPERATE_DB_ERROR("20102008", "|db.error|"),   //# "操作数据库异常"

    PAY_TIMEOUT("20102009", "|pay.timeout|"),   //# "支付超时"

    PAY_ORDER_STATUS_ERROR("20102010", "|pay.status.invalid|"),   //# "支付单状态异常"

    PAY_CALL_BACK_ERROR("20102011", "|pay.callback.failed|"),   //# "支付回调失败"

    PAY_BUILD_REQUEST_ERROR("20102012", "|pay.link.error|"),   //# "构建支付链接错误"

    PAY_USER_POINT_ERROR("20102013", "|points.deduct.error|"),   //# "积分抵扣异常"

    PAY_FLOW_NOT_EXIST("20102014", "|pay.tx.not.exist|"),   //# "支付流水不存在"

    PAY_REFUND_DETAIL_NOT_EXIST("20102015", "|refund.detail.not.exist|、|cannot.initiate.refund|"),   //# "退款单明细不存在、无法发起退款"

    PAY_REFUND_SUCCESS_REPEAT("20102016", "|refund.completed|、|duplicate.operation|"),   //# "已完成退款、重复操作"

    PAY_CALL_GATE_WAY_ERROR("20102017", "|pay.gateway.error|"),   //# "请求支付网关异常"

    PAY_UPDATE_REFUND_DETAIL_FAIL("20102018", "|update.refund.failed|"),   //# "更新退款单信息失败"

    PAY_CALL_BACK_SIGN_ERROR("20102019", "|pay.callback.verif.failed|"),   //# "支付回调验签失败"

    PAY_REFUND_CALL_BACK_ERROR("20102020", "|refund.pay.callback.failed|"),   //# "退款支付回调失败"

    PAY_FAILED("20102021", "|pay.failed|"),   //# "支付失败"

    PAY_REFUND_SEND_MSG_FAIL("20102022", "|send.refund.success.msg.failed|"),   //# "发送退款成功消息失败"

    PAY_LOCK_INVENTORY_ERROR("20102023", "|stock.lock.failed|"),   //# "库存锁定失败"

    PAY_CHECK_POINTS_FULL_DEDUCT_ERROR("20102024", "|pay.amount.check.error|：|full.points.deduct|、|actual.pay.incorrect|0"),   //# "支付金额校验出错：选择了积分全额抵扣、但是实付款不为

    PAY_POINTS_REFUND_DETAIL_NOT_EXSIT("20102025", "|refund.points.not.exist|"),   //# "积分抵扣退款明细不存在"

    PAY_USER_POINT_REBACK_ERROR("20102026", "|points.refund.error|"),   //# "退积分异常"

    PAY_UPDATE_REFUND_DETAIL_ERROR("20102027", "|update.refund.detail.failed|"),   //# "更新退款明细失败"

    PAY_UPDATE_ORDER_STATUS_ERROR("20102028", "|refund.update.status.failed|"),   //# "退款后更新订单或者售后单状态失败"

    PAY_ROLLBACK_INVENTORY_ERROR("20102029", "|refund.update.stock.failed|"),   //# "退款后回补库存失败"

    MERGE_PAY_ERROR("20102030", "|merge.pay.failed|"),   //# "合并支付失败"

    PAY_QUERY_PARAM_ERROR("20102111", "|pay.query.param.error|"),   //# "支付单查询入参错误"
    PAY_QUERY_PROCESS_ERROR("20102112", "|pay.query.exec.error|"),   //# "支付单查询执行错误"

    PAY_FEE_CHECK_ERROR("20102113", "|pay.amount.check.error|"),   //# "支付金额校验出错"

    PAY_TYPE_CANNOT_CHANGE("20102114", "|no.change.pay.method|"),   //# "支付方式不能修改"

    ;



    String code;

    String script;

    PayErrorCode(String code, String script) {
        this.code = code;
        this.script = script;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public int getArgs() {
        return 0;
    }
    public String getScript() {
        return script;
    }
}
