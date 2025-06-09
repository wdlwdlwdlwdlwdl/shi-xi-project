package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.checkout;

import cn.hutool.core.collection.CollectionUtil;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateCheckOutOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateItemInfo;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderCreateService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CheckOutCreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.repository.CacheRepository;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCheckOutCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 *  生成结算单流程
 *     step 1 入参校验
 * @anthor shifeng
 * @version 1.0.1
 * 2025-1-6 14:42:57
 */
@Component
public class CreateCheckOutOrderNewBizCheckHandler extends TradeFlowHandler.AdapterHandler<TOrderCheckOutCreate> {

    @Autowired
    private CacheRepository cacheRepository;

    // 订单对象
    @Autowired
    private OrderCreateService orderCreateService;

    @Override
    public void handle(TOrderCheckOutCreate inbound) {
        // 入参
        CreateCheckOutOrderRpcReq createCheckOutOrderRpcReq = inbound.getReq();
        CheckOutCreatingOrder checkOutCreatingOrder = inbound.getDomain();
        // 入参校验
        if (StringUtils.isEmpty(createCheckOutOrderRpcReq.getConfirmOrderToken()) || CollectionUtils.isEmpty(createCheckOutOrderRpcReq.getOrderItems())) {
            checkOutCreatingOrder.setCheckSuccess(Boolean.FALSE);
            return;
        }
        // 读取缓存参数参数
        // 解析token
        CreatingOrder tokenOrder = orderCreateService.unpackTokenCache(createCheckOutOrderRpcReq.getConfirmOrderToken());
        if (tokenOrder == null ||CollectionUtils.isEmpty(tokenOrder.getMainOrders())) {
            checkOutCreatingOrder.setCheckSuccess(Boolean.FALSE);
            return;
        }
        // 未通过check
        if (Boolean.FALSE.equals(tokenOrder.getConfirmSuccess())) {
            checkOutCreatingOrder.setCheckSuccess(Boolean.FALSE);
            return;
        }
        // 不是同一个用户
        if (!tokenOrder.getCustId().equals(createCheckOutOrderRpcReq.getCustId())) {
            checkOutCreatingOrder.setCheckSuccess(Boolean.FALSE);
            return;
        }

        // 缓存参数check
        if (!Objects.equals(tokenOrder.getPayMode(), createCheckOutOrderRpcReq.getPayMode()) ||
            !Objects.equals(tokenOrder.getIsFromCart(),createCheckOutOrderRpcReq.getIsFromCart()) ||
            !Objects.equals(tokenOrder.getOrderPrice().getTotalAmt(), createCheckOutOrderRpcReq.getTotalOrderFee()) ||
            !Objects.equals(tokenOrder.getOrderPrice().getOrderRealAmt(), createCheckOutOrderRpcReq.getRealPayFee())) {
            checkOutCreatingOrder.setCheckSuccess(Boolean.FALSE);
            return;
        }
        // 数据完整性check
        List<CreateItemInfo> orderItems = createCheckOutOrderRpcReq.getOrderItems();
        for (MainOrder mainOrder : tokenOrder.getMainOrders()) {
            if (mainOrder == null || CollectionUtil.isEmpty(mainOrder.getSubOrders())) {
                checkOutCreatingOrder.setCheckSuccess(Boolean.FALSE);
                return;
            }
            for (SubOrder subOrder : mainOrder.getSubOrders()) {
                if (subOrder == null || subOrder.getReceiver() == null || subOrder.getItemSku() == null) {
                    checkOutCreatingOrder.setCheckSuccess(Boolean.FALSE);
                    return;
                }
               Boolean exists = orderItems.stream().anyMatch(orderItem -> orderItem != null &&
                    Objects.equals(orderItem.getItemId(), subOrder.getItemSku().getItemId()) &&
                    Objects.equals(orderItem.getSkuId(), subOrder.getItemSku().getSkuId()) &&
                    Objects.equals(orderItem.getSellerId(), subOrder.getItemSku().getSeller().getSellerId())
                );
                if (Boolean.FALSE.equals(exists)) {
                    checkOutCreatingOrder.setCheckSuccess(Boolean.FALSE);
                    return;
                }
            }
            mainOrder.setCustomer(tokenOrder.getCustomer());
        }
        // 幂等ID check
        String redisKey = String.format("CREATE_CHECK_ORDER_%s", createCheckOutOrderRpcReq.getConfirmOrderToken());
        String check = cacheRepository.get(redisKey);
        if (StringUtils.isNotEmpty(check)) {
            checkOutCreatingOrder.setCreatedSuccess(Boolean.TRUE);
            return;
        }
        checkOutCreatingOrder.setCreatingOrder(tokenOrder);
    }
}
