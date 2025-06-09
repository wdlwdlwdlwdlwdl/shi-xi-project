package com.aliyun.gts.gmall.center.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.center.trade.domain.repositiry.TcEvaluationExtRepository;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcEvaluationDO;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcEvaluationMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author : zhang.beilei
 * @date : 2022/10/18 17:32
 **/
@Repository
@Slf4j
public class TcEvaluationExtRepositoryImpl implements TcEvaluationExtRepository {

    @Resource
    TcEvaluationMapper tcEvaluationMapper;

    @Override
    public TcEvaluationDO getById(Long id) {
        LambdaQueryWrapper<TcEvaluationDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TcEvaluationDO::getId, id);
        return tcEvaluationMapper.selectOne(queryWrapper);
    }

    @Override
    public Boolean updateExtend(Long id,String extendStr) {
        TcEvaluationDO tcEvaluationDO = new TcEvaluationDO();
        tcEvaluationDO.setGmtModified(new Date());
        LambdaUpdateWrapper<TcEvaluationDO> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(TcEvaluationDO::getId,id)
                .set(TcEvaluationDO::getExtend,extendStr);
        return  tcEvaluationMapper.update(tcEvaluationDO,updateWrapper)>0;
    }

    @Override
    public Boolean updateExtendByPrimaryOrderId(Long id,String extendStr) {
        TcEvaluationDO  tcEvaluation = this.getById(id);
        TcEvaluationDO tcEvaluationDO = new TcEvaluationDO();
        tcEvaluationDO.setGmtModified(new Date());
        LambdaUpdateWrapper<TcEvaluationDO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(TcEvaluationDO::getPrimaryOrderId,tcEvaluation.getPrimaryOrderId())
                .eq(TcEvaluationDO::getItemId,0)
                .set(TcEvaluationDO::getExtend,extendStr);
        return  tcEvaluationMapper.update(tcEvaluationDO,updateWrapper)>0;
    }
}
