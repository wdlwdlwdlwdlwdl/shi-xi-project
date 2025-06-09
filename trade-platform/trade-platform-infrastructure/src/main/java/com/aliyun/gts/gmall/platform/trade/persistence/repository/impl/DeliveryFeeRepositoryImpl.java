package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonConstant;
import com.aliyun.gts.gmall.platform.trade.common.constants.MerchantDeliveryFeeEnum;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcDeliveryFeeDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.DeliveryFee;
import com.aliyun.gts.gmall.platform.trade.domain.repository.DeliveryFeeRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcDeliveryFeeMapper;
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
public class DeliveryFeeRepositoryImpl implements DeliveryFeeRepository {

    @Autowired
    private TcDeliveryFeeMapper tcDeliveryFeeMapper;

    @Override
    public TcDeliveryFeeDO queryTcDeliveryFee(long id) {
        return tcDeliveryFeeMapper.selectById(id);
    }

    @Override
    public PageInfo<TcDeliveryFeeDO> queryDeliveryFeeList(DeliveryFee req) {
        LambdaQueryWrapper<TcDeliveryFeeDO> wrapper = buildWrapper(req);
        Page<TcDeliveryFeeDO> page = new Page<>(req.getPage().getPageNo(), req.getPage().getPageSize());
        Page<TcDeliveryFeeDO> res = tcDeliveryFeeMapper.selectPage(page, wrapper);
        log.info("返回信息为：{}",res.toString());
        return new PageInfo<>(res.getTotal(), res.getRecords());
    }

    /**
     * 查询条件
     * @param query
     * @return
     */
    private LambdaQueryWrapper<TcDeliveryFeeDO> buildWrapper(DeliveryFee query) {
        LambdaQueryWrapper<TcDeliveryFeeDO> wrapper = Wrappers.lambdaQuery();
        if(Objects.nonNull(query.getDeliveryRoute())) {
            wrapper.eq(TcDeliveryFeeDO::getDeliveryRoute, query.getDeliveryRoute());
        }
        if(Objects.nonNull(query.getDeliveryType())) {
            wrapper.eq(TcDeliveryFeeDO::getDeliveryType, query.getDeliveryType());
        }
        if(Objects.nonNull(query.getMerchantCode())) {
            wrapper.eq(TcDeliveryFeeDO::getMerchantCode, query.getMerchantCode());
        }
        if(Objects.nonNull(query.getMerchantName())) {
            wrapper.like(TcDeliveryFeeDO::getMerchantName, query.getMerchantName());
        }
        if(Objects.nonNull(query.getCategoryName())) {
            wrapper.like(TcDeliveryFeeDO::getCategoryName, query.getCategoryName());
        }
        if(Objects.nonNull(query.getCategoryId())) {
            wrapper.eq(TcDeliveryFeeDO::getCategoryId, query.getCategoryId());
        }
        if(Objects.nonNull(query.getActive())) {
            wrapper.eq(TcDeliveryFeeDO::getActive, query.getActive());
        }
        if(Objects.nonNull(query.getIsMerchantAll())) {
            wrapper.eq(TcDeliveryFeeDO::getIsMerchantAll, query.getIsMerchantAll());
        }
        if(Objects.nonNull(query.getIsCategoryAll())) {
            wrapper.eq(TcDeliveryFeeDO::getIsCategoryAll, query.getIsCategoryAll());
        }
        wrapper.eq(TcDeliveryFeeDO::getDeleted,0);
        wrapper.orderByDesc(TcDeliveryFeeDO::getGmtCreate);
        return wrapper;
    }

    @Override
    public TcDeliveryFeeDO saveDeliveryFee(TcDeliveryFeeDO tcDeliveryFeeDO) {
        tcDeliveryFeeMapper.insert(tcDeliveryFeeDO);
        Long id = tcDeliveryFeeDO.getId();
        if (id != null) {
            return tcDeliveryFeeMapper.selectById(id);
        }
        throw new GmallException(CommonResponseCode.DbFailure);
    }

    @Override
    public TcDeliveryFeeDO updateDeliveryFee(TcDeliveryFeeDO deliveryFee) {
        if (deliveryFee.getId() == null) {
            return null;
        }
        int count = tcDeliveryFeeMapper.updateById(deliveryFee);
        if (count > 0) {
            return tcDeliveryFeeMapper.selectById(deliveryFee.getId());
        }
        return null;
    }

    /**
     * 批量查询
     * @param tcDeliveryFeeDO
     * @return
     */
    @Override
    public List<TcDeliveryFeeDO> queryDeliveryList(TcDeliveryFeeDO tcDeliveryFeeDO) {
        List<TcDeliveryFeeDO> tcDeliveryFeeDOS = tcDeliveryFeeMapper.queryMatchDeliveryFee(tcDeliveryFeeDO);
        return tcDeliveryFeeDOS;
    }

    /**
     * 校验是否存在
     * @param tcDeliveryFeeDO
     * @return
     */
    @Override
    public List<TcDeliveryFeeDO> checkExist(TcDeliveryFeeDO tcDeliveryFeeDO) {
        LambdaQueryWrapper<TcDeliveryFeeDO> wrapper = Wrappers.lambdaQuery();
        if(Objects.nonNull(tcDeliveryFeeDO.getDeliveryRoute())) {
            wrapper.eq(TcDeliveryFeeDO::getDeliveryRoute, tcDeliveryFeeDO.getDeliveryRoute());
        }
        if(Objects.nonNull(tcDeliveryFeeDO.getDeliveryType())) {
            wrapper.eq(TcDeliveryFeeDO::getDeliveryType, tcDeliveryFeeDO.getDeliveryType());
        }
        // 全部 则是 同城+物流+全部
        if (MerchantDeliveryFeeEnum.ALL.getCode().equals(tcDeliveryFeeDO.getIsMerchantAll())) {
            wrapper.eq(TcDeliveryFeeDO::getIsMerchantAll, tcDeliveryFeeDO.getIsMerchantAll());
        } else {
            // 非全部则是同城+物流+分类+卖家
            if(Objects.nonNull(tcDeliveryFeeDO.getMerchantCode())) {
                wrapper.eq(TcDeliveryFeeDO::getMerchantCode, tcDeliveryFeeDO.getMerchantCode());
            }
        }
        // 全部生效
        if (MerchantDeliveryFeeEnum.ALL.getCode().equals(tcDeliveryFeeDO.getIsCategoryAll())) {
            wrapper.eq(TcDeliveryFeeDO::getIsCategoryAll, tcDeliveryFeeDO.getIsCategoryAll());
        } else {
            // 非全部则是同城+物流+分类+卖家
            if(Objects.nonNull(tcDeliveryFeeDO.getCategoryId())) {
                wrapper.eq(TcDeliveryFeeDO::getCategoryId, tcDeliveryFeeDO.getCategoryId());
            }
        }
        List<TcDeliveryFeeDO> tcDeliveryFeeDOList =  tcDeliveryFeeMapper.selectList(wrapper);
        return tcDeliveryFeeDOList;
    }
}
