package com.aliyun.gts.gmall.manager.front.sourcing.convert;

import com.aliyun.gts.gcai.platform.contract.api.dto.model.ContractTemplateDTO;
import com.aliyun.gts.gcai.platform.contract.api.dto.model.ContractTemplateUpdateDTO;
import com.aliyun.gts.gcai.platform.contract.common.input.query.ContractTemplateQuery;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.ContractTemplateReq;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.contract.ContractTemplateVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 *
 */
@Mapper(componentModel = "spring")
public interface ContractTemplateConvert {

    @Mappings({
        @Mapping(source = "templateFile.fileName",target = "templateFile.fileName"),
        @Mapping(source = "templateFile.ossUrl",target = "templateFile.ossUrl"),
        @Mapping(expression = "java(templateVO.getETemplateId() != null ? Long.valueOf(templateVO.getETemplateId()) : null)" ,
            target = "ETemplateId")
    })
    ContractTemplateDTO convert(ContractTemplateVO templateVO);

    @Mappings({
        @Mapping(source = "templateFile.fileName",target = "templateFile.fileName"),
        @Mapping(source = "templateFile.ossUrl",target = "templateFile.ossUrl"),
        @Mapping(expression = "java(templateDTO.getETemplateId() + \"\")" , target = "ETemplateId")
    })
    ContractTemplateVO convert(ContractTemplateDTO templateDTO);

    ContractTemplateQuery convert(ContractTemplateReq req);

    ContractTemplateUpdateDTO convert2Update(ContractTemplateVO templateVO);

}
