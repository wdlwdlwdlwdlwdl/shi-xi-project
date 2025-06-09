package com.aliyun.gts.gmall.manager.front.sourcing.convert;


import com.aliyun.gts.gmall.manager.front.trade.dto.utils.ContractUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 *
 */
@Mapper(componentModel = "spring" , imports = {ContractUtils.class})
public abstract class ContractConvert {

/*    public abstract B2BContractDTO convert(ContractVO contractVO);

    @Mappings(
        {
            @Mapping(expression = "java(contractDTO.getTemplateId() + \"\")" , target = "templateId"),
            @Mapping(expression = "java(ContractUtils.buttons(contractDTO))" , target = "buttons"),
            @Mapping(expression = "java(ContractUtils.getSignDisplay(contractDTO.getSignStatus()))" , target = "signStatusDisplay"),

        }
    )
    public abstract  ContractVO convert(B2BContractDTO contractDTO);*/

}
