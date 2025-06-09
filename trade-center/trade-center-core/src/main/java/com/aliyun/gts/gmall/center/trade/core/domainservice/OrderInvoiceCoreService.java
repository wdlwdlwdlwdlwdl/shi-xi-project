package com.aliyun.gts.gmall.center.trade.core.domainservice;

import com.aliyun.gts.gmall.center.trade.api.dto.input.InvoiceApplyMessageRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.input.InvoiceRejectMessageRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.input.InvoiceReturnRejectMessageRpcReq;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.TcOrderInvoiceDO;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.extend.OrderExtendDTO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerInvoiceDTO;

import java.util.List;

public interface OrderInvoiceCoreService {

    TcOrderInvoiceDO createGeneralInvoice(MainOrder mainOrder, CustomerInvoiceDTO invoice);

    void saveMainOrderExtend(TcOrderInvoiceDO tcOrderInvoiceDO);

    void closeApplyInvoice(Long primaryOrder);

    Boolean downLoadInvoice(TcOrderInvoiceDO tcOrderInvoiceDO);

    void getInvoiceUrl(TcOrderInvoiceDO tcOrderInvoiceDO);

    OrderExtendDTO getOrderInvoiceInfo(Long primaryOrder, String extendKey);

    List<OrderExtendDTO> getOrderInvoiceInfo(Long primaryOrder, String extendType, String extendKey);

    Boolean returnInvoice(TcOrderInvoiceDO tcOrderInvoiceDO);

    Boolean rejectInvoice(TcOrderInvoiceDO tcOrderInvoiceDO);

    RpcResponse<Boolean> acceptReturnRejectInvoiceMessage(InvoiceReturnRejectMessageRpcReq req);

    RpcResponse acceptOrderInvoiceMessage(InvoiceApplyMessageRpcReq req);

    RpcResponse<Boolean> acceptRejectInvoiceMessage(InvoiceRejectMessageRpcReq req);
}
