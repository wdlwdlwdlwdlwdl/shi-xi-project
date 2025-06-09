package com.aliyun.gts.gmall.manager.front.login.adaptor;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import com.aliyun.gts.gmall.manager.framework.common.DubboBuilder;
import com.aliyun.gts.gmall.manager.front.common.config.DatasourceConfig;
import com.aliyun.gts.gmall.manager.front.common.consts.DsIdConst;
import com.aliyun.gts.gmall.manager.front.login.common.SmsLimitCount;
import com.aliyun.gts.gmall.manager.front.login.dto.output.SecurityCodeResult;
import com.aliyun.gts.gmall.manager.front.login.dto.utils.DateTimeUtil;
import com.aliyun.gts.gmall.manager.front.login.dto.utils.LoginFrontResponseCode;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.platform.user.api.dto.contants.CustomerStatusEnum;
import com.aliyun.gts.gmall.platform.user.api.dto.input.*;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerExtendDTO;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerPhoneVerifyFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerReadFacade;
import com.aliyun.gts.gmall.platform.user.api.facade.CustomerWriteFacade;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.aliyun.gts.gmall.manager.front.common.exception.FrontCommonResponseCode.CUSTOMER_CENTER_ERROR;
import static com.aliyun.gts.gmall.manager.front.login.dto.utils.LoginFrontResponseCode.*;

@Service
@Slf4j
public class UserAdapter {

    @Autowired
    private DatasourceConfig datasourceConfig;
    @Autowired
    private CustomerPhoneVerifyFacade customerPhoneVerifyFacade;
    @Autowired
    private CustomerWriteFacade customerWriteFacade;
    @Autowired
    private CustomerReadFacade customerReadFacade;

    @Resource
    @Qualifier("cacheManager")
    private CacheManager cacheManager;

    @Value("${front-manager.default-icon:}")
    private String defaultIcon;

    private static final String SMS_LIMIT_PREFIX = "SMS_LIMIT_";

    private static final String CHECK_LIMIT_PREFIX = "CHECK_LIMIT_";


    //12小时
    private static final Long TTL_MS = 60 * 12 * 60 * 1000L;

    private static final int COUNT_IN_12_HOURS = 10;

    private static final DubboBuilder builder = DubboBuilder.builder().logger(log).sysCode(CUSTOMER_CENTER_ERROR).build();

    /**
     * 发送验证码
     */
    public SecurityCodeResult sendSecurityCode(String phone, long ttl, String type) {
        if (!checkFrequence(phone)) {
            return new SecurityCodeResult(Boolean.FALSE, LoginFrontResponseCode.CHECK_SECURITY_FREQUENCE_LIMIT_FAIL.getMessage());
        }
        PhoneCodeSendCommand phoneCodeSendCommand = new PhoneCodeSendCommand();
        phoneCodeSendCommand.setPhone(phone);
        phoneCodeSendCommand.setTtl(ttl);
        phoneCodeSendCommand.setType(type);
        return builder.create(datasourceConfig).id(DsIdConst.user_security_code_send).queryFunc(
                (Function<PhoneCodeSendCommand, RpcResponse<SecurityCodeResult>>) request -> {
                    RpcResponse<Boolean> rpcResponse = customerPhoneVerifyFacade.sendAndCacheVerifier(request);
                    if (rpcResponse.isSuccess() && rpcResponse.getData()) {
                        return RpcResponse.ok(new SecurityCodeResult(Boolean.TRUE, null));
                    }
                    return rpcResponse.isSuccess() ?
                            RpcResponse.ok(new SecurityCodeResult(Boolean.FALSE, SEND_SECURITY_CODE_FAIL.getMessage())) :
                            RpcResponse.ok(new SecurityCodeResult(Boolean.FALSE, rpcResponse.getFail().getMessage()));
                }).bizCode(SEND_SECURITY_CODE_FAIL).query(phoneCodeSendCommand);
    }

    private boolean checkFrequence(String phone) {
        try {
            String smsLimitKey = getSmsLimitKey(phone);
            SmsLimitCount smsLimitCount = cacheManager.get(smsLimitKey);
            //限制缓存没命中表示第一次发送
            if (smsLimitCount == null) {
                initSmsLimitCache(smsLimitKey);
                return true;
            }
            Date now = new Date();
            Date lastSendTime = smsLimitCount.getLastSendTime();
            if (DateTimeUtil.isInOneMinute(now, lastSendTime)) {
                log.warn("frequent send sms in one minute for phone = " + phone);
                return false;
            }
            if (smsLimitCount.getTotalCount() > COUNT_IN_12_HOURS) {
                log.warn("send sms exceed limit for phone = "+phone);
                return false;
            }
            SmsLimitCount newSmsLimitCount = new SmsLimitCount();
            newSmsLimitCount.setTotalCount(smsLimitCount.getTotalCount() + 1);
            newSmsLimitCount.setLastSendTime(new Date());
            cacheManager.set(smsLimitKey, newSmsLimitCount, TTL_MS);
            return true;
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return true;
        }
    }

    private void initSmsLimitCache(String smsLimitKey){
        SmsLimitCount smsLimitCount = new SmsLimitCount();
        smsLimitCount.setTotalCount(1);
        smsLimitCount.setLastSendTime(new Date());
        cacheManager.set(smsLimitKey, smsLimitCount, TTL_MS);
    }

    private static String getSmsLimitKey(String phone) {
        return StringUtils.join(SMS_LIMIT_PREFIX, phone);
    }

    /**
     * 校验验证码
     */
    public Boolean checkSecurityCode(String phone, String code, String type, boolean needExpired) {

        //校验验证次数
        if (!checkLimit(phone)) {
            throw new GmallException(CHECK_SECURITY_LIMIT);
        }

        try {

            PhoneCodeVerifyCommand phoneCodeVerifyCommand = new PhoneCodeVerifyCommand();
            phoneCodeVerifyCommand.setPhone(phone);
            phoneCodeVerifyCommand.setCode(code);
            phoneCodeVerifyCommand.setExpireOnSuccess(needExpired);
            phoneCodeVerifyCommand.setType(type);
            return builder.create(datasourceConfig).id(DsIdConst.user_security_code_check).queryFunc(
                (Function<PhoneCodeVerifyCommand, RpcResponse<Boolean>>)request -> {
                    RpcResponse<Boolean> rpcResponse = customerPhoneVerifyFacade.verify(request);
                    if (rpcResponse.isSuccess() && rpcResponse.getData()) {
                        return RpcResponse.ok(Boolean.TRUE);
                    }
                    return rpcResponse.isSuccess() ? RpcResponse.fail(CHECK_SECURITY_CODE_FAIL) : RpcResponse.fail(
                        rpcResponse.getFail());
                }).bizCode(CHECK_SECURITY_CODE_FAIL).query(phoneCodeVerifyCommand);

        } catch (Throwable e) {
            //验证失败后累加次数
            addLimit(phone);
            throw e;
        }

    }
    

    private void addLimit(String phone) {
        Integer num = cacheManager.get(CHECK_LIMIT_PREFIX + phone);
        cacheManager.set(CHECK_LIMIT_PREFIX + phone, num == null ? 1 : num + 1, TTL_MS);
    }


    private boolean checkLimit(String phone) {
        Integer num = cacheManager.get(CHECK_LIMIT_PREFIX + phone);
        return num == null || num <= COUNT_IN_12_HOURS;
    }

    /**
     * 创建用户
     */
    public CustomerDTO createCustomer(String phone,
                                      String username,
                                      String password) {
        return createCustomer(phone, username, password, null, CustomerStatusEnum.NORMAL.getCode(), null);
    }

    /**
     * 创建用户
     */
    public CustomerDTO createCustomer(String phone,
                                      String username,
                                      String password,
                                      List<String> tags,
                                      Integer status,
                                      Integer custLevel) {
        CreateCustomerCommand command = new CreateCustomerCommand();
        command.setPhone(phone);
        command.setUsername(username);
        command.setPwd(password);
        command.setStatus(status != null ? status.toString() : null);
        command.setPhoneIsBind(Boolean.TRUE);
        command.setTags(tags);
        command.setCustLevel(custLevel);
        command.setHeadUrl(defaultIcon);
        return builder.create(datasourceConfig).id(DsIdConst.user_customer_create).queryFunc(
            (Function<CreateCustomerCommand, RpcResponse<CustomerDTO>>) request -> {
                RpcResponse<CustomerDTO> rpcResponse = customerWriteFacade.create(request);
                if (!rpcResponse.isSuccess()) {
                    return RpcResponse.fail(rpcResponse.getFail());
                }
                CustomerDTO customerDTO = rpcResponse.getData();
                if (customerDTO == null) {
                    return RpcResponse.fail(MEMBER_CREATE_FAIL);
                }
                return RpcResponse.ok(customerDTO);
            }).bizCode(MEMBER_CREATE_FAIL).query(command);
    }


    /**
     * 免登陆创建用户
     * @param iin
     * @param userId
     * @param birthDay
     * @param firstName
     * @param lastName
     * @param middleName
     * @return CustomerDTO
     */
    public CustomerDTO createCustomer(
        String iin, String userId,
        Date birthDay, String firstName, String lastName,
        String middleName, String phone, String language) {

        CreateCustomerCommand command = new CreateCustomerCommand();
        command.setIin(iin);
        //userId塞入thirdAccounts
        command.setPhone(phone);
        command.setBirthDay(birthDay);
        command.setThirdAccounts(userId);
        command.setFirstName(firstName);
        command.setLastName(lastName);
        command.setMiddleName(middleName);
        command.setLanguage(language);
        command.setCustPrimary(iin);
        command.setStatus(CustomerStatusEnum.NORMAL.getCode().toString());
        command.setPhoneIsBind(StringUtils.isNotEmpty(phone));
        return builder.create(datasourceConfig).id(DsIdConst.user_customer_create).queryFunc(
            (Function<CreateCustomerCommand, RpcResponse<CustomerDTO>>) request -> {
                RpcResponse<CustomerDTO> rpcResponse = customerWriteFacade.create(request);
                if (!rpcResponse.isSuccess()) {
                    return RpcResponse.fail(rpcResponse.getFail());
                }
                CustomerDTO customerDTO = rpcResponse.getData();
                if (customerDTO == null) {
                    return RpcResponse.fail(MEMBER_CREATE_FAIL);
                }
                return RpcResponse.ok(customerDTO);
            }).bizCode(MEMBER_CREATE_FAIL).query(command);
    }

    /**
     * 创建用户
     * @param phone
     * @param username
     * @param password
     * @param features
     * @return
     */
    public CustomerDTO createCustomer(String phone, String username, String password, Map<String,String> features) {
        return createCustomer(phone, username, password, null, CustomerStatusEnum.NORMAL.getCode(), null,features);
    }

    /**
     * 创建用户
     * @param phone
     * @param username
     * @param password
     * @param tags
     * @param status
     * @param custLevel
     * @param features
     * @return
     */
    public CustomerDTO createCustomer(
        String phone, String username, String password, List<String> tags,
        Integer status, Integer custLevel, Map<String,String> features) {

        CreateCustomerCommand command = new CreateCustomerCommand();
        command.setPhone(phone);
        command.setUsername(username);
        command.setNickname(username);
        command.setPwd(password);
        command.setStatus(status != null ? status.toString() : null);
        command.setPhoneIsBind(Boolean.TRUE);
        command.setTags(tags);
        command.setCustLevel(custLevel);
        command.setFeatures(features);
        return builder.create(datasourceConfig).id(DsIdConst.user_customer_create).queryFunc(
            (Function<CreateCustomerCommand, RpcResponse<CustomerDTO>>) request -> {
                RpcResponse<CustomerDTO> rpcResponse = customerWriteFacade.create(request);
                if (!rpcResponse.isSuccess()) {
                    return RpcResponse.fail(rpcResponse.getFail());
                }
                CustomerDTO customerDTO = rpcResponse.getData();
                if (customerDTO == null) {
                    return RpcResponse.fail(MEMBER_CREATE_FAIL);
                }
                return RpcResponse.ok(customerDTO);
            }).bizCode(MEMBER_CREATE_FAIL).query(command);
    }


    /**
     * 保存扩展信息
     */
    public Boolean saveExtend(Long custId, String type, Map<String, String> extendData) {
        SaveExtendCommand command = new SaveExtendCommand();
        command.setCustId(custId);
        command.setType(type);
        command.setAddMap(extendData);
        Function<SaveExtendCommand, RpcResponse<Boolean>> func = customerWriteFacade::saveExtend;
        return builder.create(datasourceConfig)
            .id(DsIdConst.user_customer_extend_save)
            .queryFunc(func)
            .bizCode(MEMBER_SAVE_FAIL)
            .query(command);
    }

    public CustomerDTO queryById(Long custId) {
        CustomerByIdQuery customerByByIdQuery = new CustomerByIdQuery();
        customerByByIdQuery.setId(custId);
        customerByByIdQuery.setOption(CustomerQueryOption.builder().build());
        return builder.create(datasourceConfig).id(DsIdConst.customer_base_queryById).queryFunc(
            (Function<CustomerByIdQuery, RpcResponse<CustomerDTO>>) request -> customerReadFacade.query(request)
        ).bizCode(CUSTOMER_QUERY_FAIL).query(customerByByIdQuery);
    }

    public PageInfo<CustomerDTO> queryCustInfoByPhone(String phone) {
        CustomerQuery customerQuery = new CustomerQuery();
        customerQuery.setPhone(phone);
        return builder.create(datasourceConfig).id(DsIdConst.customer_base_queryByCustInfo).queryFunc(
            (Function<CustomerQuery, RpcResponse<PageInfo<CustomerDTO>>>) request -> customerReadFacade.queryByCustInfo(customerQuery)
        ).bizCode(CUSTOMER_QUERY_FAIL).query(customerQuery);
    }

    public PageInfo<CustomerDTO> queryCustInfoByPrimary(String custPrimary) {
        CustomerQuery customerQuery = new CustomerQuery();
        customerQuery.setCustPrimary(custPrimary);
        return builder.create(datasourceConfig).id(DsIdConst.customer_base_queryByCustInfo).queryFunc(
            (Function<CustomerQuery, RpcResponse<PageInfo<CustomerDTO>>>) request -> customerReadFacade.queryByCustInfo(customerQuery)
        ).bizCode(CUSTOMER_QUERY_FAIL).query(customerQuery);
    }

    /**
     *
     * @param custId
     * @param status
     */
    public void updateStatus(Long custId, Integer status) {
        UpdateCustomerCommand command = new UpdateCustomerCommand();
        command.setId(custId);
        command.setStatus(status.toString());
        Function<UpdateCustomerCommand, RpcResponse<CustomerDTO>> func = customerWriteFacade::update;
        builder.create(datasourceConfig)
            .id(DsIdConst.user_customer_status_update)
            .queryFunc(func)
            .bizCode(MEMBER_SAVE_FAIL)
            .query(command);
    }

    /**
     * 查询 扩展字段
     * @param custId
     * @param type
     * @param key
     * @return
     */
    public String queryExtend(Long custId, String type, String key) {
        CustomerExtendQuery query = new CustomerExtendQuery();
        query.setCustId(custId);
        query.setType(type);
        query.setK(key);
        Function<CustomerExtendQuery, RpcResponse<PageInfo<CustomerExtendDTO>>> func = customerReadFacade::queryExtend;
        PageInfo<CustomerExtendDTO> page = builder.create(datasourceConfig)
            .id(DsIdConst.user_customer_extend_query)
            .queryFunc(func)
            .bizCode(CUSTOMER_QUERY_FAIL)
            .query(query);
        if (page == null || CollectionUtils.isEmpty(page.getList())) {
            return null;
        }
        CustomerExtendDTO ext = page.getList().get(0);
        if (ext == null) {
            return null;
        }
        return ext.getV();
    }

    /**
     * 修改会员
     * @param custId
     * @param userId
     * @param birthDay
     * @param firstName
     * @param lastName
     * @param middleName
     * @param phone
     */
    public void updateCustomer(Long custId, String userId, Date birthDay, String firstName, String lastName, String middleName,String phone) {
        UpdateCustomerCommand command = new UpdateCustomerCommand();
        command.setId(custId);
        command.setThirdAccounts(userId);
        command.setBirthDay(birthDay);
        command.setFirstName(firstName);
        command.setLastName(lastName);
        command.setMiddleName(middleName);
        Function<UpdateCustomerCommand, RpcResponse<CustomerDTO>> func = customerWriteFacade::update;
        builder.create(datasourceConfig)
            .id(DsIdConst.customer_base_update)
            .queryFunc(func)
            .bizCode(MEMBER_SAVE_FAIL)
            .query(command);
    }
}
