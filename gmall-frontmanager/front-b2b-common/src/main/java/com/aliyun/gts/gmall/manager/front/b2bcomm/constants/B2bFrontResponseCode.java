package com.aliyun.gts.gmall.manager.front.b2bcomm.constants;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 */
@Getter
@AllArgsConstructor
public enum B2bFrontResponseCode implements ResponseCode {
    DUPLICATED_SKU_ID("60031001", "|duplicate|SKU ID %s",  1),  //# "重复的
    NOT_B2B_CUST("60031002", "|vip.function.only|",  0),  //# "大客户才能使用该功能"
    ;

    private String code;
    private String script;
    private int    args;

    public String getDesc() {
        return getMessage();
    }
}
