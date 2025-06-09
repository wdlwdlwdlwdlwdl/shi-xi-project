package com.aliyun.gts.gmall.platform.trade.server.service.mock;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.platform.trade.server.flow.context.base.AbstractContextEntity;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.add.*;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.calcprice.CalcFillupHandler;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.calcprice.CalcQueryHandler;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.calcprice.CalcResultHandler;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.clear.ClearCartHandler;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.count.CartQueryCountHandler;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.delete.DeleteCartHandler;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.modify.ModifyCartMergeHandler;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.modify.ModifyCartQtyHandler;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.modify.ModifyCartQueryHandler;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.modify.ModifyCartSaveHandler;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.cart.query.*;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.common.ArgumentCheckHandler;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.confirm.*;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.create.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 替换为 XML
 */
@Component
@Slf4j
public class MockFlowExecutor {

    @Autowired
    private ArgumentCheckHandler argumentCheckHandler;

    @Autowired
    private CartQueryHandler cartQueryHandler;
    @Autowired
    private CartGroupingHandler cartGroupingHandler;
    @Autowired
    private CartPagingHandler cartPagingHandler;
    @Autowired
    private CartResultHandler cartResultHandler;
    @Autowired
    private CartFillupHandler cartFillupHandler;

    @Autowired
    private CalcQueryHandler calcQueryHandler;
    @Autowired
    private CalcResultHandler calcResultHandler;
    @Autowired
    private CalcFillupHandler calcFillupHandler;

    @Autowired
    private ConfirmOrderItemHandler confirmOrderItemHandler;
    @Autowired
    private ConfirmOrderReceiverHandler confirmOrderReceiverHandler;
    @Autowired
    private ConfirmOrderResultHandler confirmOrderResultHandler;
    @Autowired
    private ConfirmOrderPriceHandler confirmOrderPriceHandler;
    @Autowired
    private ConfirmOrderTokenHandler confirmOrderTokenHandler;
    @Autowired
    private ConfirmOrderInventoryHandler confirmOrderInventoryHandler;
    @Autowired
    private ConfirmOrderCustomerHandler confirmOrderCustomerHandler;
    @Autowired
    private ConfirmOrderFillupHandler confirmOrderFillupHandler;

    @Autowired
    private CreateOrderTokenHandler createOrderTokenHandler;
    @Autowired
    private CreateOrderItemHandler createOrderItemHandler;
    @Autowired
    private CreateOrderInventoryHandler createOrderInventoryHandler;
    @Autowired
    private CreateOrderReceiverHandler createOrderReceiverHandler;
    @Autowired
    private CreateOrderPriceHandler createOrderPriceHandler;
    @Autowired
    private CreateOrderGenerateIdHandler createOrderGenerateIdHandler;
    @Autowired
    private CreateOrderSaveHandler createOrderSaveHandler;
    @Autowired
    private CreateOrderMessageHandler createOrderMessageHandler;
    @Autowired
    private CreateOrderResultHandler createOrderResultHandler;
    @Autowired
    private CreateOrderInventoryLockHandler createOrderInventoryLockHandler;
    @Autowired
    private CreateOrderInventoryPostHandler createOrderInventoryPostHandler;
    @Autowired
    private CreateOrderCustomerHandler createOrderCustomerHandler;
    @Autowired
    private CreateOrderFillupHandler createOrderFillupHandler;
    @Autowired
    private CreateOrderPaymentHandler createOrderPaymentHandler;
    @Autowired
    private CreateOrderErrorRollbackHandler createOrderPointUnlockHandler;
    @Autowired
    private CreateOrderPointLockHandler createOrderPointLockHandler;

    @Autowired
    private AddCartConvertHandler addCartConvertHandler;
    @Autowired
    private AddCartMergeHandler addCartMergeHandler;
    @Autowired
    private AddCartSaveHandler addCartSaveHandler;
    @Autowired
    private AddCartItemHandler addCartItemHandler;
    @Autowired
    private AddCartQtyLimitHandler addCartQtyLimitHandler;
    @Autowired
    private AddCartBizLimitHandler addCartBizLimitHandler;

    @Autowired
    private ModifyCartQueryHandler modifyCartQueryHandler;
    @Autowired
    private ModifyCartMergeHandler modifyCartMergeHandler;
    @Autowired
    private ModifyCartQtyHandler modifyCartQtyHandler;
    @Autowired
    private ModifyCartSaveHandler modifyCartSaveHandler;

    @Autowired
    private DeleteCartHandler deleteCartHandler;

    @Autowired
    private ClearCartHandler clearCartHandler;

    @Autowired
    private CartQueryCountHandler cartQueryCountHandler;


    public void execute(String flowName, AbstractContextEntity t) {
        if ("cart.query".equals(flowName)) {
            call(argumentCheckHandler, t);
            call(cartQueryHandler, t);
            call(cartGroupingHandler, t);
            call(cartPagingHandler, t);
            call(cartFillupHandler, t);
            call(cartResultHandler, t);
        }

        else if ("cart.query_count".equals(flowName)) {
            call(argumentCheckHandler, t);
            call(cartQueryCountHandler, t);
        }

        else if ("cart.check_add".equals(flowName)) {
            call(argumentCheckHandler, t);
            call(addCartConvertHandler, t);
            call(addCartItemHandler, t);
            call(addCartBizLimitHandler, t);
        }

        else if ("cart.calc_price".equals(flowName)) {
            call(argumentCheckHandler, t);
            call(calcQueryHandler, t);
            call(calcFillupHandler, t);
            call(calcResultHandler, t);
        }

        else if ("cart.add".equals(flowName)) {
            call(argumentCheckHandler, t);
            call(addCartConvertHandler, t);
            call(addCartMergeHandler, t);
            call(addCartQtyLimitHandler, t);
            call(addCartItemHandler, t);
            call(addCartBizLimitHandler, t);
            call(addCartSaveHandler, t);
        }

        else if ("cart.modify".equals(flowName)) {
            call(argumentCheckHandler, t);
            call(modifyCartQueryHandler, t);
            call(modifyCartMergeHandler, t);
            call(modifyCartQtyHandler, t);
            call(modifyCartSaveHandler, t);
        }

        else if ("cart.delete".equals(flowName)) {
            call(argumentCheckHandler, t);
            call(deleteCartHandler, t);
        }

        else if ("cart.clear".equals(flowName)) {
            call(argumentCheckHandler, t);
            call(clearCartHandler, t);
        }

        else if ("order.confirm".equals(flowName)) {
            call(argumentCheckHandler, t);
            call(confirmOrderItemHandler, t);
            call(confirmOrderCustomerHandler, t);
            call(confirmOrderInventoryHandler, t);
            call(confirmOrderReceiverHandler, t);
            call(confirmOrderPriceHandler, t);
            call(confirmOrderFillupHandler, t);
            call(confirmOrderTokenHandler, t);
            call(confirmOrderResultHandler, t);
        }

        else if ("order.create".equals(flowName)) {
            call(argumentCheckHandler, t);         // 入参检查
            call(createOrderTokenHandler, t);      // Token拆包
            call(createOrderCustomerHandler, t);   // 用户检查
            call(createOrderItemHandler, t);       // I18NMessageUtils.getMessage("product.query")、检查  //# 商品查询
            call(createOrderInventoryHandler, t);      // 库存检查
            call(createOrderReceiverHandler, t);       // 收件人查询、检查
            call(createOrderPriceHandler, t);          // 价格计算、检查
            call(createOrderFillupHandler, t);         // 下单常量填充
            call(createOrderGenerateIdHandler, t);      // 订单ID预生成
            call(createOrderPointLockHandler, t);       // I18NMessageUtils.getMessage("points.freeze")  //# 积分冻结
            call(createOrderPaymentHandler, t);         // 支付单生成
            call(createOrderInventoryLockHandler, t);   // I18NMessageUtils.getMessage("reserved.stock")、创建超时释放库存任务  //# 预占库存
            call(createOrderSaveHandler, t);               // 保存订单与支付单、创建超时关单任务
            call(createOrderInventoryPostHandler, t);      // 库存处理：下单减、异常释放预占等
            call(createOrderPointUnlockHandler, t);        // 异常情况释放积分锁定
            call(createOrderMessageHandler, t);            // 订单消息
            call(createOrderResultHandler, t);             // 结果转换
        }
    }

    private void call(TradeFlowHandler h, AbstractContextEntity t) {
        if (t.isError()) {
            h.handleError(t);
            return;
        }

        try {
            h.handle(t);
        } catch (Exception e) {
            log.error("", e);
            String msg = e.getMessage();
            if (StringUtils.isBlank(msg)) {
                msg = e.toString();
            }
            t.setError(h.getExceptionErrCode(), msg);
            h.handleError(t);
        }
    }
}
