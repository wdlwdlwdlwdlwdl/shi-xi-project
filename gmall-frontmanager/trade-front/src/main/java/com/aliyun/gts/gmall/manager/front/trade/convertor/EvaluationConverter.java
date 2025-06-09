package com.aliyun.gts.gmall.manager.front.trade.convertor;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.EvaluationRateVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.ItemEvaluationVO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.EvaluationDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.EvaluationRateDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.ItemEvaluationDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EvaluationConverter {

    PageInfo<ItemEvaluationVO> convertItemEvaluationPage(PageInfo<ItemEvaluationDTO> dto);

    EvaluationRateVO convertEvaluation(EvaluationRateDTO dto);
}
