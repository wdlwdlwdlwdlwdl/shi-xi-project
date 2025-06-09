package com.aliyun.gts.gmall.platform.trade.persistence.mapper;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcDeliveryFeeDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcLogisticsDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TcDeliveryFeeMapper extends BaseMapper<TcDeliveryFeeDO> {

    /**
     * 物流方式
     * @param tcDeliveryFeeDO
     */
    List<TcDeliveryFeeDO> queryMatchDeliveryFee(TcDeliveryFeeDO tcDeliveryFeeDO);

}
