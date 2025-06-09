package com.aliyun.gts.gmall.center.trade.domain.repositiry;

import com.aliyun.gts.gmall.center.trade.domain.dataobject.TcOrderInvoiceDO;
import com.aliyun.gts.gmall.center.trade.domain.entity.invoice.InvoiceQueryParam;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface TcOrderInvoiceRepository {

    void create(TcOrderInvoiceDO tcOrderInvoiceDO);

    TcOrderInvoiceDO getById(Long id);

    TcOrderInvoiceDO queryByRequestNo(String requestNo);

    TcOrderInvoiceDO queryByInvoiceId(Long invoiceId);

    void insertOrUpdate(TcOrderInvoiceDO tcOrderInvoiceDO);

    TcOrderInvoiceDO queryLatestInvoice(Long orderId);

    TcOrderInvoiceDO getCurrentOrderInvoice(Long primaryOrderId);

    boolean updateById(TcOrderInvoiceDO tcOrderInvoiceDO);

    Page<TcOrderInvoiceDO> selectByCondition(InvoiceQueryParam param);

    Integer deleteById(Long id);
}
