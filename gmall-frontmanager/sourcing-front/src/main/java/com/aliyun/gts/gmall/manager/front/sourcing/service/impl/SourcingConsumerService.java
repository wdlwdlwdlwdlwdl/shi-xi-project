package com.aliyun.gts.gmall.manager.front.sourcing.service.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gcai.platform.sourcing.api.dto.model.SourcingDTO;
import com.aliyun.gts.gcai.platform.sourcing.api.facade.SourcingReadFacade;
import com.aliyun.gts.gcai.platform.sourcing.api.facade.SourcingWriteFacade;
import com.aliyun.gts.gcai.platform.sourcing.common.input.CommandRequest;
import com.aliyun.gts.gcai.platform.sourcing.common.input.query.CommonIdQuery;
import com.aliyun.gts.gcai.platform.sourcing.common.type.SourcingStatus;
import com.aliyun.gts.gmall.center.misc.api.dto.input.flow.AppInfo;
import com.aliyun.gts.gmall.center.misc.api.dto.output.flow.message.WorkflowInvovedMessage;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.b2bcomm.consumer.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SourcingConsumerService implements ConsumerService {

    @Autowired
    SourcingWriteFacade sourcingWriteFacade;
    @Autowired
    SourcingReadFacade sourcingReadFacade;

    @Override
    public void consume(WorkflowInvovedMessage message) throws Exception {
        Long sourcingId = Long.valueOf(message.getBusinessId());
        CommonIdQuery idQuery = new CommonIdQuery();
        idQuery.setId(sourcingId);
        RpcResponse<SourcingDTO> readResponse = sourcingReadFacade.queryById(idQuery);
        if(!readResponse.getData().getStatus().equals(SourcingStatus.submit_approve.getValue())){
            return;
        }
        CommandRequest<SourcingDTO> request = new CommandRequest<SourcingDTO>();
        SourcingDTO sourcingDTO = new SourcingDTO();
        sourcingDTO.setId(sourcingId);
        if(message.getApproved()){
            sourcingDTO.setStatus(SourcingStatus.pass_approve.getValue());
        }else{
            sourcingDTO.setStatus(SourcingStatus.forbid_approve.getValue());
        }
        request.setData(sourcingDTO);
        RpcResponse<Boolean> response = sourcingWriteFacade.updateStatus(request);
        if(!response.isSuccess()){
            throw new Exception(I18NMessageUtils.getMessage("ss.req.update.fail")+" " + sourcingId);  //# "更新寻源单状态失败
        }

    }

    @Override
    public String getTag() {
        return AppInfo.SOURCING_BR;
    }
}
