package com.aliyun.gts.gmall.manager.front.trade.facade.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.center.trade.api.dto.output.OrderInvoiceDTO;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.front.common.consts.BizConst;
import com.aliyun.gts.gmall.manager.front.trade.adaptor.OrderInvoiceAdapter;
import com.aliyun.gts.gmall.manager.front.trade.convertor.InvoiceConvertor;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.OrderInvoiceApplyCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.OrderInvoiceInfoCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.InvoiceApplyVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.InvoiceDetailVO;
import com.aliyun.gts.gmall.manager.front.trade.facade.OrderInvoiceFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class OrderInvoiceFacadeImpl implements OrderInvoiceFacade {

    @Autowired
    private OrderInvoiceAdapter orderInvoiceAdapter;

    @Autowired
    private InvoiceConvertor invoiceConvertor;


    @Override
    public InvoiceApplyVO applyInvoice(OrderInvoiceApplyCommand applyCommand) {
        OrderInvoiceDTO orderInvoiceDTO;
        try {
            orderInvoiceDTO = orderInvoiceAdapter.applyOrderInvoice(applyCommand);
        } catch (GmallException gmallException) {
            if (gmallException.getFrontendCare().getCode().getCode().equals(BizConst.HAS_SPECIAL_INVOICE)) {
                InvoiceApplyVO invoiceApplyVO = new InvoiceApplyVO();
                invoiceApplyVO.setSuccess(Boolean.FALSE);
                invoiceApplyVO.setErrorMsg(I18NMessageUtils.getMessage("special.invoice.applied"));  //# "已申请专票"
                return invoiceApplyVO;
            } else {
                throw gmallException;
            }

        }
        InvoiceApplyVO invoiceApplyVO = new InvoiceApplyVO();
        invoiceApplyVO.setResult(orderInvoiceDTO.getId());
        invoiceApplyVO.setRequestNo(orderInvoiceDTO.getInvoiceAmount());
        return invoiceApplyVO;

    }

    @Override
    public InvoiceDetailVO getInvoiceInfo(OrderInvoiceInfoCommand command) {
        return this.getInvoiceInfo(command,command.getCustId());

    }

    @Override
    public InvoiceDetailVO getInvoiceInfo(OrderInvoiceInfoCommand command,Long custId) {
        OrderInvoiceDTO invoiceInfo = orderInvoiceAdapter.getInvoiceInfo(command,custId);
        InvoiceDetailVO invoiceDetailVO = invoiceConvertor.convert(invoiceInfo);
        if (Objects.nonNull(invoiceInfo.getInvoiceTitle())) {
            invoiceDetailVO.setTitle(invoiceInfo.getInvoiceTitle().getTitle());
            invoiceDetailVO.setTitleType(invoiceInfo.getInvoiceTitle().getTitleType());
            invoiceDetailVO.setDutyParagraph(invoiceInfo.getInvoiceTitle().getDutyParagraph());
        }
        return invoiceDetailVO;
    }
}
