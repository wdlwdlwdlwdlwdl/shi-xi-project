package com.aliyun.gts.gmall.platform.trade.core.convertor;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.DeliveryFeeHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.FeeRulesHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.DeliveryFeeHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.FeeRulesHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcDeliveryFeeHistoryDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcFeeRulesHistoryDO;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface FeeRulesHistoryConverter {


    FeeRulesHistoryDTO toFeeRulesHistoryDTO(TcFeeRulesHistoryDO tcFeeRulesHistoryDO);

    List<FeeRulesHistoryDTO> toFeeRulesHistoryListDTO(List<TcFeeRulesHistoryDO> list);

    TcFeeRulesHistoryDO toFeeRulesHistoryDO(FeeRulesHistoryRpcReq rpc);
}
