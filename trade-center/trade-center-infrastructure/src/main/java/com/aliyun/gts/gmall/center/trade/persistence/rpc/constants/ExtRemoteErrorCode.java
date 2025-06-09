package com.aliyun.gts.gmall.center.trade.persistence.rpc.constants;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExtRemoteErrorCode implements ResponseCode {

    B2B_SOURCING_BILL_ILLEGAL("20830001", "|invalid.bid.document|",  0);  //# "无效决标单"
    ;

    private String code;
    private String script;
    private int args;
}
