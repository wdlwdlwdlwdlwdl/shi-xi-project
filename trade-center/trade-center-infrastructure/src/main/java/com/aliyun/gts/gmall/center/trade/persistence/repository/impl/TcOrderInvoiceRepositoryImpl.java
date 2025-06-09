package com.aliyun.gts.gmall.center.trade.persistence.repository.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.center.trade.api.dto.enums.InvoiceStatusEnum;
import com.aliyun.gts.gmall.center.trade.api.dto.enums.InvoiceTypeEnum;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.TcOrderInvoiceDO;
import com.aliyun.gts.gmall.center.trade.domain.entity.invoice.InvoiceQueryParam;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.TcOrderInvoiceRepository;
import com.aliyun.gts.gmall.center.trade.persistence.mapper.TcOrderInvoiceMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

@Repository
@Slf4j
public class TcOrderInvoiceRepositoryImpl implements TcOrderInvoiceRepository {

    @Autowired
    private TcOrderInvoiceMapper tcOrderInvoiceMapper;

    @Override
    public void create(TcOrderInvoiceDO tcOrderInvoiceDO) {
        tcOrderInvoiceDO.setGmtCreate(new Date());
        tcOrderInvoiceDO.setGmtModified(new Date());
        tcOrderInvoiceDO.setDeleted(0);
        tcOrderInvoiceMapper.insert(tcOrderInvoiceDO);
        log.info("finance flow record created:{}", JSON.toJSONString(tcOrderInvoiceDO));
    }


    @Override
    public TcOrderInvoiceDO getById(Long id) {
        return tcOrderInvoiceMapper.selectById(id);
    }

    @Override
    public TcOrderInvoiceDO queryByRequestNo(String requestNo) {
        LambdaQueryWrapper<TcOrderInvoiceDO> q = Wrappers.lambdaQuery();
        return tcOrderInvoiceMapper.selectOne(q
                .eq(TcOrderInvoiceDO::getRequestNo, requestNo));
    }

    @Override
    public TcOrderInvoiceDO queryByInvoiceId(Long invoiceId) {
        LambdaQueryWrapper<TcOrderInvoiceDO> queryWrapper = Wrappers.lambdaQuery();
        return tcOrderInvoiceMapper.selectOne(queryWrapper.eq(TcOrderInvoiceDO::getInvoiceId, invoiceId));
    }

    @Override
    public void insertOrUpdate(TcOrderInvoiceDO tcOrderInvoiceDO) {
        //线下专票 创建 发票申请单
        tcOrderInvoiceDO.setInvoiceType(InvoiceTypeEnum.SPECIAL.getCode());
        if (tcOrderInvoiceDO.getId() != null) {
            tcOrderInvoiceDO.setGmtModified(new Date());
            tcOrderInvoiceMapper.updateById(tcOrderInvoiceDO);
            log.info("TcOrderInvoiceDO updated:{}", JSON.toJSONString(tcOrderInvoiceDO));
        } else {
            tcOrderInvoiceDO.setGmtCreate(new Date());
            tcOrderInvoiceDO.setGmtModified(new Date());
            tcOrderInvoiceDO.setDeleted(0);
            tcOrderInvoiceMapper.insert(tcOrderInvoiceDO);
            log.info("TcOrderInvoiceDO created:{}", JSON.toJSONString(tcOrderInvoiceDO));
        }
    }

    @Override
    public TcOrderInvoiceDO queryLatestInvoice(Long primaryOrderId) {
        LambdaQueryWrapper<TcOrderInvoiceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TcOrderInvoiceDO::getPrimaryOrderId, primaryOrderId);
        queryWrapper.orderByDesc(TcOrderInvoiceDO::getGmtCreate);
        queryWrapper.last("limit 1 ");
        return tcOrderInvoiceMapper.selectOne(queryWrapper);
    }


    @Override
    public TcOrderInvoiceDO getCurrentOrderInvoice(Long primaryOrderId) {
        LambdaQueryWrapper<TcOrderInvoiceDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TcOrderInvoiceDO::getPrimaryOrderId, primaryOrderId);
        queryWrapper.in(TcOrderInvoiceDO::getStatus, Arrays.asList(InvoiceStatusEnum.APPLYING.getCode(),
                InvoiceStatusEnum.APPLY_SUCCESS.getCode(),InvoiceStatusEnum.PART_APPLY_SUCCESS.getCode()
        ));
        queryWrapper.last("limit 1 ");
        return tcOrderInvoiceMapper.selectOne(queryWrapper);
    }

    @Override
    public boolean updateById(TcOrderInvoiceDO tcOrderInvoiceDO) {
        tcOrderInvoiceDO.setGmtModified(new Date());
        int c = tcOrderInvoiceMapper.updateById(tcOrderInvoiceDO);
        return c > 0;
    }

    @Override
    public Page<TcOrderInvoiceDO> selectByCondition(InvoiceQueryParam param) {
        Page<TcOrderInvoiceDO> page = new Page<>(param.getPageNum(), param.getPageSize(),param.getIsSearchCount());
        LambdaQueryWrapper<TcOrderInvoiceDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Objects.nonNull(param.getPrimaryOrderId()), TcOrderInvoiceDO::getPrimaryOrderId, param.getPrimaryOrderId());
        wrapper.eq(Objects.nonNull(param.getSaleType()), TcOrderInvoiceDO::getSaleType, param.getSaleType());
        wrapper.eq(StringUtils.isNotBlank(param.getInvoiceCode()), TcOrderInvoiceDO::getInvoiceCode, param.getInvoiceCode());
        wrapper.eq(StringUtils.isNotBlank(param.getInvoiceNo()), TcOrderInvoiceDO::getInvoiceNo, param.getInvoiceNo());
        wrapper.eq(Objects.nonNull(param.getStatus()), TcOrderInvoiceDO::getStatus, param.getStatus());
        wrapper.eq(Objects.nonNull(param.getInvoiceType()), TcOrderInvoiceDO::getInvoiceType, param.getInvoiceType());
        wrapper.eq(Objects.nonNull(param.getSellerId()), TcOrderInvoiceDO :: getSellerId, param.getSellerId());
        wrapper.between(Objects.nonNull(param.getStartTime())&&Objects.nonNull(param.getEndTime()),
                TcOrderInvoiceDO::getInvoiceTime, param.getStartTime(),param.getEndTime());
        wrapper.orderByDesc(TcOrderInvoiceDO::getGmtCreate);
        return tcOrderInvoiceMapper.selectPage(page, wrapper);
    }


    @Override
    public Integer deleteById(Long id) {
        return tcOrderInvoiceMapper.deleteById(id);
    }

}
