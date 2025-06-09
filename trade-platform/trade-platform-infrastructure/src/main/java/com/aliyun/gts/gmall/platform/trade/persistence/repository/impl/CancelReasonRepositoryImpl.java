package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CancelReason;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CancelReasonRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcCancelReasonMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public class CancelReasonRepositoryImpl implements CancelReasonRepository {

    @Autowired
    private TcCancelReasonMapper tcCancelReasonMapper;

    @Override
    public TcCancelReasonDO queryTcCancelReason(long id) {
        return tcCancelReasonMapper.selectById(id);
    }

    @Override
    public PageInfo<TcCancelReasonDO> queryCancelReasonList(CancelReason req) {
        LambdaQueryWrapper<TcCancelReasonDO> wrapper = buildWrapper(req);
        Page<TcCancelReasonDO> page = new Page<>(req.getPage().getPageNo(), req.getPage().getPageSize());
        Page<TcCancelReasonDO> res = tcCancelReasonMapper.selectPage(page, wrapper);
        log.info("返回信息为：{}",res.toString());
        return new PageInfo<>(res.getTotal(), res.getRecords());
    }

    @Override
    public List<TcCancelReasonDO> queryCancelReasonAll(CancelReason req) {
        LambdaQueryWrapper<TcCancelReasonDO> wrapper = Wrappers.lambdaQuery();
        if(StringUtils.isNotEmpty(req.getCancelReasonName())) {
            wrapper.apply(
                "JSON_SEARCH(JSON_UNQUOTE(LOWER(JSON_EXTRACT(cancel_reason_name, '$.langValues'))), 'one', LOWER(CONCAT({0}, '-%', {1}, '%'))) IS NOT NULL",
                LocaleContextHolder.getLocale().getLanguage(),
                req.getCancelReasonName()
            );
        }
        wrapper.eq(TcCancelReasonDO::getDeleted,0);
        wrapper.orderByDesc(TcCancelReasonDO::getGmtCreate);
        List<TcCancelReasonDO> tcCancelReasonDOS = tcCancelReasonMapper.selectList(wrapper);
        return tcCancelReasonDOS;
    }

    private LambdaQueryWrapper<TcCancelReasonDO> buildWrapper(CancelReason query) {
        LambdaQueryWrapper<TcCancelReasonDO> wrapper = Wrappers.lambdaQuery();
        if(StringUtils.isNotEmpty(query.getCancelReasonName())) {
            wrapper.apply(
                "JSON_SEARCH(JSON_UNQUOTE(LOWER(JSON_EXTRACT(cancel_reason_name, '$.langValues'))), 'one', LOWER(CONCAT({0}, '-%', {1}, '%'))) IS NOT NULL",
                LocaleContextHolder.getLocale().getLanguage(),
                query.getCancelReasonName()
            );
        }
        if(StringUtils.isNotEmpty(query.getCancelReasonCode())) {
            wrapper.like(TcCancelReasonDO::getCancelReasonCode, query.getCancelReasonCode());
        }
        if(Objects.nonNull(query.getChannel())) {
            wrapper.eq(TcCancelReasonDO::getChannel,query.getChannel());
        }
        wrapper.eq(TcCancelReasonDO::getDeleted,0);
        wrapper.orderByDesc(TcCancelReasonDO::getGmtCreate);
        return wrapper;
    }

    @Override
    public TcCancelReasonDO saveCancelReason(TcCancelReasonDO tcCancelReasonDO) {
        tcCancelReasonMapper.insert(tcCancelReasonDO);
        Long id = tcCancelReasonDO.getId();
        if (id != null) {
            return tcCancelReasonMapper.selectById(id);
        }
        throw new GmallException(CommonResponseCode.DbFailure);
    }

    @Override
    public TcCancelReasonDO updateCancelReason(TcCancelReasonDO tcCancelReasonDO) {
        if (tcCancelReasonDO.getId() == null) {
            return null;
        }
        int count = tcCancelReasonMapper.updateById(tcCancelReasonDO);
        if (count > 0) {
            return tcCancelReasonMapper.selectById(tcCancelReasonDO.getId());
        }
        return null;
    }

    @Override
    public TcCancelReasonDO queryTcCancelReasonByCode(String code) {
        LambdaQueryWrapper<TcCancelReasonDO> wrapper = Wrappers.lambdaQuery();
        if(StringUtils.isNotEmpty(code)) {
            wrapper.eq(TcCancelReasonDO::getCancelReasonCode, code);
        }
        return tcCancelReasonMapper.selectOne(wrapper);
    }

    /**
     * 批量查询
     * @param codes
     * @return
     */
    @Override
    public List<TcCancelReasonDO> queryTcCancelReasonByCodeList(List<String> codes) {
        LambdaQueryWrapper<TcCancelReasonDO> wrapper = Wrappers.lambdaQuery();
        if(CollectionUtils.isNotEmpty(codes)) {
            wrapper.in(TcCancelReasonDO::getCancelReasonCode, codes);
        }
        return tcCancelReasonMapper.selectList(wrapper);
    }
}
