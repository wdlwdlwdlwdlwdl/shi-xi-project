package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcFeeRulesDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.FeeRules;
import com.aliyun.gts.gmall.platform.trade.domain.repository.FeeRulesRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcFeeRulesMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class FeeRulesRepositoryImpl implements FeeRulesRepository {

    @Autowired
    private TcFeeRulesMapper tcFeeRulesMapper;

    @Override
    public TcFeeRulesDO queryTcFeeRules(long id) {
        return tcFeeRulesMapper.selectById(id);
    }

    @Override
    public PageInfo<TcFeeRulesDO> queryFeeRulesList(FeeRules req) {
        LambdaQueryWrapper<TcFeeRulesDO> wrapper = buildWrapper(req);
        Page<TcFeeRulesDO> page = new Page<>(req.getPage().getPageNo(), req.getPage().getPageSize());
        Page<TcFeeRulesDO> res = tcFeeRulesMapper.selectPage(page, wrapper);
        log.info("返回信息为：{}",res.toString());
        return new PageInfo<>(res.getTotal(), res.getRecords());
    }

    private LambdaQueryWrapper<TcFeeRulesDO> buildWrapper(FeeRules query) {
        LambdaQueryWrapper<TcFeeRulesDO> wrapper = Wrappers.lambdaQuery();
        if(StringUtils.isNotEmpty(query.getFeeRulesCode())) {
            wrapper.eq(TcFeeRulesDO::getFeeRulesCode, query.getFeeRulesCode());
        }
        wrapper.eq(TcFeeRulesDO::getDeleted,0);
        wrapper.orderByDesc(TcFeeRulesDO::getGmtCreate);
        return wrapper;
    }

    @Override
    public TcFeeRulesDO saveFeeRules(TcFeeRulesDO tcFeeRulesDO) {
        tcFeeRulesMapper.insert(tcFeeRulesDO);
        Long id = tcFeeRulesDO.getId();
        if (id != null) {
            return tcFeeRulesMapper.selectById(id);
        }
        throw new GmallException(CommonResponseCode.DbFailure);
    }

    @Override
    public TcFeeRulesDO updateFeeRules(TcFeeRulesDO feeRules) {
        if (feeRules.getId() == null) {
            return null;
        }
        int count = tcFeeRulesMapper.updateById(feeRules);
        if (count > 0) {
            return tcFeeRulesMapper.selectById(feeRules.getId());
        }
        return null;
    }

    @Override
    public TcFeeRulesDO queryFeeRules() {
        LambdaQueryWrapper<TcFeeRulesDO> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(TcFeeRulesDO::getDeleted,0);
        return tcFeeRulesMapper.selectOne(wrapper);
    }
}
