package com.aliyun.gts.gmall.platform.trade.persistence.mapper;

import java.util.List;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcLogisticsDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created by auto-generated on 2021/02/04.
 */

@Mapper
public interface TcLogisticsMapper extends BaseMapper<TcLogisticsDO> {


    void batchInsert(@Param("list") List<TcLogisticsDO> list);

}
