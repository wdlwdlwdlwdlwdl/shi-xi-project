package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcEvaluationDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.evaluation.Evaluation;
import com.aliyun.gts.gmall.platform.trade.domain.entity.evaluation.EvaluationRate;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcEvaluationRepository;
import com.aliyun.gts.gmall.platform.trade.domain.wrapper.EvaluationQueryWrapper;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcEvaluationMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
public class TcEvaluationRepositoryImpl implements TcEvaluationRepository {

    @Autowired
    private TcEvaluationMapper tcEvaluationMapper;

    @Override
    public List<TcEvaluationDO> batchQueryByPrimaryOrderId(Collection<Long> primaryOrderIds) {
        LambdaQueryWrapper<TcEvaluationDO> q = Wrappers.lambdaQuery();
        return tcEvaluationMapper.selectList(
                q.in(TcEvaluationDO::getPrimaryOrderId, primaryOrderIds)
                .orderByAsc(TcEvaluationDO::getGmtCreate)
        );
    }

    @Override
    public List<TcEvaluationDO> batchSubOrderByPrimaryOrderId(Collection<Long> primaryOrderIds) {
        LambdaQueryWrapper<TcEvaluationDO> q = Wrappers.lambdaQuery();
        return tcEvaluationMapper.selectList(
                q.in(TcEvaluationDO::getPrimaryOrderId, primaryOrderIds)
                        .gt(TcEvaluationDO::getItemId, 0)
                        .orderByAsc(TcEvaluationDO::getGmtCreate)
        );
    }

    @Override
    public EvaluationRate statisticsRateBySeller(Evaluation evaluation) {
        EvaluationQueryWrapper query = new EvaluationQueryWrapper();
        query.setSellerId(evaluation.getSellerId());
        query.setSkuId(evaluation.getSkuId());
        return tcEvaluationMapper.statisticsRateBySeller(query);
    }

    @Override
    public List<TcEvaluationDO> queryRatePicList(Evaluation tcEvaluationDO) {
        EvaluationQueryWrapper query = new EvaluationQueryWrapper();
        query.setSellerId(tcEvaluationDO.getSellerId());
        query.setItemId(tcEvaluationDO.getItemId());
        query.setSkuId(tcEvaluationDO.getSkuId());
        return tcEvaluationMapper.queryRatePicList(query);
    }


    @Override
    public List<TcEvaluationDO> getEvaluationList(Evaluation tcEvaluationDO) {
        EvaluationQueryWrapper query = new EvaluationQueryWrapper();
        query.setPrimaryOrderId(tcEvaluationDO.getPrimaryOrderId());
        query.setOrderId(tcEvaluationDO.getOrderId());
        return tcEvaluationMapper.getEvaluationList(query);
    }

    @Override
    public List<TcEvaluationDO> queryByPrimaryOrderId(Long primaryOrderId) {
        LambdaQueryWrapper<TcEvaluationDO> q = Wrappers.lambdaQuery();
        return tcEvaluationMapper.selectList(q
                .eq(TcEvaluationDO::getPrimaryOrderId, primaryOrderId)
                .orderByAsc(TcEvaluationDO::getGmtCreate));
    }

    @Override
    public TcEvaluationDO queryById(Long id, Long primaryOrderId) {
        LambdaQueryWrapper<TcEvaluationDO> q = Wrappers.lambdaQuery();
        return tcEvaluationMapper.selectOne(q
                .eq(TcEvaluationDO::getId, id)
                .eq(TcEvaluationDO::getPrimaryOrderId, primaryOrderId));
    }

    @Override
    public List<TcEvaluationDO> getEvaluationWithReplies(Long primaryOrderId) {
        LambdaQueryWrapper<TcEvaluationDO> queryWrapper = Wrappers.lambdaQuery();

        // 查询评论及其追评
        queryWrapper.eq(TcEvaluationDO::getPrimaryOrderId, primaryOrderId)   // 假设按照订单ID查询
                .and(i -> i.eq(TcEvaluationDO::getReplyId, 0)  // 评论本身
                        .or().isNotNull(TcEvaluationDO::getReplyId)); // 追评

        return tcEvaluationMapper.selectList(queryWrapper);
    }

    @Override
    public TcEvaluationDO create(TcEvaluationDO tcEvaluationDO) {
        Date now = new Date();
        tcEvaluationDO.setGmtCreate(now);
        tcEvaluationDO.setGmtModified(now);
        tcEvaluationMapper.insert(tcEvaluationDO);
        Long id = tcEvaluationDO.getId();

        if (id != null) {
            return tcEvaluationMapper.selectById(id);
        }

        return null;
    }

    @Override
    public boolean update(TcEvaluationDO tcEvaluationDO) {
        Date oldModified = tcEvaluationDO.getGmtModified();
        tcEvaluationDO.setGmtModified(new Date());

        LambdaUpdateWrapper<TcEvaluationDO> up = Wrappers.lambdaUpdate();
        int cnt = tcEvaluationMapper.update(tcEvaluationDO, up
                .eq(TcEvaluationDO::getPrimaryOrderId, tcEvaluationDO.getPrimaryOrderId())
                .eq(TcEvaluationDO::getId, tcEvaluationDO.getId())
                .eq(TcEvaluationDO::getGmtModified, oldModified));
        return cnt > 0;
    }

    @Override
    public int delete(Long id) {
        return tcEvaluationMapper.deleteById(id);
    }

}
