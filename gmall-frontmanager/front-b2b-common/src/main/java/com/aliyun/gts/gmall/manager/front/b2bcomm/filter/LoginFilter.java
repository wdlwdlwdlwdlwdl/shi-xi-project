package com.aliyun.gts.gmall.manager.front.b2bcomm.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author haibin.xhb
 * @description:
 * @date 2021/2/3 18:31
 */
//@Slf4j
//@Service
//@WebFilter(urlPatterns = "/*", filterName = "LoginFilter")
//public class LoginFilter { //implements Filter {
//
//    @Resource
//    private RequestService requestService;
//
//    @Resource
//    private SessionService sessionService;
//
//    @Autowired
//    private OperatorConfig operatorConfig;
//
//    private PathMatcher pathMatcher;
//
//    @Value("${gcai.portal.referer.whitelist:http://gmall-dev.hanghangohye.com/}")
//    private String portalReferers;
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        pathMatcher = new AntPathMatcher();
//    }
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        OperatorHolder.clearOperator();
//        //不要校验;直接下一个
//        boolean noNeedLogin = noNeedLogin(request , response);
//        if (noNeedLogin) {
//            filterChain.doFilter(servletRequest, response);
//            return;
//        }
//        //cookie为空要跳到登陆页面
//        String token = sessionService.getToken(request);
//        OperatorDO userDO = sessionService.getUserByToken(token);
//        if (userDO == null) {
//
//            String referer = ((RequestFacade) servletRequest).getHeader("referer");
//            if(portalReferers.contains(referer)) {
//
//                //为了统一portal做的临时兼容  后面跟gmall登录统一之后  这里去掉
//                userDO = new OperatorDO();
//                userDO.setMain(true);
//                userDO.setUsername("admin");
//                userDO.setLoginTime(new Date());
//                userDO.setOperatorId(10L);
//                userDO.setPhone("15005815457");
//                userDO.setNickname("admin");
//                userDO.setPurchaserId(2L);
//                userDO.setToken(token);
//                userDO.setType(0);
//            }else {
//
//                LoginContextHolder.clearLoginContext();
//                //token失效;前端跳转到登陆页面
//                RestResponse rest = RestResponse.fail(AdminResponseCode.TOKEN_INVALID);
//                requestService.writeJson(rest, response);
//                return;
//            }
//        }
//
//        //设置OperatorHolder上下文
//        BaseOperator baseOperator = buildBaseOperator(userDO);
//        OperatorHolder.set(baseOperator);
//
//        //登陆成功,塞到上下文;
//        BeanUtils.copyProperties(userDO, LoginContextHolder.getLoginContext());
//        //设置session
//        request.getSession().setAttribute(CommonConstants.SESSION_KEY, userDO);
//        filterChain.doFilter(servletRequest, response);
//    }
//
//    @Override
//    public void destroy() {
//    }
//
//    private BaseOperator buildBaseOperator(OperatorDO userDO) {
//        BaseOperator baseOperator = new BaseOperator();
//        baseOperator.setId(userDO.getOperatorId());
//        baseOperator.setUsername(userDO.getUsername());
//        baseOperator.setOperatorType(operatorConfig.getOperatorType());
//        baseOperator.setOutId(userDO.getPurchaserId());
//        baseOperator.setMainOperatorId(userDO.getMainAccountId());
//        baseOperator.setMain(userDO.isMain());
//        return baseOperator;
//    }
//
//    private boolean noNeedLogin(HttpServletRequest request,HttpServletResponse response) throws IOException {
//
//        //后续跟审核平台双向打通后  这里可以去掉 start
//        Cookie[] cookies = request.getCookies();
//        if(cookies != null){
//            Cookie cookie = Arrays.stream(cookies).filter(c->c.getName().equals("signToken")).findFirst().orElse(null);
//            if(cookie != null){
//                String sToken = cookie.getValue();
//                return SignatureUtils.checkDecrypt(sToken);
//            }
//        }
//
//
//        String sToken = request.getParameter("signToken");
//        if(sToken != null){
//            sToken = URLEncoder.encode(sToken);
//            Cookie wcookie = new Cookie("signToken" , sToken);
//            wcookie.setPath("/");
//            wcookie.setMaxAge(5);
//            response.addCookie(wcookie);
//            return SignatureUtils.checkDecrypt(sToken);
//        }
//        //后续跟审核平台双向打通后  这里可以去掉 end
//
//        for (String page : excludedPage1) {
//            if (pathMatcher.match(page, request.getRequestURI())) {
//                return true;
//            }
//        }
//        return false;
//    }
//    private String[] excludedPage1 = {
//            "/",
//            "/login/*",
//            "/**/home.html",
//            "/**/index.html",
//            "/**/home.html;jsessionid=*",
//            //以下是 swagger UI
////            "/swagger-ui.html",
//            "/webjars/**",
////            "/v2/**",
////            "/swagger-resources/**",
//            // 健康检查
//            "/health/check*",
//        //电子签章回调
//        "/contract/esignCallback",
//        "/api/dict/queryByKey"
//    };
//}
