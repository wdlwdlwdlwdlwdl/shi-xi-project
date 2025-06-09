package com.aliyun.gts.gmall.platform.trade.core.convertor;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.convert.MultiLangConverter;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonQueryNoPageRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonDTO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CancelReason;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Mapper(componentModel = "spring")
public abstract class CancelReasonConverter {

    @Autowired
    MultiLangConverter multiLangConverter;

    public abstract TcCancelReasonDO toTcCancelReasonDO(CancelReasonDTO dto);

    /**
     * 转换
     * @param cancelReasonDO
     * @return
     */
    @Mapping(target = "cancelReasonNameI18n", expression = "java(multiLangConverter.mText_set(multiLangConverter.to_multiLangText(cancelReasonDO.getCancelReasonName())))")
    @Mapping(target = "cancelReasonName", expression = "java(multiLangConverter.mText_to_str(multiLangConverter.to_multiLangText(cancelReasonDO.getCancelReasonName())))")
    public abstract CancelReasonDTO toCancelReasonDTO(TcCancelReasonDO cancelReasonDO);

    public abstract TcCancelReasonDO toTcCancelReasonDO(CancelReasonRpcReq rpc);

    public abstract List<CancelReasonDTO> toCancelReasonDTOList(List<TcCancelReasonDO> tcCancelReasonDO);

    public abstract PageInfo<CancelReasonDTO> toCancelReasonDTOPage(PageInfo<TcCancelReasonDO> list);

    public abstract CancelReason toTcCancelReason(CancelReasonQueryRpcReq rpc);

    public abstract CancelReason toTcCancelReason(CancelReasonQueryNoPageRpcReq rpc);

}
