package com.aliyun.gts.gmall.platform.trade.core.convertor;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.GlobalConfigDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.SellerConfigDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.SellerTradeConfig;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TradeConfigConverter {

    // SellerTradeConfig <==> SellerConfigDTO

    SellerConfigDTO toSellerConfigDTO(SellerTradeConfig conf);

    default SellerConfigDTO toSellerConfigDTO(SellerTradeConfig conf, Long sellerId) {
        SellerConfigDTO dto = toSellerConfigDTO(conf);
        dto.setSellerId(sellerId);
        return dto;
    }

    SellerTradeConfig toSellerTradeConfig(SellerConfigDTO dto);


    // SellerTradeConfig <==> GlobalConfigDTO

    GlobalConfigDTO toGlobalConfigDTO(SellerTradeConfig conf);

    SellerTradeConfig toSellerTradeConfig(GlobalConfigDTO dto);
}
