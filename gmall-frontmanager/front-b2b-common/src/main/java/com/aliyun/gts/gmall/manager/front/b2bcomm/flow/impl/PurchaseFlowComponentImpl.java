package com.aliyun.gts.gmall.manager.front.b2bcomm.flow.impl;

import com.aliyun.gts.gmall.center.misc.api.dto.output.flow.message.WorkflowInvovedMessage;
import com.aliyun.gts.gmall.manager.front.MessageSendManager;
import com.aliyun.gts.gmall.manager.front.b2bcomm.flow.PurchaseFlowComponent;
import com.aliyun.gts.gmall.manager.front.b2bcomm.model.OperatorDO;
//import com.aliyun.gts.gmall.middleware.api.mq.model.StandardEvent;
//import com.aliyun.gts.gmall.middleware.mq.model.DefaultStandardEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PurchaseFlowComponentImpl implements PurchaseFlowComponent {

    @Autowired
    private MessageSendManager messageSendManager;

    @Value("${mq.consumer.workFlow.topic}")
    private String topic;

    private final int SEND_ATTEMPTS = 3;

    @Override
    public boolean startFlow(Long yourBizId, OperatorDO operatorDO, String url, String appCode) {
        WorkflowInvovedMessage msg = new WorkflowInvovedMessage();
        msg.setApproved(true);  // 直接审核通过
        msg.setBusinessId(yourBizId + "");
        for (int i=0; i<SEND_ATTEMPTS; i++) {
            try {
                if (messageSendManager.sendMessage(msg, topic, appCode)) {
                    return true;
                }
            } catch (Exception e) {
                log.error("", e);
            }
        }
        return false;
    }
}
//
//    @Autowired
//    FlowEngineFacade flowEngineFacade;
//
//    @Autowired
//    PurchaseRedisComponent purchaseRedisComponent;
//
//    @Autowired
//    SettleInWorkFlowMessageProcessor consumeEventProcessor;
//
//    /**
//     * 审核地址
//     */
//    private String auditUrlPre;
//    private String redirectUrl;
//
//    @Override
//    public boolean startFlow(Long yourBizId, OperatorDO operatorDO, String url, String appCode) {
//        //ContentOfAuditing auditing = new ContentOfAuditing();
//        //auditing.setOutUrl(url);
//        //auditing.setOperatorName(operatorDO.getUsername());
//        //auditing.setOperatorId(operatorDO.getOperatorId() + "");
//        //auditing.setBusinessId(yourBizId + "");
//        //
//        //for (int i = 0; i < 3; i++) {
//        //    RpcResponse<WorkflowDTO> rpcResponse = flowEngineFacade.commit(auditing, appCode);
//        //    if (rpcResponse.isSuccess()) {
//        //        //purchaseRedisComponent.putApproveId(yourBizId , rpcResponse.getData().getProcessInstanceId() , appCode);
//        //        return true;
//        //    }
//        //}
//        //return false;
//
//        //流程引擎无人维护  暂时直接替换掉流程引擎
//        StandardEvent standardEvent = new DefaultStandardEvent("",appCode);
//        WorkflowInvovedMessage workflowInvovedMessage = new WorkflowInvovedMessage();
//        workflowInvovedMessage.setApproved(true);
//        workflowInvovedMessage.setBusinessId(yourBizId + "");
//        MqPayload payload = new MqPayload();
//        payload.setData(workflowInvovedMessage);
//        standardEvent.setPayload(payload);
//        consumeEventProcessor.process(standardEvent);
//
//        return true;
//
//    }
//
//    @Override
//    public boolean cancelFlow(Long yourBizId, OperatorDO operatorDO, String appCode) {
//        ContentOfAuditing auditing = new ContentOfAuditing();
//        auditing.setOperatorName(operatorDO.getUsername());
//        auditing.setOperatorId(operatorDO.getOperatorId() + "");
//        auditing.setBusinessId(yourBizId + "");
//
//        for (int i = 0; i < 3; i++) {
//            RpcResponse<String> rpcResponse = flowEngineFacade.rollback(auditing, appCode);
//            if (rpcResponse.isSuccess()) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    @Override
//    public Map<String, String> queryInstanceId(String appCode, List<Long> bizIds) {
//        List<String> strs = new ArrayList<>();
//        for (Long id : bizIds) {
//            strs.add(id + "");
//        }
//        FlowQueryRpcReq queryRpcReq = new FlowQueryRpcReq();
//        queryRpcReq.setAppCode(appCode);
//        queryRpcReq.setBusinessIds(strs);
//        RpcResponse<List<WorkflowDTO>> response = flowEngineFacade.batchQueryInstantIds(queryRpcReq);
//        Map<String, String> result = new HashMap<>();
//        if (CollectionUtils.isEmpty(response.getData())) {
//            return result;
//        }
//        for(WorkflowDTO workflowDTO : response.getData()){
//            if(workflowDTO != null && workflowDTO.getBusinessId() != null) {
//                result.put(workflowDTO.getBusinessId(), workflowDTO.getProcessInstanceId());
//            }
//        }
//        return result;
//        return null;
//    }
//
//    @Override
//    public String getAuditFlowUrl(String token, String instanceId) {
//        redirectUrl = "http://120.55.180.9/flow-platform/4A/authFree/authWithToken";
//        auditUrlPre = "http://120.55.180.9/todo/detail?actype=todo";
//        StringBuilder builder = new StringBuilder();
//        builder.append(redirectUrl).append("?token=").append(token);
//        String url = auditUrlPre + "&processInstanceId=" + instanceId;
//        builder.append("&redirectUrl=").append(urlEncode(url));
//        return builder.toString();
//    }
//    private String urlEncode(String str){
//        try {
//            return URLEncoder.encode(str, "utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            return str;
//        }
//    }
//}
