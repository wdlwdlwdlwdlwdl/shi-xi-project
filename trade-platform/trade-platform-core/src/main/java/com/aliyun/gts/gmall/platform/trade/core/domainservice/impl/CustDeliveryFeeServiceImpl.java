package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CustDeliveryFeeQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CustDeliveryFeeRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CustDeliveryFeeDTO;
import com.aliyun.gts.gmall.platform.trade.core.convertor.CustDeliveryFeeConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.CustDeliveryFeeService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCustDeliveryFeeDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CustDeliveryFeeRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcCustDeliveryFeeMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class CustDeliveryFeeServiceImpl implements CustDeliveryFeeService {

    @Autowired
    private CustDeliveryFeeRepository custDeliveryFeeRepository;

    @Autowired
    CustDeliveryFeeConverter custDeliveryFeeConverter;

    @Autowired
    private TcCustDeliveryFeeMapper tcCustDeliveryFeeMapper;

    @Override
    public PageInfo<CustDeliveryFeeDTO> queryCustDeliveryFee(CustDeliveryFeeQueryRpcReq req) {
        PageInfo<TcCustDeliveryFeeDO> list = custDeliveryFeeRepository.queryCustDeliveryFeePage(custDeliveryFeeConverter.toTcCustDeliveryFee(req));
        return  custDeliveryFeeConverter.toCustDeliveryFeeDTOPage(list);
    }

    @Override
    public  List<CustDeliveryFeeDTO> queryCustDeliveryFeeList(CustDeliveryFeeQueryRpcReq req) {
        List<TcCustDeliveryFeeDO> tcCustDeliveryFeeDOS = custDeliveryFeeRepository.queryCustDeliveryFeeList(custDeliveryFeeConverter.toTcCustDeliveryFee(req));
        return  custDeliveryFeeConverter.toCustDeliveryFeeDTOList(tcCustDeliveryFeeDOS);
    }

    @Override
    public CustDeliveryFeeDTO saveCustDeliveryFee(CustDeliveryFeeRpcReq req) {
        TcCustDeliveryFeeDO deliveryFeeDO = custDeliveryFeeConverter.toTcCustDeliveryFeeDO(req);
        return custDeliveryFeeConverter.toCustDeliveryFeeDTO(custDeliveryFeeRepository.saveCustDeliveryFee(deliveryFeeDO));
    }

    @Override
    public CustDeliveryFeeDTO updateCustDeliveryFee(CustDeliveryFeeRpcReq req) {
        TcCustDeliveryFeeDO deliveryFeeDO = custDeliveryFeeConverter.toTcCustDeliveryFeeDO(req);
        return custDeliveryFeeConverter.toCustDeliveryFeeDTO(custDeliveryFeeRepository.updateCustDeliveryFee(deliveryFeeDO));
    }

    @Override
    public CustDeliveryFeeDTO custDeliveryFeeDetail(CustDeliveryFeeRpcReq req) {
        TcCustDeliveryFeeDO deliveryFeeDO = custDeliveryFeeConverter.toTcCustDeliveryFeeDO(req);
        return custDeliveryFeeConverter.toCustDeliveryFeeDTO(custDeliveryFeeRepository.queryTcCustDeliveryFee(deliveryFeeDO));
    }

    @Override
    public boolean exist(CustDeliveryFeeRpcReq req) {
        LambdaQueryWrapper<TcCustDeliveryFeeDO> q = Wrappers.lambdaQuery();
        TcCustDeliveryFeeDO  tcCustDeliveryFeeDO = tcCustDeliveryFeeMapper.selectOne(q
                .eq(TcCustDeliveryFeeDO::getDeliveryRoute, req.getDeliveryRoute())
                .eq(TcCustDeliveryFeeDO::getDeliveryType, req.getDeliveryType()));
        return Objects.nonNull(tcCustDeliveryFeeDO);
    }

}
