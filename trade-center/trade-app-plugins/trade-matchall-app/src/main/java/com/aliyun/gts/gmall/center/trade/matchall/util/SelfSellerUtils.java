package com.aliyun.gts.gmall.center.trade.matchall.util;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Seller;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class SelfSellerUtils {

    // 是否自营商家
    public static boolean isSelfSeller(Seller seller) {
        if (seller.getSellerExtends() == null) {
            return false;
        }

        Map<String, String> shopConfig = seller.getSellerExtends().get("shop_config");
        if (shopConfig == null) {
            return false;
        }

        String value = shopConfig.get("shop_0");
        if (StringUtils.isBlank(value)) {
            return false;
        }

        return JSON.parseObject(value).getBooleanValue("selfRun");
    }
}
