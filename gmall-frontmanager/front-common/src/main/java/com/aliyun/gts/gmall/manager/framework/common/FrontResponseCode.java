package com.aliyun.gts.gmall.manager.framework.common;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import com.aliyun.gts.gmall.framework.api.rpc.dto.FailInfo;

/**
 * 不用枚举，直接将RPC错误信息包装转换
 * @author tiansong
 */
public class FrontResponseCode implements ResponseCode {
    private String code;
    private String message;

    public FrontResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public FrontResponseCode(FailInfo failInfo) {
        this.code = failInfo.getCode();
        this.message = failInfo.getMessage();
    }

    @Override
    public String name() {
        return this.code;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getScript() {
        return message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public int getArgs() {
        return 0;
    }
}
