package com.aliyun.gts.gmall.platform.trade.core.extension.reversal;

import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalStatusEnum;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;

public interface ReversalMessageExt extends IExtensionPoints {

    /**
     * 退单修改状态消息发送
     * @param reversal
     * @param targetStatus
     */
    void sendStatusChangedMessage(MainReversal reversal, ReversalStatusEnum targetStatus);


    void autoRefundMessage(MainReversal reversal, ReversalStatusEnum targetStatus);
}
