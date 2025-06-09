package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.createv2;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateItemInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.create.CreateOrderRpcReq;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderTypeEnum;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderBizCodeAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderCreateEdgeAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ItemService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderCreateService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SplitOrderItemInfo;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.server.converter.TcOrderConverter;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户下单 step2
 *    确认订单时产生的token 根据token获取缓存的数据
 *    通过token做参数check  防止参数变更异常
 *    通过入参重新计算下单信息，不再使用缓存的信息
 *    根据商品信息 拆单 ，拆单逻辑详见注释
 *    所有参数重新计算
 * @anthor shifeng
 * 2024-12-13 11:43:55
 */
@Slf4j
@Component
public class CreateOrderNewTokenHandler extends AdapterHandler<TOrderCreate> {

    // 商品
    @Autowired
    private ItemService itemService;

    // 订单对象
    @Autowired
    private OrderCreateService orderCreateService;

    // 转换对象
    @Autowired
    private TcOrderConverter tcOrderConverter;

    // FJ4J 扩展点
    @Autowired
    private OrderBizCodeAbility orderBizCodeAbility;

    // 订单扩展
    @Autowired
    private OrderCreateEdgeAbility orderCreateEdgeAbility;

    @Override
    public void handle(TOrderCreate inbound) {
        CreateOrderRpcReq createOrderRpcReq = inbound.getReq();
        //入参转换
        //补充一些页面选择的最新信息
        CreatingOrder creatingOrder = inbound.getDomain();
        creatingOrder.setPayMode(createOrderRpcReq.getPayMode());
        creatingOrder.setIsFromCart(createOrderRpcReq.getIsFromCart());
        // 订单确认产生的token
        String token = createOrderRpcReq.getConfirmOrderToken();
        // 解析token
        //这边经过一系列处理将token解析为订单对象
        CreatingOrder tokenOrder = orderCreateService.unpackToken(token);
        // token 参数验证
        if (Objects.isNull(tokenOrder) || CollectionUtils.isEmpty(tokenOrder.getMainOrders())) {
            throw new GmallException(OrderErrorCode.ORDER_TOKEN_EXPIRED);
        }
        // 所有的子单
        List<SubOrder> subOrderList = new ArrayList<>();
        tokenOrder.getMainOrders().stream().filter(Objects::nonNull).forEach(mainOrder ->
            subOrderList.addAll(CollectionUtils.isNotEmpty(mainOrder.getSubOrders()) ? mainOrder.getSubOrders() : new ArrayList<>())
        );
        if (CollectionUtils.isEmpty(subOrderList)) {
            throw new GmallException(OrderErrorCode.ORDER_TOKEN_EXPIRED);
        }
        /**
         * 是否来着购物车相同 支付方式 总金额 实付金额
         * 必须相同 否则报错
         */
        if (!Objects.equals(tokenOrder.getPayMode(), createOrderRpcReq.getPayMode()) ||
            !Objects.equals(tokenOrder.getIsFromCart(),createOrderRpcReq.getIsFromCart())) {
            throw new GmallException(OrderErrorCode.ORDER_TOKEN_EXPIRED);
        }
        // 价格比较 原价 实付价
        if(OrderTypeEnum.PHYSICAL_GOODS.getCode().equals(tokenOrder.getMainOrders().get(0).getOrderType())) {
            if (!Objects.equals(tokenOrder.getOrderPrice().getTotalAmt(), createOrderRpcReq.getTotalOrderFee()) ||
                !Objects.equals(tokenOrder.getOrderPrice().getOrderRealAmt(), createOrderRpcReq.getRealPayFee())) {
                throw new GmallException(OrderErrorCode.ORDER_TOKEN_EXPIRED);
            }
            // 优惠价格和运费
            if (Objects.nonNull(createOrderRpcReq.getPromotionFee()) &&
                !Objects.equals(createOrderRpcReq.getPromotionFee(), tokenOrder.getOrderPrice().getOrderPromotionAmt())) {
                throw new GmallException(OrderErrorCode.ORDER_TOKEN_EXPIRED);
            }
            if (Objects.nonNull(createOrderRpcReq.getFreightFee()) &&
                !Objects.equals(createOrderRpcReq.getFreightFee(), tokenOrder.getOrderPrice().getFreightAmt())) {
                throw new GmallException(OrderErrorCode.ORDER_TOKEN_EXPIRED);
            }
        }
        tokenOrder.putAllExtra(createOrderRpcReq.getExtra());
        // 入参的商品信息
        List<CreateItemInfo> orderItems = createOrderRpcReq.getOrderItems();
        if (CollectionUtils.isEmpty(orderItems)) {
            throw new GmallException(OrderErrorCode.ITEM_NOT_ENABLE);
        }
        // 商品数量必须等于子单数量
        if (!Objects.equals(subOrderList.size(), orderItems.size())) {
            throw new GmallException(OrderErrorCode.ORDER_TOKEN_EXPIRED);
        }
        /**重新查询商品数据 重新计算 */
        Map<ItemSkuId, ItemSku> itemMap = queryItems(createOrderRpcReq);
        if (MapUtils.isEmpty(itemMap)){
            log.error("商品信息为空");
            throw new GmallException(OrderErrorCode.ITEM_NOT_ENABLE);
        }

        // 订单确认拆单
        List<SplitOrderItemInfo> splitOrderItemInfos = new ArrayList<>();
        /**
         * TODO：下面这行注释很重要
         * tokenOrder中的mainOrder列表中的subsOrder列表必须包含item中的itemId和skuId，并且数目要一致
         * 商品状态 原价 城市
         * 收货信息
         * 物流方式 不二次拆单了 就按照confirm的拆单数据
         */
        //TODO:下边代码中的 orderItems-商品信息，是通过请求获取的订单数据
        for (CreateItemInfo createItemInfo : orderItems) {
            // 获取每个商品的信息 保障基础正确
            ItemSku itemSku = itemMap.get(
                new ItemSkuId(
                    createItemInfo.getItemId(),
                    createItemInfo.getSkuId(),
                    createItemInfo.getSellerId()
                )
            );
            // 商品不可以为空 不可以不能买
            if (Objects.isNull(itemSku) ||
                Boolean.FALSE.equals(itemSku.isEnabled()) ||
                CollectionUtils.isEmpty(itemSku.getPriceList())) {
                throw new GmallException(OrderErrorCode.ITEM_NOT_ENABLE);
            }
            // 商品对应的子单 - TODO：itemSubOrder是token解析出来的
            SubOrder itemSubOrder = subOrderList.stream()
                .filter(subOrder ->
                    createItemInfo.getItemId().equals(subOrder.getItemSku().getItemId()) &&
                    createItemInfo.getSkuId().equals(subOrder.getItemSku().getSkuId()) &&
                    (
                        Objects.nonNull(subOrder.getItemSku().getSeller()) &&
                        createItemInfo.getSellerId().equals(subOrder.getItemSku().getSeller().getSellerId())
                    )
                )
                .findFirst()
                .orElse(null);
            //如果不存在，则有问题
            if (Objects.isNull(itemSubOrder)) {
                inbound.setError(OrderErrorCode.ORDER_ITEM_ILLEGAL);
            }
            // 商品check 比较数量和价格 城市
            if (!Objects.equals(createItemInfo.getItemQty(), itemSubOrder.getOrderQty())) {
                inbound.setError(OrderErrorCode.ORDER_ITEM_ILLEGAL);
            }
            if (!Objects.equals(itemSku.getItemPrice().getOriginPrice() * createItemInfo.getItemQty(), itemSubOrder.getOrderPrice().getItemOriginAmt())) {
                inbound.setError(OrderErrorCode.ORDER_ITEM_ILLEGAL);
            }
            if (!Objects.equals(createItemInfo.getCityCode(), itemSubOrder.getCityCode())) {
                inbound.setError(OrderErrorCode.ORDER_ITEM_ILLEGAL);
            }
            // 收货地址check 比较入参和缓存的收货地址ID
            if (Objects.isNull(itemSubOrder.getReceiver()) ||
                Objects.isNull(itemSubOrder.getReceiver().getReceiverId())) {
                throw new GmallException(OrderErrorCode.ORDER_TOKEN_EXPIRED);
            }
            if (Objects.isNull(createItemInfo.getReceiver()) ||
                Objects.isNull(createItemInfo.getReceiver().getReceiverId())) {
                throw new GmallException(OrderErrorCode.RECEIVER_NOT_EXISTS);
            }
            // 收货地址ID必须相同
            if (!Objects.equals(createItemInfo.getReceiver().getReceiverId(), itemSubOrder.getReceiver().getReceiverId())) {
                throw new GmallException(OrderErrorCode.ORDER_TOKEN_EXPIRED);
            }
            // 缓存参数替换 用最新的
            itemSubOrder.setGmtCreate(new Date());
            itemSubOrder.setGmtModified(new Date());
            itemSubOrder.setCategoryCommissionRate(itemSku.getCategoryCommissionRate());
            // 商品数据塞到订单分组里面去
            SplitOrderItemInfo splitOrderItemInfo = new SplitOrderItemInfo();
            BeanUtils.copyProperties(createItemInfo, splitOrderItemInfo);
            // 商品信息
            splitOrderItemInfo.setItemSku(itemSku);
            splitOrderItemInfo.setLoanCycle(createOrderRpcReq.getLoanCycle());
            // 收货地址 可以为空
            splitOrderItemInfo.setReceiver(createItemInfo.getReceiver());
            splitOrderItemInfos.add(splitOrderItemInfo);
        }
        // 赋值
        BeanUtils.copyProperties(tokenOrder, creatingOrder);

        // 二次拆单
        //List<MainOrder> spliOrder = orderCreateService.splitOrderNew(splitOrderItemInfos);
        // 新旧拆单比较
        /**
         * 订单创建扩展点
         * 基础实现 DefaultOrderCreateEdgeExt
         * 扩展实现 MatchAllOrderCreateEdgeExt
         */
        orderCreateEdgeAbility.beginCreate(creatingOrder);
    }


    /**
     * 商品信息查询
     * @param createOrderRpcReq
     * @return Map<ItemSkuId, ItemSku>
     * 2024-12-11 17:02:33
     */
    protected Map<ItemSkuId, ItemSku> queryItems(CreateOrderRpcReq createOrderRpcReq) {
        List<ItemSkuId> itemSkuIds = createOrderRpcReq
            .getOrderItems()
            .stream()
            .map(item -> new ItemSkuId(
                item.getItemId(),
                item.getSkuId(),
                item.getSellerId(),
                item.getSkuQuoteId(),
                item.getCityCode(),
                item.getDeliveryType(),
                createOrderRpcReq.getPayMode()
            ))
            .collect(Collectors.toList());
        // 商品信息查询
        Map<ItemSkuId, ItemSku> itemMap = itemService.queryItemsRequired(itemSkuIds);
        for (ItemSku item : itemMap.values()) {
            if (Boolean.FALSE.equals(item.isEnabled())) {
                log.error("item is not enabled, itemSkuId:{}", item.getItemId());
                throw new GmallException(OrderErrorCode.ITEM_NOT_ENABLE);
            }
            if (Boolean.FALSE.equals(item.getCityPriceStatus())) {
                log.error("city price status is false, itemSkuId:{}", item.getItemId());
                throw new GmallException(OrderErrorCode.ITEM_NOT_ENABLE);
            }
        }
        return itemMap;
    }
}
