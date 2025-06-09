package com.aliyun.gts.gmall.platform.trade.core.convertor;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonFeeHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonFeeHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonFeeHistoryDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonHistoryDO;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface CancelReasonFeeHistoryConverter {


    CancelReasonFeeHistoryDTO toCancelReasonHistoryDTO(TcCancelReasonFeeHistoryDO cancelReasonDO);

    List<CancelReasonFeeHistoryDTO> toCancelReasonHistoryListDTO(List<TcCancelReasonFeeHistoryDO> list);

    TcCancelReasonFeeHistoryDO toCancelReasonHistoryDO(CancelReasonFeeHistoryRpcReq rpc);
}
