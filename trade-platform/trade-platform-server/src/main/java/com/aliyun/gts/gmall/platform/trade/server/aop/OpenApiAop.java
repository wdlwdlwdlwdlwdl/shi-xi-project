package com.aliyun.gts.gmall.platform.trade.server.aop;

import javax.validation.ConstraintViolationException;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.dto.AbstractRequest;
import com.aliyun.gts.gmall.framework.api.log.dto.ServerInterfaceLog;
import com.aliyun.gts.gmall.framework.api.rpc.dto.AbstractQueryRpcRequest;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.server.aop.AbstractOpenApiAop;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;
import com.aliyun.gts.gmall.platform.trade.api.constant.OrderErrorCode;
import com.aliyun.gts.gmall.platform.trade.core.util.ValidationUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(0)
@SuppressWarnings("unused")
public class OpenApiAop extends AbstractOpenApiAop {

    @Override
    @Pointcut(value = "execution(* com.aliyun.gts.gmall.platform.trade.api.facade..*.*(..))")
    public void apiPointcut() {
    }

    @Override
    @Around(value = "apiPointcut()")
    public Object doApi(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        try {
            for (Object arg : args) {
                ValidationUtil.validate(arg);
            }
        }catch (ConstraintViolationException e){
            if(joinPoint.getSignature() instanceof MethodSignature){
                Class c = ((MethodSignature)joinPoint.getSignature()).getMethod().getReturnType();
                if(c.equals(RpcResponse.class)){
                    return RpcResponse.fail(OrderErrorCode.ILLEGAL_ARGS.getCode(), I18NMessageUtils.getMessage(e.getMessage()));
                }else {
                    throw e;
                }
            }else{
                throw e;
            }
        }
        Object result =  super.doApi(joinPoint);
        if(result instanceof RpcResponse response){
            if(!response.isSuccess()){
                if(CommonResponseCode.ServerError.getCode().equals(response.getFail().getCode())){
                    response = RpcResponse.fail(OrderErrorCode.TRADE_CENTER_ERROR.getCode() ,
                        OrderErrorCode.TRADE_CENTER_ERROR.getMessage());
                    result = response;
                }
            }
        }
        return result;
    }

    @Override
    protected void updateInterfaceLog(ServerInterfaceLog serverInterfaceLog, AbstractRequest request) {
    }

    @Override
    protected void doInterfaceLog(ServerInterfaceLog serverInterfaceLog, AbstractRequest request) {
        super.doInterfaceLog(serverInterfaceLog, request);
    }
}
