package com.aliyun.gts.gmall.platform.trade.domain.util;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import com.aliyun.gts.gmall.framework.api.rpc.dto.FailInfo;
import com.aliyun.gts.gmall.framework.server.exception.FrontendCare;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.NonblockFail;

public class ErrorUtils {

    public static String getMessage(ResponseCode code, Object... args) {
        return code.getArgs() > 0 ? String.format(code.getMessage(), args) : code.getMessage();
    }

    public static FailInfo getFailInfo(ResponseCode code, Object... args) {
        return FailInfo.builder()
                .code(code.getCode())
                .message(getMessage(code, args))
                .build();
    }

    public static FailInfo getFailInfo(FrontendCare frontendCare) {
        return FailInfo.builder()
                .code(frontendCare.getCode().getCode())
                .message(frontendCare.detailMessage())
                .build();
    }

    public static NonblockFail getNonblockFail(Long sellerId, ResponseCode code, Object... args) {
        return NonblockFail.builder()
                .sellerId(sellerId)
                .code(code.getCode())
                .message(getMessage(code, args))
                .build();
    }

    public static ResponseCode mapCode(FailInfo failInfo) {
        return mapCode(failInfo.getCode(), failInfo.getMessage());
    }

    public static ResponseCode mapCode(String code, String message) {
        return new ResponseCode() {
            @Override
            public String name() {
                return code;
            }

            @Override
            public String getCode() {
                return code;
            }

            @Override
            public String getScript() {
                return "";
            }

            @Override
            public String getMessage() {
                return message;
            }

            @Override
            public int getArgs() {
                return 0;
            }
        };
    }

    public static <T> T throwUndeclared(Throwable t) {
        if (t instanceof Error) {
            throw (Error) t;
        }
        if (t instanceof RuntimeException) {
            throw (RuntimeException) t;
        }
        throw new RuntimeException(t);
    }
}
