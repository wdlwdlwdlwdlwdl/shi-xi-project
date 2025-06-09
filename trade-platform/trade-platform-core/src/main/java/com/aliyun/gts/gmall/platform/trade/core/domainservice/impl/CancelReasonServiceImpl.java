package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonQueryNoPageRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonDTO;
import com.aliyun.gts.gmall.platform.trade.core.convertor.CancelReasonConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CancelReasonService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CancelReasonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CancelReasonServiceImpl implements CancelReasonService {

    @Autowired
    private CancelReasonRepository cancelReasonRepository;

    @Autowired
    CancelReasonConverter cancelReasonConverter;

    @Override
    public PageInfo<CancelReasonDTO> queryCancelReason(CancelReasonQueryRpcReq req) {
        PageInfo<TcCancelReasonDO> list = cancelReasonRepository.queryCancelReasonList(cancelReasonConverter.toTcCancelReason(req));
        return  cancelReasonConverter.toCancelReasonDTOPage(list);
    }

    @Override
    public List<CancelReasonDTO> queryCancelReasonAll(CancelReasonQueryNoPageRpcReq req) {
        List<TcCancelReasonDO> list = cancelReasonRepository.queryCancelReasonAll(cancelReasonConverter.toTcCancelReason(req));
        return cancelReasonConverter.toCancelReasonDTOList(list);
    }

    @Override
    public CancelReasonDTO saveCancelReason(CancelReasonRpcReq req) {
        TcCancelReasonDO cancelReasonDO = cancelReasonConverter.toTcCancelReasonDO(req);
        return cancelReasonConverter.toCancelReasonDTO(cancelReasonRepository.saveCancelReason(cancelReasonDO));
    }

    @Override
    public CancelReasonDTO updateCancelReason(CancelReasonRpcReq req) {
        TcCancelReasonDO cancelReasonDO = cancelReasonConverter.toTcCancelReasonDO(req);
        return cancelReasonConverter.toCancelReasonDTO(cancelReasonRepository.updateCancelReason(cancelReasonDO));
    }

    @Override
    public CancelReasonDTO cancelReasonDetail(CancelReasonRpcReq req) {
        TcCancelReasonDO tcCancelReasonDO = cancelReasonRepository.queryTcCancelReason(req.getId());
        return cancelReasonConverter.toCancelReasonDTO(tcCancelReasonDO);
    }

    @Override
    public CancelReasonDTO cancelReasonDetailByCode(CancelReasonRpcReq req) {
        TcCancelReasonDO tcCancelReasonDO = cancelReasonRepository.queryTcCancelReasonByCode(req.getCancelReasonCode());
        return cancelReasonConverter.toCancelReasonDTO(tcCancelReasonDO);
    }
}
