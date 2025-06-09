package com.aliyun.gts.gmall.manager.front.b2bcomm.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/2/5 14:29
 */
//@Service
//@Aspect
//@Slf4j
//public class RequestAopHandler {
//
//    @Pointcut("execution(* com.aliyun.gts.gcai.purchase.sourcing.web.controller..*.*(..))"
//            + "|| execution(* com.aliyun.gts.gcai.purchase.user.web.controller..*.*(..))"
//        + "|| execution(* com.aliyun.gts.gcai.purchase.material.web.controller..*.*(..))"
//    )
//    public void pointCut() {
//    }
//
//    @Around("pointCut()")
//    public Object around(ProceedingJoinPoint pjp) throws Throwable {
//        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
//        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
//        Signature s = pjp.getSignature();
//        MethodSignature ms = (MethodSignature) s;
//        Method m = ms.getMethod();
//        log.debug("pointCut:" + m.getClass().getName() + "," + m.getName());
//        assert sra != null;
//        Object[] params = pjp.getArgs();
//
//        try {
//            Arrays.stream(params).forEach(p -> {
//                ValidationUtil.validate(p);
//            });
//        } catch (ConstraintViolationException e) {
//            if (pjp.getSignature() instanceof MethodSignature) {
//                Class c = ((MethodSignature) pjp.getSignature()).getMethod().getReturnType();
//                if (c.equals(RestResponse.class)) {
//                    return RestResponse.fail(CommonResponseCode.IllegalArgument, e.getMessage());
//                }
//            }
//        }
//
//        for (Object param : params) {
//            //用户登陆数据
//            if (param instanceof CommonReq) {
//                OperatorDO operatorDO = getLogin(((ServletRequestAttributes) ra).getRequest());
//                CommonReq common = (CommonReq) param;
//                common.setLoginAccount(operatorDO);
//            }
//            if (param instanceof Believable) {
//                ((Believable) param).checkInput();
//            }
//        }
//        //TODO: 校验权限
//        checkPermission(m);
//        Object object = pjp.proceed();
//        if (object instanceof AbstractOutputInfo) {
//            ((AbstractOutputInfo) object).setTraceId(Tracer.builder().getSpan().getTraceId());
//        }
//        log.info(JSON.toJSONString(object));
//        return object;
//    }
//
//    private void checkPermission(Method m ){
////        List<MPair<Long,String>> entities = new ArrayList<MPair<Long,String>>();
////        entities.add(MPair.of(1L,"1"));
//        //checkPermissionService.checkMethod(m,entities, MiscConstants.appName);
//    }
//    /**
//     * 获取登陆账户
//     *
//     * @param request
//     * @return
//     */
//    private OperatorDO getLogin(HttpServletRequest request) {
//        if (request.getSession() != null) {
//            return (OperatorDO) request.getSession().getAttribute(CommonConstants.SESSION_KEY);
//        }
//        return null;
//    }
//}
