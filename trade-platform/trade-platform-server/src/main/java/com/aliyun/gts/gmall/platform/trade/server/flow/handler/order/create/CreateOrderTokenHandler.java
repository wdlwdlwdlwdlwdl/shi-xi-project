package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.create;

import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.PayMode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateItemInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderCreateEdgeAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderCreateService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.server.converter.TcOrderConverter;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreateOrderTokenHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private OrderCreateService orderCreateService;
    @Autowired
    private OrderCreateEdgeAbility orderCreateEdgeAbility;

    @Autowired
    private TcOrderConverter tcOrderConverter;

    @Override
    public void handle(TOrderCreate inbound) {
        CreateOrderRpcReq req = inbound.getReq();
        String token = req.getConfirmOrderToken();
        CreatingOrder tokenOrder = orderCreateService.unpackToken(token);
        tokenOrder.putAllExtra(req.getExtra());


        //补充一些页面选择的最新信息
        CreatingOrder domain = inbound.getDomain();
        //最新的支付方式，确保payMode得是可选的几种方式中一个
        domain.setPayMode(req.getPayMode());
        /*if (req.getPayMode().startsWith(PayMode.LOAN)) {
            if (CollectionUtils.isEmpty(tokenOrder.getLoan()) || !tokenOrder.getLoan().contains(Integer.valueOf(req.getPayMode().substring(PayMode.LOAN.length()+1)))) {
                 //不包含，报错
                inbound.setError(OrderErrorCode.PAY_MODEL_ILLEGAL);
                return;
            }
        }
        else */if (req.getPayMode().startsWith(PayMode.INSTALLMENT)) {
            if (CollectionUtils.isEmpty(tokenOrder.getInstallment()) || !tokenOrder.getInstallment().contains(Integer.valueOf(req.getPayMode().substring(PayMode.INSTALLMENT.length()+1)))) {
                //不包含，报错
                inbound.setError(OrderErrorCode.PAY_MODEL_ILLEGAL);
                return;
            }
        }
        //判断新选的orderItems 小于等于 token存储的，因为只允许修改confirm过后的
        List<CreateItemInfo> orderItems = req.getOrderItems();
        if (CollectionUtils.isEmpty(orderItems)) {
            //报错
            inbound.setError(OrderErrorCode.ORDER_ITEM_NULL);
            return;
        }
        for (CreateItemInfo item : orderItems) {
            //tokenOrder中的mainOrder列表中的subsOrder列表必须包含item中的itemId和skuId，并且数目要一致
            Boolean exist = false;
            for (MainOrder mainOrder : tokenOrder.getMainOrders()) {
                for (SubOrder subOrder : mainOrder.getSubOrders()) {
                    if (item.getItemId().equals(subOrder.getItemSku().getItemId()) && item.getSkuId().equals(subOrder.getItemSku().getSkuId())
                            && item.getItemQty() <= subOrder.getOrderQty()) {
                        exist = true;
                        break;
                    }
                }
            }
            if (!exist) {
                //如果不存在，则有问题
                inbound.setError(OrderErrorCode.ORDER_ITEM_ILLEGAL);
                return;
            }
        }
        //token中的订单可能被删除或者修改地址等，要剔除删除的，并且按照地址物流等再次拆单
        for (MainOrder mainOrder : tokenOrder.getMainOrders()) {
            SubOrder subOrder = null;
            for (int i = mainOrder.getSubOrders().size(); i > 0; i--) {
                subOrder = mainOrder.getSubOrders().get(i - 1);
                boolean exist = false;
                for (CreateItemInfo item : orderItems) {
                    if (item.getItemId().equals(subOrder.getItemSku().getItemId()) && item.getSkuId().equals(subOrder.getItemSku().getSkuId())) {
                        //这个地方需要补充最新的地址和快递方式
                        subOrder.setReceiver(tcOrderConverter.toReceiveAddr(item.getReceiver()));
                        subOrder.setLogisticsType(item.getLogisticsType());
                        subOrder.setSkuQuoteId(item.getSkuQuoteId());
                        subOrder.setCityCode(item.getCityCode());
                        exist = true;
                        break;
                    }
                }
                if (!exist) {
                    //不存在，则需要删除
                    mainOrder.getSubOrders().remove(i - 1);
                }
            }
            //更新最新的支付方式
            tokenOrder.setPayMode(req.getPayMode());
        }
        //重新拆单
        List<MainOrder> mainOrderList = orderCreateService.splitOrder(tokenOrder.getMainOrders());
        //拆单结束后，把子订单信息补充到mainOrder中
        for (MainOrder mainOrder : mainOrderList) {
            for (SubOrder subOrder : mainOrder.getSubOrders()) {
                mainOrder.setReceiver(subOrder.getReceiver());
                mainOrder.setLogisticsType(subOrder.getLogisticsType());
            }
        }

        tokenOrder.setMainOrders(mainOrderList);
        BeanUtils.copyProperties(tokenOrder, inbound.getDomain());
        //再次根据商户，
        orderCreateEdgeAbility.beginCreate(inbound.getDomain());
    }
}
