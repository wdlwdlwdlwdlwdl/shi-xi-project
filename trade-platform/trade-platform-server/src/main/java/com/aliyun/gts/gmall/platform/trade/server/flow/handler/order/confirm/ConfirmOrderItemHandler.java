package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.confirm;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmOrderInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderBizCodeAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ItemService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderCreateService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import com.aliyun.gts.gmall.platform.trade.server.utils.BizCodeHandlerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ConfirmOrderItemHandler extends AdapterHandler<TOrderConfirm> {

    @Autowired
    private OrderCreateService orderCreateService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private OrderBizCodeAbility orderBizCodeAbility;

    @Override
    public void handle(TOrderConfirm inbound) {
        ConfirmOrderInfoRpcReq req = inbound.getReq();
        CreatingOrder order = inbound.getDomain();

        // 查询商品
        Map<ItemSkuId, ItemSku> itemMap = queryItems(inbound);

        //如果是代客下单需要对代客下单价进行处理
//        if(CreatingOrderParamUtils.isHelpOrder(req.getParams())) {
//            Map<ItemSkuId, Long> item2HelpOrderPrice = req.getOrderItems().stream()
//                    .filter(item-> Objects.nonNull(item.getHelpOrderPrice())&&item.getHelpOrderPrice()>=0)
//                    .collect(Collectors.toMap(item -> new ItemSkuId(item.getItemId(), item.getSkuId()), ConfirmItemInfo::getHelpOrderPrice));
//
//            if(item2HelpOrderPrice.size()>0) {
//               for(Map.Entry<ItemSkuId, ItemSku> entry: itemMap.entrySet()) {
//                   ItemSkuId itemSkuId = entry.getKey();
//                   Long helpOrderPrice = item2HelpOrderPrice.get(itemSkuId);
//                   entry.getValue().getItemPrice().setHelpOrderPrice(helpOrderPrice);
//                   //将代客下单价写入order.params供创单时使用
//                   order.addParam(CreatingOrderParamConstants.HELP_ORDER_PREFIX+itemSkuId.getItemId()+"-"+itemSkuId.getSkuId(), helpOrderPrice);
//               }
//            }
//        }

        // 按卖家拆单
        List<MainOrder> mainOrders = orderCreateService.splitOrder(itemMap.values());
        order.setMainOrders(mainOrders);

        // 商品业务身份
        BizCodeHandlerUtils.fillBizCode(inbound, orderBizCodeAbility::getBizCodesFromItem);
    }

    protected Map<ItemSkuId, ItemSku> queryItems(TOrderConfirm inbound) {
        ConfirmOrderInfoRpcReq req = inbound.getReq();
        List<ItemSkuId> idList = req.getOrderItems().stream()
                .map(item -> new ItemSkuId(item.getItemId(), item.getSkuId()))
                .collect(Collectors.toList());
        Map<ItemSkuId, ItemSku> itemMap = itemService.queryItemsRequired(idList);
        for (ItemSku item : itemMap.values()) {
            if (!item.isEnabled()) {
                throw new GmallException(OrderErrorCode.ITEM_NOT_ENABLE);
            }
        }
        return itemMap;
    }
}
