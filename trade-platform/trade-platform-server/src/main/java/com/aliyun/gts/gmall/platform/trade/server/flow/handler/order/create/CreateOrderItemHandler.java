package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.create;

import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.common.constants.CreatingOrderParamConstants;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderFeatureAbility;
import com.aliyun.gts.gmall.platform.trade.core.ability.order.create.OrderBizCodeAbility;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ItemService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.repository.ItemRepository;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderCreate;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.TradeFlowHandler.AdapterHandler;
import com.aliyun.gts.gmall.platform.trade.server.utils.BizCodeHandlerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class CreateOrderItemHandler extends AdapterHandler<TOrderCreate> {

    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrderBizCodeAbility orderBizCodeAbility;
    @Autowired
    private OrderFeatureAbility orderFeatureAbility;
    @Override
    public void handle(TOrderCreate inbound) {

        CreatingOrder order = inbound.getDomain();

        // 查询商品
        List<ItemSkuId> idList = order.getMainOrders().stream()
                .flatMap(main -> main.getSubOrders().stream())
                .map(sub -> new ItemSkuId(sub.getItemSku().getItemId(), sub.getItemSku().getSkuId()))
                .collect(Collectors.toList());
        Map<ItemSkuId, ItemSku> itemMap = itemService.queryItemsRequired(idList);
        for (ItemSku item : itemMap.values()) {
            if (!item.isEnabled()) {
                throw new GmallException(OrderErrorCode.ITEM_NOT_ENABLE);
            }
        }

        // 查类目信息
        itemRepository.fillCategoryInfo(itemMap.values());

        // 回填数据
        for (MainOrder main : order.getMainOrders()) {
            for (SubOrder sub : main.getSubOrders()) {
                ItemSku item = itemMap.get(sub.getItemSku().getItemSkuId());
                //对代客下单逻辑进行处理
                String helpOrderItemSkuKey = CreatingOrderParamConstants.HELP_ORDER_PREFIX+item.getItemId()+"-"+item.getSkuId();
                Object helpOrderPrice = order.getParam(helpOrderItemSkuKey);
                if(Objects.nonNull(helpOrderPrice)) {
                    // 避免 class cast error
                    item.getItemPrice().setHelpOrderPrice(Long.parseLong(helpOrderPrice.toString()));
                }
                sub.setItemSku(item);
                main.setSeller(item.getSeller());
            }
        }

        // 商品业务身份
        BizCodeHandlerUtils.fillBizCode(inbound, orderBizCodeAbility::getBizCodesFromItem);
        //扩展数据
        orderFeatureAbility.addItemFeature(order);
    }
}
