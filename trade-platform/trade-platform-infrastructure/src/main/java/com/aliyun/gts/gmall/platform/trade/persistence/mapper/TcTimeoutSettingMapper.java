package com.aliyun.gts.gmall.platform.trade.persistence.mapper;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcTimeoutSettingDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.TimeoutSetting;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TcTimeoutSettingMapper extends BaseMapper<TcTimeoutSettingDO> {

    List<TcTimeoutSettingDO> queryTimeoutSettingList(TimeoutSetting req);

}
