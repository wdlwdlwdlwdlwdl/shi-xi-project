package com.aliyun.gts.gmall.manager.front.customer.dto.utils;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;

/**
 * 登录模块的响应码
 *
 * @author tiansong
 */
public enum CustomerFrontResponseCode implements ResponseCode {
    PASSWORD_IS_WRONG("60050001", "|password.incorrect|，|try.again|！",  0),  //# "密码不正确，请重试
    CUSTOMER_UPDATE_FAIL("60050002", "|user.info.update.fail|，|try.again|！",  0),  //# "用户信息更新失败，请重试
    CUSTOMER_QUERY_FAIL("60050003", "|user.query.fail|，|try.again|！",  0),  //# "用户查询失败，请重试
    CUSTOMER_LEVEL_CONFIG_FAIL("60050004", "|user.level.config.query.fail|，|try.again|！",  0),  //# "用户等级配置查询失败，请重试
    CUSTOMER_LEVEL_FAIL("60050005", "|user.level.query.fail|，|try.again|！",  0),  //# "用户等级查询失败，请重试
    CUSTOMER_NEW_PREDICT_FAIL("6005006", "|new.user.check.fail|, |try.again|", 0),   //# "新用户判断失败,请重试"
    ADDRESS_SAVE_FAIL("60050010", "|delivery.address.save.fail|，|try.again|！",  0),  //# "收货地址保存失败，请重试
    ADDRESS_DEL_FAIL("600500011", "|delivery.address.delete.fail|，|try.again|！",  0),  //# "收货地址删除失败，请重试
    ADDRESS_QUERY_FAIL("600500012", "|address.query.fail|，|try.again|！",  0),  //# "收货地址查询失败，请重试
    ADDRESS_DEFAULT_FAIL("600500013", "|set.default.delivery.address.fail|，|try.again|！",  0),  //# "设置默认收货地址失败，请重试
    INVOICE_CREATE_FAIL("600500020", "|invoice.add.fail|，|try.again|！",  0),  //# "新增发票失败，请重试
    INVOICE_DELETE_FAIL("600500021", "|invoice.delete.fail|，|try.again|！",  0),  //# "删除发票失败，请重试
    INVOICE_UPDATE_FAIL("600500022", "|invoice.edit.fail|，|try.again|！",  0),  //# "编辑发票失败，请重试
    INVOICE_QUERY_FAIL("600500023", "|invoice.query.fail|，|try.again|！",  0),  //# "查询发票失败，请重试
    LEVEL_COUPON_FAIL("600500030", "|level.benefit.not.exist|，|please.select.other.benefit|！",  0),  //# "该等级权益不存在，请领取其他权益
    COUPON_ALREADY_APPLY("600500031", "|benefit.already.received|+，|please.select.other.benefit|！",  0),  //# "该权益已领取，请领取其他权益
    DISTRIBUTED_LOCK_FAIL("600500034", "|request.too.fast|，|plz.try.again.later|",  0),  //# "你请求的太快了，请稍后再试
    COUPON_APPLY_FAIL("600500032", "|event.participated.or.not.targeted|",  0),  //# "您已参与活动或不在定向活动人群内
    COUPON_APPLY_ERROR("600500035", "|please.retry.after.config|",  0),  //# "当前券
    COUPON_APPLY_SUCCESS("600500032", "|gift.receive.success|，|each.coupon.limit.one|！",  0),  //# "礼包领取成功，各类型优惠券限领一张
    COUPON_APPLY_PART_SUCCESS("600500033", "|partial.gift.receive.success|，|please.retry.later|！",  0),  //# "礼包部分领取成功，请稍后重试
    PROMOTION_IS_JOINED("600500034", "|already.participated.event|！|thank.you.for.your.attention|！",  0),  //# "此活动您已参与！感谢您的关注
    CPUPON_APPLY_CUST_LEVEL_NOT_MATH("600500035", "|user.level.not.satisfy|！",  0),  //# "当前用户等级不满足该权益领取条件
    CANNOT_JOIN_C_VIP("600500036", "|cannot.join.store.member|",  0),  //# "不可加入店铺会员"
    CANNOT_JOIN_B_VIP("600500037", "|cannot.join.vip.customer|",  0),  //# "不可加入合作大客户"
    SHOP_NOT_EXIST("600500038", "|store.not.exist|",  0),  //# "店铺不存在"
    FAVOURITE_EXCEED_LIMIT("600500048", "|favourite.exceed.limit|",  0),  //# "店铺不存在"
    ;

    private String code;
    private String script;
    private int args;

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public int getArgs() {
        return this.args;
    }

    private CustomerFrontResponseCode(String code, String script,  int args) {
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
