package com.aliyun.gts.gmall.manager.front.trade.facade;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.*;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.ConfirmOrderRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.CountOrderRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.OrderListRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.PrimaryOrderRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.*;

import java.util.List;

/**
 * 订单接口
 *
 * @author tiansong
 */
public interface OrderFacade {

    /**
     * 确认订单前check
     * [该接口核心目的是为了前端页面跳转前提前check，避免页面跳转到订单确认页面后再往回跳转]
     *
     * @param confirmOrderRestQuery
     * @return
     */
    Boolean checkConfirm(ConfirmOrderRestQuery confirmOrderRestQuery);

    /**
     * 订单确认
     *
     * @param confirmOrderRestQuery
     * @return
     */
    OrderConfirmVO confirm(ConfirmOrderRestQuery confirmOrderRestQuery);

    /**
     * 创建订单
     *
     * @param createOrderRestCommand
     * @return
     */
    OrderCreateResultVO createOrder(CreateOrderRestCommand createOrderRestCommand);

    /**
     * 订单确认 新版本
     * @anthor shfeng
     * @param confirmOrderRestQuery
     * @return
     */
    OrderConfirmVO confirmNew(ConfirmOrderRestQuery confirmOrderRestQuery);

    /**
     * 订单确认 新版本
     * @anthor shfeng
     * @param createCheckOutOrderRestCommand
     * @return
     */
    CheckOutOrderResultVO checkOutOrderNew(CreateCheckOutOrderRestCommand createCheckOutOrderRestCommand);

    /**
     * 创建订单 新版本
     * @anthor shfeng
     * @param createOrderRestCommand
     * @return
     */
    OrderCreateResultVO createOrderNew(CreateOrderRestCommand createOrderRestCommand);

    /**
     * 订单详情查询
     *
     * @param primaryOrderRestQuery
     * @return
     */
    OrderMainVO getDetail(PrimaryOrderRestQuery primaryOrderRestQuery);

    /**
     * 订单详情(传primaryOrderIdList列表)
     *
     * @param primaryOrderRestQuery
     * @return
     */
    List<OrderMainVO> getDetailNew(PrimaryOrderRestQuery primaryOrderRestQuery);


    /**
     * 订单列表查询
     *
     * @param orderListRestQuery
     * @return
     */
    PageInfo<OrderMainVO> getList(OrderListRestQuery orderListRestQuery);

    /**
     * 取消订单
     *
     * @param primaryOrderRestCommand
     * @return
     */
    Boolean cancelOrder(PrimaryOrderRestCommand primaryOrderRestCommand);

    /**
     * 删除订单
     *
     * @param primaryOrderRestCommand
     * @return
     */
    Boolean delOrder(PrimaryOrderRestCommand primaryOrderRestCommand);

    /**
     * 确认收货
     *
     * @param primaryOrderRestCommand
     * @return
     */
    Boolean confirmReceipt(PrimaryOrderRestCommand primaryOrderRestCommand);

    /**
     * 多阶段用户确认
     */
    Boolean confirmStepOrder(StepOrderRestCommand stepOrderRestCommand);

    /**
     * 订单评价
     *
     * @param addEvaluationRestCommand
     * @return
     */
    Boolean addEvaluation(AddEvaluationRestCommand addEvaluationRestCommand);

    /**
     * 计算订单各种状态的数量
     *
     * @param query
     * @return
     */
    OrderCountByStatusVO count(CountOrderRestQuery query);

    /**
     * 查询物流详情
     *
     * @param primaryOrderRestQuery
     * @return
     */
    List<LogisticsDetailVO> queryLogisticsList(PrimaryOrderRestQuery primaryOrderRestQuery);

    /**
     * 订单操作流查询
     *
     * @param primaryOrderRestQuery
     * @return
     */
    List<OrderOperateFlowVO> getFlowList(PrimaryOrderRestQuery primaryOrderRestQuery);

    /**
     * 记录用户的
     * @param orderPickCommand
     * @return
     */
    Boolean saveUserLogisticsPick(OrderPickCommand orderPickCommand);


    Boolean cancelOrders(PrimaryOrderRestCommand primaryOrderRestCommand);
}
