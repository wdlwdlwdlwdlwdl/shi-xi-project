package com.aliyun.gts.gmall.center.trade.core.domainservice.impl;

import com.aliyun.gts.gmall.center.item.api.dto.output.CombineSkuDTO;
import com.aliyun.gts.gmall.center.trade.api.dto.output.CombineItemDTO;
import com.aliyun.gts.gmall.center.trade.api.dto.output.ReversalCombItemDTO;
import com.aliyun.gts.gmall.center.trade.common.constants.ItemConstants;
import com.aliyun.gts.gmall.center.trade.core.constants.ExtReversalErrorCode;
import com.aliyun.gts.gmall.center.trade.core.converter.CombItemConvert;
import com.aliyun.gts.gmall.center.trade.core.domainservice.CombineItemService;
import com.aliyun.gts.gmall.center.trade.core.util.CombineItemBuilder;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.promotion.common.util.JsonUtils;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.CreateReversalRpcReq;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.ItemService;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSku;
import com.aliyun.gts.gmall.platform.trade.domain.entity.item.ItemSkuId;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.CreatingOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.SubReversal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.aliyun.gts.gmall.center.trade.core.constants.ExtOrderErrorCode.INVENTORY_NOT_ENOUGH;


/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/10/22 17:28
 */
@Service
@Slf4j
public class CombineItemServiceImpl implements CombineItemService {
    @Autowired
    private ItemService itemService;

    @Autowired
    private CombItemConvert convert;

    @Override
    public Map<Long, List<CombineItemDTO>> queryCombineItem(List<MainOrder> mainOrders) {
        Map<Long, List<CombineItemDTO>> result = new HashMap<>();
        //解析所有SKU
        List<ItemSku> skuList = mainOrders.stream()
                .flatMap(main -> main.getSubOrders().stream())
                .map(sub -> sub.getItemSku())
                .collect(Collectors.toList());
        //过滤组合商品
        Map<Long, List<CombineSkuDTO>> combineSkus = CombineItemBuilder.parseCombItemFromItemFeature(skuList);
        if (combineSkus.size() <= 0) {
            return result;
        }
        //解析参数;查询SKU信息
        Set<ItemSkuId> idList = new HashSet<>();
        for (List<CombineSkuDTO> list : combineSkus.values()) {
            for (CombineSkuDTO skuDTO : list) {
                idList.add(new ItemSkuId(skuDTO.getItemId(), skuDTO.getId()));
            }
        }
        //查询组合商品的SKU信息
        Map<ItemSkuId, ItemSku> response = itemService.queryItemsRequired(new ArrayList<>(idList));
        return fillResult(combineSkus, response);
    }

    @Override
    public TradeBizResult confirmCheck(CreatingOrder order) {
        //确认订单校验
        Map<Long, List<CombineItemDTO>> result =  queryCombineItem(order.getMainOrders());
        List<SubOrder> orders = order.getMainOrders().stream()
                .flatMap(main -> main.getSubOrders().stream())
                .collect(Collectors.toList());
        //组合商品库存校验;
        for (SubOrder subOrder : orders) {
            List<CombineItemDTO> combItemSkus = result.get(subOrder.getItemSku().getSkuId());
            if (!CollectionUtils.isEmpty(combItemSkus)) {
                for(CombineItemDTO csku : combItemSkus){
                    //组合商品库存不足
                    int num = csku.getJoinQty() * subOrder.getOrderQty();
                    if(num > csku.getSkuQty() ){
                        return TradeBizResult.fail(INVENTORY_NOT_ENOUGH);
                    }
                }
                Map<String, String> store = new HashMap<>();
                store.put(ItemConstants.COMBINE_ITEM, JsonUtils.toJSONString(combItemSkus));
                subOrder.getItemSku().addStoreExt(store);
            }
        }
        return TradeBizResult.ok();
    }

    @Override
    public void checkReversal(MainReversal reversal, List<MainReversal> historyList) {
        Map<Long,Map<ItemSkuId,Integer>> canceled = CombineItemBuilder.parseHistoryReversalQty(historyList);
        for (SubReversal sub : reversal.getSubReversals()) {
            List<ReversalCombItemDTO> combines =(List<ReversalCombItemDTO>)sub.getExtra(ItemConstants.COMBINE_ITEM);
            if(CollectionUtils.isEmpty(combines)){
                continue;
            }
            //组合商品
            Map<ItemSkuId,Integer> qtysMaps = canceled.get(sub.getSubOrder().getOrderId());
            qtysMaps = qtysMaps == null ? new HashMap<>() : qtysMaps;
            for(ReversalCombItemDTO cbsku : combines){
                Integer canceledQty = qtysMaps.get(new ItemSkuId(cbsku.getItemId(), cbsku.getSkuId()));
                canceledQty = canceledQty == null ? 0 : canceledQty;
                Integer leftQty = cbsku.getJoinQty() * sub.getSubOrder().getOrderQty() - canceledQty;
                if(cbsku.getCancelQty() == null){
                    cbsku.setCancelQty(leftQty);
                }
                //库存不足
                if(cbsku.getCancelQty() > leftQty){
                    throw new GmallException(ExtReversalErrorCode.CREATE_REVERSAL_OUT_OF_QTY);
                }
            }
            //是create操作;不是query操作
            if(reversal.getReversalReasonCode() != null) {
                this.fillRefundFinish(reversal, sub, combines, canceled);
            }
            //添加到feature
            sub.reversalFeatures().addFeature(ItemConstants.COMBINE_ITEM, JsonUtils.toJSONString(combines));
        }
    }

    @Override
    public void fillReversal(MainReversal reversal, CreateReversalRpcReq req) {
        Map<Long,Map<ItemSkuId,Integer>> cancelQty = CombineItemBuilder.parseInputReversalQty(req);
        for (SubReversal sub : reversal.getSubReversals()) {
            List<CombineItemDTO> combines = CombineItemBuilder.parseCombineItemByStored(sub.getSubOrder().getItemSku());
            if(CollectionUtils.isEmpty(combines)){
                continue;
            }
            Map<ItemSkuId,Integer> qtysMaps = cancelQty.get(sub.getSubOrder().getOrderId());
            //组合商品
            List<ReversalCombItemDTO> list = new ArrayList<>();
            for(CombineItemDTO cbsku : combines){
                ReversalCombItemDTO reversalItem = convert.convert(cbsku);
                if(cancelQty.size() > 0){
                    if(qtysMaps == null){
                        continue;
                    }
                    //退款数量;为空表示劝退
                    Integer value = qtysMaps.get(new ItemSkuId(cbsku.getItemId(), cbsku.getSkuId()));
                    if(value == null){
                        continue;
                    }
                    //覆盖为用户填写的数量
                    reversalItem.setCancelQty(value != null ? value : reversalItem.getCancelQty());
                }
                list.add(reversalItem);
            }
            //先存到内存里面
            sub.putExtra(ItemConstants.COMBINE_ITEM,list);
        }
    }

    /**
     *
     * @param sub
     * @param reversalsCB
     * @param canceled 已经退款数量
     */
    private void fillRefundFinish(MainReversal reversal,SubReversal sub,List<ReversalCombItemDTO> reversalsCB,Map<Long,Map<ItemSkuId,Integer>> canceled){
        List<CombineItemDTO> combines = CombineItemBuilder.parseCombineItemByStored(sub.getSubOrder().getItemSku());
        Map<Long,Integer> cancelMap =reversalsCB.stream().collect(Collectors.toMap(m->m.getSkuId(),m->m.getCancelQty()));
        //组合商品
        Map<ItemSkuId,Integer> qtysMaps = canceled.get(sub.getSubOrder().getOrderId());
        qtysMaps = qtysMaps == null ? new HashMap<>() : qtysMaps;
        Boolean finish = true;
        for(CombineItemDTO cbsku : combines){
            //已经退款数量
            Integer hasCanceledQty = qtysMaps.get(new ItemSkuId(cbsku.getItemId(), cbsku.getSkuId()));
            hasCanceledQty = hasCanceledQty == null ? 0 : hasCanceledQty;
            //总数
            Integer totalQty = cbsku.getJoinQty() * sub.getSubOrder().getOrderQty();
            //本次退款数量
            Integer cancelQty = cancelMap.get(cbsku.getSkuId());
            cancelQty = cancelQty == null ? 0 : cancelQty;
            if(hasCanceledQty + cancelQty != totalQty){
                finish = false;
            }
        }
        //如果所有组合商品数量都退款完成;则设置退款单数量
        if(finish){
            sub.setCancelQty(sub.getSubOrder().getOrderQty());
            Integer total = reversal.getCancelQty() == null ? 0 : reversal.getCancelQty();
            reversal.setCancelQty(total + sub.getSubOrder().getOrderQty());
        }
    }
    /**
     * @param combineSkus
     * @param response
     * @return
     */
    private Map<Long, List<CombineItemDTO>> fillResult(Map<Long, List<CombineSkuDTO>> combineSkus, Map<ItemSkuId, ItemSku> response) {
        Map<Long, List<CombineItemDTO>> resultMap = new HashMap<>();
        for (Long skuId : combineSkus.keySet()) {
            //对象转换
            List<CombineSkuDTO> list = combineSkus.get(skuId);
            for (CombineSkuDTO skuDTO : list) {
                ItemSku sku = response.get(new ItemSkuId(skuDTO.getItemId(), skuDTO.getId()));
                CombineItemDTO combineItemDTO = convert.defConvert(sku);
                combineItemDTO.setJoinQty(skuDTO.getPerNum());
                List<CombineItemDTO> combItemSkuList = resultMap.get(skuId);
                if (combItemSkuList == null) {
                    combItemSkuList = new ArrayList<>();
                    resultMap.put(skuId, combItemSkuList);
                }
                combItemSkuList.add(combineItemDTO);
            }
        }
        return resultMap;
    }
}
