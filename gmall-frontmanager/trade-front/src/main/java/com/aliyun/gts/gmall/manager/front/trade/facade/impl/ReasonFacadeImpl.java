package com.aliyun.gts.gmall.manager.front.trade.facade.impl;


import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.trade.convertor.TradeResponseConvertor;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.*;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.*;

import com.aliyun.gts.gmall.manager.front.trade.facade.ReasonFacade;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.tradeconf.CancelReasonQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.CancelReasonDTO;

import com.aliyun.gts.gmall.platform.trade.api.facade.tradeconf.CancelReasonFacade;

import jodd.bean.BeanCopy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 订单操作接口
 *
 * @author tiansong
 */
@Slf4j
@Service
public class ReasonFacadeImpl implements ReasonFacade {
    @Autowired
    private TradeResponseConvertor   tradeResponseConvertor;
    @Autowired
    private CancelReasonFacade cancelReasonFacade;

    @Override
    public PageInfo<CancelReasonVO> queryCancelReasonList(CancelReasonQueryReq req) {
        CancelReasonQueryRpcReq rpc = new CancelReasonQueryRpcReq();
        BeanUtils.copyProperties(req, rpc);
        rpc.setChannel(2);
        RpcResponse<PageInfo<CancelReasonDTO>> rpcList = cancelReasonFacade.queryCancelReason(rpc);
        return tradeResponseConvertor.convertToVO(rpcList.getData());
    }
}