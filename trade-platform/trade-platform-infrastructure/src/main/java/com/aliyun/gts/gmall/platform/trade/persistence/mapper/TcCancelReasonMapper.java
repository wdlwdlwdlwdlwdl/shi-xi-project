package com.aliyun.gts.gmall.platform.trade.persistence.mapper;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcSellerConfigDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcTimeoutSettingDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CancelReason;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.TimeoutSetting;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TcCancelReasonMapper extends BaseMapper<TcCancelReasonDO> {

    List<TcCancelReasonDO> queryCancelReasonList(CancelReason req);
}
