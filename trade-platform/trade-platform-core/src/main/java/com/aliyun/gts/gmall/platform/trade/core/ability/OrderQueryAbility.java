package com.aliyun.gts.gmall.platform.trade.core.ability;

import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderQueryOption;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;

import java.util.List;

public interface OrderQueryAbility {


    /**
     * 数据库查询主订单（含子订单）
     */
    MainOrder getMainOrder(Long primaryOrderId);

    /**
     * 数据库查询主订单（含子订单）
     */
    MainOrder getMainOrder(Long primaryOrderId, OrderQueryOption option);

    /**
     * 批量查询（含子订单）
     */
    List<MainOrder> batchGetMainOrder(List<Long> primaryOrderIds);

    /**
     * 批量查询（含子订单）
     */
    List<MainOrder> batchGetMainOrder(List<Long> primaryOrderIds, OrderQueryOption option);


    /**
     * 定制接口：买家数据库查询主订单（含子订单）
     */
    MainOrder getBoughtMainOrder(Long primaryOrderId, OrderQueryOption option);

    /**
     * 查订单扩展结构 (用于已经获取到订单信息, 但没查扩展结构, 单独查扩展结构的场景)
     */
    void fillOrderExtends(List<MainOrder> orders);

    /**
     * 单独查阶段信息
     */
    void fillStepOrders(List<MainOrder> orders);





}
