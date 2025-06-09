//package com.aliyun.gts.gmall.platform.trade.server.flow.handler.order.confirmreceipt;
//
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.nacos.api.config.annotation.NacosValue;
//import com.aliyun.gts.gmall.framework.api.flow.dto.FlowNodeResult;
//import com.aliyun.gts.gmall.framework.processengine.core.model.ProcessFlowNodeHandler;
//import com.aliyun.gts.gmall.framework.server.exception.GmallException;
//import com.aliyun.gts.gmall.middleware.logistics.LogisticsComponent;
//import com.aliyun.gts.gmall.middleware.logistics.constants.KdnLogisticsCompanyEnum;
//import com.aliyun.gts.gmall.middleware.logistics.constants.LogisTraceStateEnum;
//import com.aliyun.gts.gmall.middleware.logistics.entity.LogisticsQueryReq;
//import com.aliyun.gts.gmall.middleware.logistics.entity.LogisticsTraceDTO;
//import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
//import com.aliyun.gts.gmall.platform.trade.api.dto.common.logistics.LogisticsDetailDTO;
//import com.aliyun.gts.gmall.platform.trade.api.dto.common.logistics.ReceiverInfoDTO;
//import com.aliyun.gts.gmall.platform.trade.core.ability.OrderQueryAbility;
//import com.aliyun.gts.gmall.platform.trade.core.bizcode.BizCodeEntity;
//import com.aliyun.gts.gmall.platform.trade.core.domainservice.OrderTaskService;
//import com.aliyun.gts.gmall.platform.trade.core.task.biz.SystemConfirmOrderTask;
//import com.aliyun.gts.gmall.platform.trade.core.task.param.TaskParam;
//import com.aliyun.gts.gmall.platform.trade.domain.dataobject.TcLogisticsDO;
//import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeOperate;
//import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderChangeOperateEnum;
//import com.aliyun.gts.gmall.platform.trade.domain.entity.order.OrderStatus;
//import com.aliyun.gts.gmall.platform.trade.domain.entity.task.ScheduleTask;
//import com.aliyun.gts.gmall.platform.trade.domain.repository.TcLogisticsRepository;
//import com.google.common.collect.Lists;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections4.CollectionUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * 系统自动确认收货时校验当前的物流签收状态、如果未签收则自动延长确认收货时间
// */
//@Slf4j
//@Component
//public class CheckLogisSignForHandler implements ProcessFlowNodeHandler<OrderStatus, FlowNodeResult<Boolean>> {
//
//    @Autowired
//    private OrderQueryAbility orderQueryAbility;
//
//    @Autowired
//    private TcLogisticsRepository tcLogisticsRepository;
//
//    @Autowired
//    private LogisticsComponent logisticsComponent;
//
//    @Autowired
//    private SystemConfirmOrderTask systemConfirmOrderTask;
//
//    @Autowired
//    private OrderTaskService orderTaskService;
//
//    @NacosValue(value = "${isOpenCheckLogisSignState:true}", autoRefreshed = true)
//    private boolean isOpenCheckLogisSignState;
//
//    @Override
//    public FlowNodeResult<Boolean> handleBiz(Map<String, Object> map, OrderStatus orderStatus) {
//        //如果开关关闭则不需要校验签收状态
//        if(!isOpenCheckLogisSignState){
//            return FlowNodeResult.ok(Boolean.TRUE);
//        }
//        OrderChangeOperate op = (OrderChangeOperate) map.get("op");
//        //主动确认收货不需要校验签收状态
//        if(OrderChangeOperateEnum.CUST_CONFIRM_RECEIVE.getOprType() == op.getOprType()){
//            return FlowNodeResult.ok(Boolean.TRUE);
//        }
//
//        List<TcLogisticsDO> list = tcLogisticsRepository.queryByPrimaryId(orderStatus.getPrimaryOrderId(), null);
//        //无物流信息、有可能为无需物流场景、直接兼容返回true;
//        if (CollectionUtils.isEmpty(list)) {
//            return FlowNodeResult.ok(Boolean.TRUE);
//        }
//        for (TcLogisticsDO tcLogisticsDO : list) {
//            if (orderStatus.getCustId() != null) {
//                if (!orderStatus.getCustId().equals(tcLogisticsDO.getCustId())) {
//                    throw new GmallException(OrderErrorCode.LOGISTICS_USER_NOT_MATCH);
//                }
//            }
//            if (orderStatus.getSellerId() != null) {
//                if (!orderStatus.getSellerId().equals(tcLogisticsDO.getSellerId())) {
//                    throw new GmallException(OrderErrorCode.LOGISTICS_USER_NOT_MATCH);
//                }
//            }
//            LogisticsDetailDTO logisticsDetailDTO = new LogisticsDetailDTO();
//            logisticsDetailDTO.setPrimaryOrderId(orderStatus.getPrimaryOrderId());
//            ReceiverInfoDTO receiverInfoDTO = new ReceiverInfoDTO();
//            logisticsDetailDTO.setReceiverInfo(receiverInfoDTO);
//            receiverInfoDTO.setReceiverAddr(tcLogisticsDO.getReceiverAddr());
//            receiverInfoDTO.setReceiverName(tcLogisticsDO.getReceiverName());
//            receiverInfoDTO.setReceiverPhone(tcLogisticsDO.getReceiverPhone());
//            logisticsDetailDTO.setLogisticsAttr(tcLogisticsDO.getLogisticsAttr());
//            logisticsDetailDTO.setDeliveryId(tcLogisticsDO.getLogisticsId());
//
//            KdnLogisticsCompanyEnum company = KdnLogisticsCompanyEnum.codeOf(tcLogisticsDO.getCompanyType());
//            //查不到公司类型为上游发货时传入物流公司类型同快递鸟不匹配、这里跳过校验
//            if (company == null) {
//                continue;
//            }
//            logisticsDetailDTO.setDeliveryCompany(company.getName());
//            //判断是否已签收;只要有一单未签收、则确认收货整体自动延长
//            if (!checkLogisHasSignFor(tcLogisticsDO, company)) {
//                log.warn("物流单号："+tcLogisticsDO.getLogisticsId()+" 未签收；主订单："+orderStatus.getPrimaryOrderId()+" 确认收货自动延长");
//                newBuildTask(map,orderStatus,tcLogisticsDO);
//                return FlowNodeResult.ok(Boolean.FALSE);
//            }
//        }
//
//        return FlowNodeResult.ok(Boolean.TRUE);
//    }
//
//    private void newBuildTask(Map<String, Object> map, OrderStatus orderStatus,TcLogisticsDO tcLogisticsDO){
//        BizCodeEntity bizCodeEntity =  (BizCodeEntity) map.get("bizCodeEntity");
//        ScheduleTask task = systemConfirmOrderTask.buildTask(new TaskParam(
//                tcLogisticsDO.getSellerId(),
//                orderStatus.getPrimaryOrderId(),
//                Lists.newArrayList(bizCodeEntity)));
//        orderTaskService.createScheduledTask(Lists.newArrayList(task));
//    }
//
//    private boolean checkLogisHasSignFor(TcLogisticsDO logistics, KdnLogisticsCompanyEnum company) {
//        LogisticsQueryReq req = new LogisticsQueryReq();
//        req.setCompanyCode(company.getCompanyCode());
//        req.setLogisticsCode(logistics.getLogisticsId());
//        req.setCustomerPhone(logistics.getReceiverPhone());
//
//        LogisticsTraceDTO logisticsTraceDTO = logisticsComponent.queryTraceV2(req);
//        // 为空的情况、可能是快递鸟因为系统异常查询失败、这里做弱依赖处理、直接返回已签收
//        if (StringUtils.isEmpty(logisticsTraceDTO.getState())) {
//            return true;
//        }
//        if(LogisTraceStateEnum.SIGN_FOR.getCode().intValue() !=
//                Integer.valueOf(logisticsTraceDTO.getState()).intValue()){
//            log.warn("LogisTrace not sign for: "+ JSONObject.toJSONString(logisticsTraceDTO));
//            return false;
//        }
//        return true;
//    }
//
//
//}
