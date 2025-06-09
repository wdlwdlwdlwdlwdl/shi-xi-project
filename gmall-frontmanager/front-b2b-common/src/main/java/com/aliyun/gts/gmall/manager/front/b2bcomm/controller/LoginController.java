package com.aliyun.gts.gmall.manager.front.b2bcomm.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author haibin.xhb
 * @description: TODO 该页面是不需要登陆的
 * @date 2021/2/3 19:32
 */
//@Controller
//@RequestMapping(value = "/login")
//@Slf4j
//public class LoginController {
//    @Resource
//    private SessionService sessionService;
//    @Autowired
//    private LoginService loginService;
//    @Resource
//    private LoginConverter converter;
//
//    @Resource
//    private FrontProperties frontProperties;
//
//    @RequestMapping(value = "/index.html")
//    public ModelAndView index(HttpServletRequest request) {
//        ModelAndView modelAndView = new ModelAndView("/login");
//        //前端页面信息
//        modelAndView.addAllObjects(frontProperties.toMap());
//        modelAndView.addObject("currentUser", null);
//        return modelAndView;
//    }
//
//    /**
//     * 校验token
//     *
//     * @param request
//     * @return
//     */
//    @RequestMapping(value = "/checkToken")
//    public RestResponse<JSONObject> checkToken(HttpServletRequest request) {
//        String token = sessionService.getToken(request);
//        OperatorDO operatorDO = sessionService.getUserByToken(token);
//        if (operatorDO != null) {
//            //返回结果
//            JSONObject result = new JSONObject();
//            result.put(CommonConstants.token, token);
//            return RestResponse.ok(result);
//        }
//        return RestResponse.fail("1001", "token失效");
//    }
//
//    /**
//     * 登陆接口,传入用户名密码
//     *
//     * @param user
//     * @return
//     */
//    @RequestMapping(value = "/lg")
//    @ResponseBody
//    public RestResponse<OperatorVO> login(@RequestBody JSONObject user) {
//        String userName = user.getString("username");
//        String password = user.getString("password");
//        ParamUtil.nonNull(userName, "userName不能为空");
//        ParamUtil.nonNull(password, "password不能为空");
//        OperatorDO operatorDO = loginService.loginByPwd(userName, password);
//        if (operatorDO == null) {
//            return RestResponse.fail(CommonResponseCode.ErrorPassword.getCode(), "密码错误或用户不存在");
//        }
//        OperatorVO vo = converter.operator2Vo(operatorDO);
//        //返回结果
//        return RestResponse.ok(vo);
//    }
//
//    /**
//     * 登出
//     *
//     * @return
//     */
//    @RequestMapping(value = "/out")
//    @ResponseBody
//    public RestResponse<Boolean> loginOut(HttpServletRequest request) {
//        String token = sessionService.getToken(request);
//        sessionService.expireToken(token);
//        return RestResponse.ok(true);
//    }
//
//    /**
//     * 校验token
//     *
//     * @return
//     */
//    @RequestMapping(value = "/openLogin")
//    @ResponseBody
//    public JSONObject openLogin(@RequestBody JSONObject param) {
//        JSONObject result = new JSONObject();
//        String token = param.getString("token");
//        OperatorDO operatorDO = sessionService.getUserByToken(token);
//        if (operatorDO == null) {
//            result.put("success", false);
//            result.put("reason", "session not found");
//            return result;
//        }
//        result.put("success", true);
//        result.put("userCode", operatorDO.getPhone());
//        result.put("displayName", operatorDO.getUsername());
//        return result;
//    }
//
//    /**
//     * 校验token
//     *
//     * @return
//     */
//    @RequestMapping(value = "/imLogin")
//    @ResponseBody
//    public RestResponse<JSONObject> imLogin(@RequestBody JSONObject param) {
//        try {
//            String token = param.getString("token");
//            OperatorDO operatorDO = sessionService.getUserByToken(token);
//            if (operatorDO == null) {
//                return RestResponse.fail("1001", "session not found");
//            }
//            JSONObject object = new JSONObject();
//            object.put("outId", operatorDO.getOperatorId());
//            object.put("app", "purchaser");
//            object.put("nickname", operatorDO.getNickname());
//            return RestResponse.ok(object);
//        } catch (Exception e) {
//            return RestResponse.fail("1001", "exp:" + e.getMessage());
//        }
//    }
//}
