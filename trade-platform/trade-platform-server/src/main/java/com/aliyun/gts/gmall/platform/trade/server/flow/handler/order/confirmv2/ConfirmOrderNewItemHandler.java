package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.confirmv2;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmItemInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmOrderInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderBizCodeAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ItemService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderCreateService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SplitOrderItemInfo;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import com.aliyun.gts.gmall.platform.trade.server.utils.BizCodeHandlerUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单确认 step 1
 *    商品查询 确认订单的商品信息查询 查询商品的基本信息和配置信息
 *    通过入参的商品信息查询商品SKU信息，获取商品价格，支持的物流方式，
 *    根据城市和支付方式计算结算价价钱
 *    取所有商品价格取的交集算出来支相同的贷款信息， 计算贷款支付方式
 *    根据商品分组生成订单数据  一个商品一个对象  不做拆单 拆单在下单里面拆单
 * @anthor shifeng
 * @version 1.0.1
 * 2024-12-11 16:54:10
 */
@Slf4j
@Component
public class ConfirmOrderNewItemHandler extends AdapterHandler<TOrderConfirm> {

    // 商品
    @Autowired
    private ItemService itemService;

    // 订单
    @Autowired
    private OrderCreateService orderCreateService;

    // FJ4J 扩展点
    @Autowired
    private OrderBizCodeAbility orderBizCodeAbility;

    /**
     * 商品信息处理
     * @param inbound
     */
    @Override
    public void handle(TOrderConfirm inbound) {
        // step1 参数转换
        ConfirmOrderInfoRpcReq confirmOrderInfoRpcReq = inbound.getReq();
        // 商品不能为空
        if (CollectionUtils.isEmpty(confirmOrderInfoRpcReq.getOrderItems())) {
            throw new GmallException(OrderErrorCode.ITEM_NOT_ENABLE);
        }
        // 获取支付方式 等信息
        CreatingOrder creatingOrder = inbound.getDomain();
        creatingOrder.setCartId(confirmOrderInfoRpcReq.getCartId());
        creatingOrder.setPayMode(confirmOrderInfoRpcReq.getPayMode());
        creatingOrder.setPayChannel(confirmOrderInfoRpcReq.getPayChannel());
        creatingOrder.setIsFromCart(confirmOrderInfoRpcReq.getIsFromCart());
        //透传前端扩展参数
        if(Objects.nonNull(confirmOrderInfoRpcReq.getParams())) {
            creatingOrder.addParams(confirmOrderInfoRpcReq.getParams());
        }

//        LambdaQueryWrapper<TcOrderDO> wrapper= Wrappers.lambdaQuery();
//        wrapper.eq(TcOrderDO::getItemId,itemId);
//        tcOrderPayMapper.selectOne(wrapper);


        // step2 查询商品信息  商品的  获取支持的物流信息
        Map<ItemSkuId, ItemSku> itemMap = queryItems(confirmOrderInfoRpcReq);
        if (MapUtils.isEmpty(itemMap)){
            throw new GmallException(OrderErrorCode.ITEM_NOT_ENABLE);
        }
        //筛选没有城市价格的
        List<ItemSkuId> noCityPriceItems = new ArrayList<>();
        // 使用 Iterator 遍历并删除元素
        Iterator<Map.Entry<ItemSkuId, ItemSku>> iterator = itemMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<ItemSkuId, ItemSku> entry = iterator.next();
            // 删除无效元素
            if (Boolean.FALSE.equals(entry.getValue().getCityPriceStatus())) {
                noCityPriceItems.add(entry.getKey());
                log.warn("item has no city price ItemSkuId: {}", entry.getKey());
//                iterator.remove();
                creatingOrder.setConfirmSuccess(Boolean.FALSE);
                continue;
            }
            // 删除无效元素
            if (CollectionUtils.isEmpty(entry.getValue().getSupportDeliveryList())) {
                noCityPriceItems.add(entry.getKey());
                log.warn("item has no city price ItemSkuId: {}", entry.getKey());
//                iterator.remove();
                creatingOrder.setConfirmSuccess(Boolean.FALSE);
            }
        }
        creatingOrder.setNoCityPriceSkuIds(noCityPriceItems);
        if (MapUtils.isEmpty(itemMap)){
            log.error("itemMap is empty");
            throw new GmallException(OrderErrorCode.DELIVERY_NOT_ENABLE);
        }
        // 获取所有商品 可以分期支付的期数集合
        List<Integer> installmentList = itemMap.values()
            .stream()
            .map(ItemSku::getInstallment)
            .filter(Objects::nonNull)
            .filter(set -> !set.isEmpty()) // 过滤掉空集合
            .reduce((a, b) -> {
                //用 retainAll 来保留两个集合的交集，并将结果传递给下一个迭代。
                List<Integer> aTemp = new ArrayList<>(a);
                List<Integer> bTemp = new ArrayList<>(b);
                aTemp.retainAll(bTemp);
                return aTemp;
            })
            .orElse(new ArrayList<>());
        // 根据ItemSku中的installment和loan分期取交集，只允许都有的分期才行
        creatingOrder.setInstallment(installmentList);

        // step 3 拆单计算
        creatingOrder.setMainOrders(splitOrder(confirmOrderInfoRpcReq, itemMap));
        
        /**
         * 商品业务身份 --- 商品业务扩展点
         * 通过PF4J扩展能力扩展定开
         * 基类   DefaultOrderBizCodeExt
         * 扩展类 MatchAllOrderBizCodeExt 实现
         */
        BizCodeHandlerUtils.fillBizCode(inbound, orderBizCodeAbility::getBizCodesFromItem);
    }

    /**
     * 商品信息查询
     * @param confirmOrderInfoRpcReq
     * @return Map<ItemSkuId, ItemSku>
     * 2024-12-11 17:02:33
     */
    protected Map<ItemSkuId, ItemSku> queryItems(ConfirmOrderInfoRpcReq confirmOrderInfoRpcReq) {
        List<ItemSkuId> itemSkuIds = confirmOrderInfoRpcReq
            .getOrderItems()
            .stream()
            .map(item -> new ItemSkuId(
                item.getItemId(),
                item.getSkuId(),
                item.getSellerId(),
                item.getSkuQuoteId(),
                item.getCityCode(),
                item.getDeliveryType(),
                confirmOrderInfoRpcReq.getPayMode()
            ))
            .collect(Collectors.toList());
        // 商品信息查询
        Map<ItemSkuId, ItemSku> itemMap = itemService.queryItemsRequiredNew(itemSkuIds);
        for (ItemSku item : itemMap.values()) {
            if (Boolean.FALSE.equals(item.isEnabled())) {
                throw new GmallException(OrderErrorCode.ITEM_NOT_ENABLE);
            }
        }
        return itemMap;
    }

    /**
     * 拆单计算
     * @param confirmOrderInfoRpcReq
     * @param itemMap
     * 2025-2-19 11:45:32
     */
    private List<MainOrder> splitOrder(ConfirmOrderInfoRpcReq confirmOrderInfoRpcReq, Map<ItemSkuId, ItemSku> itemMap) {
        // step3 应该按照商品为维度生成数据 计算拆单
        // 订单确认拆单
        List<SplitOrderItemInfo> splitOrderItemInfos = new ArrayList<>();
        for (ConfirmItemInfo confirmItemInfo : confirmOrderInfoRpcReq.getOrderItems()) {
            //遍历每一个商品
            // 根据卖家+sku+item+卖家 分组商品
            ItemSku itemSku = itemMap.get(
                new ItemSkuId(
                    confirmItemInfo.getItemId(),
                    confirmItemInfo.getSkuId(),
                    confirmItemInfo.getSellerId()
                )
            );
            if (Objects.isNull(itemSku)) {
                throw new GmallException(OrderErrorCode.ITEM_NOT_ENABLE);
            }
            // 商品数据塞到订单分组里面去
            SplitOrderItemInfo splitOrderItemInfo = new SplitOrderItemInfo();
            BeanUtils.copyProperties(confirmItemInfo, splitOrderItemInfo);
            // 商品信息
            splitOrderItemInfo.setItemSku(itemSku);
            // 收货地址 可以为空
            splitOrderItemInfo.setReceiver(confirmItemInfo.getReceiver());
            // 分期数
            splitOrderItemInfo.setLoanCycle(confirmOrderInfoRpcReq.getLoanCycle());
            splitOrderItemInfos.add(splitOrderItemInfo);
        }
        /**
         * token中的订单可能被删除或者修改地址等，要剔除删除的，并且按照地址物流等再次拆单
         * 重新拆单 计算 确认订单的数据无效
         * 1、卖家 --- 不同卖家 不可以合单 先按照卖家拆分
         * 2、商品类别 --- 商品类别有参数 是否允许合单 允许合单则合并成一单 多个商品 不允许 则单独一个商品一个订单
         * 3、仓库  --- 在完成上面两个拆合单后，剩余的单个商品 商品发货地址 如果存在交集 ，则可以合单 使用贪心算法，最大交集算法处理
         */
        return orderCreateService.splitOrderNew(splitOrderItemInfos);
    }
}