package com.aliyun.gts.gmall.platform.trade.core.convertor;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonHistoryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonHistoryDTO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonHistoryDO;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface CancelReasonHistoryConverter {


    CancelReasonHistoryDTO toCancelReasonHistoryDTO(TcCancelReasonHistoryDO cancelReasonDO);

    List<CancelReasonHistoryDTO> toCancelReasonHistoryListDTO(List<TcCancelReasonHistoryDO> list);

    TcCancelReasonHistoryDO toCancelReasonHistoryDO(CancelReasonHistoryRpcReq rpc);
}
