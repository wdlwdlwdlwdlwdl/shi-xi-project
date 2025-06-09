package com.aliyun.gts.gmall.platform.trade.core.extension.order;

import java.util.List;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.AbilityExtension;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;

public interface CustomerDeleteOrderPreCheckExt extends IExtensionPoints {

    @AbilityExtension(
        code = "CUST_DEL_ORDER_CHECK",
        name = "买家删除订单前置校验",
        description = "买家删除订单前置校验"
    )
    TradeBizResult<Boolean> check(Long primaryOrderId);

    TradeBizResult<Boolean> check(Long primaryOrderId, Long custId);

}
