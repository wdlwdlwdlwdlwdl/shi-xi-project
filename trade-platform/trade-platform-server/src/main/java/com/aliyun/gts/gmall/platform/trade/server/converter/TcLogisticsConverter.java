package com.aliyun.gts.gmall.platform.trade.server.converter;

import java.util.Map;

import com.alibaba.fastjson.JSON;

import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcLogisticsDO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.TcLogisticsDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics.TcLogisticsRpcReq;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import com.aliyun.gts.gmall.platform.trade.common.constants.LogisticsCompanyTypeEnum;

/**
 * Created by auto-generated on 2021/02/04.
 */
@Mapper(componentModel = "spring")
public interface TcLogisticsConverter {


    TcLogisticsDTO tcLogisticsDOToDTO(TcLogisticsDO tcLogisticsDO);

    TcLogisticsDO tcLogisticsDTOToDO(TcLogisticsDTO tcLogisticsDTO);

    @Named("mapToString")
    default String mapToString(Map map) {
        return JSON.toJSONString(map);
    }

    @Named("stringToMap")
    default Map stringToMap(String json) {
        return JSON.parseObject(json , Map.class);
    }

}
