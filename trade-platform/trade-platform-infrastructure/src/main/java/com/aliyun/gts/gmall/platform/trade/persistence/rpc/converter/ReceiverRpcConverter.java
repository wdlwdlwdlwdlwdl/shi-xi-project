package com.aliyun.gts.gmall.platform.trade.persistence.rpc.converter;

import com.aliyun.gts.gmall.platform.trade.api.dto.common.ReceiverDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ReceiveAddr;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerAddressDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.SellerAddressDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ReceiverRpcConverter {

    @Mappings({
        @Mapping(target = "receiverId", source = "id"),
        @Mapping(target = "receiverName", source = "name"),
        @Mapping(target = "phone", source = "phone"),
        @Mapping(target = "deliveryAddr", source = "completeAddr"),
        @Mapping(target = "provinceCode", source = "provinceId"),
        @Mapping(target = "cityCode", source = "cityId"),
        @Mapping(target = "districtCode", source = "areaId"),
        @Mapping(target = "streetCode", source = "streetId"),
        @Mapping(target = "apartmentNo", source = "apartmentNo"),
        @Mapping(target = "addressDetail", source = "addressDetail"),
    })
    ReceiveAddr toReceiveAddr(CustomerAddressDTO addr);


    @Mappings({
        @Mapping(target = "receiverId", source = "id"),
        @Mapping(target = "receiverName", source = "name"),
        @Mapping(target = "phone", source = "phone"),
        @Mapping(target = "deliveryAddr", source = "completeAddr"),
        @Mapping(target = "provinceCode", source = "provinceId"),
        @Mapping(target = "cityCode", source = "cityId"),
        @Mapping(target = "districtCode", source = "areaId"),
        @Mapping(target = "streetCode", source = "streetId"),
    })
    ReceiveAddr toReceiveAddr(SellerAddressDTO addr);


    ReceiverDTO toReceiverDTO(ReceiveAddr addr);

}
