package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.framework.server.util.PageUtils;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCityDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.City;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CityRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcCityMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class CityRepositoryImpl implements CityRepository {

    @Autowired
    private TcCityMapper tcCityMapper;

    @Override
    public TcCityDO queryTcCity(long id) {
        return tcCityMapper.selectById(id);
    }

    @Override
    public PageInfo<TcCityDO> queryCityList(City req) {
        log.info("queryCityList传入信息为：{}",req.toString());
        LambdaQueryWrapper<TcCityDO> wrapper = buildWrapper(req);
        Page<TcCityDO> page = new Page<>(req.getPage().getPageNo(), req.getPage().getPageSize());
        Page<TcCityDO> result = tcCityMapper.selectPage(page, wrapper);
        log.info("返回信息为：{}",result.toString());
        return PageUtils.toPage(result);
    }

    private LambdaQueryWrapper<TcCityDO> buildWrapper(City query) {
        LambdaQueryWrapper<TcCityDO> wrapper = Wrappers.lambdaQuery();
        if(StringUtils.isNotEmpty(query.getCityName())) {
            wrapper.like(TcCityDO::getCityName, query.getCityName());
        }
        if(Objects.nonNull(query.getActive())){
            wrapper.eq(TcCityDO::getActive, query.getActive());
        }
        if(CollectionUtils.isNotEmpty(query.getCodes())) {
            wrapper.in(TcCityDO::getCityCode, query.getCodes());
        }
        wrapper.eq(TcCityDO::getDeleted,0);
        wrapper.orderByDesc(TcCityDO::getGmtCreate);
        return wrapper;
    }

    @Override
    public TcCityDO saveCity(TcCityDO tcCityDO) {
        tcCityMapper.insert(tcCityDO);
        Long id = tcCityDO.getId();
        if (id != null) {
            return tcCityMapper.selectById(id);
        }
        throw new GmallException(CommonResponseCode.DbFailure);
    }

    @Override
    public TcCityDO updateCity(TcCityDO tcCityDO) {
        if (tcCityDO.getId() == null) {
            return null;
        }
        int count = tcCityMapper.updateById(tcCityDO);
        if (count > 0) {
            return tcCityMapper.selectById(tcCityDO.getId());
        }
        return null;
    }

    @Override
    public boolean exist(String cityCode) {
        LambdaQueryWrapper<TcCityDO> q = Wrappers.lambdaQuery();
        List<TcCityDO> tcCityDO = tcCityMapper.selectList(q
                .eq(TcCityDO::getCityCode , cityCode)
                .eq(TcCityDO::getDeleted,0));
        if (CollectionUtils.isNotEmpty(tcCityDO)) {
            return Objects.nonNull(tcCityDO.get(0));
        }
        return Boolean.TRUE;
    }

    @Override
    public List<TcCityDO> queryList(City req){
        LambdaQueryWrapper<TcCityDO> wrapper = buildWrapper(req);
        return tcCityMapper.selectList(wrapper);
    }

    @Override
    public TcCityDO detail(String cityCode) {
        LambdaQueryWrapper<TcCityDO> q = Wrappers.lambdaQuery();
        TcCityDO  tcCityDO = tcCityMapper.selectOne(q
                .eq(TcCityDO::getCityCode , cityCode)
                .eq(TcCityDO::getDeleted,0));
        return tcCityDO;
    }
}
