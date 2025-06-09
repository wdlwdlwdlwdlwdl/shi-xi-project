package com.aliyun.gts.gmall.platform.trade.server.converter;

import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.EvaluationDTO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcEvaluationDO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.TcEvaluationRpcReq;
import org.mapstruct.Mapper;

/**
 * Created by auto-generated on 2021/02/04.
 */
@Mapper(componentModel = "spring")
public interface TcEvaluationConverter {

    EvaluationDTO tcEvaluationDOToDTO(TcEvaluationDO tcEvaluationDO);

    TcEvaluationDO tcEvaluationDTOToDO(EvaluationDTO tcEvaluationDTO);

    //@Mappings({
    //    @Mapping(target = "ratePic", source = "ratePic",ignore = true),
    //    @Mapping(target = "rateVideo", source = "rateVideo",ignore = true)
    //})
    TcEvaluationDO tcEvaluationReqToDO(TcEvaluationRpcReq tcEvaluationRpcReq);

}
