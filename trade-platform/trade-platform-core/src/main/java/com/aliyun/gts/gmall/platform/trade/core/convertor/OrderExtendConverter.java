package com.aliyun.gts.gmall.platform.trade.core.convertor;

import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.extend.OrderExtendQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.extend.OrderExtendDTO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderExtendDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Mapper(componentModel = "spring")
public interface OrderExtendConverter {

    // ============ domain --> do ============

    default List<TcOrderExtendDO> toExtendList(Map<String, Map<String, String>> extendMap) {
        List<TcOrderExtendDO> list = new ArrayList<>();
        if (extendMap == null) {
            return list;
        }
        Date now = new Date();
        for (Entry<String, Map<String, String>> typeEn : extendMap.entrySet()) {
            for (Entry<String, String> kvEn : typeEn.getValue().entrySet()) {
                TcOrderExtendDO ext = new TcOrderExtendDO();
                ext.setExtendType(typeEn.getKey());
                ext.setExtendKey(kvEn.getKey());
                ext.setExtendName(kvEn.getKey());
                ext.setExtendValue(kvEn.getValue());
                ext.setGmtCreate(now);
                ext.setGmtModified(now);
                ext.setValid(1);
                list.add(ext);
            }
        }
        return list;
    }

    default void fillMainOrder(List<TcOrderExtendDO> list, MainOrder mainOrder) {
        if (list == null) {
            return;
        }
        for (TcOrderExtendDO ext : list) {
            ext.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
            ext.setOrderId(mainOrder.getPrimaryOrderId());
            ext.setCustId(mainOrder.getCustomer().getCustId());
        }
    }

    default void fillSubOrder(List<TcOrderExtendDO> list, SubOrder subOrder, MainOrder main) {
        if (list == null) {
            return;
        }
        for (TcOrderExtendDO ext : list) {
            ext.setPrimaryOrderId(subOrder.getPrimaryOrderId());
            ext.setOrderId(subOrder.getOrderId());
            ext.setCustId(main.getCustomer().getCustId());
        }
    }

    default void fillOrder(List<TcOrderExtendDO> list, TcOrderDO order) {
        if (list == null) {
            return;
        }
        for (TcOrderExtendDO ext : list) {
            ext.setPrimaryOrderId(order.getPrimaryOrderId());
            ext.setOrderId(order.getOrderId());
            ext.setCustId(order.getCustId());
        }
    }


    // ============ dto <--> do ============

    TcOrderExtendDO toTcOrderExtendDO(OrderExtendQueryRpcReq req);

    List<OrderExtendDTO> toOrderExtendDTOList(List<TcOrderExtendDO> list);
}
