package com.aliyun.gts.gmall.platform.trade.core.extension.logistics.defaultimpl;

import java.util.ArrayList;
import java.util.List;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics.LogisticsInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics.TcLogisticsRpcReq;
import com.aliyun.gts.gmall.platform.trade.common.constants.LogisticsCompanyTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.LogisticsTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.extension.logistics.LogisticsParamExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcLogisticsDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultLogisticsParamExt implements LogisticsParamExt {

    @Override
    public TradeBizResult<List<TcLogisticsDO>> preProcess(TcLogisticsRpcReq req) {
        List<TcLogisticsDO> list = new ArrayList<>();
        for(LogisticsInfoRpcReq logisticsInfoRpcReq : req.getInfoList()){
            TcLogisticsDO tcLogisticsDO = new TcLogisticsDO();
            BeanUtils.copyProperties(req , tcLogisticsDO);
            BeanUtils.copyProperties(logisticsInfoRpcReq , tcLogisticsDO);
            tcLogisticsDO.setCompanyType(logisticsInfoRpcReq.getCompanyType());
            tcLogisticsDO.setType(req.getType());
            list.add(tcLogisticsDO);
        }
        return TradeBizResult.ok(list);
    }
}
