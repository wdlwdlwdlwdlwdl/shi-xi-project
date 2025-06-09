package com.aliyun.gts.gmall.center.trade.core.domainservice;

import com.aliyun.gts.gmall.center.trade.api.dto.output.CombineItemDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.CreateReversalRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;

import java.util.List;
import java.util.Map;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/10/22 17:18
 */
public interface CombineItemService {

    /**
     * 查询订单里的;组合商品
     * @param mainOrders 返回skuId,合并对象ID
     */
    Map<Long, List<CombineItemDTO>> queryCombineItem(List<MainOrder> mainOrders);

    /**
     * 确认订单校验
     * @param order
     * @return
     */
    TradeBizResult confirmCheck(CreatingOrder order);

    /**
     *
     * @param reversal
     * @param historyList
     */
    void checkReversal(MainReversal reversal, List<MainReversal> historyList);

    /**
     * 退单
     * @param reversal
     * @param req
     */
    void fillReversal(MainReversal reversal, CreateReversalRpcReq req);
    
}
