package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.input.EPayTokenRpcReq;
import com.aliyun.gts.gmall.platform.pay.api.constant.PayErrorCode;
import com.aliyun.gts.gmall.platform.pay.api.dto.input.*;
import com.aliyun.gts.gmall.platform.pay.api.dto.input.inner.*;
import com.aliyun.gts.gmall.platform.pay.api.dto.output.ConfirmPayRpcResp;
import com.aliyun.gts.gmall.platform.pay.api.dto.output.OrderMergePayRpcResp;
import com.aliyun.gts.gmall.platform.pay.api.dto.output.OrderPayRpcResp;
import com.aliyun.gts.gmall.platform.pay.api.dto.output.OrderRefundRpcResp;
import com.aliyun.gts.gmall.platform.pay.api.dto.output.dto.OrderPayDTO;
import com.aliyun.gts.gmall.platform.pay.api.dto.output.dto.PayChannelDTO;
import com.aliyun.gts.gmall.platform.pay.api.dto.output.dto.PayFlowDTO;
import com.aliyun.gts.gmall.platform.pay.api.enums.PayStatusEnum;
import com.aliyun.gts.gmall.platform.pay.api.enums.PayTypeEnum;
import com.aliyun.gts.gmall.platform.pay.api.facade.OrderPayReadFacade;
import com.aliyun.gts.gmall.platform.pay.api.facade.OrderPayWriteFacade;
import com.aliyun.gts.gmall.platform.promotion.common.util.JsonUtils;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.TradeExtendKeyConstants;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelReasonFeeDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcCancelRefundLogDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.RefundFee;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderPayInfo;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.stepOrder.StepOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.PayChannel;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.ToPayData;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.ToPayData.PayInfo;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.ToRefundData;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.ConfirmPrice;
import com.aliyun.gts.gmall.platform.trade.domain.entity.price.SeparateRule;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CancelReasonFeeRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.OrderPayRepository;
import com.aliyun.gts.gmall.platform.trade.domain.util.ErrorUtils;
import com.aliyun.gts.gmall.platform.trade.domain.util.NumUtils;
import com.aliyun.gts.gmall.platform.trade.domain.util.StepOrderUtils;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcCancelRefundLogMapper;
import com.aliyun.gts.gmall.platform.trade.persistence.rpc.converter.PayChannelConverter;
import com.aliyun.gts.gmall.platform.trade.persistence.rpc.util.RpcUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Component
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "trade", name = "orderPayRepository", havingValue = "default", matchIfMissing = true)
public class OrderPayRepositoryImpl implements OrderPayRepository {

    @Autowired
    private OrderPayWriteFacade orderPayWriteFacade;

    @Autowired
    private OrderPayReadFacade orderPayReadFacade;

    @Autowired
    private PayChannelConverter payChannelConverter;

    @Autowired
    private TcCancelRefundLogMapper tcCancelRefundLogMapper;

    @Autowired
    private CancelReasonFeeRepository cancelReasonFeeRepository;

    @NacosValue(value = "${trade.pay.mock:false}", autoRefreshed = true)
    private boolean mockPay;

    @Value("${trade.pay.returnUrl:}")
    private String returnUrlConf;
    private Map<String, String> returnUrlMap;

    @Value("${trade.pay.quitUrl:}")
    private String quitUrlConf;
    private Map<String, String> quitUrlMap;

    @Value("${trade.pay.alipay.platformAccount:}")
    private String aliPlatformAccount;
    @Value("${trade.pay.alipay.platformMchid:}")
    private String aliPlatformMchid;
    @Value("${trade.pay.alipay.sellerMchidKey:}")
    private String aliSellerMchidKey;
    @Value("${trade.pay.alipay.channelCode:}")
    private String aliChannelCode;
    @Value("${trade.pay.wxpay.platformAccount:}")
    private String wxPlatformAccount;
    @Value("${trade.pay.wxpay.platformMchid:}")
    private String wxPlatformMchid;
    @Value("${trade.pay.wxpay.sellerMchidKey:}")
    private String wxSellerMchidKey;
    @Value("${trade.pay.wxpay.channelCode:}")
    private String wxChannelCode;


    @Value("${trade.pay.epay.platformAccount:}")
    private String epayPlatformAccount;
    @Value("${trade.pay.epay.platformMchid:}")
    private String epayPlatformMchid;
    @Value("${trade.pay.epay.channelCode:}")
    private String epayChannelCode;
    @Value("${trade.pay.epay.payChannel:108}")
    private String epayPayChannel;

    @Value("${trade.pay.pointpay.channelCode:}")
    private String pointChannelCode;

    private static final Cache<Long, List<PayChannel>> payChannelCache =
        CacheBuilder
        .newBuilder()
        .expireAfterWrite(60, TimeUnit.SECONDS).build();


    @Override
    public ToPayData toPay(MainOrder mainOrder) {
        // 请求对象
        OrderPayRpcReq req = buildToPayRequest(mainOrder);
        //
        RpcResponse<OrderPayRpcResp> resp =
            RpcUtils.invokeRpc(() -> orderPayWriteFacade.toPay(req),
                "toPay",
                I18NMessageUtils.getMessage("initiate.payment"),
                mainOrder.getPrimaryOrderId()
            );  //# "发起支付"
        OrderPayRpcResp data = resp.getData();
        return ToPayData.builder()
            .payData(data.getPayData())
            .cartId(data.getCartId())
            .payInfos(Collections.singletonList(PayInfo.builder()
            .payId(data.getPayId())
            .payFlowId(data.getPayFlowId())
            .primaryOrderId(mainOrder.getPrimaryOrderId().toString())
            .build()))
            .build();
    }

    protected OrderPayRpcReq buildToPayRequest(MainOrder mainOrder) {
        OrderPayRpcReq req = new OrderPayRpcReq();
        req.setPrimaryOrderId(mainOrder.getPrimaryOrderId().toString());
        req.setCustId(mainOrder.getCustomer().getCustId().toString());
        req.setCustName(mainOrder.getCustomer().getCustName());
        req.setSellerId(mainOrder.getSeller().getSellerId().toString());
        req.setSellerName(mainOrder.getSeller().getSellerName());
        req.setOrderChannel(mainOrder.getOrderChannel());
        req.setStepNo(mainOrder.orderAttr().getCurrentStepNo());
        req.setAllowChangeFee(true);
        req.setMockPay(mockPay);

        // 商品
        List<PayItemInfo> items = mainOrder.getSubOrders().stream().map(sub -> {
            PayItemInfo item = new PayItemInfo();
            item.setItemId(sub.getItemSku().getItemId().toString());
            item.setItemTitle(sub.getItemSku().getItemTitle());
            item.setItemPrice(sub.getItemSku().getItemPrice().getItemPrice());
            item.setQuantity(sub.getOrderQty());
            return item;
        }).collect(Collectors.toList());
        req.setItems(items);

        // 支付渠道
        OrderPayInfo payInfo = mainOrder.getCurrentPayInfo();
        String payChannel = payInfo.getPayChannel();
        if (StringUtils.isBlank(payChannel)) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG, "payChannel"+I18NMessageUtils.getMessage("is.empty"));  //# 为空"
        }
        List<PayChannelInfo> channels = new ArrayList<>();
        if (NumUtils.getNullZero(payInfo.getPayPrice().getPointCount()) > 0) {
            PayChannelInfo channel = new PayChannelInfo();
            channel.setPayChannel(pointChannelCode);
            channel.setPayAmt(0L);
            channel.setPayQuantity(BigDecimal.valueOf(payInfo.getPayPrice().getPointCount()));
            channels.add(channel);
        }
        if (!payChannel.equals(pointChannelCode)) {
            PayChannelInfo channel = new PayChannelInfo();
            channel.setPayChannel(payChannel);
            channel.setPayAmt(payInfo.getPayPrice().getOrderRealAmt());
            channel.setPayQuantity(BigDecimal.ZERO);
            channels.add(channel);
        }
        req.setPayChannels(channels);

        // 账号
        if (payChannel.equals(aliChannelCode)) {
            AccountInfo platform = new AccountInfo();
            platform.setAccountNo(aliPlatformAccount);
            platform.setMerchantId(aliPlatformMchid);
            req.setPlatformAccount(platform);

            AccountInfo seller = new AccountInfo();
            seller.setAccountNo(mainOrder.getSeller().getSellerAccountInfo().getAlipayAccountNo());
            seller.setMerchantId(mainOrder.getSeller().getSellerAccountInfo().getExtendAccountInfos().get(aliSellerMchidKey));
            req.setSellerAccount(seller);
        } else if (payChannel.equals(wxChannelCode)) {
            AccountInfo platform = new AccountInfo();
            platform.setAccountNo(wxPlatformAccount);
            platform.setMerchantId(wxPlatformMchid);
            req.setPlatformAccount(platform);

            AccountInfo seller = new AccountInfo();
            seller.setAccountNo(mainOrder.getSeller().getSellerAccountInfo().getWechatAccountNo());
            seller.setMerchantId(mainOrder.getSeller().getSellerAccountInfo().getExtendAccountInfos().get(wxSellerMchidKey));
            req.setSellerAccount(seller);
        } else if (payChannel.equals(epayChannelCode)) {
            AccountInfo platform = new AccountInfo();
            platform.setAccountNo(epayPlatformAccount);
            platform.setMerchantId(epayPlatformMchid);
            req.setPlatformAccount(platform);

            // halyk 直接付款给halyk 银行
            //AccountInfo seller = new AccountInfo();
            //seller.setAccountNo(mainOrder.getSeller().getSellerAccountInfo().getWechatAccountNo());
            //seller.setMerchantId(mainOrder.getSeller().getSellerAccountInfo().getExtendAccountInfos().get(wxSellerMchidKey));
            //req.setSellerAccount(seller);
        }

        // 跳转地址
        req.setReturnUrl(getReturnUrl(mainOrder.getOrderChannel()));
        req.setQuitUrl(getQuitUrl(mainOrder.getOrderChannel()));

        // 扩展信息
        req.setExtraInfo((Map) mainOrder.getExtra(EXT_PAY_INFO));
        Map<String, String> bizMap = Optional.ofNullable(req.getBizFeature()).orElse(new HashMap<>());
        mainOrder.getExtraMap().forEach(new BiConsumer<String, Object>() {
            @Override
            public void accept(String k, Object v) {
                if(v instanceof String){
                    bizMap.put(k , v.toString());
                }
            }
        });
        req.setBizFeature(bizMap);

        return req;
    }

    private String getReturnUrl(String orderChannel) {
        if (returnUrlMap == null) {
            returnUrlMap = initUrlMap(returnUrlConf);
        }
        String url = returnUrlMap.get(orderChannel);
        if (StringUtils.isBlank(url)) {
            url = returnUrlMap.get("default");
        }
        return url;
    }

    private String getQuitUrl(String orderChannel) {
        if (quitUrlMap == null) {
            quitUrlMap = initUrlMap(quitUrlConf);
        }
        String url = quitUrlMap.get(orderChannel);
        if (StringUtils.isBlank(url)) {
            url = quitUrlMap.get("default");
        }
        return url;
    }

    private static Map<String, String> initUrlMap(String conf) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isBlank(conf)) {
            return map;
        }
        try {
            JSONObject json = JSON.parseObject(conf);
            for (Entry<String, Object> en : json.entrySet()) {
                map.put(en.getKey(), String.valueOf(en.getValue()));
            }
        } catch (Exception e) {
            log.error("", e);
            // ignore
        }
        return map;
    }

    @Override
    public ToPayData toMergePay(List<MainOrder> mainOrders) {
        OrderMergePayRpcReq req = buildMergePayRequest(mainOrders);
        RpcResponse<OrderMergePayRpcResp> resp = RpcUtils.invokeRpc(() ->
            orderPayWriteFacade.toMergePay(req),
            "toMergePay",
            I18NMessageUtils.getMessage("initiate.merge.payment"),  //# "发起合并支付"
            mainOrders.stream().map(MainOrder::getPrimaryOrderId).collect(Collectors.toList())
        );
        OrderMergePayRpcResp data = resp.getData();
        return ToPayData.builder()
            .payData(data.getPayData())
            .cartId(data.getCartId())
            .payInfos(data.getMergePayFlows().stream().map(pf -> PayInfo.builder()
                .payId(pf.getPayId())
                .payFlowId(pf.getPayFlowId())
                .primaryOrderId(pf.getPrimaryOrderId())
                .build()).collect(Collectors.toList()))
            .build();
    }

    /**
     * 构建参数
     * @param mainOrders
     * @return
     */
    protected OrderMergePayRpcReq buildMergePayRequest(List<MainOrder> mainOrders) {
        List<MergePayInfo> payList = mainOrders.stream().map(mainOrder -> {
            MergePayInfo pay = new MergePayInfo();
            pay.setPrimaryOrderId(mainOrder.getPrimaryOrderId().toString());
            pay.setSellerId(mainOrder.getSeller().getSellerId().toString());
            pay.setSellerName(mainOrder.getSeller().getSellerName());
            pay.setStepNo(mainOrder.orderAttr().getCurrentStepNo());
            // 商品
            List<PayItemInfo> items = mainOrder.getSubOrders().stream().map(sub -> {
                PayItemInfo item = new PayItemInfo();
                item.setItemId(sub.getItemSku().getItemId().toString());
                item.setItemTitle(sub.getItemSku().getItemTitle());
                item.setItemPrice(sub.getItemSku().getItemPrice().getItemPrice());
                item.setQuantity(sub.getOrderQty());
                return item;
            }).collect(Collectors.toList());
            pay.setItems(items);
            // 支付渠道
            OrderPayInfo payInfo = mainOrder.getCurrentPayInfo();
            String payChannel = payInfo.getPayChannel();
            if (StringUtils.isBlank(payChannel)) {
                throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG, "[payChannel]" + I18NMessageUtils.getMessage("is.empty"));  //# 为空"
            }
            List<PayChannelInfo> channels = new ArrayList<>();
            if (NumUtils.getNullZero(payInfo.getPayPrice().getPointCount()) > 0) {
                PayChannelInfo channel = new PayChannelInfo();
                channel.setPayChannel(pointChannelCode);
                channel.setPayAmt(0L);
                channel.setPayQuantity(BigDecimal.valueOf(payInfo.getPayPrice().getPointCount()));
                channels.add(channel);
            }
            if (!payChannel.equals(pointChannelCode)) {
                PayChannelInfo channel = new PayChannelInfo();
                channel.setPayChannel(payChannel);
                channel.setPayAmt(payInfo.getPayPrice().getOrderRealAmt());
                channel.setPayQuantity(BigDecimal.ZERO);
                channels.add(channel);
            }
            pay.setPayChannels(channels);
            // 账号
            if (payChannel.equals(aliChannelCode)) {
                AccountInfo seller = new AccountInfo();
                seller.setAccountNo(mainOrder.getSeller().getSellerAccountInfo().getAlipayAccountNo());
                seller.setMerchantId(mainOrder.getSeller().getSellerAccountInfo().getExtendAccountInfos().get(aliSellerMchidKey));
                pay.setSellerAccount(seller);
            } else if (payChannel.equals(wxChannelCode)) {
                AccountInfo seller = new AccountInfo();
                seller.setAccountNo(mainOrder.getSeller().getSellerAccountInfo().getWechatAccountNo());
                seller.setMerchantId(mainOrder.getSeller().getSellerAccountInfo().getExtendAccountInfos().get(wxSellerMchidKey));
                pay.setSellerAccount(seller);
            }
            return pay;
        }).collect(Collectors.toList());

        // 买家信息、在线支付的渠道 等由调用者保证必须相同
        MainOrder head = mainOrders.get(0);
        String payChannel = head.getCurrentPayInfo().getPayChannel();
        OrderMergePayRpcReq req = new OrderMergePayRpcReq();
        req.setPayList(payList);
        req.setCustId(head.getCustomer().getCustId().toString());
        req.setCustName(head.getCustomer().getCustName());
        req.setOrderChannel(head.getOrderChannel());
        req.setAllowChangeFee(true);
        req.setMockPay(mockPay);

        // 平台账号
        if (payChannel.equals(aliChannelCode)) {
            AccountInfo platform = new AccountInfo();
            platform.setAccountNo(aliPlatformAccount);
            platform.setMerchantId(aliPlatformMchid);
            req.setPlatformAccount(platform);
        } else if (payChannel.equals(wxChannelCode)) {
            AccountInfo platform = new AccountInfo();
            platform.setAccountNo(wxPlatformAccount);
            platform.setMerchantId(wxPlatformMchid);
            req.setPlatformAccount(platform);
        } else if (payChannel.equals(epayChannelCode)) {
            AccountInfo platform = new AccountInfo();
            platform.setAccountNo(epayPlatformAccount);
            platform.setMerchantId(epayPlatformMchid);
            req.setPlatformAccount(platform);
        }
        // 跳转地址
        req.setReturnUrl(getReturnUrl(mainOrders.get(0).getOrderChannel()));
        req.setQuitUrl(getQuitUrl(mainOrders.get(0).getOrderChannel()));
        // 扩展信息
        req.setExtraInfo((Map) mainOrders.get(0).getExtra(EXT_PAY_INFO));
        return req;
    }

    @Override
    public boolean confirmPay(MainOrder mainOrder, String payFlowId) {
        // 查支付流水单
        PayFlowDTO payFlow;
        {
            PayFlowIdQueryRpcReq req = new PayFlowIdQueryRpcReq();
            req.setCustId(mainOrder.getCustomer().getCustId().toString());
            req.setPayFlowId(payFlowId);
            req.setCartId(mainOrder.getCartId());
            RpcResponse<PayFlowDTO> resp = RpcUtils.invokeRpc(
                () -> orderPayReadFacade.queryPayFlowById(req),
                "queryPayFlowById",
                I18NMessageUtils.getMessage("query.pay.tx"),  //# "查支付流水单"
                payFlowId
            );
            payFlow = resp.getData();
            if (payFlow == null) {
                return false;
            }
        }
        // 确认支付结果
        {
            ConfirmPayRpcReq req = new ConfirmPayRpcReq();
            req.setPayId(payFlow.getPayId());
            req.setPayFlowId(payFlowId);
            RpcResponse<ConfirmPayRpcResp> resp = RpcUtils.invokeRpc(
                () -> orderPayWriteFacade.confirmPay(req),
                "confirmPay",
                I18NMessageUtils.getMessage("confirm.pay"),  //# "确认支付"
                mainOrder.getPrimaryOrderId().toString()
            );
            return resp.getData().isPaySuccess();
        }
    }


    @Override
    public void cancelPay(MainOrder mainOrder, Integer stepNo) {
        PayCancelRpcReq req = buildCancelRequest(mainOrder, stepNo);
        RpcUtils.invokeRpc(
                () -> orderPayWriteFacade.cancelPay(req),
                "cancelPay", I18NMessageUtils.getMessage("cancel.pay"),  //# "取消支付"
                mainOrder.getPrimaryOrderId().toString(),
                fail -> {
                    // 忽略支付单不存在的情况
                    return fail != null && PayErrorCode.PAY_ORDER_NOT_EXISTS.getCode().equals(fail.getCode());
                });
    }

    protected PayCancelRpcReq buildCancelRequest(MainOrder mainOrder, Integer stepNo) {
        PayCancelRpcReq req = new PayCancelRpcReq();
        req.setPrimaryOrderId(mainOrder.getPrimaryOrderId().toString());
        req.setCustId(mainOrder.getCustomer().getCustId().toString());
        req.setStepNo(stepNo);
        return req;
    }

    @Override
    public void settle(MainOrder mainOrder, Integer stepNo) {
        PaySettleRpcReq req = buildSettleRequest(mainOrder, stepNo);
        if (req != null) {
            RpcUtils.invokeRpc(() -> orderPayWriteFacade.settle(req),
                "settle", I18NMessageUtils.getMessage("settlement"),  //# "结算"
                mainOrder.getPrimaryOrderId().toString());
        }
    }

    protected PaySettleRpcReq buildSettleRequest(MainOrder mainOrder, Integer stepNo) {
        OrderPayInfo payInfo = mainOrder.getPayInfo(stepNo);
        ConfirmPrice confirmPrice = payInfo.getPayPrice().getConfirmPrice();
        //TODO 因为没有实际支付，这个地方会报空指针，先返回空，等有实际支付再发结算
        if (confirmPrice == null) {
            log.error("it will not get paySettleRpcReq for confirmPrice is null");
            return null;
        }
        PaySettleRpcReq req = new PaySettleRpcReq();
        req.setPrimaryOrderId(mainOrder.getPrimaryOrderId().toString());
        req.setCustId(mainOrder.getCustomer().getCustId().toString());
        req.setStepNo(mainOrder.orderAttr().getCurrentStepNo());

        req.setSettleAmt(confirmPrice.getConfirmRealAmt());
        req.setSplitPlatformAmt(NumUtils.getNullZero(confirmPrice.getSeparateRealAmt().get(SeparateRule.ROLE_PLATFORM)));
        req.setSplitSellerAmt(NumUtils.getNullZero(confirmPrice.getSeparateRealAmt().get(SeparateRule.ROLE_SELLER)));
        return req;
    }

    @Override
    public ToRefundData createRefund(MainReversal mainReversal, Integer stepNo) {
        OrderRefundRpcReq req = buildRefundRequest(mainReversal, stepNo);
        RpcResponse<OrderRefundRpcResp> resp;
        TcCancelRefundLogDO logDO = new TcCancelRefundLogDO();
        logDO.setPrimaryReversalId(mainReversal.getPrimaryReversalId());
        logDO.setRefundMsg(JsonUtils.toJSONString(req));
        logDO.setGmtCreate(new Date());
        try {
            resp = RpcUtils.invokeRpc(
                () -> orderPayWriteFacade.createRefund(req),
                "createRefund",
                I18NMessageUtils.getMessage("initiate.refund"),  //# "发起退款"
                mainReversal.getPrimaryReversalId() + "_" + stepNo
            );
            logDO.setReturnMsg(resp.toString());
            tcCancelRefundLogMapper.insert(logDO);
        } catch (Exception e) {
            logDO.setReturnMsg(e.getMessage());
            tcCancelRefundLogMapper.insert(logDO);
            return new ToRefundData();
        }
        return ToRefundData.builder().refundId(resp.getData().getRefundId()).build();
    }

    /**
     * 兼容预售分部退款
     * @param mainOrder
     * @return
     */
    @Override
    public ToRefundData createRefund(MainOrder mainOrder, Integer stepNo) {
        OrderRefundRpcReq req = buildRefundByOrder(mainOrder,stepNo);
        RpcResponse<OrderRefundRpcResp> resp;
        TcCancelRefundLogDO logDO = new TcCancelRefundLogDO();
        logDO.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        logDO.setRefundMsg(JsonUtils.toJSONString(req));
        logDO.setGmtCreate(new Date());
        try {
            resp = RpcUtils.invokeRpc(
                    () -> orderPayWriteFacade.createRefund(req),
                    "createRefund",
                    I18NMessageUtils.getMessage("initiate.refund"),  //# "发起退款"
                    mainOrder.getPrimaryOrderId()
            );
            logDO.setReturnMsg(resp.toString());
            tcCancelRefundLogMapper.insert(logDO);
        } catch (Exception e) {
            logDO.setReturnMsg(e.getMessage());
            tcCancelRefundLogMapper.insert(logDO);
            return new ToRefundData();
        }
        return ToRefundData.builder().refundId(resp.getData().getRefundId()).build();
    }

    /**
     * 构建退款对象
     * @param mainOrder
     * @return
     */
    protected OrderRefundRpcReq buildRefundByOrder(MainOrder mainOrder,Integer stepNo) {
        OrderPayInfo payInfo = mainOrder.getPayInfo(stepNo);
        RefundFee refundFee = new RefundFee();
        //计算运费
        calcFreightAmt(mainOrder, refundFee,stepNo);
        // 商品
        List<PayItemInfo> items = mainOrder.getSubOrders().stream().map(sub -> {
            PayItemInfo item = new PayItemInfo();
            item.setItemId(sub.getItemSku().getItemId().toString());
            item.setItemTitle(sub.getItemSku().getItemTitle());
            item.setItemPrice(sub.getItemSku().getItemPrice().getItemPrice());
            item.setQuantity(sub.getOrderQty());
            return item;
        }).collect(Collectors.toList());

        OrderRefundRpcReq req = new OrderRefundRpcReq();
        req.setCustId(mainOrder.getCustomer().getCustId().toString());
        req.setPrimaryOrderId(mainOrder.getPrimaryOrderId().toString());
        req.setStepNo(stepNo);
        req.setRefundItems(items);
        // 支付渠道
        String payChannel = payInfo.getPayChannel();
        if (StringUtils.isBlank(payChannel)) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG, "payChannel"+I18NMessageUtils.getMessage("is.empty"));  //# 为空"
        }
        List<RefundChannelInfo> channels = new ArrayList<>();
        if (!payChannel.equals(pointChannelCode)) {
            RefundChannelInfo channel = new RefundChannelInfo();
            channel.setPayChannel(payChannel);
            channel.setRefundAmt(refundFee.getCancelTotalAmt());
            channel.setRefundQuantity(BigDecimal.ZERO);
            channels.add(channel);
        }
        req.setRefundChannels(channels);
        return req;
    }

    /**
     *   //有关取消运费承担计算
     * @param mainOrder
     * @param refundFee
     */
    private void calcFreightAmt(MainOrder mainOrder, RefundFee refundFee,Integer stepNo) {
            //一个主单只能退一次运费 不能超额
            TcCancelReasonFeeDO cancelReasonFee = new TcCancelReasonFeeDO();
            cancelReasonFee.setCancelReasonCode(mainOrder.getOrderAttr().getReasonCode());
            if(!StringUtils.isEmpty(mainOrder.getOrderAttr().getReasonCode())){
                TcCancelReasonFeeDO reasonFeeDO = cancelReasonFeeRepository.queryTcCancelReasonFee(cancelReasonFee);
                if(reasonFeeDO != null){
                    if(TradeExtendKeyConstants.FREIGHT_FEE_BELONG_CUST == reasonFeeDO.getCustFee()
                    && NumUtils.getNullZero(mainOrder.getOrderPrice().getFreightAmt())>=0 ){
                        returnFee(mainOrder, refundFee, stepNo);
                    }else{//退运费
                        noReturnFee(mainOrder, refundFee, stepNo);
                    }
                }else{
                    noReturnFee(mainOrder, refundFee, stepNo);
                }
            }else{
                //取消原因为空 默认正常退不包含运费
                noReturnFee(mainOrder, refundFee, stepNo);
            }
    }

    private static void noReturnFee(MainOrder mainOrder, RefundFee refundFee, Integer stepNo) {
        //客户承担运费不退运费
        if (StepOrderUtils.isMultiStep(mainOrder)){
            for (StepOrder stepOrder : mainOrder.getStepOrders()) {
                if(stepNo.equals(stepOrder.getStepNo())) {
                    //尾款支付涉及退运费
                    if (StepOrderUtils.isPaid(stepOrder) && stepOrder.getStepNo().intValue() == 2) {
                        refundFee.setFreightRefunded(false);
                        refundFee.setCancelRealAmt(
                                NumUtils.getNullZero(stepOrder.getPrice().getRealAmt()) -
                                        NumUtils.getNullZero(mainOrder.getOrderPrice().getFreightAmt()));
                    }else if(StepOrderUtils.isPaid(stepOrder) && stepOrder.getStepNo().intValue() == 1){
                        //定金支付不涉及退运费
                        refundFee.setCancelRealAmt(stepOrder.getPrice().getRealAmt());
                        refundFee.setFreightRefunded(false);
                    }
                }
            }
        }else{
            refundFee.setCancelRealAmt(mainOrder.getOrderPrice().getOrderRealAmt());
            refundFee.setFreightRefunded(false);
        }
    }

    /**
     * 退运费金额计算
     * @param mainOrder
     * @param refundFee
     * @param stepNo
     */
    private static void returnFee(MainOrder mainOrder, RefundFee refundFee, Integer stepNo) {
        if (StepOrderUtils.isMultiStep(mainOrder)){
            for (StepOrder stepOrder : mainOrder.getStepOrders()) {
                if(stepNo.equals(stepOrder.getStepNo())) {
                    if (StepOrderUtils.isPaid(stepOrder) && stepOrder.getStepNo().intValue() == 2) {
                        refundFee.setFreightRefunded(true);
                        refundFee.setCancelRealAmt(
                                NumUtils.getNullZero(stepOrder.getPrice().getRealAmt()));
                    }else if(StepOrderUtils.isPaid(stepOrder) && stepOrder.getStepNo().intValue() == 1){
                        refundFee.setCancelRealAmt(stepOrder.getPrice().getRealAmt());
                        refundFee.setFreightRefunded(false);
                    }
                }
            }
        }else{
            refundFee.setCancelRealAmt(mainOrder.getOrderPrice().getOrderRealAmt());
            refundFee.setFreightAmt(mainOrder.getOrderPrice().getFreightAmt());
            refundFee.setFreightRefunded(true);
        }
    }

    protected OrderRefundRpcReq buildRefundRequest(MainReversal mainReversal, Integer stepNo) {
        MainOrder mainOrder = mainReversal.getMainOrder();
        OrderPayInfo payInfo = mainOrder.getPayInfo(stepNo);
        RefundFee refundFee;
        if (MapUtils.isNotEmpty(mainReversal.reversalFeatures().getStepRefundFee())) {
            refundFee = mainReversal.reversalFeatures().getStepRefundFee().get(stepNo);
        } else {
            refundFee = mainReversal.reversalFeatures();
        }
        // 商品
        List<PayItemInfo> items = mainReversal.getSubReversals().stream().map(sub -> {
            PayItemInfo item = new PayItemInfo();
            item.setItemId(sub.getSubOrder().getItemSku().getItemId().toString());
            item.setItemTitle(sub.getSubOrder().getItemSku().getItemTitle());
            item.setItemPrice(sub.getSubOrder().getItemSku().getItemPrice().getItemPrice());
            item.setQuantity(sub.getSubOrder().getOrderQty());
            return item;
        }).collect(Collectors.toList());
        OrderRefundRpcReq req = new OrderRefundRpcReq();
        req.setCustId(mainReversal.getCustId().toString());
        req.setPrimaryOrderId(mainOrder.getPrimaryOrderId().toString());
        req.setPrimaryReversalId(mainReversal.getPrimaryReversalId().toString());
        req.setStepNo(stepNo);
        req.setRefundItems(items);

        // 支付渠道
        String payChannel = payInfo.getPayChannel();
        if (StringUtils.isBlank(payChannel)) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG, "payChannel"+I18NMessageUtils.getMessage("is.empty"));  //# 为空"
        }
        List<RefundChannelInfo> channels = new ArrayList<>();
        if (NumUtils.getNullZero(refundFee.getCancelPointCount()) > 0) {
            RefundChannelInfo channel = new RefundChannelInfo();
            channel.setPayChannel(pointChannelCode);
            channel.setRefundAmt(0L);
            channel.setRefundQuantity(BigDecimal.valueOf(refundFee.getCancelPointCount()));
            channels.add(channel);
        }
        if (!payChannel.equals(pointChannelCode)) {
            RefundChannelInfo channel = new RefundChannelInfo();
            channel.setPayChannel(payChannel);
            channel.setRefundAmt(refundFee.getCancelRealAmt());
            channel.setRefundQuantity(BigDecimal.ZERO);
            channels.add(channel);
        }
        req.setRefundChannels(channels);

        // 退分账
        Map<String, Long> splitMap = refundFee.getCancelSeparateRealAmt();
        if (splitMap != null && !splitMap.isEmpty()) {
            RefundSplits splits = new RefundSplits();
            Long platformAmt = NumUtils.getNullZero(splitMap.get(SeparateRule.ROLE_PLATFORM));
            Long sellerAmt = NumUtils.getNullZero(splitMap.get(SeparateRule.ROLE_SELLER));
            splits.setSplitPlatformAmt(platformAmt);
            splits.setSplitSellerAmt(sellerAmt);
            req.setRefundSplits(splits);
        }

        return req;
    }

    @Override
    public OrderPay queryByUk(Long primaryOrderId, Long custId, Integer stepNo) {
        PayQueryRpcReq req = new PayQueryRpcReq();
        req.setPrimaryOrderId(primaryOrderId.toString());
        req.setCustId(custId.toString());
        req.setStepNo(stepNo);
        req.setIncludePayFlows(true);
        RpcResponse<OrderPayDTO> resp = RpcUtils.invokeRpc(
            () -> orderPayReadFacade.queryPayInfo(req),
            "queryByUk",
            I18NMessageUtils.getMessage("query.pay.order"),  //# "查询支付单"
            String.format("primaryOrderId:%s, custId:%s, stepNo:%s", primaryOrderId, custId, stepNo)
        );
        return convertOrderPay(resp.getData());
    }

    protected OrderPay convertOrderPay(OrderPayDTO data) {
        if (data == null) {
            return null;
        }

        OrderPay pay = new OrderPay();
        pay.setPayId(data.getPayId());
        pay.setPrimaryOrderId(data.getPrimaryOrderId());
        pay.setCustId(data.getCustId());
        pay.setStepNo(data.getStepNo());
        pay.setPayStatus(data.getPayStatus());
        pay.setPayTime(data.getPayTime());
        pay.setBizTags(data.getBizTags());
        pay.setBizFeature(data.getBizFeature());

        PayFlowDTO flow;
        if (data.getPayFlows().size() == 1) {
            flow = data.getPayFlows().get(0);
        } else {
            // 优先取支付成功的
            flow = data.getPayFlows().stream()
                    .filter(pf -> !PayTypeEnum.ASSERTS_PAY.getCode().equals(pf.getPayType()))
                    .filter(pf -> PayStatusEnum.PAID.getCode().equals(pf.getPayFlowStatus()))
                    .findFirst().orElse(null);
            // 积分支付成功的
            if (flow == null) {
                flow = data.getPayFlows().stream()
                        .filter(pf -> PayStatusEnum.PAID.getCode().equals(pf.getPayFlowStatus()))
                        .findFirst().orElse(null);
            }
            // 还没支付的
            if (flow == null) {
                flow = data.getPayFlows().stream()
                        .filter(pf -> !PayTypeEnum.ASSERTS_PAY.getCode().equals(pf.getPayType()))
                        .findFirst().orElse(null);
            }
        }
        if (flow == null) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG, I18NMessageUtils.getMessage("pay.channel.invalid"));  //# "支付渠道错误"
        }
        pay.setPayChannel(flow.getPayChannel());
        pay.setPayType(flow.getPayType());
        pay.setPayCartId(flow.getCartId());
        return pay;
    }

    @Override
    public List<PayChannel> queryAllPayChannel() {
        Callable<List<PayChannel>> c = () -> {
            RpcResponse<List<PayChannelDTO>> resp = orderPayReadFacade.queryAllPayChannel(EmptyRpcReq.get());
            if (!resp.isSuccess()) {
                throw new GmallException(ErrorUtils.mapCode(resp.getFail()));
            }
            return payChannelConverter.toPayChannels(resp.getData());
        };
        try {
            return payChannelCache.get(0L, c);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof GmallException) {
                throw (GmallException) e.getCause();
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updatePayStatus(MainOrder mainOrder, String code) {
        PayUpdateStatusRpcReq req = buildUpdateStatusRequest(mainOrder, code);
        RpcUtils.invokeRpc(() -> orderPayWriteFacade.updatePayStatus(req),
                "update pay status", "修改支付单状态",
                mainOrder.getPrimaryOrderId().toString());
    }

    protected PayUpdateStatusRpcReq buildUpdateStatusRequest(MainOrder mainOrder, String code) {
        PayUpdateStatusRpcReq req = new PayUpdateStatusRpcReq();
        req.setPrimaryOrderId(mainOrder.getPrimaryOrderId().toString());
        req.setCustId(mainOrder.getCustomer().getCustId().toString());
        req.setPayStatus(code);
        return req;
    }

    @Override
    public boolean confirmPayCartId(List<Map<String, String>> payFlowList, Long cartId) {
        ConfirmPayCartIdRpcReq req = new ConfirmPayCartIdRpcReq();
        req.setCartId(cartId);
        req.setPayFlowList(payFlowList);
        RpcResponse<ConfirmPayRpcResp> resp = RpcUtils.invokeRpc(
            () -> orderPayWriteFacade.confirmPayCartId(req),
            "confirmPay",
            I18NMessageUtils.getMessage("confirmPayCartId.pay"),  //# "确认支付"
            cartId
        );
        return resp.getData().isPaySuccess();
    }

    @Override
    public RpcResponse<Boolean> paymentV2(EPayTokenRpcReq ePayTokenRpcReq, com.aliyun.gts.gmall.platform.trade.api.dto.output.pay.OrderPayRpcResp tradeOrderPayRpcResp) {
        com.aliyun.gts.gmall.platform.pay.api.dto.output.OrderPayRpcResp newOrderPayRpcResp = new com.aliyun.gts.gmall.platform.pay.api.dto.output.OrderPayRpcResp();
        BeanUtils.copyProperties(tradeOrderPayRpcResp, newOrderPayRpcResp);
        return orderPayWriteFacade.doPay(ePayTokenRpcReq, newOrderPayRpcResp);
    }

}
