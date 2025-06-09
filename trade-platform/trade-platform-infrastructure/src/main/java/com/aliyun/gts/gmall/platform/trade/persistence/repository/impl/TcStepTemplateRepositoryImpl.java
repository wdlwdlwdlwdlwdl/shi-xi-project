package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.platform.trade.common.domain.step.StepMeta;
import com.aliyun.gts.gmall.platform.trade.common.domain.step.StepTemplate;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcStepTemplateDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcStepTemplateRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.ErrorUtils;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcStepTemplateMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class TcStepTemplateRepositoryImpl implements TcStepTemplateRepository {
    private static final Cache<String, StepTemplate> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS).maximumSize(1000).build();
    private static final StepTemplate CACHE_NULL = new StepTemplate();

    @Autowired
    private TcStepTemplateMapper tcStepTemplateMapper;

    @Override
    public TcStepTemplateDO queryByName(String name) {
        LambdaQueryWrapper<TcStepTemplateDO> q = Wrappers.lambdaQuery();
        return tcStepTemplateMapper.selectOne(q.eq(TcStepTemplateDO::getName, name));
    }

    @Override
    public StepTemplate queryParsedTemplateWithCache(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        try {
            StepTemplate t = cache.get(name, () -> queryParsedTemplateNoCache(name));
            return t == CACHE_NULL ? null : t;
        } catch (ExecutionException e) {
            ErrorUtils.throwUndeclared(e);
            return null;
        }
    }

    protected StepTemplate queryParsedTemplateNoCache(String name) {
        TcStepTemplateDO template = queryByName(name);
        if (template == null) {
            return CACHE_NULL;
        }
        List<Map> list = (List) JSON.parseArray(template.getContent());
        List<StepMeta> target = list == null ? null :
            (List) list.stream()
            .map(o -> new StepMeta().build(o))
            .collect(Collectors.toList());
        StepTemplate st = new StepTemplate();
        st.setTemplateName(name);
        st.setStepMetas(target);
        return st;
    }

}
