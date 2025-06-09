package com.aliyun.gts.gmall.manager.front.sourcing.service.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.PurchaseRequestStatusDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.facade.PurchaseRequestFacade;
import com.aliyun.gts.gcai.platform.sourcing.common.type.PurchaseRequestStatus;
import com.aliyun.gts.gmall.center.misc.api.dto.input.flow.AppInfo;
import com.aliyun.gts.gmall.center.misc.api.dto.output.flow.message.WorkflowInvovedMessage;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.b2bcomm.consumer.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PurchaseRequestConsumerService implements ConsumerService {

    @Autowired
    PurchaseRequestFacade purchaseRequestFacade;

    @Override
    public void consume(WorkflowInvovedMessage message) throws Exception {
        Long prId = Long.valueOf(message.getBusinessId());
        PurchaseRequestStatusDTO statusDTO = new PurchaseRequestStatusDTO();
        statusDTO.setId(prId);
        if(message.getApproved()) {
            statusDTO.setStatus(PurchaseRequestStatus.Approved);
        }else{
            statusDTO.setStatus(PurchaseRequestStatus.Rejected);
        }
        RpcResponse response = purchaseRequestFacade.updateStatus(statusDTO);
        if(!response.isSuccess()){
            throw new Exception(I18NMessageUtils.getMessage("pr.req.update.fail")+" " + prId);  //# "更新请购单状态失败
        }

    }

    @Override
    public String getTag() {
        return AppInfo.SOURCING_PR;
    }
}
