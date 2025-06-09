package com.aliyun.gts.gmall.manager.front.customer.converter;

import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.AddFavouriteRestCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.DeleteFavouriteRestCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.customer.CustomerFavouriteVO;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerFavouriteDeleteCommand;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerFavouriteQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.message.CustFavouriteMessageDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerFavouriteDTO;
import org.mapstruct.Mapper;

/**
 *
 *
 * @author FaberWong
 */
@Mapper(componentModel = "spring")
public interface CustomerFavouriteConverter {

    /**
     * cmd转message
     *
     * @param addFavouriteRestCommand
     * @return
     */
    CustFavouriteMessageDTO command2Message(AddFavouriteRestCommand addFavouriteRestCommand);

    CustomerFavouriteQuery to(com.aliyun.gts.gmall.manager.front.customer.dto.input.query.CustomerFavouriteQuery query);

    CustomerFavouriteVO toVO(CustomerFavouriteDTO customerFavouriteDTO);

    CustomerFavouriteDeleteCommand toDeleteCommand(DeleteFavouriteRestCommand deleteFavouriteRestCommand);

    CustFavouriteMessageDTO command2Message(DeleteFavouriteRestCommand command);
}
