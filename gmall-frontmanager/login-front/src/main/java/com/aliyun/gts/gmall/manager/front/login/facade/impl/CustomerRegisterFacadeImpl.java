package com.aliyun.gts.gmall.manager.front.login.facade.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.center.user.api.dto.input.QueryCustomerReq;
import com.aliyun.gts.gmall.center.user.api.enums.CheckReceivePointTypeEnum;
import com.aliyun.gts.gmall.center.user.api.enums.SecurityCodeType;
import com.aliyun.gts.gmall.center.user.api.facade.CustomerReadExtFacade;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.biz.constant.UserFeatures;
import com.aliyun.gts.gmall.manager.front.common.exception.FrontManagerException;
import com.aliyun.gts.gmall.manager.front.login.adaptor.LoginAdaptor;
import com.aliyun.gts.gmall.manager.front.login.adaptor.UserAdapter;
import com.aliyun.gts.gmall.manager.front.login.dto.input.command.RegisterRestCommand;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.SecurityCodeQuery;
import com.aliyun.gts.gmall.manager.front.login.dto.output.RegisterResult;
import com.aliyun.gts.gmall.manager.front.login.dto.output.SecurityCodeResult;
import com.aliyun.gts.gmall.manager.front.login.dto.utils.LoginFrontResponseCode;
import com.aliyun.gts.gmall.manager.front.login.dto.utils.RsaUtil;
import com.aliyun.gts.gmall.manager.front.login.facade.CustomerRegisterFacade;
import com.aliyun.gts.gmall.manager.front.login.facade.LoginFacade;
import com.aliyun.gts.gmall.platform.user.api.dto.contants.CustomerStatusEnum;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import com.aliyun.gts.gmall.platform.user.api.exception.UserResponseCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;


/**
 * @author alice
 * @version 1.0.0
 * @createTime 2022年09月21日 15:38:00
 */
@Slf4j
@Service
public class CustomerRegisterFacadeImpl implements CustomerRegisterFacade {

    @Autowired
    private UserAdapter userAdapter;

    @Autowired
    private LoginAdaptor loginAdaptor;

    @Autowired
    private LoginFacade loginFacade;

    @Autowired
    private CustomerReadExtFacade customerReadExtFacade;

    @Value("${front-manager.login.pwd.publickey:MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC7Ho4+jvIThQfm36HLzBQ6wCFN9dfgrMMBhMbmU1qkrdM73pZVzkgP3nleozT5wa26xIQui2r7OmEJ1SEkn7nt2ow1DcZHz7gZFuQTjdOCMOBu/4apTN3/z52hiCfl0f4Hut7moulsriTfzKK9RAX/tqBdCUQRkw1JYFTDD68YVwIDAQAB}")
    private String LOGIN_PUBLIC_KEY;
    @Value("${front-manager.login.pwd.privatekey:MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALsejj6O8hOFB+bfocvMFDrAIU311+CswwGExuZTWqSt0zvellXOSA/eeV6jNPnBrbrEhC6Lavs6YQnVISSfue3ajDUNxkfPuBkW5BON04Iw4G7/hqlM3f/PnaGIJ+XR/ge63uai6WyuJN/Mor1EBf+2oF0JRBGTDUlgVMMPrxhXAgMBAAECgYEAriPGn2Oq2A2jB/KxgZdgcDikBgi4Kki9hqlJ7co+/iki/4GsCRjbqaES9McQoyrjAoCmvyZSnpF7A4qMuKfJgIEk00JQ5SFyVK3ZdlPn/tonoUbbbVbQaiAqUXdRMfuV8yeppw04mb+kjD7AQYioqnWS7EBW7eQetoyRJlzY5zECQQDoBLKRtWqcBNawEphMKIihblNNr4V1G8B0DzmQ1yu4z5YRztMfSX/VDcuIbYEY7iTxCZuyxdRYPDHwTjZEGyuvAkEAznXP4Pk24YKacKom0j5w5xagFc0h+mm+HFaYev/7QBqIa4Wuxjmx5CHSFN5E5i9CKfZlhRqUrDjgWs8t0Ek/2QJAZkyr71LuDpyTjE6ml+3HUGY0lKwvS9NQp9uOpi45OONOZ8upisH9exL6Cs09fqCB6UUzQT+4wK7J1gqmIqFYQQJBAK7k9DVTSB9ewK+iJALELGZGL1RoklkMDKT64m6HvHJAR8I126lGJKDp2YoeA+WusPDVLojDzJ/cCopCqT+hXXkCQDr1EW7gXAky9Y7GnWNWalpLGJvg2SYD0+hqTiWKnC1Me0bi4Js20/3E+jT4J2bBb4/p9eKUuwyXO7yFjQ72Q18=}")
    private String LOGIN_PRIVATE_KEY;

    private static final Long TTL_MS = 5 * 60 * 1000L;

    @Override
    public SecurityCodeResult sendCustomerSecurityCode(SecurityCodeQuery req) {
        return userAdapter.sendSecurityCode(req.getPhone(), TTL_MS, SecurityCodeType.REGISTER.getCode());
    }

    @Override
    public RegisterResult customerRegister(RegisterRestCommand req) {
        RegisterResult registerResult = new RegisterResult();
        // 验证码校验
        userAdapter.checkSecurityCode(req.getPhone(), req.getSecurityCode(), SecurityCodeType.REGISTER.getCode(), true);

        CustomerDTO customerDTO = loginAdaptor.queryCustomerByPhone(req.getPhone());
        if (Objects.nonNull(customerDTO)) {
            throw new FrontManagerException(LoginFrontResponseCode.CUSTOMER_EXIST);
        }
        checkUserName(req.getUsername());
        try {
            req.setPassword( RsaUtil.decryptByPrivateKey(LOGIN_PRIVATE_KEY, req.getPassword()));
        } catch (Exception exception) {
            log.error("rsa decrypt error:", exception);
            throw new FrontManagerException(LoginFrontResponseCode.DECRYPT_PASSWORD_FAIL);
        }
        RegisterRestCommand.checkLen(req.getPassword(),8, 20, I18NMessageUtils.getMessage("password.format")+"8位以上且包含数字和字母");  //# "密码必须
        // 创建用户
        HashMap<String,String> features=new HashMap<>(1);
        features.put(UserFeatures.RESET_PWD,UserFeatures.RESET_PWD_Y);
        req.setFeatures(features);
        createCustomer(req);
        //发放积分
        String s = loginFacade.grantIntegral(req.getPhone(), CheckReceivePointTypeEnum.REGISTER.getCode());
        registerResult.setResult(Boolean.TRUE);
        registerResult.setMessage(s);
        // PC注册成功，不需要直接登录
        return registerResult;
    }

    private void checkUserName(String userName) {
        if (StringUtils.isNotBlank(userName)) {
            QueryCustomerReq req = new QueryCustomerReq();
            req.setUserName(userName);
            RpcResponse<CustomerDTO> customerByName = customerReadExtFacade.queryCustomerByName(req);
            if (customerByName.isSuccess()) {
                CustomerDTO customer = customerByName.getData();
                if (Objects.nonNull(customer)) {
                    throw new GmallException(UserResponseCodeEnum.USERNAME_DUPLICATED, new Object[]{customer.getUsername()});
                }
            }
        }
    }

    private void createCustomer(RegisterRestCommand req) {
        userAdapter.createCustomer(
                req.getPhone(),
                req.getUsername(),
                req.getPassword(),
                null,
                CustomerStatusEnum.NORMAL.getCode(),
                null,req.getFeatures());
    }

}
