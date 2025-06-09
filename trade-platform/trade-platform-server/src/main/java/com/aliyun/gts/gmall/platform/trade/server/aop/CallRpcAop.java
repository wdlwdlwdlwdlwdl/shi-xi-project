package com.aliyun.gts.gmall.platform.trade.server.aop;

import com.aliyun.gts.gmall.framework.server.aop.AbstractCallRpcAop;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author linyi
 */
@Aspect
@Component
@Order(0)
@Slf4j
public class CallRpcAop extends AbstractCallRpcAop {

    @Pointcut("execution(* com.aliyun.gts.gmall.platform.*.api.facade..*(..))"
        + "&& !execution(* com.aliyun.gts.gmall.platform.trade.api.facade..*(..)) ")
    @Override
    public void apiPointcut() {
    }

}

