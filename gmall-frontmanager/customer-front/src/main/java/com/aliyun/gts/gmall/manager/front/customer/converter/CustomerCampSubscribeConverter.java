package com.aliyun.gts.gmall.manager.front.customer.converter;

import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.CustomerCampSubscribeCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.CustomerCampUnSubscribeCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.CustomerCampSubscribeQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.customer.CustomerCampSubscribeVO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.input.CustomerCampSubscribeQueryReq;
import com.aliyun.gts.gmall.platform.promotion.api.dto.model.CustomerCampSubscribeDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.model.CustomerCampUnSubscribeDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.CustomerCampSubscribeResultDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerCampSubscribeConverter {


    CustomerCampSubscribeDTO convertToSubDTO(CustomerCampSubscribeCommand command);

    CustomerCampUnSubscribeDTO convertToUnSubDTO(CustomerCampUnSubscribeCommand command);


    CustomerCampSubscribeVO convertCustomerSubVO(CustomerCampSubscribeResultDTO customerCampSubscribeResultDTO);

    CustomerCampSubscribeQueryReq convertCustomerSubQueryReq(CustomerCampSubscribeQuery customerCampSubscribeQuery);
}
