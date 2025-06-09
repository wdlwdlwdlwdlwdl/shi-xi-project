package com.aliyun.gts.gmall.manager.front.aop;

import com.aliyun.gts.gmall.manager.biz.output.CustDTO;
import com.aliyun.gts.gmall.manager.front.common.config.GmallFrontConfig;
import com.aliyun.gts.gmall.manager.front.common.util.IpAddressUtils;
import com.aliyun.gts.gmall.manager.front.common.util.IpHolder;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.service.ResponseWriteService;
import com.aliyun.gts.gmall.manager.front.service.SessionService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 *
 */
@Slf4j
@Service
@WebFilter(urlPatterns = "/*", filterName = "LoginFilter")
public class LoginFilter implements Filter {
    @Resource
    private SessionService sessionService;
    @Resource
    private ResponseWriteService responseWriteService;
    @Autowired
    private GmallFrontConfig gmallFrontConfig;
    private static String HEAD_CUST_ID = "custid";

    private PathMatcher pathMatcher;
    @Override
    public void init(FilterConfig filterConfig) {
        pathMatcher = new AntPathMatcher();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        UserHolder.clearUser();
        IpHolder.set(IpAddressUtils.getIpAddress(request));
        //不要校验;直接下一个
        if (noNeedDoFilter(request)) {
            filterChain.doFilter(servletRequest, response);
            return;
        }
        //cookie为空要跳到登陆页面
        CustDTO custDTO = sessionService.getUser(request);
        if (custDTO != null ) {
            log.info("custDTO返回结果："+custDTO.toString());
            addRequestHeader(request,custDTO);
            UserHolder.set(custDTO);
        }
        filterChain.doFilter(servletRequest, response);
    }

    private void addRequestHeader(HttpServletRequest request,CustDTO custDTO){
        //设置头部信息
        responseWriteService.addRequestHeader(request,HEAD_CUST_ID,custDTO.getCustId()+"");
    }

    protected List<String> getExcludedPage() {
        return Lists.newArrayList(
                "/",
                "/api/login/*",
                "/api/dict/*",
                //以下是 swagger UI
                "/swagger-ui.html",
                "/webjars/**",
                "/v2/**",
                "/swagger-resources/**",
                // 健康检查
                "/health/check*",
                // epay 支付成功回调
                "/api/trade/epay/epaySuccess/**",
                // epay 支付失败回调
                "/api/trade/epay/epayFail/**",
                // minio
                "/api/minio/getPolicy"
        );
    }

    /**
     * 白名单，优先级高于黑名单
     * @return
     */
    protected List<String> getIncludedPage() {
        return Lists.newArrayList(
    "/api/login/checkLogin*",
            "/api/login/casLogin"
        );
    }

    /**
     * 不需要校验的
     * @param request
     * @return
     */
    private boolean noNeedDoFilter(HttpServletRequest request) {
        //使用kong的不需要校验
        if(gmallFrontConfig.getUseKong()){
           return true;
        }
        //跨域尝试请求不需要校验
        if("OPTIONS".equals(request.getMethod())){
            return true;
        }
        //必须要登陆的
        for (String page : getIncludedPage()) {
            if (pathMatcher.match(page, request.getRequestURI())) {
                return false;
            }
        }
        //不需要登陆的不需要校验
        for (String page : getExcludedPage()) {
            if (pathMatcher.match(page, request.getRequestURI())) {
                return true;
            }
        }
        return false;
    }
}
