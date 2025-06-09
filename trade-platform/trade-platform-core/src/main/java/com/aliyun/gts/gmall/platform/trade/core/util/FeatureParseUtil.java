package com.aliyun.gts.gmall.platform.trade.core.util;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderFeatureKeyConstants;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.SellerAccountInfo;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;

public class FeatureParseUtil {

    public static SellerAccountInfo parseSellerAccountInfo(TcOrderDO tcOrder) {

        OrderAttrDO orderAttrDO = tcOrder.getOrderAttr();
        if (orderAttrDO == null) {
            return null;
        }
        Map<String, String> extras = orderAttrDO.extras();
        if (MapUtils.isEmpty(extras)) {
            return null;
        }

        String sellerAccountInfoStr = extras.get(OrderFeatureKeyConstants.SELLER_ACCOUNT_INFO);
        if (sellerAccountInfoStr == null) {
            return null;
        }

        return JSONObject.parseObject(sellerAccountInfoStr,SellerAccountInfo.class);
    }
}
