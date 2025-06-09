package com.aliyun.gts.gmall.manager.front.trade.util;

import com.aliyun.gts.gmall.framework.api.consts.ResponseCode;
import com.aliyun.gts.gmall.framework.api.rpc.dto.FailInfo;
import com.aliyun.gts.gmall.framework.server.exception.FrontendCare;

import java.util.Map;

public class ErrorCodeUtils {

    public static FrontendCare mapFailInfo(FailInfo failInfo) {
        return mapFailInfo(failInfo, null);
    }

    public static FrontendCare mapFailInfo(FailInfo failInfo, Map<String, String> errMapping) {
        String s = failInfo.getMessage();
        if (errMapping != null) {
            String mapping = errMapping.get(failInfo.getCode());
            if (mapping != null) {
                s = mapping;
            }
        }
        final String message = s;

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
}
