package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CustDeliveryFeeHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CustDeliveryFeeHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.DeliveryFeeHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.core.convertor.CustDeliveryFeeHistoryConverter;
import com.aliyun.gts.gmall.platform.trade.core.convertor.DeliveryFeeHistoryConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CustDeliveryFeeHistoryService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.DeliveryFeeHistoryService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCustDeliveryFeeHistoryDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcDeliveryFeeHistoryDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CustDeliveryFeeHistoryRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.DeliveryFeeHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CustDeliveryFeenHistoryServiceImpl implements CustDeliveryFeeHistoryService {

    @Autowired
    private CustDeliveryFeeHistoryRepository custDeliveryFeeHistoryRepository;

    @Autowired
    CustDeliveryFeeHistoryConverter custDeliveryFeeHistoryConverter;

    @Override
    public List<CustDeliveryFeeHistoryDTO> queryCustDeliveryFeeHistory(CustDeliveryFeeHistoryRpcReq req) {

        List<TcCustDeliveryFeeHistoryDO> list = custDeliveryFeeHistoryRepository.queryCustDeliveryFeeList(req.getCustFeeCode());
        return custDeliveryFeeHistoryConverter.toDeliveryFeeHistoryListDTO(list);
    }

    @Override
    public CustDeliveryFeeHistoryDTO saveCustDeliveryFeeHistory(CustDeliveryFeeHistoryRpcReq req) {
        TcCustDeliveryFeeHistoryDO historyDO = custDeliveryFeeHistoryConverter.toCustDeliveryFeeHistoryDO(req);
        return custDeliveryFeeHistoryConverter.toCustDeliveryFeeHistoryDTO(custDeliveryFeeHistoryRepository.saveCustDeliveryFeeHistory(historyDO));
    }
}
