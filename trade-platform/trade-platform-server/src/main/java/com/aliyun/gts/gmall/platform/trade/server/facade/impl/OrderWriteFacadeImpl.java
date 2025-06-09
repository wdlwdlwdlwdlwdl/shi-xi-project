package com.aliyun.gts.gmall.platform.trade.server.facade.impl;

import com.alibaba.nacos.api.utils.StringUtils;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.aliyun.gts.gmall.framework.api.flow.dto.FlowNodeResult;
import com.aliyun.gts.gmall.framework.api.log.sdk.OpLogCommonUtil;
import com.aliyun.gts.gmall.framework.api.log.sdk.consts.AppIdConstants;
import com.aliyun.gts.gmall.framework.api.log.sdk.consts.BizTypeConstants;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.processengine.WorkflowEngine;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.middleware.api.cache.lock.DistributedLock;
import com.aliyun.gts.gmall.platform.gim.api.dto.input.ImCommonMessageRequest;
import com.aliyun.gts.gmall.platform.gim.api.enums.ImTemplateTypeEnum;
import com.aliyun.gts.gmall.platform.gim.api.facade.ImManualFacade;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.input.CallDeliveryReq;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.output.CallDeliveryResp;
import com.aliyun.gts.gmall.platform.open.customized.api.facade.DeliveryFacade;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonConstant;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.logistics.TcLogisticsRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateCheckOutOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.extend.OrderExtraSaveRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.operate.*;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.CheckOutOrderResultDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.create.CreateOrderResultDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.OrderStatisticsDTO;

import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderWriteFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.*;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderPushAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderRefundAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderTaskAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.OrderMessageAbility;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.component.OrderStatusChangeFlowComponent;
import com.aliyun.gts.gmall.platform.trade.core.config.WorkflowProperties;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderChangedNotifyService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderExtraService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderSynService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.StepOrderDomainService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.impl.OrderChangedNotifyServiceImpl;
import com.aliyun.gts.gmall.platform.trade.core.message.sender.ImSendManager;
import com.aliyun.gts.gmall.platform.trade.core.util.CodeUtils;
import com.aliyun.gts.gmall.platform.trade.core.util.RpcServerUtils;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcLogisticsDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderOperateFlowDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderSummaryDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.SalesInfoDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.*;
import com.aliyun.gts.gmall.platform.trade.domain.repository.*;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcSumOrderMapper;
import com.aliyun.gts.gmall.platform.trade.server.converter.TcLogisticsConverter;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCheckOutCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OrderWriteFacadeImpl implements OrderWriteFacade {

    @Autowired
    private TcLogisticsRepository tcLogisticsRepository;

    @Autowired
    private TcLogisticsConverter tcLogisticsConverter;

    @Autowired
    private TcOrderRepository tcOrderRepository;

    @Autowired
    private WorkflowEngine workflowEngine;

    @Autowired
    private WorkflowProperties workflowProperties;

    @Autowired
    private OrderStatusChangeFlowComponent orderStatusChangeFlowComponent;

    @Autowired
    private OrderExtraService orderExtraService;

    @Autowired
    private StepOrderDomainService stepOrderDomainService;

    @Autowired
    private OrderSynService orderSynService;

    @Autowired
    private ImManualFacade imManualFacade;

    @Autowired
    private OrderPayRepository orderPayRepository;

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private TcOrderOperateFlowRepository tcOrderOperateFlowRepository;

    @Autowired
    private OrderTaskAbility orderTaskAbility;

    @Autowired
    private DeliveryFacade deliveryFacade;

    @Autowired
    private ImSendManager imSendManager;

    @Autowired
    private OrderChangedNotifyService orderChangedNotifyService;

    @Autowired
    private OrderMessageAbility orderMessageAbility;

    @Autowired
    private OrderPushAbility orderPushAbility;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private TcSumOrderMapper tcSumOrderMapper;

    @Value("${order.push.detailUrl:}")
    private String detailUrl;
    @Value("${order.push.evaluateUrl:}")
    private String evaluateUrl;
    @Value("${order.push.cartUrl:}")
    private String cartUrl;
    @Value("${delivery.switch:1}")
    private String deliverySwitch;
    @Autowired
    private OrderRefundAbility orderRefundAbility;

    private final static String IS_DC = "2";
    private final static Integer EVOUCHER = 2;
    private final static String SMS_CODE = "send_sms_verification_code";

    @Override
    public RpcResponse<CreateOrderResultDTO> createOrder(CreateOrderRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            TOrderCreate ord = new TOrderCreate();
            ord.setReq(req);
            ord.setDomain(new CreatingOrder());
            workflowEngine.invokeAndGetResult(workflowProperties.getOrderCreate(), ord.toWorkflowContext());
            RpcResponse<CreateOrderResultDTO> resp = ord.getResponse();
            //创建订单埋点日志
            OpLogCommonUtil.addWithoutDiff(
                resp.isSuccess(),
                req.getClientInfo(),
                AppIdConstants.TRADE_CENTER,
                BizTypeConstants.CREATE_ORDER,
                req.getCustId().toString(),
                req.getCustId().toString()
            );
            return resp;
        }, "OrderWriteFacadeImpl.createOrder", BizCodeEntity.buildByReq(req));
    }

    /**
     * 创建结算订单， 如果最终下单 则删除 不下单 则记录下
     *    由于不是最终下单计算，这里重新拆单 计算价格，就做订单而已
     * @param createCheckOutOrderRpcReq
     * @return CreateOrderResultDTO
     */
    @ApiOperation("创建订单")
    @Override
    public  RpcResponse<CheckOutOrderResultDTO> createCheckOutOrder(CreateCheckOutOrderRpcReq createCheckOutOrderRpcReq) {
        return RpcServerUtils.invoke(() -> {
            TOrderCheckOutCreate ord = new TOrderCheckOutCreate();
            ord.setReq(createCheckOutOrderRpcReq);
            ord.setDomain(new CheckOutCreatingOrder());
            workflowEngine.invokeAndGetResult(workflowProperties.getOrderCheckOutCreate(), ord.toWorkflowContext());
            RpcResponse<CheckOutOrderResultDTO> resp = ord.getResponse();
            return resp;
        }, "OrderWriteFacadeImpl.createOrder", BizCodeEntity.buildByReq(createCheckOutOrderRpcReq));
    }

    /**
     * 创建订单 新版本
     *    创建订单全链路逻辑
     *    不再使用确认订单的计算数据 所有场景数据全部重新计算
     *    下单接口直接支付 ， 支付成功保存订单 ，支付失败不保存
     *
     * @param createOrderRpcReq
     * @return CreateOrderResultDTO
     */
    @ApiOperation("创建订单")
    @Override
    public  RpcResponse<CreateOrderResultDTO> createOrderNew(CreateOrderRpcReq createOrderRpcReq) {
        return RpcServerUtils.invoke(() -> {
            TOrderCreate tOrderCreate = new TOrderCreate();
            tOrderCreate.setReq(createOrderRpcReq);
            tOrderCreate.setDomain(new CreatingOrder());
            workflowEngine.invokeAndGetResult(workflowProperties.getOrderCreateNew(), tOrderCreate.toWorkflowContext());
            RpcResponse<CreateOrderResultDTO> resp = tOrderCreate.getResponse();
            return resp;
        }, "OrderWriteFacadeImpl.createOrder", BizCodeEntity.buildByReq(createOrderRpcReq));
    }

    @Override
    public RpcResponse updateReceiverInfo(UpdateRecevierInfoRpcReq req) {
        return RpcResponse.ok(null);
    }

    @Override
    public RpcResponse sendOrder(TcLogisticsRpcReq tcLogisticsRpcReq) {

        BizCodeEntity bizCodeEntity = BizCodeEntity.buildByReq(tcLogisticsRpcReq);
        Map<String, Object> context = new HashMap<>();
        context.put("tcLogisticsRpcReq", tcLogisticsRpcReq);
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setPrimaryOrderId(tcLogisticsRpcReq.getPrimaryOrderId());
        orderStatus.setStatus(OrderStatusEnum.ORDER_SENDED);
        orderStatus.setCheckStatus(OrderStatusEnum.ORDER_WAIT_DELIVERY);
        orderStatus.setSellerId(tcLogisticsRpcReq.getSellerId());
        context.put("orderStatus", orderStatus);
        context.put("op", OrderChangeOperateEnum.SELLER_SEND);

        return RpcServerUtils.invoke(() -> {
                    FlowNodeResult result = (FlowNodeResult) workflowEngine.invokeAndGetResult(workflowProperties.getOrderSend(), context);
                    //发货日志
                    OpLogCommonUtil.update(result.isSuccess(), tcLogisticsRpcReq.getClientInfo(), AppIdConstants.TRADE_CENTER,
                            BizTypeConstants.ORDER_SEND, tcLogisticsRpcReq.getPrimaryOrderId().toString());
                    if (!result.isSuccess()) {
                        log.error("TcLogisticsWriteFacadeImpl.createTcLogistics return occurred exceptions!");
                        return RpcResponse.fail(result.getFail());
                    }
                    return RpcResponse.ok(null);
                },
                "OrderWriteFacadeImpl.sendOrder", bizCodeEntity);
    }

    @Override
    public RpcResponse cancelOrder(PrimaryOrderRpcReq req) {
        TcOrderDO dbOrderDO = tcOrderRepository.queryPrimaryByOrderId(req.getPrimaryOrderId());
        // 判空 否则可能会空指针
        if (Objects.isNull(dbOrderDO)) {
            throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
        }
        //客户取消 根据不同状态判断
        //CREATED PARTIALLY_PAID PAYMENT_CONFIRMED
        Map<String, String> extra = req.getExtra();
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setExtra(extra);
        orderStatus.setCustId(req.getCustId());
        orderStatus.setStatus(OrderStatusEnum.CANCELLED);
        orderStatus.setPrimaryOrderId(req.getPrimaryOrderId());
        orderStatus.setCheckStatus(OrderStatusEnum.codeOf(dbOrderDO.getOrderStatus()));
        orderStatus.setReasonCode(req.getReasonCode());
        return RpcServerUtils.invoke(() -> {
            FlowNodeResult result = orderStatusChangeFlowComponent.changeOrderStatus(
                workflowProperties.getOrderCustomerCancel(),
                BizCodeEntity.buildByReq(req),
                orderStatus,
                OrderChangeOperateEnum.CUST_CANCEL
            );
            //取消订单行为日志
            OpLogCommonUtil.update(
                result.isSuccess(),
                req.getClientInfo(),
                AppIdConstants.TRADE_CENTER,
                BizTypeConstants.CANCEL_ORDER,
                req.getPrimaryOrderId().toString()
            );
            if (!result.isSuccess()) {
                log.error("TcLogisticsWriteFacadeImpl.cancelOrder return occurred exceptions!");
                return RpcResponse.fail(result.getFail());
            }
            return RpcResponse.ok(null);
        }, "OrderWriteFacadeImpl.cancelOrder");
    }

    @Override
    public RpcResponse deleteOrderByCust(PrimaryOrderRpcReq req) {
        BizCodeEntity bizCodeEntity = BizCodeEntity.buildByReq(req);
        Map<String, Object> context = new HashMap<>();
        context.put("primaryId", req.getPrimaryOrderId());
        context.put("custId", req.getCustId());

        return RpcServerUtils.invoke(() -> {
            FlowNodeResult result = (FlowNodeResult) workflowEngine.invokeAndGetResult(workflowProperties.getOrderCustomerDel(), context);
            if (!result.isSuccess()) {
                log.error("TcLogisticsWriteFacadeImpl.custDeleteOrder return occurred exceptions!");
                return RpcResponse.fail(result.getFail());
            }
            return RpcResponse.ok(null);
        }, "OrderWriteFacadeImpl.deleteOrderByCust", bizCodeEntity);
    }

    @Override
    public RpcResponse confirmReceiveOrder(PrimaryOrderRpcReq req) {
        Map<String, String> extra = req.getExtra();

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setPrimaryOrderId(req.getPrimaryOrderId());
        orderStatus.setStatus(OrderStatusEnum.ORDER_CONFIRM);
        orderStatus.setCheckStatus(OrderStatusEnum.ORDER_SENDED);
        orderStatus.setCustId(req.getCustId());

        return RpcServerUtils.invoke(() -> {

            FlowNodeResult result = orderStatusChangeFlowComponent.changeOrderStatus(
                    workflowProperties.getOrderConfirmReceive(),
                    BizCodeEntity.buildByReq(req), orderStatus, OrderChangeOperateEnum.CUST_CONFIRM_RECEIVE);

            //取消订单行为日志
            OpLogCommonUtil.update(result.isSuccess(), req.getClientInfo(), AppIdConstants.TRADE_CENTER,
                    BizTypeConstants.CONFIRM_ORDER, req.getPrimaryOrderId().toString());

            if (!result.isSuccess()) {
                log.error("TcLogisticsWriteFacadeImpl.confirmOrder return occurred exceptions!");
                return RpcResponse.fail(result.getFail());
            }
            return RpcResponse.ok(null);
        }, "OrderWriteFacadeImpl.confirmReceiveOrder");
    }

    @Override
    public RpcResponse closeOrder(PrimaryOrderRpcReq req) {
        Map<String, String> extra = req.getExtra();
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setPrimaryOrderId(req.getPrimaryOrderId());
        orderStatus.setStatus(OrderStatusEnum.CANCELLED);
        orderStatus.setCheckStatus(OrderStatusEnum.ORDER_WAIT_PAY);
        orderStatus.setSellerId(req.getSellerId());
        orderStatus.setCustId(req.getCustId());

        return RpcServerUtils.invoke(() -> {
            FlowNodeResult result = orderStatusChangeFlowComponent.changeOrderStatus(
                    workflowProperties.getOrderSellerClose(),
                    BizCodeEntity.buildByReq(req), orderStatus, OrderChangeOperateEnum.SELLER_CANCEL);
            if (!result.isSuccess()) {
                log.error("TcLogisticsWriteFacadeImpl.closeOrder return occurred exceptions!");
                return RpcResponse.fail(result.getFail());
            }
            return RpcResponse.ok(null);
        }, "OrderWriteFacadeImpl.closeOrder");
    }

    @Override
    public RpcResponse memoOrderBySeller(SellerMemoWriteRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            TcOrderDO dbOrderDO = tcOrderRepository.queryPrimaryByOrderId(req.getPrimaryOrderId());
            if (!dbOrderDO.getSellerId().equals(req.getSellerId())) {
                return RpcResponse.fail(OrderErrorCode.ORDER_USER_NOT_MATCH.getCode(),
                        OrderErrorCode.ORDER_USER_NOT_MATCH.getMessage());
            }
            OrderAttrDO orderAttrDO = dbOrderDO.getOrderAttr();
            if (orderAttrDO == null) {
                orderAttrDO = new OrderAttrDO();
            }
            orderAttrDO.setSellerMemo(req.getMemo());

            updateOrderAttr(orderAttrDO,req.getPrimaryOrderId(),dbOrderDO.getVersion());
            return RpcResponse.ok(null);
        }, "OrderWriteFacadeImpl.memoOrderBySeller");
    }


    @Override
    @Transactional
    public RpcResponse sellerConfirm(SellerConfirmWriteRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            TcOrderDO mainOrder = tcOrderRepository.queryPrimaryByOrderId(req.getPrimaryOrderId());
            // 判空 否则可能会空指针
            if (Objects.isNull(mainOrder)) {
                throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
            }
            if((!OrderStatusEnum.ACCEPTED_BY_MERCHANT.getCode().equals(mainOrder.getOrderStatus()))
                    &&!OrderStatusEnum.PAYMENT_CONFIRMED.getCode().equals(mainOrder.getOrderStatus())){
                throw new GmallException(OrderErrorCode.ORDER_STATUS_ERROR);
            }
            // 必须是卖家的订单
            if (Objects.nonNull(req.getSellerId()) &&
                !Objects.equals(req.getSellerId(), mainOrder.getSellerId())) {
                return RpcResponse.fail(CommonErrorCode.NOT_DATA_OWNER);
            }
            // 确认接收
            if(Boolean.TRUE.equals(req.getConfirm())){
                // 查询订单
                MainOrder order = orderQueryAbility.getMainOrder(req.getPrimaryOrderId());
                //生成物流信息
                TcLogisticsDO  tcLogisticsDO = new TcLogisticsDO();
                tcLogisticsDO.setPrimaryOrderId(order.getPrimaryOrderId());
                tcLogisticsDO.setCustId(order.getCustomer().getCustId());
                tcLogisticsDO.setSellerId(order.getSeller().getSellerId());
                tcLogisticsDO.setReceiverAddr(order.getReceiver().getDeliveryAddr());
                tcLogisticsDO.setReceiverName(order.getReceiver().getReceiverName());
                tcLogisticsDO.setReceiverPhone(order.getReceiver().getPhone());
                tcLogisticsDO.setType(order.orderAttr().getLogisticsType());
                tcLogisticsDO.setGmtCreate(new Date());
                tcLogisticsDO.setGmtModified(new Date());
                SalesInfoDO salesInfoDO = new SalesInfoDO();
                // 自有物流或者自提
                if(Objects.equals(order.orderAttr().getLogisticsType(), LogisticsTypeEnum.COURIER_LODOOR.getCode()) ||
                    Objects.equals(order.orderAttr().getLogisticsType(),LogisticsTypeEnum.POINT_LODOOR.getCode())){
                    //商家物流生成OTP HM物流回调生成OTP
                    tcLogisticsDO.setOtpCode(CodeUtils.number(4));
                } else {
                    //临时DC配置数据
                    extracted(req);
                    //后续成熟了就可以去掉
                    if (deliverySwitch.equals("0")) {
                        //商家确认接单同步物流
                        orderSynService.deliveryConfirm(order, req);
                    }
                    //添加发货地址
                    if(req.getWarehouse() != null) {
                        salesInfoDO.setCity(req.getWarehouse().getCity());
                        salesInfoDO.setCityCode(req.getWarehouse().getCityCode());
                        salesInfoDO.setPostCode(req.getWarehouse().getPostalCode());
                        salesInfoDO.setDeliveryAddr(req.getWarehouse().getAddress());
                        salesInfoDO.setPhone(req.getWarehouse().getTelephone());
                        if(req.getWarehouse().getLatitude() != null){
                            salesInfoDO.setLatitude(String.valueOf(req.getWarehouse().getLatitude()));
                        }
                        if(req.getWarehouse().getLongitude() != null){
                            salesInfoDO.setLongitude(String.valueOf(req.getWarehouse().getLongitude()));
                        }
                        salesInfoDO.setName(req.getWarehouse().getName());
                        salesInfoDO.setCountry(req.getWarehouse().getCountry());
                        salesInfoDO.setSeatNum(req.getWarehouse().getSeatNum());
                        salesInfoDO.setIsDc(req.getWarehouse().getIsDc());
                    }
                }
                // 保存订单物流信息
                tcLogisticsRepository.create(tcLogisticsDO);
                // 更新发货点
                updateSalesInfo(salesInfoDO, req.getPrimaryOrderId(), null);
                // 更新订单状态  变为卖家接受
                tcOrderRepository.updateStatusAndStageByPrimaryId(
                    req.getPrimaryOrderId(),
                    OrderStatusEnum.ACCEPTED_BY_MERCHANT.getCode(),
                    null,
                    OrderStatusEnum.PAYMENT_CONFIRMED.getCode()
                );
                // 再查询一次
                TcOrderDO dbOrderDO = tcOrderRepository.queryPrimaryByOrderId(req.getPrimaryOrderId());
                // 写一条流水
                addFlow(mainOrder,OrderStatusEnum.ACCEPTED_BY_MERCHANT.getCode(), OrderStatusEnum.ACCEPTED_BY_MERCHANT.getInner(),false);
                // 给买家发消息推送PUSH
                sendPush(dbOrderDO);
                // 创建一个定时任务， 到时取消任务
                orderTaskAbility.orderTask(order,OrderStatusEnum.ACCEPTED_BY_MERCHANT.getCode());
                // 订单状态变化消息
                sendMqMessage(req.getPrimaryOrderId(),OrderChangeOperateEnum.ACCEPTED_BY_MERCHANT);
            } else {
                OrderStatus orderStatus = new OrderStatus();
                orderStatus.setPrimaryOrderId(req.getPrimaryOrderId());
                // 查询订单
                MainOrder order = orderQueryAbility.getMainOrder(req.getPrimaryOrderId());
                //PAYMENT_CONFIRMED 商家取消
                if (Objects.equals(mainOrder.getOrderStatus(), OrderStatusEnum.PAYMENT_CONFIRMED.getCode())) {
                    orderStatus.setCheckStatus(OrderStatusEnum.PAYMENT_CONFIRMED);
                }else if(Objects.equals(mainOrder.getOrderStatus(), OrderStatusEnum.ACCEPTED_BY_MERCHANT.getCode())){
                    //ACCEPTED_BY_MERCHANT 商家取消
                    orderStatus.setCheckStatus(OrderStatusEnum.ACCEPTED_BY_MERCHANT);
                }
                orderStatus.setCancelFromStatus(OrderStatusEnum.codeOf(mainOrder.getOrderStatus()));
                orderStatus.setStatus(OrderStatusEnum.CANCEL_REQUESTED);
                //添加取消请求flow
                addFlow(mainOrder,OrderStatusEnum.CANCEL_REQUESTED.getCode(),OrderStatusEnum.CANCEL_REQUESTED.getInner(),false);
                // 修改状态后在查询一次
                TcOrderDO dbOrderDO = tcOrderRepository.queryPrimaryByOrderId(req.getPrimaryOrderId());
                //订单扩展字段
                OrderAttrDO orderAttrDO = Objects.isNull(dbOrderDO.getOrderAttr()) ? new OrderAttrDO() : dbOrderDO.getOrderAttr();
                orderAttrDO.setScr(req.getReasonName());
                orderAttrDO.setReasonCode(req.getReasonCode());
                // 更新扩展字段 将订单取消写进去
                updateOrderAttr(orderAttrDO, req.getPrimaryOrderId(),dbOrderDO.getVersion());
                //首先更新CANCEL_REQUESTED
                tcOrderRepository.updateStatusAndStageByPrimaryId(
                        orderStatus.getPrimaryOrderId(),
                        orderStatus.getStatus().getCode(),
                        orderStatus.getOrderStage(),
                        orderStatus.getCheckStatus().getCode()
                );
                //查询最新状态
                MainOrder mainOrderNeedRefund = orderQueryAbility.getMainOrder(req.getPrimaryOrderId());
                // 退钱
                orderRefundAbility.doRefund(orderStatus,mainOrderNeedRefund);
                tcOrderRepository.reversalStatusSynOrder(
                        mainOrder.getPrimaryOrderId(),
                        mainOrderNeedRefund.getPrimaryOrderStatus(),
                        orderStatus.getStatus().getCode()
                );
                // 退券
                promotionRepository.orderRollbackUserAssets(order);
                TcOrderDO newOrderDO = tcOrderRepository.queryPrimaryByOrderId(req.getPrimaryOrderId());
                // 发送取消PUSH 通知客户
                sendCancelPush(newOrderDO,orderStatus.getCancelFromStatus().getCode());
                // 发订单状态变化消息
                sendMqMessage(req.getPrimaryOrderId(),OrderChangeOperateEnum.CANCELLED);
            }
            return RpcResponse.ok(null);
        },"OrderWriteFacadeImpl.sellerConfirm");
    }

    private static void extracted(SellerConfirmWriteRpcReq req) {
        //临时DC配置数据
        if(req.getWarehouse() != null) {
            if (req.getWarehouse().getIsDc().equals("2")) {
                req.getWarehouse().setCityCode("750000000");
                req.getWarehouse().setCity("Almaty");
                req.getWarehouse().setLatitude(43.230437);
                req.getWarehouse().setLongitude(76.894594);
                req.getWarehouse().setPostalCode("050000");
            }
        }
    }

    /**
     * 发消息
     * @param primaryOrderId
     * @param operateEnum
     */
    protected void sendMqMessage(Long primaryOrderId, OrderChangeOperateEnum operateEnum) {
        MainOrder mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
        OrderChangeNotify change =  OrderChangeNotify.builder()
            .mainOrder(mainOrder)
            .op(operateEnum)
            .build();
        // 发消息
        orderMessageAbility.messageMqSend(change);
    }

    @Override
    public RpcResponse saveOrderExtras(OrderExtraSaveRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            req.checkInput();
            orderExtraService.saveOrderExtras(req);
            return RpcResponse.ok(null);
        }, "OrderWriteFacadeImpl.saveOrderExtras");
    }

    @Override
    public RpcResponse handleStepOrderBySeller(StepOrderHandleRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            req.checkInput();
            stepOrderDomainService.handleStepOrderBySeller(req);
            return RpcResponse.ok(null);
        }, "OrderWriteFacadeImpl.handleStepOrderBySeller");
    }

    @Override
    public RpcResponse confirmStepOrderByCustomer(StepOrderHandleRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            req.checkInput();
            stepOrderDomainService.confirmStepOrderByCustomer(req);
            return RpcResponse.ok(null);
        }, "OrderWriteFacadeImpl.confirmStepOrderByCustomer");
    }

    /**
     * 更新订单扩展字段
     * @param orderAttrDO
     * @param primaryOrderId
     * @param version
     */
    private void updateOrderAttr(OrderAttrDO orderAttrDO,Long primaryOrderId , Long version){
        TcOrderDO tcOrderDO = new TcOrderDO();
        tcOrderDO.setOrderAttr(orderAttrDO);
        tcOrderDO.setPrimaryOrderId(primaryOrderId);
        tcOrderDO.setOrderId(primaryOrderId);
        tcOrderDO.setVersion(version);
        tcOrderDO.setCancelCode(orderAttrDO.getReasonCode());
        tcOrderRepository.updateByOrderIdVersion(tcOrderDO);

    }

    /**
     * 更新卖家的发货点
     * @param salesInfoDO
     * @param primaryOrderId
     * @param version
     */
    private void updateSalesInfo(SalesInfoDO salesInfoDO,Long primaryOrderId , Long version){
        TcOrderDO tcOrderDO = new TcOrderDO();
        tcOrderDO.setSalesInfo(salesInfoDO);
        tcOrderDO.setPrimaryOrderId(primaryOrderId);
        tcOrderDO.setOrderId(primaryOrderId);
//        tcOrderDO.setVersion(version);
        tcOrderRepository.updateByOrderIdVersion(tcOrderDO);
    }

    /**
     * 更新订单状态
     * @param req
     * @return
     */
    @Override
    public RpcResponse updateOrderStatus(PrimaryOrderRpcReq req) {
        // 加锁 单一订单 加锁处理 不能同事处理两个场景
        DistributedLock orderLock = cacheManager.getLock(
            String.format(CommonConstant.ORDER_LOCK_KEY, req.getPrimaryOrderId())
        );
        boolean lock = Boolean.FALSE;
        try {
            lock = orderLock.tryLock(
                CommonConstant.ORDER_TIME_OUT,
                CommonConstant.ORDER_MAX_TIME_OUT,
                TimeUnit.MILLISECONDS
            );
        } catch (Exception e) {
            throw new GmallException(OrderErrorCode.ORDER_LOCKED);
        }
        // 获取锁失败 同一时间 只能处理一个状态
        if (Boolean.FALSE.equals(lock)) {
            throw new GmallException(OrderErrorCode.ORDER_LOCKED);
        }
        try {
            OrderStatus orderStatus = new OrderStatus();
            orderStatus.setPrimaryOrderId(req.getPrimaryOrderId());
            orderStatus.setStatus(OrderStatusEnum.codeOf(req.getOrderStatus()));
            orderStatus.setCheckStatus(req.getStatus());
            //枚举映射
            OrderChangeOperateEnum orderChangeOperateEnum = this.initOp(orderStatus);
            return RpcServerUtils.invoke(() -> {
                FlowNodeResult result = orderStatusChangeFlowComponent.changeOrderStatus(
                    workflowProperties.getDeliveryUpdate(),
                    BizCodeEntity.buildByReq(req),
                    orderStatus,
                    orderChangeOperateEnum
                );
                if (!result.isSuccess()) {
                    log.error("TcLogisticsWriteFacadeImpl.updateOrderStatus return occurred exceptions!");
                    return RpcResponse.fail(result.getFail());
                }
                return RpcResponse.ok(null);
            }, "OrderWriteFacadeImpl.updateOrderStatus");
        } finally {
            orderLock.unLock();
        }
    }

    /**
     * 枚举映射
     * @param orderStatus
     * @return
     */
    public OrderChangeOperateEnum initOp(OrderStatus orderStatus){
        OrderChangeOperateEnum orderChangeOperateEnum = OrderChangeOperateEnum.DELIVERY;
        if(OrderStatusEnum.DELIVERY.getCode().equals(orderStatus.getStatus().getCode())){
            orderChangeOperateEnum = OrderChangeOperateEnum.DELIVERY;
        } else if(OrderStatusEnum.READY_FOR_PICKUP.getCode().equals(orderStatus.getStatus().getCode())){
            orderChangeOperateEnum = OrderChangeOperateEnum.READY_FOR_PICKUP;
        } else if(OrderStatusEnum.RETURNING_TO_MERCHANT.getCode().equals(orderStatus.getStatus().getCode())){
            orderChangeOperateEnum = OrderChangeOperateEnum.RETURNING_TO_MERCHANT;
        } else if(OrderStatusEnum.CANCEL_REQUESTED.getCode().equals(orderStatus.getStatus().getCode())){
            orderChangeOperateEnum = OrderChangeOperateEnum.CANCEL_REQUESTED;
        } else if(OrderStatusEnum.CANCELLED.getCode().equals(orderStatus.getStatus().getCode())) {
            orderChangeOperateEnum = OrderChangeOperateEnum.CANCELLED;
        } else if(OrderStatusEnum.COMPLETED.getCode().equals(orderStatus.getStatus().getCode())){
            orderChangeOperateEnum = OrderChangeOperateEnum.COMPLETED;
        } else {
            orderChangeOperateEnum = OrderChangeOperateEnum.CANCEL_FAILED;
        }
        return orderChangeOperateEnum;
    }

    @Override
    public RpcResponse sendOtp(SellerConfirmWriteRpcReq req) {
        TcLogisticsDO logisticsDO = tcLogisticsRepository.queryLogisticsByPrimaryId(req.getPrimaryOrderId(),null);
        if (logisticsDO != null) {
            MainOrder order = orderQueryAbility.getMainOrder(req.getPrimaryOrderId());
            //调用SMS发送OTP码
            ImCommonMessageRequest ucs = new ImCommonMessageRequest();
            ucs.setCode(SmsTemplateEnum.codeOf(order.getPrimaryOrderStatus()).getName());
            ucs.setReceiver(order.getReceiver().getPhone());
            Map<String, String> replacements = new HashMap<>();
            replacements.put("optCode",logisticsDO.getOtpCode());
            ucs.setTemplateType(ImTemplateTypeEnum.SMS.getCode());
            ucs.setReplacements(replacements);
            RpcResponse<Boolean> rpc = imManualFacade.sendMessage(ucs);
            if(!rpc.isSuccess()){
                return RpcResponse.fail(rpc.getFail());
            }
        }
        return RpcResponse.ok(null);
    }

    /**
     * 确认OTP
     * @param req
     * @return
     */
    @Override
    public RpcResponse confirmOtp(SellerConfirmWriteRpcReq req) {
        if(StringUtils.isEmpty(req.getOtpCode())) {
            throw new GmallException(OrderErrorCode.OPT_CODE_NULL, I18NMessageUtils.getMessage("opt.code.null"));  //OTP码为空
        }
        //判断是否商家物流
        TcLogisticsDO logisticsDO = tcLogisticsRepository.queryLogisticsByPrimaryId(req.getPrimaryOrderId(),null);
        if (Objects.isNull(logisticsDO)) {
            throw new GmallException(OrderErrorCode.TC_LOGISTICS_NULL, I18NMessageUtils.getMessage("tc.logistics.null"));  //查询结果为空
        }
        // OPT正确
        if(logisticsDO.getOtpCode().equals(req.getOtpCode())){
            // 查一下订单
            TcOrderDO dbOrderDO = tcOrderRepository.queryPrimaryByOrderId(req.getPrimaryOrderId());
            //更新确认完成状态 表示订单完成
            tcOrderRepository.updateStatusAndStageByPrimaryId(
                req.getPrimaryOrderId(),
                OrderStatusEnum.COMPLETED.getCode(),
                null,
                OrderStatusEnum.ACCEPTED_BY_MERCHANT.getCode()
            );
            // 写入订单扩展字段
            OrderAttrDO orderAttrDO = Objects.isNull(dbOrderDO.getOrderAttr()) ? new OrderAttrDO() : dbOrderDO.getOrderAttr();
            orderAttrDO.setConfirmReceiveTime(new Date());
            // 更新扩展字段
            updateOrderAttr(orderAttrDO,req.getPrimaryOrderId(),dbOrderDO.getVersion());
            // 写入操作流水 发买家PUSH
            sendMsg(dbOrderDO.getPrimaryOrderId());
        } else {
            //OTP码不匹配
            throw new GmallException(
                OrderErrorCode.OTP_CODE_ERROR,
                I18NMessageUtils.getMessage("otp.code.error")
            );
        }
        return RpcResponse.ok(null);
    }


    /**
     * 发买家PUSH
     * @param primaryOrderId
     */
    private void sendMsg(Long primaryOrderId){
        MainOrder mainOrder = orderQueryAbility.getMainOrder(primaryOrderId);
        List<TcOrderOperateFlowDO> list = Lists.newArrayList(
            OrderChangedNotifyServiceImpl.buildOrderPay(
                mainOrder,
                OrderStatusEnum.ACCEPTED_BY_MERCHANT,
                OrderStatusEnum.COMPLETED
            )
        );
        orderChangedNotifyService.afterStatusChange(
            OrderChangeNotify.builder()
            .mainOrder(mainOrder)
            .flows(list)
            .op(OrderChangeOperateEnum.COMPLETED)
            .build()
        );
    }


    private void sendPush(TcOrderDO dbOrderDO){
        if(Objects.nonNull(PushTemplateEnum.codeOf(dbOrderDO.getPrimaryOrderStatus()))) {
            ImCommonMessageRequest msg = new ImCommonMessageRequest();
            msg.setCode(PushTemplateEnum.codeOf(dbOrderDO.getPrimaryOrderStatus()).getName());
            imSendManager.initParam(dbOrderDO.getCustId(), msg,true);
            Map<String, String> replacements = new HashMap<>();
            MainOrder order = orderQueryAbility.getMainOrder(dbOrderDO.getPrimaryOrderId());
            if (CollectionUtils.isNotEmpty(order.getSubOrders())) {
                replacements.put("imageHeight", "0");//模板默认值
                replacements.put("imageUrl", order.getSubOrders().get(0).getItemSku().getItemPic());
            }
            if(OrderStatusEnum.COMPLETED.getCode().equals(dbOrderDO.getPrimaryOrderStatus())){
                replacements.put("advertiseLink",evaluateUrl+String.valueOf(dbOrderDO.getPrimaryOrderId()));
            }else {
                replacements.put("advertiseLink",detailUrl+String.valueOf(dbOrderDO.getPrimaryOrderId()));
            }
            msg.setTemplateType(ImTemplateTypeEnum.PUSH.getCode());
            msg.setSellerId(order.getSeller().getSellerId());
            msg.setReplacements(replacements);
            imSendManager.sendMessage(msg);
        }
    }

    private void sendCancelPush(TcOrderDO dbOrderDO,int cancelFromStatus){
        if(Objects.nonNull(MerchantCancelTemplateEnum.codeOf(cancelFromStatus))) {
            ImCommonMessageRequest msg = new ImCommonMessageRequest();
            msg.setCode(MerchantCancelTemplateEnum.codeOf(cancelFromStatus).getName());
            imSendManager.initParam(dbOrderDO.getCustId(), msg,true);
            Map<String, String> replacements = new HashMap<>();
            MainOrder order = orderQueryAbility.getMainOrder(dbOrderDO.getPrimaryOrderId());
            if (CollectionUtils.isNotEmpty(order.getSubOrders())) {
                replacements.put("imageHeight", "0");//模板默认值
                replacements.put("imageUrl", order.getSubOrders().get(0).getItemSku().getItemPic());
            }
            replacements.put("advertiseLink",detailUrl+String.valueOf(dbOrderDO.getPrimaryOrderId()));
            msg.setTemplateType(ImTemplateTypeEnum.PUSH.getCode());
            msg.setReplacements(replacements);
            imSendManager.sendMessage(msg);
        }
    }

    /**
     * 完成订单
     * @param req
     * @return
     */
    @Override
    public RpcResponse completeOrder(PrimaryOrderRpcReq req) {
        return RpcServerUtils.invoke(() -> {
            TcOrderDO dbOrderDO = tcOrderRepository.queryPrimaryByOrderId(req.getPrimaryOrderId());
            if (req.getSellerId() != null && !req.getSellerId().equals(dbOrderDO.getSellerId())) {
                return RpcResponse.fail(CommonErrorCode.NOT_DATA_OWNER);
            }

         /*   if(!dbOrderDO.getItemFeature().getItemType().equals(EVOUCHER)){
                return RpcResponse.fail(OrderErrorCode.ITEM_TYPE_NOT_MATCH);
            }*/
            tcOrderRepository.updateStatusAndStageByPrimaryId(
                req.getPrimaryOrderId(),
                OrderStatusEnum.COMPLETED.getCode(),
                null,
                OrderStatusEnum.ACCEPTED_BY_MERCHANT.getCode()
            );
            //入参没sellerId
          /*  if (!dbOrderDO.getSellerId().equals(req.getSellerId())) {
                return RpcResponse.fail(
                    OrderErrorCode.ORDER_USER_NOT_MATCH.getCode(),
                    OrderErrorCode.ORDER_USER_NOT_MATCH.getMessage()
                );
            }*/
            OrderAttrDO orderAttrDO = Objects.isNull(dbOrderDO.getOrderAttr()) ?  new OrderAttrDO() : dbOrderDO.getOrderAttr();
            orderAttrDO.setConfirmReceiveTime(new Date());
            orderAttrDO.setRemark(req.getRemark());
            // 更新订单扩展字段
            updateOrderAttr(orderAttrDO,req.getPrimaryOrderId(),dbOrderDO.getVersion());
            // 发消息
            sendMsg(dbOrderDO.getPrimaryOrderId());
            return RpcResponse.ok(null);
        },"OrderWriteFacadeImpl.completeOrder");
    }

    /**
     * 呼叫快递员
     * @param req
     * @return
     */
    @Override
    public RpcResponse callDelivery(PrimaryOrderRpcReq req) {
        TcOrderDO dbOrderDO = tcOrderRepository.queryPrimaryByOrderId(req.getPrimaryOrderId());
        if (Objects.isNull(dbOrderDO)) {
            throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
        }
        CallDeliveryReq callDeliveryReq = new CallDeliveryReq();
        callDeliveryReq.setOrderNumber(req.getPrimaryOrderId());
        if(dbOrderDO.getSalesInfo() != null){
            callDeliveryReq.setIsDc(dbOrderDO.getSalesInfo().getIsDc());
        }
        RpcResponse<CallDeliveryResp> result = deliveryFacade.callCourier(callDeliveryReq);
        if(result.isSuccess()){
            String opName = "";
            OrderStatus orderStatus = new OrderStatus();
            //原来call逻辑
          /*  orderStatus.setCheckStatus(OrderStatusEnum.ACCEPTED_BY_MERCHANT);
            if(dbOrderDO.getSalesInfo().getIsDc().equals(IS_DC)){
                orderStatus.setStatus(OrderStatusEnum.DELIVERY_TO_DC);
                opName = OrderStatusEnum.DELIVERY_TO_DC.getInner();
             }else {
                orderStatus.setStatus(OrderStatusEnum.WAITING_FOR_COURIER);
                opName = OrderStatusEnum.WAITING_FOR_COURIER.getInner();
            }*/
            //新逻辑
            if(dbOrderDO.getSalesInfo().getIsDc().equals(IS_DC)){
                orderStatus.setStatus(OrderStatusEnum.WAITING_FOR_COURIER);
                orderStatus.setCheckStatus(OrderStatusEnum.DELIVERY_TO_DC);
                opName = OrderStatusEnum.WAITING_FOR_COURIER.getInner();
            }else {
                orderStatus.setStatus(OrderStatusEnum.WAITING_FOR_COURIER);
                orderStatus.setCheckStatus(OrderStatusEnum.ACCEPTED_BY_MERCHANT);
                opName = OrderStatusEnum.WAITING_FOR_COURIER.getInner();
            }
            // 更新订单状态
            tcOrderRepository.updateStatusAndStageByPrimaryId(
                req.getPrimaryOrderId(),
                orderStatus.getStatus().getCode(),
                null,
                orderStatus.getCheckStatus().getCode()
            );
            // 写订单扩展信息
            OrderAttrDO orderAttrDO = Objects.isNull(dbOrderDO.getOrderAttr()) ?
                new OrderAttrDO() : dbOrderDO.getOrderAttr();
            orderAttrDO.setSendTime(new Date());
            orderAttrDO.setRemark(req.getRemark());
            updateOrderAttr(orderAttrDO,req.getPrimaryOrderId(),dbOrderDO.getVersion());
            // 写流水
            addFlow(dbOrderDO, orderStatus.getStatus().getCode(), opName,false);
            // 重新查一下订单
            MainOrder mainOrder = orderQueryAbility.getMainOrder(req.getPrimaryOrderId());
            // 生成自动退单任务
            orderTaskAbility.orderTask(mainOrder,mainOrder.getPrimaryOrderStatus());
            // 给买家发一个PUSH 消息
            orderPushAbility.send(mainOrder,ImTemplateTypeEnum.PUSH.getCode());
            return RpcResponse.ok(null);
        }
        return RpcResponse.fail(OrderErrorCode.ORDER_NOT_EXISTS);
    }

    @Override
    public RpcResponse summaryOrder(List<OrderStatisticsDTO> req) {
        List<TcOrderSummaryDO> list = new ArrayList<>();
        for(OrderStatisticsDTO orderStatisticsDTO :req){
            TcOrderSummaryDO orderSummaryDO = new TcOrderSummaryDO();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            orderSummaryDO.setSellerId(orderStatisticsDTO.getSellerId());
            orderSummaryDO.setStatisticDate(format.format(new Date()));
            orderSummaryDO.setOrderNum(orderStatisticsDTO.getTotalCompleteCountLast30Days());
            orderSummaryDO.setCancelOrderNum(orderStatisticsDTO.getCancelCountLast30Days());
            orderSummaryDO.setCancelRate(calculatePercentage(orderStatisticsDTO.getCancelCountLast30Days(),orderStatisticsDTO.getTotalCompleteCountLast30Days()));
            orderSummaryDO.setCreateTime(new Date());
            list.add(orderSummaryDO);
        }
        tcSumOrderMapper.batchInsert(list);
        return RpcResponse.ok(null);
    }

    private  String calculatePercentage(Long numerator, Long denominator) {
        // 检查分母是否为零，如果为零抛出异常
        if (denominator == 0) {
            //throw new IllegalArgumentException("分母不能为零");
            return "100";
        }
        // 计算百分比
        double percentage = ((double) numerator / denominator) * 100;
        // 将结果格式化为带两位小数的字符串
        return String.format("%.2f", percentage);
    }


    protected void addFlow(TcOrderDO mainOrder, Integer targetStatus, String name, boolean isCustomer) {
        OrderOperateFlowQuery query = new OrderOperateFlowQuery();
        query.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        query.setFromOrderStatus(mainOrder.getPrimaryOrderStatus());
        query.setToOrderStatus(targetStatus);
        if(!tcOrderOperateFlowRepository.exist(query)) {
            TcOrderOperateFlowDO flow = new TcOrderOperateFlowDO();
            flow.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
            flow.setOrderId(mainOrder.getOrderId());
            flow.setFromOrderStatus(mainOrder.getOrderStatus());
            flow.setToOrderStatus(targetStatus);
            flow.setOperatorType(isCustomer ? 1 : 0);
            flow.setGmtCreate(new Date());
            flow.setOpName(name);
            flow.setOperator(String.valueOf(mainOrder.getCustId()));
            flow.setGmtModified(new Date());
            tcOrderOperateFlowRepository.create(flow);
        }
    }

    public static void main(String[] args) {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setPrimaryOrderId(2222L);
        orderStatus.setStatus(OrderStatusEnum.codeOf(68));
        OrderChangeOperateEnum op = OrderChangeOperateEnum.DELIVERY;
        if(OrderStatusEnum.DELIVERY.getCode().equals(orderStatus.getStatus().getCode())){
            op = OrderChangeOperateEnum.DELIVERY;
        }else if(OrderStatusEnum.READY_FOR_PICKUP.getCode().equals(orderStatus.getStatus().getCode())){
            op = OrderChangeOperateEnum.READY_FOR_PICKUP;
        }else if(OrderStatusEnum.RETURNING_TO_MERCHANT.getCode().equals(orderStatus.getStatus().getCode())){
            op = OrderChangeOperateEnum.RETURNING_TO_MERCHANT;
        }else if(OrderStatusEnum.CANCEL_REQUESTED.getCode().equals(orderStatus.getStatus().getCode())){
            op = OrderChangeOperateEnum.CANCEL_REQUESTED;
        }else if(OrderStatusEnum.CANCELLED.getCode().equals(orderStatus.getStatus().getCode())){
            op = OrderChangeOperateEnum.CANCELLED;
        }else {
            op = OrderChangeOperateEnum.CANCEL_FAILED;
        }
        System.out.println(op.getOpName());
    }
}
