package com.aliyun.gts.gmall.manager.front.customer.facade.impl;

import java.util.List;

import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.InvoiceAdapter;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.InvoiceCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.InvoiceOptCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.CustomerInvoiceVO;
import com.aliyun.gts.gmall.manager.front.customer.facade.InvoiceFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 发票操作接口实现
 *
 * @author tiansong
 */
@Service
public class InvoiceFacadeImpl implements InvoiceFacade {
    @Autowired
    private InvoiceAdapter invoiceAdapter;

    @Override
    public List<CustomerInvoiceVO> queryList(LoginRestQuery loginRestQuery) {
        return invoiceAdapter.queryInvoiceList(loginRestQuery.getCustId());
    }

    @Override
    public Long addOrEdit(InvoiceCommand invoiceCommand) {
        if (invoiceCommand.getId() == null || invoiceCommand.getId() <= 0L) {
            // 新增发票
            return invoiceAdapter.createInvoice(invoiceCommand);
        }
        // 更新发票
        if (invoiceAdapter.updateInvoice(invoiceCommand)) {
            return invoiceCommand.getId();
        }
        return null;
    }

    @Override
    public Boolean delById(InvoiceOptCommand invoiceOptCommand) {
        return invoiceAdapter.deleteInvoice(invoiceOptCommand.getCustId(), invoiceOptCommand.getInvoiceId());
    }
}
