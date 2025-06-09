package com.aliyun.gts.gmall.platform.trade.api.constant;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;

public enum CommonErrorCode implements ResponseCode {

    SERVER_ERROR("20001001", "|server.error|",  0),  //# "服务器异常"
    SERVER_ERROR_WITH_ARG("20001002", "|server.error|+: %s",  1),  //# "服务器异常
    NOT_DATA_OWNER("20001003", "|no.other.data|",  0),  //# "无法操作他人数据"
    CONCURRENT_UPDATE_FAIL("20001004", "|concurrent.modify.error|, |try.later|", 0),   //# "并发修改错误,稍后再试"
    CALL_PROMOTION_CENTER_FAIL("20001006", "|call.promotion.center.fail|", 2),
    ;


    // =========================

    String code;
    String script;
    int args;

    CommonErrorCode(String code, String script,  int args) {
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
