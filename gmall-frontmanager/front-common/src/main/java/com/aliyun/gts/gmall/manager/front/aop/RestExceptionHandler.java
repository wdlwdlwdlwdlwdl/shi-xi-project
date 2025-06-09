package com.aliyun.gts.gmall.manager.front.aop;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.consts.CommonResponseCode;
import com.aliyun.gts.gmall.framework.api.exception.GmallInvalidArgumentException;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.server.exception.GmallException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author haibin.xhb
 * @description: 统一异常处理
 * @date 2021/2/4 11:33
 */
@Service
@Slf4j
@Order(0)
public class RestExceptionHandler implements HandlerExceptionResolver {

    @Resource
    private RequestService requestService;

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
        Exception e) {

        RestResponse response = new RestResponse();

        Throwable real;
        if (e instanceof java.lang.reflect.UndeclaredThrowableException) {
            real = e.getCause();
        } else {
            real = e;
        }

        log.error("exception, uri: {} ", httpServletRequest.getRequestURI(), real);

        if (real instanceof GmallException) {
            GmallException gmallException = (GmallException)real;
            if(gmallException.getFrontendCare() != null && gmallException.getFrontendCare().getArgs() != null){
                response.setMessage(StringUtils.join(gmallException.getFrontendCare().getArgs()));
            }else{
                response.setMessage(real.getMessage());
            }

            response.setCode(gmallException.getFrontendCare().getCode().getCode());
        } else if (real instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException)real;
            response.setMessage(getValidationErrors(methodArgumentNotValidException));
            response.setCode(CommonResponseCode.IllegalArgument.getCode());
        }  else if (real instanceof GmallInvalidArgumentException) {
            //参数错误
            GmallInvalidArgumentException permissionException = (GmallInvalidArgumentException)real;
            permissionException.getMessage();
            response.setMessage(permissionException.getMessage());
            response.setCode(CommonResponseCode.IllegalArgument.getCode());
        } else if (real instanceof HttpMessageConversionException) {
            // @RequestBody 转换错误
            response.setMessage(I18NMessageUtils.getMessage("param.error"));  //# "参数错误"
            response.setCode(CommonResponseCode.IllegalArgument.getCode());
        } else {
            String message = I18NMessageUtils.getMessage("system.exception")+":" + ExceptionUtils.getRootCauseMessage(real);  //# "系统异常
            response.setMessage(message);
            response.setCode(CommonResponseCode.ServerError.getCode());
        }

        //返回数据
        requestService.writeJson(response, httpServletResponse);
        return new ModelAndView();
    }

    private String getValidationErrors(MethodArgumentNotValidException e) {
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        if (CollectionUtils.isEmpty(errors)) {
            return null;
        }
        return errors.stream().map(objectError -> objectError.getDefaultMessage()).collect(Collectors.joining(";"));
    }
}