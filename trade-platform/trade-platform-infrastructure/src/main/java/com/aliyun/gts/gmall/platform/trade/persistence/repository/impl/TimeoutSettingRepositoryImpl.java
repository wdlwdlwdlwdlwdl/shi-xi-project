package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.alibaba.nacos.api.utils.StringUtils;
import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcTimeoutSettingDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.TimeoutSetting;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.TimeoutSettingQuery;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TimeoutSettingRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcTimeoutSettingMapper;
import com.aliyun.gts.gmall.platform.trade.persistence.rpc.converter.TimeoutConverter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class TimeoutSettingRepositoryImpl implements TimeoutSettingRepository {

    @Autowired
    private TcTimeoutSettingMapper tcTimeoutSettingMapper;

    @Autowired
    TimeoutConverter timeoutConverter;

    @Override
    public PageInfo<TcTimeoutSettingDO> queryTimeoutSettingList(TimeoutSetting req) {
        log.info("queryTimeoutSettingList传入信息为：{}",req.toString());
        LambdaQueryWrapper<TcTimeoutSettingDO> wrapper = buildWrapper(req);
        Page<TcTimeoutSettingDO> page = new Page<>(req.getPage().getPageNo(), req.getPage().getPageSize());
        Page<TcTimeoutSettingDO> res = tcTimeoutSettingMapper.selectPage(page, wrapper);
        log.info("返回信息为：{}",res.toString());
        return new PageInfo<>(res.getTotal(), res.getRecords());
    }

    private LambdaQueryWrapper<TcTimeoutSettingDO> buildWrapper(TimeoutSetting query) {
        LambdaQueryWrapper<TcTimeoutSettingDO> wrapper = Wrappers.lambdaQuery();
        if(Objects.nonNull(query.getOrderStatus())) {
            wrapper.eq(TcTimeoutSettingDO::getOrderStatus, query.getOrderStatus());
        }
        if(Objects.nonNull(query.getPayType())) {
            wrapper.eq(TcTimeoutSettingDO::getPayType, query.getPayType());
        }
        if(Objects.nonNull(query.getTimeType())) {
            wrapper.eq(TcTimeoutSettingDO::getTimeType, query.getTimeType());
        }
        wrapper.orderByDesc(TcTimeoutSettingDO::getGmtCreate);
        return wrapper;
    }

    @Override
    public TcTimeoutSettingDO queryTimeoutSetting(Long id) {
        return tcTimeoutSettingMapper.selectById(id);
    }

    @Override
    public TcTimeoutSettingDO queryTimeoutSetting(TimeoutSettingQuery req) {
        LambdaQueryWrapper<TcTimeoutSettingDO> wrapper = Wrappers.lambdaQuery();
        if(Objects.nonNull(req.getOrderStatus())) {
            wrapper.eq(TcTimeoutSettingDO::getOrderStatus, req.getOrderStatus());
        }
        if(Objects.nonNull(req.getPayType())) {
            wrapper.eq(TcTimeoutSettingDO::getPayType, req.getPayType());
        }
        return tcTimeoutSettingMapper.selectOne(wrapper);
    }

    @Override
    public TcTimeoutSettingDO saveTimeoutSetting(TcTimeoutSettingDO tcTimeoutSettingDO) {
        tcTimeoutSettingMapper.insert(tcTimeoutSettingDO);
        Long id = tcTimeoutSettingDO.getId();
        if (id != null) {
            return tcTimeoutSettingMapper.selectById(id);
        }
        throw new GmallException(CommonResponseCode.DbFailure);
    }

    @Override
    public TcTimeoutSettingDO updateTimeoutSetting(TcTimeoutSettingDO tcTimeoutSettingDO) {
        if (tcTimeoutSettingDO.getId() == null) {
            return null;
        }
        int count = tcTimeoutSettingMapper.updateById(tcTimeoutSettingDO);
        if (count > 0) {
            return tcTimeoutSettingMapper.selectById(tcTimeoutSettingDO.getId());
        }
        return null;
    }

    @Override
    public boolean exist(String orderStatus,String payType) {
        LambdaQueryWrapper<TcTimeoutSettingDO> wrapper = Wrappers.lambdaQuery();
        if(!StringUtils.isBlank(payType)) {
            wrapper.eq(TcTimeoutSettingDO::getPayType , payType);
        }
        TcTimeoutSettingDO  tcTimeoutSettingDO = tcTimeoutSettingMapper.selectOne(wrapper
                .eq(TcTimeoutSettingDO::getOrderStatus , orderStatus)
                .eq(TcTimeoutSettingDO::getDeleted,0));
        return Objects.nonNull(tcTimeoutSettingDO);
    }
}
