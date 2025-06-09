package com.aliyun.gts.gmall.manager.front.sourcing.service.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gcai.platform.sourcing.common.input.StatusUpdateRequest;
import com.aliyun.gts.gcai.platform.sourcing.common.type.PricingBillStatus;
import com.aliyun.gts.gmall.center.misc.api.dto.input.flow.AppInfo;
import com.aliyun.gts.gmall.center.misc.api.dto.output.flow.message.WorkflowInvovedMessage;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.b2bcomm.consumer.service.ConsumerService;
import com.aliyun.gts.gmall.manager.front.sourcing.facade.PricingBillFacade;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ChooseBidConsumerService implements ConsumerService {

    @Resource
    private PricingBillFacade facade;

    @Override
    public void consume(WorkflowInvovedMessage message) throws Exception {
        Long id = Long.valueOf(message.getBusinessId());
        StatusUpdateRequest dto = new StatusUpdateRequest();
        dto.setId(id);
        if (message.getApproved()) {
            dto.setStatus(PricingBillStatus.pass_approve.getValue());
        } else {
            dto.setStatus(PricingBillStatus.not_approve.getValue());
        }
        RestResponse<Boolean> response = facade.approve(dto);
        if(!response.isSuccess()){
            throw new Exception(I18NMessageUtils.getMessage("bid.sheet.update.fail")+" ： " + response.getMessage() + " , " + id);  //# "决标单更新失败
        }
    }

    @Override
    public String getTag() {
        return AppInfo.SOURCING_BID;
    }
}
