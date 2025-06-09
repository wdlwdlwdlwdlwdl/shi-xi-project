package com.aliyun.gts.gmall.center.trade.server.flow.handler.orderchecksplit;

import com.aliyun.gts.gmall.center.trade.core.domainservice.MixOrderSplitService;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmItemInfo;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmOrderInfoRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ItemService;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.server.flow.context.TOrderConfirm;
import com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.confirm.ConfirmOrderItemHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Component
public class ConfirmOrderItemHandler4Split extends ConfirmOrderItemHandler {

    @Autowired
    private ItemService itemService;

    @Autowired
    private MixOrderSplitService mixOrderSplitService;

    @Override
    protected Map<ItemSkuId, ItemSku> queryItems(TOrderConfirm inbound) {
        ConfirmOrderInfoRpcReq req = inbound.getReq();
        List<ItemSkuId> idList = new ArrayList<>();
        Map<ItemSkuId, Integer> qtyMap = new HashMap<>();
        for (ConfirmItemInfo item : req.getOrderItems()) {
            ItemSkuId id = new ItemSkuId(item.getItemId(), item.getSkuId());
            idList.add(id);
            qtyMap.put(id, item.getItemQty());
        }

        Map<ItemSkuId, ItemSku> itemMap = itemService.queryItems(idList);
        List<ItemSkuId> toRemove = new ArrayList<>();
        for (Entry<ItemSkuId, ItemSku> en : itemMap.entrySet()) {
            if (!en.getValue().isEnabled()) {
                toRemove.add(en.getKey());
            }
        }
        for (ItemSkuId remove : toRemove) {
            itemMap.remove(remove);
        }
        for (ItemSkuId id : idList) {
            if (!itemMap.containsKey(id)) {
                mixOrderSplitService.markDisableItem(inbound.getDomain(), id, qtyMap.get(id),
                        OrderErrorCode.ITEM_NOT_EXISTS.getCode(),
                        OrderErrorCode.ITEM_NOT_EXISTS.getMessage());
            }
        }
        if (itemMap.isEmpty()) {
            CheckSkipAllHandler.setSkipAll(inbound);
        }
        return itemMap;
    }
}
