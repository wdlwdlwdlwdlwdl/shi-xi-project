package com.aliyun.gts.gmall.manager.front.b2bcomm.constants;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/2/5 9:59
 */
public enum  AdminResponseCode implements ResponseCode {
    TOKEN_INVALID("401", "token|invalid|"),   //# 失效"
    SERVER_ERROR("9999999", "|server.error|"),   //# "服务器错误"
    LIST_ACCOUNT_ERROR("1000001", "|account.query.fail|"),   //# "查询账号失败"
    EMPTY_REQUEST_PARAMS("1000002", "|empty.request.param|"),   //# "空请求参数"
    CREATE_ACCOUNT_TOTAL_FAILED("1000003", "|account.creation.full.fail|"),   //# "创建账号完全失败"
    CREATE_ACCOUNT_PARTIAL_FAILED("1000004", "|account.creation.partial.fail|"),   //# "创建账号部分失败"
    CREATE_ACCOUNT_FAILED("1200005", "|account.creation.fail|"),   //# "创建账号失败"
    UPDATE_ACCOUNT_FAILED("1200006", "|account.creation.fail|"),   //# "创建账号失败"
    DELETE_ACCOUNT_FAILED("1200006", "|account.deletion.fail|"),   //# "删除账号失败"
    DELETE_IDENTIFY_ACCOUNT_ERROR("1200007", "|self.account.deletion.error|"),   //# "删除自己账号错误"
    QUERY_SUPPLIER_ERROR("1030002", "查询supplier失败, supplierId=%s",1),
    CONFIRM_AUDIT_FAILED("1030003", "|audit.confirm.fail|"),   //# "审核确认失败"

    ;

    private String code;
    private String script;
    private int args;

    public String getCode() {
        return this.code;
    }

    public int getArgs() {
        return this.args;
    }

    private AdminResponseCode(String code, String script){
        this.code = code;
        this.script = script;
        this.args = 0;
    }
    private AdminResponseCode(String code,  String script, int args) {
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
