package com.aliyun.gts.gmall.manager.front.trade.adaptor;

import java.util.List;
import java.util.function.Function;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.framework.common.DubboBuilder;
import com.aliyun.gts.gmall.manager.front.common.consts.DsIdConst;
import com.aliyun.gts.gmall.manager.front.trade.convertor.TradeRequestConvertor;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.CreateReversalRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.ModifyReversalRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.ReversalBuyerConfirmRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.ReversalDeliverRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.param.ReversalSubOrder;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.ReversalCheckRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.ReversalRestQuery;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.*;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.MainReversalDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.ReversalCheckDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.ReversalReasonDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.ReversalSubOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.reversal.ReversalReadFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.reversal.ReversalWriteFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.ReversalTypeEnum;
import com.aliyun.gts.gmall.platform.trade.common.util.NumUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode.TRADE_CENTER_ERROR;
import static com.aliyun.gts.gmall.manager.front.trade.dto.utils.TradeFrontResponseCode.*;

/**
 * 交易逆向接口,售后单处理
 *
 * @author tiansong
 */
@Slf4j
@Service
public class ReversalAdapter {

    @Autowired
    private ReversalReadFacade reversalReadFacade;
    @Autowired
    private ReversalWriteFacade reversalWriteFacade;
    @Autowired
    private TradeRequestConvertor tradeRequestConvertor;

    DubboBuilder reversalBuilder = DubboBuilder.builder().logger(log).sysCode(TRADE_CENTER_ERROR).build();

    public PageInfo<MainReversalDTO> queryList(ReversalRestQuery reversalRestQuery) {
        return reversalBuilder.create()
            .id(DsIdConst.trade_reversal_queryList)
            .queryFunc(
                (Function<ReversalRestQuery, RpcResponse<PageInfo<MainReversalDTO>>>) request -> {
                // convert
                ReversalQueryRpcReq reversalQueryRpcReq = tradeRequestConvertor.convertQueryList(request);
                // request
                return reversalReadFacade.queryReversalList(reversalQueryRpcReq);
            })
            .bizCode(REVERSAL_LIST_ERROR)
            .query(reversalRestQuery);
    }


    public List<ReversalReasonDTO> queryReasonList(ReversalTypeEnum reversalTypeEnum) {
        // convert
        GetReasonRpcReq reasonRpcReq = new GetReasonRpcReq();
        reasonRpcReq.setReversalType(reversalTypeEnum.getCode());
        // request
        return reversalBuilder.create()
            .id(DsIdConst.trade_reversal_reasonList)
            .queryFunc((Function<GetReasonRpcReq, RpcResponse<List<ReversalReasonDTO>>>)
                reasonRpcReq1 -> reversalReadFacade
                .queryReversalReasons(reasonRpcReq1)
            )
            .bizCode(REVERSAL_REASON_ERROR)
            .query(reasonRpcReq);
    }


    public MainReversalDTO queryDetail(Long primaryReversalId) {
        GetDetailRpcReq detailRpcReq = new GetDetailRpcReq();
        detailRpcReq.setPrimaryReversalId(primaryReversalId);
        detailRpcReq.setIncludeFlows(Boolean.TRUE);
        detailRpcReq.setIncludeReason(Boolean.TRUE);
        return reversalBuilder.create().
            id(DsIdConst.trade_reversal_queryDetail)
            .queryFunc((Function<GetDetailRpcReq, RpcResponse<MainReversalDTO>>) request ->
                reversalReadFacade.queryReversalDetail(request)
            )
            .bizCode(REVERSAL_DETAIL_ERROR)
            .query(detailRpcReq);
    }


    public ReversalCheckDTO checkOrder(ReversalCheckRestQuery checkRestQuery) {
        return reversalBuilder.create()
            .id(DsIdConst.trade_reversal_checkOrder)
            .queryFunc(
                (Function<ReversalCheckRestQuery, RpcResponse<ReversalCheckDTO>>)request -> {
                    CheckReversalRpcReq checkReversalRpcReq = tradeRequestConvertor.convertCheckOrder(request);
                return reversalReadFacade.checkReversal(checkReversalRpcReq);
            })
            .bizCode(REVERSAL_CHECK_ERROR)
            .query(checkRestQuery);
    }


    public Long create(CreateReversalRestCommand createReversalRestCommand) {
        return reversalBuilder.create()
            .id(DsIdConst.trade_reversal_create)
            .queryFunc((Function<CreateReversalRestCommand, RpcResponse<Long>>)request -> {
                CreateReversalRpcReq createReversalRpcReq = convert(request);
                return reversalWriteFacade.createReversal(createReversalRpcReq);
            })
            .bizCode(REVERSAL_ADD_ERROR)
            .query(createReversalRestCommand);
    }

    private CreateReversalRpcReq convert(CreateReversalRestCommand cmd) {
        long cancelFreight = 0L;
        for (ReversalSubOrder sub : cmd.getSubOrders()) {
            // 含运费金额, 拆分分别退, 优先退运费
            if (NumUtils.getNullZero(sub.getMaxFreightAmt()) > 0) {
                long cancelAmt = sub.getCancelAmt();
                long freight = Math.min(cancelAmt, sub.getMaxFreightAmt());
                sub.setCancelAmt(cancelAmt - freight);
                cancelFreight += freight;
            }
        }
        CreateReversalRpcReq req = tradeRequestConvertor.convertCreate(cmd);
        req.setCancelFreightAmt(cancelFreight);
        return req;
    }

    public void cancel(ModifyReversalRestCommand modifyReversalRestCommand) {
        reversalBuilder.create()
            .id(DsIdConst.trade_reversal_cancel)
            .queryFunc((Function<ModifyReversalRestCommand, RpcResponse>)restCommand -> {
                ReversalModifyRpcReq request = tradeRequestConvertor.convertCancel(restCommand);
                return reversalWriteFacade.cancelByCustomer(request);
            })
            .bizCode(REVERSAL_CANCEL_ERROR)
            .query(modifyReversalRestCommand);
    }


    public void sendDeliver(ReversalDeliverRestCommand reversalDeliverRestCommand) {
        reversalBuilder.create()
            .id(DsIdConst.trade_reversal_sendDeliver)
            .queryFunc((Function<ReversalDeliverRestCommand, RpcResponse>)restCommand -> {
                ReversalDeliverRpcReq request = tradeRequestConvertor.convertDeliver(restCommand);
                return reversalWriteFacade.sendDeliverByCustomer(request);
            })
            .bizCode(REVERSAL_DELIVER_ERROR)
            .query(reversalDeliverRestCommand);
    }

    public void buyerConfirmRefund(ReversalBuyerConfirmRestCommand command) {
        reversalBuilder.create()
            .id(DsIdConst.trade_buyer_confrim_refund)
            .queryFunc((Function<ReversalBuyerConfirmRestCommand, RpcResponse>)restCommand -> {
                ReversalBuyerConfirmReq request = tradeRequestConvertor.convertBuyerConfirmReq(restCommand);
                return reversalWriteFacade.buyerConfirmRefund(request);
            })
            .bizCode(REVERSAL_BCR_ERROR)
            .query(command);
    }
}