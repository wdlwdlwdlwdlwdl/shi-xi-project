package com.aliyun.gts.gmall.manager.front.trade.convertor;

import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.AddBankCardCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.BindedCardListQuery;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.input.epay.EpayBindCardsRpcReq;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.input.epay.EpayCardRpcReq;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ApiPayConvertor {
    ApiPayConvertor INSTANCE = Mappers.getMapper(ApiPayConvertor.class);

    EpayCardRpcReq toRpc(AddBankCardCommand cmd);

    EpayBindCardsRpcReq toRpc(BindedCardListQuery req);

}
