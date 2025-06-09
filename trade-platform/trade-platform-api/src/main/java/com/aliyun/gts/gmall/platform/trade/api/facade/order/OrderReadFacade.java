package com.aliyun.gts.gmall.platform.trade.api.facade.order;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmOrderInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.extend.OrderExtendQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.*;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.extend.OrderExtendDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;
import java.util.Map;

@Api("订单读接口")
public interface OrderReadFacade {

    @ApiOperation("订单确认展示")
    RpcResponse<ConfirmOrderDTO> confirmOrderInfo(ConfirmOrderInfoRpcReq req);

    /**
     * 订单确认展示  新版本
     *    仅用来计算订单商品信息不生成订单
     * @anthor shifeng
     * @param req
     * @return
     */
    @ApiOperation("订单确认展示")
    RpcResponse<ConfirmOrderDTO> confirmOrderInfoNew(ConfirmOrderInfoRpcReq req);

    @ApiOperation("买家查询已买到列表")
    RpcResponse<PageInfo<MainOrderDTO>> queryCustOrderList(CustomerOrderQueryRpcReq customerOrderQueryRpcReq);

    @ApiOperation("卖家查询已卖出列表")
    RpcResponse<PageInfo<MainOrderDTO>> querySellerOrderList(SellerOrderQueryRpcReq sellerOrderQueryRpcReq);

    @ApiOperation("运营查询已卖出列表")
    RpcResponse<PageInfo<MainOrderDTO>> queryAdminOrderList(SellerOrderQueryRpcReq sellerOrderQueryRpcReq);

    @ApiOperation("通用订单搜索")
    RpcResponse<PageInfo<MainOrderDTO>> queryOrderList(OrderSearchRpcReq orderSearchRpcReq);

    @ApiOperation("订单详情查询")
    RpcResponse<MainOrderDetailDTO> queryOrderDetail(OrderDetailQueryRpcReq orderDetailQueryRpcReq);

    @ApiOperation("订单详情查询(列表)")
    RpcResponse<List<MainOrderDetailDTO>> queryOrderDetailNew(OrderDetailQueryRpcReq orderDetailQueryRpcReq);

    @ApiOperation("根据状态统计订单数")
    RpcResponse<Map<Integer , Integer>> countOrderByStatus(CountOrderQueryRpcReq req);

    @ApiOperation("查询订单扩展结构")
    RpcResponse<List<OrderExtendDTO>> queryOrderExtend(OrderExtendQueryRpcReq req);

    @ApiOperation("订单批量查询")
    RpcResponse<List<MainOrderDTO>> batchQueryOrderInfo(OrderBatchQueryRpcReq req);

    /**
     * 根据商家id & 订单状态统计订单数量
     */
    RpcResponse<List<OrderStatisticsDTO>> statisticsBySellerIds(OrderStatisticsQueryRpcReq req);

    /**
     * 批量根据商家id & 统计订单数量
     */
    RpcResponse<List<OrderStatisticsDTO>> statisticsBySeller(OrderStatisticsQueryRpcReq req);
    /**
     * 统计item和skuQuote两个维度的总销量，近30天销量
     */
    RpcResponse<OrderSalesStatisticsDTO> statisticsOrderSales(OrderSalesStatisticsQueryRpcReq req);

    /**
     * 查询统计信息
     * @param req
     * @return
     */
    RpcResponse<List<OrderSummaryStatisticsDTO>> queryOrderSummaryList(OrderSummaryQueryRpcReq req);

}
