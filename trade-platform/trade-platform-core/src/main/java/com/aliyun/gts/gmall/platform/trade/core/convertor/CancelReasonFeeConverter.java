package com.aliyun.gts.gmall.platform.trade.core.convertor;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.convert.MultiLangConverter;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonFeeQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonFeeRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonFeeDTO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonFeeDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CancelReasonFee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;


@Mapper(componentModel = "spring")
public abstract class CancelReasonFeeConverter {

    @Autowired
    MultiLangConverter multiLangConverter;

    public abstract  TcCancelReasonFeeDO toTcCancelReasonDO(CancelReasonFeeDTO dto);

    @Mapping(target = "cancelReasonNameI18n", expression = "java(multiLangConverter.mText_set(multiLangConverter.to_multiLangText(cancelReasonFeeDO.getCancelReasonName())))")
    @Mapping(target = "cancelReasonName", expression = "java(multiLangConverter.mText_to_str(multiLangConverter.to_multiLangText(cancelReasonFeeDO.getCancelReasonName())))")
    public abstract  CancelReasonFeeDTO toCancelReasonDTO(TcCancelReasonFeeDO cancelReasonFeeDO);

    public abstract  TcCancelReasonFeeDO toTcCancelReasonDO(CancelReasonFeeRpcReq rpc);

    public abstract  PageInfo<CancelReasonFeeDTO> toCancelReasonDTOPage(PageInfo<TcCancelReasonFeeDO> list);

    public abstract  CancelReasonFee toTcCancelReason(CancelReasonFeeQueryRpcReq rpc);

}
