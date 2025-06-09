package com.aliyun.gts.gmall.platform.trade.server.facade.impl;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.TimeoutSettingQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.TimeoutSettingRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.TimeoutSettingDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.TimeoutSettingFacade;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.TimeoutSettingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 说明： 超时设置实现
 *
 * @author yangl
 * @version 1.0
 * @date 2024/8/23 14:45
 */
@Service
@Slf4j
public class TimeoutSettingFacadeImpl implements TimeoutSettingFacade {

    @Autowired
    private TimeoutSettingService timeoutSettingService;

    @Override
    public RpcResponse<PageInfo<TimeoutSettingDTO>> queryTimeoutSetting(TimeoutSettingQueryRpcReq req) {
        return RpcResponse.ok(timeoutSettingService.queryTimeoutSetting(req));
    }

    @Override
    public RpcResponse<TimeoutSettingDTO> saveTimeoutSetting(TimeoutSettingRpcReq req) {
        if(timeoutSettingService.exist(req)){
            throw new GmallException(CommonResponseCode.AlreadyExists);
        }
        TimeoutSettingDTO dto = timeoutSettingService.saveTimeoutSetting(req);
        if (dto != null) {
            return RpcResponse.ok(dto);
        }
        throw new GmallException(CommonResponseCode.ServerError);
    }

    @Override
    public RpcResponse<TimeoutSettingDTO> updateTimeoutSetting(TimeoutSettingRpcReq req) {
        if (Objects.nonNull(req.getPayType()) && Objects.nonNull(req.getOrderStatus())) {
            // 唯一性校验 防止重复
            TimeoutSettingQueryRpcReq timeoutSettingQueryRpcReq = new TimeoutSettingQueryRpcReq();
            timeoutSettingQueryRpcReq.setPage(new PageParam(PageParam.DEFAULT_PAGE_NO, PageParam.DEFAULT_PAGE_SIZE));
            timeoutSettingQueryRpcReq.setPayType(req.getPayType());
            timeoutSettingQueryRpcReq.setOrderStatus(req.getOrderStatus());
            PageInfo<TimeoutSettingDTO> timeoutSettingDTOPageInfo = timeoutSettingService.queryTimeoutSetting(timeoutSettingQueryRpcReq);
            if (Objects.nonNull(timeoutSettingDTOPageInfo) && CollectionUtils.isNotEmpty(timeoutSettingDTOPageInfo.getList())) {
                TimeoutSettingDTO checkObj = timeoutSettingDTOPageInfo.getList().stream()
                    .filter(Objects::nonNull)
                    .filter(timeoutSettingDTO -> !req.getId().equals(timeoutSettingDTO.getId()))
                    .findFirst()
                    .orElse(null);
                if (Objects.nonNull(checkObj)) {
                    throw new GmallException(CommonResponseCode.AlreadyExists);
                }
            }
        }
        TimeoutSettingDTO dto = timeoutSettingService.updateTimeoutSetting(req);
        if (dto != null) {
            return RpcResponse.ok(dto);
        }
        throw new GmallException(CommonResponseCode.ServerError);
    }

    @Override
    public RpcResponse<TimeoutSettingDTO> timeoutSettingDetail(TimeoutSettingRpcReq req) {
        return RpcResponse.ok(timeoutSettingService.timeoutSettingDetail(req));
    }
}
