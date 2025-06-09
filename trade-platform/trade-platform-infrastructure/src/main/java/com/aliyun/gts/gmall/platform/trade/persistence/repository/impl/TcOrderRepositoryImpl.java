package com.aliyun.gts.gmall.platform.trade.persistence.repository.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.middleware.mq.esindex.OrderEsIndexMqClient;
import com.aliyun.gts.gmall.middleware.mq.esindex.entity.OrderDumpInfo;
import com.aliyun.gts.gmall.platform.promotion.api.facade.account.AccountBookWriteFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.admin.PromotionConfigFacade;
import com.aliyun.gts.gmall.platform.promotion.common.util.JsonUtils;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonConstant;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.reversal.ReversalRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.output.reversal.MainReversalDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.*;
import com.aliyun.gts.gmall.platform.trade.common.domain.KVDO;
import com.aliyun.gts.gmall.platform.trade.common.util.CartIdUtil;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderOperateFlowDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.SalesInfoDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderOperateFlowQuery;
import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatistics;
import com.aliyun.gts.gmall.platform.trade.domain.entity.reversal.MainReversal;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderOperateFlowRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcReversalFlowRepository;
import com.aliyun.gts.gmall.platform.trade.domain.wrapper.OrderQueryWrapper;
import com.aliyun.gts.gmall.platform.trade.domain.wrapper.OrderStatisticsWrapper;
import com.aliyun.gts.gmall.platform.trade.persistence.mapper.TcOrderMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nullable;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.BiConsumer;

@Component
@Slf4j
public class TcOrderRepositoryImpl implements TcOrderRepository {

    public static final Integer UPDATE = 2;

    public static final String DEFAULT_CURRENCY=  "KZT";

    @Value("${search.type:opensearch}")
    private String searchType;

    @Autowired(required = false)
    private OrderEsIndexMqClient orderEsIndexMqClient;

    @Autowired
    private TcOrderMapper tcOrderMapper;
    @Autowired
    private TcOrderOperateFlowRepository tcOrderOperateFlowRepository;
    @Autowired
    private TcReversalFlowRepository tcReversalFlowRepository;
    @Autowired
    private AccountBookWriteFacade accountBookWriteFacade;
    @Autowired
    private PromotionConfigFacade promotionConfigFacade;

    @Override
    public void create(TcOrderDO order) {
        Date now = new Date();
        order.setGmtCreate(now);
        order.setGmtModified(now);
        order.setVersion(1L);
        // 设置是否已评论默认值
        order.setEvaluate(OrderEvaluateEnum.NOT_EVALUATE.getCode());
        tcOrderMapper.insert(order);
    }

    @Override
    public void batchCreate(List<TcOrderDO> orders) {
        String cartId = "";
        if (Objects.nonNull(orders.get(0)) && Objects.nonNull(orders.get(0).getCustId())) {
            cartId = CartIdUtil.convert(Long.toString(orders.get(0).getCustId()));
        }
        Date now = new Date();
        for (TcOrderDO order : orders) {
            order.setGmtCreate(now);
            order.setGmtModified(now);
            order.setVersion(1L);
            order.setPayCartId(cartId);
            // 设置是否已评论默认值
            order.setEvaluate(OrderEvaluateEnum.NOT_EVALUATE.getCode());
        }
        tcOrderMapper.batchCreate(orders);
    }

    @Override
    public TcOrderDO queryPrimaryByOrderId(Long primaryOrderId) {
        LambdaQueryWrapper<TcOrderDO> q = Wrappers.lambdaQuery();
        return tcOrderMapper.selectOne(q
            .eq(TcOrderDO::getPrimaryOrderId, primaryOrderId)
            .eq(TcOrderDO::getPrimaryOrderFlag, PrimaryOrderFlagEnum.PRIMARY_ORDER.getCode()));
    }

    @Override
    public TcOrderDO querySubByOrderId(Long primaryOrderId, Long orderId) {
        LambdaQueryWrapper<TcOrderDO> q = Wrappers.lambdaQuery();
        return tcOrderMapper.selectOne(q
            .eq(TcOrderDO::getPrimaryOrderId, primaryOrderId)
            .eq(TcOrderDO::getOrderId, orderId)
            .eq(TcOrderDO::getPrimaryOrderFlag, PrimaryOrderFlagEnum.SUB_ORDER.getCode()));
    }

    @Override
    public List<TcOrderDO> queryOrdersByPrimaryId(Long primaryOrderId) {
        LambdaQueryWrapper<TcOrderDO> q = Wrappers.lambdaQuery();
        return tcOrderMapper.selectList(q.eq(TcOrderDO::getPrimaryOrderId, primaryOrderId));
    }

    @Override
    public List<TcOrderDO> queryOrdersByPrimaryIds(List<Long> primaryOrderIds) {
        LambdaQueryWrapper<TcOrderDO> q = Wrappers.lambdaQuery();
        return tcOrderMapper.selectList(q.in(TcOrderDO::getPrimaryOrderId, primaryOrderIds));
    }

    /**
     * 更新主订单和子订单状态、stage
     * checkStatus、stage 可空
     */
    @Override
    public void updateStatusAndStageByPrimaryId(Long primaryOrderId, Integer status, Integer stage, Integer checkStatus) {
        updateStatusAndStageByPrimaryId(primaryOrderId, status, stage, checkStatus, null);
    }

    @Override
    @Transactional
    public void updateStatusAndStageByPrimaryId(
        Long primaryOrderId,
        Integer status,
        Integer stage,
        Integer checkStatus,
        BiConsumer<TcOrderDO, TcOrderDO> processUpdateValue){

        if (primaryOrderId == null) {
            throw new GmallException(OrderErrorCode.ILLEGAL_ARGS);
        }
        List<TcOrderDO> orderList = queryOrdersByPrimaryId(primaryOrderId);

        boolean isToPay = OrderStatusEnum.CREATED.getCode().equals(checkStatus)&&
                OrderStatusEnum.WAITING_FOR_PAYMENT.getCode().equals(status);
        Date start2Pay = new Date();
        for(TcOrderDO tcOrderDO : orderList){
            LambdaUpdateWrapper<TcOrderDO> wrapper = Wrappers.lambdaUpdate();
            wrapper.eq(TcOrderDO::getPrimaryOrderId , primaryOrderId);
            TcOrderDO newTcOrderDO = new TcOrderDO();
            newTcOrderDO.setPrimaryOrderStatus(status);
            newTcOrderDO.setGmtModified(new Date());
            //wrapper.eq(TcOrderDO::getVersion, tcOrderDO.getVersion());

            boolean ignoreStatus = false;
            if(tcOrderDO.getPrimaryOrderFlag() == 1){
                newTcOrderDO.setOrderStatus(status);
                wrapper.eq(TcOrderDO::getOrderId, primaryOrderId);
            }else{
                wrapper.eq(TcOrderDO::getOrderId, tcOrderDO.getOrderId());
               /* if (OrderStatusEnum.isReversal(tcOrderDO.getOrderStatus())) {
                    //售后相关的状态, 不更新子订单状态
                    ignoreStatus = true;
                }else {
                    newTcOrderDO.setOrderStatus(status);
                }*/
                newTcOrderDO.setOrderStatus(status);
            }
            if(!ignoreStatus && checkStatus != null) {
                wrapper.eq(TcOrderDO::getOrderStatus, checkStatus);
            }
            if (stage != null && !ignoreStatus) {
                OrderAttrDO orderAttr = tcOrderDO.getOrderAttr();
                if (orderAttr == null) {
                    orderAttr = new OrderAttrDO();
                }
                orderAttr.setOrderStage(stage);
                newTcOrderDO.setOrderAttr(orderAttr);
            }
            if (processUpdateValue != null) {
                processUpdateValue.accept(tcOrderDO, newTcOrderDO);
            }
            if (OrderEvaluateEnum.NOT_EVALUATE.getCode().equals(newTcOrderDO.getEvaluate())) {
                newTcOrderDO.setEvaluate(null);
            }
            if(isToPay)
            {
                newTcOrderDO.setToPayDate(start2Pay);
            }
            int update = tcOrderMapper.update(newTcOrderDO, wrapper);
            log.info("updateByOrderIdVersion:{},update:{}", newTcOrderDO, update);
            if (update != 1) {
                throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
            }

            //添加发货地址信息
            newTcOrderDO.setSalesInfo(tcOrderDO.getSalesInfo());

            // 订单状态同步过后
            if (!CommonConstant.OPENSEARCH.equals(searchType)) {
                OrderDumpInfo orderDumpInfo = OrderDumpInfo.of(tcOrderDO.getOrderId(), UPDATE);
                Map<String, String> orderParam = orderDumpInfo.getOrderParam();
                Map<String, String> orderMap = getUpdateOrderMap(newTcOrderDO);
                orderParam.putAll(orderMap);
                orderParam.put("primaryOrderId", String.valueOf(primaryOrderId));
                orderEsIndexMqClient.push(orderDumpInfo);
            }
        }
        // 订单完成的MQ消息 会处理 这里不会在调用
        //if(OrderStatusEnum.COMPLETED.getCode().equals(status)){
        //    grantAssets(primaryOrderId);
        //}
    }


    /**
     * 订单支付成功后调用，为了并保存支付的cartID，并更新订单状态
     * @param primaryOrderId
     * @param status
     * @param stage
     * @param checkStatus
     * @param cartId 支付的cartID
     */
    @Override
    @Transactional
    public void updateStatusAndStageAndCartIdByPrimaryId(
        Long primaryOrderId, Integer status, Integer stage,
        Integer checkStatus, String cartId){

        if (primaryOrderId == null) {
            throw new GmallException(OrderErrorCode.ILLEGAL_ARGS);
        }
        boolean isPayed = OrderStatusEnum.PAYMENT_CONFIRMED.getCode().equals(status)||OrderStatusEnum.PARTIALLY_PAID.getCode().equals(status);
        Date isPayedDate = new Date();
        List<TcOrderDO> orderList = queryOrdersByPrimaryId(primaryOrderId);
        for(TcOrderDO tcOrderDO : orderList){
            LambdaUpdateWrapper<TcOrderDO> wrapper = Wrappers.lambdaUpdate();
            wrapper.eq(TcOrderDO::getPrimaryOrderId , primaryOrderId);
            TcOrderDO newTcOrderDO = new TcOrderDO();
            newTcOrderDO.setPrimaryOrderStatus(status);
            newTcOrderDO.setGmtModified(new Date());
            //wrapper.eq(TcOrderDO::getVersion, tcOrderDO.getVersion());
            boolean ignoreStatus = false;
            if(tcOrderDO.getPrimaryOrderFlag() == 1){
                newTcOrderDO.setOrderStatus(status);
                wrapper.eq(TcOrderDO::getOrderId, primaryOrderId);
            }else{
                wrapper.eq(TcOrderDO::getOrderId, tcOrderDO.getOrderId());
               /* if (OrderStatusEnum.isReversal(tcOrderDO.getOrderStatus())) {
                    //售后相关的状态, 不更新子订单状态
                    ignoreStatus = true;
                }else {
                    newTcOrderDO.setOrderStatus(status);
                }*/
                newTcOrderDO.setOrderStatus(status);
            }
            if(!ignoreStatus && checkStatus != null) {
                wrapper.eq(TcOrderDO::getOrderStatus, checkStatus);
            }
            if (stage != null && !ignoreStatus) {
                OrderAttrDO orderAttr = tcOrderDO.getOrderAttr();
                if (orderAttr == null) {
                    orderAttr = new OrderAttrDO();
                }
                orderAttr.setOrderStage(stage);
                newTcOrderDO.setOrderAttr(orderAttr);
            }
            if (StringUtils.isNotBlank(cartId)) {
                newTcOrderDO.setPayCartId(cartId);
            }
            if (OrderEvaluateEnum.NOT_EVALUATE.getCode().equals(newTcOrderDO.getEvaluate())) {
                newTcOrderDO.setEvaluate(null);
            }
            if(isPayed)
            {
                newTcOrderDO.setPaymentDate(isPayedDate);
                //支付货币之支持KZT
                newTcOrderDO.setCurrency(DEFAULT_CURRENCY);
                newTcOrderDO.setPayStatus(PayStatusEnum.PAY_PAID.getCode());
            }
            int update = tcOrderMapper.update(newTcOrderDO, wrapper);
            log.info("updateByOrderIdVersion:{},update:{}", newTcOrderDO, update);
            if (update != 1) {
                throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
            }
            newTcOrderDO.setSalesInfo(tcOrderDO.getSalesInfo());
            // 订单状态同步过后
            if (!CommonConstant.OPENSEARCH.equals(searchType)) {
                OrderDumpInfo orderDumpInfo = OrderDumpInfo.of(tcOrderDO.getOrderId(), UPDATE);
                Map<String, String> orderParam = orderDumpInfo.getOrderParam();
                Map<String, String> orderMap = getUpdateOrderMap(newTcOrderDO);
                orderParam.putAll(orderMap);
                orderParam.put("primaryOrderId", String.valueOf(primaryOrderId));
                orderEsIndexMqClient.push(orderDumpInfo);
            }
        }
        // 订单完成的MQ消息 会处理 这里不会在调用
        //if(OrderStatusEnum.COMPLETED.getCode().equals(status)){
        //    grantAssets(primaryOrderId);
        //}
    }


    /**
     * 订单支付成功后调用，为了并保存支付的cartID，并更新订单状态
     * @param primaryOrderIds
     * @param cartId 支付的cartID
     */
    @Override
    @Transactional
    public void updateCartIdByPrimaryIds(List<Long> primaryOrderIds, String cartId){
        if (CollectionUtils.isEmpty(primaryOrderIds) || StringUtils.isBlank(cartId)) {
            throw new GmallException(OrderErrorCode.ILLEGAL_ARGS);
        }
        List<TcOrderDO> orderList = queryOrdersByPrimaryIds(primaryOrderIds);
        for(TcOrderDO tcOrderDO : orderList){
            Long primaryOrderId = tcOrderDO.getPrimaryOrderId();
            LambdaUpdateWrapper<TcOrderDO> wrapper = Wrappers.lambdaUpdate();
            wrapper.eq(TcOrderDO::getPrimaryOrderId , primaryOrderId);
            TcOrderDO newTcOrderDO = new TcOrderDO();
            newTcOrderDO.setGmtModified(new Date());

            boolean ignoreStatus = false;
            if(tcOrderDO.getPrimaryOrderFlag() == 1){
                wrapper.eq(TcOrderDO::getOrderId, primaryOrderId);
            }else{
                wrapper.eq(TcOrderDO::getOrderId, tcOrderDO.getOrderId());
            }

            if (StringUtils.isNotBlank(cartId)) {
                newTcOrderDO.setPayCartId(cartId);
            }

            int update = tcOrderMapper.update(newTcOrderDO, wrapper);
            log.info("updateByOrderIdVersion:{},update:{}", newTcOrderDO, update);
            if (update != 1) {
                throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
            }
        }
    }

    /**
     * 根据cartID 更新订单号
     * @param primaryOrderIds
     * @param payCartId
     * @param bankCardNbr
     * 2025-3-21 20:09:25
     */
    @Override
    public void updateCartIdAndBankIdByPrimaryIds(List<Long> primaryOrderIds, String payCartId, String bankCardNbr) {
        if (CollectionUtils.isEmpty(primaryOrderIds) || StringUtils.isBlank(payCartId)) {
            throw new GmallException(OrderErrorCode.ILLEGAL_ARGS);
        }
        // 通过主单号查询订单
        List<TcOrderDO> orderList = queryOrdersByPrimaryIds(primaryOrderIds);
        for(TcOrderDO tcOrderDO : orderList){
            Long primaryOrderId = tcOrderDO.getPrimaryOrderId();
            LambdaUpdateWrapper<TcOrderDO> wrapper = Wrappers.lambdaUpdate();
            wrapper.eq(TcOrderDO::getPrimaryOrderId , primaryOrderId);
            TcOrderDO newTcOrderDO = new TcOrderDO();
            newTcOrderDO.setGmtModified(new Date());
            if(tcOrderDO.getPrimaryOrderFlag() == 1){
                wrapper.eq(TcOrderDO::getOrderId, primaryOrderId);
            }else{
                wrapper.eq(TcOrderDO::getOrderId, tcOrderDO.getOrderId());
            }
            if (StringUtils.isNotBlank(payCartId)) {
                String originalPayCardId = tcOrderDO.getPayCartId();
                OrderAttrDO orderAttr = tcOrderDO.getOrderAttr();
                if (Objects.nonNull(orderAttr) && OrderTypeEnum.MULTI_STEP_ORDER.getCode().equals(orderAttr.getOrderType()) &&
                        Objects.nonNull(orderAttr.getCurrentStepNo()) && orderAttr.getCurrentStepNo() == 2 &&
                        StringUtils.isNotBlank(originalPayCardId)) {
                    String firstPayCardId= originalPayCardId.split("\\|")[0];
                    newTcOrderDO.setPayCartId(firstPayCardId + "|" + payCartId);
                }
                else {
                    newTcOrderDO.setPayCartId(payCartId);
                }
            }

            if (StringUtils.isNotBlank(bankCardNbr)) {
                OrderAttrDO orderAttr = tcOrderDO.getOrderAttr();
                if (orderAttr == null) {
                    orderAttr = new OrderAttrDO();
                    orderAttr.setBankCardNbr(bankCardNbr);
                } else {
                    String originalBank = orderAttr.getBankCardNbr();
                    if (OrderTypeEnum.MULTI_STEP_ORDER.getCode().equals(orderAttr.getOrderType()) &&
                        Objects.nonNull(orderAttr.getCurrentStepNo()) &&
                        orderAttr.getCurrentStepNo() == 2 &&
                        StringUtils.isNotBlank(originalBank)) {
                        String firstBank = originalBank.split("\\|")[0];
                        orderAttr.setBankCardNbr(firstBank + "|" + bankCardNbr);
                    }
                    else {
                        orderAttr.setBankCardNbr(bankCardNbr);
                    }
                }
                newTcOrderDO.setOrderAttr(orderAttr);
            }
            int update = tcOrderMapper.update(newTcOrderDO, wrapper);
            log.info("updateByOrderIdVersion:{},update:{}", newTcOrderDO, update);
            if (update != 1) {
                throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
            }
        }
    }

    @Override
    public void reversalStatusSynOrder(Long primaryOrderId, Integer checkStatus, Integer status){
        if (primaryOrderId == null) {
            throw new GmallException(OrderErrorCode.ILLEGAL_ARGS);
        }
        List<TcOrderDO> orderList = queryOrdersByPrimaryId(primaryOrderId);
        for(TcOrderDO tcOrderDO : orderList){
            LambdaUpdateWrapper<TcOrderDO> wrapper = Wrappers.lambdaUpdate();
            wrapper.eq(TcOrderDO::getPrimaryOrderId , primaryOrderId);
            TcOrderDO newTcOrderDO = new TcOrderDO();
            newTcOrderDO.setPrimaryOrderStatus(status);
            newTcOrderDO.setOrderStatus(status);
            newTcOrderDO.setGmtModified(new Date());
            wrapper.eq(TcOrderDO::getOrderId, tcOrderDO.getOrderId());
            int update = tcOrderMapper.update(newTcOrderDO, wrapper);
            log.info("updateByOrderIdVersion:{},update:{}", newTcOrderDO, update);
            //操作状态添加
            if(tcOrderDO.getPrimaryOrderFlag() == 1){
                addFlow(tcOrderDO,status,checkStatus,false);
            }

            // 订单状态同步过后
            if (!CommonConstant.OPENSEARCH.equals(searchType)) {
                OrderDumpInfo orderDumpInfo = OrderDumpInfo.of(tcOrderDO.getOrderId(), UPDATE);
                Map<String, String> orderParam = orderDumpInfo.getOrderParam();
                Map<String, String> orderMap = getUpdateOrderMap(newTcOrderDO);
                orderDumpInfo.setId(tcOrderDO.getOrderId());
                orderParam.putAll(orderMap);
                orderParam.put("primaryOrderId", String.valueOf(primaryOrderId));
                log.info("push es req={}", JsonUtils.toJSONString(orderDumpInfo));
                orderEsIndexMqClient.push(orderDumpInfo);
            }
        }
    }

    @Override
    public void reversalStatusSynOrder(MainReversal reversal, Long primaryOrderId, Integer checkStatus, Integer status) {
        log.info("reversalStatusSynOrder start primaryOrderId={},checkStatus={},status={}",primaryOrderId,checkStatus,status);
        if (primaryOrderId == null) {
            throw new GmallException(OrderErrorCode.ILLEGAL_ARGS);
        }
        List<Long> orderIds = reversal.getSubReversals().stream().map(p->p.getSubOrder().getOrderId()).toList();
        List<TcOrderDO> orderList = queryOrdersByPrimaryId(primaryOrderId);
        //退款成功之后，判断是否是部分退
        if(ReversalStatusEnum.REFUND_FULL_SUCCESS.getCode().equals(status)){
            TcOrderDO temp = orderList.stream().filter(p->!orderIds.contains(p.getOrderId()))
                .filter(p->p.getPrimaryOrderFlag()!=1)
                .filter(p->(!p.getOrderStatus().equals(ReversalStatusEnum.REFUND_PART_SUCCESS.getCode()))
                        &&!p.getOrderStatus().equals(ReversalStatusEnum.REFUND_FULL_SUCCESS.getCode()))
                .findFirst()
                .orElse(null);
            if(temp != null){
                status = ReversalStatusEnum.REFUND_PART_SUCCESS.getCode();
            }
        }
        for(TcOrderDO tcOrderDO : orderList){
            LambdaUpdateWrapper<TcOrderDO> wrapper = Wrappers.lambdaUpdate();
            wrapper.eq(TcOrderDO::getPrimaryOrderId , primaryOrderId);
            TcOrderDO newTcOrderDO = new TcOrderDO();
            newTcOrderDO.setPrimaryOrderStatus(status);
            newTcOrderDO.setGmtModified(new Date());
            //wrapper.eq(TcOrderDO::getVersion, tcOrderDO.getVersion());
            boolean ignoreStatus = false;
            if(tcOrderDO.getPrimaryOrderFlag() == 1){
                newTcOrderDO.setOrderStatus(status);
                wrapper.eq(TcOrderDO::getOrderId, primaryOrderId);
                //订单操作状态流程添加
                addFlow(tcOrderDO,status,checkStatus,false);
            }else if(orderIds.contains(tcOrderDO.getOrderId())){
                //子单不存在部分退成功
                wrapper.eq(TcOrderDO::getOrderId, tcOrderDO.getOrderId());
                newTcOrderDO.setOrderStatus(status);
            }else {
                continue;
            }
            int update = tcOrderMapper.update(newTcOrderDO, wrapper);
            log.info("updateByOrderIdVersion:{},update:{}", newTcOrderDO, update);
            log.info("searchType = {}",searchType);

            // 订单状态同步过后
            if (!CommonConstant.OPENSEARCH.equals(searchType)) {
                //更新ES售后单状态
                OrderDumpInfo orderReversalDumpInfo = OrderDumpInfo.of(reversal.getPrimaryReversalId(), UPDATE);
                Map<String, String> orderReversalParam = orderReversalDumpInfo.getOrderParam();
                Map<String, String> orderReversalMap = getUpdateOrderMap(newTcOrderDO);
                if(ReversalStatusEnum.REFUND_FULL_SUCCESS.getCode().equals(status) ||
                    ReversalStatusEnum.REFUND_PART_SUCCESS.getCode().equals(status)){
                    orderReversalMap.put("reversalCompletedTime", JSONObject.toJSONString(newTcOrderDO.getGmtModified()));
                }
                orderReversalMap.put("orderStatus", String.valueOf(status));
                orderReversalMap.put("reversalStatus",String.valueOf(status));
                orderReversalParam.putAll(orderReversalMap);
                orderReversalParam.put("primaryOrderId", String.valueOf(primaryOrderId));
                log.info("Push Reversal ES req = {}",orderReversalDumpInfo);
                orderEsIndexMqClient.push(orderReversalDumpInfo);

                //更新ES订单状态
                OrderDumpInfo orderDumpInfo = OrderDumpInfo.of(tcOrderDO.getOrderId(),UPDATE);
                Map<String, String> orderParam = orderDumpInfo.getOrderParam();
                Map<String, String> orderMap = getUpdateOrderMap(newTcOrderDO);
                orderMap.put("orderStatus", String.valueOf(status));
                orderParam.putAll(orderMap);
                log.info("Push Order ES req = {}",orderDumpInfo);
                orderEsIndexMqClient.push(orderDumpInfo);

            }
        }
    }

    /**
     * 写流水
     * @param mainOrder
     * @param status
     * @param checkStatus
     * @param isCustomer
     */
    protected void addFlow(TcOrderDO mainOrder, Integer status, Integer checkStatus,  boolean isCustomer) {
        OrderOperateFlowQuery query = new OrderOperateFlowQuery();
        query.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
        query.setFromOrderStatus(checkStatus);
        query.setToOrderStatus(status);
        if(!tcOrderOperateFlowRepository.exist(query)) {
            TcOrderOperateFlowDO flow = new TcOrderOperateFlowDO();
            flow.setPrimaryOrderId(mainOrder.getPrimaryOrderId());
            flow.setOrderId(mainOrder.getOrderId());
            flow.setFromOrderStatus(checkStatus);
            flow.setToOrderStatus(status);
            flow.setOperatorType(isCustomer ? 1 : 0);
            flow.setGmtCreate(new Date());
            flow.setOpName(OrderStatusEnum.codeOf(status).getInner());
            flow.setOperator(String.valueOf(mainOrder.getCustId()));
            flow.setGmtModified(new Date());
            tcOrderOperateFlowRepository.create(flow);
        }
    }

    private Map<String, String> getUpdateOrderMap(TcOrderDO newTcOrderDO) {
        Map<String, String> orderMap = new HashMap<>();
        // 支付成功以后更新状态
        orderMap.put("orderStatus", String.valueOf(newTcOrderDO.getOrderStatus()));
        orderMap.put("reversalStatus",String.valueOf(newTcOrderDO.getOrderStatus()));
        if (!OrderEvaluateEnum.NOT_EVALUATE.getCode().equals(newTcOrderDO.getEvaluate())) {
            orderMap.put("evaluate", String.valueOf(newTcOrderDO.getEvaluate()));
        }
        orderMap.put("gmtModified", JSONObject.toJSONString(newTcOrderDO.getGmtModified()));
        if(Objects.nonNull(newTcOrderDO.getToPayDate())) {
            orderMap.put("toPayDate", JSONObject.toJSONString(newTcOrderDO.getToPayDate()));
        }
        if(Objects.nonNull(newTcOrderDO.getPaymentDate())) {
            orderMap.put("paymentDate", JSONObject.toJSONString(newTcOrderDO.getPaymentDate()));
        }
        if(Objects.nonNull(newTcOrderDO.getCurrency())) {
            orderMap.put("currency", newTcOrderDO.getCurrency());
        }
        if(Objects.nonNull(newTcOrderDO.getPayStatus())) {
            orderMap.put("payStatus", String.valueOf(newTcOrderDO.getPayStatus()));
        }
        if (null != newTcOrderDO.getSalesInfo()){
            //添加发货人信息
            orderMap.put("dispatchCity", newTcOrderDO.getSalesInfo().getCity());
            orderMap.put("dispatchCityCode",newTcOrderDO.getSalesInfo().getCityCode());
            orderMap.put("merchantAddress",newTcOrderDO.getSalesInfo().getDeliveryAddr());
        }

        return orderMap;
    }

    @Override
    public int updateStatusByOrderId(Long primaryOrderId, Long orderId, Integer primaryStatus, @Nullable Integer status, @Nullable Integer checkStatus) {
        LambdaUpdateWrapper<TcOrderDO> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(TcOrderDO::getPrimaryOrderId , primaryOrderId);
        wrapper.eq(TcOrderDO::getOrderId , orderId);
        if (checkStatus != null) {
            wrapper.eq(TcOrderDO::getOrderStatus, checkStatus);
        }
        TcOrderDO tcOrderDO = new TcOrderDO();
        tcOrderDO.setEvaluate(null);
        tcOrderDO.setOrderStatus(status);
        tcOrderDO.setPrimaryOrderStatus(primaryStatus);
        tcOrderDO.setGmtModified(new Date());
        return tcOrderMapper.update(tcOrderDO , wrapper);
    }

    @Override
    public void updateByOrderId(TcOrderDO update) {
        if (update.getOrderId() == null || update.getPrimaryOrderId() == null) {
            throw new GmallException(CommonErrorCode.SERVER_ERROR);
        }
        if (OrderEvaluateEnum.NOT_EVALUATE.getCode().equals(update.getEvaluate())) {
            update.setEvaluate(null);
        }
        update.setGmtModified(new Date());
        LambdaUpdateWrapper<TcOrderDO> wrapper = Wrappers.lambdaUpdate();
        tcOrderMapper.update(
            update ,
            wrapper
            .eq(TcOrderDO::getOrderId , update.getOrderId())
            .eq(TcOrderDO::getPrimaryOrderId, update.getPrimaryOrderId())
        );
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void customerDeleteOrder(Long primaryId){
        List<TcOrderDO> orderList = queryOrdersByPrimaryId(primaryId);
        for(TcOrderDO tcOrderDO : orderList){
            LambdaUpdateWrapper<TcOrderDO> wrapper = Wrappers.lambdaUpdate();
            wrapper.eq(TcOrderDO::getPrimaryOrderId , tcOrderDO.getPrimaryOrderId());
            wrapper.eq(TcOrderDO::getOrderId , tcOrderDO.getOrderId());
            TcOrderDO newTcOrderDO = new TcOrderDO();
            newTcOrderDO.setCustDeleted(1);
            tcOrderMapper.update(newTcOrderDO , wrapper);
        }
    }

    @Override
    public List<KVDO<Integer , Integer>>  countByStatus(Long custId , List<Integer> status){
        return tcOrderMapper.countByStatus(custId , status);
    }

    @Override
    public List<TcOrderDO> queryBoughtOrders(OrderQueryWrapper query) {
        return tcOrderMapper.queryBoughtOrders(query);
    }

    @Override
    public List<TcOrderDO> queryBoughtDetailByPrimaryId(Long primaryOrderId) {
        LambdaQueryWrapper<TcOrderDO> tcOrderDOLambdaQueryWrapper = Wrappers.lambdaQuery();
        return tcOrderMapper.selectList(
            tcOrderDOLambdaQueryWrapper.eq(TcOrderDO::getPrimaryOrderId, primaryOrderId)
            .eq(TcOrderDO::getCustDeleted,0)
        );
    }

    @Override
    public Integer countBoughtOrders(OrderQueryWrapper query) {
        return tcOrderMapper.countBoughtOrders(query);
    }

    @Override
    public List<TcOrderDO> querySoldOrders(OrderQueryWrapper query) {
        return tcOrderMapper.querySoldOrders(query);
    }

    @Override
    public boolean updateByOrderIdVersion(TcOrderDO update) {
        Long oldVersion = update.getVersion();
        Long newVersion = oldVersion == null ? 1 : oldVersion + 1;
        update.setVersion(newVersion);
        update.setGmtModified(new Date());
        if (OrderEvaluateEnum.NOT_EVALUATE.getCode().equals(update.getEvaluate())) {
            update.setEvaluate(null);
        }
        LambdaUpdateWrapper<TcOrderDO> tcOrderDOLambdaUpdateWrapper = Wrappers.lambdaUpdate();
        tcOrderDOLambdaUpdateWrapper
                .eq(TcOrderDO::getOrderId, update.getOrderId())
        .eq(TcOrderDO::getPrimaryOrderId, update.getPrimaryOrderId());
        /*  if (oldVersion != null) {
            up.eq(TcOrderDO::getVersion, oldVersion);
        }*/
        log.info("updateByOrderIdVersion:{}", update);
        int c = tcOrderMapper.update(update, tcOrderDOLambdaUpdateWrapper);
        return c > 0;
    }

    @Override
    public List<OrderStatistics> statisticsBySellerIds(List<Long> sellerIds, int orderStatus) {
        return tcOrderMapper.statisticsBySellerId(sellerIds, orderStatus);
    }

    @Override
    public List<OrderStatistics> statisticsBySeller(List<Long> sellerIds) {
        return tcOrderMapper.statisticsBySeller(sellerIds);
    }

    @Override
    public List<MainReversalDTO> statisticsSellerByCancelCodeAndTime(ReversalRpcReq req) {
        OrderStatisticsWrapper wrapper = new OrderStatisticsWrapper();
        wrapper.setReasonCode(req.getReversalReasonCode());
        if(req.getCreateTime() != null){
            wrapper.setStartTime(new Timestamp(req.getCreateTime().getStartTime().getTime()));
            wrapper.setEndTime(new Timestamp(req.getCreateTime().getEndTime().getTime()));
        }
        return tcOrderMapper.statisticsSellerByCancelCodeAndTime(wrapper);
    }

    @Override
    public int statisticsOrderByCancelCodeAndTime(ReversalRpcReq req) {
        OrderStatisticsWrapper wrapper = getStatisticsWrapper(req);
        return tcOrderMapper.statisticsOrderByCancelCodeAndTime(wrapper);
    }

    private static @NotNull OrderStatisticsWrapper getStatisticsWrapper(ReversalRpcReq req) {
        OrderStatisticsWrapper wrapper = new OrderStatisticsWrapper();
        wrapper.setReasonCode(req.getReversalReasonCode());
        wrapper.setSellerId(req.getSellerId());
        if(req.getCreateTime()!=null){
            wrapper.setStartTime(new Timestamp(req.getCreateTime().getStartTime().getTime()));
            wrapper.setEndTime(new Timestamp(req.getCreateTime().getEndTime().getTime()));
        }
        return wrapper;
    }

    @Override
    public int statisticsOrderByTime(ReversalRpcReq req) {
        OrderStatisticsWrapper wrapper = getStatisticsWrapper(req);
        return tcOrderMapper.statisticsOrderByTime(wrapper);
    }


    /**
     * 根据主单删除临时结算单
     * @param primaryId
     * @return
     */
    @Override
    public void deleteCheckOrder(Long primaryId){
        List<TcOrderDO> orderList = queryOrdersByPrimaryId(primaryId);
        for(TcOrderDO tcOrderDO : orderList){
            LambdaUpdateWrapper<TcOrderDO> wrapper = Wrappers.lambdaUpdate();
            wrapper.eq(TcOrderDO::getPrimaryOrderId , tcOrderDO.getPrimaryOrderId());
            wrapper.eq(TcOrderDO::getOrderId , tcOrderDO.getOrderId());
            wrapper.eq(TcOrderDO::getOrderStatus , OrderStatusEnum.CREATED.getCode());
            tcOrderMapper.delete(wrapper);
        }
    }

    /**
     * 通过支付查询所有的主单号
     * @param payCartId
     * @return
     */
    @Override
    public List<TcOrderDO> queryMainOrderByCartId(String payCartId) {
        LambdaQueryWrapper<TcOrderDO> tcOrderDOLambdaQueryWrapper = Wrappers.lambdaQuery();
        return tcOrderMapper.selectList(
            tcOrderDOLambdaQueryWrapper
            .eq(TcOrderDO::getPayCartId, payCartId)
            .eq(TcOrderDO::getPrimaryOrderFlag, PrimaryOrderFlagEnum.PRIMARY_ORDER.getCode())
        );
    }
}
