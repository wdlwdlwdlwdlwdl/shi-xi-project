package com.aliyun.gts.gmall.platform.trade.core.extension.pay;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.AbilityExtension;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.core.input.pay.ToPayInput;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;

/**
 * @author xinchen
 * <p>
 * 支付校验扩展能力
 */
public interface CheckPayExt extends IExtensionPoints {


    @AbilityExtension(
            code = "CUSTOM_TO_PAY_CHECK",
            name = "发起支付自定义校验",
            description = "发起支付自定义校验"
    )
    TradeBizResult<Boolean> customToPayCheck(MainOrder mainOrder, ToPayInput toPayInput);
}
