package com.aliyun.gts.gmall.manager.front.sourcing.service.impl;

import com.aliyun.gts.gcai.platform.contract.api.dto.model.ContractDTO;
import com.aliyun.gts.gcai.platform.contract.api.dto.model.ContractSignStatusDTO;
import com.aliyun.gts.gcai.platform.contract.api.facade.ContractFacade;
import com.aliyun.gts.gcai.platform.contract.api.query.ContractSignPageQuery;
import com.aliyun.gts.gcai.platform.contract.common.type.SignStatus;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.ByIdQueryRequest;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.SignatureUtils;
import com.aliyun.gts.gmall.manager.front.sourcing.service.ContractService;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.contract.ContractSignStatusVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ContractServiceImpl implements ContractService {

    @Autowired
    ContractFacade contractFacade;

    @Value("${esign.callback.url:}")
    String eSignCallBack;



    @Override
    public RestResponse signStatus(ContractSignStatusVO signStatusVO) {

        SignStatus status = SignStatus.valueOf(signStatusVO.getSignStatus());
        ContractSignStatusDTO statusDTO = new ContractSignStatusDTO();
        statusDTO.setId(signStatusVO.getId());
        statusDTO.setSignStatus(status);
        RpcResponse response = contractFacade.changeSignStatus(statusDTO);
        if(!response.isSuccess()){
            return RestResponse.fail(response.getFail().getCode() , response.getFail().getMessage());
        }else{
            return RestResponse.okWithoutMsg(null);
        }
    }

    @Override
    public RestResponse<String> signPage(Long contractId) {

        ContractDTO contractDTO = getContract(contractId);
        Long eContractId = contractDTO.getFeature().getEContractId();
        String cellPhone = contractDTO.getFeature().getJiaEsignInfo().getCellPhone();
        ContractSignPageQuery signPageQuery = new ContractSignPageQuery();
        signPageQuery.setEContractId(eContractId);
        signPageQuery.setCellPhone(cellPhone);
        signPageQuery.setCallBackUrl(eSignCallBack + contractDTO.getId());
        String signature = signature(contractId);
        String callback = String.format(eSignCallBack, contractDTO.getId() , signature);
        signPageQuery.setCallBackUrl(callback);
        RpcResponse<String>  pageResponse = contractFacade.signPage(signPageQuery);

        if(pageResponse.isSuccess()){
            return RestResponse.okWithoutMsg(pageResponse.getData());
        }else{
            return RestResponse.fail(pageResponse.getFail().getCode() , pageResponse.getFail().getMessage());
        }
    }

    @Override
    public boolean esignCallback(Long contractId, String sign) {

        String signature = signature(contractId);
        if(sign.equals(signature)){
            ContractDTO contractDTO = getContract(contractId);
            if(contractDTO.getSignStatus() == null ||
                (contractDTO.getSignStatus() & SignStatus.PurchaseSigned.getValue()) !=
                SignStatus.PurchaseSigned.getValue()){
                ContractSignStatusDTO statusDTO = new ContractSignStatusDTO();
                statusDTO.setId(contractId);
                statusDTO.setSignStatus(SignStatus.PurchaseSigned);
                RpcResponse response = contractFacade.changeSignStatus(statusDTO);
                if(response.isSuccess()){
                    return true;
                }else {
                    return false;
                }
            }
        }

        return false;
    }

    private ContractDTO getContract(Long contractId){
        ByIdQueryRequest idQueryRequest = new ByIdQueryRequest();
        idQueryRequest.setId(contractId);

        RpcResponse<ContractDTO>  response = contractFacade.getContract(idQueryRequest);
        ContractDTO contractDTO = response.getData();
        return contractDTO;
    }

    private String signature(Long contractId){
        return SignatureUtils.signatureMd5(contractId);

    }
}
