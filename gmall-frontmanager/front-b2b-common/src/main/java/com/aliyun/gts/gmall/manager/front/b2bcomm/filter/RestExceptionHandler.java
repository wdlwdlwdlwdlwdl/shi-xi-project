package com.aliyun.gts.gmall.manager.front.b2bcomm.filter;

/**
 * @author haibin.xhb
 * @description: 统一异常处理
 * @date 2021/2/4 11:33
 */
//@Service
//@Slf4j(topic = "exceptioin.log")
//@Order(0)
//public class RestExceptionHandler implements HandlerExceptionResolver {
//
//    @Resource
//    private RequestService requestService;
//
//    @Override
//    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
//        Exception e) {
//
//        log.error("系统异常", e);
//        RestResponse response = new RestResponse();
//
//        Throwable real;
//        if (e instanceof java.lang.reflect.UndeclaredThrowableException) {
//            real = e.getCause();
//        } else {
//            real = e;
//        }
//
//        if (real instanceof GmallException) {
//            GmallException gmallException = (GmallException)real;
//            if(gmallException.getFrontendCare() != null && gmallException.getFrontendCare().getArgs() != null){
//                response.setMessage(StringUtils.join(gmallException.getFrontendCare().getArgs()));
//            }else{
//                response.setMessage(real.getMessage());
//            }
//
//            response.setCode(gmallException.getFrontendCare().getCode().getCode());
//        } else if (real instanceof MethodArgumentNotValidException) {
//            MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException)e;
//            response.setMessage(getValidationErrors(methodArgumentNotValidException));
//            response.setCode(CommonResponseCode.IllegalArgument.getCode());
//        } else if (real instanceof GmallInvalidArgumentException) {
//            //参数错误
//            GmallInvalidArgumentException permissionException = (GmallInvalidArgumentException)real;
//            permissionException.getMessage();
//            response.setMessage(permissionException.getMessage());
//            response.setCode(CommonResponseCode.IllegalArgument.getCode());
//        } else {
//            String message = "系统异常:" + ExceptionUtils.getRootCauseMessage(real);
//            log.error(ExceptionUtils.getFullStackTrace(real));
//            response.setMessage(real.getMessage());
//            response.setCode(CommonResponseCode.ServerError.getCode());
//        }
//
//        //返回数据
//        requestService.writeJson(response, httpServletResponse);
//        return new ModelAndView();
//    }
//
//    private String getValidationErrors(MethodArgumentNotValidException e) {
//        List<ObjectError> errors = e.getBindingResult().getAllErrors();
//        if (CollectionUtils.isEmpty(errors)) {
//            return null;
//        }
//        return errors.stream().map(objectError -> objectError.getDefaultMessage()).collect(Collectors.joining(";"));
//    }
//}