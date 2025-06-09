package com.aliyun.gts.gmall.platform.trade.core.convertor;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CustDeliveryFeeQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CustDeliveryFeeRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CustDeliveryFeeDTO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCustDeliveryFeeDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CustDeliveryFee;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface CustDeliveryFeeConverter {


    TcCustDeliveryFeeDO toTcCustDeliveryFeeDO(CustDeliveryFeeDTO dto);

    CustDeliveryFeeDTO toCustDeliveryFeeDTO(TcCustDeliveryFeeDO deliveryFeeDO);

    List<CustDeliveryFeeDTO> toCustDeliveryFeeDTOList(List<TcCustDeliveryFeeDO> deliveryFeeDO);

    TcCustDeliveryFeeDO toTcCustDeliveryFeeDO(CustDeliveryFeeRpcReq rpc);

    PageInfo<CustDeliveryFeeDTO> toCustDeliveryFeeDTOPage(PageInfo<TcCustDeliveryFeeDO> list);

    CustDeliveryFee toTcCustDeliveryFee(CustDeliveryFeeQueryRpcReq rpc);
}
