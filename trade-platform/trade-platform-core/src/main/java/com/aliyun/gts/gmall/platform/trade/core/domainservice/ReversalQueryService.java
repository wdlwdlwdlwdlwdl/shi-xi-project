package com.aliyun.gts.gmall.platform.trade.core.domainservice;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.ReversalRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.MainReversalDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.ReversalDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.ReversalReasonDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderQueryOption;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.ReversalDetailOption;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.ReversalSearchQuery;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.ReversalSearchResult;

import java.util.List;

public interface ReversalQueryService {

    /**
     * 查询售后理由
     */
    List<ReversalReasonDTO> queryReason(Integer reversalType);

    /**
     * 查询售后单
     */
    List<MainReversal> queryReversalByOrder(Long primaryOrderId);

    /**
     * 查询售后单, 含操作流水, 含售后原因内容, 不含订单信息（使用 fillOrderInfo 填充订单信息）
     */
    MainReversal queryReversal(Long primaryReversalId, ReversalDetailOption option);

    /**
     * 批量查询
     */
    List<MainReversal> batchQueryReversal(List<Long> primaryReversalIds, ReversalDetailOption option);

    /**
     * 搜索查询
     */
    ReversalSearchResult searchReversal(ReversalSearchQuery query);

    /**
     * 补全订单信息
     */
    void fillOrderInfo(List<MainReversal> reversalList);

    /**
     * 补全订单信息
     */
    void fillOrderInfo(List<MainReversal> reversalList, OrderQueryOption option);

    /**
     * 查询无库存退货商家
     */
    List<MainReversalDTO> queryRefundMerchant(ReversalRpcReq req);
    /**
     * 查询指定商家指定时间范围内的订单总数和因【无库存】退单总数
     */
    ReversalDTO statisticsReversal(ReversalRpcReq req);
}
