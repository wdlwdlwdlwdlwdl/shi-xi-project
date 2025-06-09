package com.aliyun.gts.gmall.center.trade.server.facade.impl;

import com.aliyun.gts.gmall.center.trade.api.dto.enums.InvoiceTypeEnum;
import com.aliyun.gts.gmall.center.trade.api.dto.input.InvoiceApplyRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.input.InvoiceQueryRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.input.InvoiceRemoveRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.input.OrderInvoiceListRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.output.OrderInvoiceDTO;
import com.aliyun.gts.gmall.center.trade.api.facade.InvoiceFacade;
import com.aliyun.gts.gmall.center.trade.core.constants.OrderInvoiceErrorCode;
import com.aliyun.gts.gmall.center.trade.core.converter.OrderFinanceFlowSyncConverter;
import com.aliyun.gts.gmall.center.trade.core.converter.OrderInvoiceConverter;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.TcOrderInvoiceDO;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.TcOrderInvoiceRepository;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.boot.rpc.dubbo.light.provider.anns.DubboLight;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.platform.pay.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderStatusEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderExtraService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderQueryOption;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerInvoiceReadFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
@DubboLight(interfaceClass = InvoiceFacade.class)
@Slf4j
public class InvoiceFacadeImpl implements InvoiceFacade {

    private static final String INVOICE = "invoice";

    private static final String ORDER_INVOICE_LOCK_NAME = "order_invoice_lock_name";

    private static final String HIDE_APPLY_BUTTON = "hide_apply_button";

    private static final int LOCK_WAIT = 2000;
    private static final int LOCK_MAX = 5000;

//    @Autowired
//    private OrderInvoiceCoreService orderInvoiceCoreService;

    @Autowired
    private TcOrderInvoiceRepository tcOrderInvoiceRepository;

    @Autowired
    private OrderInvoiceConverter orderInvoiceConverter;

    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private OrderFinanceFlowSyncConverter orderFinanceFlowSyncConverter;

    @Autowired
    private CustomerInvoiceReadFacade customerInvoiceReadFacade;

    @Autowired
    private OrderExtraService orderExtraService;

    @Autowired
    private CacheManager tradeCacheManager;

    @Override
    public RpcResponse<OrderInvoiceDTO> applyOrderInvoice(InvoiceApplyRpcReq req) {
//        log.info("申请发票入参:{}", JSONObject.toJSONString(req));
//        // 锁住当前查询订单
//        String lockKey = ORDER_INVOICE_LOCK_NAME + req.getPrimaryOrderId();
//        DistributedLock lock = tradeCacheManager.getLock(lockKey);
//        boolean b;
//        try {
//            b = lock.tryLock(LOCK_WAIT, LOCK_MAX, TimeUnit.MILLISECONDS);
//        } catch (InterruptedException e) {
//            throw new GmallException(e);
//        }
//        if (!b) {
//            return RpcResponse.fail(OrderInvoiceErrorCode.REMOTE_REQUEST_FAILED);
//        }
//        try {
//            // 专票非编辑状态  进行当前发票校验
//            TcOrderInvoiceDO tcOrderInvoiceDO = null;
//            if (Objects.equals(req.getInvoiceType(), InvoiceTypeEnum.GENERAL.getCode())) {
//                tcOrderInvoiceDO = createNormalOrderInvoice(req);
//            } else if (Objects.equals(req.getInvoiceType(), InvoiceTypeEnum.SPECIAL.getCode())) {
//                tcOrderInvoiceDO = createSpecialOrderInvoice(req);
//            }
//            if (Objects.isNull(tcOrderInvoiceDO)) {
//                throw new GmallException(OrderInvoiceErrorCode.REMOTE_REQUEST_FAILED);
//            }
//            //更新扩展字段信息
//            orderInvoiceCoreService.saveMainOrderExtend(tcOrderInvoiceDO);
//            log.info("申请发票结果:{}", JSONObject.toJSONString(tcOrderInvoiceDO));
//            return RpcResponse.ok(orderInvoiceConverter.toDto(tcOrderInvoiceDO));
//        } catch (GmallException var3) {
//            log.error("apply invoice error: primaryOrderId-{},error-{}!", req.getPrimaryOrderId(), var3);
//            return RpcResponse.fail(var3.getFrontendCare().getCode().getCode(), var3.getFrontendCare().detailMessage());
//        } catch (Exception var4) {
//            log.error("apply invoice error: primaryOrderId-{},error-{}!", req.getPrimaryOrderId(), var4);
//            return RpcResponse.fail(CommonErrorCode.SERVER_ERROR.getCode(), CommonErrorCode.SERVER_ERROR.getMessage() + ":" + var4.getMessage());
//        } finally {
//            lock.unLock();
//        }
        return null;
    }

    private TcOrderInvoiceDO createNormalOrderInvoice(InvoiceApplyRpcReq req) {
//        Long primaryOrderId = req.getPrimaryOrderId();
//        TcOrderInvoiceDO currentOrderInvoice = tcOrderInvoiceRepository.getCurrentOrderInvoice(primaryOrderId);
//        //是否已经存在可用的发票记录
//        isAvailable(currentOrderInvoice);
//        MainOrder mainOrder = getMainOrder(req.getPrimaryOrderId(),req.getSellerId(),req.getCustId());
//        //查询发票抬头
//        CustomerInvoiceDTO customerInvoiceDTO;
//        if (Objects.isNull(req.getTitleId())) {
//            //创建订单写入的发票抬头信息
//            List<TcOrderExtendDO> invoices = mainOrder.getOrderExtendList()
//                    .stream()
//                    .filter(item -> StringUtils.equals(INVOICE, item.getExtendKey()) && StringUtils.equals(INVOICE, item.getExtendType()))
//                    .collect(Collectors.toList());
//            if (invoices.isEmpty()) {
//                log.error("发票内部抬头信息:{}", JSONObject.toJSONString(mainOrder.getOrderExtendList()));
//                throw new GmallException(OrderInvoiceErrorCode.NO_INVOICE_TITLE);
//            }
//            String extendValue = invoices.get(0).getExtendValue();
//            JSONObject jsonObject = JSON.parseObject(extendValue);
//            req.setTitleId(jsonObject.getLong("id"));
//        }
//        RpcResponse<CustomerInvoiceDTO> response = customerInvoiceReadFacade.queryById(CommonByIdAndCustIdQuery.of(req.getCustId(), req.getTitleId()));
//        if (!response.isSuccess() || Objects.isNull(response.getData())) {
//            throw new GmallException(OrderInvoiceErrorCode.NO_INVOICE_TITLE);
//        }
//        customerInvoiceDTO = response.getData();
//        OrderExtraSaveRpcReq save = new OrderExtraSaveRpcReq();
//        save.setPrimaryOrderId(req.getPrimaryOrderId());
//        Map<String, String> extend = new HashMap<>();
//        extend.put(INVOICE, JSONObject.toJSONString(customerInvoiceDTO));
//        Map<String, Map<String, String>> extendMap = new HashMap<>();
//        extendMap.put(INVOICE, extend);
//        save.setAddExtends(extendMap);
//        orderExtraService.saveOrderExtras(save);
//        return orderInvoiceCoreService.createGeneralInvoice(mainOrder, customerInvoiceDTO);
        return null;
    }

    /**
     * 获取主订单信息
     *
     * @param primaryOrderId
     * @return
     */
    private MainOrder getMainOrder(Long primaryOrderId,Long sellerId,Long custId) {
        MainOrder mainOrder = orderQueryAbility.getMainOrder(primaryOrderId, OrderQueryOption.builder()
                .includeExtends(true).build());
        if (Objects.isNull(mainOrder)) {
            throw new GmallException(OrderInvoiceErrorCode.NO_MAIN_ORDER);
        }
        Map<String, String> extras = mainOrder.getOrderAttr().getExtras();
        if (extras != null && extras.get(HIDE_APPLY_BUTTON) != null &&
                Objects.equals(extras.get(HIDE_APPLY_BUTTON),"close")) {
            throw new GmallException(OrderInvoiceErrorCode.CLOSE_APPLY_INVOICE);
        }
        if (!(Objects.equals(mainOrder.getSeller().getSellerId(),sellerId)||
                Objects.equals(mainOrder.getCustomer().getCustId(),custId))){
            throw new GmallException(OrderInvoiceErrorCode.NO_MAIN_ORDER);
        }
        if (!(Objects.equals(mainOrder.getPrimaryOrderStatus(), OrderStatusEnum.ORDER_CONFIRM.getCode()) ||
                Objects.equals(mainOrder.getPrimaryOrderStatus(), OrderStatusEnum.SYSTEM_CONFIRM.getCode())) ||
                Objects.equals(mainOrder.getPrimaryOrderStatus(), OrderStatusEnum.ORDER_SUCCESS.getCode())) {
            //当前订单状态已确认收货
            throw new GmallException(OrderInvoiceErrorCode.NO_ORDER_CONFIRM);
        }
        return mainOrder;
    }

    private TcOrderInvoiceDO editSpecialOrderInvoice(InvoiceApplyRpcReq req) {
//        InvoiceApplyRpcReq.SpecialInvoice specialInvoice = req.getSpecialInvoice();
//        //编辑
//        TcOrderInvoiceDO tcOrderInvoiceDO = tcOrderInvoiceRepository.getById(specialInvoice.getId());
//        //编辑权限校验
//        permission(tcOrderInvoiceDO, req.getInvoiceType(), req.getCustId(), req.getSellerId());
//        tcOrderInvoiceDO.setPrimaryOrderId(specialInvoice.getPrimaryOrderId());
//        tcOrderInvoiceDO.setInvoiceAmount(specialInvoice.getInvoiceAmount());
//        tcOrderInvoiceDO.setInvoiceCode(specialInvoice.getInvoiceCode());
//        tcOrderInvoiceDO.setInvoiceNo(specialInvoice.getInvoiceNo());
//        tcOrderInvoiceRepository.updateById(tcOrderInvoiceDO);
//        return tcOrderInvoiceDO;
        return null;
    }

    /**
     * 针对订单修改的权限问题
     *
     * @param tcOrderInvoiceDO
     */
    private void permission(TcOrderInvoiceDO tcOrderInvoiceDO, Integer invoiceType, Long custId, Long sellerId) {
        if (Objects.isNull(tcOrderInvoiceDO)) {
            throw new GmallException(OrderInvoiceErrorCode.NO_ORDER_INVOICE);
        }
        if (!Objects.equals(tcOrderInvoiceDO.getInvoiceType(), invoiceType)) {
            throw new GmallException(OrderInvoiceErrorCode.NO_CAN_REJECT);
        }
        if (!(Objects.equals(custId, tcOrderInvoiceDO.getCustId()) ||
                Objects.equals(sellerId, tcOrderInvoiceDO.getSellerId()))) {
            throw new GmallException(CommonErrorCode.NOT_DATA_OWNER);
        }
    }


    private TcOrderInvoiceDO createSpecialOrderInvoice(InvoiceApplyRpcReq req) {
//        InvoiceApplyRpcReq.SpecialInvoice specialInvoice = req.getSpecialInvoice();
//        if (Objects.nonNull(specialInvoice.getId())) {
//            //编辑专票
//            return editSpecialOrderInvoice(req);
//        }
//        //新增专票校验  1、存在专票 重复开票 2、存在普票 已申请普票，请勿重新开票
//        TcOrderInvoiceDO currentOrderInvoice = tcOrderInvoiceRepository.getCurrentOrderInvoice(req.getPrimaryOrderId());
//        //是否已经存在可用的发票记录
//        isAvailable(currentOrderInvoice);
//        MainOrder mainOrder = getMainOrder(req.getPrimaryOrderId(),req.getSellerId(),req.getCustId());
//        SaleTypeEnum saleTypeEnum = orderFinanceFlowSyncConverter.getSaleType(mainOrder);
//        TcOrderInvoiceDO tcOrderInvoiceDO = new TcOrderInvoiceDO();
//        if (Objects.nonNull(saleTypeEnum)) {
//            tcOrderInvoiceDO.setSaleType(saleTypeEnum.getCode());
//        }
//        tcOrderInvoiceDO.setRequestNo(UuidUtil.uuidWithoutUnderscore());
//        tcOrderInvoiceDO.setPrimaryOrderId(specialInvoice.getPrimaryOrderId());
//        tcOrderInvoiceDO.setMasterOrderAmount(String.valueOf(mainOrder.getOrderPrice().getOrderRealAmt()));
//        tcOrderInvoiceDO.setInvoiceAmount(specialInvoice.getInvoiceAmount());
//        tcOrderInvoiceDO.setInvoiceCode(specialInvoice.getInvoiceCode());
//        tcOrderInvoiceDO.setInvoiceNo(specialInvoice.getInvoiceNo());
//        tcOrderInvoiceDO.setStatus(InvoiceStatusEnum.APPLY_SUCCESS.getCode());
//        tcOrderInvoiceDO.setInvoiceTime(specialInvoice.getInvoiceTime());
//        //线下申请创建 专票记录
//        tcOrderInvoiceDO.setInvoiceType(InvoiceTypeEnum.SPECIAL.getCode());
//        tcOrderInvoiceDO.setCustId(mainOrder.getCustomer().getCustId());
//        tcOrderInvoiceDO.setSellerId(mainOrder.getSeller().getSellerId());
//        tcOrderInvoiceRepository.create(tcOrderInvoiceDO);
//        return tcOrderInvoiceDO;
        return null;
    }

    /**
     * 该订单下的发票是否是可用发票
     *
     * @param tcOrderInvoiceDO
     * @return
     */
    private void isAvailable(TcOrderInvoiceDO tcOrderInvoiceDO) {
        if (Objects.isNull(tcOrderInvoiceDO)) {
            return;
        }
        if (Objects.equals(tcOrderInvoiceDO.getInvoiceType(), InvoiceTypeEnum.GENERAL.getCode())) {
            throw new GmallException(OrderInvoiceErrorCode.HAS_NORMAL_INVOICE);
        } else {
            throw new GmallException(OrderInvoiceErrorCode.HAS_SPECIAL_INVOICE);
        }
    }


    /**
     * 4、撤销订单发票信息操作
     *
     * @param req
     * @return
     */
    @Override
    public RpcResponse<OrderInvoiceDTO> removeOrderInvoice(InvoiceRemoveRpcReq req) {
//        log.info("撤销订单发票信息操作入参:{}", JSONObject.toJSONString(req));
//        TcOrderInvoiceDO tcOrderInvoiceDO = getCurrentInvoiceInfo(req);
//        if (Objects.isNull(tcOrderInvoiceDO)) {
//            return RpcResponse.fail(OrderInvoiceErrorCode.NO_ORDER_INVOICE);
//        }
//        // 锁住当前查询订单
//        String lockKey = ORDER_INVOICE_LOCK_NAME + tcOrderInvoiceDO.getPrimaryOrderId();
//        DistributedLock lock = tradeCacheManager.getLock(lockKey);
//        boolean b;
//        try {
//            b = lock.tryLock(LOCK_WAIT, LOCK_MAX, TimeUnit.MILLISECONDS);
//        } catch (InterruptedException e) {
//            throw new GmallException(e);
//        }
//        if (!b) {
//            return RpcResponse.fail(OrderInvoiceErrorCode.REMOTE_REQUEST_FAILED);
//        }
//        try {
//            //权限校验
//            permission(tcOrderInvoiceDO, req.getInvoiceType(), req.getCustId(), req.getSellerId());
//            if (Objects.equals(InvoiceTypeEnum.GENERAL.getCode(), tcOrderInvoiceDO.getInvoiceType())) {
//                rejectNormalInvoice(tcOrderInvoiceDO);
//            } else if (Objects.equals(InvoiceTypeEnum.SPECIAL.getCode(), tcOrderInvoiceDO.getInvoiceType())) {
//                rejectSpecialInvoice(tcOrderInvoiceDO);
//            }
//            tcOrderInvoiceRepository.updateById(tcOrderInvoiceDO);
//            orderInvoiceCoreService.saveMainOrderExtend(tcOrderInvoiceDO);
//            if (Objects.nonNull(req.getClose()) && req.getClose()) {
//                //主动关闭发票申请入口
//                orderInvoiceCoreService.closeApplyInvoice(tcOrderInvoiceDO.getPrimaryOrderId());
//            }
//            return RpcResponse.ok(orderInvoiceConverter.toDto(tcOrderInvoiceDO));
//        } catch (GmallException var3) {
//            log.error("apply invoice error: primaryOrderId-{},error-{}!", req.getPrimaryOrderId(), var3);
//            return RpcResponse.fail(var3.getFrontendCare().getCode().getCode(), var3.getFrontendCare().detailMessage());
//        } catch (Exception var4) {
//            log.error("apply invoice error: primaryOrderId-{},error-{}!", req.getPrimaryOrderId(), var4);
//            return RpcResponse.fail(CommonErrorCode.SERVER_ERROR.getCode(), CommonErrorCode.SERVER_ERROR.getMessage() + ":" + var4.getMessage());
//        } finally {
//            lock.unLock();
//        }
        return null;
    }

    private TcOrderInvoiceDO getCurrentInvoiceInfo(InvoiceRemoveRpcReq req) {
//        TcOrderInvoiceDO tcOrderInvoiceDO=null;
//        if (Objects.nonNull(req.getId())) {
//            tcOrderInvoiceDO = tcOrderInvoiceRepository.getById(req.getId());
//        } else if (Objects.nonNull(req.getPrimaryOrderId())) {
//            tcOrderInvoiceDO= tcOrderInvoiceRepository.getCurrentOrderInvoice(req.getPrimaryOrderId());
//        }
//        if (Objects.nonNull(tcOrderInvoiceDO)&&
//                !(Objects.equals(tcOrderInvoiceDO.getSellerId(),req.getSellerId())||
//                        Objects.equals(tcOrderInvoiceDO.getCustId(),req.getCustId()))){
//            //操作的不是自己的发票信息时
//            return null;
//        }
//        return tcOrderInvoiceDO;
        return null;
    }


    @Override
    public RpcResponse<Boolean> closeApplyInvoice(InvoiceQueryRpcReq req) {
//        log.info("关闭发票申领入口操作入参:{}", JSONObject.toJSONString(req));
//        return RpcServerUtils.invoke(() -> {
//            MainOrder mainOrder = getMainOrder(req.getPrimaryOrderId(),req.getSellerId(),req.getCustId());
//            orderInvoiceCoreService.closeApplyInvoice(mainOrder.getPrimaryOrderId());
//            return RpcResponse.ok(null);
//        }, "InvoiceFacadeImpl.closeApplyInvoice");
        return null;
    }


    private void rejectNormalInvoice(TcOrderInvoiceDO invoice) {
//        if (Objects.equals(InvoiceStatusEnum.APPLYING.getCode(), invoice.getStatus())) {
//            //申领中 调用 撤销发票 接口
//            orderInvoiceCoreService.rejectInvoice(invoice);
//            invoice.setStatus(InvoiceStatusEnum.RETURN_SUCCESS.getCode());
//        } else if (Objects.equals(InvoiceStatusEnum.APPLY_SUCCESS.getCode(), invoice.getStatus())) {
//            //调用 退票 接口
//            if (Objects.equals(InvoiceRejectEnum.NO_APPLY.getCode(), invoice.getInvoiceReject())) {
//                orderInvoiceCoreService.returnInvoice(invoice);
//                invoice.setInvoiceReject(InvoiceRejectEnum.APPLYING.getCode());
//            }else if (Objects.equals(InvoiceRejectEnum.APPLYING.getCode(), invoice.getInvoiceReject())) {
//                throw new GmallException(OrderInvoiceErrorCode.NOT_ALLOWED_OPERATION);
//            }else {
//                throw new GmallException(OrderInvoiceErrorCode.NOT_ALLOWED_REJECT);
//            }
//        } else {
//            throw new GmallException(OrderInvoiceErrorCode.NOT_ALLOWED_REJECT);
//        }
    }

    /**
     * 专票发票作废操作操作
     *
     * @param tcOrderInvoiceDO
     * @return
     */
    private void rejectSpecialInvoice(TcOrderInvoiceDO tcOrderInvoiceDO) {
//        //专票直接删除即可 不需要更高发票状态
//        if (Objects.equals(InvoiceStatusEnum.APPLY_SUCCESS.getCode(), tcOrderInvoiceDO.getStatus())) {
//            tcOrderInvoiceDO.setStatus(InvoiceStatusEnum.REJECT_SUCCESS.getCode());
//        } else {
//            throw new GmallException(OrderInvoiceErrorCode.NOT_ALLOWED_REJECT);
//        }
    }


    @Override
    public RpcResponse<OrderInvoiceDTO> getOrderInvoice(InvoiceQueryRpcReq req) {
//        log.info("查询订单发票详情信息操作入参:{}", JSONObject.toJSONString(req));
//        return RpcServerUtils.invoke(() -> {
//            TcOrderInvoiceDO tcOrderInvoiceDO = null;
//            if (Objects.nonNull(req.getId())) {
//                tcOrderInvoiceDO = tcOrderInvoiceRepository.getById(req.getId());
//            } else if (Objects.nonNull(req.getPrimaryOrderId())) {
//                tcOrderInvoiceDO = tcOrderInvoiceRepository.queryLatestInvoice(req.getPrimaryOrderId());
//            }
//            if (Objects.isNull(tcOrderInvoiceDO)) {
//                throw new GmallException(OrderInvoiceErrorCode.NO_ORDER_INVOICE);
//            }
//            if (!(Objects.equals(req.getCustId(), tcOrderInvoiceDO.getCustId()) ||
//                    Objects.equals(req.getSellerId(), tcOrderInvoiceDO.getSellerId()))) {
//                throw new GmallException(CommonErrorCode.NOT_DATA_OWNER);
//            }
//            orderInvoiceCoreService.getInvoiceUrl(tcOrderInvoiceDO);
//            OrderInvoiceDTO orderInvoiceDTO = orderInvoiceConverter.toDto(tcOrderInvoiceDO);
//            return RpcResponse.ok(orderInvoiceDTO);
//        }, "getOrderInvoice");
        return null;
    }


    @Override
    public RpcResponse<PageInfo<OrderInvoiceDTO>> selectByCondition(OrderInvoiceListRpcReq rpcReq) {
//        log.info("查询订单发票列表信息操作入参:{}", JSONObject.toJSONString(rpcReq));
//        return RpcServerUtils.invoke(() -> {
//            InvoiceQueryParam param = orderInvoiceConverter.toEntity(rpcReq);
//            PageParam pageParam = rpcReq.getPage();
//            param.setPageNum(pageParam.getPageNo());
//            param.setPageSize(pageParam.getPageSize());
//            Page<TcOrderInvoiceDO> page = tcOrderInvoiceRepository.selectByCondition(param);
//            PageInfo<OrderInvoiceDTO> result = new PageInfo<>();
//            result.setTotal(page.getTotal());
//            result.setList(orderInvoiceConverter.toDtoList(page.getRecords()));
//            return RpcResponse.ok(result);
//        }, "selectByCondition");
        return null;
    }
}
