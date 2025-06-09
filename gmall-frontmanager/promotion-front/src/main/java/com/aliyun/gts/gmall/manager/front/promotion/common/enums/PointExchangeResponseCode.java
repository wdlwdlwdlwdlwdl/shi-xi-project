package com.aliyun.gts.gmall.manager.front.promotion.common.enums;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;


public enum PointExchangeResponseCode implements ResponseCode {
    DATA_PARSE_ERROR("50000000", "|system.exception|，|plz.try.again.later|",  0),  //# "系统异常，请稍后再试"
    FREEZE_POINT_ERROR("50000001", "|points.freeze.fail|",  0),  //# "冻结积分失败"
    FREEZE_DEDUCT_POINT_ERROR("50000002", "|points.deduct.fail|",  0),  //# "抵扣积分失败"
    UN_FREEZE_POINT_ERROR("50000003", "|points.unfreeze.fail|",  0),  //# "解冻积分失败"
    APPLY_COUPON_ERROR("50000004", "|coupon.receive.fail|",  0),  //# "领取优惠券失败"
    QUERY_COUPON_ERROR("50000005", "|coupon.query.fail|",  0),  //# "查询优惠券失败"
    COUPON_POINT_PRICE_ERROR("50000006", "|points.coupon.price.empty|",  0),  //# "积分优惠券积分价格为空"
    COUPON_INFO_ERROR("50000007", "|points.coupon.info.error|",  0),  //# "积分优惠券信息有误"
    ;

    private String code;
    private String script;
    private int    args;

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public int getArgs() {
        return this.args;
    }

    private PointExchangeResponseCode(String code, String script,  int args) {
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
