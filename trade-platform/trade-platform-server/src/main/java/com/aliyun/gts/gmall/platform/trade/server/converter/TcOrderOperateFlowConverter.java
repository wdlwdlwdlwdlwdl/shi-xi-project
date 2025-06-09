package com.aliyun.gts.gmall.platform.trade.server.converter;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.TcOrderOperateFlowRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.TcOrderOperateFlowDTO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderOperateFlowDO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Created by auto-generated on 2021/03/23.
 */
@Mapper(componentModel = "spring")
public interface TcOrderOperateFlowConverter {

    TcOrderOperateFlowDTO tcOrderOperateFlowDOToDTO(TcOrderOperateFlowDO tcOrderOperateFlowDO);

    TcOrderOperateFlowDO tcOrderOperateFlowDTOToDO(TcOrderOperateFlowDTO tcOrderOperateFlowDTO);

    TcOrderOperateFlowDO tcOrderOperateFlowReqToDO(TcOrderOperateFlowRpcReq tcOrderOperateFlowRpcReq);

    List<TcOrderOperateFlowDTO> tcOrderOperateFlowDOToDTO(List<TcOrderOperateFlowDO> tcOrderOperateFlowDO);

}
