package com.aliyun.gts.gmall.manager.front.trade.convertor;

import com.aliyun.gts.gmall.center.trade.api.dto.input.OrderInvoiceTitle;
import com.aliyun.gts.gmall.center.trade.api.dto.output.OrderInvoiceDTO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.InvoiceDetailVO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerInvoiceDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvoiceConvertor {

    OrderInvoiceTitle convertInvoice(CustomerInvoiceDTO customerInvoiceDTO);


    InvoiceDetailVO convert(OrderInvoiceDTO invoiceDTO);
}
