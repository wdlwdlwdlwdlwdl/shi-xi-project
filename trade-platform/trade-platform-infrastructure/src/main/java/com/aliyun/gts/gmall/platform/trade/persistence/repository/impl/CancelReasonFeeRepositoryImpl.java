package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonFeeDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CancelReasonFee;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CancelReasonFeeRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcCancelReasonFeeMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class CancelReasonFeeRepositoryImpl implements CancelReasonFeeRepository {

    @Autowired
    private TcCancelReasonFeeMapper tcCancelReasonFeeMapper;

    @Override
    public TcCancelReasonFeeDO queryTcCancelReasonFee(TcCancelReasonFeeDO req) {
        LambdaQueryWrapper<TcCancelReasonFeeDO> wrapper = Wrappers.lambdaQuery();
        if(Objects.nonNull(req.getId())) {
            wrapper.eq(TcCancelReasonFeeDO::getId, req.getId());
        }
        if(StringUtils.isNotEmpty(req.getCancelReasonCode())) {
            wrapper.eq(TcCancelReasonFeeDO::getCancelReasonCode, req.getCancelReasonCode());
        }
        List<TcCancelReasonFeeDO> list = tcCancelReasonFeeMapper.selectList(wrapper);
        //兼容垃圾数据 理论上只有一条数据
        TcCancelReasonFeeDO result = new TcCancelReasonFeeDO();
        if(!CollectionUtils.isEmpty(list) && list.size() > 1){
            result = list.get(0);
        }
        return result;
    }

    @Override
    public PageInfo<TcCancelReasonFeeDO> queryCancelReasonFeeList(CancelReasonFee req) {
        LambdaQueryWrapper<TcCancelReasonFeeDO> wrapper = buildWrapper(req);
        Page<TcCancelReasonFeeDO> page = new Page<>(req.getPage().getPageNo(), req.getPage().getPageSize());
        Page<TcCancelReasonFeeDO> res = tcCancelReasonFeeMapper.selectPage(page, wrapper);
        log.info("返回信息为：{}",res.toString());
        return new PageInfo<>(res.getTotal(), res.getRecords());
    }

    private LambdaQueryWrapper<TcCancelReasonFeeDO> buildWrapper(CancelReasonFee query) {
        LambdaQueryWrapper<TcCancelReasonFeeDO> wrapper = Wrappers.lambdaQuery();
        if(StringUtils.isNotEmpty(query.getCancelReasonName())) {
            wrapper.like(TcCancelReasonFeeDO::getCancelReasonName, query.getCancelReasonName());
        }
        wrapper.eq(TcCancelReasonFeeDO::getDeleted,0);
        wrapper.orderByDesc(TcCancelReasonFeeDO::getGmtCreate);
        return wrapper;
    }

    @Override
    public TcCancelReasonFeeDO saveCancelReasonFee(TcCancelReasonFeeDO tcCancelReasonFeeDO) {
        tcCancelReasonFeeMapper.insert(tcCancelReasonFeeDO);
        Long id = tcCancelReasonFeeDO.getId();
        if (id != null) {
            return tcCancelReasonFeeMapper.selectById(id);
        }
        throw new GmallException(CommonResponseCode.DbFailure);
    }

    @Override
    public TcCancelReasonFeeDO updateCancelReasonFee(TcCancelReasonFeeDO tcCancelReasonFeeDO) {
        if (tcCancelReasonFeeDO.getId() == null) {
            return null;
        }
        int count = tcCancelReasonFeeMapper.updateById(tcCancelReasonFeeDO);
        if (count > 0) {
            return tcCancelReasonFeeMapper.selectById(tcCancelReasonFeeDO.getId());
        }
        return null;
    }

    @Override
    public boolean exist(String cancelReasonCode) {
        LambdaQueryWrapper<TcCancelReasonFeeDO> q = Wrappers.lambdaQuery();
        List<TcCancelReasonFeeDO> tcCancelReasonFeeDO = tcCancelReasonFeeMapper.selectList(q
            .eq(TcCancelReasonFeeDO::getCancelReasonCode , cancelReasonCode)
            .eq(TcCancelReasonFeeDO::getDeleted,0)
        );
        if (CollectionUtils.isNotEmpty(tcCancelReasonFeeDO)) {
            return Objects.nonNull(tcCancelReasonFeeDO.get(0));
        }
        return Boolean.FALSE;
    }
}
