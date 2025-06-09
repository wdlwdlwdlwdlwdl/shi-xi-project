package com.aliyun.gts.gmall.manager.front.customer.converter;

import com.aliyun.gts.gmall.manager.front.common.dto.PageLoginRestQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.AddFavouriteRestCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.AddVisitHisRestCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.customer.ItemVisitHisVO;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerByCustIdQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.message.ItemVisitHisMessageDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerItemVisitHisDTO;
import org.mapstruct.Mapper;

/**
 *
 *
 * @author FaberWong
 */
@Mapper(componentModel = "spring")
public interface ItemVisitHisConverter {

    /**
     * cmd转message
     *
     * @param addFavouriteRestCommand
     * @return
     */
    ItemVisitHisMessageDTO command2Message(AddVisitHisRestCommand addFavouriteRestCommand);

    CustomerByCustIdQuery toCustomerByCustIdQuery(PageLoginRestQuery query);

    ItemVisitHisVO toVO(CustomerItemVisitHisDTO customerItemVisitHisDTO);
}
