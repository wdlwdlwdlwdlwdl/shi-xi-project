package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.core.convertor.CancelReasonHistoryConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CancelReasonHistoryService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonHistoryDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CancelReasonHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CancelReasonHistoryServiceImpl implements CancelReasonHistoryService {

    @Autowired
    private CancelReasonHistoryRepository cancelReasonHistoryRepository;

    @Autowired
    CancelReasonHistoryConverter cancelReasonHistoryConverter;

    @Override
    public List<CancelReasonHistoryDTO> queryCancelReasonHistory(CancelReasonHistoryRpcReq req) {

        List<TcCancelReasonHistoryDO> list = cancelReasonHistoryRepository.queryCancelReasonList(req.getCancelReasonCode());
        return cancelReasonHistoryConverter.toCancelReasonHistoryListDTO(list);
    }

    @Override
    public CancelReasonHistoryDTO saveCancelReasonHistory(CancelReasonHistoryRpcReq req) {
        TcCancelReasonHistoryDO historyDO = cancelReasonHistoryConverter.toCancelReasonHistoryDO(req);
        return cancelReasonHistoryConverter.toCancelReasonHistoryDTO(cancelReasonHistoryRepository.saveCancelReasonHistory(historyDO));
    }
}
