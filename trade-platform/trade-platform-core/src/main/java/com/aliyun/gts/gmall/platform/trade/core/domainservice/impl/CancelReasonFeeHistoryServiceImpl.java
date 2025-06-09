package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonFeeHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonFeeHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.core.convertor.CancelReasonFeeHistoryConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CancelReasonFeeHistoryService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonFeeHistoryDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CancelReasonFeeHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CancelReasonFeeHistoryServiceImpl implements CancelReasonFeeHistoryService {

    @Autowired
    private CancelReasonFeeHistoryRepository cancelReasonFeeHistoryRepository;

    @Autowired
    CancelReasonFeeHistoryConverter cancelReasonFeeHistoryConverter;

    @Override
    public List<CancelReasonFeeHistoryDTO> queryCancelReasonFeeHistory(CancelReasonFeeHistoryRpcReq req) {

        List<TcCancelReasonFeeHistoryDO> list = cancelReasonFeeHistoryRepository.queryCancelReasonFeeList(req.getReasonFeeCode());
        return cancelReasonFeeHistoryConverter.toCancelReasonHistoryListDTO(list);
    }

    @Override
    public CancelReasonFeeHistoryDTO saveCancelReasonFeeHistory(CancelReasonFeeHistoryRpcReq req) {
        TcCancelReasonFeeHistoryDO historyDO = cancelReasonFeeHistoryConverter.toCancelReasonHistoryDO(req);
        return cancelReasonFeeHistoryConverter.toCancelReasonHistoryDTO(cancelReasonFeeHistoryRepository.saveCancelReasonFeeHistory(historyDO));
    }
}
