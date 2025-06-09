package com.aliyun.gts.gmall.platform.trade.core.convertor;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.DeliveryFeeHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonHistoryDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcDeliveryFeeHistoryDO;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface DeliveryFeeHistoryConverter {


    DeliveryFeeHistoryDTO toDeliveryFeeHistoryDTO(TcDeliveryFeeHistoryDO deliveryFeeDO);

    List<DeliveryFeeHistoryDTO> toDeliveryFeeHistoryListDTO(List<TcDeliveryFeeHistoryDO> list);

    TcDeliveryFeeHistoryDO toDeliveryFeenHistoryDO(DeliveryFeeHistoryRpcReq rpc);
}
