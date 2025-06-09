package com.aliyun.gts.gmall.manager.front.b2bcomm.utils;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import com.aliyun.gts.gmall.framework.api.rpc.dto.FailInfo;
import com.aliyun.gts.gmall.framework.server.exception.FrontendCare;

import java.util.Map;

public class ErrorCodeUtils {

    public static FrontendCare mapFailInfo(FailInfo failInfo) {
        return mapFailInfo(failInfo, null, null);
    }

    public static FrontendCare mapFailInfo(FailInfo failInfo, String extMessage) {
        return mapFailInfo(failInfo, null, extMessage);
    }

    public static FrontendCare mapFailInfo(FailInfo failInfo, Map<String, String> errMapping) {
        return mapFailInfo(failInfo, errMapping, null);
    }

    public static FrontendCare mapFailInfo(FailInfo failInfo, Map<String, String> errMapping, String extMessage) {
        String s = failInfo.getMessage();
        if (errMapping != null) {
            String mapping = errMapping.get(failInfo.getCode());
            if (mapping != null) {
                s = mapping;
            }
        }
        final String message = extMessage == null ? s : (s + "," + extMessage);

        return FrontendCare.builder().code(new ResponseCode() {
            @Override
            public String name() {
                return failInfo.getCode();
            }

            @Override
            public String getCode() {
                return failInfo.getCode();
            }

            @Override
            public String getScript() {
                return message;
            }

            @Override
            public String getMessage() {
                return message;
            }

            @Override
            public int getArgs() {
                return 0;
            }
        }).build();
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
                return message;
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

    public static FailInfo getFailInfo(FrontendCare frontendCare) {
        FailInfo fail = new FailInfo();
        fail.setCode(frontendCare.getCode().getCode());
        fail.setMessage(frontendCare.detailMessage());
        return fail;
    }
}
