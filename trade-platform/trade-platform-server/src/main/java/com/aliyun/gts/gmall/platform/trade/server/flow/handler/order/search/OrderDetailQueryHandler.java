package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.search;

import com.aliyun.gts.gmall.framework.api.flow.dto.FlowNodeResult;
import com.aliyun.gts.gmall.framework.convert.MultiLangConverter;
import com.aliyun.gts.gmall.framework.processengine.core.model.ProcessFlowNodeHandler;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.order.query.OrderDetailQueryRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.evaluation.EvaluationDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.MainOrderDetailDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.SubOrderDTO;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.order.result.SubOrderDetailDTO;
import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
import com.aliyun.gts.gmall.platform.trade.core.convertor.SearchOrderConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderConfigService;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.PayQueryService;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcAsyncTaskDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcEvaluationDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcLogisticsDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.MainOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderQueryOption;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.SubOrder;
import com.aliyun.gts.gmall.platform.trade.domain.entity.pay.OrderPay;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.SellerTradeConfig;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Shop;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcAsyncTaskRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcEvaluationRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcLogisticsRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.UserRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 订单详情查询
 * step 1
 * 查询订单的详细信息
 */
@Component
public class OrderDetailQueryHandler implements ProcessFlowNodeHandler<OrderDetailQueryRpcReq, FlowNodeResult> {

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

    @Autowired
    private OrderConfigService orderConfigService;

    @Autowired
    private TcLogisticsRepository tcLogisticsRepository;

    @Value("${trade.reversal.limit.day:14}")
    private Integer maxDay;

    private static final long ONE_DAY_MILLIS = 24 * 3600 * 1000;

    @Override
    public FlowNodeResult<MainOrderDetailDTO> handleBiz(Map<String, Object> map, OrderDetailQueryRpcReq orderDetailQueryRpcReq) {
        OrderQueryOption opt = OrderQueryOption
            .builder()
            .includeReversalInfo(orderDetailQueryRpcReq.isIncludeReversalInfo())
            .includeExtends(orderDetailQueryRpcReq.isIncludeExtend())
            .build();
        MainOrder mainOrder = orderDetailQueryRpcReq.isSeller() ?
            orderQueryAbility.getMainOrder(orderDetailQueryRpcReq.getPrimaryOrderId() , opt) :
            orderQueryAbility.getBoughtMainOrder(orderDetailQueryRpcReq.getPrimaryOrderId(), opt);
        if (Objects.isNull(mainOrder)) {
            throw new GmallException(OrderErrorCode.ORDER_NOT_EXISTS);
        }
        // 用户参数校验
        if (Objects.nonNull(orderDetailQueryRpcReq.getCustId())) {
            if (!orderDetailQueryRpcReq.getCustId().equals(mainOrder.getCustomer().getCustId())) {
                throw new GmallException(OrderErrorCode.ORDER_USER_NOT_MATCH);
            }
        }
        if (Objects.nonNull(orderDetailQueryRpcReq.getSellerId())) {
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
        TcLogisticsDO logisticsDO =tcLogisticsRepository.queryLogisticsByPrimaryId(mainOrder.getPrimaryOrderId(),null);
        if(logisticsDO!=null){
            detailDTO.setDeliveryCompanyName(logisticsDO.getCompanyName());
            detailDTO.setLogisticsId(logisticsDO.getLogisticsId());
        }
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
        //售后按钮展示  后续可做超时任务处理
        isApplyButton(mainOrder, detailDTO);
        // 评论
        List<TcEvaluationDO> list = tcEvaluationRepository.queryByPrimaryOrderId(orderDetailQueryRpcReq.getPrimaryOrderId());
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
        return FlowNodeResult.ok(detailDTO);
    }


    private void isApplyButton(MainOrder mainOrder, MainOrderDetailDTO detailDTO) {
        Date received = mainOrder.getOrderAttr().getConfirmReceiveTime();
        if (received != null) {
            //默认收货14天内可申请售后
            SellerTradeConfig config = orderConfigService.getSellerConfig(mainOrder.getSeller().getSellerId());
            long maxMillis;
            if (config != null) {
                maxMillis = config.getCreateReversalMaxDays() * ONE_DAY_MILLIS;
            } else {
                maxMillis = maxDay * ONE_DAY_MILLIS;
            }
            long currMillis = System.currentTimeMillis() - received.getTime();
            if (currMillis > maxMillis) {
                detailDTO.setApply(false);
            }
        }
    }

}
