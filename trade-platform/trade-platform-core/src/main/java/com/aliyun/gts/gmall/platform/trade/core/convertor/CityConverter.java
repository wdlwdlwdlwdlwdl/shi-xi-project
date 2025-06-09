package com.aliyun.gts.gmall.platform.trade.core.convertor;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CityQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CityRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CityDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.DeliveryFeeDTO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCityDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcDeliveryFeeDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.City;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.DeliveryFee;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface CityConverter {


    TcCityDO toTcCityDO(CityDTO dto);

    CityDTO toCityDTO(TcCityDO cityDO);

    TcCityDO toTcCityDO(CityRpcReq rpc);

    PageInfo<CityDTO> toCityDTOPage(PageInfo<TcCityDO> list);

    City toTcCity(CityQueryRpcReq rpc);

    List<CityDTO> toCityListDTO(List<TcCityDO> cityDO);
}
