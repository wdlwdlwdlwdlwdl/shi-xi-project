package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.FeeRulesHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.DeliveryFeeHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.FeeRulesHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.core.convertor.DeliveryFeeHistoryConverter;
import com.aliyun.gts.gmall.platform.trade.core.convertor.FeeRulesHistoryConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.DeliveryFeeHistoryService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.FeeRulesHistoryService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcDeliveryFeeHistoryDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcFeeRulesHistoryDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.DeliveryFeeHistoryRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.FeeRulesHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class FeeRulesHistoryServiceImpl implements FeeRulesHistoryService {

    @Autowired
    private FeeRulesHistoryRepository feeRulesHistoryRepository;

    @Autowired
    FeeRulesHistoryConverter feeRulesHistoryConverter;

    @Override
    public List<FeeRulesHistoryDTO> queryFeeRulesHistory(FeeRulesHistoryRpcReq req) {

        List<TcFeeRulesHistoryDO> list = feeRulesHistoryRepository.queryFeeRulesList(req.getFeeRulesCode());
        return feeRulesHistoryConverter.toFeeRulesHistoryListDTO(list);
    }

    @Override
    public FeeRulesHistoryDTO saveFeeRulesHistory(FeeRulesHistoryRpcReq req) {
        TcFeeRulesHistoryDO historyDO = feeRulesHistoryConverter.toFeeRulesHistoryDO(req);
        return feeRulesHistoryConverter.toFeeRulesHistoryDTO(feeRulesHistoryRepository.saveFeeRulesHistory(historyDO));
    }
}
