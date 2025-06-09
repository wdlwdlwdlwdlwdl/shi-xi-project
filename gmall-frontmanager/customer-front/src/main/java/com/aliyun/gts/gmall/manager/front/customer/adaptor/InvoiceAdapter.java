package com.aliyun.gts.gmall.manager.front.customer.adaptor;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.framework.common.DubboBuilder;
import com.aliyun.gts.gmall.manager.front.common.config.DatasourceConfig;
import com.aliyun.gts.gmall.manager.front.common.config.DegradationConfig;
import com.aliyun.gts.gmall.manager.front.common.consts.BizConst;
import com.aliyun.gts.gmall.manager.front.common.consts.DsIdConst;
import com.aliyun.gts.gmall.manager.front.customer.converter.CustomerConverter;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.InvoiceCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.CustomerInvoiceVO;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CreateCustomerInvoiceCommand;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerCommonDeleteByIdCommand;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerInvoiceQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.input.UpdateCustomerInvoiceCommand;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerInvoiceDTO;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerInvoiceReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerInvoiceWriteFacade;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode.CUSTOMER_CENTER_ERROR;
import static com.aliyun.gts.gmall.manager.front.customer.dto.utils.CustomerFrontResponseCode.INVOICE_CREATE_FAIL;
import static com.aliyun.gts.gmall.manager.front.customer.dto.utils.CustomerFrontResponseCode.INVOICE_DELETE_FAIL;
import static com.aliyun.gts.gmall.manager.front.customer.dto.utils.CustomerFrontResponseCode.INVOICE_QUERY_FAIL;
import static com.aliyun.gts.gmall.manager.front.customer.dto.utils.CustomerFrontResponseCode.INVOICE_UPDATE_FAIL;

/**
 * 发票信息接口
 *
 * @author tiansong
 */
@Slf4j
@ApiOperation(value = "发票信息接口")
@Component
public class InvoiceAdapter {
    @Autowired
    private CustomerInvoiceReadFacade  customerInvoiceReadFacade;
    @Autowired
    private CustomerInvoiceWriteFacade customerInvoiceWriteFacade;
    @Autowired
    private CustomerConverter          customerConverter;

    private final DubboBuilder builder = DubboBuilder.builder().logger(log).sysCode(CUSTOMER_CENTER_ERROR).build();

    /**
     * 获取用户的发票列表【强依赖】
     *
     * @param custId 用户ID
     * @return 发票列表
     */
    public List<CustomerInvoiceVO> queryInvoiceList(Long custId) {
        return builder.create().id(DsIdConst.customer_invoice_queryList).queryFunc(
                (Function<CustomerInvoiceQuery, RpcResponse<List<CustomerInvoiceVO>>>) request -> {
                    RpcResponse<PageInfo<CustomerInvoiceDTO>> pageInfo = customerInvoiceReadFacade.pageQuery(request);
                    if (!pageInfo.isSuccess()) {
                        return RpcResponse.fail(pageInfo.getFail());
                    }
                    if (pageInfo.getData() == null || pageInfo.getData().isEmpty()) {
                        return RpcResponse.ok(Collections.EMPTY_LIST);
                    }
                    return RpcResponse.ok(customerConverter.convertInvoiceList(pageInfo.getData().getList()));
                }).bizCode(INVOICE_QUERY_FAIL).query(
                CustomerInvoiceQuery.of(custId, BizConst.PAGE_NO, BizConst.INVOICE_PAGE_SIZE));
    }

    /**
     * 创建发票【强依赖】
     *
     * @param invoiceCommand 发票信息
     * @return 发票ID
     */
    public Long createInvoice(InvoiceCommand invoiceCommand) {
        return builder.create().id(DsIdConst.customer_invoice_create).queryFunc(
                (Function<CreateCustomerInvoiceCommand, RpcResponse<Long>>) request -> {
                    RpcResponse<CustomerInvoiceDTO> rpcResponse = customerInvoiceWriteFacade.create(request);
                    if (!rpcResponse.isSuccess()) {
                        return RpcResponse.fail(rpcResponse.getFail());
                    }
                    if (rpcResponse.getData() == null || rpcResponse.getData().getId() == null) {
                        return RpcResponse.fail(INVOICE_CREATE_FAIL);
                    }
                    return RpcResponse.ok(rpcResponse.getData().getId());
                }).bizCode(INVOICE_CREATE_FAIL).query(customerConverter.convertCreateInvoice(invoiceCommand));
    }

    /**
     * 更新发票【强依赖】
     *
     * @param invoiceCommand 发票信息
     * @return 是否成功
     */
    public Boolean updateInvoice(InvoiceCommand invoiceCommand) {
        return builder.create().id(DsIdConst.customer_invoice_update).queryFunc(
                (Function<UpdateCustomerInvoiceCommand, RpcResponse<Boolean>>) request -> {
                    RpcResponse<CustomerInvoiceDTO> rpcResponse = customerInvoiceWriteFacade.update(request);
                    if (!rpcResponse.isSuccess()) {
                        return RpcResponse.fail(rpcResponse.getFail());
                    }
                    if (rpcResponse.getData() == null) {
                        return RpcResponse.fail(INVOICE_UPDATE_FAIL);
                    }
                    return RpcResponse.ok(Boolean.TRUE);
                }).bizCode(INVOICE_UPDATE_FAIL).query(customerConverter.convertUpdateInvoice(invoiceCommand));
    }

    /**
     * 删除发票【强依赖】
     *
     * @param custId    用户ID
     * @param invoiceId 发票ID
     * @return 是否成功
     */
    public Boolean deleteInvoice(Long custId, Long invoiceId) {
        return builder.create().id(DsIdConst.customer_invoice_delete).queryFunc(
                (Function<CustomerCommonDeleteByIdCommand, RpcResponse<Boolean>>) request ->
                        customerInvoiceWriteFacade.delete(request)
        ).bizCode(INVOICE_DELETE_FAIL).query(CustomerCommonDeleteByIdCommand.of(custId, invoiceId));
    }
}
