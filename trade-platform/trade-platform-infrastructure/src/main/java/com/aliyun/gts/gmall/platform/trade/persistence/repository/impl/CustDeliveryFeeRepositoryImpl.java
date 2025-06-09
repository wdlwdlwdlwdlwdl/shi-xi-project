package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCustDeliveryFeeDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CustDeliveryFee;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CustDeliveryFeeRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcCustDeliveryFeeMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class CustDeliveryFeeRepositoryImpl implements CustDeliveryFeeRepository {

    @Autowired
    private TcCustDeliveryFeeMapper tcCustDeliveryFeeMapper;

    @Override
    public TcCustDeliveryFeeDO queryTcCustDeliveryFee(TcCustDeliveryFeeDO tcDeliveryFeeDO) {
        LambdaQueryWrapper<TcCustDeliveryFeeDO> queryWrapper = Wrappers.lambdaQuery();
        if(Objects.nonNull(tcDeliveryFeeDO.getId())) {
            queryWrapper.eq(TcCustDeliveryFeeDO::getId, tcDeliveryFeeDO.getId());
        }
        if(Objects.nonNull(tcDeliveryFeeDO.getDeliveryRoute())) {
            queryWrapper.eq(TcCustDeliveryFeeDO::getDeliveryRoute, tcDeliveryFeeDO.getDeliveryRoute());
        }
        if(Objects.nonNull(tcDeliveryFeeDO.getDeliveryType())) {
            queryWrapper.eq(TcCustDeliveryFeeDO::getDeliveryType, tcDeliveryFeeDO.getDeliveryType());
        }
        if(Objects.nonNull(tcDeliveryFeeDO.getActive())) {
            queryWrapper.eq(TcCustDeliveryFeeDO::getActive, tcDeliveryFeeDO.getActive());
        }
        List<TcCustDeliveryFeeDO> list = tcCustDeliveryFeeMapper.selectList(queryWrapper);
        if(list != null && !list.isEmpty()){
            return list.get(0);
        }
        return null;
    }

    @Override
    public PageInfo<TcCustDeliveryFeeDO> queryCustDeliveryFeePage(CustDeliveryFee req) {
        LambdaQueryWrapper<TcCustDeliveryFeeDO> wrapper = buildWrapper(req);
        Page<TcCustDeliveryFeeDO> page = new Page<>(req.getPage().getPageNo(), req.getPage().getPageSize());
        Page<TcCustDeliveryFeeDO> res = tcCustDeliveryFeeMapper.selectPage(page, wrapper);
        log.info("返回信息为：{}",res.toString());
        return new PageInfo<>(res.getTotal(), res.getRecords());
    }

    @Override
    public  List<TcCustDeliveryFeeDO> queryCustDeliveryFeeList(CustDeliveryFee req) {
        LambdaQueryWrapper<TcCustDeliveryFeeDO> wrapper = buildWrapper(req);
        List<TcCustDeliveryFeeDO> res = tcCustDeliveryFeeMapper.selectList(wrapper);
        log.info("返回信息为：{}",res.toString());
        return res;
    }

    private LambdaQueryWrapper<TcCustDeliveryFeeDO> buildWrapper(CustDeliveryFee query) {
        LambdaQueryWrapper<TcCustDeliveryFeeDO> wrapper = Wrappers.lambdaQuery();
        if(Objects.nonNull(query.getDeliveryRoute())) {
            wrapper.eq(TcCustDeliveryFeeDO::getDeliveryRoute, query.getDeliveryRoute());
        }
        if(Objects.nonNull(query.getDeliveryType())) {
            wrapper.eq(TcCustDeliveryFeeDO::getDeliveryType, query.getDeliveryType());
        }
        if(Objects.nonNull(query.getActive())) {
            wrapper.eq(TcCustDeliveryFeeDO::getActive, query.getActive());
        }
        wrapper.eq(TcCustDeliveryFeeDO::getDeleted,0);
        wrapper.orderByDesc(TcCustDeliveryFeeDO::getGmtCreate);
        return wrapper;
    }

    @Override
    public TcCustDeliveryFeeDO saveCustDeliveryFee(TcCustDeliveryFeeDO tcDeliveryFeeDO) {
        tcCustDeliveryFeeMapper.insert(tcDeliveryFeeDO);
        Long id = tcDeliveryFeeDO.getId();
        if (id != null) {
            return tcCustDeliveryFeeMapper.selectById(id);
        }
        throw new GmallException(CommonResponseCode.DbFailure);
    }

    @Override
    public TcCustDeliveryFeeDO updateCustDeliveryFee(TcCustDeliveryFeeDO deliveryFee) {
        if (deliveryFee.getId() == null) {
            return null;
        }
        int count = tcCustDeliveryFeeMapper.updateById(deliveryFee);
        if (count > 0) {
            return tcCustDeliveryFeeMapper.selectById(deliveryFee.getId());
        }
        return null;
    }
}
