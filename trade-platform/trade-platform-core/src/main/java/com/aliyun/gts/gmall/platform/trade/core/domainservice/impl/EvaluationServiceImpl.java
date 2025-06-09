package com.aliyun.gts.gmall.platform.trade.core.domainservice.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.framework.api.mq.dto.SearchMessage;
import com.aliyun.gts.gmall.framework.convert.MultiLangConverter;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.middleware.mq.esindex.entity.MessageDTO;
import com.aliyun.gts.gmall.platform.trade.api.constant.CommonErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.api.constant.TradeExtendKeyConstants;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationExtendModifyReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.input.evaluation.EvaluationRpcReq;
import com.aliyun.gts.gmall.platform.trade.api.dto.message.EvaluateMessageDTO;
import com.aliyun.gts.gmall.platform.trade.common.constants.EvaluationApproveStatusEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.OrderEvaluateEnum;
import com.aliyun.gts.gmall.platform.trade.common.constants.PrimaryOrderFlagEnum;
import com.aliyun.gts.gmall.platform.trade.core.convertor.EvaluationConverter;
import com.aliyun.gts.gmall.platform.trade.core.domainservice.EvaluationService;
import com.aliyun.gts.gmall.platform.trade.core.message.sender.MessageSendManager;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcEvaluationDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcOrderDO;
import com.aliyun.gts.gmall.platform.trade.domain.dataobject.inner.OrderAttrDO;
import com.aliyun.gts.gmall.platform.trade.domain.entity.user.Seller;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcEvaluationRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.TcOrderRepository;
import com.aliyun.gts.gmall.platform.trade.domain.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.testng.collections.Lists;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Configuration
@ConditionalOnProperty(prefix = "trade", name = "evaluationService", havingValue = "default", matchIfMissing = true)
public class EvaluationServiceImpl implements EvaluationService {

    @Autowired
    private TcEvaluationRepository tcEvaluationRepository;

    @Autowired
    private TcOrderRepository tcOrderRepository;

    @Autowired
    private EvaluationConverter evaluationConverter;

    @Autowired
    private UserRepository userRepository;

    @Value("${trade.order.sellerevaluate.topic:}")
    private String sellerEvaluateTopic;
    @Value("${elasticsearch.name.tcEvaluation:evaluation_new_dev}")
    private String indexName;
    @Value("${trade.syn.topic:}")
    private String topic;
    @Value("${trade.syn.tag:}")
    private String tag;
    @Autowired
    private MessageSendManager messageSendManager;
    @Autowired
    protected MultiLangConverter multiLangConverter;

    @Override
    public void evaluate(EvaluationRpcReq evaluationRpcReq){
        evaluate(Lists.newArrayList(evaluationRpcReq));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void evaluate(List<EvaluationRpcReq> evaluations) {
        for (EvaluationRpcReq evaluation : evaluations) {
            evaluation.extend().put(TradeExtendKeyConstants.EVALUATION_TYPE, TradeExtendKeyConstants.EVALUATION_TYPE_MAIN);
            //evaluation.extend().put(TradeExtendKeyConstants.EVALUATION_HAS_ADDITION, false);
        }
        evaluateOrder(evaluations, OrderEvaluateEnum.NOT_EVALUATE, OrderEvaluateEnum.FIRST_EVALUATED);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void additionalEvaluate(List<EvaluationRpcReq> evaluations) {
        for (EvaluationRpcReq evaluation : evaluations) {
            evaluation.extend().put(TradeExtendKeyConstants.EVALUATION_TYPE, TradeExtendKeyConstants.EVALUATION_TYPE_ADDITION);
        }
        evaluateOrder(evaluations, OrderEvaluateEnum.FIRST_EVALUATED, OrderEvaluateEnum.ADDITIONAL_EVALUATED);
        updateAdditionFlag(evaluations);    // 更新主评价记录的标
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void addExtraEvaluate(List<EvaluationRpcReq> evaluationRpcReq) {
        // 创建评价记录
        for(EvaluationRpcReq req : evaluationRpcReq){
            TcEvaluationDO tcEvaluationDO = evaluationConverter.toTcEvaluationDO(req);
            tcEvaluationRepository.create(tcEvaluationDO);
        }
    }

    @Override
    public void updateExtendInfo(EvaluationExtendModifyReq req) {
        TcEvaluationDO exist = tcEvaluationRepository.queryById(req.getEvaluationId(), req.getPrimaryOrderId());
        if (exist == null) {
            throw new GmallException(OrderErrorCode.EVALUATION_NOT_FOUND);
        }
        if (req.getCustId() != null && !req.getCustId().equals(exist.getCustId())) {
            throw new GmallException(CommonErrorCode.NOT_DATA_OWNER);
        }
        if (req.getSellerId() != null && !req.getSellerId().equals(exist.getSellerId())) {
            throw new GmallException(CommonErrorCode.NOT_DATA_OWNER);
        }

        Map<String, Object> map = exist.getExtend();
        if (map == null) {
            map = new HashMap<>();
        }
        if (req.getUpdateExtendMap() != null) {
            map.putAll(req.getUpdateExtendMap());
        }
        TcEvaluationDO up = new TcEvaluationDO();
        up.setId(exist.getId());
        up.setPrimaryOrderId(exist.getPrimaryOrderId());
        up.setGmtModified(exist.getGmtModified());
        up.setExtend(map);
        boolean success = tcEvaluationRepository.update(up);
        if (!success) {
            throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
        }
    }

    @Override
    @Transactional
    public void autoEvaluation(Long primaryOrderId) {
        List<TcOrderDO> orderDOList = tcOrderRepository.queryOrdersByPrimaryId(primaryOrderId);
        if (orderDOList == null) {
            return;
        }
        List<EvaluationRpcReq> reqList = new ArrayList<>();
        for(TcOrderDO tcOrderDO : orderDOList) {
            // 排除已评价的
            if (tcOrderDO.getEvaluate() != null
                    && !OrderEvaluateEnum.NOT_EVALUATE.getCode().equals(tcOrderDO.getEvaluate())) {
                continue;
            }

            EvaluationRpcReq evaluationRpcReq = new EvaluationRpcReq();
            evaluationRpcReq.setPrimaryOrderId(primaryOrderId);
            evaluationRpcReq.setCustId(tcOrderDO.getCustId());
            evaluationRpcReq.setOrderId(tcOrderDO.getOrderId());
            evaluationRpcReq.setRateDesc(I18NMessageUtils.getMessage("system.auto.review"));  //# "系统自动好评"
            evaluationRpcReq.setItemId(tcOrderDO.getItemId());
            evaluationRpcReq.setRateScore(5);
            evaluationRpcReq.setSellerId(tcOrderDO.getSellerId());
            evaluationRpcReq.setReplyId(0L);
            evaluationRpcReq.setBinOrIin(tcOrderDO.getBinOrIin());
            evaluationRpcReq.setSkuId(tcOrderDO.getSkuId());

            Map<String, Object> extend = evaluationRpcReq.extend();
            extend.put(TradeExtendKeyConstants.EVALUATION_IS_SYSTEM, true);
            extend.put(TradeExtendKeyConstants.EVALUATION_CUST_NAME, tcOrderDO.getCustName());
            extend.put(TradeExtendKeyConstants.EVALUATION_ITEM_TITLE, tcOrderDO.getItemTitle());
            extend.put(TradeExtendKeyConstants.EVALUATION_APPROVE_CONTENT, StringUtils.EMPTY);
            extend.put(TradeExtendKeyConstants.CUSTOMER_IP, "127.0.0.1");
            extend.put(TradeExtendKeyConstants.EVALUATION_APPROVE_STATUS, EvaluationApproveStatusEnum.PASSED.getCode());
            extend.put(TradeExtendKeyConstants.EVALUATION_APPROVE_USER_NAME, "admin");
            extend.put(TradeExtendKeyConstants.EVALUATION_APPROVE_TIME, System.currentTimeMillis());

            // 主订单物流评分
            if(tcOrderDO.getPrimaryOrderFlag() > 0){
                extend.put(TradeExtendKeyConstants.EVALUATION_LOGISTICS_RATE, 5);
            }
            reqList.add(evaluationRpcReq);
        }

        if (!reqList.isEmpty()) {
            evaluate(reqList);
        }
    }

    private void evaluateOrder(List<EvaluationRpcReq> evaluations, OrderEvaluateEnum from, OrderEvaluateEnum to) {
        long primaryOrderId = evaluations.get(0).getPrimaryOrderId();
        for (EvaluationRpcReq ev : evaluations) {
            if (ev.getPrimaryOrderId() != primaryOrderId) {
                throw new GmallException(OrderErrorCode.EVALUATION_CROSS_PRIMARY_ORDER);
            }
        }
        List<TcOrderDO> orderDOList = tcOrderRepository.queryOrdersByPrimaryId(primaryOrderId);
        Map<Long,Object> map = new HashMap<>();
        for(TcOrderDO orderDO : orderDOList){
            Integer evaluate = orderDO.getEvaluate();
            if (evaluate == null) {
                evaluate = OrderEvaluateEnum.NOT_EVALUATE.getCode();
            }
            if (!from.getCode().equals(evaluate)) {
                throw new GmallException(OrderErrorCode.EVALUATION_STATUS_ILLEGAL);
            }
            map.put(orderDO.getOrderId(),orderDO);
        }

        // 创建评价记录
        for(EvaluationRpcReq req : evaluations){
            TcOrderDO orderDO = (TcOrderDO)map.get(req.getOrderId());
            TcEvaluationDO tcEvaluationDO = evaluationConverter.toTcEvaluationDO(req);
            tcEvaluationDO.setBinOrIin(orderDO.getBinOrIin());
            tcEvaluationRepository.create(tcEvaluationDO);
           /* if(tcEvaluationDO.getItemId()==null || tcEvaluationDO.getItemId()==0){
                EvaluateMessageDTO msg = toEvaluateMsg(tcEvaluationDO);
                //发送主单上的商家评价MQ
                messageSendManager.sendMessage(msg, sellerEvaluateTopic, "SUCCESS");
            }*/
            pushEsInfo(tcEvaluationDO,orderDO);
        }

        // 更新订单
        Set<Long> subOrderIdSet = evaluations.stream().map(EvaluationRpcReq::getOrderId).collect(Collectors.toSet());
        TcOrderDO mainOrder = null;
        boolean updateMain = true;
        for(TcOrderDO orderDO : orderDOList){
            if(subOrderIdSet.contains(orderDO.getOrderId())){
                updateOrderEvaluationInfo(orderDO, to);
                if (PrimaryOrderFlagEnum.PRIMARY_ORDER.getCode().equals(orderDO.getPrimaryOrderFlag())) {
                    updateMain = false;
                }
            }
            if (PrimaryOrderFlagEnum.PRIMARY_ORDER.getCode().equals(orderDO.getPrimaryOrderFlag())) {
                mainOrder = orderDO;
            } else if (!to.getCode().equals(orderDO.getEvaluate())) {
                updateMain = false;
            }
        }
        if (updateMain) {
            updateOrderEvaluationInfo(mainOrder, to);
        }
    }

    private void pushEsInfo(TcEvaluationDO tcEvaluationDO,TcOrderDO orderDO ) {
        SearchMessage message = new SearchMessage();
        //同步ES
        JSONObject param = new JSONObject();
        param.put("id", tcEvaluationDO.getId());
        param.put("cust_id", tcEvaluationDO.getCustId());
        param.put("evaluation_approve_status", tcEvaluationDO.getExtend().get("evaluationApproveStatus"));
        param.put("gmt_create", tcEvaluationDO.getGmtCreate());
        param.put("gmt_modified", tcEvaluationDO.getGmtModified());
        param.put("item_id", tcEvaluationDO.getItemId());
        param.put("sku_id", tcEvaluationDO.getSkuId());
        param.put("order_id", tcEvaluationDO.getOrderId());
        param.put("primary_order_id", tcEvaluationDO.getPrimaryOrderId());
        param.put("seller_id", tcEvaluationDO.getSellerId());
        param.put("rate_score", tcEvaluationDO.getRateScore());
        param.put("reply_id", tcEvaluationDO.getReplyId());
        if(!ObjectUtils.isEmpty(orderDO)){
            param.put("item_title", multiLangConverter.mText_to_str(multiLangConverter.to_multiLangText(orderDO.getItemTitle())));
        }
        param.put("seller_bin", tcEvaluationDO.getBinOrIin());
        param.put("is_system", false);
        if(!ObjectUtils.isEmpty(tcEvaluationDO.getSellerId())){
            Seller seller = userRepository.getSeller(tcEvaluationDO.getSellerId());
            param.put("seller_name", seller.getSellerName());
        }
        if(tcEvaluationDO.getItemId()==null || tcEvaluationDO.getItemId()==0){
            param.put("has_item_id",false);
            param.put("evaluation_type", 1);
        }else{
            param.put("has_item_id",true);
            param.put("evaluation_type", 1);
        }
        if(tcEvaluationDO.getRatePic()==null || tcEvaluationDO.getRatePic().isEmpty()){
            param.put("has_rate_pic",false);
        }else{
            param.put("has_rate_pic",true);
        }
        message.setContent(param);
        message.setType(1);
        message.setIndexName(indexName);
        message.setId(String.valueOf(tcEvaluationDO.getId()));
        MessageDTO msg = new MessageDTO();
        msg.setMessage(message);
        messageSendManager.sendMessage(message, topic, tag);
    }

    protected EvaluateMessageDTO toEvaluateMsg(TcEvaluationDO evaluationDO) {
        EvaluateMessageDTO dto = new EvaluateMessageDTO();
        BeanUtils.copyProperties(evaluationDO, dto);
        return dto;
    }
    private void updateOrderEvaluationInfo(TcOrderDO orderDO, OrderEvaluateEnum to){
        OrderAttrDO orderAttrDO = orderDO.getOrderAttr();
        if(orderAttrDO == null){
            orderAttrDO = new OrderAttrDO();
        }
        if(orderAttrDO.getEvaluateTime() == null) {
            orderAttrDO.setEvaluateTime(new Date());
        }

        TcOrderDO newTcOrderDO = new TcOrderDO();
        newTcOrderDO.setOrderAttr(orderAttrDO);
        newTcOrderDO.setEvaluate(to.getCode());
        newTcOrderDO.setPrimaryOrderId(orderDO.getPrimaryOrderId());
        newTcOrderDO.setOrderId(orderDO.getOrderId());
        newTcOrderDO.setVersion(orderDO.getVersion());
        boolean success = tcOrderRepository.updateByOrderIdVersion(newTcOrderDO);
        if (!success) {
            throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
        }
        orderDO.setEvaluate(newTcOrderDO.getEvaluate());
        orderDO.setVersion(newTcOrderDO.getVersion());
    }

    private void updateAdditionFlag(List<EvaluationRpcReq> evaluations) {
        long primaryOrderId = evaluations.get(0).getPrimaryOrderId();
        List<TcEvaluationDO> list = tcEvaluationRepository.queryByPrimaryOrderId(primaryOrderId);
        Set<Long> subOrderIdSet = evaluations.stream().map(EvaluationRpcReq::getOrderId).collect(Collectors.toSet());
        for (TcEvaluationDO ev : list) {
            if (ev.getExtend() == null && !subOrderIdSet.contains(ev.getOrderId())) {
                continue;
            }
            Integer type = (Integer) ev.getExtend().get(TradeExtendKeyConstants.EVALUATION_TYPE);
            if (type == null || type.intValue() != TradeExtendKeyConstants.EVALUATION_TYPE_MAIN) {
                continue;
            }
            ev.getExtend().put(TradeExtendKeyConstants.EVALUATION_HAS_ADDITION, true);

            TcEvaluationDO up = new TcEvaluationDO();
            up.setId(ev.getId());
            up.setPrimaryOrderId(ev.getPrimaryOrderId());
            up.setGmtModified(ev.getGmtModified());
            up.setExtend(ev.getExtend());
            boolean success = tcEvaluationRepository.update(up);
            if (!success) {
                throw new GmallException(CommonErrorCode.CONCURRENT_UPDATE_FAIL);
            }
        }
    }
}
