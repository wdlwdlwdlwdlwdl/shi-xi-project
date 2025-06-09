package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonFeeQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonFeeRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonFeeDTO;
import com.aliyun.gts.gmall.platform.trade.core.convertor.CancelReasonFeeConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CancelReasonFeeService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonFeeDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CancelReasonFeeRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CancelReasonRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CancelReasonFeeServiceImpl implements CancelReasonFeeService {

    @Autowired
    private CancelReasonRepository cancelReasonRepository;

    @Autowired
    private CancelReasonFeeRepository cancelReasonFeeRepository;

    @Autowired
    CancelReasonFeeConverter cancelReasonFeeConverter;

    @Override
    public PageInfo<CancelReasonFeeDTO> queryCancelReasonFee(CancelReasonFeeQueryRpcReq req) {
        PageInfo<TcCancelReasonFeeDO> list = cancelReasonFeeRepository.queryCancelReasonFeeList(cancelReasonFeeConverter.toTcCancelReason(req));
        // 转换下 取消原因名称 做多语言解析
        if (Objects.nonNull(list) && CollectionUtils.isNotEmpty(list.getList())) {
            List<String> cancelList = list.getList()
                .stream()
                .filter(Objects::nonNull)
                .map(tcCancelReasonFeeDO -> tcCancelReasonFeeDO.getCancelReasonCode())
                .collect(Collectors.toList());
            //查询所有的取消
            List<TcCancelReasonDO> tcCancelReasonDOS = cancelReasonRepository.queryTcCancelReasonByCodeList(cancelList);
            if (CollectionUtils.isNotEmpty(tcCancelReasonDOS)) {
                for (TcCancelReasonFeeDO cancelReasonFee : list.getList()) {
                    // 取消
                    TcCancelReasonDO cancelReasonDO = tcCancelReasonDOS.stream()
                        .filter(Objects::nonNull)
                        .filter(tcCancelReasonDO -> cancelReasonFee.getCancelReasonCode().equals(tcCancelReasonDO.getCancelReasonCode()))
                        .findFirst()
                        .orElse(null);
                    if (Objects.isNull(cancelReasonDO)) {
                        throw new GmallException(CommonResponseCode.ServerError);
                    }
                    cancelReasonFee.setCancelReasonName(cancelReasonDO.getCancelReasonName());
                }
            }
        }
        return  cancelReasonFeeConverter.toCancelReasonDTOPage(list);
    }

    @Override
    public CancelReasonFeeDTO saveCancelReasonFee(CancelReasonFeeRpcReq req) {
        TcCancelReasonFeeDO reasonDO = cancelReasonFeeConverter.toTcCancelReasonDO(req);
        TcCancelReasonFeeDO tcCancelReasonFeeDO = cancelReasonFeeRepository.saveCancelReasonFee(reasonDO);
        // 转换下 取消原因名称 做多语言解析
        TcCancelReasonDO tcCancelReasonDOS = cancelReasonRepository.queryTcCancelReasonByCode(req.getCancelReasonCode());
        if (Objects.isNull(tcCancelReasonDOS)) {
            throw new GmallException(CommonResponseCode.ServerError);
        }
        tcCancelReasonFeeDO.setCancelReasonName(tcCancelReasonDOS.getCancelReasonName());
        return cancelReasonFeeConverter.toCancelReasonDTO(tcCancelReasonFeeDO);
    }

    @Override
    public boolean exist(CancelReasonFeeRpcReq req) {
        return cancelReasonFeeRepository.exist(req.getCancelReasonCode());
    }

    @Override
    public CancelReasonFeeDTO updateCancelReasonFee(CancelReasonFeeRpcReq req) {
        TcCancelReasonFeeDO reasonDO = cancelReasonFeeConverter.toTcCancelReasonDO(req);
        // check 一下 修改的取消CODE 已经存在配置了 不能重复配置
        if (StringUtils.isNotEmpty(req.getCancelReasonCode())) {
            TcCancelReasonFeeDO check = new TcCancelReasonFeeDO();
            check.setCancelReasonCode(req.getCancelReasonCode());
            TcCancelReasonFeeDO checkReasonFeeDO = cancelReasonFeeRepository.queryTcCancelReasonFee(check);
            if (Objects.nonNull(checkReasonFeeDO) && !req.getId().equals(checkReasonFeeDO.getId())) {
                throw new GmallException(CommonResponseCode.AlreadyExists);
            }
        }
        TcCancelReasonFeeDO tcCancelReasonFeeDO = cancelReasonFeeRepository.updateCancelReasonFee(reasonDO);
        // 转换下 取消原因名称 做多语言解析
        TcCancelReasonDO tcCancelReasonDOS = cancelReasonRepository.queryTcCancelReasonByCode(req.getCancelReasonCode());
        if (Objects.isNull(tcCancelReasonDOS)) {
            throw new GmallException(CommonResponseCode.ServerError);
        }
        tcCancelReasonFeeDO.setCancelReasonName(tcCancelReasonDOS.getCancelReasonName());
        return cancelReasonFeeConverter.toCancelReasonDTO(tcCancelReasonFeeDO);
    }

    @Override
    public CancelReasonFeeDTO cancelReasonFeeDetail(CancelReasonFeeRpcReq req) {
        TcCancelReasonFeeDO tcCancelReasonFeeDO = new TcCancelReasonFeeDO();
        tcCancelReasonFeeDO.setId(req.getId());
        tcCancelReasonFeeDO.setCancelReasonCode(req.getCancelReasonCode());
        TcCancelReasonFeeDO reasonDO = cancelReasonFeeRepository.queryTcCancelReasonFee(tcCancelReasonFeeDO);
        // 转换下 取消原因名称 做多语言解析
        TcCancelReasonDO tcCancelReasonDOS = cancelReasonRepository.queryTcCancelReasonByCode(req.getCancelReasonCode());
        if (Objects.isNull(tcCancelReasonDOS)) {
            throw new GmallException(CommonResponseCode.ServerError);
        }
        reasonDO.setCancelReasonName(tcCancelReasonDOS.getCancelReasonName());
        return cancelReasonFeeConverter.toCancelReasonDTO(reasonDO);
    }

}
