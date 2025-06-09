package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.search;


import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.api.flow.dto.FlowNodeResult;
import com.aliyun.gts.gmall.framework.convert.MultiLangConverter;
import com.aliyun.gts.gmall.framework.i18n.LangText;
import com.aliyun.gts.gmall.framework.processengine.core.model.ProcessFlowNodeHandler;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.item.api.dto.output.item.SkuPropDTO;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.OrderDetailQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.EvaluationDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDetailDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.SubOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.SubOrderDetailDTO;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.convertor.SearchOrderConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.PayQueryService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcAsyncTaskDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcEvaluationDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderQueryOption;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Shop;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcAsyncTaskRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcEvaluationRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.UserRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class OrderDetailQueryHandlerNew implements ProcessFlowNodeHandler<OrderDetailQueryRpcReq, FlowNodeResult> {


    @Autowired
    private OrderQueryAbility orderQueryAbility;

    @Autowired
    private TcEvaluationRepository tcEvaluationRepository;

    @Autowired
    private SearchOrderConverter searchOrderConverter;

    @Autowired
    private PayQueryService payQueryService;

    @Autowired
    private TcAsyncTaskRepository tcAsyncTaskRepository;

    @Autowired
    protected MultiLangConverter multiLangConverter;

    @Autowired
    protected UserRepository userRepository;

    @Override
    public FlowNodeResult<List<MainOrderDetailDTO>> handleBiz(Map<String, Object> map, OrderDetailQueryRpcReq orderDetailQueryRpcReq) {
        OrderQueryOption opt = OrderQueryOption
                .builder()
                .includeReversalInfo(orderDetailQueryRpcReq.isIncludeReversalInfo())
                .includeExtends(orderDetailQueryRpcReq.isIncludeExtend())
                .build();


        List<MainOrderDetailDTO> detailDTOList=new ArrayList<>();
        for(Long primaryOrderId:orderDetailQueryRpcReq.getPrimaryOrderIdList()){
            detailDTOList.add(fillMainOrderDetailDTOList(orderDetailQueryRpcReq,opt,primaryOrderId));
        }


        return FlowNodeResult.ok(detailDTOList);
    }

    private MainOrderDetailDTO fillMainOrderDetailDTOList(OrderDetailQueryRpcReq orderDetailQueryRpcReq,OrderQueryOption opt,Long primaryOrderId) {

        //orderDetailQueryRpcReq.getPrimaryOrderId() 这边要改
        MainOrder mainOrder = orderDetailQueryRpcReq.isSeller() ?
                orderQueryAbility.getMainOrder(primaryOrderId , opt) :
                orderQueryAbility.getBoughtMainOrder(primaryOrderId, opt);
        if (Objects.isNull(mainOrder)) {
            throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
        }
        // 用户参数校验
        if (orderDetailQueryRpcReq.getCustId() != null) {
            if (!orderDetailQueryRpcReq.getCustId().equals(mainOrder.getCustomer().getCustId())) {
                throw new GmallException(OrderErrorCode.ORDER_USER_NOT_MATCH);
            }
        }
        if (orderDetailQueryRpcReq.getSellerId() != null) {
            if (!orderDetailQueryRpcReq.getSellerId().equals(mainOrder.getSeller().getSellerId())) {
                throw new GmallException(OrderErrorCode.ORDER_USER_NOT_MATCH);
            }
        }
        // 子单信息
        List<SubOrder> subOrders = mainOrder.getSubOrders();
        // 查询支付信息
        OrderPay orderPay = payQueryService.queryByOrder(mainOrder);

        // 合并订单和支付数据
        MainOrderDetailDTO detailDTO = searchOrderConverter.convertOrderDetailDTO(mainOrder, orderPay);
        List baseOrderDTOS = new ArrayList<>();
        for (SubOrder subOrder : subOrders) {
            SubOrderDetailDTO subOrderDTO = searchOrderConverter.convertSubDetailOrder(subOrder);
            subOrderDTO.setItemTitle(multiLangConverter.mText_to_str(multiLangConverter.to_multiLangText(subOrder.getItemSku().getItemTitle())));
            subOrderDTO.setSkuDesc(multiLangConverter.mText_to_str(multiLangConverter.to_multiLangText(subOrder.getItemSku().getSkuDesc())));
            subOrderDTO.setCanRefunds(subOrder.getOrderAttr().getCanRefunds());
            subOrderDTO.setCategoryName(subOrder.getItemFeature().getCategoryName());
            baseOrderDTOS.add(subOrderDTO);
        }
        detailDTO.setSubOrderList(baseOrderDTOS);
        detailDTO.setSubDetailOrderList(baseOrderDTOS);
        Shop shop = userRepository.getSellerShop(detailDTO.getSellerId());
        if(Objects.nonNull(shop)){
            detailDTO.setSellerLogo(shop.getLogoUrl());
            detailDTO.setSellerPhone(shop.getContactPhone());
        }

        // 订单定时任务
        if (CollectionUtils.isNotEmpty(orderDetailQueryRpcReq.getIncludeOrderTaskTypes())) {
            List<TcAsyncTaskDO> tasks = tcAsyncTaskRepository.queryByOrder(
                    detailDTO.getOrderId(),
                    orderDetailQueryRpcReq.getIncludeOrderTaskTypes()
            );
            detailDTO.setOrderTasks(searchOrderConverter.convertOrderTasks(tasks));
        }
        // 评论
        //orderDetailQueryRpcReq.getPrimaryOrderId() 这边要改
        List<TcEvaluationDO> list = tcEvaluationRepository.queryByPrimaryOrderId(primaryOrderId);
        for(SubOrderDTO subOrderDTO : detailDTO.getSubOrderList()){
            Optional<TcEvaluationDO> evaluation = list.stream()
                    .filter(p->p.getOrderId().equals(subOrderDTO.getOrderId()))
                    .findAny();
            if(Objects.isNull(subOrderDTO.getEvaluationDTO())){
                subOrderDTO.setEvaluationDTO(new EvaluationDTO());
            }
            evaluation.ifPresent(tcEvaluationDO -> BeanUtils.copyProperties(tcEvaluationDO, subOrderDTO.getEvaluationDTO()));
        }
        //增加 receiveId 字段
        detailDTO.setReceiverId(mainOrder.getReceiver().getReceiverId());

        return detailDTO;
    }





    public String convertSkuDesc(String skuDesc) {
        List<SkuPropDTO> list = JSON.parseArray(skuDesc, SkuPropDTO.class);
        StringBuilder sku = new StringBuilder();
        for(SkuPropDTO skuPropDTO:list){
            Optional<LangText> nameText = skuPropDTO.getName().getLangTextByLang(LocaleContextHolder.getLocale().getLanguage());
            String name = "";
            String value = "";
            if (nameText.isPresent()) {
                name = nameText.get().getValue();
            }

            Optional<LangText> valueText = skuPropDTO.getValue().getLangTextByLang(LocaleContextHolder.getLocale().getLanguage());
            if (nameText.isPresent()) {
                value = valueText.get().getValue();
            }
            sku.append(name).append(':').append(value).append(';');
        }
        if (sku.length() > 0) {
            sku.setLength(sku.length() - 1);
        }
        System.out.println(sku);
        return sku.toString();
    }

}
