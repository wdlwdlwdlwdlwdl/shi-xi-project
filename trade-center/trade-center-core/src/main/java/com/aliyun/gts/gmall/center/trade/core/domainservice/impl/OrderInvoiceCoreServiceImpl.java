package com.aliyun.gts.gmall.center.trade.core.domainservice.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.center.trade.api.dto.enums.InvoiceStatusEnum;
import com.aliyun.gts.gmall.center.trade.api.dto.enums.InvoiceTypeEnum;
import com.aliyun.gts.gmall.center.trade.api.dto.input.InvoiceApplyMessageRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.input.InvoiceRejectMessageRpcReq;
import com.aliyun.gts.gmall.center.trade.api.dto.input.InvoiceReturnRejectMessageRpcReq;
import com.aliyun.gts.gmall.center.trade.common.constants.OrderFeatureKey;
import com.aliyun.gts.gmall.center.trade.core.converter.OrderFinanceFlowSyncConverter;
import com.aliyun.gts.gmall.center.trade.core.domainservice.OrderInvoiceCoreService;
import com.aliyun.gts.gmall.center.trade.core.enums.SaleTypeEnum;
import com.aliyun.gts.gmall.center.trade.domain.dataobject.TcOrderInvoiceDO;
import com.aliyun.gts.gmall.center.trade.domain.repositiry.TcOrderInvoiceRepository;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.integration.api.dto.input.shopinvoice.ApplyInvoiceRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.extend.OrderExtendQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.extend.OrderExtraSaveRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.extend.OrderExtendDTO;
import com.aliyun.gts.gmall.platform.trade.api.facade.order.OrderReadFacade;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderExtraService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerInvoiceDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class OrderInvoiceCoreServiceImpl implements OrderInvoiceCoreService {
    private static final String INVOICE_ID = "invoice_id";
    private static final String HIDE_APPLY_BUTTON = "hide_apply_button";
    private static final String ALLOW_VIEW_BUTTON = "allow_view_button";
    private static final String TRUE_STR = "true";

//    @Autowired
//    private ShopInvoiceFacade shopInvoiceFacade;

    @Autowired
    private OrderExtraService orderExtraService;
    @Autowired
    private OrderReadFacade orderReadFacade;
    @Autowired
    private TcOrderInvoiceRepository tcOrderInvoiceRepository;
    @Autowired
    private OrderFinanceFlowSyncConverter orderFinanceFlowSyncConverter;


    /**
     * 创建发票申请记录
     *
     * @param mainOrder
     * @param invoice
     * @return
     */
    @Override
    public TcOrderInvoiceDO createGeneralInvoice(MainOrder mainOrder, CustomerInvoiceDTO invoice) {
        ApplyInvoiceRpcReq rpcReq = buildApplyOrderInvoice(mainOrder, invoice);
        //远程请求
        String invoiceId = autoApplyOrderInvoice(rpcReq);
        TcOrderInvoiceDO tcOrderInvoiceDO = new TcOrderInvoiceDO();
        tcOrderInvoiceDO.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        tcOrderInvoiceDO.setMasterOrderAmount(String.valueOf(mainOrder.getOrderPrice().getOrderRealAmt()));
        tcOrderInvoiceDO.setInvoiceId(Long.valueOf(invoiceId));
        //销售类型
        SaleTypeEnum saleTypeEnum = orderFinanceFlowSyncConverter.getSaleType(mainOrder);
        if (Objects.nonNull(saleTypeEnum)) {
            tcOrderInvoiceDO.setSaleType(saleTypeEnum.getCode());
        }
        tcOrderInvoiceDO.setRequestNo(rpcReq.getRequestNo());
        tcOrderInvoiceDO.setInvoiceTitle(JSONObject.toJSONString(invoice));
        tcOrderInvoiceDO.setInvoiceApplyReq(JSONObject.toJSONString(rpcReq));
        tcOrderInvoiceDO.setInvoiceAmount(rpcReq.getBillAmount().toString());
        tcOrderInvoiceDO.setStatus(InvoiceStatusEnum.APPLYING.getCode());
        tcOrderInvoiceDO.setSellerId(mainOrder.getSeller().getSellerId());
        tcOrderInvoiceDO.setCustId(mainOrder.getCustomer().getCustId());
        //线上申请创建 发票申请单
        tcOrderInvoiceDO.setInvoiceType(InvoiceTypeEnum.GENERAL.getCode());
        tcOrderInvoiceRepository.create(tcOrderInvoiceDO);
        return tcOrderInvoiceDO;
    }

    @Override
    public void saveMainOrderExtend(TcOrderInvoiceDO tcOrderInvoiceDO) {
        //扩展字段填充
        OrderExtraSaveRpcReq save = new OrderExtraSaveRpcReq();
        save.setPrimaryOrderId(tcOrderInvoiceDO.getPrimaryOrderId());
        save.setAddFeatures(new HashMap<>());
        save.setRemoveFeatures(new HashSet<>());
        //普通发票 发票id
        save.getAddFeatures().put(INVOICE_ID, String.valueOf(tcOrderInvoiceDO.getId()));
        // 有值申请发票隐藏
        if (Objects.equals(tcOrderInvoiceDO.getStatus(), InvoiceStatusEnum.APPLYING.getCode()) ||
                Objects.equals(tcOrderInvoiceDO.getStatus(), InvoiceStatusEnum.APPLY_SUCCESS.getCode()) ||
                Objects.equals(tcOrderInvoiceDO.getStatus(), InvoiceStatusEnum.PART_APPLY_SUCCESS.getCode())) {
            // 专票-已开  普通发票-正在申请 或者已开
            save.getAddFeatures().put(HIDE_APPLY_BUTTON, TRUE_STR);
        } else {
            save.getRemoveFeatures().add(HIDE_APPLY_BUTTON);
        }
        // 默认隐藏，有值进行查看
        if ((Objects.equals(tcOrderInvoiceDO.getStatus(), InvoiceStatusEnum.APPLYING.getCode()) ||
                Objects.equals(tcOrderInvoiceDO.getStatus(), InvoiceStatusEnum.APPLY_SUCCESS.getCode()) ||
                Objects.equals(tcOrderInvoiceDO.getStatus(), InvoiceStatusEnum.PART_APPLY_SUCCESS.getCode()) ||
                Objects.equals(tcOrderInvoiceDO.getStatus(), InvoiceStatusEnum.REJECT_SUCCESS.getCode()))
                && Objects.equals(tcOrderInvoiceDO.getInvoiceType(), InvoiceTypeEnum.GENERAL.getCode())) {
            // 普通发票-正在申请 或者已开 或者作废 状态可进行发票查看
            save.getAddFeatures().put(ALLOW_VIEW_BUTTON, TRUE_STR);
        } else {
            save.getRemoveFeatures().add(ALLOW_VIEW_BUTTON);
        }
        orderExtraService.saveOrderExtras(save);
    }

    /**
     * 构建申请参数
     *
     * @param mainOrder
     * @param invoice
     * @return
     */
    private ApplyInvoiceRpcReq buildApplyOrderInvoice(MainOrder mainOrder, CustomerInvoiceDTO invoice) {
        ApplyInvoiceRpcReq applyInvoiceRpcReq = new ApplyInvoiceRpcReq();
//        // 客户Id 必填
//        applyInvoiceRpcReq.setUserId(mainOrder.getCustomer().getCustId());
//        //客户系统(比如：HAVANA) 必填
//        applyInvoiceRpcReq.setUserType(null);
//        // 上游系统 必填
//        applyInvoiceRpcReq.setRelatedSystem(null);
//        // 业务类型 必填
//        applyInvoiceRpcReq.setBizType(null);
//        // 对应上游系统的发票请求号 必填
//        applyInvoiceRpcReq.setRequestNo(UuidUtil.uuidWithoutUnderscore());
//        //发票材质（纸票 0 , 电票 1) 必填
//        applyInvoiceRpcReq.setInvoiceMaterial((byte) 1);
//        //发票类型（普票 1, 专票 2) 必填
//        applyInvoiceRpcReq.setInvoiceClass((byte) 1);
//        //购方名称（抬头) 必填
//        applyInvoiceRpcReq.setPurchaserName(invoice.getTitle());
////        if (applyInvoiceRpcReq.getInvoiceClass() == 2) {
//        //购方税号: 专票必填
//        applyInvoiceRpcReq.setPurchaserTaxNo(invoice.getDutyParagraph());
//        //购方地址及电话: 专票必填
//        applyInvoiceRpcReq.setPurchaserContactInfo(invoice.getTel());
//        //购方开户行及账号: 专票必填
//        applyInvoiceRpcReq.setPurchaserBankInfo(invoice.getBankAccount());
////        }
//        if (applyInvoiceRpcReq.getInvoiceMaterial() == 0) {
//            //邮寄地址（用于将纸票邮寄给商家）: 纸票必填
//            ApplyInvoiceRpcReq.Address address = new ApplyInvoiceRpcReq.Address();
//            //国家
//            address.setCountry("");
//            //省份 非空
//            address.setProvince("");
//            //城市 非空
//            address.setCity("");
//            //详细地址 非空
//            address.setDetailedAddress("");
//            //区
//            address.setDistrict("");
//            //街道
//            address.setStreet("");
//            //邮编
//            address.setZipCode("");
//            //收件人 非空
//            address.setRecipientName("");
//            //固定电话
//            address.setFixedNumber("");
//            //移动电话
//            address.setMobileNumber("");
//            //省市等拼接而成的详细地址
//            address.setFullAddress("");
//            //商家邮箱
//            address.setEmail("");
//            applyInvoiceRpcReq.setAddress(address);
//        }
//        //销方OU  （测试环境建议使用T50，配置较为齐全）
//        applyInvoiceRpcReq.setCompanyCode(null);
//        //发票备注，打印在发票上的备注栏位
//        applyInvoiceRpcReq.setMemo(null);
//        //发票上是否需要备注 必填
//        applyInvoiceRpcReq.setNeedInvoiceMemo(false);
//        //针对可以合并的明细行，是否需要合并 (合并条件参见1.6) 必填
//        applyInvoiceRpcReq.setNeedMerge(false);
//        //扩展信息
//        applyInvoiceRpcReq.setExtendInfo(null);
//        //上游系统主单号
//        applyInvoiceRpcReq.setMajorBillNo(null);
//        // 上游系统子单号
//        applyInvoiceRpcReq.setBillNo(null);
//        //上游系统账单类型
//        applyInvoiceRpcReq.setBillType(null);
//        //账单金额（单位：元）
////        applyInvoiceRpcReq.setBillAmount(new BigDecimal(PriceUtils.fen2Yuan(mainOrder.getOrderPrice().getOrderRealAmt())));
//        //币种（CNY） 非空
//        applyInvoiceRpcReq.setCurrency("CNY");
//        AtomicLong totalAmount = new AtomicLong();
//        AtomicInteger size = new AtomicInteger(0);
//        List<ApplyInvoiceRpcReq.SingleDetail> singleDetails = mainOrder.getSubOrders().stream()
//                .filter(item -> !Objects.equals(item.getOrderStatus(), OrderStatusEnum.REVERSAL_SUCCESS.getCode())
//                ).map(subOrder -> {
//                    //明细行列表  非空
//                    ApplyInvoiceRpcReq.SingleDetail singleDetail = new ApplyInvoiceRpcReq.SingleDetail();
//                    //含税金额  非空
//                    Long orderRealAmt;
//                    ConfirmPrice confirmPrice = subOrder.getOrderPrice().getConfirmPrice();
//                    if(Objects.nonNull(confirmPrice)){
//                        orderRealAmt= confirmPrice.getConfirmRealAmt();
//                    }else {
//                        orderRealAmt= subOrder.getOrderPrice().getOrderRealAmt();
//                    }
//                    singleDetail.setInvoiceAmountWithTax(new BigDecimal(String.valueOf(orderRealAmt)));
//                    totalAmount.addAndGet(orderRealAmt);
//                    size.incrementAndGet();
//                    //不含税金额
//                    singleDetail.setInvoiceAmountWithoutTax(null);
//                    //税额
//                    singleDetail.setTaxAmount(null);
//                    //设置税编税率
//                    setTaxInfo(singleDetail, subOrder);
//                    //币种 非空
//                    singleDetail.setCurrency("CNY");
//                    //货物名称 非空
//                    singleDetail.setCargoName(subOrder.getItemSku().getItemTitle());
//                    //货物类型"0: 服务类--一次性开票1: 服务类 --月度开票2：商品类  非空
//                    singleDetail.setCargoType((byte) 2);
//                    //数量 非空
//                    singleDetail.setQuantity(Long.valueOf(subOrder.getOrderQty()));
//                    //单价 非空
//                    singleDetail.setUnitPrice(BigDecimal.valueOf(subOrder.getOrderPrice().getItemPrice().getItemPrice()));
//                    //单位 非空
//                    singleDetail.setUnit("个");
//                    //规格型号 非空
//                    singleDetail.setSpecifications(subOrder.getItemSku().getSkuDesc());
//                    //零税率标识 0 出口退税1 免税2 不征税3 普通 0税率
//                    singleDetail.setZeroTaxRateCode(null);
//                    //折扣额(含税)
//                    singleDetail.setDiscountAmountWithTax(null);
//                    //折扣额(不含税)
//                    singleDetail.setDiscountAmountWithoutTax(null);
//                    return singleDetail;
//                }).collect(Collectors.toList());
//        applyInvoiceRpcReq.setDetailList(singleDetails);
//        if (size.get() == 0) {
//            throw new GmallException(OrderInvoiceErrorCode.NO_SUB_ORDER);
//        }
//        //账单金额（单位：元）
//        if (totalAmount.get() == 0) {
//            throw new GmallException(OrderInvoiceErrorCode.NO_CAN_APPLY);
//        }
//        applyInvoiceRpcReq.setBillAmount(new BigDecimal(String.valueOf(totalAmount.get())));
        return applyInvoiceRpcReq;
    }

    private void setTaxInfo(ApplyInvoiceRpcReq.SingleDetail singleDetail, SubOrder subOrder) {
        Map<String, String> featureMap = subOrder.getItemSku().getStoredExt();
        String taxCode = null;
        String taxRate = null;
        //订单上取税编
        if (hasTaxInfoOnOrder(featureMap)) {
            taxCode = featureMap.get(OrderFeatureKey.TAX_CODE);
            taxRate = featureMap.get(OrderFeatureKey.TAX_RATE);
        }

        if (StringUtils.isBlank(taxCode) || StringUtils.isBlank(taxRate)) {
            log.error("taxcode or taxrate is empty: taxcode = " + taxCode + ";taxrate=" + taxRate);
            return;
        }
        //税收编码
        singleDetail.setTaxCode(taxCode);
        Integer taxRateInt = Integer.parseInt(taxRate);
        //税率、前台已校验传入为整数
        singleDetail.setTaxRate(BigDecimal.valueOf((double) taxRateInt / 100));

    }

    private boolean hasTaxInfoOnOrder(Map<String, String> featureMap) {
        return MapUtils.isNotEmpty(featureMap) && StringUtils.isNotBlank(featureMap.get(OrderFeatureKey.TAX_CODE))
                && StringUtils.isNotBlank(featureMap.get(OrderFeatureKey.TAX_RATE));
    }

    private String autoApplyOrderInvoice(ApplyInvoiceRpcReq rpcReq) {
//        try {
//            RpcResponse<ApplyInvoiceResponse> rpcResponse = shopInvoiceFacade.applyInvoice(rpcReq);
//            if (!rpcResponse.isSuccess() || !Objects.equals(rpcResponse.getData().getResCode(), "0")) {
//                log.error("申请发票失败，参数:{},失败原因:{}", JSONObject.toJSONString(rpcReq), rpcResponse.getData().getResMsg());
//                throw new GmallException(OrderInvoiceErrorCode.REMOTE_REQUEST_FAILED);
//            }
//            log.info("申请发票成功，参数{},返回结果:{}", JSONObject.toJSONString(rpcReq), rpcResponse.getData().getData());
//            return rpcResponse.getData().getData();
//        } catch (Exception e) {
//            log.error("申请发票异常，参数:{},失败原因:{}", JSONObject.toJSONString(rpcReq), e);
//            throw new GmallException(OrderInvoiceErrorCode.REMOTE_REQUEST_FAILED);
//        }
        return "";
    }

    @Override
    public void closeApplyInvoice(Long primaryOrder) {
        // 关闭申请通道
        Map<String, String> attrs = new HashMap<>();
        attrs.put(HIDE_APPLY_BUTTON, "true");
        OrderExtraSaveRpcReq save = new OrderExtraSaveRpcReq();
        save.setPrimaryOrderId(primaryOrder);
        save.setAddFeatures(attrs);
        orderExtraService.saveOrderExtras(save);
    }

    @Override
    public Boolean downLoadInvoice(TcOrderInvoiceDO tcOrderInvoiceDO) {
//        if (Objects.equals(InvoiceTypeEnum.SPECIAL.getCode(), tcOrderInvoiceDO.getInvoiceType())) {
//            return false;
//        }
//        DownloadInvoiceRpcReq rpcReq = new DownloadInvoiceRpcReq();
//        rpcReq.setInvoiceCode(tcOrderInvoiceDO.getInvoiceCode());
//        rpcReq.setInvoiceNo(tcOrderInvoiceDO.getInvoiceNo());
//        rpcReq.setRelatedSystem(null);
//        try {
//            RpcResponse<DownloadInvoiceResponse> rpcResponse = shopInvoiceFacade.downloadInvoice(rpcReq);
//            if (!rpcResponse.isSuccess() || !Objects.equals(rpcResponse.getData().getResCode(), "0")) {
//                log.error("下载发票失败，参数:{},失败原因:{}", JSONObject.toJSONString(rpcReq), rpcResponse.getData().getResMsg());
//                return false;
//            }
//            log.info("下载发票成功，参数{},返回结果:{}", JSONObject.toJSONString(rpcReq), rpcResponse.getData().getData());
//            tcOrderInvoiceDO.setPdfKey(rpcResponse.getData().getData());
//            tcOrderInvoiceDO.setPngKey(rpcResponse.getData().getPngKey());
//            return true;
//        } catch (Exception e) {
//            log.error("下载发票异常，参数:{},失败原因:{}", JSONObject.toJSONString(rpcReq), e);
//            return false;
//        }
        return Boolean.TRUE;
    }

    @Override
    public void getInvoiceUrl(TcOrderInvoiceDO tcOrderInvoiceDO) {
//        if (Objects.equals(InvoiceTypeEnum.SPECIAL.getCode(), tcOrderInvoiceDO.getInvoiceType())) {
//            //专票不需要下载
//            return;
//        }
//        if (Objects.equals(tcOrderInvoiceDO.getStatus(), InvoiceStatusEnum.APPLYING.getCode())) {
//            //代审批的不需要下载
//            return;
//        }
//        if (StringUtils.isBlank(tcOrderInvoiceDO.getInvoiceCode()) ||
//                StringUtils.isBlank(tcOrderInvoiceDO.getInvoiceNo())) {
//            //关键参数不存在
//            return;
//        }
//        if (StringUtils.isBlank(tcOrderInvoiceDO.getPdfKey()) ||
//                StringUtils.isBlank(tcOrderInvoiceDO.getPngKey())) {
//            //重新下载数据
//            if (downLoadInvoice(tcOrderInvoiceDO)) {
//                //更新数据表结构
//                tcOrderInvoiceRepository.updateById(tcOrderInvoiceDO);
//            } else {
//                return;
//            }
//        }
//        InvoiceUrlRpcReq rpcReq = new InvoiceUrlRpcReq();
//        rpcReq.setPdfKey(tcOrderInvoiceDO.getPdfKey());
//        rpcReq.setPngKey(tcOrderInvoiceDO.getPngKey());
//        try {
//            RpcResponse<InvoiceUrlResponse> rpcResponse = shopInvoiceFacade.getUrlByOssKey(rpcReq);
//            if (!rpcResponse.isSuccess() || !Objects.equals(rpcResponse.getData().getResCode(), "0")) {
//                log.error("获取发票失败，参数:{},失败原因:{}", JSONObject.toJSONString(rpcReq), rpcResponse.getData().getResMsg());
//                throw new GmallException(OrderInvoiceErrorCode.REMOTE_REQUEST_FAILED);
//            }
//            log.info("下载发票成功，参数{},返回结果:{}", JSONObject.toJSONString(rpcReq), JSONObject.toJSONString(rpcResponse.getData()));
//            tcOrderInvoiceDO.setPdfKey(rpcResponse.getData().getPdfUrl());
//            tcOrderInvoiceDO.setPngKey(rpcResponse.getData().getPngUrl());
//        } catch (Exception e) {
//            log.error("申请发票异常，参数:{},失败原因:{}", JSONObject.toJSONString(rpcReq), e);
//        }

    }

    @Override
    public OrderExtendDTO getOrderInvoiceInfo(Long primaryOrder, String extendKey) {
        List<OrderExtendDTO> data = getOrderInvoiceInfo(primaryOrder, extendKey, extendKey);
        if (data != null && data.size() > 0) {
            return data.get(0);
        }
        return null;
    }


    @Override
    public List<OrderExtendDTO> getOrderInvoiceInfo(Long primaryOrder, String extendType, String extendKey) {
        OrderExtendQueryRpcReq extendQueryRpcReq = new OrderExtendQueryRpcReq();
        extendQueryRpcReq.setPrimaryOrderId(primaryOrder);
        extendQueryRpcReq.setExtendKey(extendKey);
        extendQueryRpcReq.setExtendType(extendType);
        RpcResponse<List<OrderExtendDTO>> extendResult = orderReadFacade.queryOrderExtend(extendQueryRpcReq);
        if (extendResult.isSuccess()) {
            List<OrderExtendDTO> data = extendResult.getData();
            if (data != null && data.size() > 0) {
                return data;
            }
        }
        return null;
    }

    /**
     * 确认收获后  1、 写入TcOrderExtendDO 记录 2、发起申请接口
     *
     * @return
     */

    @Override
    public Boolean returnInvoice(TcOrderInvoiceDO tcOrderInvoiceDO) {
//        //1、发起申请接口
//        ReturnInvoiceRpcReq rpcReq = new ReturnInvoiceRpcReq();
//        // 上游系统 非空
//        rpcReq.setRelatedSystem(null);
//        // 业务类型 非空
//        rpcReq.setBizType(null);
//        //对应上游系统的发票请求号
//        rpcReq.setRequestNo(tcOrderInvoiceDO.getRequestNo());
//        //退票物流单号
//        rpcReq.setTrackingNumber(null);
//        //物流企业
//        rpcReq.setLogisticCompany(null);
//        // 退票原因类型 非空 0	发票信息错误,1	发票类型错误,2	退款,3	电子换纸质,4	其他
//        rpcReq.setReasonType((byte) 0);
//        //退票原因 非空
//        rpcReq.setExchangeReason(I18NMessageUtils.getMessage("info.error"));  //# "信息填写错误"
//        //退票时间, e.g. \"2018-12-12 09:20:02\" 非空
//        rpcReq.setApplyDate(DateUtils.toDateString(new Date()));
//        try {
//            RpcResponse<ReturnInvoiceResponse> rpcResponse = shopInvoiceFacade.returnInvoice(rpcReq);
//            if (!rpcResponse.isSuccess() || !Objects.equals(rpcResponse.getData().getResCode(), "0")) {
//                log.error("退票失败，参数:{},失败原因:{}", JSONObject.toJSONString(rpcReq), rpcResponse.getData().getResMsg());
//                throw new GmallException(OrderInvoiceErrorCode.REMOTE_REQUEST_FAILED);
//            }
//            log.info("退票成功，参数{},返回结果:{}", JSONObject.toJSONString(rpcReq), rpcResponse.getData().getData());
//            return true;
//        } catch (Exception e) {
//            log.error("退票异常，参数:{},失败原因:{}", JSONObject.toJSONString(rpcReq), e);
//            return false;
//        }
        return Boolean.TRUE;
    }


    @Override
    public Boolean rejectInvoice(TcOrderInvoiceDO tcOrderInvoiceDO) {
//        //1、发起申请接口
//        RejectInvoiceRpcReq rpcReq = new RejectInvoiceRpcReq();
//        //上游系统 必填
//        rpcReq.setRelatedSystem(null);
//        //汇金平台发票ID。（开票请求后，会受到UNISSED消息，消息体中会带有invoiceId）")
////        rpcReq.setInvoiceId(tcOrderInvoiceDO.getInvoiceId());
//        //对应上游系统的发票请求号
//        rpcReq.setRequestNo(tcOrderInvoiceDO.getRequestNo());
//        //撤销原因 必填
//        rpcReq.setRejectReason(I18NMessageUtils.getMessage("info.error"));  //# "信息填写错误"
//        //申请人 必填
//        rpcReq.setApplier(tcOrderInvoiceDO.getSellerId().toString());
//        try {
//            RpcResponse<RejectInvoiceResponse> rpcResponse = shopInvoiceFacade.rejectInvoice(rpcReq);
//            if (!rpcResponse.isSuccess() || !Objects.equals(rpcResponse.getData().getResCode(), "0")) {
//                log.error("撤销发票失败，参数:{},失败原因:{}", JSONObject.toJSONString(rpcReq), rpcResponse.getData().getResMsg());
//                throw new GmallException(OrderInvoiceErrorCode.REMOTE_REQUEST_FAILED);
//            }
//            log.info("撤销发票成功，参数{},返回结果:{}", JSONObject.toJSONString(rpcReq), rpcResponse.getData().getData());
//            return true;
//        } catch (Exception e) {
//            log.error("撤销发票异常，参数:{},失败原因:{}", JSONObject.toJSONString(rpcReq), e);
//            throw new GmallException(OrderInvoiceErrorCode.REMOTE_REQUEST_FAILED);
//        }
        return Boolean.TRUE;
    }


    @Override
    public RpcResponse<Boolean> acceptReturnRejectInvoiceMessage(InvoiceReturnRejectMessageRpcReq req) {
//        log.info("退票申请驳回message:{}", JSONObject.toJSONString(req));
//        return RpcServerUtils.invoke(() -> {
//            TcOrderInvoiceDO tcOrderInvoiceDO = tcOrderInvoiceRepository.queryByInvoiceId(req.getInvoiceId());
//            if (Objects.isNull(tcOrderInvoiceDO)) {
//                throw new GmallException(OrderInvoiceErrorCode.NO_ORDER_INVOICE);
//            }
//            //申请失败，原因
//            if (req.getIsSuccess()) {
//                //同意 作废
//                tcOrderInvoiceDO.setInvoiceReject(InvoiceRejectEnum.APPLY_AGREE.getCode());
//                tcOrderInvoiceDO.setStatus(InvoiceStatusEnum.REJECT_SUCCESS.getCode());
//            } else {
//                //拒绝作废申请
//                tcOrderInvoiceDO.setInvoiceReject(InvoiceRejectEnum.APPLY_REJECT.getCode());
//            }
//            tcOrderInvoiceDO.setInvoiceRejectReason(req.getRejectReason());
//            tcOrderInvoiceRepository.updateById(tcOrderInvoiceDO);
//            saveMainOrderExtend(tcOrderInvoiceDO);
//            return RpcResponse.ok(true);
//        }, "acceptReturnRejectInvoiceMessage");
        return RpcResponse.ok(Boolean.TRUE);
    }


    /**
     * 2、税务发票平台成功开具发票后发送
     * 包括蓝票的开具，以及红票的开具
     * "EXCHANGE: 退票导致的作废/冲红（客户主动发起的退票）
     *
     * @param req
     * @return
     */
    @Override
    public RpcResponse acceptOrderInvoiceMessage(InvoiceApplyMessageRpcReq req) {
//        log.info("税务发票平台成功开具发票后发送message:{}", JSONObject.toJSONString(req));
//        return RpcServerUtils.invoke(() -> {
//            TcOrderInvoiceDO tcOrderInvoiceDO = tcOrderInvoiceRepository.queryByRequestNo(req.getRequestNo());
//            if (Objects.isNull(tcOrderInvoiceDO)) {
//                throw new GmallException(OrderInvoiceErrorCode.NO_ORDER_INVOICE);
//            }
//            tcOrderInvoiceDO.setInvoiceId(req.getId());
//            tcOrderInvoiceDO.setInvoiceCode(req.getInvoiceCode());
//            tcOrderInvoiceDO.setInvoiceNo(req.getInvoiceNo());
//            tcOrderInvoiceDO.setInvoiceTime(req.getInvoiceDate());
//            tcOrderInvoiceDO.setInvoiceReject(InvoiceRejectEnum.NO_APPLY.getCode());
//            tcOrderInvoiceDO.setInvoiceResult(JSONObject.toJSONString(req));
//            //下载发票
//            downLoadInvoice(tcOrderInvoiceDO);
//            //如果当前发票状态不是申领中 的普通发票，说明发票被撤回了，消息出现延迟
//            if (Objects.equals(tcOrderInvoiceDO.getStatus(), InvoiceStatusEnum.APPLYING.getCode())) {
//                //如果当前不是申领中 保持订单和发票关联状态
//                tcOrderInvoiceDO.setStatus(InvoiceStatusEnum.APPLY_SUCCESS.getCode());
//                saveMainOrderExtend(tcOrderInvoiceDO);
//            }
//            tcOrderInvoiceRepository.updateById(tcOrderInvoiceDO);
//            return RpcResponse.ok(null);
//        }, "acceptOrderInvoiceMessage");
        return RpcResponse.ok(null);
    }


    /**
     * 发票退回
     *
     * @param req
     * @return
     */
    @Override
    public RpcResponse<Boolean> acceptRejectInvoiceMessage(InvoiceRejectMessageRpcReq req) {
//        log.info("税务发票平台发票退回通知:{}", JSONObject.toJSONString(req));
//        return RpcServerUtils.invoke(() -> {
//            TcOrderInvoiceDO tcOrderInvoiceDO = tcOrderInvoiceRepository.queryByRequestNo(req.getRequestNo());
//            if (Objects.isNull(tcOrderInvoiceDO)) {
//                throw new GmallException(OrderInvoiceErrorCode.NO_ORDER_INVOICE);
//            }
//            //发票退回
//            tcOrderInvoiceDO.setInvoiceReject(InvoiceRejectEnum.APPLY_FAILURE.getCode());
//            tcOrderInvoiceDO.setInvoiceRejectReason(req.getRejectReason());
//            tcOrderInvoiceDO.setStatus(InvoiceStatusEnum.APPLY_FAILURE.getCode());
//            tcOrderInvoiceRepository.updateById(tcOrderInvoiceDO);
//            saveMainOrderExtend(tcOrderInvoiceDO);
//            return RpcResponse.ok(true);
//        }, "acceptRejectInvoiceMessage");
        return RpcResponse.ok(Boolean.TRUE);
    }

}
