package com.aliyun.gts.gmall.platform.trade.core.convertor;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonFeeRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonFeeDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.DeliveryFeeDTO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonFeeDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcDeliveryFeeDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CancelReason;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CancelReasonFee;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.DeliveryFee;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface DeliveryFeeConverter {


    TcDeliveryFeeDO toTcDeliveryFeeDO(DeliveryFeeDTO dto);

    DeliveryFeeDTO toDeliveryFeeDTO(TcDeliveryFeeDO deliveryFeeDO);

    TcDeliveryFeeDO toTcDeliveryFeeDO(DeliveryFeeRpcReq rpc);

    PageInfo<DeliveryFeeDTO> toDeliveryFeeDTOPage(PageInfo<TcDeliveryFeeDO> list);

    DeliveryFee toTcDeliveryFee(DeliveryFeeQueryRpcReq rpc);

    TcDeliveryFeeDO toTcDeliveryFeeDO(DeliveryFeeQueryRpcReq req);
}
