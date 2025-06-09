package com.aliyun.gts.gmall.manager.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.framework.api.operate.log.sdk.annotation.OpLog;
import com.aliyun.gts.gmall.framework.api.operate.log.sdk.aspect.OperateSaveAop;
import com.aliyun.gts.gmall.framework.api.operate.log.sdk.enums.SystemEnum;
import com.aliyun.gts.gmall.framework.api.operate.log.sdk.model.OperateLogSave;
import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.MessageSendManager;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;

@Slf4j
@Order(0)
@Component
@Aspect
public class OperateLogAop extends OperateSaveAop {

    @Value("${mq.operate.tags:}")
    private String tags;
    @Value("${mq.operate.topic:}")
    private String topic;

    @Autowired
    private MessageSendManager messageSendManager;

    /**
     * 切面全部
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value="(execution(* com.aliyun.gts.gmall..*(..))) && @annotation(com.aliyun.gts.gmall.framework.api.operate.log.sdk.annotation.OpLog)")
    public Object operateLogPointCut(ProceedingJoinPoint joinPoint) throws Throwable {
        JSONObject requestParam = null;
        // 获取注解
        OpLog opLog = getMyOperationLogByJoinPoint(joinPoint);
        // 登录人
        CustDTO user = UserHolder.getUser();
        // 正常执行
        Object response = joinPoint.proceed();
        // 保存数据
        try {
            if (opLog != null ) {
                // 校验参数 通过
                if (Boolean.TRUE.equals(checkParam(joinPoint, opLog))) {
                    requestParam = JSON.parseObject(JSON.toJSONString(joinPoint.getArgs()[0]));
                }
                // 返回结果构建
                JSONObject responseObj = checkResponse(response) ?
                    JSON.parseObject(JSON.toJSONString(response)) : null;
                Long userId = user != null ? user.getCustId() : null;
                String userName = user != null ? user.getCustPrimary() : null;
                // 构建消息对象
                OperateLogSave operateLogSave = buildSave(
                    SystemEnum.ADMIN.getCode(), opLog,
                    requestParam, responseObj, userId, userName
                );
                // 发消息
                messageSendManager.sendMessage(operateLogSave, topic, tags);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
        return response;
    }

    /**
     * 获取注解
     * @param joinPoint
     * @return OpLog
     */
    public OpLog getMyOperationLogByJoinPoint(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (null == method) {
            return null;
        }
        return method.getAnnotation(OpLog.class);
    }
}
