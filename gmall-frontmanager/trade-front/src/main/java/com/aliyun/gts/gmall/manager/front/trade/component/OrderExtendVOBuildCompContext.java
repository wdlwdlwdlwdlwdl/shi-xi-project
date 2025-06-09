package com.aliyun.gts.gmall.manager.front.trade.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import com.aliyun.gts.gmall.framework.convert.MultiLangConverter;
import com.aliyun.gts.gmall.manager.front.common.exception.FrontManagerException;
import com.aliyun.gts.gmall.manager.front.trade.annotation.VoOrderType;
import com.aliyun.gts.gmall.manager.front.trade.dto.input.query.ConfirmOrderRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.Result;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.OrderExtendContainerVO;
import com.aliyun.gts.gmall.manager.front.trade.dto.output.order.extend.OrderExtendVO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.ConfirmOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.confirm.OrderGroupDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDetailDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.SubOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.SubOrderDetailDTO;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import static com.aliyun.gts.gmall.manager.front.trade.dto.utils.TradeFrontResponseCode.NO_HANDLER_ITEM_TYPE;
import static com.aliyun.gts.gmall.manager.front.trade.dto.utils.TradeFrontResponseCode.ORDER_CONFIRM_NO_ITEM;

@Configuration
public class OrderExtendVOBuildCompContext {

    @Autowired
    private List<OrderExtendVOBuildComponent> components;

    @Autowired
    protected MultiLangConverter multiLangConverter;

    final Map<Integer,OrderExtendVOBuildComponent> map = new HashMap<>();

    @PostConstruct
    public void init(){
        for (OrderExtendVOBuildComponent component : components) {
            VoOrderType tag = component.getClass().getAnnotation(VoOrderType.class);
            if (tag != null) {
                map.put(tag.value(),component);
            }
        }
    }

    public OrderExtendVO buildExtend(SubOrderDTO subOrderDTO , MainOrderDetailDTO mainOrderDetailDTO){
        OrderExtendVOBuildComponent component = map.get(mainOrderDetailDTO.getOrderType());
        if(component == null){
            throw new FrontManagerException(NO_HANDLER_ITEM_TYPE);
        }
        return component.buildExtend(subOrderDTO , mainOrderDetailDTO);
    }

    /**
     * 订单详情对象转换
     * @param mainOrderDetailDTO
     * @return
     */
    public OrderExtendContainerVO buildExtend(MainOrderDetailDTO mainOrderDetailDTO){
        OrderExtendVOBuildComponent component = map.get(mainOrderDetailDTO.getOrderType());
        if(component == null){
            throw new FrontManagerException(NO_HANDLER_ITEM_TYPE);
        }
        OrderExtendContainerVO orderExtendContainerVO = new OrderExtendContainerVO();
        OrderExtendVO mainExtendVO = component.buildExtend(mainOrderDetailDTO);
        orderExtendContainerVO.setMainOrderExtend(mainExtendVO);
        Map<Long , OrderExtendVO> subExtendMap = new HashMap<>();
        for(SubOrderDetailDTO subOrderDTO : mainOrderDetailDTO.getSubDetailOrderList()){
            OrderExtendVO subExtendVO = component.buildExtend(subOrderDTO , mainOrderDetailDTO);
            subExtendMap.put(subOrderDTO.getOrderId(), subExtendVO);
        }
        orderExtendContainerVO.setSubOrderExtendMap(subExtendMap);
        return orderExtendContainerVO;
    }

    /**
     * 订单确认页扩展信息：  订单的公共信息  比如收件人信息   电子凭证信息
     * @param confirmOrderDTO
     * @param query
     * @return
     */
    public OrderExtendContainerVO buildExtend(ConfirmOrderDTO confirmOrderDTO,ConfirmOrderRestQuery query){
        Integer orderType = confirmOrderDTO.getOrderGroups().get(0).getOrderType();
        OrderExtendVOBuildComponent component = map.get(orderType);
        if(component == null){
            throw new FrontManagerException(NO_HANDLER_ITEM_TYPE);
        }
        Result<OrderExtendVO> result = component.buildExtend(query,confirmOrderDTO);
        OrderExtendContainerVO orderExtendContainerVO = new OrderExtendContainerVO();
        orderExtendContainerVO.setMainOrderExtend(result.getData());
        if(!result.isSuccess()){
            orderExtendContainerVO.setSuccess(false);
            orderExtendContainerVO.setErrorMsg(result.getErrorMsg());
        }
        return orderExtendContainerVO;
    }

    public Map<Long, OrderExtendContainerVO> buildExtendList(List<MainOrderDTO> orderList) {
        if (CollectionUtils.isEmpty(orderList)) {
            return new HashMap<>();
        }
        Multimap<Integer, MainOrderDTO> typeMap = HashMultimap.create();
        for (MainOrderDTO order : orderList) {
            typeMap.put(order.getOrderType(), order);
            for(SubOrderDTO subOrderDTO : order.getSubOrderList()){
                String title = subOrderDTO.getItemTitle();
                String skuDesc = subOrderDTO.getSkuDesc();
                subOrderDTO.setItemTitle(multiLangConverter.mText_to_str(multiLangConverter.to_multiLangText(title)));
                subOrderDTO.setSkuDesc(multiLangConverter.mText_to_str(multiLangConverter.to_multiLangText(skuDesc)));
            }
        }
        Map<Long, OrderExtendContainerVO> resultMap = new HashMap<>();
        for (Integer orderType : typeMap.keySet()) {
            Collection<MainOrderDTO> typeList = typeMap.get(orderType);
            OrderExtendVOBuildComponent component = map.get(orderType);
            Map<Long, OrderExtendVO> extMap = component.buildExtendList(typeList);
            if (extMap != null) {
                for (Entry<Long, OrderExtendVO> en : extMap.entrySet()) {
                    OrderExtendContainerVO container = new OrderExtendContainerVO();
                    container.setMainOrderExtend(en.getValue());
                    resultMap.put(en.getKey(), container);
                }
            }
        }
        return resultMap;
    }

    /**
     * 扩展字数据格式化
     * @param confirmOrderDTO
     * @param query
     * @return
     */
    public List<OrderExtendContainerVO> buildGroupExtend(ConfirmOrderDTO confirmOrderDTO, ConfirmOrderRestQuery query) {
        List<OrderExtendContainerVO> list = new ArrayList<>();
        for (OrderGroupDTO orderGroupDTO : confirmOrderDTO.getOrderGroups()) {
            Integer orderType = orderGroupDTO.getOrderType();
            OrderExtendVOBuildComponent component = map.get(orderType);
            if(component == null){
                throw new FrontManagerException(NO_HANDLER_ITEM_TYPE);
            }
            Result<OrderExtendVO> result = component.buildExtend(query, confirmOrderDTO);
            OrderExtendContainerVO orderExtendContainerVO = new OrderExtendContainerVO();
            orderExtendContainerVO.setMainOrderExtend(result.getData());
            orderExtendContainerVO.setPrimaryOrderId(orderGroupDTO.getPrimaryOrderId());
            if (!result.isSuccess()) {
                orderExtendContainerVO.setSuccess(false);
                orderExtendContainerVO.setErrorMsg(result.getErrorMsg());
            }
            list.add(orderExtendContainerVO);
        }
        return list;
    }

}
