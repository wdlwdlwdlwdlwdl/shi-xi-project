package com.aliyun.gts.gmall.manager.front.customer.facade;

import java.util.List;

import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.InvoiceCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.InvoiceOptCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.CustomerInvoiceVO;

/**
 * 发票操作接口
 *
 * @author tiansong
 */
public interface InvoiceFacade {

    /**
     * 获取用户的发票列表
     *
     * @param loginRestQuery 登录用户
     * @return 发票列表
     */
    List<CustomerInvoiceVO> queryList(LoginRestQuery loginRestQuery);

    /**
     * 创建发票
     *
     * @param invoiceCommand 发票信息
     * @return 发票ID
     */
    Long addOrEdit(InvoiceCommand invoiceCommand);

    /**
     * 删除发票
     *
     * @param invoiceOptCommand
     * @return 是否删除成功
     */
    Boolean delById(InvoiceOptCommand invoiceOptCommand);
}
