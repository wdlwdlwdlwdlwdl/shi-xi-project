package com.aliyun.gts.gmall.manager.biz.constant;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import lombok.Getter;

@Getter
public enum MiscResponseCode implements ResponseCode {

    DICT_QUERY_FAIL("60020001", "|query.fail|",  0),  //# "查询失败"

    DATA_OWNER_ERROR("60020002", "|no.permission|",  0),  //# "无权操作该对象"
    ;


    private String code;
    private String script;
    private int args;

    private MiscResponseCode(String code, String script,  int args) {
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
