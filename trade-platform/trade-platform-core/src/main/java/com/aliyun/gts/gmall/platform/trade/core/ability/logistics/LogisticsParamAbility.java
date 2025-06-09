package com.aliyun.gts.gmall.platform.trade.core.ability.logistics;

import java.util.List;
import java.util.Objects;

import com.aliyun.gts.gmall.framework.extensionengine.ext.aop.Ability;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.BaseAbility;
import com.aliyun.gts.gmall.framework.extensionengine.ext.util.reducers.Reducers;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics.TcLogisticsRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.extension.logistics.LogisticsParamExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.logistics.defaultimpl.DefaultLogisticsParamExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcLogisticsDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Ability(
    code = LogisticsParamAbility.LOGISTICS_PARAM_ABILITY,
    fallback = DefaultLogisticsParamExt.class,
    description = "物流参数预处理能力"
)
public class LogisticsParamAbility extends BaseAbility<BizCodeEntity, LogisticsParamExt> {

    public static final String LOGISTICS_PARAM_ABILITY =
        "com.aliyun.gts.gmall.platform.trade.core.ability.search.LogisticsParamAbility";

    public TradeBizResult<List<TcLogisticsDO>> preProcess(TcLogisticsRpcReq request){
        TradeBizResult<List<TcLogisticsDO>>  result = this.executeExt(
            BizCodeEntity.getFromThreadLocal(),
            extension -> extension.preProcess(request),
            LogisticsParamExt.class,
            Reducers.firstOf(Objects::nonNull)
        );
        return result;
    }

}
