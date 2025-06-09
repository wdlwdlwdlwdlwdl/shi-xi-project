package com.aliyun.gts.gmall.manager.front.login.consumer;

import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.center.misc.common.utils.JsonUtils;
import com.aliyun.gts.gmall.center.user.api.dto.input.CheckReceivePointCreateReq;
import com.aliyun.gts.gmall.center.user.api.dto.output.CheckReceivePointDTO;
import com.aliyun.gts.gmall.center.user.api.enums.CheckReceivePointTypeEnum;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.login.adaptor.CheckReceivePointAdaptor;
import com.aliyun.gts.gmall.manager.front.login.adaptor.IntegralAdaptor;
import com.aliyun.gts.gmall.manager.front.login.adaptor.UserAdapter;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.GrantAssetsMsg;
import com.aliyun.gts.gmall.manager.front.login.dto.input.query.GrantIntegralConfig;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.middleware.api.cache.lock.DistributedLock;
import com.aliyun.gts.gmall.middleware.api.mq.anns.MQConsumer;
import com.aliyun.gts.gmall.middleware.api.mq.consumer.ConsumeEventProcessor;
import com.aliyun.gts.gmall.middleware.api.mq.model.StandardEvent;
import com.aliyun.gts.gmall.platform.promotion.api.dto.model.PromotionConfigDTO;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 */
@Slf4j
@MQConsumer(groupId = "${front-manager.message.grantpoint.groupId}", topic = "${front-manager.message.grantpoint.topic}", tag = "grantpoint")
public class GrantPointConsume implements ConsumeEventProcessor {
    @Autowired
    CacheManager cacheManager;
    @Autowired
    CheckReceivePointAdaptor checkReceivePointAdaptor;
    @Autowired
    UserAdapter userAdapter;
    @Autowired
    IntegralAdaptor integralAdaptor;

    public static final String bizIdPhonePrefix="newRegister_or_noLogin_phone_grant";

    public static final String bizIdIinPrefix="newRegister_or_noLogin_iin_grant";
    @Override
    public boolean process(StandardEvent event) {
        GrantAssetsMsg message = (GrantAssetsMsg) event.getPayload().getData();
        log.info("GrantPointConsume.process,msgBody:{}", JSONObject.toJSONString(message));
        try {
            if (StringUtils.isNotEmpty(message.getPhone())) {
                return grantIntegral(message.getPhone(), message.getCheckReceivePointType());
            }
            else {
                //进行信息用户分组绑定关系建立
                PageInfo<CustomerDTO> customerDTOPageInfo = userAdapter.queryCustInfoByPrimary(message.getCustPrimary());
                if (null != customerDTOPageInfo && !CollectionUtils.isEmpty(customerDTOPageInfo.getList())) {
                    CustomerDTO customerDTO = customerDTOPageInfo.getList().get(0);
                    integralAdaptor.addNewRegisterRelation(customerDTO);
                }
                return grantIntegralByIin(message.getCustPrimary(), message.getCheckReceivePointType());
            }
        } catch (Exception e) {
            log.error("PointLotteryConsumer消费积分消息失败," + JsonUtils.toJSONString(message), e);
            return false;
        }
    }

    private String getGrantPointLockKey(String phone) {
        return "lock_grant_point_" + phone;
    }

    public Boolean grantIntegral(String phone, Integer checkReceivePointType) {
        DistributedLock lock = cacheManager.getLock(getGrantPointLockKey(phone));
        try {
            boolean b = lock.tryLock(500, 5000, TimeUnit.MILLISECONDS);
            ParamUtil.expectTrue(b, I18NMessageUtils.getMessage("system.busy")+","+I18NMessageUtils.getMessage("try.again.later"));  //# "系统繁忙,稍后再试"
            //查询积分流水信息看是否是第一次注册，如果有积分流水但是不存在客户，可能是客户注销后再次注册，那就不能再次送积分,积分流水中没有手机号那怎么区分这个客户是否是再次创建那,
            List<CheckReceivePointDTO> checkReceivePointDTOS = checkReceivePointAdaptor.queryByPhone(phone);
            if (!org.springframework.util.CollectionUtils.isEmpty(checkReceivePointDTOS)) {
                //两种方式只要有一种获取了积分就不能再次获取积分
                log.warn("已经赠送过积分不能重复赠送");
                return true;
            }
            //查询注册用户信息
            PageInfo<CustomerDTO> customerDTOPageInfo = userAdapter.queryCustInfoByPhone(phone);
            if (customerDTOPageInfo == null || org.springframework.util.CollectionUtils.isEmpty(customerDTOPageInfo.getList())) {
                log.warn("用户信息为空");
                return false;
            }
            Long custId = customerDTOPageInfo.getList().get(0).getId();
            //获取积分配置信息
            PromotionConfigDTO promotionConfigDTO = integralAdaptor.grantIntegral();
            if (promotionConfigDTO == null) {
                log.warn("积分配置信息为空");
                return true;
            }
            GrantIntegralConfig grantIntegralConfig = JSONObject.toJavaObject(promotionConfigDTO.getBody(),
                    GrantIntegralConfig.class);
            if (grantIntegralConfig == null) {
                log.warn("积分配置信息为空");
                return true;
            }
            Long grantAssetsValue = null;
            if (checkReceivePointType.equals(CheckReceivePointTypeEnum.REGISTER.getCode())) {
                //用户新注册积分规则
                Long registerPointValue = grantIntegralConfig.getRegisterPointValue();
                if (registerPointValue == null) {
                    log.warn("新用户注册对应的积分配置信息为空");
                    return true;
                }
                grantAssetsValue = registerPointValue;
                Boolean tradeRegisterPoint = grantIntegralConfig.getTradeRegisterPoint();
                if (!Boolean.TRUE.equals(tradeRegisterPoint)) {
                    log.warn("未开启新用户注册对应的积分配置信息");
                    return true;
                }
            } else if (checkReceivePointType.equals(CheckReceivePointTypeEnum.NOLOGIN.getCode())) {
                //授权免登积分规则
                Boolean tradeNoLoginPoint = grantIntegralConfig.getTradeNoLoginPoint();
                if (!Boolean.TRUE.equals(tradeNoLoginPoint)) {
                    log.warn("未开启免授权登录对应的积分配置信息");
                    return true;
                }
                Long noLoginPointValue = grantIntegralConfig.getNoLoginPointValue();
                if (noLoginPointValue == null) {
                    log.warn("免授权登录对应的积分金额配置信息为空");
                    return true;
                }
                grantAssetsValue = noLoginPointValue;
            }
            //先插入积分流水表，这样的话如果服务挂掉，顶多会漏发积分，不会多发放积分
            CheckReceivePointCreateReq addCheckReceivePointDTO = new CheckReceivePointCreateReq();
            addCheckReceivePointDTO.setPhone(phone);
            addCheckReceivePointDTO.setReceivePointType(checkReceivePointType);
            //给用户下发积分(这个是幂等id下发积分可以多次尝试)
            String bizId = bizIdPhonePrefix+"_"+phone;
            Boolean aBoolean = integralAdaptor.grantAssets(custId, grantAssetsValue, grantIntegralConfig, checkReceivePointType, bizId);
            if(aBoolean){
                //插入流水记录的时候再查询一次 如果是已经插入过送积分流水就不在插入
                List<CheckReceivePointDTO> secondCheckReceivePointDTOS = checkReceivePointAdaptor.queryByPhone(phone);
                if (!org.springframework.util.CollectionUtils.isEmpty(secondCheckReceivePointDTOS)) {
                    return true;
                }
                checkReceivePointAdaptor.save(addCheckReceivePointDTO);
            }
            return aBoolean;
        } catch (Exception e) {
            log.error("发放积分失败用户手机号:" + phone, e);
            return false;
        } finally {
            lock.unLock();
        }
    }


    public Boolean grantIntegralByIin(String custPrimary, Integer checkReceivePointType) {
        DistributedLock lock = cacheManager.getLock(getGrantPointLockKey(custPrimary));
        try {
            boolean b = lock.tryLock(500, 5000, TimeUnit.MILLISECONDS);
            ParamUtil.expectTrue(b, I18NMessageUtils.getMessage("system.busy")+","+I18NMessageUtils.getMessage("try.again.later"));  //# "系统繁忙,稍后再试"
            //查询积分流水信息看是否是第一次注册，如果有积分流水但是不存在客户，可能是客户注销后再次注册，那就不能再次送积分,积分流水中没有手机号那怎么区分这个客户是否是再次创建那,
            List<CheckReceivePointDTO> checkReceivePointDTOS = checkReceivePointAdaptor.queryByPrimary(custPrimary);
            if (!org.springframework.util.CollectionUtils.isEmpty(checkReceivePointDTOS)) {
                //两种方式只要有一种获取了积分就不能再次获取积分
                log.warn("已经赠送过积分不能重复赠送");
                return true;
            }
            //查询注册用户信息
            PageInfo<CustomerDTO> customerDTOPageInfo = userAdapter.queryCustInfoByPrimary(custPrimary);
            if (customerDTOPageInfo == null || org.springframework.util.CollectionUtils.isEmpty(customerDTOPageInfo.getList())) {
                log.warn("用户信息为空");
                return false;
            }
            Long custId = customerDTOPageInfo.getList().get(0).getId();
            //获取积分配置信息
            PromotionConfigDTO promotionConfigDTO = integralAdaptor.grantIntegral();
            if (promotionConfigDTO == null) {
                log.warn("积分配置信息为空");
                return true;
            }
            GrantIntegralConfig grantIntegralConfig = JSONObject.toJavaObject(promotionConfigDTO.getBody(),
                GrantIntegralConfig.class);
            if (grantIntegralConfig == null) {
                log.warn("积分配置信息为空");
                return true;
            }
            Long grantAssetsValue = null;
            if (checkReceivePointType.equals(CheckReceivePointTypeEnum.REGISTER.getCode())) {
                //用户新注册积分规则
                Long registerPointValue = grantIntegralConfig.getRegisterPointValue();
                if (registerPointValue == null) {
                    log.warn("新用户注册对应的积分配置信息为空");
                    return true;
                }
                grantAssetsValue = registerPointValue;
                Boolean tradeRegisterPoint = grantIntegralConfig.getTradeRegisterPoint();
                if (!Boolean.TRUE.equals(tradeRegisterPoint)) {
                    log.warn("未开启新用户注册对应的积分配置信息");
                    return true;
                }
            } else if (checkReceivePointType.equals(CheckReceivePointTypeEnum.NOLOGIN.getCode())) {
                //授权免登积分规则
                Boolean tradeNoLoginPoint = grantIntegralConfig.getTradeNoLoginPoint();
                if (!Boolean.TRUE.equals(tradeNoLoginPoint)) {
                    log.warn("未开启免授权登录对应的积分配置信息");
                    return true;
                }
                Long noLoginPointValue = grantIntegralConfig.getNoLoginPointValue();
                if (noLoginPointValue == null) {
                    log.warn("免授权登录对应的积分金额配置信息为空");
                    return true;
                }
                grantAssetsValue = noLoginPointValue;
            }
            //先插入积分流水表，这样的话如果服务挂掉，顶多会漏发积分，不会多发放积分
            CheckReceivePointCreateReq addCheckReceivePointDTO = new CheckReceivePointCreateReq();
            addCheckReceivePointDTO.setCustPrimary(custPrimary);
            addCheckReceivePointDTO.setReceivePointType(checkReceivePointType);
            //给用户下发积分(这个是幂等id下发积分可以多次尝试)
            String bizId = bizIdIinPrefix + "_" + custPrimary;
            Boolean aBoolean = integralAdaptor.grantAssets(custId, grantAssetsValue, grantIntegralConfig, checkReceivePointType, bizId);
            if(aBoolean){
                //插入流水记录的时候再查询一次 如果是已经插入过送积分流水就不在插入
                List<CheckReceivePointDTO> secondCheckReceivePointDTOS = checkReceivePointAdaptor.queryByPrimary(custPrimary);
                if (!org.springframework.util.CollectionUtils.isEmpty(secondCheckReceivePointDTOS)) {
                    return true;
                }
                checkReceivePointAdaptor.save(addCheckReceivePointDTO);
            }
            return aBoolean;
        } catch (Exception e) {
            log.error("发放积分失败用户iin:" + custPrimary, e);
            return false;
        } finally {
            lock.unLock();
        }
    }
}
