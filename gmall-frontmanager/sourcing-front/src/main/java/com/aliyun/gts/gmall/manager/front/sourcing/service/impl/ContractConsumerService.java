package com.aliyun.gts.gmall.manager.front.sourcing.service.impl;

import com.aliyun.gts.gcai.platform.contract.api.dto.model.ContractStatusDTO;
import com.aliyun.gts.gcai.platform.contract.api.facade.ContractFacade;
import com.aliyun.gts.gcai.platform.contract.common.type.ContractStatus;
import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.PurchaseContractDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.facade.SourcingContractFacade;
import com.aliyun.gts.gmall.center.misc.api.dto.input.flow.AppInfo;
import com.aliyun.gts.gmall.center.misc.api.dto.output.flow.message.WorkflowInvovedMessage;
import com.aliyun.gts.gmall.framework.api.rpc.dto.ByIdQueryRequest;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.b2bcomm.consumer.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContractConsumerService implements ConsumerService {

    @Autowired
    ContractFacade contractFacade;

    @Autowired
    SourcingContractFacade sourcingContractFacade;

    @Override
    public void consume(WorkflowInvovedMessage message) throws Exception {

        ContractStatusDTO contractStatusDTO = new ContractStatusDTO();
        contractStatusDTO.setId(Long.valueOf(message.getBusinessId()));

        ByIdQueryRequest idQueryRequest = new ByIdQueryRequest();
        idQueryRequest.setId(contractStatusDTO.getId());
        RpcResponse<PurchaseContractDTO> contractResponse = sourcingContractFacade.getContract(idQueryRequest);
        PurchaseContractDTO purchaseContractDTO = contractResponse.getData();
        if(purchaseContractDTO.getStatus().equals(ContractStatus.Deal.getValue())){
            return ;
        }
        if(message.getApproved()) {
            contractStatusDTO.setStatus(ContractStatus.Signing);
        }else{
            contractStatusDTO.setStatus(ContractStatus.Rejected);
        }
        RpcResponse response = contractFacade.changeStatus(contractStatusDTO);
        if(!response.isSuccess()){
            throw new Exception(response.getFail().getMessage());
        }
        if(message.getApproved()) {
            if(purchaseContractDTO.getFeature().getSignType().equals(1)) {
                RpcResponse sendResponse = contractFacade.sendContract(idQueryRequest);
                if (!sendResponse.isSuccess()) {
                    throw new Exception(sendResponse.getFail().getMessage());
                }
            }
        }
    }

    @Override
    public String getTag() {
        return AppInfo.SOURCING_CR;
    }
}
