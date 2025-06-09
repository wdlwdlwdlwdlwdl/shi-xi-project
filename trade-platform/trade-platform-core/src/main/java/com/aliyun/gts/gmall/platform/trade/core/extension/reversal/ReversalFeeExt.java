package com.aliyun.gts.gmall.platform.trade.core.extension.reversal;

import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;

public interface ReversalFeeExt extends IExtensionPoints {

    /**
     * 输入：指定主售后单、子售后单中的退款金额 (cancelAmt)，也可以不指定，退还默认金额
     * 输出： 填充主售后单、子售后单中的 cancelAmt、pointAmt、pointCount、realAmt、cancelSeparateAmt
     *      fixbug: 同时校验&填充 cancelQty
     *      202305: 运费独立到 mainReversal.reversalFeatures.cancelFreightFee
     *
     * 逻辑：校验+分摊
     *
     *
     * 默认实现:
     * 1：指定的子单金额、件数   -- 按指定数退
     * 2：不指定的子单金额、件数  -- 全退
     * 3：主单上的退回金额、件数忽略
     */
    void divideCancelAmt(MainReversal reversal);
}
