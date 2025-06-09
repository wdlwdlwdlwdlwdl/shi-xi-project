package com.aliyun.gts.gmall.platform.trade.persistence.mapper;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonFeeDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CancelReason;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TcCancelReasonFeeMapper extends BaseMapper<TcCancelReasonFeeDO> {

}
