package com.aliyun.gts.gmall.center.trade.core.domainservice.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.center.trade.api.dto.input.EvaluationDTORpcReq;
import com.aliyun.gts.gmall.center.trade.core.domainservice.EvaluationExtService;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.TcEvaluationExtRepository;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.EvaluationDTO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcEvaluationDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcEvaluationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author : zhang.beilei
 * @date : 2022/10/18 17:39
 **/
@Service
@Slf4j
public class EvaluationExtServiceImpl implements EvaluationExtService {

    @Resource
    TcEvaluationExtRepository tcEvaluationExtRepository;

    @Resource
    TcEvaluationRepository tcEvaluationRepository;

    @Override
    public EvaluationDTO getById(Long id) {
        EvaluationDTO dto = new EvaluationDTO();
        TcEvaluationDO entity = tcEvaluationExtRepository.getById(id);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

    @Override
    public Boolean updateEvaluation(EvaluationDTORpcReq dto) {
        return tcEvaluationExtRepository.updateExtend(dto.getId(), JSON.toJSONString(dto.getExtend()));
    }

    @Override
    public Boolean updateExtendByPrimaryOrderId(EvaluationDTORpcReq dto) {
        return tcEvaluationExtRepository.updateExtendByPrimaryOrderId(dto.getId(), JSON.toJSONString(dto.getExtend()));
    }
}
