package com.aliyun.gts.gmall.platform.trade.common.util;

import com.aliyun.gts.gmall.platform.trade.common.constants.CreatingOrderParamConstants;

import java.util.Map;
import java.util.Objects;

public class CreatingOrderParamUtils {
    public static boolean isHelpOrder(Map<String, Object> params) {
        return Objects.nonNull(params)&& ((Boolean) params.getOrDefault(CreatingOrderParamConstants.IS_HELP_ORDER, Boolean.FALSE));
    }
}
