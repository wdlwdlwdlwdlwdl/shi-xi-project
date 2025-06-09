package com.aliyun.gts.gmall.platform.trade.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "trade.flow")
public class WorkflowProperties {

    // === 购物车 ===

    private String cartAdd = "flow/cart/cartAdd";
    private String cartCalcPrice = "flow/cart/cartCalcPrice";
    private String cartCheckAdd = "flow/cart/cartCheckAdd";
    private String cartClear = "flow/cart/cartClear";
    private String cartDelete = "flow/cart/cartDelete";
    private String cartModify = "flow/cart/cartModify";
    private String cartQuery = "flow/cart/cartQuery";
    private String cartQueryPayMode = "flow/cart/cartQueryPayMode";
    private String cartSingleQuery = "flow/cart/cartSingleQuery";
    private String cartQueryCount = "flow/cart/cartQueryCount";
    private String cartItemV2QueryCount = "flow/cart/cartItemV2QueryCount";


    // === 物流 ===

    private String logisticsQuery = "flow/logistics/logisticsQuery";


    // === 订单 ===
    private String orderConfirm = "flow/order/orderConfirm";
    private String orderConfirmReceive = "flow/order/orderConfirmReceive";
    private String orderCheckOutCreate = "flow/order/orderCheckOutCreate";
    private String orderCreate = "flow/order/orderCreate";
    private String orderCustomerCancel = "flow/order/orderCustomerCancel";
    private String orderCustomerDel = "flow/order/orderCustomerDel";
    private String orderCustomerQuery = "flow/order/orderCustomerQuery";
    private String orderCustomerSearch = "flow/order/orderCustomerSearch";
    private String orderDetail = "flow/order/orderDetail";
    private String orderDetailNew= "flow/order/orderDetailNew";
    private String orderSellerClose = "flow/order/orderSellerClose";
    private String deliveryUpdate = "flow/order/deliveryUpdate";
    private String orderSellerSearch = "flow/order/orderSellerSearch";
    private String orderAdminSearch = "flow/order/orderAdminSearch";
    private String orderCommonSearch = "flow/order/orderCommonSearch";
    private String orderSend = "flow/order/orderSend";
    private String orderSystemClose = "flow/order/orderSystemClose";
    private String orderSystemReceive = "flow/order/orderSystemReceive";
    private String orderSystemCancel= "flow/order/orderSystemCancel";

    private String  orderCommonEsSearch = "flow/order/orderCommonEsSearch";
    private String orderCustomerEsSearch = "flow/order/orderCustomerEsSearch";
    private String orderSellerEsSearch = "flow/order/orderSellerEsSearch";


    // === 订单 new ===
    private String orderConfirmNew = "flow/order/orderConfirm";
    private String orderCreateNew = "flow/order/orderCreate";

    // === 支付 ===

    private String payRender = "flow/pay/payRender";
    private String confirmPay = "flow/pay/confirmPay";
    private String confirmingPay = "flow/pay/confirmingPay";
    private String toPay = "flow/pay/toPay";
    private String toMergePay = "flow/pay/toMergePay";


    // === 售后 ===

    private String reversalCheck = "flow/reversal/reversalCheck";
    private String reversalCreate = "flow/reversal/reversalCreate";
}
