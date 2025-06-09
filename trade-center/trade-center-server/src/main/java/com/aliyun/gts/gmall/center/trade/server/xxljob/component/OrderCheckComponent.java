package com.aliyun.gts.gmall.center.trade.server.xxljob.component;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.center.trade.server.flow.handler.confirmPay.ConfirmPayHandlerCenter;
import com.aliyun.gts.gmall.framework.api.flow.dto.FlowNodeResult;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.input.EPayTokenRpcReq;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.output.EpayTransactionStatusDTO;
import com.aliyun.gts.gmall.platform.open.customized.api.facade.EPaymentFacade;
import com.aliyun.gts.gmall.platform.pay.api.dto.output.TcConfirmPayRecordDTO;
import com.aliyun.gts.gmall.platform.pay.api.enums.EpayStatusEnum;
import com.aliyun.gts.gmall.platform.pay.api.facade.TcConfirmPayFacade;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.cart.AddCartRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.ConfirmPayCheckRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.pay.ConfirmingPayCheckRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.ConfirmingPayCheckRpcResp;
import com.aliyun.gts.gmall.platform.trade.api.facade.cart.CartWriteFacade;
import com.aliyun.gts.gmall.platform.trade.api.facade.pay.OrderPayReadFacade;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
import com.aliyun.gts.gmall.platform.trade.core.component.OrderStatusChangeFlowComponent;
import com.aliyun.gts.gmall.platform.trade.core.config.WorkflowProperties;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeOperateEnum;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CreateCustomerCardInfoCommand;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerBankCardByCardIdQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerBankCardInfoDTO;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerBankCardInfoReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerBankCardInfoWriteFacade;
import com.xxl.job.core.context.XxlJobHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class OrderCheckComponent {



    private final Integer authStatus = 2;

    private final Integer partiallyPaidStepNo = 1;

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private EPaymentFacade ePaymentFacade;

    @Autowired
    private ConfirmPayHandlerCenter confirmPayHandlerCenter;

    @Autowired
    private CartWriteFacade cartWriteFacade;

    @Autowired
    private TcConfirmPayFacade tcConfirmPayFacade;
    @Autowired
    private OrderPayReadFacade orderPayReadFacade;
    @Autowired
    private CustomerBankCardInfoReadFacade customerBankCardInfoReadFacade;
    @Autowired
    private CustomerBankCardInfoWriteFacade customerBankCardInfoWriteFacade;

    @Autowired
    private OrderStatusChangeFlowComponent orderStatusChangeFlowComponent;

    @Autowired
    protected WorkflowProperties workflowProperties;



    public void shardingExecute(List<TcConfirmPayRecordDTO> dataList, int shardIndex, int shardTotal, boolean isIn15Min) {
        XxlJobHelper.log("Start executing sharding tasks，Current shard={}, Total number of shards={}", shardIndex, shardTotal);
        Integer size = dataList.size();
        // 计算当前分片应处理的数据范围
        int start = (shardIndex * size) / shardTotal;
        int end = ((shardIndex + 1) * size) / shardTotal;
        // 处理当前分片的数据
        for (int i = start; i < end; i++) {
            TcConfirmPayRecordDTO tcConfirmPayRecordDTO = dataList.get(i);
            log.info("Start processing data: {}", JSONObject.toJSONString(tcConfirmPayRecordDTO));
            try {
                doSingleExecute(tcConfirmPayRecordDTO, isIn15Min);
            }
            catch (Exception e) {
                log.error("Handling data exception: {}", JSONObject.toJSONString(tcConfirmPayRecordDTO), e);
            }
        }
    }

    private void doSingleExecute(TcConfirmPayRecordDTO tcConfirmPayRecordDTO, boolean isIn15Min) {
        // 查询获取支付单的 cartId
        Long cartId = tcConfirmPayRecordDTO.getCartId();
        Long custId = tcConfirmPayRecordDTO.getCustId();
        String primaryOrderIdStr = tcConfirmPayRecordDTO.getPrimaryOrderId();
        List<MainOrder> mainOrderList = getMainOrderList(primaryOrderIdStr);
        int noConfirmed = getIsAllPaymentConfirmed(mainOrderList);
        if (noConfirmed == -1) {
            deleteConfirmPayRecord(tcConfirmPayRecordDTO);
            return;
        }
        // 是否预售订单
        boolean isPreOrder = tcConfirmPayRecordDTO.isPresale();
        MainOrder bOrder = mainOrderList.get(noConfirmed);//bad
        EpayTransactionStatusDTO epayTransactionStatusDTO = getEpayStatus(cartId);
        if (null == epayTransactionStatusDTO) {
            log.error(" OrderCheckXxlJob epay payment status is null", epayTransactionStatusDTO);
            return;
        }
//        this.saveEpayCard(epayTransactionStatusDTO);
        if (isIn15Min) {
            if(EpayStatusEnum.AUTH.getCode().equals(epayTransactionStatusDTO.getStatusName())){
                for (MainOrder mainOrder : mainOrderList) {
                    if (OrderStatusEnum.WAITING_FOR_PAYMENT.getCode() == mainOrder.getPrimaryOrderStatus()
                            || OrderStatusEnum.PARTIALLY_PAID.getCode() == bOrder.getPrimaryOrderStatus()){
                        //paymentConfirming(mainOrder,custId);
                    }
                }
            } else if (EpayStatusEnum.CHARGE.getCode().equals(epayTransactionStatusDTO.getStatusName())) {
                // 待支付改为支付 PAYMENT_CONFIRMED
                if (OrderStatusEnum.WAITING_FOR_PAYMENT.getCode() == bOrder.getPrimaryOrderStatus()
                        || OrderStatusEnum.PARTIALLY_PAID.getCode() == bOrder.getPrimaryOrderStatus()
                        || OrderStatusEnum.PAYMENT_CONFIRMING.getCode() == bOrder.getPrimaryOrderStatus()
                ) {
                    ConfirmPayCheckRpcReq req = new ConfirmPayCheckRpcReq();
                    req.setCustId(custId);
                    req.setCartId(cartId);
                    req.setOutFlowNo(epayTransactionStatusDTO.getId());
                    req.setPrimaryOrderId(Long.valueOf(tcConfirmPayRecordDTO.getPrimaryOrderId()));
                    boolean success = confirmPayHandlerCenter.handleConfirmPay(req);
                    if (success) {
                        updateConfirmPayRecordStatus(tcConfirmPayRecordDTO);
                    }
                }
            }else{
                deleteConfirmPayRecord(tcConfirmPayRecordDTO);
            }
        }else {
            if (EpayStatusEnum.CHARGE.getCode().equals(epayTransactionStatusDTO.getStatusName())) {
                deleteConfirmPayRecord(tcConfirmPayRecordDTO);
            }else {
                // 取消订单
                if (isPreOrder) {
                    // 定金支付 取消订单
                    if (partiallyPaidStepNo == tcConfirmPayRecordDTO.getStepNo()) {
                        // 订单取消
                        cancelOrder(mainOrderList, custId, tcConfirmPayRecordDTO);
                    }else {
                        // 尾款支付 回退到 定金支付

                    }
                }else {
                    // 订单取消
                    cancelOrder(mainOrderList, custId, tcConfirmPayRecordDTO);
                }

            }
        }
    }

    private void saveEpayCard(EpayTransactionStatusDTO statusDTO) {
        log.info("=============xxljobsavecard=======================");
        log.info(JSONObject.toJSONString(statusDTO));
        log.info("====================================");
        try {
            String accountId = statusDTO.getAccountID();
            String cardId = statusDTO.getCardID();
            String cardMask = statusDTO.getCardMask();
            String reference = statusDTO.getReference();
            String terminal = statusDTO.getTerminal();
            String cardType = statusDTO.getCardType();
            String bankCardType = statusDTO.getIssuer();
            if (StringUtils.isNotEmpty(cardId)) {
                // 要先查询到卡没有 在保存
                CustomerBankCardByCardIdQuery customerBankCardByCardIdQuery = new CustomerBankCardByCardIdQuery();
                customerBankCardByCardIdQuery.setCardId(cardId);
                RpcResponse<List<CustomerBankCardInfoDTO>> rpcResponse = customerBankCardInfoReadFacade.queryByCardId(customerBankCardByCardIdQuery);
                log.info("-----------------------------------");
                if (rpcResponse.isSuccess() && CollectionUtils.isEmpty(rpcResponse.getData())) {
                    CreateCustomerCardInfoCommand createCustomerCardInfoCommand = new CreateCustomerCardInfoCommand();
                    createCustomerCardInfoCommand.setAccountId(accountId);
                    createCustomerCardInfoCommand.setCardId(cardId);
                    createCustomerCardInfoCommand.setCardMask(cardMask);
                    createCustomerCardInfoCommand.setTerminal(terminal);
                    createCustomerCardInfoCommand.setReference(reference);
                    createCustomerCardInfoCommand.setType(cardType);
                    createCustomerCardInfoCommand.setBankCardType(bankCardType);
                    createCustomerCardInfoCommand.setActive(1);
                    //createCustomerCardInfoCommand.setCardName(card.getCardName());
                    //createCustomerCardInfoCommand.setForeign(card.getForeign() ? 0 : 1);
                    //createCustomerCardInfoCommand.setMerchantId(card.getMerchantID());
                    //createCustomerCardInfoCommand.setOpenwayId(card.getOpenwayID());
                    //createCustomerCardInfoCommand.setType(card.getType());
                    //createCustomerCardInfoCommand.setPayerName(card.getPayerName());
                    RpcResponse<CustomerBankCardInfoDTO> customerBankCardInfoDTORpcResponse = customerBankCardInfoWriteFacade.create(createCustomerCardInfoCommand);
                    log.info("++++++++++++++++++++++++++++++++");
                    log.info(customerBankCardInfoDTORpcResponse.getData().toString());
                }
            }
        }
        catch (Exception e) {
            log.error("confirmPay failed", e);
        }

    }

    private int getIsAllPaymentConfirmed(List<MainOrder> mainOrderList) {
        int isAllPaymentConfirmed = -1;
        for (int i = 0; i < mainOrderList.size(); i++) {
            MainOrder mainOrder = mainOrderList.get(i);
            if (OrderStatusEnum.PAYMENT_CONFIRMED.getCode()!= mainOrder.getPrimaryOrderStatus()) {
                isAllPaymentConfirmed = i;
            }
        }
        return isAllPaymentConfirmed;
    }

    private void deleteConfirmPayRecord(TcConfirmPayRecordDTO tcConfirmPayRecordDTO) {
        tcConfirmPayFacade.delete(tcConfirmPayRecordDTO);
    }

    private void updateConfirmPayRecordStatus(TcConfirmPayRecordDTO tcConfirmPayRecordDTO) {
        TcConfirmPayRecordDTO updateConfirmPayRecordDTO = new TcConfirmPayRecordDTO();
        updateConfirmPayRecordDTO.setId(tcConfirmPayRecordDTO.getId());
        updateConfirmPayRecordDTO.setStatus(authStatus);
        tcConfirmPayFacade.update(updateConfirmPayRecordDTO);
    }

    private boolean paymentConfirming(MainOrder mainOrder, Long custId) {
        try {

            OrderStatus orderStatus = new OrderStatus();
            orderStatus.setStatus(OrderStatusEnum.PAYMENT_CONFIRMING);
            orderStatus.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
            orderStatus.setCheckStatus(OrderStatusEnum.WAITING_FOR_PAYMENT);
            ConfirmingPayCheckRpcReq confirmingPayCheckRpcReq = new ConfirmingPayCheckRpcReq();
            confirmingPayCheckRpcReq.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
            confirmingPayCheckRpcReq.setCustId(custId);
            RpcResponse<ConfirmingPayCheckRpcResp> result = orderPayReadFacade.confirmingPay(confirmingPayCheckRpcReq);
//            FlowNodeResult result = orderStatusChangeFlowComponent.changeOrderStatus(workflowProperties.getConfirmingPay(),
//                    BizCodeEntity.buildWithDefaultBizCode(mainOrder), orderStatus, OrderChangeOperateEnum.CUST_PAY);
            log.info("============================================");
            log.info("OrderCheckXxlJob.paymentConfirming result:", JSONObject.toJSONString(result.getData()));
            log.info("============================================");
            if (result.isSuccess()) {
                return true;
            }
        }catch (Exception e){
            log.error("OrderCheckXxlJob.paymentConfirming error:", JSONObject.toJSONString(e.getMessage()+e.getCause()));
        }
        return false;
    }
    private boolean systemCalledOrder(MainOrder mainOrder, Long custId) {
//        OrderStatus orderStatus = new OrderStatus();
//        orderStatus.setStatus(OrderStatusEnum.CANCELLED);
//        orderStatus.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
//        orderStatus.setCheckStatus(OrderStatusEnum.PAYMENT_CONFIRMED);
//        FlowNodeResult result = orderStatusChangeFlowComponent.changeOrderStatus(workflowProperties.getOrderSystemClose(),
//                BizCodeEntity.buildWithDefaultBizCode(mainOrder), orderStatus, OrderChangeOperateEnum.SYS_CANCEL);
//        if (result.isSuccess()) {
//            addCarts(mainOrder, custId);
//        }else {
//            log.error("OrderCheckXxlJob.system.cancelOrder return occurred exceptions!", JSONObject.toJSONString(result));
//        }
//        return result.isSuccess();
        return true;
    }

    private void addCarts(MainOrder mainOrder, Long custId) {
        for (SubOrder subOrder : mainOrder.getSubOrders()){
            ItemSku itemSku = subOrder.getItemSku();
            AddCartRpcReq cartRpcReq = new AddCartRpcReq();
            cartRpcReq.setCustId(custId);
            cartRpcReq.setSellerId(subOrder.getSellerId());
            cartRpcReq.setItemId(itemSku.getItemId());
            cartRpcReq.setItemQty(subOrder.getOrderQty());
            cartRpcReq.setSkuId(itemSku.getSkuId());
            cartRpcReq.setPayMode("epay");
            cartWriteFacade.addCart(cartRpcReq);
        }
    }

    private boolean getIsPreOrder(List<MainOrder> mainOrderList) {
        return mainOrderList.size() == 1 && CollectionUtils.isNotEmpty(mainOrderList.get(0).getStepOrders());
    }


    private boolean getPaymentConfirmed(String epayStatus) {
        return EpayStatusEnum.CHARGE.getCode().equals(epayStatus);
    }

    private EpayTransactionStatusDTO getEpayStatus(Long cartId) {
        EPayTokenRpcReq ePayTokenRpcReq = new EPayTokenRpcReq();
        ePayTokenRpcReq.setInvoiceID(String.valueOf(cartId));
        RpcResponse<EpayTransactionStatusDTO> response  = ePaymentFacade.getInvoiceIdStatu(ePayTokenRpcReq);
        return response.getData();
    }


    /**
     * 判断是否要取消订单
     *
     * @param mainOrderList
     * @param custId
     * @param tcConfirmPayRecordDTO
     * @return
     */
    private void cancelOrder(List<MainOrder> mainOrderList, Long custId, TcConfirmPayRecordDTO tcConfirmPayRecordDTO) {
        List<Long> primaryOrderIds = new ArrayList<>();
        // 用户申请退款，售后删除 支付待确认表
        for (MainOrder mainOrder : mainOrderList) {
//            if (OrderStatusEnum.PAYMENT_CONFIRMED.getCode() == mainOrder.getPrimaryOrderStatus()) {
//                // 系统取消订单
//
//                continue;
//            }
            boolean isSuccess = systemCalledOrder(mainOrder, custId);
            if (isSuccess) {
                primaryOrderIds.add(mainOrder.getPrimaryOrderId());
            }
            //primaryOrderIds.add(mainOrder.getPrimaryOrderId());
        }
        if (mainOrderList.size() == primaryOrderIds.size()) {
            deleteConfirmPayRecord(tcConfirmPayRecordDTO);
        }
    }

    //private boolean judgmentOrderTime(Date createTime) {
    //    Date currentTime = new Date();
    //    long diff = Math.abs(currentTime.getTime() - createTime.getTime());
    //    long diffMinutes = diff / (60 * 1000);
    //    return confirmPayTime > diffMinutes;
    //}


    private List<MainOrder> getMainOrderList(String primaryOrderIdStr) {
        List<String> list = Arrays.stream(primaryOrderIdStr.split(",")).toList();
        List<MainOrder> mainOrderList = new ArrayList<>();
        for (String primaryOrderId : list) {
            MainOrder mainOrder = orderQueryAbility.getMainOrder(Long.parseLong(primaryOrderId));
            if (mainOrder != null) {
                mainOrderList.add(mainOrder);
            }
        }
        return mainOrderList;
    }


}
