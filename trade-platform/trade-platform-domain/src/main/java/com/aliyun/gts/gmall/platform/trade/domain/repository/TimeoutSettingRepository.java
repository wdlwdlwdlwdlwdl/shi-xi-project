package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcTimeoutSettingDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.TimeoutSetting;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.TimeoutSettingQuery;

public interface TimeoutSettingRepository {

    PageInfo<TcTimeoutSettingDO> queryTimeoutSettingList(TimeoutSetting req);

    TcTimeoutSettingDO queryTimeoutSetting(Long id);

    TcTimeoutSettingDO saveTimeoutSetting(TcTimeoutSettingDO tcTimeoutSettingDO);

    TcTimeoutSettingDO updateTimeoutSetting(TcTimeoutSettingDO tcTimeoutSettingDO);

    boolean exist(String orderStatus,String payType);

    TcTimeoutSettingDO queryTimeoutSetting(TimeoutSettingQuery req);
}
