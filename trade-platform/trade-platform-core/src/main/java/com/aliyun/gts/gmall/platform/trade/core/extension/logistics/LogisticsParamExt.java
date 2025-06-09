package com.aliyun.gts.gmall.platform.trade.core.extension.logistics;

import java.util.List;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.AbilityExtension;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.IExtensionPoints;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics.TcLogisticsRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcLogisticsDO;

public interface LogisticsParamExt extends IExtensionPoints {

    @AbilityExtension(
        code = "LOGISTICS_PRE_PROCESS",
        name = "物流信息预处理",
        description = "物流信息预处理"
    )
    TradeBizResult<List<TcLogisticsDO>> preProcess(TcLogisticsRpcReq req);

}
