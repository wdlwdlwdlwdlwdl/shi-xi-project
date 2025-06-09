package com.aliyun.gts.gmall.center.trade.core.domainservice.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.center.promotion.api.dto.input.OrderGiftRequest;
import com.aliyun.gts.gmall.center.promotion.api.facade.PromotionManZengFacade;
import com.aliyun.gts.gmall.center.trade.common.constants.OrderFeatureKey;
import com.aliyun.gts.gmall.center.trade.common.constants.OrderTagPrefix;
import com.aliyun.gts.gmall.center.trade.core.domainservice.ManzengGrantService;
import com.aliyun.gts.gmall.center.trade.core.util.ManzengBuildUtils;
import com.aliyun.gts.gmall.center.trade.domain.entity.promotion.ManzengGift;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.promotion.common.constant.PromotionToolCodes;
import com.aliyun.gts.gmall.platform.trade.api.dto.message.OrderMessageDTO;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ManzengGrantServiceImpl implements ManzengGrantService {

    @Autowired
    private OrderQueryAbility orderQueryAbility;
    @Autowired
    private PromotionManZengFacade promotionManZengFacade;

    @Override
    public void onManzengSend(OrderMessageDTO message) {
        // 忽略没有满赠标的
        if (CollectionUtils.isEmpty(message.getOrderTags())) {
            return;
        }
        //表示有满赠订单
        String mzTag = OrderTagPrefix.CAMP_TOOL + PromotionToolCodes.MANZENG;
        if (message.getOrderTags().indexOf(mzTag) < 0) {
            return;
        }
        //处理满赠逻辑
        try {
            RpcResponse<Boolean> response = this.processSendSucc(message.getPrimaryOrderId());
            log.info("manzengSend,"+message.getPrimaryOrderId()+",",JSON.toJSONString(response));
        } catch (Exception e) {
            log.error("manzengSendSuccExp,"+message.getPrimaryOrderId()+",", e);
        }
    }

    @Override
    public void onManzengPaid(OrderMessageDTO message) {
        // 忽略没有满赠标的
        if (CollectionUtils.isEmpty(message.getOrderTags())) {
            return;
        }
        //表示有满赠订单
        String mzTag = OrderTagPrefix.CAMP_TOOL + PromotionToolCodes.MANZENG;
        if (message.getOrderTags().indexOf(mzTag) < 0) {
            return;
        }
        //处理满赠逻辑
        try {
            RpcResponse<Boolean> response = this.processPaySucc(message.getPrimaryOrderId());
            log.info("onManzengPaid,"+message.getPrimaryOrderId()+",",JSON.toJSONString(response));
        } catch (Exception e) {
            log.error("onManzengPaidExp,"+message.getPrimaryOrderId()+",", e);
        }
    }

    @Override
    public void giftOrderGrantItem(MainOrder mainOrder) {
        //发放商品
        try {
            this.processGrantItem(mainOrder);
            log.info("grantOnOrderPaid," + mainOrder.getPrimaryOrderId()+",");
        } catch (Exception e) {
            log.error("grantOnOrderPaidExp," + mainOrder.getPrimaryOrderId(), e);
        }
    }

    public void test(Long mainOrderId) {
        processPaySucc(mainOrderId);
    }

    private void processGrantItem(MainOrder mainOrder) {
        Long custId = mainOrder.getCustomer().getCustId();
        for (SubOrder subOrder : mainOrder.getSubOrders()) {
            //有满赠订单
            if (!subOrder.containsTag(OrderTagPrefix.MANZENG_GIFT)) {
                return;
            }
            String str = subOrder.getOrderAttr().getExtend(OrderFeatureKey.MANZENG_GIFT);
            ManzengGift gift = JSON.parseObject(str, ManzengGift.class);
            //主订单发放
            OrderGiftRequest request = ManzengBuildUtils.buildRequest(
                    custId,
                    gift.getCampId(),
                    subOrder.getOrderId() + "",
                    gift.getRefOrders()
            );
            request.getFeature().put("orderId", mainOrder.getPrimaryOrderId());
            request.setItemId(subOrder.getItemSku().getItemId());
            this.paySuccess(request);
        }
    }

    /**
     * 校验满赠是否ok
     *
     * @param mainOrderId
     * @return
     */
    public RpcResponse<Boolean> processSendSucc(Long mainOrderId) {
        MainOrder mainOrder = orderQueryAbility.getMainOrder(mainOrderId);
        //合并支付的主订单
        List<Long> mergeOrderIds = mainOrder.getOrderAttr().getMergeOrderIds();
        if (mergeOrderIds == null) {
            mergeOrderIds = new ArrayList<>();
            mergeOrderIds.add(mainOrder.getPrimaryOrderId());
        }
        List<Long> orderIds = mergeOrderIds.stream()
                .filter(p -> (p.longValue() != mainOrderId.longValue()))
                .collect(Collectors.toList());
        //查询所有主订单
        List<MainOrder> mainOrders = orderQueryAbility.batchGetMainOrder(orderIds);
        List<SubOrder> subOrders = new ArrayList<>();
        subOrders.addAll(mainOrder.getSubOrders());
        for (MainOrder order : mainOrders) {
            subOrders.addAll(order.getSubOrders());
        }
        //过滤出满赠订单
        Map<Long, List<SubOrder>> maps = ManzengBuildUtils.partitionSubOrder(subOrders);
        for (Long campId : maps.keySet()) {
            List<SubOrder> orders = maps.get(campId);
            Boolean success = ManzengBuildUtils.checkAllOrderSuccess(orders);
            if (success != true) {
                continue;
            }
            //生成幂等ID
            String bizId = ManzengBuildUtils.buildBizId(campId, mergeOrderIds);
            List<Long> refOrders = orders.stream().map(o -> o.getOrderId()).collect(Collectors.toList());
            OrderGiftRequest request = ManzengBuildUtils.buildRequest(mainOrder.getCustomer().getCustId(), campId, bizId, refOrders);
            return this.confirmTake(request);
        }
        return RpcResponse.fail("101", "no wardSend");
    }


    /**
     * 校验满赠是否ok
     *
     * @param mainOrderId
     * @return
     */
    public RpcResponse<Boolean> processPaySucc(Long mainOrderId) {
        MainOrder mainOrder = orderQueryAbility.getMainOrder(mainOrderId);
        //合并支付的主订单
        List<Long> mergeOrderIds = mainOrder.getOrderAttr().getMergeOrderIds();
        if (CollectionUtils.isEmpty(mergeOrderIds)) {
            mergeOrderIds = new ArrayList<>();
            mergeOrderIds.add(mainOrder.getPrimaryOrderId());
        }
        List<SubOrder> subOrders = new ArrayList<>();

        List<Long> orderIds = mergeOrderIds.stream()
                .filter(p -> (!p.equals(mainOrderId)))
                .collect(Collectors.toList());
        //查询所有主订单
        if (CollectionUtils.isNotEmpty(orderIds)) {
            List<MainOrder> mainOrders = orderQueryAbility.batchGetMainOrder(orderIds);
            for (MainOrder order : mainOrders) {
                subOrders.addAll(order.getSubOrders());
            }
        }
        //过滤出满赠订单
        Map<Long, List<SubOrder>> maps = ManzengBuildUtils.partitionSubOrder(mainOrder.getSubOrders());
        for (Long campId : maps.keySet()) {
            List<SubOrder> orders = maps.get(campId);
            //生成幂等ID
            String bizId = ManzengBuildUtils.buildBizId(campId, mergeOrderIds);
            List<Long> refOrders = orders.stream().map(o -> o.getOrderId()).collect(Collectors.toList());
            OrderGiftRequest request = ManzengBuildUtils.buildRequest(
                    mainOrder.getCustomer().getCustId(),
                    campId,
                    bizId,
                    refOrders
            );
            if(CollectionUtils.isNotEmpty(subOrders)) {
                if(Objects.nonNull(subOrders.get(0).getItemSku())) {
                    request.setItemId(subOrders.get(0).getItemSku().getItemId());
                }
            }
            return this.paySuccess(request);
        }
        return RpcResponse.fail("101", "no wardSend");
    }

    /**
     * 营销赠送满赠
     * @return
     */
    private RpcResponse<Boolean> paySuccess(OrderGiftRequest request) {
        return promotionManZengFacade.paySuccess(request);
    }

    /**
     * 确定订单
     * @param request
     * @return
     */
    private RpcResponse<Boolean> confirmTake(OrderGiftRequest request) {
        return promotionManZengFacade.confirmTake(request);
    }
}
