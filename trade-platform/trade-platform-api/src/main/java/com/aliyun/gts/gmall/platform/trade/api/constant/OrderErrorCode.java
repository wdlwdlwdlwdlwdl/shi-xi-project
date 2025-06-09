package com.aliyun.gts.gmall.platform.trade.api.constant;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;

import java.util.Arrays;

public enum OrderErrorCode implements ResponseCode {
    //通用
    ILLEGAL_ARGS("20000000", "|invalid.param|"),   //# "入参非法"
    // 下单相关
    INVENTORY_NOT_ENOUGH("20110001", "|stock.insufficient|"),   //# "库存不足"
    POINT_NOT_ENOUGH("20110002", "|points.insufficient|"),   //# "用户积分不足"
    ITEM_NOT_EXISTS("20110003", "|product.not.exist|"),   //# "商品不存在"
    USER_NOT_EXISTS("20110004", "|user.not.exist|"),   //# "用户不存在"
    PROMOTION_NOT_EXISTS("20110005", "|discount.not.exist|"),   //# "所选优惠不存在"
    RECEIVER_NOT_EXISTS("20110006", "|address.not.exist|"),   //# "收货地址不存在或地址内容异常"
    PRICE_CALC_DISCORD("20110007", "|price.mismatch|"),   //# "价格计算不一致"
    ORDER_STATUS_ILLEGAL("20110008", "|order.status.invalid|"),   //# "订单状态不合法"
    ORDER_REDUCE_INVENTORY_ERROR("20110009", "|stock.deduct.failed|"),   //# "订单扣减库存失败"
    ORDER_USER_NOT_MATCH("20110010", "|user.not.match|, |no.permission|"), //# \"订单用户不匹配", 无权操作"
    ITEM_NOT_ENABLE("20110011", "|product.not.listed|"),   //# "商品未上架"
    ORDER_TOKEN_EXPIRED("20110012", "|order.token.invalid|"),
    PAY_PRICE_ZERO("20110013", "|order.amount|+0"),   //# "下单金额为
    PAY_CHANNEL_ILLEGAL("20110014", "|pay.channel.invalid|"),   //# "支付渠道错误"
    RECEIVER_NOT_SUPPORT_BY_ITEM("20110015", "|address.unsupported|,|not.sellable|"), //# \"商品不支持用户所在收货地址", 无法销售"
    ORDER_TOKEN_LOCK_FAIL("20110016", "|concurrent.order|"),   //# "下单并发提交"
    ORDER_TOKEN_USED("20110017", "|order.created|,|no.duplicate.submit|"), //# \"订单已创建", 请勿重复提交"
    ORDER_PRICE_ILLEGAL("20110018", "|order.amount.abnormal|"),   //# "订单金额异常"
    MERGE_ORDER_FORBIDDEN("20110019", "|no.merge.orders|"),   //# "当前商品不允许合并下单"
    USER_STATUS_ILLEGAL("20110020", "|user.status.invalid|"),   //# "用户当前状态不能进行交易"
    USER_AGE_ILLEGAL("20110023", "|user.age.invalid|"),   //# "用户当前状态不能进行交易"
    PROMOTION_DISCORD("20110021", "|promotion.changed|"),   //# "营销活动发生变化"
    ITEM_TYPE_NOT_MATCH("20110022", "|item.type.not.match|"),
    ORDER_NOT_EXISTS("20111001", "|order.not.exist|"),   //# "订单不存在"
    ORDER_DEDUCT_POINT_ILLEGAL_ARGS("20111009", "|order.info.incomplete|、|points.deduct.failed|"),   //# "订单信息不完整、无法扣减积分"
    DELIVERY_NOT_ENABLE("20250324", "|delivery.not.listed|"),

    // 购物车相关
    CART_AGE_LIMIT("20211001", "|trade.cart.age.limit|"),   //# "订单不存在"

    //搜索相关
    ORDER_QUERY_PARAM_ERROR("20010001", "|query.param.invalid|"),   //# "订单查询入参错误"
    ORDER_QUERY_PROCESS_ERROR("20010002", "|query.exec.error|"),   //# "订单查询执行错误"

    //修改订单相关
    ORDER_STATUS_ERROR("20020004", "|order.status.error|"),
    ORDER_STATUS_CHANGE_ERROR("20020001", "|order.modify.error|"),   //# "订单修改状态错误"
    ORDER_PRICE_CHANGE_ILLEGAL("20020002", "|order.amount.incorrect|"),   //# "订单金额不正确"
    ORDER_CHANGED_ON_CONFIRM("20020003", "|order.changed|, |confirm.again|"), //# 操作的订单发生变化", 请重新确认"
    ORDER_NOTCANCEL_MERCHANT_LOGISTICS("20020005", "|not.cancel|"),//不可取消

    //物流相关
    LOGISTICS_CREATE_ERROR("20030001", "|logistics.creation.failed|"),   //# "物流信息创建失败"
    LOGISTICS_USER_NOT_MATCH("20030002", "|logistics.user.not.match|+,+|no.permission|"), //# \"物流用户不匹配", 无权操作"
    LOGISTICS_OTP_NOT_MATCH("20030003", "|logistics.otp.not.match|"),
    // 多阶段
    MULTI_STEP_DISCORD("20031001", "|multi.stage.mismatch|"),   //# "多阶段计算不一致"
    STEP_NO_ILLEGAL("20031002", "|stage.num.incorrect|"),   //# "阶段序号不正确"
    NOT_MULTI_STEP_ORDER("20031003", "|not.multi.stage|"),   //# "非多阶段订单"
    STEP_STATUS_ILLEGAL("20031004", "|stage.status.incorrect|"),   //# "阶段状态不正确"
    STEP_FORM_DATA_CHANGED("20031005", "|order.info.changed|"),   //# "订单信息发生变化"
    STEP_TEMPLATE_ERROR("20031006", "|multi.stage.config.error|"),   //# "多阶段模版配置不正确"
    STEP_FORM_DATA_ERROR("20031007", "|multi.stage.data.error|"),   //# "多阶段提交数据不正确"

    // 评价
    EVALUATION_CROSS_PRIMARY_ORDER("20032001", "|review.main.order|ID|not.match|"),   //# "评价主订单ID不一致"
    EVALUATION_STATUS_ILLEGAL("20032002", "|cannot.review|"),   //# "当前订单不能评价"
    EVALUATION_NOT_FOUND("20032003", "|review.not.exist|"),   //# "评价记录不存在"
    EVALUATION_NOT_AUDIT("20032004", "|review.not.audit|"),

    LOAN_OVER_LIMIT("20032005", "|loan.over.limit|"),

    INSTALLMENT_OVER_LIMIT("20032007", "|installment.over.limit|"),

    UNABLE_ORDER_SAME_TIME("20032017", "|cannot.order.same.time|"),

    PAY_MODEL_ILLEGAL("20032019", "|pay.model.illegal|"),

    ORDER_ITEM_NULL("20032022", "|order.item.null|"),

    ORDER_ITEM_ILLEGAL("20032023", "|order.item.illegal|"),

    TRADE_CENTER_ERROR("20999999", "|trade.system.error|"),  //# "交易系统异常"

    //订单锁单
    ORDER_LOCKED("20037021", "|order.locked|"),

    //OTP码为空
    OPT_CODE_NULL("30000000", "|opt.code.null|"),
    OTP_CODE_ERROR("30000002", "|otp.code.error|"),
    TC_LOGISTICS_NULL("30000001", "|tc.logistics.null|"),
    CARD_NOT_EXISTED("20250325", "|cardId.not.existed|"),;

    // =================================

    String code;

    String script;

    OrderErrorCode(String code, String script) {
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

    public static OrderErrorCode codeOf(String code) {
        return Arrays.stream(OrderErrorCode.values())
            .filter(en -> en.code.equals(code))
            .findFirst().orElse(null);
    }
    public String getScript() {
        return script;
    }
}
