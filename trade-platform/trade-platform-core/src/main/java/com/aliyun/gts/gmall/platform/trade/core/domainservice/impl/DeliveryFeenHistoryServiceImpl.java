package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.DeliveryFeeHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.core.convertor.CancelReasonHistoryConverter;
import com.aliyun.gts.gmall.platform.trade.core.convertor.DeliveryFeeHistoryConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CancelReasonHistoryService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.DeliveryFeeHistoryService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonHistoryDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcDeliveryFeeHistoryDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CancelReasonHistoryRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.DeliveryFeeHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DeliveryFeenHistoryServiceImpl implements DeliveryFeeHistoryService {

    @Autowired
    private DeliveryFeeHistoryRepository deliveryFeeHistoryRepository;

    @Autowired
    DeliveryFeeHistoryConverter deliveryFeeHistoryConverter;

    @Override
    public List<DeliveryFeeHistoryDTO> queryDeliveryFeeHistory(DeliveryFeeHistoryRpcReq req) {
        List<TcDeliveryFeeHistoryDO> list = deliveryFeeHistoryRepository.queryDeliveryFeeList(req.getFeeCode());
        return deliveryFeeHistoryConverter.toDeliveryFeeHistoryListDTO(list);
    }

    @Override
    public DeliveryFeeHistoryDTO saveDeliveryFeeHistory(DeliveryFeeHistoryRpcReq req) {
        TcDeliveryFeeHistoryDO historyDO = deliveryFeeHistoryConverter.toDeliveryFeenHistoryDO(req);
        return deliveryFeeHistoryConverter.toDeliveryFeeHistoryDTO(deliveryFeeHistoryRepository.saveDeliveryFeeHistory(historyDO));
    }
}
