package com.aliyun.gts.gmall.manager.front.login.adaptor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import cn.hutool.core.util.StrUtil;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.tokenservice.enums.TokenSourceEnum;
import com.aliyun.gts.gmall.framework.tokenservice.model.Result;
import com.aliyun.gts.gmall.framework.tokenservice.query.TokenClaim;
import com.aliyun.gts.gmall.framework.tokenservice.query.TokenContent;
import com.aliyun.gts.gmall.framework.tokenservice.query.TokenQuery;
import com.aliyun.gts.gmall.framework.tokenservice.service.TokenService;
import com.aliyun.gts.gmall.manager.biz.constant.UserFeatures;
import com.aliyun.gts.gmall.manager.framework.common.DubboBuilder;
import com.aliyun.gts.gmall.manager.front.common.config.DatasourceConfig;
import com.aliyun.gts.gmall.manager.front.common.consts.DsIdConst;
import com.aliyun.gts.gmall.manager.front.common.exception.FrontManagerException;
import com.aliyun.gts.gmall.manager.front.login.dto.output.SecurityCodeResult;
import com.aliyun.gts.gmall.manager.front.login.dto.utils.LoginFrontResponseCode;
import com.aliyun.gts.gmall.platform.user.api.dto.input.BindCustPhoneCommand;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerAccountTypeEnum;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerByAccountQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerLoginAccountCommand;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerPwdCheckRpcRequest;
import com.aliyun.gts.gmall.platform.user.api.dto.input.CustomerQuery;
import com.aliyun.gts.gmall.platform.user.api.dto.input.UpdateCustomerCommand;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerLoginAccountDTO;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerWriteFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.testng.collections.Maps;

import static com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode.CUSTOMER_CENTER_ERROR;
import static com.aliyun.gts.gmall.manager.front.login.common.SecurityCodeType.LOGIN;
import static com.aliyun.gts.gmall.manager.front.login.dto.utils.LoginFrontResponseCode.CHECK_PASSWORD_FAIL;
import static com.aliyun.gts.gmall.manager.front.login.dto.utils.LoginFrontResponseCode.CUSTOMER_NOT_EXIST;
import static com.aliyun.gts.gmall.manager.front.login.dto.utils.LoginFrontResponseCode.CUSTOMER_RESET_PWD_FAIL;
import static com.aliyun.gts.gmall.manager.front.login.dto.utils.LoginFrontResponseCode.PHONE_BIND_FAIL;
import static com.aliyun.gts.gmall.manager.front.login.dto.utils.LoginFrontResponseCode.WECHAT_BIND_FAIL;

/**
 * 登录相关的操作
 *
 * @author tiansong
 * @date 2021-01-28
 */
@Service
@Slf4j
public class LoginAdaptor {

    @Autowired
    private CustomerReadFacade customerReadFacade;
    @Autowired
    private CustomerWriteFacade customerWriteFacade;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private DatasourceConfig datasourceConfig;
    @Autowired
    private UserAdapter userAdapter;


    private static final Long TTL_MS = 5 * 60 * 1000L;

    DubboBuilder builder = DubboBuilder.builder().logger(log).sysCode(CUSTOMER_CENTER_ERROR).build();

    /**
     * 发送验证码【强依赖】
     *
     * @param phone 手机号
     * @return 验证码发送成功/失败
     */
    public SecurityCodeResult sendSecurityCode(String phone, String sendType) {
        return userAdapter.sendSecurityCode(phone, TTL_MS, sendType);
    }

    /**
     * 校验手机和验证码是否匹配【弱依赖】
     *
     * @param phone 手机号
     * @param code  验证码
     * @return 成功：true/失败：false
     */
    public Boolean checkSecurityCode(String phone, String code, boolean needExpired) {
        return userAdapter.checkSecurityCode(phone, code, LOGIN, needExpired);
    }

    /**
     * 校验手机号与密码是否匹配 【强依赖】
     *
     * @param phone    手机号
     * @param password 密码
     * @return 成功：true/失败：false
     */
    public boolean checkPassword(String phone, String password) {
        CustomerPwdCheckRpcRequest customerPwdCheckRpcRequest = new CustomerPwdCheckRpcRequest();
        customerPwdCheckRpcRequest.setPhone(phone);
        customerPwdCheckRpcRequest.setPwd(password);
        return builder.create(datasourceConfig).id(DsIdConst.login_security_checkPwd).queryFunc(
            (Function<CustomerPwdCheckRpcRequest, RpcResponse<Boolean>>)request -> {
                RpcResponse<Boolean> rpcResponse = customerReadFacade.checkByPwd(request);
                if (rpcResponse.isSuccess() && rpcResponse.getData()) {
                    return RpcResponse.ok(Boolean.TRUE);
                }
                return rpcResponse.isSuccess() ? RpcResponse.fail(CHECK_PASSWORD_FAIL)
                    : RpcResponse.fail(rpcResponse.getFail());
            }).bizCode(CHECK_PASSWORD_FAIL).query(customerPwdCheckRpcRequest);
    }

    /**
     * 通过手机号获取用户信息
     *
     * @param phone 手机号
     * @return 用户信息
     */
    public CustomerDTO queryCustomerByPhone(String phone) {
        CustomerQuery customerQuery = new CustomerQuery();
        customerQuery.setPhone(phone);
        return builder.create(datasourceConfig).id(DsIdConst.login_customer_queryByPhone).queryFunc(
            (Function<CustomerQuery, RpcResponse<CustomerDTO>>)request -> {
                RpcResponse<PageInfo<CustomerDTO>> rpcResponse = customerReadFacade.queryByCustInfo(request);
                if (!rpcResponse.isSuccess()) {
                    return RpcResponse.fail(rpcResponse.getFail());
                }
                if (rpcResponse.getData() == null || rpcResponse.getData().isEmpty()) {
                    return RpcResponse.OK_VOID;
                }
                return RpcResponse.ok(rpcResponse.getData().getList().get(0));
            }).bizCode(CUSTOMER_NOT_EXIST).query(customerQuery);
    }


    /**
     * 通过手机号获取用户信息
     *
     * @param iin Halykbank的cas对应的唯一标识
     * @return 用户信息
     */
    public CustomerDTO queryCustomerByPrimary(String iin) {
        CustomerQuery customerQuery = new CustomerQuery();
        customerQuery.setCustPrimary(iin);
        return builder.create(datasourceConfig).id(DsIdConst.login_customer_queryByPhone).queryFunc(
            (Function<CustomerQuery, RpcResponse<CustomerDTO>>)request -> {
                RpcResponse<PageInfo<CustomerDTO>> rpcResponse = customerReadFacade.queryByCustInfo(request);
                if (!rpcResponse.isSuccess()) {
                    return RpcResponse.fail(rpcResponse.getFail());
                }
                if (rpcResponse.getData() == null || rpcResponse.getData().isEmpty()) {
                    return RpcResponse.OK_VOID;
                }
                return RpcResponse.ok(rpcResponse.getData().getList().get(0));
            }).bizCode(CUSTOMER_NOT_EXIST).query(customerQuery);
    }

    public boolean resetPwd(String phone, String password) {

        CustomerDTO customerDTO = this.queryCustomerByPhone(phone);

        UpdateCustomerCommand customerWriteCommand = new UpdateCustomerCommand();

        customerWriteCommand.setId(customerDTO.getId());
        customerWriteCommand.setPhone(phone);
        customerWriteCommand.setPwd(password);

        return builder.create(datasourceConfig).id(DsIdConst.customer_base_update).queryFunc(
            (Function<UpdateCustomerCommand, RpcResponse<Boolean>>)request -> {
                RpcResponse<CustomerDTO> rpcResponse = customerWriteFacade.update(request);
                if (rpcResponse.isSuccess() && rpcResponse.getData() != null) {
                    return RpcResponse.ok(Boolean.TRUE);
                }
                return RpcResponse.fail(rpcResponse.getFail());
            }).bizCode(CUSTOMER_RESET_PWD_FAIL).query(customerWriteCommand);

    }

    /**
     * 创建用户信息
     *
     * @param phone  用户手机号码，必填
     * @param openId 微信小程序唯一标识【非必填】
     * @return 用户信息
     */
    public CustomerDTO createCustomer(String phone, String openId) {
        CustomerDTO customerDTO = userAdapter.createCustomer(phone, phone, null);
        // 新创建用户默认绑定手机号;绑定失败后，可通过用户设置手动绑定
        this.bindPhone(customerDTO.getId(), phone);
        if (openId != null) {
            this.bindWxMini(customerDTO.getId(), openId);
        }
        return customerDTO;
    }

    /**
     * 创建用户后，默认绑定手机号【弱依赖】
     * 默认绑定失败后，可通过用户设置重新绑定
     *
     * @param custId 用户ID
     * @param phone  手机号码
     */
    private Boolean bindPhone(Long custId, String phone) {
        BindCustPhoneCommand command = BindCustPhoneCommand.builder().custId(custId).phone(phone).build();
        return builder.create(datasourceConfig).id(DsIdConst.login_customer_bindPhone).queryFunc(
            (Function<BindCustPhoneCommand, RpcResponse<Boolean>>)request -> {
                RpcResponse<Boolean> rpcResponse = customerWriteFacade.bindPhone(request);
                if (rpcResponse.isSuccess() && rpcResponse.getData()) {
                    return RpcResponse.ok(Boolean.TRUE);
                }
                return rpcResponse.isSuccess() ? RpcResponse.fail(PHONE_BIND_FAIL) : RpcResponse.fail(
                    rpcResponse.getFail());
            }).strong(Boolean.FALSE).query(command);
    }

    /**
     * 创建登录TOKEN【强依赖】
     *
     * @param customerDTO 用户信息
     * @return token
     */
    public String generateToken(CustomerDTO customerDTO, String casKey) {
        try {
            Result<String> result = tokenService.generateTokenWithCacheInfo(this.buildQuery(customerDTO, casKey));
            if (result.isSuccess() && result.getModel() != null) {
                return result.getModel();
            }
        } catch (Exception e) {
            log.error("LoginAdaptor.generateToken error:", e);
        }
        throw new FrontManagerException(LoginFrontResponseCode.LOGIN_FAIL);
    }

    /**
     * 构建token
     * @param customerDTO
     * @return
     */
    private TokenQuery buildQuery(CustomerDTO customerDTO, String casKey) {
        TokenQuery tokenQuery = new TokenQuery();
        tokenQuery.setTokenSource(TokenSourceEnum.TO_C_MEMBER.getTokenSource());
        tokenQuery.setLoginName(customerDTO.getUsername());
        TokenContent tokenContent = new TokenContent();
        tokenContent.setSubject(String.valueOf(customerDTO.getId()));
        TokenClaim tokenClaim = new TokenClaim();
        tokenClaim.setCustNo(String.valueOf(customerDTO.getId()));
        tokenContent.setTokenClaim(tokenClaim);
        tokenQuery.setTokenContent(tokenContent);
        Map<String, Object> tokenCacheMap = Maps.newHashMap();
        tokenQuery.setTokenCacheMap(tokenCacheMap);
        tokenCacheMap.put("head_url", customerDTO.getHeadUrl() == null ? "" : customerDTO.getHeadUrl());
        if (StrUtil.isNotEmpty(casKey)){
            tokenCacheMap.put("casKey", casKey);
        }
        if (customerDTO.getId() != null) {
            tokenCacheMap.put("custId", customerDTO.getId());
        }
        if (StrUtil.isNotEmpty(customerDTO.getPhone())) {
            tokenCacheMap.put("phone", customerDTO.getPhone());
        }
        if (StrUtil.isNotEmpty(customerDTO.getNickname())) {
            tokenCacheMap.put("nick", customerDTO.getNickname());
        }
        if (StrUtil.isNotEmpty(customerDTO.getIin())) {
            tokenCacheMap.put("iin", customerDTO.getIin());
        }
        if (StrUtil.isNotEmpty(customerDTO.getFirstName())) {
            tokenCacheMap.put("firstName", customerDTO.getFirstName());
        }
        if (StrUtil.isNotEmpty(customerDTO.getLastName())) {
            tokenCacheMap.put("lastName", customerDTO.getLastName());
        }
        if (StrUtil.isNotEmpty(customerDTO.getMiddleName())) {
            tokenCacheMap.put("middleName", customerDTO.getMiddleName());
        }
        if (customerDTO.getBirthDay() != null) {
            tokenCacheMap.put("birthday", customerDTO.getBirthDay());
        }
        if (StrUtil.isNotEmpty(customerDTO.getCustPrimary())) {
            tokenCacheMap.put("custPrimary", customerDTO.getCustPrimary());
        }
        if (StrUtil.isNotEmpty(customerDTO.getLanguage())) {
            tokenCacheMap.put("language", customerDTO.getLanguage());
        }
        if (StrUtil.isNotEmpty(customerDTO.getCasLoginToken())){
            tokenCacheMap.put("casLoginToken", customerDTO.getCasLoginToken());
        }
        if (StrUtil.isNotEmpty(customerDTO.getThirdAccounts())){
            tokenCacheMap.put("accountId", customerDTO.getThirdAccounts());
        }
        return tokenQuery;
    }

    /**
     * 删除token
     * @param oldToken
     * @return
     */
    public Boolean removeToken(String oldToken) {
        try {
            Result<Boolean> result = tokenService.removeToken(oldToken, TokenSourceEnum.TO_C_MEMBER.getTokenSource());
            return result.isSuccess() && result.getModel();
        } catch (Exception e) {
            log.error("LoginAdaptor.removeToken error:", e);
        }
        return Boolean.FALSE;
    }

    /**
     * 查询微信小程序的用户信息【强依赖】
     *
     * @param openId 微信小程序唯一标识
     * @return 用户信息
     */
    public CustomerDTO queryCustomerByOpenId(String openId) {
        CustomerByAccountQuery customerByAccountQuery = new CustomerByAccountQuery();
        customerByAccountQuery.setAccountType(CustomerAccountTypeEnum.WECHAT.getCode());
        customerByAccountQuery.setAccount(openId);
        return builder.create(datasourceConfig).id(DsIdConst.login_customer_queryByOpenId).queryFunc(
            (Function<CustomerByAccountQuery, RpcResponse<CustomerDTO>>)request -> {
                RpcResponse<CustomerDTO> rpcResponse = customerReadFacade.queryByAccount(request);
                if (rpcResponse.isSuccess()) {
                    return RpcResponse.ok(rpcResponse.getData());
                }
                return RpcResponse.fail(rpcResponse.getFail());
            }).bizCode(CUSTOMER_NOT_EXIST).query(customerByAccountQuery);
    }

    /**
     * 绑定微信小程序【强依赖】
     *
     * @param custId 用户ID
     * @param openId 微信ID
     * @return 是否成功
     */
    public Boolean bindWxMini(long custId, String openId) {
        CustomerLoginAccountCommand command = new CustomerLoginAccountCommand();
        command.setCustId(custId);
        command.setAccountType(CustomerAccountTypeEnum.WECHAT.getCode());
        command.setAccount(openId);
        return builder.create(datasourceConfig).id(DsIdConst.login_customer_bindWeiXin).queryFunc(
            (Function<CustomerLoginAccountCommand, RpcResponse<Boolean>>)request -> {
                RpcResponse<CustomerLoginAccountDTO> rpcResponse = customerWriteFacade.bindThirdAccount(request);
                if (rpcResponse.isSuccess() && rpcResponse.getData() != null) {
                    return RpcResponse.ok(Boolean.TRUE);
                }
                return rpcResponse.isSuccess() ? RpcResponse.fail(WECHAT_BIND_FAIL) : RpcResponse.fail(
                    rpcResponse.getFail());
            }).bizCode(WECHAT_BIND_FAIL).query(command);
    }
    /**
     * 更新 RESET_PWD_N
     * @return
     */
    public void updateRestPwdFeature(CustomerDTO customer){
        if(customer!=null) {
            Map<String, String> features = customer.getFeatures();
            if(features==null){
                features = new HashMap<>();
            }
            if(!features.containsKey(UserFeatures.RESET_PWD)) {
                features.put(UserFeatures.RESET_PWD, UserFeatures.RESET_PWD_N);
                this.updateFeature(customer.getId(), features);
            }
        }
    }

    public boolean updateFeature(Long id,Map<String, String> features){
        UpdateCustomerCommand customerWriteCommand = new UpdateCustomerCommand();
        customerWriteCommand.setId(id);
        customerWriteCommand.setFeatures(features);
        return builder.create(datasourceConfig).id(DsIdConst.customer_base_update).queryFunc(
                (Function<UpdateCustomerCommand, RpcResponse<Boolean>>)request -> {
                    RpcResponse<CustomerDTO> rpcResponse = customerWriteFacade.update(request);
                    if (rpcResponse.isSuccess() && rpcResponse.getData() != null) {
                        return RpcResponse.ok(Boolean.TRUE);
                    }
                    return RpcResponse.fail(rpcResponse.getFail());
                }).bizCode(CUSTOMER_RESET_PWD_FAIL).query(customerWriteCommand);
    }

}