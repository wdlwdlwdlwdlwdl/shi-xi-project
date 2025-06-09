package com.aliyun.gts.gmall.platform.trade.persistence.mapper;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderSummaryDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.TcSumOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by auto-generated on 2021/02/04.
 */

@Mapper
public interface TcSumOrderMapper extends BaseMapper<TcOrderSummaryDO> {
    void batchInsert(@Param("list") List<TcOrderSummaryDO> list);

    List<TcOrderSummaryDO> querySummaryList(TcSumOrder req);

}
