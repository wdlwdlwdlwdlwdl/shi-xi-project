package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.aliyun.gts.gmall.framework.api.consts.ChannelEnum;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.promotion.api.dto.input.OrderCreateReq;
import com.aliyun.gts.gmall.platform.promotion.api.dto.input.OrderCreateReq.Cluster;
import com.aliyun.gts.gmall.platform.promotion.api.dto.input.PromotionQueryReq;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.ItemPriceDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.display.PromotionSummation;
import com.aliyun.gts.gmall.platform.promotion.api.facade.cust.PromotionAssetsWriteFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.cust.PromotionReadFacade;
import com.aliyun.gts.gmall.platform.promotion.common.model.PromDivideDTO;
import com.aliyun.gts.gmall.platform.promotion.common.model.TargetCust;
import com.aliyun.gts.gmall.platform.promotion.common.model.TargetItem;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemDivideDetail;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.ItemPromotionQuery;
import com.aliyun.gts.gmall.platform.trade.domain.entity.promotion.OrderPromotion;
import com.aliyun.gts.gmall.platform.trade.domain.repository.PromotionRepository;
import com.aliyun.gts.gmall.platform.trade.persistence.rpc.converter.PromotionRpcConverter;
import com.aliyun.gts.gmall.platform.trade.persistence.rpc.util.RpcUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import static com.aliyun.gts.gmall.platform.promotion.common.type.AssetsType.NONE;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Slf4j
@Component
@Configuration
@ConditionalOnProperty(prefix = "trade", name = "promotionRepository", havingValue = "default", matchIfMissing = true)
public class PromotionRepositoryImpl implements PromotionRepository {
    private static final boolean checkChannel = false;

    @Autowired
    private PromotionReadFacade promotionReadFacade;
    @Autowired
    private PromotionRpcConverter promotionRpcConverter;

    @Autowired
    private PromotionAssetsWriteFacade assetsWriteFacade;

    private static final String IS_NEW_USER = "isNewUser";

    private static final String SPECIFIC_GROUP_ID = "specificGroupId";

    @Override
    public List<ItemPromotion> queryItemPromotion(ItemPromotionQuery query) {
        if (checkChannel && query.getChannel() == null) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG, I18NMessageUtils.getMessage("missing")+"channel");  //# "缺少
        }
        if (CollectionUtils.isEmpty(query.getList())) {
            return new ArrayList<>();
        }
        // 参数解析
        PromotionQueryReq promotionQueryReq = convertItemPromotionRequest(query);
        //一口价接口
        RpcResponse<List<ItemPriceDTO>> resp = RpcUtils.invokeRpc(
            () -> promotionReadFacade.queryItemPrices(promotionQueryReq),
            "promotionReadFacade.queryItemPrices",
            I18NMessageUtils.getMessage("query.sku") + "'" + I18NMessageUtils.getMessage("fixed.price")+"'" + I18NMessageUtils.getMessage("discount"), promotionQueryReq
        );
        //# "查询单品'一口价'优惠"
        return convertItemPromotionResponse(resp, query, promotionQueryReq);
    }

    /**
     * 参数解析
     * @param query
     * @return PromotionQueryReq
     */
    protected PromotionQueryReq convertItemPromotionRequest(ItemPromotionQuery query) {
        List<TargetItem> items = query.getList().stream().map(item -> {
            TargetItem targetItem = new TargetItem();
            targetItem.setItemId(item.getItemId());
            targetItem.setSkuId(item.getSkuId());
            targetItem.setItemQty(1);
            targetItem.setSellerId(item.getSeller().getSellerId());
            targetItem.setSkuOriginPrice(item.getItemPrice().getOriginPrice());
            return targetItem;
        }).collect(Collectors.toList());
        //用户信息
        TargetCust cust = new TargetCust();
        cust.setCustId(query.getCustId());
        PromotionQueryReq promotionQueryReq = new PromotionQueryReq();
        promotionQueryReq.setCust(cust);
        promotionQueryReq.setItems(items);
        ChannelEnum ch = ChannelEnum.get(query.getChannel());
        if (ch != null) {
            promotionQueryReq.setChannel(ch.getId());
        }
        promotionQueryReq.setPromotionSource(query.getPromotionSource());
        return promotionQueryReq;
    }

    /**
     * 解析营销查询结果
     * @param resp
     * @param query
     * @param req
     * @return
     */
    protected List<ItemPromotion> convertItemPromotionResponse(
        RpcResponse<List<ItemPriceDTO>> resp, ItemPromotionQuery query, PromotionQueryReq req) {

        if (CollectionUtils.isEmpty(resp.getData())) {
            return new ArrayList<>();
        }
        // 遍历解析数据
        return resp.getData().stream().map(item -> {
            ItemPromotion itemPromotion = new ItemPromotion();
            itemPromotion.setItemSkuId(new ItemSkuId(item.getItemId(), item.getSkuId(), item.getSellerId()));
            itemPromotion.setItemPrice(item.getItemPriceNotNull());
            if (item.getCamp() != null) {
                itemPromotion.setItemPriceName(item.getCamp().getName());
                itemPromotion.setItemDivideDetails(Lists.newArrayList(promotionRpcConverter.campToItemDivideDetail(item.getCamp())));
            }
            return itemPromotion;
        }).collect(Collectors.toList());
    }

    @Override
    public OrderPromotion queryOrderPromotion(OrderPromotion query, QueryFrom from) {
        if (checkChannel && query.getOrderChannel() == null) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG, I18NMessageUtils.getMessage("missing")+"channel");  //# "缺少
        }
        PromotionQueryReq req = convertOrderPromotionRequest(query, from);
        setForExtra(query,req);
        RpcResponse<PromotionSummation> resp;
        if (from == QueryFrom.CART) {
            resp = RpcUtils.invokeRpc(
                () -> promotionReadFacade.queryPromotionSummation(req),
    "promotionReadFacade.queryPromotionSummation#cart",
                I18NMessageUtils.getMessage("cart.discount.query"), req);  //# "购物车查询优惠"
        } else if (from == QueryFrom.CONFIRM_ORDER) {
            resp = RpcUtils.invokeRpc(
                () -> promotionReadFacade.queryPromotionSummation(req),
    "promotionReadFacade.queryPromotionSummation#confirmOrder",
                I18NMessageUtils.getMessage("confirm.discount.query"), req);  //# "确认订单查询优惠"
        } else if (from == QueryFrom.CREATE_ORDER) {
            resp = RpcUtils.invokeRpc(
                () -> promotionReadFacade.queryPromotionSummation(req),
    "promotionReadFacade.queryPromotionSummation#createOrder",
                I18NMessageUtils.getMessage("create.discount.query"), req);  //# "创单查询优惠"
        } else {
            throw new GmallException(CommonErrorCode.SERVER_ERROR);
        }
        return convertOrderPromotionResponse(resp, query, from, req);
    }

    /**
     * 解析营销结果
     * @param resp
     * @param query
     * @param from
     * @param req
     * @return
     */
    protected OrderPromotion convertOrderPromotionResponse(
            RpcResponse<PromotionSummation> resp,
            OrderPromotion query,
            QueryFrom from,
            PromotionQueryReq req) {
        return promotionRpcConverter.toOrderPromotion(resp.getData(), query);
    }


    /**
     * 查合并下单、购物车组合优惠信息
     * @param query
     * @param from
     * 2025-2-13 09:52:16
     */
    @Override
    public OrderPromotion queryOrderPromotionNew(OrderPromotion query, QueryFrom from) {
        PromotionQueryReq req = convertOrderPromotionRequest(query, from);
        setForExtra(query, req);
        RpcResponse<PromotionSummation> resp;
        if (from == QueryFrom.CART) {
            req.setBizType("shoppingCart");
            resp = RpcUtils.invokeRpc(
                () -> promotionReadFacade.queryPromotionSummation(req),
                "promotionReadFacade.queryPromotionSummation#cart",
                I18NMessageUtils.getMessage("cart.discount.query"), req);  //# "购物车查询优惠"
            return convertCartPromotionResponseNew(resp, query);
        } else if (from == QueryFrom.CONFIRM_ORDER) {
            resp = RpcUtils.invokeRpc(
                () -> promotionReadFacade.calculatePromotionPrice(req),
                "promotionReadFacade.queryPromotionSummation#confirmOrder",
                I18NMessageUtils.getMessage("confirm.discount.query"), req);  //# "确认订单查询优惠"
            return convertOrderPromotionResponseNew(resp, query);
        } else if (from == QueryFrom.CREATE_ORDER) {
            req.setBizType("shoppingCart");
            resp = RpcUtils.invokeRpc(
                () -> promotionReadFacade.queryPromotionSummation(req),
                "promotionReadFacade.queryPromotionSummation#createOrder",
                I18NMessageUtils.getMessage("create.discount.query"), req);  //# "创单查询优惠"
            return convertCartPromotionResponseNew(resp, query);
        } else {
            throw new GmallException(CommonErrorCode.SERVER_ERROR);
        }

    }

    /**
     * 解析营销结果
     * @param resp
     * @param query
     * @return
     */
    protected OrderPromotion convertCartPromotionResponseNew(RpcResponse<PromotionSummation> resp, OrderPromotion query) {
        return promotionRpcConverter.toCartPromotionBySeller(resp.getData(), query);
    }

    /**
     * 解析 订单营销结果
     * @param resp
     * @param query
     * @return
     */
    protected OrderPromotion convertOrderPromotionResponseNew(RpcResponse<PromotionSummation> resp, OrderPromotion query) {
        return promotionRpcConverter.toOrderPromotionByQuoteId(resp.getData(), query);
    }

    private void setForExtra(OrderPromotion query, PromotionQueryReq req) {
        if (MapUtils.isEmpty(query.getExtraMap())) {
            return;
        }
        if (query.getExtra(IS_NEW_USER) != null && query.getExtra(SPECIFIC_GROUP_ID) != null) {
            TargetCust targetCust = new TargetCust();
            Boolean isNewUser = safeParseBoolean(query.getExtra(IS_NEW_USER));
            Object specificGroupId = query.getExtra(SPECIFIC_GROUP_ID);
            if (isNewUser != null && isNewUser) {
                targetCust.setCustId(query.getCustId());
                targetCust.setIsNewUser(true);
                targetCust.setSpecificGroupId(Long.valueOf(specificGroupId.toString()));
                req.setCust(targetCust);
            }
        }
    }

    private Boolean safeParseBoolean(Object isNewUser) {
        if (isNewUser instanceof String) {
            return Boolean.valueOf((String) isNewUser);
        }
        if (isNewUser instanceof Boolean) {
            return (Boolean) isNewUser;
        }
        throw new IllegalArgumentException();
    }

    protected PromotionQueryReq convertOrderPromotionRequest(OrderPromotion query, QueryFrom from) {
        PromotionQueryReq req = promotionRpcConverter.toPromotionQueryReq(query);
        ChannelEnum ch = ChannelEnum.get(query.getOrderChannel());
        if (ch != null) {
            req.setChannel(ch.getId());
        }
        if (from == QueryFrom.CART) {
            //req.setEnableCache(true);  不开启缓存, 开启后预热期价格会出错
        } else if (from == QueryFrom.CONFIRM_ORDER) {
            req.setCustSelection(true); // 确认订单页既要支持用户选择，又须推算其他项可选性
            req.setWithSelectable(true);
        } else if (from == QueryFrom.CREATE_ORDER) {
            req.setCustSelection(true); // 创建订单只须支持用户选择，无须推算其他项可选性
        } else {
            throw new GmallException(CommonErrorCode.SERVER_ERROR);
        }
        return req;
    }


    @Override
    public void deductUserAssets(CreatingOrder order) {
        if (checkChannel && Objects.isNull(order.getOrderChannel())) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG, I18NMessageUtils.getMessage("missing")+"channel");  //# "缺少
        }
        OrderCreateReq orderCreateReq = getDeductParams(order);
        RpcUtils.invokeRpc(() ->
            assetsWriteFacade.deductAssets(orderCreateReq),
            "tradePromotionFacade.createOrder",
            I18NMessageUtils.getMessage("deduct.promo.asset"), orderCreateReq
        );  //# "扣减营销资产"
    }

    @Override
    public void rollbackUserAssets(CreatingOrder order) {
        if (checkChannel &&  Objects.isNull(order.getOrderChannel())) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR_WITH_ARG, I18NMessageUtils.getMessage("missing")+"channel");  //# "缺少
        }
        OrderCreateReq req = getDeductParams(order);
        RpcUtils.invokeRpc(
            () -> assetsWriteFacade.returnAssets(req),
            "tradePromotionFacade.returnBackOrder",
            I18NMessageUtils.getMessage("rollback.promo.asset"),
            req
        );  //# "回滚营销资产"
    }

    // 扣减优惠券的参数
    private OrderCreateReq getDeductParams(CreatingOrder order) {
        if (order.getPromotions() == null || order.getPromotions().getDeductUserAssets() == null) {
            return null; // 无优惠信息
        }

        String cacheKey = "__GMALL_DeductCouponParams";
        OrderCreateReq cache = (OrderCreateReq) order.getExtra(cacheKey);
        if (cache != null) {
            return cache;
        }

        Map<Long, List<PromDivideDTO>> map = (Map) order.getPromotions().getDeductUserAssets();
        Map<Long, Long> pidMap = new HashMap<>();
        Long custId = null;
        for (MainOrder main : order.getMainOrders()) {
            pidMap.put(main.getSeller().getSellerId(), main.getPrimaryOrderId());
            custId = main.getCustomer().getCustId();
        }

        List<Cluster> list = new ArrayList<>();
        for (Entry<Long, List<PromDivideDTO>> en : map.entrySet()) {
            Cluster cluster = new Cluster();
            cluster.setSellerId(en.getKey());
            cluster.setMainOrderId(pidMap.get(en.getKey()));
            cluster.setUsedProms(en.getValue());
            list.add(cluster);
        }

        OrderCreateReq req = new OrderCreateReq();
        req.setCustId(custId);
        req.setOrders(list);
        order.putExtra(cacheKey, req);
        return req;
    }

    /**
     * 订单退券 逆向流程退券
     * 回滚营销资产（优惠券）
     * 整单退券使用！！
     */
    @Override
    public void orderRollbackUserAssets(MainOrder mainOrder) {
        if (checkChannel ||
            Objects.isNull(mainOrder) ||
            CollectionUtils.isEmpty(mainOrder.getSubOrders())) {
            return;
        }
        OrderCreateReq req = new OrderCreateReq();
        req.setCustId(mainOrder.getCustomer().getCustId());
        //订单只会有一个单
        List<OrderCreateReq.Cluster> orders = new ArrayList<>();
        OrderCreateReq.Cluster cluster = new Cluster();
        cluster.setMainOrderId(mainOrder.getPrimaryOrderId());
        cluster.setSellerId(mainOrder.getSeller().getSellerId());
        // 优惠数组
        List<PromDivideDTO> usedProms = new ArrayList<>();
        Map<Long, ItemDivideDetail> longItemDivideDetailMap = new HashMap<>();
        for (SubOrder subOrder : mainOrder.getSubOrders()) {
            if (Objects.isNull(subOrder) ||
                Objects.isNull(subOrder.getPromotions()) ||
                CollectionUtils.isEmpty(subOrder.getPromotions().getItemDivideDetails())) {
                continue;
            }
            for (ItemDivideDetail itemDivideDetail : subOrder.getPromotions().getItemDivideDetails()) {
                if (Objects.nonNull(longItemDivideDetailMap.get(itemDivideDetail.getCampId()))) {
                    continue;
                }
                PromDivideDTO promDivideDTO = new PromDivideDTO();
                if (Objects.nonNull(itemDivideDetail.getAssetType()) && itemDivideDetail.getAssetType() > NONE.getCode()) {
                    promDivideDTO.setAssetType(itemDivideDetail.getAssetType());
                    promDivideDTO.setAssetsId(itemDivideDetail.getAssetsId());
                    promDivideDTO.setCampId(itemDivideDetail.getCampId());
                    promDivideDTO.setLevel(itemDivideDetail.getLevel());
                    promDivideDTO.setToolCode(itemDivideDetail.getToolCode());
                    promDivideDTO.setName(itemDivideDetail.getName());
                    promDivideDTO.setReduce(itemDivideDetail.getReduce());
                    usedProms.add(promDivideDTO);
                    longItemDivideDetailMap.put(itemDivideDetail.getCampId(), itemDivideDetail);
                }
            }
        }
        if (CollectionUtils.isEmpty(usedProms)) {
            return;
        }
        cluster.setUsedProms(usedProms);
        orders.add(cluster);
        req.setOrders(orders);
        RpcUtils.invokeRpc(
            () -> assetsWriteFacade.returnAssets(req),
            "tradePromotionFacade.returnBackOrder",
            I18NMessageUtils.getMessage("rollback.promo.asset"),
            req
        );  //# "回滚营销资产"
    }

}
