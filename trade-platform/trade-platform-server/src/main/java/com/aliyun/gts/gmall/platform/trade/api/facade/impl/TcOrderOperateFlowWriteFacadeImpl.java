//package com.aliyun.gts.gmall.platform.trade.api.facade.impl;
//
//import com.aliyun.gts.gmall.platform.trade.api.dto.output.TcOrderOperateFlowDTO;
//import com.aliyun.gts.gmall.platform.trade.api.facade.TcOrderOperateFlowWriteFacade;
//import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderOperateFlowDO;
//import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderOperateFlowRepository;
//import com.aliyun.gts.gmall.platform.trade.server.converter.TcOrderOperateFlowConverter;
//import com.aliyun.gts.gmall.platform.trade.api.dto.input.TcOrderOperateFlowRpcReq;
//import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import lombok.extern.slf4j.Slf4j;
//
///**
// * Created by auto-generated on 2021/03/23.
// */
//@Service
//@Slf4j
//public class TcOrderOperateFlowWriteFacadeImpl implements TcOrderOperateFlowWriteFacade {
//
//    @Autowired
//    private TcOrderOperateFlowRepository tcOrderOperateFlowRepository;
//
//    @Autowired
//    private TcOrderOperateFlowConverter tcOrderOperateFlowConverter;
//
//    @Override
//    public RpcResponse<TcOrderOperateFlowDTO> createTcOrderOperateFlow(TcOrderOperateFlowRpcReq tcOrderOperateFlowRpcReq) {
//        TcOrderOperateFlowDO tcOrderOperateFlowDO = tcOrderOperateFlowConverter.tcOrderOperateFlowReqToDO(tcOrderOperateFlowRpcReq);
//        TcOrderOperateFlowDO result = tcOrderOperateFlowRepository.create(tcOrderOperateFlowDO);
//        return RpcResponse.ok(tcOrderOperateFlowConverter.tcOrderOperateFlowDOToDTO(result));
//    }
//
//    @Override
//    public RpcResponse<TcOrderOperateFlowDTO> updateTcOrderOperateFlow(TcOrderOperateFlowRpcReq tcOrderOperateFlowRpcReq) {
//        TcOrderOperateFlowDO record = tcOrderOperateFlowConverter.tcOrderOperateFlowReqToDO(tcOrderOperateFlowRpcReq);
//        TcOrderOperateFlowDO newRecord = tcOrderOperateFlowRepository.update(record);
//        return RpcResponse.ok(tcOrderOperateFlowConverter.tcOrderOperateFlowDOToDTO(newRecord));
//    }
//
//    @Override
//    public RpcResponse<Boolean> deleteTcOrderOperateFlow(TcOrderOperateFlowRpcReq tcOrderOperateFlowRpcReq) {
//        int cnt = tcOrderOperateFlowRepository.delete(tcOrderOperateFlowRpcReq.getId());
//        return RpcResponse.ok(cnt > 0);
//    }
//
//
//}