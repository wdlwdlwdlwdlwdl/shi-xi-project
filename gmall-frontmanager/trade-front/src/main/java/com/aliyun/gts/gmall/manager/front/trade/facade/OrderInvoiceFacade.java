package com.aliyun.gts.gmall.manager.front.trade.facade;

import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.OrderInvoiceApplyCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.OrderInvoiceInfoCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.InvoiceApplyVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.InvoiceDetailVO;

/**
 * 发票相关操作
 */
public interface OrderInvoiceFacade {
    InvoiceApplyVO applyInvoice(OrderInvoiceApplyCommand orderInvoiceApplyCommand);

    InvoiceDetailVO getInvoiceInfo(OrderInvoiceInfoCommand orderInvoiceInfoCommand);

    InvoiceDetailVO getInvoiceInfo(OrderInvoiceInfoCommand command, Long custId);
}
