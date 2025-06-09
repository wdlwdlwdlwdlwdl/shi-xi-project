package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.TimeoutSettingQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.TimeoutSettingRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.TimeoutSettingDTO;
import com.aliyun.gts.gmall.platform.trade.core.convertor.TimeoutSettingConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.TimeoutSettingService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcTimeoutSettingDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.TimeoutSetting;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TimeoutSettingRepository;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TimeoutSettingServiceImpl implements TimeoutSettingService {

    @Autowired
    private TimeoutSettingRepository timeoutSettingRepository;

    @Autowired
    TimeoutSettingConverter timeoutSettingConverter;

    @Override
    public PageInfo<TimeoutSettingDTO> queryTimeoutSetting(TimeoutSettingQueryRpcReq req) {
        PageInfo<TcTimeoutSettingDO> list = timeoutSettingRepository.queryTimeoutSettingList(timeoutSettingConverter.toTimeoutSetting(req));
        return timeoutSettingConverter.toTimeoutSettingDTOPage(list);
    }

    @Override
    public TimeoutSettingDTO saveTimeoutSetting(TimeoutSettingRpcReq req) {
        TcTimeoutSettingDO timeoutSettingDO = timeoutSettingConverter.toTimeoutSettingDO(req);
        return timeoutSettingConverter.toTimeoutSettingDTO(timeoutSettingRepository.saveTimeoutSetting(timeoutSettingDO));
    }

    @Override
    public TimeoutSettingDTO updateTimeoutSetting(TimeoutSettingRpcReq req) {
        TcTimeoutSettingDO timeoutSettingDO = timeoutSettingConverter.toTimeoutSettingDO(req);
        return timeoutSettingConverter.toTimeoutSettingDTO(timeoutSettingRepository.updateTimeoutSetting(timeoutSettingDO));
    }

    @Override
    public TimeoutSettingDTO timeoutSettingDetail(TimeoutSettingRpcReq req) {
        TcTimeoutSettingDO timeoutSettingDO = timeoutSettingRepository.queryTimeoutSetting(req.getId());
        return timeoutSettingConverter.toTimeoutSettingDTO(timeoutSettingDO);
    }

    @Override
    public TimeoutSettingDTO queryTimeoutSettingDetail(TimeoutSettingRpcReq req) {
        TcTimeoutSettingDO timeoutSettingDO = timeoutSettingRepository.queryTimeoutSetting(timeoutSettingConverter.rpcToTimeoutSetting(req));
        return timeoutSettingConverter.toTimeoutSettingDTO(timeoutSettingDO);
    }

    @Override
    public boolean exist(TimeoutSettingRpcReq req) {
        return timeoutSettingRepository.exist(
            String.valueOf(req.getOrderStatus()),
            req.getPayType() ==null ? null : String.valueOf(req.getPayType())
        );
    }
}
