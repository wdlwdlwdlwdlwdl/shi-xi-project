package com.aliyun.gts.gmall.platform.trade.core.extension.reversal;

import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.CreateReversalRpcReq;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;

public interface ReversalCreateExt extends IExtensionPoints {

    /**
     * 售后单保存之前
     */
    void beforeSave(MainReversal reversal);

    /**
     * 售后单保存之前
     */
    void fillReversalInfo(MainReversal reversal, CreateReversalRpcReq req);

}
