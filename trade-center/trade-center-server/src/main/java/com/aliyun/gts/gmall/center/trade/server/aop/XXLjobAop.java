package com.aliyun.gts.gmall.center.trade.server.aop;

import com.aliyun.gts.gmall.framework.server.aop.AbstractJobTraceAop;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(0)
@Component
@Aspect
public class XXLjobAop extends AbstractJobTraceAop {

    @Override
    @Pointcut("@annotation(com.xxl.job.core.handler.annotation.XxlJob)")
    public void xxljobPointcut() {

    }

    @Override
    @Around(value = "xxljobPointcut()")
    public Object doXXLjobApi(ProceedingJoinPoint joinPoint) throws Throwable {
        return super.doXXLjobApi(joinPoint);
    }

}
