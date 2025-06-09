package com.aliyun.gts.gmall.platform.trade.domain.repository;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.ReversalRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.MainReversalDTO;
import com.aliyun.gts.gmall.platform.trade.common.domain.KVDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatistics;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.wrapper.OrderQueryWrapper;

import java.util.List;
import java.util.function.BiConsumer;

public interface TcOrderRepository {
    /**
     * 保存到数据库
     */
    void create(TcOrderDO order);

    /**
     * 批量保存
     * @param orders
     */
    void batchCreate(List<TcOrderDO> orders);

    /**
     * 查主订单记录
     */
    TcOrderDO queryPrimaryByOrderId(Long primaryOrderId);

    /**
     * 查子订单记录
     */
    TcOrderDO querySubByOrderId(Long primaryOrderId, Long orderId);

    /**
     * 查主订单+其所有子订单记录
     */
    List<TcOrderDO> queryOrdersByPrimaryId(Long primaryOrderId);


    List<TcOrderDO> queryOrdersByPrimaryIds(List<Long> primaryOrderIds);

    /**
     * 更新主订单和子订单状态、stage
     * checkStatus、stage 可空
     */
    void updateStatusAndStageByPrimaryId(Long primaryOrderId, Integer status, Integer stage, Integer checkStatus);


    void updateStatusAndStageByPrimaryId(
        Long primaryOrderId,
        Integer status,
        Integer stage,
        Integer checkStatus,
        BiConsumer<TcOrderDO, TcOrderDO> processUpdateValue
    );

    int updateStatusByOrderId(Long primaryOrderId, Long orderId, Integer primaryStatus, Integer status, Integer checkStatus);

    /**
     * 根据 order_id 通用更新, primary_order_id 也必传
     */
    void updateByOrderId(TcOrderDO update);

    /**
     * 买家删除订单
     * @param primaryId
     */
    void customerDeleteOrder(Long primaryId);

    /**
     * 按状态统计订单
     * @param custId
     * @param status
     * @return
     */
    List<KVDO<Integer , Integer>>  countByStatus(Long custId , List<Integer> status);

    /**
     * @param query
     * @return
     */
    List<TcOrderDO> queryBoughtOrders(OrderQueryWrapper query);

    /**
     * 买家查主订单+其所有子订单记录
     */
    List<TcOrderDO> queryBoughtDetailByPrimaryId(Long primaryOrderId);

    /**
     *
     * @param query
     * @return
     */
    Integer countBoughtOrders(OrderQueryWrapper query);

    /**
     *
     * @param query
     * @return
     */
    List<TcOrderDO> querySoldOrders(OrderQueryWrapper query);

    /**
     * 根据 primaryOrderId + orderId + version 通用更新, 更新后version+1
     */
    boolean updateByOrderIdVersion(TcOrderDO update);
    
    /**
     * 根据商家id & 订单状态统计订单数量
     * 
     * @param sellerIds 商家ids
     * @param orderStatus 订单状态
     * @return
     */
    List<OrderStatistics> statisticsBySellerIds(List<Long> sellerIds, int orderStatus);

    /**
     * 更新状态
     * @param primaryOrderId
     * @param status
     * @param stage
     * @param checkStatus
     * @param cartId
     */
    void updateStatusAndStageAndCartIdByPrimaryId(
        Long primaryOrderId,
        Integer status,
        Integer stage,
        Integer checkStatus,
        String cartId
    );

    /**
     * 根据cartID 更新订单号
     * @param primaryOrderIds
     * @param cartId
     */
    void updateCartIdByPrimaryIds(List<Long> primaryOrderIds, String cartId);

    /**
     * 根据cartID 更新订单号
     * @param primaryOrderIds
     * @param cartId
     * @param bankCardNbr
     */
    void updateCartIdAndBankIdByPrimaryIds(List<Long> primaryOrderIds, String cartId, String bankCardNbr);


    void reversalStatusSynOrder(Long primaryOrderId, Integer status, Integer checkStatus);


    void reversalStatusSynOrder(MainReversal reversal, Long primaryOrderId, Integer status, Integer checkStatus);


    List<OrderStatistics> statisticsBySeller(List<Long> sellerIds);


    List<MainReversalDTO> statisticsSellerByCancelCodeAndTime(ReversalRpcReq req);


    int statisticsOrderByCancelCodeAndTime(ReversalRpcReq req);


    int statisticsOrderByTime(ReversalRpcReq req);

    /**
     * 根据主单删除临时结算单
     * @param primaryId
     * @return
     */
    void deleteCheckOrder(Long primaryId);

    /**
     * 通过支付查询所有的主单号
     * @param payCartId
     * @return
     */
    List<TcOrderDO> queryMainOrderByCartId(String payCartId);

}
