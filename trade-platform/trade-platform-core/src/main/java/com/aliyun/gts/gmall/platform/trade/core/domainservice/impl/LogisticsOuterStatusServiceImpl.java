package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.aliyun.gts.gmall.middleware.logistics.LogisticsComponent;
import com.aliyun.gts.gmall.middleware.logistics.constants.KdnLogisticsCompanyEnum;
import com.aliyun.gts.gmall.middleware.logistics.constants.LogisTraceStateEnum;
import com.aliyun.gts.gmall.middleware.logistics.entity.LogisticsQueryReq;
import com.aliyun.gts.gmall.middleware.logistics.entity.LogisticsTraceDTO;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.LogisticsOuterStatusService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcLogisticsDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcLogisticsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Configuration
@ConditionalOnProperty(prefix = "trade", name = "logisticsOuterStatusService",
        havingValue = "default", matchIfMissing = true)
public class LogisticsOuterStatusServiceImpl implements LogisticsOuterStatusService {

    @Autowired
    private LogisticsComponent logisticsComponent;
    @Autowired
    private TcLogisticsRepository tcLogisticsRepository;

    @NacosValue(value = "${trade.mock.logisticsReceiveStatus:}", autoRefreshed = true)
    private String mockLogisticsReceiveStatus;

    @Override
    public ReceiveStatus getOrderReceiveStatus(Long primaryOrderId) {
        List<TcLogisticsDO> list = tcLogisticsRepository.queryByPrimaryId(primaryOrderId, null);
        if (CollectionUtils.isEmpty(list)) {
            return ReceiveStatus.UNKNOW;
        }
        boolean allReceived = true;
        for (TcLogisticsDO logist : list) {
            ReceiveStatus s = getLogistReceiveStatus(logist);
            if (s == ReceiveStatus.NOT_RECEIVED) {
                return ReceiveStatus.NOT_RECEIVED;
            } else if (s != ReceiveStatus.RECEIVED) {
                allReceived = false;
            }
        }
        return allReceived ? ReceiveStatus.RECEIVED : ReceiveStatus.UNKNOW;
    }

    @Override
    public ReceiveStatus getLogistReceiveStatus(TcLogisticsDO logistics) {
        if (StringUtils.isNoneBlank(mockLogisticsReceiveStatus)) {
            return ReceiveStatus.valueOf(mockLogisticsReceiveStatus);
        }

        KdnLogisticsCompanyEnum company = KdnLogisticsCompanyEnum.codeOf(logistics.getCompanyType());
        //查不到公司类型为上游发货时传入物流公司类型同快递鸟不匹配、这里跳过校验
        if (company == null) {
            return ReceiveStatus.UNKNOW;
        }

        LogisticsQueryReq req = new LogisticsQueryReq();
        req.setCompanyCode(company.getCompanyCode());
        req.setLogisticsCode(logistics.getLogisticsId());
        req.setCustomerPhone(logistics.getReceiverPhone());

        LogisticsTraceDTO logisticsTraceDTO = logisticsComponent.queryTraceV2(req);
        // 为空的情况、可能是快递鸟因为系统异常查询失败、这里做弱依赖处理、直接返回已签收
        if (StringUtils.isEmpty(logisticsTraceDTO.getState())) {
            return ReceiveStatus.UNKNOW;
        }
        if (LogisTraceStateEnum.SIGN_FOR.getCode().intValue() !=
                Integer.valueOf(logisticsTraceDTO.getState()).intValue()) {
            log.warn("LogisTrace not sign for: " + JSONObject.toJSONString(logisticsTraceDTO));
            return ReceiveStatus.NOT_RECEIVED;
        }
        return ReceiveStatus.RECEIVED;
    }
}
