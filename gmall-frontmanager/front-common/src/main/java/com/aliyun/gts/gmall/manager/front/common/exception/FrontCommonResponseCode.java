package com.aliyun.gts.gmall.manager.front.common.exception;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;

/**
 * 前台应用统一的错误码
 *
 * @author tiansong
 */
public enum FrontCommonResponseCode implements ResponseCode {
    DATA_PARSE_ERROR("60000000", "|system.exception|，|plz.try.again.later|！",  0),  //# "系统异常，请稍后再试
    CUSTOMER_CENTER_ERROR("60000001", "|system.exception|，|plz.try.again.later|！",  0),  //# "系统异常，请稍后再试
    ITEM_CENTER_ERROR("60000002", "|system.exception|，|plz.try.again.later|！",  0),  //# "系统异常，请稍后再试
    PROMOTION_CENTER_ERROR("60000003", "|system.exception|，|plz.try.again.later|！",  0),  //# "系统异常，请稍后再试
    TRADE_CENTER_ERROR("60000004", "|system.exception|，|plz.try.again.later|！",  0),  //# "系统异常，请稍后再试
    MISC_CENTER_ERROR("60000005", "|system.exception|，|plz.try.again.later|！",  0),  //# "系统异常，请稍后再试
    DATA_NO_PERMISSION("60000006", "|data.inaccessible|！",  0),  //# "数据无法访问
    DATA_NOT_EXIST("60000007", "|data.not.exist|！",  0),  //# "访问的数据不存在
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

    private FrontCommonResponseCode(String code, String script,  int args) {
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
