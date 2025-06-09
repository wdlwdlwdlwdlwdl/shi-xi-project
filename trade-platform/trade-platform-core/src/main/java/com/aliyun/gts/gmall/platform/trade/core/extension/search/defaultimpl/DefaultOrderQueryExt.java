package com.aliyun.gts.gmall.platform.trade.core.extension.search.defaultimpl;

import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.convertor.OrderConverter;
import com.aliyun.gts.gmall.platform.trade.core.extension.search.OrderQueryExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ListOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.domain.wrapper.OrderQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.testng.collections.Lists;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DefaultOrderQueryExt implements OrderQueryExt {

    @Autowired
    TcOrderRepository tcOrderRepository;

    @Autowired
    OrderConverter orderConverter;

    @Autowired
    OrderQueryAbility orderQueryAbility;

    @Override
    public TradeBizResult<ListOrder> query(OrderQueryWrapper queryWrapper) {
        queryWrapper.setPrimaryOrderFlag(true);
        // 总数
        Integer total = tcOrderRepository.countBoughtOrders(queryWrapper);
        ListOrder listOrder = new ListOrder();
        listOrder.setTotal(total);
        if(total == 0){
            return TradeBizResult.ok(listOrder);
        }
        // 分页查询主单
        List<TcOrderDO> list = tcOrderRepository.queryBoughtOrders(queryWrapper);
        List<Long> primaryOrderIds = list.stream().map(t-> t.getPrimaryOrderId()).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(primaryOrderIds)){
            return TradeBizResult.ok(listOrder);
        }
        // 用主订单号再来一次 查询主单+子单  一单最多50个商品 因此最多500条
        OrderQueryWrapper query = new OrderQueryWrapper();
        query.setCustId(queryWrapper.getCustId());
        query.setPrimaryOrderIds(primaryOrderIds);
        query.setPageSize(500);
        list = tcOrderRepository.queryBoughtOrders(query);
        if(CollectionUtils.isEmpty(list)){
            return TradeBizResult.ok(listOrder);
        }
        Map<Long , MainOrder> mainMap = new LinkedHashMap<>();
        // 去所有的子单
        List<SubOrder> subOrders = new ArrayList<>();
        for(TcOrderDO order : list){
            Integer flag = order.getPrimaryOrderFlag();
            if(flag == 0){
                SubOrder subOrder = orderConverter.convertSubOrder(order);
                subOrders.add(subOrder);
            }else{
                MainOrder mainOrder = orderConverter.convertMainOrder(order);
                mainMap.put(mainOrder.getPrimaryOrderId() , mainOrder);
            }
        }
        // 主单 子单合并
        for(SubOrder subOrder : subOrders){
            Long primaryOrderId = subOrder.getPrimaryOrderId();
            MainOrder mainOrder = mainMap.get(primaryOrderId);
            mainOrder.getSubOrders().add(subOrder);
        }
        List<MainOrder> mainList = Lists.newArrayList(mainMap.values());
        // 分步订单计算
        orderQueryAbility.fillStepOrders(mainList);
        listOrder.setOrderList(mainList);
        return TradeBizResult.ok(listOrder);
    }
}
