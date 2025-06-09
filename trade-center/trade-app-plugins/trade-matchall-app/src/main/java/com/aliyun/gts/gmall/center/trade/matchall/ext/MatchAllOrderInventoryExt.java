package com.aliyun.gts.gmall.center.trade.matchall.ext;

import com.aliyun.gts.gmall.center.trade.api.dto.output.CombineItemDTO;
import com.aliyun.gts.gmall.center.trade.api.dto.output.ReversalCombItemDTO;
import com.aliyun.gts.gmall.center.trade.core.util.CombineItemBuilder;
import com.aliyun.gts.gmall.framework.extensionengine.ext.model.ExtensionFilterContext;
import com.aliyun.gts.gmall.platform.trade.core.extension.defaultimpl.order.DefaultOrderInventoryExt;
import com.aliyun.gts.gmall.platform.trade.core.extension.order.OrderInventoryExt;
import com.aliyun.gts.gmall.platform.trade.domain.entity.inventory.InventoryReduceParam;
import com.aliyun.gts.gmall.platform.trade.domain.entity.inventory.InventoryRollbackParam;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import lombok.extern.slf4j.Slf4j;
import org.pf4j.Extension;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/10/22 12:22
 */
@Slf4j
@Extension(points = {OrderInventoryExt.class})
public class MatchAllOrderInventoryExt extends DefaultOrderInventoryExt {

    @Override
    public boolean filter(ExtensionFilterContext context) {
        return super.filter(context);   // 此扩展点不排他
    }
    /**
     * 订单关闭,回滚库存;非逆向
     * @param orders
     * @return
     */
    @Override
    protected List<InventoryRollbackParam> getRollbackList(List<SubOrder> orders) {
        List<InventoryRollbackParam> result = new ArrayList<>();
        for(SubOrder sub : orders){
            List<CombineItemDTO> combItemSkus = CombineItemBuilder.parseCombineItemByStored(sub.getItemSku());
            //组合商品的话,sku抵扣选择子sku
            if(combItemSkus != null){
                for(CombineItemDTO csku : combItemSkus){
                    InventoryRollbackParam p = new InventoryRollbackParam();
                    p.setOrderId(sub.getOrderId());
                    p.setSkuId(new ItemSkuId(csku.getItemId(),csku.getSkuId()));
                    //回补数量
                    p.setSkuQty(sub.getOrderQty() * csku.getJoinQty());
                    result.add(p);
                }
            }else {
                InventoryRollbackParam p = new InventoryRollbackParam();
                p.setOrderId(sub.getOrderId());
                p.setSkuId(sub.getItemSku().getItemSkuId());
                p.setSkuQty(sub.getOrderQty());
                result.add(p);
            }
        }
        return result;
    }

    @Override
    protected List<InventoryReduceParam> getReduceList(List<MainOrder> orders) {
        List<SubOrder> subOrders = orders.stream()
                .flatMap(main -> main.getSubOrders().stream())
                .collect(Collectors.toList());
        List<InventoryReduceParam> result = new ArrayList<>();
        for(SubOrder sub : subOrders){
            List<CombineItemDTO> combItemSkus = CombineItemBuilder.parseCombineItemByStored(sub.getItemSku());
            //组合商品的话,sku抵扣选择子sku
            if(combItemSkus != null){
                for(CombineItemDTO csku : combItemSkus){
                    InventoryReduceParam p = new InventoryReduceParam();
                    p.setOrderId(sub.getOrderId());
                    p.setSkuId(new ItemSkuId(csku.getItemId(),csku.getSkuId()));
                    p.setSkuQty(sub.getOrderQty() * csku.getJoinQty());
                    result.add(p);
                }
            }else {
                InventoryReduceParam p = new InventoryReduceParam();
                p.setOrderId(sub.getOrderId());
                p.setSkuId(sub.getItemSku().getItemSkuId());
                p.setSkuQty(sub.getOrderQty());
                result.add(p);
            }
        }
        return result;
    }

    /**
     * 关闭订单回滚库存
     * @param mainReversal
     * @return
     */
    @Override
    protected List<InventoryRollbackParam> getRollbackList(MainReversal mainReversal) {
        List<InventoryRollbackParam> inventoryRollbackParams = new ArrayList<>();
        mainReversal.getSubReversals().stream().forEach(subReversal -> {
            subReversal.getReversalFeatures().getFeature();
            List<ReversalCombItemDTO> combItemDTOS = CombineItemBuilder.parseReversal(subReversal.getReversalFeatures());
            if(!CollectionUtils.isEmpty(combItemDTOS)){
                for(ReversalCombItemDTO csku : combItemDTOS){
                    InventoryRollbackParam p = new InventoryRollbackParam();
                    p.setOrderId(subReversal.getSubOrder().getOrderId());
                    p.setReversalId(subReversal.getReversalId());
                    p.setSkuId(new ItemSkuId(csku.getItemId(),csku.getSkuId()));
                    p.setSkuQty(csku.getCancelQty());
                    inventoryRollbackParams.add(p);
                }
            }else {
                InventoryRollbackParam inventoryRollbackParam = new InventoryRollbackParam();
                inventoryRollbackParam.setOrderId(subReversal.getSubOrder().getOrderId());
                inventoryRollbackParam.setReversalId(subReversal.getReversalId());
                ItemSkuId itemSkuId = new ItemSkuId();
                itemSkuId.setItemId(subReversal.getSubOrder().getItemSku().getItemId());
                itemSkuId.setSkuId(subReversal.getSubOrder().getItemSku().getSkuId());
                inventoryRollbackParam.setSkuId(itemSkuId);
                inventoryRollbackParam.setSkuQty(subReversal.getCancelQty());
                inventoryRollbackParams.add(inventoryRollbackParam);
            }
        });
        return inventoryRollbackParams;
    }
}
