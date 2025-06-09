package com.aliyun.gts.gmall.test.platform.trade.integrate.mock;

import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.platform.promotion.api.dto.input.PromotionQueryReq;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.ItemPriceDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.display.PromotionInfo;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.display.PromotionSummation;
import com.aliyun.gts.gmall.platform.promotion.api.facade.cust.PromotionReadFacade;
import com.aliyun.gts.gmall.platform.promotion.common.model.ItemDividePriceDTO;
import com.aliyun.gts.gmall.platform.promotion.common.model.TargetItem;
import com.aliyun.gts.gmall.platform.promotion.common.model.TargetItemCluster;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MockPromotionReadFacade implements PromotionReadFacade {
    @Override
    public RpcResponse<List<ItemPriceDTO>> queryItemPrices(PromotionQueryReq promotionQueryReq) {
        List<ItemPriceDTO> list = new ArrayList<>();
        for (TargetItemCluster seller : promotionQueryReq.getItemClusters()) {
            for (TargetItem item : seller.getTargetItems()) {
                ItemPriceDTO price = new ItemPriceDTO();
                price.setTotalPromPrice(100L * item.getItemQty());
                price.setOrigPrice(item.getSkuOriginPrice());
                price.setItemPrice(100L);
                price.setItemQty(item.getItemQty());
                price.setItemId(item.getItemId());
                price.setSkuId(item.getSkuId());
                list.add(price);
            }
        }
        return RpcResponse.ok(list);
    }

    @Override
    public RpcResponse<PromotionInfo> queryPromotionInfo(PromotionQueryReq promotionQueryReq) {
        PromotionInfo info = new PromotionInfo();
        return RpcResponse.ok(info);
    }

    @Override
    public RpcResponse<PromotionSummation> queryPromotionSummation(PromotionQueryReq promotionQueryReq) {
        PromotionSummation summation = new PromotionSummation();
        Map<Long, ItemDividePriceDTO> divMap = new HashMap<>();
        summation.setItemDivide(divMap);
        for (TargetItemCluster seller : promotionQueryReq.getItemClusters()) {
            for (TargetItem item : seller.getTargetItems()) {
                ItemDividePriceDTO price = new ItemDividePriceDTO();
                divMap.put(item.getSkuId(), price);
                price.setTotalPromPrice(100L * item.getItemQty());
                price.setOrigPrice(item.getSkuOriginPrice());
                price.setItemPrice(100L);
                price.setItemQty(item.getItemQty());
                price.setItemId(item.getItemId());
                price.setSkuId(item.getSkuId());
            }
        }
        return RpcResponse.ok(summation);
    }
}
