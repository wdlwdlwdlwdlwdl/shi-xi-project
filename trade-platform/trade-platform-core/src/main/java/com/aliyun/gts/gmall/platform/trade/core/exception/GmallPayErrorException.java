package com.aliyun.gts.gmall.platform.trade.core.exception;


import com.aliyun.gts.gmall.platform.trade.api.constant.PayErrorCode;

public class GmallPayErrorException extends RuntimeException{

    private final PayErrorCode error;

    public GmallPayErrorException(PayErrorCode error) {
        super(error.description());
        this.error = error;
    }

    public PayErrorCode getPayError() {
        return error;
    }
}
