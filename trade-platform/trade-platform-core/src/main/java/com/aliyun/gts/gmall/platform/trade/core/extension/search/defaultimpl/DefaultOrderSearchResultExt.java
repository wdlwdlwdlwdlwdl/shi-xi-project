package com.aliyun.gts.gmall.platform.trade.core.extension.search.defaultimpl;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.middleware.oss.GmallOssClient;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.SubOrderDTO;
import com.aliyun.gts.gmall.platform.trade.core.convertor.SearchOrderConverter;
import com.aliyun.gts.gmall.platform.trade.core.extension.search.OrderSearchResultExt;
import com.aliyun.gts.gmall.platform.trade.core.result.TradeBizResult;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.ListOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
public class DefaultOrderSearchResultExt implements OrderSearchResultExt {

    @Autowired
    SearchOrderConverter searchOrderConverter;

    @Autowired
    GmallOssClient publicGmallOssClient;

    @Override
    public TradeBizResult<PageInfo<MainOrderDTO>> processResult(ListOrder listOrder) {
        List<MainOrder> orderList = listOrder.getOrderList();
        orderList.sort(new Comparator<MainOrder>() {
            @Override
            public int compare(MainOrder o1, MainOrder o2) {
                return Long.compare(o2.getGmtCreate().getTime(), o1.getGmtCreate().getTime());
            }
        });
        List<MainOrderDTO> resultList = new ArrayList<>();
        for(MainOrder mainOrder : orderList){
            MainOrderDTO mainOrderDTO = searchOrderConverter.convertMainOrder(mainOrder);
            List<SubOrder> subOrders = mainOrder.getSubOrders();
            for (SubOrder subOrder : subOrders) {
                SubOrderDTO subDTO = searchOrderConverter.convertSubOrder(subOrder);
                subDTO.setItemPic(publicGmallOssClient.getFileHttpUrl(subDTO.getItemPic()));
                mainOrderDTO.getSubOrderList().add(subDTO);
            }
            resultList.add(mainOrderDTO);
        }
        return TradeBizResult.ok(new PageInfo((long)listOrder.getTotal(),Lists.newArrayList(resultList)));

    }

    @Override
    public TradeBizResult<PageInfo<MainOrderDTO>> processAdminResult(ListOrder listOrder) {
        List<MainOrder> orderList = listOrder.getOrderList();
        orderList.sort(new Comparator<MainOrder>() {
            @Override
            public int compare(MainOrder o1, MainOrder o2) {
                return Long.compare(o2.getGmtCreate().getTime(), o1.getGmtCreate().getTime());
            }
        });
        List<MainOrderDTO> resultList = new ArrayList<>();
        for(MainOrder mainOrder : orderList){
            MainOrderDTO mainOrderDTO = searchOrderConverter.convertMainOrder(mainOrder);
            List<SubOrder>  subOrders = mainOrder.getSubOrders();
            for(SubOrder subOrder : subOrders){
                SubOrderDTO subDTO = searchOrderConverter.convertSubOrder(subOrder);
                subDTO.setItemPic(publicGmallOssClient.getFileHttpUrl(subDTO.getItemPic()));
                mainOrderDTO.getSubOrderList().add(subDTO);
            }
            resultList.add(mainOrderDTO);

        }
        return TradeBizResult.ok(new PageInfo((long)listOrder.getTotal(),Lists.newArrayList(resultList)));

    }

    @Override
    public TradeBizResult<PageInfo<MainOrderDTO>> processCustResult(ListOrder listOrder) {
        List<MainOrder> orderList = listOrder.getOrderList();
        orderList.sort(new Comparator<MainOrder>() {
            @Override
            public int compare(MainOrder o1, MainOrder o2) {
                return Long.compare(o2.getGmtCreate().getTime(), o1.getGmtCreate().getTime());
            }
        });
        List<MainOrderDTO> resultList = new ArrayList<>();
        for(MainOrder mainOrder : orderList){
                MainOrderDTO mainOrderDTO = searchOrderConverter.convertMainOrder(mainOrder);
                List<SubOrder>  subOrders = mainOrder.getSubOrders();
                for(SubOrder subOrder : subOrders){
                    SubOrderDTO subDTO = searchOrderConverter.convertSubOrder(subOrder);
                    subDTO.setItemPic(publicGmallOssClient.getFileHttpUrl(subDTO.getItemPic()));
                    mainOrderDTO.getSubOrderList().add(subDTO);
                }
                resultList.add(mainOrderDTO);
        }
        return TradeBizResult.ok(new PageInfo((long)listOrder.getTotal(),Lists.newArrayList(resultList)));
    }

}
