package com.aliyun.gts.gmall.manager.front.trade.facade;

import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.DeliveryAccessQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.DeliveryAddressQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.DeliveryTimeQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.DeliveryAddressVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.DeliveryTimeVO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.DeliveryAccessDTO;

/**
 * 取消原因接口
 *
 * @author yangl
 */
public interface TradeDeliveryFacade {

    DeliveryTimeVO queryDeliveryTime(DeliveryTimeQuery req);

    DeliveryAddressVO queryDeliveryAddress(DeliveryAddressQuery req);

//    DeliveryTypeDetailDTO queryDeliveryType(DeliveryTypeQueryReq req);

    DeliveryAccessDTO queryDeliveryAccess(DeliveryAccessQuery req);
}
