package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.TimeoutSettingQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.TimeoutSettingRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.TimeoutSettingDTO;

public interface TimeoutSettingService {

    PageInfo<TimeoutSettingDTO> queryTimeoutSetting(TimeoutSettingQueryRpcReq req);

    TimeoutSettingDTO saveTimeoutSetting(TimeoutSettingRpcReq req);

    TimeoutSettingDTO updateTimeoutSetting(TimeoutSettingRpcReq req);

    TimeoutSettingDTO timeoutSettingDetail(TimeoutSettingRpcReq req);

    boolean exist(TimeoutSettingRpcReq req);

    TimeoutSettingDTO queryTimeoutSettingDetail(TimeoutSettingRpcReq req);
}
