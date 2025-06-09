package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.FeeRulesQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.FeeRulesRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.FeeRulesDTO;
import com.aliyun.gts.gmall.platform.trade.core.convertor.FeeRulesConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.FeeRulesService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcFeeRulesDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.FeeRulesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FeeRulesServiceImpl implements FeeRulesService {

    @Autowired
    private FeeRulesRepository feeRulesRepository;

    @Autowired
    FeeRulesConverter feeRulesConverter;

    @Override
    public PageInfo<FeeRulesDTO> queryFeeRules(FeeRulesQueryRpcReq req) {
        PageInfo<TcFeeRulesDO> list = feeRulesRepository.queryFeeRulesList(feeRulesConverter.toTcFeeRules(req));
        return  feeRulesConverter.toFeeRulesDTOPage(list);
    }

    @Override
    public FeeRulesDTO saveFeeRules(FeeRulesRpcReq req) {
        TcFeeRulesDO feeRulesDO = feeRulesConverter.toTcFeeRulesDO(req);
        return feeRulesConverter.toFeeRulesDTO(feeRulesRepository.saveFeeRules(feeRulesDO));
    }

    @Override
    public FeeRulesDTO updateFeeRules(FeeRulesRpcReq req) {
        TcFeeRulesDO feeRulesDO = feeRulesConverter.toTcFeeRulesDO(req);
        return feeRulesConverter.toFeeRulesDTO(feeRulesRepository.updateFeeRules(feeRulesDO));
    }

    @Override
    public FeeRulesDTO feeRulesDetail(FeeRulesRpcReq req) {
        TcFeeRulesDO feeRulesDO = feeRulesRepository.queryTcFeeRules(req.getId());
        return feeRulesConverter.toFeeRulesDTO(feeRulesDO);
    }

    @Override
    public FeeRulesDTO queryFeeRules() {
        TcFeeRulesDO feeRulesDO = feeRulesRepository.queryFeeRules();
        return feeRulesConverter.toFeeRulesDTO(feeRulesDO);
    }

}
