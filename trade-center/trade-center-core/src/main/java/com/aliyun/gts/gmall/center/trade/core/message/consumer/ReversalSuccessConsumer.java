package com.aliyun.gts.gmall.center.trade.core.message.consumer;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.middleware.api.mq.anns.MQConsumer;
import com.aliyun.gts.gmall.middleware.api.mq.consumer.ConsumeEventProcessor;
import com.aliyun.gts.gmall.middleware.api.mq.model.StandardEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@MQConsumer(
    groupId = "${trade.reversal.statuschange.groupId}",
    topic = "${trade.reversal.statuschange.topic}",
    tag = ReversalSuccessConsumer.STATUS
)
public class ReversalSuccessConsumer implements ConsumeEventProcessor {

    static final String STATUS = "100"; // @see ReversalStatusEnum

//    @Autowired
//    private PointGrantService pointGrantService;

//    @Autowired
//    private InvoiceFacade invoiceFacade;

    @Override
    public boolean process(StandardEvent event) {
        log.info("receive event msg = " + JSONObject.toJSONString(event));
        //ReversalMessageDTO message = (ReversalMessageDTO) event.getPayload().getData();
        //pointGrantService.rollbackOnReversalSuccess(message.getPrimaryReversalId());
        //this.closeInvoice(event);
        return true;
    }

//    /**
//     * 发生售后，售后退款成功关闭开票入口
//     *
//     * @param event
//     */
//    private void closeInvoice(StandardEvent event) {
//        try {
//            ExtendReversalMessage message = (ExtendReversalMessage) event.getPayload().getData();
//            if (message == null) {
//                return;
//            }
//            Long primaryOrderId = Long.valueOf(message.getPrimaryOrderId());
//            Long sellerId = Long.valueOf(message.getSellerId());
//            Long custId = message.getCustId();
//            InvoiceQueryRpcReq invoiceQueryRpcReq = new InvoiceQueryRpcReq();
//            invoiceQueryRpcReq.setPrimaryOrderId(primaryOrderId);
//            invoiceQueryRpcReq.setSellerId(sellerId);
//            invoiceQueryRpcReq.setCustId(custId);
//            RpcResponse<Boolean> booleanRpcResponse = invoiceFacade.closeApplyInvoice(invoiceQueryRpcReq);
//            if (!booleanRpcResponse.isSuccess()) {
//                log.error("售后退款成功，关闭开票入口失败！msg:{}", booleanRpcResponse.getFail().getMessage());
//            }
//        } catch (Exception e) {
//            log.error("售后退款成功，关闭开票入口异常！", e);
//        }
//    }
}
