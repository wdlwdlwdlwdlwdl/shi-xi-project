package com.aliyun.gts.gmall.manager.front;
import com.aliyun.gts.gmall.framework.api.dto.AbstractOutputInfo;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.aliyun.gts.gmall.center.user.common.utils.ParamDesUtils;
import com.aliyun.gts.gmall.framework.api.dto.AbstractRequest;
import com.aliyun.gts.gmall.framework.api.log.dto.ServerInterfaceLog;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.server.aop.AbstractRestAop;
import com.aliyun.gts.gmall.framework.server.env.RequestContext;
import com.aliyun.gts.gmall.framework.server.util.ConverterUtils;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.platform.user.api.dto.common.FrontManagerVisitMessage;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Rest Controller AOP
 *
 * @author tiansong
 */
@Aspect
@Component
@Slf4j
@Order(0)
public class RestControllerAop extends AbstractRestAop {

    @NacosValue(value = "${front-manager.message.visit.enable}", autoRefreshed = true)
    @Value("${front-manager.message.visit.enable:false}")
    private Boolean frontVisitMessageEnabled;

    @NacosValue(value = "${front-manager.message.visit.topic}", autoRefreshed = true)
    @Value("${front-manager.message.visit.topic:}")
    private String frontVisitMessageTopic;

    @Autowired
    private MessageSendManager messageSendManager;

    @Autowired
    @Qualifier("custGrowthCacheManager")
    private CacheManager custGrowthCacheManager;

    private final String methodNames = "ShortUrlController.jumpLongLink,ShortUrlController.jumpLongLinkByItemId,PagesController.htmlTemplate,TrackingLogController.addLog,TrackingLogController.queryAll,MinioController.getPolicy,PrintLogController.log";

    @Override
    public void apiPointcut() {
    }

    @Pointcut(value = "within(com.aliyun.gts.gmall.manager.front..*Controller)")
    public void restApiPointcut() {
    }

    @Override
    @Around(value = "restApiPointcut()")
    public Object doRestApi(ProceedingJoinPoint joinPoint) {
        try {
            //短链跳转中定向，过滤
            try {
                String name = joinPoint.getTarget().getClass().getSimpleName() + "." + joinPoint.getSignature().getName();
                String[] split = methodNames.split(",");
                if (ArrayUtils.contains(split,name)){
                    return joinPoint.proceed();
                }
            }catch (Exception e) {
                log.error(e.getMessage(),e);
            }
            sendFrontManagerVisitMsg(joinPoint);
            Object result = super.doRestApi(joinPoint);
            // 成功应答
            if (result instanceof AbstractOutputInfo) {
                ((AbstractOutputInfo) result).setTraceId(getTraceId(null));
            }
            return result;
        }catch (Throwable e){
            log.error("doRestApi",e);
            return RestResponse.fail("1002",I18NMessageUtils.getMessage("system.exception")+":"+e.getMessage());  //# "系统异常
        }
    }

    private void sendFrontManagerVisitMsg(ProceedingJoinPoint joinPoint) {
        if (!frontVisitMessageEnabled) {
            return;
        }
        FrontManagerVisitMessage visitMessage = buildCustLoginMsg(joinPoint);
        if (visitMessage == null) {
            return;
        }

        messageSendManager.sendMessage(visitMessage, frontVisitMessageTopic, "VISIT");
    }

    private FrontManagerVisitMessage buildCustLoginMsg(ProceedingJoinPoint joinPoint) {

        String custIdStr = StringUtils.trim(RequestContext.getHeaderParam("custid"));

        if (StringUtils.isBlank(custIdStr)) {
            return null;
        }
        Long custId = ConverterUtils.converter(custIdStr, Long.class);

        if (custId == null || custId <= 0) {
            return null;
        }

        Date visitTime = new Date();

        String s = custGrowthCacheManager.get(getVisitCacheKey(custId, visitTime));
        if (BooleanUtils.toBoolean(s)) {
            return null;
        }

        String endpoint = getEndpoint(joinPoint);
        return FrontManagerVisitMessage.builder().custId(custId).visitEndpoint(endpoint).time(visitTime).build();
    }

    private String getVisitCacheKey(Long custId, Date time) {
        return Joiner.on("_").join(
                Lists.newArrayList("first_visit_cache", custId, new SimpleDateFormat("yyyyMMdd").format(time)));
    }

    private String getEndpoint(ProceedingJoinPoint joinPoint) {
        return Joiner.on("#").join(joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
    }

}