package com.aliyun.gts.gmall.center.trade.core.constants;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import lombok.Getter;

/**
 * @author : [admin]
 * @version : [v1.0]
 * @description : [一句话描述该类的功能]
 * @createTime : [2022/9/28 20:12]
 * @updateUser : [admin]
 * @updateTime : [2022/9/28 20:12]
 * @updateRemark : [说明本次修改内容]
 */


@Getter
public enum JstErrorCode implements ResponseCode {
    JST_CANCEL_ERROR("20900001", "|refund.cancel.order.failed|，|check.order.shipping.status|；|order.number|：%s", 1),  //# "退款操作聚水潭取消订单失败，请检查聚水潭订单物流状态；订单号
    JST_CONFIRM_ERROR("20900002", "|order.receipt.fail|: %s", 1),  //# "订单确认收货失败
    JST_OID_NOT_FOUND_ERROR("20900003", "|order.not.pushed|，|confirm.jushuitan.order.exists|", 0),  //# "订单还未推送到聚水潭，请确认聚水潭订单是否存在"
    JST_SALE_TYPE_ERROR("20900004", "|refund.failed|!|reason|：|non.direct.sale.no.cancel|", 0),  //# "退款失败!原因：非直销类订单不允许操作取消聚水潭订单"
    JST_REFUND_ONLY_ERROR("20900005", "|refund.failed|!|reason|：|non.refund.no.cancel|", 0),  //# "退款失败!原因：非仅退款不可操作聚水潭取消子订单"
    JST_WITHOUT_SYNC_ERROR("20900006", "|non.shipped.order|, |block.all.sync|", 0),   //# "非待发货状态的订单,全部拦截不能同步订单到聚水潭"
    JST_ORDER_SYNC_LIMIT_ERROR("20900007", "|order.sync.limit.exceeded|，MQ|retry|", 0),  //# "订单同步限流主动抛异常，MQ重新消费"
    JST_ORDER_LOGISTICS_ERROR("20900008", "|order.delivery.notify.fail|;订单Id：%s", 1),  //# "订单发货通知失败
    JST_ORDER_QUERY_ERROR("20900009", "|order.query.fail|;订单Id：%s", 1),  //# "聚水潭订单查询失败
    JST_ORDER_QUERY_EMPTY_ERROR("20900010", "|order.query.empty|;订单Id：%s", 1),  //# "聚水潭订单查询为空
    JST_ORDER_DELIVER_ERROR("20900011", "|refund.failed|!|reason|：操作未发货仅退款、但实际聚水潭订单已发货;订单Id：%s", 1),  //# "退款失败!原因
    JST_ORDER_NOT_EXIST_ERROR("20900012", "|order.push.fail|，|sub.order.empty|", 0),  //# "订单推送失败，子订单为空"
    JST_ORDER_REVERSAL_ERROR("20900013", "|after.sale|，|retry.later|", 0),  //# "售后中，待重试"
    JST_ORDER_SYNCING_ERROR("20900014", "|pushing|，|try.later|", 0),  //# "推送中，请稍后重试"
    JST_SALE_TYPE_NOT_EXIST_ERROR("20900015", "|no.direct.sale|、|consignment.order|，|no.upload.needed|", 0),  //# "非直销、代销订单，无需上传"
    JST_ORDER_LOGISTICS_MANY("209000016", "|multiple.delivery.notifications|;订单Id：%s", 1),  //# "订单发货多次通知
    JST_ORDER_INVOICE_ERROR("20900017", "|vat.invoice.issued|，|please.revoke|/|void.before.refund|！", 0);  //# "此订单已经开具了增值税专票，请撤销/作废后再审批退款
    ;

    String code;

    String script;

    int args;

    JstErrorCode(String code, String script,  int args) {
        this.code = code;
        this.script = script;
        this.args = args;
    }

}
