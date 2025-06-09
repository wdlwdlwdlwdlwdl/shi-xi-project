package com.aliyun.gts.gmall.manager.front.sourcing.controller;

import com.aliyun.gts.gcai.platform.sourcing.common.type.SourcingStatus;
import com.aliyun.gts.gcai.platform.sourcing.common.type.SourcingType;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.b2bcomm.constants.AccountTypeEnum;
import com.aliyun.gts.gmall.manager.front.b2bcomm.input.ByIdLoginRestReq;
import com.aliyun.gts.gmall.manager.front.b2bcomm.model.OperatorDO;
import com.aliyun.gts.gmall.manager.front.sourcing.facade.SourcingFacade;
import com.aliyun.gts.gmall.manager.front.sourcing.input.SourcingQueryReq;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/tendering")
public class TenderingController extends SourcingController{

    @Autowired
    SourcingFacade sourcingFacade;

    @Override
    public SourcingType getSourcingType() {
        return SourcingType.Zhao;
    }

    @RequestMapping(value = "/page")
    @Override
    public RestResponse<PageInfo<SourcingVo>> page(@RequestBody SourcingQueryReq query) {
        OperatorDO operatorDO = getLogin();
        if(operatorDO.getType().equals(AccountTypeEnum.EXPERT_ACCOUNT.getCode())){
            query.setStatus(SourcingStatus.bid_chosing.getValue());
        }
        RestResponse<PageInfo<SourcingVo>> response = super.page(query);
        if(response.isSuccess()){
            response.getData().getList().forEach(s->s.button(operatorDO));
        }
        return response;
    }

    @RequestMapping(value = "/detail")
    @Override
    public RestResponse<SourcingVo> detail(@RequestBody ByIdLoginRestReq query) {
        RestResponse<SourcingVo> response = super.detail(query);
        if(response.isSuccess()){
            OperatorDO operatorDO = getLogin();
            response.getData().button(operatorDO);
        }
        return response;
    }

}
