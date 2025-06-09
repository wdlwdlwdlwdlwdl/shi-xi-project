package com.aliyun.gts.gmall.platform.trade.common.util;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

public class PayUtils {

    public static String buildOutTradeNo(Long primaryOrderId, Integer stepNo) {
        return stepNo == null
                ? String.valueOf(primaryOrderId)
                : primaryOrderId + "_" + stepNo;
    }

    public static OutTradeNo parse(String outTradeNo) {
        OutTradeNo parsed = new OutTradeNo();
        if (outTradeNo == null) {
            return parsed;
        }
        String[] arr = StringUtils.split(outTradeNo, '_');
        if (arr.length > 0 && StringUtils.isNumeric(arr[0])) {
            parsed.primaryOrderId = Long.parseLong(arr[0]);
        }
        if (arr.length > 1 && StringUtils.isNumeric(arr[1])) {
            parsed.stepNo = Integer.parseInt(arr[1]);
        }
        return parsed;
    }

    @Data
    public static class OutTradeNo {
        private Long primaryOrderId;
        private Integer stepNo;
    }
}
