package com.aliyun.gts.gmall.platform.trade.api.facade.impl;


import com.aliyun.gts.gmall.framework.api.rest.dto.CommonByIdQuery;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.TcOrderOperateFlowRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.TcOrderOperateFlowDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.TcOrderOperateFlowReadFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderOperateFlowDO;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderOperateFlowRepository;
import com.aliyun.gts.gmall.platform.trade.server.converter.TcOrderOperateFlowConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Created by auto-generated on 2021/03/23.
 */

@Service
@Slf4j
public class TcOrderOperateFlowReadFacadeImpl implements TcOrderOperateFlowReadFacade {

    @Autowired
    private TcOrderOperateFlowRepository tcOrderOperateFlowRepository;

    @Autowired
    private TcOrderOperateFlowConverter tcOrderOperateFlowConverter;

    @Override
    public RpcResponse<TcOrderOperateFlowDTO> query(CommonByIdQuery commonByIdQuery) {
         TcOrderOperateFlowDO tcOrderOperateFlowDO=tcOrderOperateFlowRepository.queryTcOrderOperateFlow(commonByIdQuery.getId());
         TcOrderOperateFlowDTO tcOrderOperateFlowDTO = tcOrderOperateFlowConverter.tcOrderOperateFlowDOToDTO(tcOrderOperateFlowDO);
         return RpcResponse.ok(tcOrderOperateFlowDTO);
    }

    @Override
    public RpcResponse<List<TcOrderOperateFlowDTO>> queryList(TcOrderOperateFlowRpcReq query) {
        List<TcOrderOperateFlowDO> tcOrderOperateFlowDO = tcOrderOperateFlowRepository.queryByPrimaryId(query.getPrimaryOrderId());
        for(TcOrderOperateFlowDO tcOrder : tcOrderOperateFlowDO){
            OrderStatusEnum orderStatus = OrderStatusEnum.codeOf(tcOrder.getToOrderStatus());
            if(Objects.nonNull(orderStatus)){
                tcOrder.setOpName(orderStatus.getInner());
            }
        }
        return RpcResponse.ok(tcOrderOperateFlowConverter.tcOrderOperateFlowDOToDTO(tcOrderOperateFlowDO));
    }
}
