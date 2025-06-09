package com.aliyun.gts.gmall.center.trade.api.facade;

import com.aliyun.gts.gmall.center.trade.api.dto.input.*;
import com.aliyun.gts.gmall.center.trade.api.dto.output.OrderInvoiceDTO;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("发票操作")
public interface InvoiceFacade {

    @ApiOperation("申请发票")
    RpcResponse<OrderInvoiceDTO> applyOrderInvoice(InvoiceApplyRpcReq req);

    @ApiOperation("撤销/退票")
    RpcResponse<OrderInvoiceDTO> removeOrderInvoice(InvoiceRemoveRpcReq req);

    @ApiOperation("关闭申请通道")
    RpcResponse<Boolean> closeApplyInvoice(InvoiceQueryRpcReq rpcReq);

    @ApiOperation("发票详情")
    RpcResponse<OrderInvoiceDTO> getOrderInvoice(InvoiceQueryRpcReq req);

    @ApiOperation("发票分页信息查询")
    RpcResponse<PageInfo<OrderInvoiceDTO>> selectByCondition(OrderInvoiceListRpcReq orderInvoiceRpcReq);
}
