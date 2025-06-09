package com.aliyun.gts.gmall.center.trade.server.aop;

import com.aliyun.gts.gmall.framework.server.aop.AbstractMqTraceAop;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(0)
@Component
@Aspect
public class MQConsumerAop extends AbstractMqTraceAop {

    @Override
    @Pointcut("execution(* com.aliyun.gts.gmall.middleware.api.mq.consumer.ConsumeEventProcessor.process(..))")
    public void mqPointcut() {

    }

    @Override
    @Around(value = "mqPointcut()")
    public Object doMqApi(ProceedingJoinPoint joinPoint) throws Throwable {
        return super.doMqApi(joinPoint);
    }

}
