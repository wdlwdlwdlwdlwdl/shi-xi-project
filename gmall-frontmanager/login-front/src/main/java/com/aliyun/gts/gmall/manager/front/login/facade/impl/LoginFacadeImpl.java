package com.aliyun.gts.gmall.manager.front.login.facade.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.core.codec.Base64Encoder;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.aliyun.gts.gmall.center.user.api.dto.output.CheckReceivePointDTO;
import com.aliyun.gts.gmall.center.user.api.enums.CheckReceivePointTypeEnum;
import com.aliyun.gts.gmall.framework.api.consts.ChannelEnum;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.framework.tokenservice.model.Result;
import com.aliyun.gts.gmall.framework.tokenservice.service.TokenService;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.manager.biz.constant.UserFeatures;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.MessageSendManager;
import com.aliyun.gts.gmall.manager.front.common.consts.BizConst;
import com.aliyun.gts.gmall.manager.front.common.dto.LoginRestQuery;
import com.aliyun.gts.gmall.manager.front.common.exception.FrontManagerException;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.customer.CustomerVO;
import com.aliyun.gts.gmall.manager.front.customer.facade.CustomerFacade;
import com.aliyun.gts.gmall.manager.front.login.adaptor.CheckReceivePointAdaptor;
import com.aliyun.gts.gmall.manager.front.login.adaptor.LoginAdaptor;
import com.aliyun.gts.gmall.manager.front.login.adaptor.ThirdAdapter;
import com.aliyun.gts.gmall.manager.front.login.adaptor.UserAdapter;
import com.aliyun.gts.gmall.manager.front.login.dto.input.command.LogoutRestCommand;
import com.aliyun.gts.gmall.manager.front.login.dto.input.command.ResetPwdRestCommand;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.*;
import com.aliyun.gts.gmall.manager.front.login.dto.output.*;
import com.aliyun.gts.gmall.manager.front.login.dto.utils.LoginUtils;
import com.aliyun.gts.gmall.manager.front.login.facade.LoginFacade;
import com.aliyun.gts.gmall.manager.utils.JsonUtils;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.middleware.api.cache.lock.DistributedLock;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.input.CasLoginRpcReq;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.input.CasUserRpcReq;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.output.CasTokenBaseDto;
import com.aliyun.gts.gmall.platform.open.customized.api.dto.output.CasUserBaseDto;
import com.aliyun.gts.gmall.platform.open.customized.api.facade.CommonHttpRequestFacade;
import com.aliyun.gts.gmall.platform.open.customized.common.constants.HttpSystemCostants;
import com.aliyun.gts.gmall.platform.promotion.common.util.DateUtils;
import com.aliyun.gts.gmall.platform.user.api.dto.common.FrontManagerVisitMessage;
import com.aliyun.gts.gmall.platform.user.api.dto.contants.CustomerStatusEnum;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.aliyun.gts.gmall.manager.front.login.dto.utils.LoginFrontResponseCode.*;

/**
 * 登录模块
 *
 * @author tiansong
 */
@Slf4j
@Service
public class LoginFacadeImpl implements LoginFacade {

    @Value("${front-manager.login.imageCode.disable:false}")
    private Boolean imageCodeDisable;
    @Value("${front-manager.message.grantpoint.topic:}")
    private String grantpointTopic;
    @NacosValue(value = "${front-manager.message.visit.topic}", autoRefreshed = true)
    @Value("${front-manager.message.visit.topic:}")
    private String frontVisitMessageTopic;

    @Resource
    private LoginAdaptor loginAdaptor;

    @Resource
    private ThirdAdapter thirdAdapter;

    @Resource
    @Qualifier("cacheManager")
    private CacheManager cacheManager;

    @Resource
    private UserAdapter userAdapter;

    @Resource
    private CommonHttpRequestFacade commonHttpRequestFacade;

    @Autowired
    private MessageSendManager messageSendManager;

    @Resource
    private CheckReceivePointAdaptor checkReceivePointAdaptor;

    @Autowired
    private CustomerFacade customerFacade;

    @Resource
    private TokenService tokenService;

    /**
     * 登录图片验证码前缀
     */
    private static final String LOGIN_SHEAR_CAPTCHA_PREFIX = "front:login:CAPTCHA:";

    /**
     * 登录标识缓存前缀
     */
    private static final String LOGIN_PREFIX = "Login";

    @NacosValue(value = "${front-manager.promotion.newuser.limit.day:30}", autoRefreshed = true)
    private Integer limit;

    @Value("${halyk.caslogin.mock.switch:N}")
    private String casLoginMockSwitch;

    @Override
    public SecurityCodeResult sendSecurityCode(SecurityCodeQuery securityCodeQuery) {
        if (securityCodeQuery.isCheckUserExist()) {
            CustomerDTO cust = loginAdaptor.queryCustomerByPhone(securityCodeQuery.getPhone());
            if (cust == null) {
                throw new FrontManagerException(CUSTOMER_NOT_EXIST);
            }
        }
        return loginAdaptor.sendSecurityCode(securityCodeQuery.getPhone(),securityCodeQuery.getSendType());
    }

    @Override
    public PhoneLoginResult phoneLogin(PhoneLoginQuery phoneLoginQuery) {
        // 0. 校验图片验证码
        checkVerifyCode(phoneLoginQuery.getImageKey(), phoneLoginQuery.getImageValue());
        // 1. 通过手机号码查询用户信息
        CustomerDTO customerDTO = loginAdaptor.queryCustomerByPhone(phoneLoginQuery.getPhone());
        // 2. 用户不存在，新建用户信息
        if (customerDTO == null) {
            customerDTO = createCustomer(phoneLoginQuery);
            //如果新成功注册后发放积分
            if (customerDTO != null) {
                grantIntegral(customerDTO.getPhone(), CheckReceivePointTypeEnum.REGISTER.getCode());
            }
        }
        // 3. 登录验证
        this.phoneLoginCheck(phoneLoginQuery);

        PhoneLoginResult result = this.getLoginResult(customerDTO, ChannelEnum.H5);
        if (customerDTO != null) {
            result.setRestPwd(isResetPwd(customerDTO));
            result.setB2bSettleIn(isB2bSettleIn(customerDTO));
        }
        return result;
    }


    /**
     * Cas登录
     *
     * @param casLoginQuery
     * @return
     */
    @Override
    public RestResponse<CasLoginResult> casLogin(CasLoginQuery casLoginQuery) {
        CasLoginResult casLoginResult = null;
        if ("Y".equals(casLoginMockSwitch)) {
            String cacheToken = cacheManager.get(casLoginQuery.getCasKey());
            if (StringUtils.isNotBlank(cacheToken)) {
                Result<Map<String, Object>> result = tokenService.getTokenInfo(cacheToken);
                log.info("casLoginMockSwitch开启,获取到的token结果{}", result.toString());
                Map<String, Object> custInfo = result.getModel();
                if (custInfo == null || custInfo.isEmpty()) {
                    return null;
                }
                String str = JsonUtils.toJSONString(custInfo);
                CustDTO custDTO = JsonUtils.toJavaBean(str, CustDTO.class);
                casLoginResult = new CasLoginResult();
                casLoginQuery.setCustId(custDTO.getCustId());
                casLoginResult.setToken(cacheToken);
                casLoginResult.setLanguage(custDTO.getLanguage());
                // 发成长值
                FrontManagerVisitMessage visitMessage = FrontManagerVisitMessage.builder().custId(custDTO.getCustId()).visitEndpoint("").time(new Date()).build();
                messageSendManager.sendMessage(visitMessage, frontVisitMessageTopic, "VISIT");
                return RestResponse.ok(fillCasLoginResult(casLoginQuery, casLoginResult));
            }
        }
        //获取的cas远程请求登录 获取token
        CasLoginRpcReq casLoginRpcReq = new CasLoginRpcReq();
        casLoginRpcReq.setSystem(HttpSystemCostants.MEMBER_LOGIN_SYSTEM);
        casLoginRpcReq.setInter(HttpSystemCostants.MEMBER_LOGIN_INTER);
        casLoginRpcReq.setCasKey(casLoginQuery.getCasKey());
        RpcResponse<CasTokenBaseDto> casLoginRpcResponse = commonHttpRequestFacade.casLogin(casLoginRpcReq);
        if (Boolean.FALSE.equals(casLoginRpcResponse.isSuccess()) && null == casLoginRpcResponse.getData()) {
            return RestResponse.ok(fillCasLoginResult(casLoginQuery, casLoginResult));
        }
        // 认证token
        CasTokenBaseDto casTokenBaseDto = casLoginRpcResponse.getData();
        // 通过token 获取用户信息
        CasUserRpcReq casUserRpcReq = new CasUserRpcReq();
        casUserRpcReq.setSystem(HttpSystemCostants.MEMBER_INFO_SYSTEM);
        casUserRpcReq.setInter(HttpSystemCostants.MEMBER_INFO_INTER);
        casUserRpcReq.setAccessToken(casTokenBaseDto.getAccessToken());
        //获取用户信息
        RpcResponse<CasUserBaseDto> casUserRpcResponse = commonHttpRequestFacade.casUser(casUserRpcReq);
        if (Boolean.FALSE.equals(casLoginRpcResponse.isSuccess()) && null == casLoginRpcResponse.getData()) {
            return RestResponse.ok(fillCasLoginResult(casLoginQuery, casLoginResult));
        }
        CasUserBaseDto casUserBaseDto = casUserRpcResponse.getData();
        if (StringUtils.isEmpty(casUserBaseDto.getIin())) {
            //iin作为唯一标识，不能为空
            throw new FrontManagerException(IIN_IS_NULL);
        }
        CustomerDTO customerDTO = loginAdaptor.queryCustomerByPrimary(casUserBaseDto.getIin());
        //用户不存在，则直接注册
        if (customerDTO == null) {
            customerDTO = userAdapter.createCustomer(
                casUserBaseDto.getIin(), casUserBaseDto.getUserId(),
                casUserBaseDto.getBirthDay(), casUserBaseDto.getFirstName(),
                casUserBaseDto.getLastName(), casUserBaseDto.getMiddleName(),
                casUserBaseDto.getPhone(), casLoginQuery.getLanguage()
            );
            //如果新成功注册后发放积分
            if (customerDTO != null) {
                grantIntegralByPrimary(customerDTO.getIin(), CheckReceivePointTypeEnum.REGISTER.getCode());
            }
        } else {
            //对比信息，如果有变更，需要更新
            boolean hasChange = false;
            if ((StringUtils.isNotEmpty(customerDTO.getFirstName()) && StringUtils.isEmpty(casUserBaseDto.getFirstName())) ||
                (StringUtils.isEmpty(customerDTO.getFirstName()) && StringUtils.isNotEmpty(casUserBaseDto.getFirstName())) ||
                (StringUtils.isNotEmpty(customerDTO.getFirstName()) && StringUtils.isNotEmpty(casUserBaseDto.getFirstName()) &&
                    !customerDTO.getFirstName().equals(casUserBaseDto.getFirstName()))) {
                hasChange = true;
            }
            if ((StringUtils.isNotEmpty(customerDTO.getMiddleName()) && StringUtils.isEmpty(casUserBaseDto.getMiddleName())) ||
                (StringUtils.isEmpty(customerDTO.getMiddleName()) && StringUtils.isNotEmpty(casUserBaseDto.getMiddleName())) ||
                (StringUtils.isNotEmpty(customerDTO.getMiddleName()) && StringUtils.isNotEmpty(casUserBaseDto.getMiddleName()) &&
                    !customerDTO.getMiddleName().equals(casUserBaseDto.getMiddleName()))) {
                hasChange = true;
            }
            if ((StringUtils.isNotEmpty(customerDTO.getLastName()) && StringUtils.isEmpty(casUserBaseDto.getLastName())) ||
                (StringUtils.isEmpty(customerDTO.getLastName()) && StringUtils.isNotEmpty(casUserBaseDto.getLastName())) ||
                (StringUtils.isNotEmpty(customerDTO.getLastName()) && StringUtils.isNotEmpty(casUserBaseDto.getLastName()) &&
                    !customerDTO.getLastName().equals(casUserBaseDto.getLastName()))) {
                hasChange = true;
            }
            if ((customerDTO.getBirthDay() != null && casUserBaseDto.getBirthDay() == null) ||
                (customerDTO.getBirthDay() == null && casUserBaseDto.getBirthDay() != null) ||
                (customerDTO.getBirthDay() != null && casUserBaseDto.getBirthDay() != null &&
                    customerDTO.getBirthDay().compareTo(casUserBaseDto.getBirthDay()) != 0)) {
                hasChange = true;
            }
            if (StringUtils.isNotEmpty(customerDTO.getThirdAccounts()) &&
                StringUtils.isNotEmpty(casUserBaseDto.getUserId()) &&
                !customerDTO.getThirdAccounts().equals(casUserBaseDto.getUserId())) {
                hasChange = true;
            }
            if (!StringUtils.equals(customerDTO.getPhone(), casUserBaseDto.getPhone())) {
                hasChange = true;
            }
            if (hasChange) {
                //如果有变更，更新
                userAdapter.updateCustomer(
                    customerDTO.getId(), casUserBaseDto.getUserId(),
                    casUserBaseDto.getBirthDay(), casUserBaseDto.getFirstName(),
                    casUserBaseDto.getLastName(), casUserBaseDto.getMiddleName(),
                    casUserBaseDto.getPhone()
                );
                // 修改后重新查询下 否则还是旧的数据
                customerDTO = loginAdaptor.queryCustomerByPrimary(casUserBaseDto.getIin());
            }
        }
        // 入参语言和数据库语言比价， 不一样修改语言
        if (StringUtils.isNotEmpty(casLoginQuery.getLanguage())){
            customerDTO.setLanguage(casLoginQuery.getLanguage());
        }
        casLoginQuery.setCustId(customerDTO.getId());
        customerDTO.setCasLoginToken(casTokenBaseDto.getAccessToken());
        // 1. 通过用户信息创建登录token
        String token = loginAdaptor.generateToken(customerDTO, casLoginQuery.getCasKey());
        casLoginResult = new CasLoginResult();
        casLoginResult.setToken(token);
        casLoginResult.setLanguage(customerDTO.getLanguage());
        // 2. 设置token的失效时长
        cacheManager.expire(casTokenBaseDto.getAccessToken(), casTokenBaseDto.getExpiresIn(), TimeUnit.SECONDS);
        try {
            if (casLoginMockSwitch.equals("Y")){
                cacheManager.set(casLoginQuery.getCasKey(), token, casTokenBaseDto.getExpiresIn(), TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            log.error("casLoginMockSwitch.error" + e.getMessage(), e);
        }
        // 3. 同登录端，再次登录后互踢下线   这个地方暂时先注释掉吧，后边有校验token合法性的，会报错
        this.refreshToken(customerDTO.getId(), ChannelEnum.H5, casTokenBaseDto.getAccessToken());
        // 发成长值
        FrontManagerVisitMessage visitMessage = FrontManagerVisitMessage.builder().custId(customerDTO.getId()).visitEndpoint("").time(new Date()).build();
        messageSendManager.sendMessage(visitMessage, frontVisitMessageTopic, "VISIT");
        // 返回结果处理
        return RestResponse.ok(fillCasLoginResult(casLoginQuery, casLoginResult));
    }

    /**
     * 返回结果处理
     * @param casLoginQuery
     * @param casLoginResult
     * @return
     */
    private CasLoginResult fillCasLoginResult(CasLoginQuery casLoginQuery, CasLoginResult casLoginResult) {
        //如果不是首次访问将当前的用户信息直接放进去
        if (null != casLoginQuery.getCustId()) {
            if (null == casLoginResult) {
                casLoginResult = new CasLoginResult();
            }
            LoginRestQuery loginRestQuery = new LoginRestQuery();
            loginRestQuery.setCustId(casLoginQuery.getCustId());
            CustomerVO customerVO = customerFacade.queryById(loginRestQuery);
            casLoginResult.setCurrentUser(customerVO);
        }
        return casLoginResult;
    }


    private boolean isResetPwd(CustomerDTO customerDTO) {
        if (customerDTO.getFeatures() != null && customerDTO.getFeatures().containsKey(UserFeatures.RESET_PWD)) {
            //feature里如果有标志位，标记已经重置过密码了，则不需要重置密码
            String restPwdSigh = customerDTO.getFeatures().get(UserFeatures.RESET_PWD);
            if (UserFeatures.RESET_PWD_Y.equalsIgnoreCase(restPwdSigh)) {
                return false;
            }
            return true;
        }
        return customerDTO.getPwdYn() == null || !customerDTO.getPwdYn();
    }

    private boolean isB2bSettleIn(CustomerDTO customerDTO) {
        return customerDTO.isB2b() && !CustomerStatusEnum.NORMAL.getCode()
                .toString().equals(customerDTO.getStatus());
    }

    /**
     * 手机登录验证
     *
     * @param phoneLoginQuery
     */
    private void phoneLoginCheck(PhoneLoginQuery phoneLoginQuery) {
        // 3.1 验证用户的验证码，并失效
        if (StringUtils.isNotBlank(phoneLoginQuery.getSecurityCode())) {
            // TODO 验证码重新对接
            //loginAdaptor.checkSecurityCode(phoneLoginQuery.getPhone(), phoneLoginQuery.getSecurityCode(), Boolean.TRUE);
        }
        // 3.2 验证用户的密码
        if (StringUtils.isNotBlank(phoneLoginQuery.getPassword()) && !loginAdaptor.checkPassword(
            phoneLoginQuery.getPhone(), phoneLoginQuery.getPassword())) {
            throw new FrontManagerException(LOGIN_FAIL);
        }
    }

    /**
     * 第一次手机验证码登录创建新用户
     *
     * @param phoneLoginQuery
     * @return
     */
    private CustomerDTO createCustomer(PhoneLoginQuery phoneLoginQuery) {
        CustomerDTO customerDTO;
        // 2.0 不自动创建的
        if (!phoneLoginQuery.isAutoRegister()) {
            throw new FrontManagerException(CUSTOMER_NOT_EXIST);
        }
        // 2.1 用户使用密码登录，则exception
        if (StringUtils.isBlank(phoneLoginQuery.getSecurityCode())) {
            throw new FrontManagerException(CUSTOMER_NOT_EXIST);
        }
        // 2.2 验证用户的验证码，机码不一致则exception
        // TODO 验证码重新对接
        // loginAdaptor.checkSecurityCode(phoneLoginQuery.getPhone(), phoneLoginQuery.getSecurityCode(), Boolean.FALSE);
        // 2.3 创建新的用户信息并获取customerId
        customerDTO = loginAdaptor.createCustomer(phoneLoginQuery.getPhone(), null);
        return customerDTO;
    }

    @Override
    public PhoneLoginResult wxMiniLogin(WxMiniLoginQuery query) {
        // 1. 根据微信小程序授权code获取，openId、手机号
        WxMiniLoginResult wxMiniLoginResult = thirdAdapter.getWxMiniLoginInfo(query.getCode());
        // 2. 查询用户信息
        CustomerDTO customerDTO = loginAdaptor.queryCustomerByOpenId(wxMiniLoginResult.getOpenId());
        // 3. 用户不存在，新建/绑定用户信息
        if (customerDTO == null && StringUtils.isNotBlank(query.getPhone())) {
            // 3.1 校验手机验证码
            // TODO 验证码重新对接
//            if (!loginAdaptor.checkSecurityCode(query.getPhone(), query.getSecurityCode(), true)) {
//                throw new FrontManagerException(LOGIN_FAIL);
//            }
            // 3.2 通过手机号码查询用户信息
            customerDTO = loginAdaptor.queryCustomerByPhone(query.getPhone());
            if (customerDTO == null) {
                // 3.3 创建新的用户信息并绑定
                customerDTO = loginAdaptor.createCustomer(query.getPhone(), wxMiniLoginResult.getOpenId());
            } else {
                // 3.4 用户已存在，单独绑定
                loginAdaptor.bindWxMini(customerDTO.getId(), wxMiniLoginResult.getOpenId());
            }
        } else if (customerDTO == null) {
            // 4. 临时登录
            customerDTO = this.createTempDTO(query.getCode(), wxMiniLoginResult.getOpenId());
        }
        return this.getLoginResult(customerDTO, ChannelEnum.WX_MINI);
    }

    @Override
    public Boolean logout(LogoutRestCommand logoutRestCommand) {
        if (logoutRestCommand.getCustId() == null || logoutRestCommand.getCustId() <= 0L) {
            return Boolean.TRUE;
        }
        return this.refreshToken(logoutRestCommand.getCustId(), ChannelEnum.get(logoutRestCommand.getChannel()), null);
    }

    @Override
    public String checkLogin(CheckLoginQuery checkLoginQuery) {
        if (checkLoginQuery.getCustId() == null || checkLoginQuery.getCustId() <= 0L) {
            return UUID.randomUUID().toString();
        }
        return String.valueOf(checkLoginQuery.getCustId());
    }

    @Override
    public boolean resetPwd(ResetPwdRestCommand restCommand) {

        //验证用户的验证码，并失效
        if (StringUtils.isNotBlank(restCommand.getSecurityCode())) {
            // TODO 验证码重新对接
//            userAdapter.checkSecurityCode(restCommand.getPhone(), restCommand.getSecurityCode(), RESET_PWD,
//                Boolean.TRUE);
        }

        return loginAdaptor.resetPwd(restCommand.getPhone(), restCommand.getNewPwd());

    }

    private CustomerDTO createTempDTO(String code, String openId) {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(LoginUtils.convertToCustId(openId));
        customerDTO.setPhone(openId);
        customerDTO.setNickname(BizConst.CUSTOMER_TEMP_NICK);
        customerDTO.setUsername(code);
        return customerDTO;
    }

    /**
     * 获取登录返回结果
     *
     * @param customerDTO
     * @param channelEnum
     * @return
     */
    public PhoneLoginResult getLoginResult(CustomerDTO customerDTO, ChannelEnum channelEnum) {
        // 1. 通过用户信息创建登录token
        String token = loginAdaptor.generateToken(customerDTO, null);
        PhoneLoginResult phoneLoginResult = new PhoneLoginResult();
        if (customerDTO.getId() >= LoginUtils.BASE_ID) {
            // 临时登录账号，不设置该值
            phoneLoginResult.setPhone(customerDTO.getPhone());
        }
        phoneLoginResult.setToken(token);
        // 2. 设置token的失效时长
        cacheManager.expire(token, ChannelEnum.WX_MINI.equals(channelEnum) ?
            BizConst.LOGIN_EXPIRE_WX : BizConst.LOGIN_EXPIRE_H5, TimeUnit.MILLISECONDS);
        // 3. 同登录端，再次登录后互踢下线
        this.refreshToken(customerDTO.getId(), channelEnum, token);
        return phoneLoginResult;
    }

    /**
     * 刷新登录Token
     *
     * @param custId
     * @param channelEnum
     * @param newToken
     * @return
     */
    @Override
    public Boolean refreshToken(Long custId, ChannelEnum channelEnum, String newToken) {
        String loginKey = LOGIN_PREFIX + custId;
        String oldToken = cacheManager.getMapValue(loginKey, channelEnum.getCode());
        if (StringUtils.isNotBlank(oldToken)) {
            loginAdaptor.removeToken(oldToken);
        }
        if (StringUtils.isNotBlank(newToken)) {
            cacheManager.putMapValue(loginKey, channelEnum.getCode(), newToken);
        }
        return Boolean.TRUE;
    }

    @Override
    public String grantIntegral(String phone, Integer checkReceivePointType) {
        DistributedLock lock = cacheManager.getLock(getGrantPointLockKey(phone));
        try {
            boolean b = lock.tryLock(500, 5000, TimeUnit.MILLISECONDS);
            ParamUtil.expectTrue(b, I18NMessageUtils.getMessage("system.busy")+","+I18NMessageUtils.getMessage("try.again.later"));  //# "系统繁忙,稍后再试"
            //查询积分流水信息看是否是第一次注册，如果有积分流水但是不存在客户，可能是客户注销后再次注册，那就不能再次送积分,积分流水中没有手机号那怎么区分这个客户是否是再次创建那,
            List<CheckReceivePointDTO> checkReceivePointDTOS = checkReceivePointAdaptor.queryByPhone(phone);
            if (!CollectionUtils.isEmpty(checkReceivePointDTOS)) {
                //两种方式只要有一种获取了积分就不能再次获取积分
                log.warn("已经赠送过积分不能重复赠送");
                return null;
            }
            else{
                GrantAssetsMsg grantAssetsMsg = new GrantAssetsMsg();
                grantAssetsMsg.setPhone(phone);
                grantAssetsMsg.setCheckReceivePointType(checkReceivePointType);
                messageSendManager.sendMessage(grantAssetsMsg,grantpointTopic,"grantpoint");
            }
        } catch (Exception e) {
            log.error("发放积分失败用户手机号:" + phone, e);
        } finally {
            lock.unLock();
        }

        return null;
    }

    /**
     * 注册用户发放积分
     * @param custPrimary
     * @param checkReceivePointType
     * @return
     */
    @Override
    public String grantIntegralByPrimary(String custPrimary, Integer checkReceivePointType) {
        DistributedLock lock = cacheManager.getLock(getGrantPointLockKey(custPrimary));
        try {
            boolean b = lock.tryLock(500, 5000, TimeUnit.MILLISECONDS);
            ParamUtil.expectTrue(b, I18NMessageUtils.getMessage("system.busy")+","+I18NMessageUtils.getMessage("try.again.later"));  //# "系统繁忙,稍后再试"
            //查询积分流水信息看是否是第一次注册，如果有积分流水但是不存在客户，可能是客户注销后再次注册，那就不能再次送积分,积分流水中没有手机号那怎么区分这个客户是否是再次创建那,
            List<CheckReceivePointDTO> checkReceivePointDTOS = checkReceivePointAdaptor.queryByPrimary(custPrimary);
            if (!CollectionUtils.isEmpty(checkReceivePointDTOS)) {
                //两种方式只要有一种获取了积分就不能再次获取积分
                log.warn("已经赠送过积分不能重复赠送");
                return null;
            } else{
                // 没发过 发积分
                GrantAssetsMsg grantAssetsMsg = new GrantAssetsMsg();
                grantAssetsMsg.setCustPrimary(custPrimary);
                grantAssetsMsg.setCheckReceivePointType(checkReceivePointType);
                messageSendManager.sendMessage(grantAssetsMsg,grantpointTopic,"grantpoint");
            }
        } catch (Exception e) {
            log.error("发放积分失败用户iin:" + custPrimary, e);
        } finally {
            lock.unLock();
        }
        return null;
    }

    @Override
    public WeiXinPhoneResult getWxPhone(WxMiniLoginQuery query) {
        return thirdAdapter.getWxPhoneNumber(query.getCode());
    }

    private String getGrantPointLockKey(String phone) {
        return "lock_grant_point_" + phone;
    }

    private String getRegisterGrantTotalCountLockKey() {
        return "registerGrantTotalCountLock";
    }

    private String getRegisterGrantTotalCountKey() {
        return "registerGrantTotalCount";
    }

    @Override
    public CaptchaResponse genImageCaptchaImage() {
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(90, 30, 4, 35);
        lineCaptcha.setGenerator(new MathGenerator(1));
        lineCaptcha.createCode();
        String ticket = UUID.randomUUID().toString();
        CaptchaResponse response = new CaptchaResponse();
        response.setImageKey(ticket);
        response.setImageBase64("data:image/png;base64," + Base64Encoder.encode(lineCaptcha.getImageBytes()));
        cacheManager.set(LOGIN_SHEAR_CAPTCHA_PREFIX + ":" + ticket, lineCaptcha.getCode(), 90L,TimeUnit.SECONDS);
        return response;
    }

    /**
     * 验证码  不用了
     * @param ticket
     * @param verifyCode
     */
    private void checkVerifyCode(String ticket, String verifyCode){
        if (Boolean.TRUE.equals(imageCodeDisable)) {
            return;
        }
        String cacheKey = LOGIN_SHEAR_CAPTCHA_PREFIX + ":" + ticket;
        String code = cacheManager.get(cacheKey);
        cacheManager.delete(cacheKey);  // 一个码只能输入一次
        MathGenerator mathGenerator = new MathGenerator(1);
        if (StringUtils.isBlank(code)){
            throw new FrontManagerException(VERIFICATION_CODE_EXPIRED_ERROR);
        }
        if (!mathGenerator.verify(code,verifyCode)){
            throw new FrontManagerException(VERIFICATION_CODE_ERROR);
        }
    }
}