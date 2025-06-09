package com.aliyun.gts.gmall.center.trade.core.message.consumer;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.middleware.api.mq.anns.MQConsumer;
import com.aliyun.gts.gmall.middleware.api.mq.consumer.ConsumeEventProcessor;
import com.aliyun.gts.gmall.middleware.api.mq.model.StandardEvent;
import com.aliyun.gts.gmall.platform.trade.api.dto.message.OrderMessageDTO;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * description: 确认收货通知
 *      group：GID_ORDER_CONFIRM_NOTIFY_SALE_PROXY
 *      topic: 原订单状态变更的topic
 *      tag：监听 确认收货和系统确认收货的消息
 * @author hu.zhiyong
 * @date 2022/10/01 20:06
 **/
@Slf4j
@MQConsumer(
    topic = "${trade.order.statuschange.topic}",
    groupId = "${trade.order.statuschange.groupId}",
    tag = "25||27"
)
public class OrderConfirmNotifyConsumer implements ConsumeEventProcessor {

    @Autowired
    private OrderQueryAbility orderQueryAbility;

//    @Autowired
//    private InvoiceFacade invoiceFacade;

    private static final String INVOICE = "invoice";

    @Override
    public boolean process(StandardEvent event) {
        log.info("OrderConfirmNotifiedConsumer event msg = " + JSONObject.toJSONString(event));
        OrderMessageDTO message = (OrderMessageDTO) event.getPayload().getData();
        if (message.getPrimaryOrderId().longValue() != message.getOrderId().longValue()) {
            // 忽略子订单消息
            return true;
        }
        return true;
        //Long primaryOrderId = message.getPrimaryOrderId();
        //MainOrder mainOrder = orderQueryAbility.getMainOrder(
        //    primaryOrderId, OrderQueryOption.builder()
        //    .includeExtends(true).build()
        //);
        //return applyOrderInvoice(mainOrder);
    }

    private Boolean applyOrderInvoice( MainOrder mainOrder) {
        return Boolean.TRUE;
//        try {
//            List<TcOrderExtendDO> invoices = mainOrder.getOrderExtendList()
//                .stream()
//                .filter(item -> StringUtils.equals(INVOICE, item.getExtendKey()) && StringUtils.equals(INVOICE, item.getExtendType()))
//                .collect(Collectors.toList());
//            if (invoices.isEmpty()) {
//                return true;
//            }
//            String extendValue = invoices.get(0).getExtendValue();
//            JSONObject jsonObject = JSON.parseObject(extendValue);
//            Long id = jsonObject.getLong("id");
//            Long primaryOrderId = mainOrder.getPrimaryOrderId();
//            InvoiceApplyRpcReq invoiceApplyRpcReq = new InvoiceApplyRpcReq();
//            invoiceApplyRpcReq.setTitleId(id);
//            invoiceApplyRpcReq.setPrimaryOrderId(primaryOrderId);
//            invoiceApplyRpcReq.setCustId(mainOrder.getCustomer().getCustId());
//            invoiceApplyRpcReq.setSellerId(mainOrder.getSeller().getSellerId());
//            invoiceApplyRpcReq.setInvoiceType(InvoiceTypeEnum.GENERAL.getCode());
//            RpcResponse<OrderInvoiceDTO> rpcResponse = invoiceFacade.applyOrderInvoice(invoiceApplyRpcReq);
//            return rpcResponse.isSuccess();
//        } catch (Exception e) {
//            log.error("确认收货后进行发票申请异常!,primaryOrderId:{},error message{}", mainOrder.getPrimaryOrderId(), e);
//            return false;
//        }
    }
}
