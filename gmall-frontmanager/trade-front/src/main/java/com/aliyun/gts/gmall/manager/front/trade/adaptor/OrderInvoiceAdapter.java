package com.aliyun.gts.gmall.manager.front.trade.adaptor;

import com.aliyun.gts.gmall.center.trade.api.dto.enums.InvoiceTypeEnum;
import com.aliyun.gts.gmall.center.trade.api.dto.input.InvoiceApplyRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.input.InvoiceQueryRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.output.OrderInvoiceDTO;
import com.aliyun.gts.gmall.center.trade.api.facade.InvoiceFacade;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.framework.common.DubboBuilder;
import com.aliyun.gts.gmall.manager.front.common.consts.DsIdConst;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.OrderInvoiceApplyCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.OrderInvoiceInfoCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;

import static com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode.TRADE_CENTER_ERROR;
import static com.aliyun.gts.gmall.manager.front.trade.dto.utils.TradeFrontResponseCode.ORDER_INVOICE_APPLY_ERROR;
import static com.aliyun.gts.gmall.manager.front.trade.dto.utils.TradeFrontResponseCode.ORDER_INVOICE_GET_ERROR;

@Service
@Slf4j
public class OrderInvoiceAdapter {

    @Autowired
    private InvoiceFacade invoiceFacade;

    DubboBuilder tradeBuilder = DubboBuilder.builder().sysCode(TRADE_CENTER_ERROR).logger(log).build();

    public OrderInvoiceDTO applyOrderInvoice(OrderInvoiceApplyCommand applyCommand) {
        //构建 入参
        InvoiceApplyRpcReq invoiceApplyRpcReq = new InvoiceApplyRpcReq();
        invoiceApplyRpcReq.setPrimaryOrderId(applyCommand.getPrimaryOrderId());
        invoiceApplyRpcReq.setTitleId(applyCommand.getInvoiceId());
        invoiceApplyRpcReq.setCustId(applyCommand.getCustId());
        invoiceApplyRpcReq.setInvoiceType(InvoiceTypeEnum.GENERAL.getCode());
        return tradeBuilder.create().id(DsIdConst.trade_invoice_apply).queryFunc(
                (Function<InvoiceApplyRpcReq, RpcResponse<OrderInvoiceDTO>>) request ->
                        invoiceFacade.applyOrderInvoice(request)

        ).bizCode(ORDER_INVOICE_APPLY_ERROR).query(invoiceApplyRpcReq);
    }

    public OrderInvoiceDTO getInvoiceInfo(OrderInvoiceInfoCommand orderInvoiceInfoCommand,Long custId) {
        InvoiceQueryRpcReq invoiceQueryRpcReq = new InvoiceQueryRpcReq();
        invoiceQueryRpcReq.setPrimaryOrderId(orderInvoiceInfoCommand.getPrimaryOrderId());
        invoiceQueryRpcReq.setId(orderInvoiceInfoCommand.getId());
        invoiceQueryRpcReq.setCustId(custId);
        return tradeBuilder.create().id(DsIdConst.trade_invoice_reject).queryFunc(
                (Function<InvoiceQueryRpcReq, RpcResponse<OrderInvoiceDTO>>) request ->
                        invoiceFacade.getOrderInvoice(request)
        ).bizCode(ORDER_INVOICE_GET_ERROR).query(invoiceQueryRpcReq);
    }
}
