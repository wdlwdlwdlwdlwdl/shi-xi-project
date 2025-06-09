package com.aliyun.gts.gmall.platform.trade.core.convertor;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CustDeliveryFeeHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CustDeliveryFeeHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.DeliveryFeeHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCustDeliveryFeeHistoryDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcDeliveryFeeHistoryDO;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface CustDeliveryFeeHistoryConverter {


    CustDeliveryFeeHistoryDTO toCustDeliveryFeeHistoryDTO(TcCustDeliveryFeeHistoryDO deliveryFeeDO);

    List<CustDeliveryFeeHistoryDTO> toDeliveryFeeHistoryListDTO(List<TcCustDeliveryFeeHistoryDO> list);

    TcCustDeliveryFeeHistoryDO toCustDeliveryFeeHistoryDO(CustDeliveryFeeHistoryRpcReq rpc);
}
