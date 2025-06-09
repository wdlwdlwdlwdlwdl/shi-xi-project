package com.aliyun.gts.gmall.platform.trade.core.extension.order;

import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;

/**
 * 退款后自定义处理扩展类
 *
 * @author xinchen
 */
public interface AfterRefundExt extends IExtensionPoints {


    TradeBizResult afterRefund(MainReversal mainReversal);
}
