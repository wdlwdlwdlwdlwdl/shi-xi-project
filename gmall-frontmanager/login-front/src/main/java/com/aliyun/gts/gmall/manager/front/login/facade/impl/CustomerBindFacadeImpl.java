package com.aliyun.gts.gmall.manager.front.login.facade.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.aliyun.gts.gmall.center.user.api.dto.input.CustomerBindThirdAccountReq;
import com.aliyun.gts.gmall.center.user.api.dto.output.CustomerLoginAccountInfoDTO;
import com.aliyun.gts.gmall.center.user.api.enums.CheckReceivePointTypeEnum;
import com.aliyun.gts.gmall.center.user.api.enums.ThirdCustomerAccountTypeEnum;
import com.aliyun.gts.gmall.center.user.common.enums.MobileRegularExpEnum;
import com.aliyun.gts.gmall.framework.api.consts.ChannelEnum;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.common.config.GmallFrontConfig;
import com.aliyun.gts.gmall.manager.front.common.exception.FrontManagerException;
import com.aliyun.gts.gmall.manager.front.login.adaptor.CustomerBindAdaptor;
import com.aliyun.gts.gmall.manager.front.login.adaptor.LoginAdaptor;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.AligenieAuthorizationInfo;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.BindCustomerQuery;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.BindCustomerReq;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.UnbindCustomerReq;
import com.aliyun.gts.gmall.manager.front.login.dto.output.BindCustomerResult;
import com.aliyun.gts.gmall.manager.front.login.dto.output.PhoneLoginResult;
import com.aliyun.gts.gmall.manager.front.login.dto.utils.LoginFrontResponseCode;
import com.aliyun.gts.gmall.manager.front.login.facade.CustomerBindFacade;
import com.aliyun.gts.gmall.manager.front.login.facade.LoginFacade;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerAccountTypeEnum;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author alice
 * @version 1.0.0
 * @createTime 2022年09月22日 15:07:00
 */
@Slf4j
@Service
public class CustomerBindFacadeImpl implements CustomerBindFacade {

    @Autowired
    private GmallFrontConfig gmallFrontConfig;

    @Resource
    private CustomerBindAdaptor customerBindAdaptor;

    @Autowired
    private LoginFacade loginFacade;
    @Autowired
    LoginAdaptor loginAdaptor;
    @Override
    public PhoneLoginResult bindCustomerRelation(BindCustomerReq customerReq) {
        CustomerBindThirdAccountReq req = getCustomerBindThirdAccountRequest(customerReq);
        checkPhone(req.getPhone());
        CustomerDTO customer = customerBindAdaptor.bindCustomerRelation(req);
        if (Objects.isNull(customer)) {
            throw new FrontManagerException(LoginFrontResponseCode.APP_BIND_FAIL);
        }
        /**
         * 授权登录以后，看一下是否有这个参数，没有的话， 去添加一下
         */
        loginAdaptor.updateRestPwdFeature(customer);
        PhoneLoginResult loginResult = getLoginResult(customer);
        //授权发放积分
        String alertMsg = loginFacade.grantIntegral(req.getPhone(), CheckReceivePointTypeEnum.NOLOGIN.getCode());
        loginResult.setMessage(alertMsg);
        return loginResult;
    }

    private void checkPhone(String phone) {
        if (!MobileRegularExpEnum.isMobileNumber(phone)) {
            throw new FrontManagerException(LoginFrontResponseCode.PHONE_FAIL);
        }
    }

    private CustomerBindThirdAccountReq getCustomerBindThirdAccountRequest(BindCustomerReq customerReq) {
        CustomerBindThirdAccountReq req = new CustomerBindThirdAccountReq();
        AligenieAuthorizationInfo aligenieAuthorizationInfo = getAligenieAuthorizationInfoByJwt(customerReq.getAuthorizationToken());
        req.setOpenId(aligenieAuthorizationInfo.getOpenId());
        req.setPhone(aligenieAuthorizationInfo.getPhoneNumber());
        req.setType(customerReq.getType());
        req.setUserName(aligenieAuthorizationInfo.getNickname());
        req.setHeadUrl(aligenieAuthorizationInfo.getAvatarUrl());
        return req;
    }

    /**
     *  解析jwt获取第三方用户信息
     * @Author: alice
     * @param authorizationToken:
     * @return
     */
    private AligenieAuthorizationInfo getAligenieAuthorizationInfoByJwt(String authorizationToken) {
        DecodedJWT jwt;
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(gmallFrontConfig.getTmallGenieJwtSecret())).build();
            // 信息验证
            jwt = verifier.verify(authorizationToken);
        } catch (Exception e) {
            log.error("解析jwt报错：", e);
            throw new FrontManagerException(LoginFrontResponseCode.APP_BIND_JWT_FAIL);
        }
        Claim claim = jwt.getClaim("authorizationToken");
        AligenieAuthorizationInfo aligenieAuthorizationInfo = claim.as(AligenieAuthorizationInfo.class);
        if (Objects.isNull(aligenieAuthorizationInfo)) {
            throw new FrontManagerException(LoginFrontResponseCode.APP_BIND_FAIL);
        }
        return aligenieAuthorizationInfo;
    }

    @Override
    public RestResponse<Boolean> unbindCustomerRelation(UnbindCustomerReq unbindCustomerReq) {
        return customerBindAdaptor.unbindCustomerRelation(unbindCustomerReq.getPhone(),
                unbindCustomerReq.getOpenId(),
                unbindCustomerReq.getType(),
                unbindCustomerReq.getCustId(),
                unbindCustomerReq.getChannel());
    }

    @Override
    public List<BindCustomerResult> queryCustomerLoginAccount(BindCustomerQuery bindCustomerQuery) {
        List<CustomerLoginAccountInfoDTO> accountList = customerBindAdaptor.queryCustomerLoginAccount(bindCustomerQuery.getCustId(), bindCustomerQuery.getType());
        if (CollectionUtil.isNotEmpty(accountList)) {
            return accountList.stream().map(dto -> {
                BindCustomerResult customerResult = new BindCustomerResult();
                customerResult.setOpenId(dto.getAccount());
                customerResult.setType(dto.getAccountType());
                customerResult.setTypeDesc(getAccountTypeDesc(dto.getAccountType()));
                customerResult.setNickName(dto.getNickName());
                return customerResult;
            }).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    private String getAccountTypeDesc(String type) {
        String desc = ThirdCustomerAccountTypeEnum.getDescByCode(type);
        if (StringUtils.isNotBlank(desc)) {
            return desc;
        }
        for (CustomerAccountTypeEnum e : CustomerAccountTypeEnum.values()) {
            if (type.equals(e.getCode())) {
                return e.getDesc();
            }
        }
        return "";
    }

    private PhoneLoginResult getLoginResult(CustomerDTO customerDTO) {
        return loginFacade.getLoginResult(customerDTO, ChannelEnum.H5);
    }
}
