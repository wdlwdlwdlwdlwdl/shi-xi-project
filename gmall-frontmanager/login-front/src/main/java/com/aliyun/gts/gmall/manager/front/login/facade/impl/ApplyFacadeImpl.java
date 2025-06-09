package com.aliyun.gts.gmall.manager.front.login.facade.impl;

import com.alibaba.fastjson.JSON;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.framework.server.util.PublicFileHttpUrl;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestCommand;
import com.aliyun.gts.gmall.manager.front.login.adaptor.LoginAdaptor;
import com.aliyun.gts.gmall.manager.front.login.adaptor.UserAdapter;
import com.aliyun.gts.gmall.manager.front.login.converter.ApplyConverter;
import com.aliyun.gts.gmall.manager.front.login.dto.input.command.ApplyRestCommand;
import com.aliyun.gts.gmall.manager.front.login.dto.input.command.RegisterRestCommand;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.SecurityCodeQuery;
import com.aliyun.gts.gmall.manager.front.login.dto.output.*;
import com.aliyun.gts.gmall.manager.front.login.dto.utils.LoginFrontResponseCode;
import com.aliyun.gts.gmall.manager.front.login.facade.ApplyFacade;
import com.aliyun.gts.gmall.platform.user.api.dto.common.CustomerApplyExtendDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.common.CustomerApplyResultExtendDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.contants.CustomerStatusEnum;
import com.aliyun.gts.gmall.platform.user.api.dto.contants.UserTagConstants;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ApplyFacadeImpl implements ApplyFacade {

    @Autowired
    private UserAdapter userAdapter;
    @Autowired
    private LoginAdaptor loginAdaptor;
    @Autowired
    private ApplyConverter applyConverter;

    @Autowired
    PublicFileHttpUrl publicFileHttpUrl;


    private static final String SECURITY_TYPE_REG = "b2bReg";
    private static final String SECURITY_TYPE_APPLY = "b2bApply";
    private static final Long TTL_MS = 5 * 60 * 1000L;
    private static final int INITIAL_CUST_LEVEL = 101;


    @Override
    public SecurityCodeResult sendRegSecurityCode(SecurityCodeQuery req) {
        return userAdapter.sendSecurityCode(req.getPhone(), TTL_MS, SECURITY_TYPE_REG);
    }

    @Override
    public SecurityCodeResult sendApplySecurityCode(SecurityCodeQuery req) {
        return userAdapter.sendSecurityCode(req.getPhone(), TTL_MS, SECURITY_TYPE_APPLY);
    }

    @Override
    public RegisterResult b2bCustomerRegister(RegisterRestCommand req) {
        // 验证码校验
        userAdapter.checkSecurityCode(req.getPhone(), req.getSecurityCode(), SECURITY_TYPE_REG, true);

        // 创建用户
        CustomerDTO customer = userAdapter.createCustomer(
                req.getPhone(),
                req.getUsername(),
                req.getPassword(),
                Arrays.asList(UserTagConstants.B2B),
                CustomerStatusEnum.APPLY_EMPTY.getCode(),
                INITIAL_CUST_LEVEL);

        // 登录
        String token = loginAdaptor.generateToken(customer, null);

        RegisterResult result = new RegisterResult();
        result.setPhone(customer.getPhone());
        result.setToken(token);
        return result;
    }

    @Override
    public void b2bCustomerApply(ApplyRestCommand req) {
        // 验证码校验
        userAdapter.checkSecurityCode(req.getPhone(), req.getVerifyCode(), SECURITY_TYPE_APPLY, true);

        CustomerDTO customer = userAdapter.queryById(req.getCustId());
        if (customer == null) {
            throw new GmallException(LoginFrontResponseCode.CUSTOMER_NOT_EXIST);
        }
        if (!CustomerStatusEnum.APPLY_EMPTY.getCode().toString().equals(customer.getStatus())
                && !CustomerStatusEnum.APPLY_REFUSED.getCode().toString().equals(customer.getStatus())
                && !CustomerStatusEnum.CLEAN_UP.getCode().toString().equals(customer.getStatus())) {
            throw new GmallException(LoginFrontResponseCode.CUSTOMER_APPLY_CANNOT_EDIT);
        }
        processPriPic(req);
        CustomerApplyExtendDTO ext = applyConverter.convert(req);
        String json = JSON.toJSONString(ext);
        Map<String, String> map = new HashMap<>();
        map.put(CustomerApplyExtendDTO.EXTEND_KEY, json);
        userAdapter.saveExtend(req.getCustId(), CustomerApplyExtendDTO.EXTEND_TYPE, map);
        userAdapter.updateStatus(req.getCustId(), CustomerStatusEnum.APPLY_APPROVING.getCode());
    }

    private void processPriPic(ApplyRestCommand command) {

        command.setContactIdCardFront(publicFileHttpUrl.httpToOssOrMinio(command.getContactIdCardFront()));
        command.setContactIdCardBack(publicFileHttpUrl.httpToOssOrMinio(command.getContactIdCardBack()));
        ApplyRestCommand.BusinessInfo businessInfo = command.getBusinessInfo();
        if (businessInfo != null) {
            businessInfo.setLegalIdCardBack(publicFileHttpUrl.httpToOssOrMinio(businessInfo.getLegalIdCardBack()));
            businessInfo.setLegalIdCardFront(publicFileHttpUrl.httpToOssOrMinio(businessInfo.getLegalIdCardFront()));
            businessInfo.setBusinessLicense(publicFileHttpUrl.httpToOssOrMinio(businessInfo.getBusinessLicense()));
        }

    }

    private void processPriPic(ApplyInfo applyInfo) {

        applyInfo.setContactIdCardFront(publicFileHttpUrl.getFileHttpUrl(applyInfo.getContactIdCardFront()));
        applyInfo.setContactIdCardBack(publicFileHttpUrl.getFileHttpUrl(applyInfo.getContactIdCardBack()));

        ApplyInfo.BusinessInfo business = applyInfo.getBusinessInfo();
        if (business != null) {
            business.setLegalIdCardBack(publicFileHttpUrl.getFileHttpUrl(business.getLegalIdCardBack()));
            business.setLegalIdCardFront(publicFileHttpUrl.getFileHttpUrl(business.getLegalIdCardFront()));
            business.setBusinessLicense(publicFileHttpUrl.getFileHttpUrl(business.getBusinessLicense()));
        }
    }

    @Override
    public ApplyQueryResult queryApply(LoginRestCommand req) {
        CustomerDTO customer = userAdapter.queryById(req.getCustId());
        if (customer == null) {
            throw new GmallException(LoginFrontResponseCode.CUSTOMER_NOT_EXIST);
        }

        String applyInfoValue = userAdapter.queryExtend(req.getCustId(),
                CustomerApplyExtendDTO.EXTEND_TYPE, CustomerApplyExtendDTO.EXTEND_KEY);
        String applyResultValue = userAdapter.queryExtend(req.getCustId(),
                CustomerApplyResultExtendDTO.EXTEND_TYPE, CustomerApplyResultExtendDTO.EXTEND_KEY);
        ApplyInfo applyInfo = JSON.parseObject(applyInfoValue, ApplyInfo.class);
        if (applyInfo != null) {
            processPriPic(applyInfo);
        }
        CustomerApplyResultExtendDTO applyResult = JSON.parseObject(applyResultValue, CustomerApplyResultExtendDTO.class);
        ApplyQueryResult r = new ApplyQueryResult();
        r.setApplyInfo(applyInfo);
        r.setCustomerStatus(customer.getStatus());
        if (applyResult != null) {
            r.setApproveMessage(applyResult.getMessage());
        }
        return r;
    }


    @Override
    public void cancelApply(LoginRestCommand req) {
        CustomerDTO customer = userAdapter.queryById(req.getCustId());
        if (customer == null) {
            throw new GmallException(LoginFrontResponseCode.CUSTOMER_NOT_EXIST);
        }
        if (!CustomerStatusEnum.APPLY_APPROVING.getCode().toString().equals(customer.getStatus())) {
            throw new GmallException(LoginFrontResponseCode.CUSTOMER_APPLY_CANNOT_EDIT);
        }
        userAdapter.updateStatus(req.getCustId(), CustomerStatusEnum.APPLY_EMPTY.getCode());
    }
}
