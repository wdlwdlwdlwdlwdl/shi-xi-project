package com.aliyun.gts.gmall.manager.front.trade.facade.impl;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.center.pay.api.enums.PayChannelEnum;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.exception.FrontManagerException;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.trade.adaptor.OrderAdapter;
import com.aliyun.gts.gmall.manager.front.trade.adaptor.OrderPayAdapter;
import com.aliyun.gts.gmall.manager.front.trade.convertor.TradeRequestConvertor;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.AddCardRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.OrderMergePayRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.OrderPayRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.command.PrimaryOrderRestCommand;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.EPayQueryReq;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.PayCheckRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.PrimaryOrderRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.PrimaryOrderVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.pay.OrderPayVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.pay.PayRenderVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.utils.TradeFrontResponseCode;
import com.aliyun.gts.gmall.manager.front.trade.facade.OrderPayFacade;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.platform.trade.api.constant.PayErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 支付操作
 *
 * @author tiansong
 */
@Service
@Slf4j
public class OrderPayFacadeImpl implements OrderPayFacade {
    @Autowired
    private OrderPayAdapter orderPayAdapter;
    @Autowired
    private TradeRequestConvertor tradeRequestConvertor;
    @Autowired
    private OrderAdapter orderAdapter;

    @Resource
    @Qualifier("cacheManager")
    private CacheManager cacheManager;

    @Override
    public PayRenderVO payRender(PrimaryOrderRestQuery primaryOrderRestQuery) {
        PayRenderVO payRenderVO = orderPayAdapter.payRender(primaryOrderRestQuery);
        payRenderVO.setPayChannelSelected(payRenderVO.getPayChannelList().get(0).getPayChannel());
        // 1. 补充支付渠道
        //payRenderVO.setPayChannelCode(primaryOrderRestQuery.getPayChannel() == null ?
        //        OnlinePayChannelEnum.ALIPAY.getCode() : primaryOrderRestQuery.getPayChannel());
        //payRenderVO.setPayChannelList(OnlinePayChannelEnum.getVOList());
        // 2. 补充去支付信息
        // this.fillPayInfo(primaryOrderRestQuery, payRenderVO);
        return payRenderVO;
    }

    private void fillPayInfo(PrimaryOrderRestQuery primaryOrderRestQuery, PayRenderVO payRenderVO) {
        OrderPayRestCommand orderPayRestCommand = new OrderPayRestCommand();
        orderPayRestCommand.setCustId(primaryOrderRestQuery.getCustId());
        orderPayRestCommand.setChannel(primaryOrderRestQuery.getOrderChannel());
        orderPayRestCommand.setPrimaryOrderId(primaryOrderRestQuery.getPrimaryOrderId());
        orderPayRestCommand.setPayChannel(primaryOrderRestQuery.getPayChannel());
        orderPayRestCommand.setRealPayFee(payRenderVO.getRealPayFee());
        orderPayRestCommand.setTotalOrderFee(payRenderVO.getTotalOrderFee());
        orderPayRestCommand.setPointAmount(payRenderVO.getPointAmount() == null ? 0L : payRenderVO.getPointAmount());
        orderPayRestCommand.checkInput();
        payRenderVO.setOrderPayVO(orderPayAdapter.toPay(orderPayRestCommand));
    }

    //支付成功后需要查询订单的信息拿到金额 加上维度（卖家id，客户id，赊销方式）+当期  然后插入收款认领表，这个需要考虑匹配策略吗？先不考虑查询策略
    @Override
    public Boolean payCheck(PayCheckRestQuery payCheckRestQuery) {
        Boolean aBoolean = orderPayAdapter.payCheck(payCheckRestQuery);
        return aBoolean;
    }


    @Override
    public OrderPayVO toPay(OrderPayRestCommand orderPayRestCommand) {

        if (CollectionUtils.isEmpty(orderPayRestCommand.getPrimaryOrderList()) ||
            orderPayRestCommand.getPrimaryOrderList().size() == 1) {
            String s = orderPayRestCommand.getPrimaryOrderList().get(0);
            PrimaryOrderVO order = PrimaryOrderVO.fromMergeParam(s);
            orderPayRestCommand.setPrimaryOrderId(order.getPrimaryOrderId());
            return orderPayAdapter.toPay(orderPayRestCommand);
        }

        // 平台版：合并支付
        List<Long> orders = new ArrayList<>();
        for (String s : orderPayRestCommand.getPrimaryOrderList()) {
            PrimaryOrderVO order = PrimaryOrderVO.fromMergeParam(s);
            orders.add(order.getPrimaryOrderId());
        }

        OrderMergePayRestCommand req = tradeRequestConvertor.convertMergePay(orderPayRestCommand);
        req.setPrimaryOrderIds(orders);
        req.setPointAmount(orderPayRestCommand.getPointAmount());
        req.setRealPayFee(orderPayRestCommand.getRealPayFee());
        req.setTotalOrderFee(orderPayRestCommand.getTotalOrderFee());
        req.checkInput();
        return orderPayAdapter.toMergePay(req);
    }

    /**
     * 添加卡的配置参数
     * @return
     */
    @Override
    public JSONObject addPay() {
        CustDTO user = UserHolder.getUser();
        String key = "cards:"+user.getAccountId();
        cacheManager.delete(key);
        return orderPayAdapter.addPay();
    }

    /**
     * 支付取消订单
     * @param primaryOrderRestCommand
     * @return
     */
    @Override
    public Boolean cancelOrder(PrimaryOrderRestCommand primaryOrderRestCommand) {
        orderAdapter.cancelOrder(primaryOrderRestCommand);
        return Boolean.TRUE;
    }

    @Override
    public String payment(OrderPayRestCommand orderPayRestCommand, OrderPayVO orderPayVO) {
        String result = orderPayAdapter.payment(orderPayRestCommand, orderPayVO);
        return result;
    }

    /**
     * 添加epay 卡
     * @param addCardRestCommand
     * @return
     */
    @Override
    public void addCard(AddCardRestCommand addCardRestCommand) {
        orderPayAdapter.addCard(addCardRestCommand);
    }

    /**
     * 获取所有 epay 的cardIds
     * @param ePayQueryReqd
     * @return
     */
    @Override
    public Long getBonuses(EPayQueryReq ePayQueryReqd) {
        return orderPayAdapter.getBonusesByUser();
    }

    /**
     * 删除卡
     * @param ePayQueryReqd
     * @return
     */
    @Override
    public Boolean removeCard(EPayQueryReq ePayQueryReqd) {
        return orderPayAdapter.removeCard(ePayQueryReqd);
    }

    @Override
    public List<JSONObject> getCardIds(EPayQueryReq ePayQueryReqd) {
        String key = String.format("cards:%s", ePayQueryReqd.getAccountId());
        List<JSONObject> list = cacheManager.get(key);
        if(CollectionUtils.isEmpty(list)){
            // 查一下卡
            list = orderPayAdapter.getCardIds(ePayQueryReqd);
            cacheManager.set(key, list, 300, TimeUnit.MINUTES);
        }
        return list;
    }

    /**
     * 获取用户所有的卡
     * @param  accountId
     * @return
     */
    @Override
    public List<JSONObject> getBankCards(String accountId) {
        List<JSONObject> list = orderPayAdapter.getUserCards(accountId);
        return list;
    }

    @Override
    public RestResponse<Boolean> payOrder(OrderPayRestCommand orderPayRestCommand) {
        // 创建支付数据
        OrderPayVO orderPayVO = toPay(orderPayRestCommand);
        if (null == orderPayVO && StringUtils.isEmpty(orderPayVO.getCartId())) {
            cancelOrders(orderPayRestCommand);
            throw new FrontManagerException(TradeFrontResponseCode.PAY_ADD_ERROR, orderPayVO);
        }
        if (PayChannelEnum.EPAY.getCode().equals(orderPayRestCommand.getPayChannel())) {
            ParamUtil.nonNull(orderPayRestCommand.getAccountId(), I18NMessageUtils.getMessage("accountId required"));
            // 开始支付
            RpcResponse<Boolean> rpcResponse = this.orderPayAdapter.paymentV2(orderPayRestCommand, orderPayVO);

            if (!rpcResponse.isSuccess()) {
                log.error("pay error rpcResponse={}", rpcResponse);
                if (Objects.nonNull(rpcResponse.getFail()) && StringUtils.isNotBlank(rpcResponse.getFail().getMessage())) {
                    cancelOrders(orderPayRestCommand);
                    return RestResponse.fail(rpcResponse.getFail().getCode(), rpcResponse.getFail().getMessage());
                }
                cancelOrders(orderPayRestCommand);
                return RestResponse.fail(PayErrorCode.PAY_FAILED.getCode(), I18NMessageUtils.getMessage("pay.failed"));
            }
            if (Objects.isNull(rpcResponse.getData()) || !rpcResponse.getData()) {
                log.error("pay error rpcResponse is false: {}", rpcResponse);
                cancelOrders(orderPayRestCommand);
                return RestResponse.fail(PayErrorCode.PAY_FAILED.getCode(), I18NMessageUtils.getMessage("pay.failed"));
            }
            return RestResponse.ok(true);
        }
        cancelOrders(orderPayRestCommand);
        return RestResponse.ok(false);
    }


    /**
     * 订单支付异常取消订单，只取消状态为待支付的订单
     * @param orderPayRestCommand
     */
    private void cancelOrders(OrderPayRestCommand orderPayRestCommand) {
        // 平台版：合并取消订单
        if (CollectionUtils.isEmpty(orderPayRestCommand.getPrimaryOrderList())) {
            log.warn("cancelOrder primaryOrderList is empty");
            return;
        }
        try {
            List<Long> primaryOrderIds = new ArrayList<>();
            for (String s : orderPayRestCommand.getPrimaryOrderList()) {
                PrimaryOrderVO order = PrimaryOrderVO.fromMergeParam(s);
                primaryOrderIds.add(order.getPrimaryOrderId());
            }
            RpcResponse<Boolean> result = orderPayAdapter.cancelOrders(primaryOrderIds);
            if (!result.isSuccess()) {
                log.error("cancelOrder error, result is {}", result);
            }
        } catch (Exception e) {
            log.error("cancelOrder error", e);
        }
    }
}
