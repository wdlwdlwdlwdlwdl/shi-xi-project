package com.aliyun.gts.gmall.manager.front.trade.dto.utils;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;

/**
 * 交易的错误码
 *
 * @author tiansong
 */
public enum TradeFrontResponseCode implements ResponseCode {
    CART_ADD_FAILED("60040001", "|add.to.cart.fail|，|try.again|！",  0),  //# "添加购物车失败，请重试
    CART_QUERY_FAILED("60040002", "|cart.query.fail|，|try.again|！",  0),  //# "购物车查询失败，请重试
    CART_CHECK_ADD_FAILED("60040003", "|product.not.add.to.cart|！",  0),  //# "该商品不可加购物车
    CART_MODIFY_FAILED("60040004", "|cart.modify.fail|，|try.again|！",  0),  //# "修改购物车失败，请重试
    CART_DEL_FAILED("60040005", "|cart.product.delete.fail|，|try.again|！",  0),  //# "购物车商品删除失败，请重试
    CART_CAL_PRICE_FAILED("60040006", "|cart.price.calculate.fail|，|try.again|！",  0),  //# "购物车价格计算失败，请重试
    ORDER_CONFIRM_ERROR("60040010", "|order.confirm.fail|，|try.again|！",  0),  //# "订单确认失败，请重试
    ORDER_CREATE_ERROR("60040011", "|order.create.fail|，|try.again|！",  0),  //# "订单创建失败，请重试
    ORDER_CANCEL_ERROR("60040012", "|order.cancel.fail|，|try.again|！",  0),  //# "订单取消失败，请重试
    ORDER_DEL_ERROR("60040013", "|order.delete.fail|，|try.again|！",  0),  //# "订单删除失败，请重试
    ORDER_CONFIRM_RECEIPT_ERROR("60040014", "|order.confirm.receipt.fail|，|try.again|！",  0),  //# "订单确认收货失败，请重试
    ORDER_DETAIL_ERROR("60040015", "|order.details.query.fail|，|try.again|！",  0),  //# "查询订单详情失败，请重试
    ORDER_LIST_ERROR("60040016", "|order.list.query.fail|，|try.again|！",  0),  //# "查询订单列表失败，请重试
    ORDER_CONFIRM_ADDRESS_ERROR("60040017", "|address.not.support.delivery|，|select.delivery.address|",  0),  //# "该地址不支持配送，请重新选择收货地址"
    ORDER_CONFIRM_NO_ITEM("60040018", "|select.product.to.buy|",  0),  //# "请选择需要购买的商品"
    ORDER_CONFIRM_SELLER_ERROR("60040019", "单次不能购买超过%s个卖家的商品",  1),
    PAY_RENDER_ERROR("60040020", "|load.fail|，|try.again|！",  0),  //# "加载失败，请重试
    PAY_CHECK_ERROR("60040021", "|payment.status.load.fail|，|check.order.list|！",  0),  //# "支付状态加载失败，请移步订单列表查看
    TO_PAY_ERROR("60040022", "|payment.fail|，|try.again|！",  0),  //# "支付失败，请重试
    REVERSAL_LIST_ERROR("60040030", "|return.list.query.fail|，|try.again|！",  0),  //# "退货列表查询失败，请重试
    REVERSAL_DETAIL_ERROR("60040031", "|return.details.query.fail|，|try.again|！",  0),  //# "退货详情查询失败，请重试
    REVERSAL_ADD_ERROR("60040032", "|return.apply.submit.fail|，|try.again|！",  0),  //# "退货申请提交失败，请重试
    REVERSAL_CANCEL_ERROR("60040033", "|return.cancel.fail|，|try.again|！",  0),  //# "退货取消失败，请重试
    REVERSAL_REASON_ERROR("60040034", "|return.reason.query.fail|，|try.again|！",  0),  //# "退货原因查询失败，请重试
    REVERSAL_DELIVER_ERROR("60040035", "|mail.submit.fail|，|try.again|！",  0),  //# "邮寄提交失败，请重试
    REVERSAL_CHECK_ERROR("60040036", "|order.not.returnable|！",  0),  //# "该订单不可申请退货
    REVERSAL_BCR_ERROR("60040037", "|buyer.confirm.refund.fail|, |try.again|！", 0),   //# "买家确认退款失败,请重试
    TRADE_EVALUATION_ERROR("60040040", "|order.evaluation.fail|，|try.again|！",  0),  //# "订单评价失败，请重试
    LOGISTICS_DETAIL_ERROR("60040041", "|logistics.query.fail|，|try.again|！",  0),  //# "物流查询失败，请重试
    TRADE_ITEM_BATCH_ERROR("60040042", "|product.query.fail|，|try.again|！",  0),  //# "商品查询失败，请重试
    TRADE_ADDRESS_ERROR("60040043", "|address.query.fail|，|try.again|！",  0),  //# "收货地址查询失败，请重试
    TRADE_CNT_ERROR("60040044", "|order.qty.query.fail|，|try.again|",  0),  //# "订单数量查询失败，请重试"
    TRADE_SKU_BATCH_ERROR("60040045", "|product|SKU|batch.query.fail|，|try.again|！",  0),  //# "商品SKU批量查询失败，请重试
    TRADE_EVALUATION_REPEAT("60040046", "|cannot.repeated.evaluate|！",  0),  //# "已评价订单不可重复评价
    TRADE_ITEM_NOT_EXIST("60040047", "|product.not.exist|！",  0),  //# "商品不存在
    CART_SINGLE_QUERY_FAILED("60040048", "|cart.product.query.fail|，|try.again|！",  0),  //# "查询购物车商品失败，请重试
    ORDER_INVOICE_APPLY_ERROR("60040050", "|invoice.apply.fail|，|try.again|！",  0),  //# "申请发票失败，请重试
    ORDER_INVOICE_GET_ERROR("60040052", "|invoice.get.fail|，|try.again|！",  0),  //# "获取发票失败，请重试
    OSS_BUCKET_EMPTY("60040090", "|file.upload.fail|，|check.business.type|！", 0),
    QUERY_FAIL("60040091", "|query.fail|！",  0),
    PAY_ADD_ERROR("60040053", "|payment.fail|",  0),  //# "添加支付数据异常
    BACKEND_PAYMENT_ERROR("60040054", "|payment.fail|",  0),  // 后端支付异常
    CARD_DOES_NOT_EXIST("60040055","|query.fail|！" ,  0), // 卡不存在
    NO_HANDLER_ITEM_TYPE("60040056", "|select.product.to.buy|",  0),  //# "请选择需要购买的商品"
    ;

    /**
     * code
     */
    private String code;
    /**
     * script
     */
    private String script;
    /**
     * args
     */
    private int    args;

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public int getArgs() {
        return this.args;
    }

    private TradeFrontResponseCode(String code, String script,  int args) {
        this.code = code;
        this.script = script;
        this.args = args;
    }
    public String getScript() {
        return script;
    }

    public String getDesc() {
        return getMessage();
    }

}
