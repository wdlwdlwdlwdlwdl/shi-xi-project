package com.aliyun.gts.gmall.center.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.center.trade.api.dto.output.ConfirmOrderSplitDTO;
import com.aliyun.gts.gmall.center.trade.api.dto.output.ConfirmOrderSplitDTO.SplitPartDTO;
import com.aliyun.gts.gmall.center.trade.common.constants.ExtBizCode;
import com.aliyun.gts.gmall.center.trade.core.domainservice.MixOrderSplitService;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.confirm.ConfirmItemInfo;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MixOrderSplitServiceImpl implements MixOrderSplitService {
    private static final String CHECK_SPLIT_FLAG = "CHECK_SPLIT";
    private static final String MARK_ALONE_FLAG = "CHECK_SPLIT_ALONE";
    private static final String MARK_ALONE_BZCODE = "CHECK_SPLIT_ALONE_CODE";
    private static final String MARK_DIS_ITEMS = "CHECK_SPLIT_DIS_ITEMS";

    @Override
    public void setCheckSplit(CreatingOrder ord) {
        ord.putExtra(CHECK_SPLIT_FLAG, true);
    }

    @Override
    public boolean isCheckSplit(CreatingOrder ord) {
        return Boolean.TRUE.equals(ord.getExtra(CHECK_SPLIT_FLAG));
    }

    @Override
    public void markDisableItem(CreatingOrder ord, ItemSkuId skuId, Integer qty, String code, String message) {
        Map<String, SplitPartDTO> map = (Map) ord.getExtra(MARK_DIS_ITEMS);
        if (map == null) {
            map = new HashMap<>();
            ord.putExtra(MARK_DIS_ITEMS, map);
        }
        SplitPartDTO part = map.get(code);
        if (part == null) {
            part = new SplitPartDTO();
            part.setItems(new ArrayList<>());
            part.setCanOrder(false);
            part.setCanNotOrderCode(code);
            part.setCanNotOrderMessage(message);
            map.put(code, part);
        }
        ConfirmItemInfo item = new ConfirmItemInfo();
        item.setItemId(skuId.getItemId());
        item.setSkuId(skuId.getSkuId());
        item.setItemQty(qty);
        part.getItems().add(item);
    }

    @Override
    public void markAlone(SubOrder subOrder, String bizCode) {
        subOrder.putExtra(MARK_ALONE_FLAG, true);
        subOrder.putExtra(MARK_ALONE_BZCODE, bizCode);
    }

    @Override
    public ConfirmOrderSplitDTO getSplitResult(CreatingOrder ord) {
        SplitPartDTO mergePart = new SplitPartDTO();
        mergePart.setBizCode(ExtBizCode.NORMAL_TRADE);
        List<SplitPartDTO> aloneParts = new ArrayList<>();
        boolean hasMergePart = false;
        for (MainOrder mainOrder : ord.getMainOrders()) {
            for (SubOrder subOrder : mainOrder.getSubOrders()) {
                if (Boolean.TRUE.equals(subOrder.getExtra(MARK_ALONE_FLAG))) {
                    SplitPartDTO alone = new SplitPartDTO();
                    alone.setBizCode((String) subOrder.getExtra(MARK_ALONE_BZCODE));
                    aloneParts.add(alone);
                    addToPart(alone, subOrder);
                } else {
                    hasMergePart = true;
                    addToPart(mergePart, subOrder);
                }
            }
        }
        List<SplitPartDTO> resultList = new ArrayList<>();
        resultList.addAll(aloneParts);
        if (hasMergePart) {
            resultList.add(mergePart);
        }

        // disable items
        Map<String, SplitPartDTO> disItems = (Map) ord.getExtra(MARK_DIS_ITEMS);
        if (MapUtils.isNotEmpty(disItems)) {
            resultList.addAll(disItems.values());
        }

        ConfirmOrderSplitDTO result = new ConfirmOrderSplitDTO();
        result.setSplitParts(resultList);
        return result;
    }

    private void addToPart(SplitPartDTO part, SubOrder subOrder) {
        if (part.getItems() == null) {
            part.setItems(new ArrayList<>());
        }
        ConfirmItemInfo item = new ConfirmItemInfo();
        item.setItemId(subOrder.getItemSku().getItemId());
        item.setSkuId(subOrder.getItemSku().getSkuId());
        item.setItemQty(subOrder.getOrderQty());
        part.getItems().add(item);
    }
}
