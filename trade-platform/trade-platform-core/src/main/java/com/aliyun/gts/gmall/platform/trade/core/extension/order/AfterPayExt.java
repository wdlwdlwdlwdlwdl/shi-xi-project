package com.aliyun.gts.gmall.platform.trade.core.extension.order;

import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.core.input.pay.ToPayInput;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;

/**
 * 发起支付后、支付执行、支付回调成功后处理扩展能力
 *
 * @author xinchen
 */
public interface AfterPayExt extends IExtensionPoints {


    /**
     * 发起支付成功后扩展能力
     *
     * @param toPayInput
     * @return
     */
    TradeBizResult afterToPaySuccess(ToPayInput toPayInput);

    /**
     * 发起支付失败后处理扩展能力
     *
     * @param toPayInput
     * @return
     */
    TradeBizResult afterToPayFail(ToPayInput toPayInput);
}
