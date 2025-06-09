package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcReversalDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.ReversalDbQuery;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcReversalRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcReversalMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TcReversalRepositoryImpl implements TcReversalRepository {
    private static final long ROUTER_MASK = 10000;

    @Autowired
    private TcReversalMapper tcReversalMapper;

    @Override
    public List<TcReversalDO> queryByPrimaryOrderId(Long primaryOrderId) {
        LambdaQueryWrapper<TcReversalDO> tcReversalDOLambdaQueryWrapper = Wrappers.lambdaQuery();
        return tcReversalMapper.selectList(
            tcReversalDOLambdaQueryWrapper
            .eq(TcReversalDO::getRouterId, getRouterId(primaryOrderId))
            .eq(TcReversalDO::getPrimaryOrderId, primaryOrderId)
        );
    }

    @Override
    public List<TcReversalDO> queryByPrimaryReversalId(Long primaryReversalId) {
        LambdaQueryWrapper<TcReversalDO> q = Wrappers.lambdaQuery();
        return tcReversalMapper.selectList(q
                .eq(TcReversalDO::getRouterId, getRouterId(primaryReversalId))
                .eq(TcReversalDO::getPrimaryReversalId, primaryReversalId));
    }

    @Override
    public List<TcReversalDO> batchQueryByPrimaryReversalId(List<Long> primaryReversalIds) {
        if (primaryReversalIds.isEmpty()) {
            return new ArrayList<>();
        }
        Set<Integer> routerIds = primaryReversalIds.stream()
                .map(id -> getRouterId(id))
                .collect(Collectors.toSet());
        LambdaQueryWrapper<TcReversalDO> q = Wrappers.lambdaQuery();
        return tcReversalMapper.selectList(q
                .in(TcReversalDO::getRouterId, routerIds)
                .in(TcReversalDO::getPrimaryReversalId, primaryReversalIds));
    }

    @Override
    public TcReversalDO queryByReversalId(Long reversalId) {
        LambdaQueryWrapper<TcReversalDO> q = Wrappers.lambdaQuery();
        return tcReversalMapper.selectOne(q
                .eq(TcReversalDO::getRouterId, getRouterId(reversalId))
                .eq(TcReversalDO::getReversalId, reversalId));
    }

    @Override
    public List<TcReversalDO> queryMainReversalsByPrimaryOrderId(Long primaryOrderId) {
        LambdaQueryWrapper<TcReversalDO> q = Wrappers.lambdaQuery();
        return tcReversalMapper.selectList(q
                .eq(TcReversalDO::getRouterId, getRouterId(primaryOrderId))
                .eq(TcReversalDO::getPrimaryOrderId, primaryOrderId)
                .eq(TcReversalDO::getIsReversalMain, 1));
    }

    @Override
    public void insert(TcReversalDO reversal) {
        Date now = new Date();
        reversal.setGmtModified(now);
        reversal.setGmtCreate(now);
        reversal.setRouterId(getRouterId(reversal.getPrimaryReversalId()));
        reversal.setVersion(1L);
        tcReversalMapper.insert(reversal);
    }

    @Override
    public boolean updateByPrimaryReversalId(TcReversalDO up, int fromStatus) {
        if (up.getPrimaryReversalId() == null) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR);
        }
        LambdaQueryWrapper<TcReversalDO> q = Wrappers.lambdaQuery();
        up.setGmtModified(new Date());
        int count = tcReversalMapper.update(up, q
                .eq(TcReversalDO::getRouterId, getRouterId(up.getPrimaryReversalId()))
                .eq(TcReversalDO::getPrimaryReversalId, up.getPrimaryReversalId())
                .eq(TcReversalDO::getReversalStatus, fromStatus));
        return count > 0;
    }

    @Override
    public Page<TcReversalDO> queryPrimaryByCondition(ReversalDbQuery query) {
        LambdaQueryWrapper<TcReversalDO> q = Wrappers.lambdaQuery();
        Integer routerId = null;
        if (query.getCustId() != null) {
            q.eq(TcReversalDO::getCustId, query.getCustId());
            routerId = getRouterId(query.getCustId());
        }
        if (query.getPrimaryOrderId() != null) {
            q.eq(TcReversalDO::getPrimaryOrderId, query.getPrimaryOrderId());
            routerId = getRouterId(query.getPrimaryOrderId());
        }
        if (query.getPrimaryReversalId() != null) {
            q.eq(TcReversalDO::getPrimaryReversalId, query.getPrimaryReversalId());
            routerId = getRouterId(query.getPrimaryReversalId());
        }
      /*  if (routerId == null) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG, I18NMessageUtils.getMessage("no.query.param"));  //# "没有查询参数"
        } else {
            q.eq(TcReversalDO::getRouterId, routerId);
        }*/
        if (CollectionUtils.isNotEmpty(query.getReversalStatus())) {
            q.in(TcReversalDO::getReversalStatus, query.getReversalStatus());
        }
        if (query.getSellerId() != null && query.getSellerId().longValue() > 0) {
            q.eq(TcReversalDO::getSellerId, query.getSellerId());
        }
        //姓氏
        if (StringUtils.isNotEmpty(query.getCustomerLastName())) {
            q.eq(TcReversalDO::getLastName, query.getCustomerLastName());
        }
        //名字
        if (StringUtils.isNotEmpty(query.getCustomerFirstName())) {
            q.eq(TcReversalDO::getFirstName, query.getCustomerFirstName());
        }
        //Bin/Iin
        if (StringUtils.isNotEmpty(query.getSellerBin())) {
            q.eq(TcReversalDO::getBinOrIin, query.getSellerBin());
        }
        //商家名称
        if (StringUtils.isNotEmpty(query.getSellerName())) {
            q.eq(TcReversalDO::getSellerName, query.getSellerName());
        }
        //售后单创建时间范围
        if (query.getCreateTime() != null) {
            q.between(TcReversalDO::getGmtCreate, query.getCreateTime().getStartTime(), query.getCreateTime().getEndTime());
        }
        //多选  状态
        if (query.getCreateTime() != null) {
            q.between(TcReversalDO::getGmtCreate, query.getCreateTime().getStartTime(), query.getCreateTime().getEndTime());
        }
        q.eq(TcReversalDO::getIsReversalMain, true);
        q.orderByDesc(TcReversalDO::getGmtCreate);

        Page<TcReversalDO> page = new Page<>(query.getPageNum(), query.getPageSize());
        return tcReversalMapper.selectPage(page, q);
    }

    private static int getRouterId(Long bizId) {
        return (int) (bizId.longValue() % ROUTER_MASK);
    }

    /**
     * 根据 reversalId + version 通用更新, 更新后version+1
     */
    @Override
    public boolean updateByReversalIdVersion(TcReversalDO update) {
        Long oldVersion = update.getVersion();
        Long newVersion = oldVersion == null ? 1 : oldVersion + 1;
        update.setVersion(newVersion);
        update.setGmtModified(new Date());
        LambdaUpdateWrapper<TcReversalDO> tcReversalDOLambdaUpdateWrapper = Wrappers.lambdaUpdate();
        tcReversalDOLambdaUpdateWrapper
        .eq(TcReversalDO::getReversalId, update.getReversalId())
        .eq(TcReversalDO::getRouterId, getRouterId(update.getReversalId()));
        if (oldVersion != null) {
            tcReversalDOLambdaUpdateWrapper.eq(TcReversalDO::getVersion, oldVersion);
        }
        int c = tcReversalMapper.update(update, tcReversalDOLambdaUpdateWrapper);
        return c > 0;
    }
}
