package com.aliyun.gts.gmall.platform.trade.api.facade.order;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics.TcLogisticsRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateCheckOutOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.extend.OrderExtraSaveRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate.*;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.CheckOutOrderResultDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.create.CreateOrderResultDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.OrderStatisticsDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

@Api("订单写接口")
public interface OrderWriteFacade {

    @ApiOperation("创建订单")
    RpcResponse<CreateOrderResultDTO> createOrder(CreateOrderRpcReq req);

    /**
     * 创建结算订单， 如果最终下单 则删除 不下单 则记录下
     * @param createCheckOutOrderRpcReq
     * @return CreateOrderResultDTO
     */
    @ApiOperation("创建订单")
    RpcResponse<CheckOutOrderResultDTO> createCheckOutOrder(CreateCheckOutOrderRpcReq createCheckOutOrderRpcReq);

    /**
     * 下订单 新版本
     *    创建订单全链路逻辑
     *
     * @param createOrderRpcReq
     * @return CreateOrderResultDTO
     */
    @ApiOperation("创建订单")
    RpcResponse<CreateOrderResultDTO> createOrderNew(CreateOrderRpcReq createOrderRpcReq);

    @ApiOperation("修改收货信息")
    RpcResponse updateReceiverInfo(UpdateRecevierInfoRpcReq req);

    @ApiOperation("发货")
    RpcResponse sendOrder(TcLogisticsRpcReq tcLogisticsRpcReq);

    @ApiOperation("取消订单")
    RpcResponse cancelOrder(PrimaryOrderRpcReq req);

    @ApiOperation("删除订单")
    RpcResponse deleteOrderByCust(PrimaryOrderRpcReq req);

    @ApiOperation("确认收货")
    RpcResponse confirmReceiveOrder(PrimaryOrderRpcReq req);

    @ApiOperation("关闭订单")
    RpcResponse closeOrder(PrimaryOrderRpcReq req);

    @ApiOperation("卖家备注")
    RpcResponse memoOrderBySeller(SellerMemoWriteRpcReq req);

    @ApiOperation("卖家确认接单")
    RpcResponse sellerConfirm(SellerConfirmWriteRpcReq req);

    @ApiOperation("更新订单扩展属性，包括feature与扩展结构")
    RpcResponse saveOrderExtras(OrderExtraSaveRpcReq req);

    @ApiOperation("多阶段卖家处理")
    RpcResponse handleStepOrderBySeller(StepOrderHandleRpcReq req);

    @ApiOperation("多阶段用户确认")
    RpcResponse confirmStepOrderByCustomer(StepOrderHandleRpcReq req);

    @ApiOperation("更新订单状态")
    RpcResponse updateOrderStatus(PrimaryOrderRpcReq req);

    @ApiOperation("发送OTP")
    RpcResponse sendOtp(SellerConfirmWriteRpcReq req);

    @ApiOperation("确认OTP")
    RpcResponse confirmOtp(SellerConfirmWriteRpcReq req);

    @ApiOperation("完成订单")
    RpcResponse completeOrder(PrimaryOrderRpcReq req);

    @ApiOperation("呼叫快递员")
    RpcResponse callDelivery(PrimaryOrderRpcReq req);

    @ApiOperation("订单统计结果入库")
    RpcResponse summaryOrder(List<OrderStatisticsDTO> req);

}
