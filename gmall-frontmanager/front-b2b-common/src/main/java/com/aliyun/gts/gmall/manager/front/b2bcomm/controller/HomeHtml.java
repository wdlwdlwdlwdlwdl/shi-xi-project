package com.aliyun.gts.gmall.manager.front.b2bcomm.controller;

import com.aliyun.gts.gmall.manager.front.b2bcomm.configuration.FrontProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author haibin.xhb
 * @description: 只要继承这个就可以了;
 * @date 2021/1/15 18:31
 */
public abstract class HomeHtml {

    @Resource
    private FrontProperties frontProperties;

    @RequestMapping(value = "/home.html")
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("/index");
        //构建前端页面信息
        buildFront(modelAndView, request);
        // Important的一个地址，因为前后端分离了的，这个页面是后续页面的异步请求的一个载体，所以必须要是登陆状态的，如果非登陆状态，直接抛出未认证异常，让shiro进行跳转处理
        modelAndView.addObject("currentUser", null);
        return modelAndView;
    }

    /**
     * 构建前端信息
     */
    protected void buildFront(ModelAndView view, HttpServletRequest request) {
        String path = request.getServletPath();
        //解析应用名称如:promotion
        //前端页面信息
        view.addAllObjects(frontProperties.toMap());
    }

    /**
     * 解析应用名称
     *
     * @param path
     * @return
     */
    private static String parseApp(String path) {
        if (path == null) {
            return "null";
        }
        //以"/"开头的
        if (path.startsWith("/")) {
            path = path.substring(1);
            int index = path.indexOf("/");
            return path.substring(0, index);
        }
        return "null";
    }
}
