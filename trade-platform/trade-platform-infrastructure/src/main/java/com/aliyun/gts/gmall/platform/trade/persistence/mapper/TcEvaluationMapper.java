package com.aliyun.gts.gmall.platform.trade.persistence.mapper;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcEvaluationDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.evaluation.EvaluationRate;
import com.aliyun.gts.gmall.platform.trade.domain.wrapper.EvaluationQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * Created by auto-generated on 2021/02/04.
 */

@Mapper
public interface TcEvaluationMapper extends BaseMapper<TcEvaluationDO> {

    EvaluationRate statisticsRateBySeller(EvaluationQueryWrapper query);

    List<TcEvaluationDO> queryRatePicList(EvaluationQueryWrapper query);

    List<TcEvaluationDO> getEvaluationList(EvaluationQueryWrapper query);
}
