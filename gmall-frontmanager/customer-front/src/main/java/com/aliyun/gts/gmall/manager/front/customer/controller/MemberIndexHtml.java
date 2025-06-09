package com.aliyun.gts.gmall.manager.front.customer.controller;

import com.aliyun.gts.gmall.framework.server.permission.Permission;
import com.aliyun.gts.gmall.manager.front.customer.facade.HtmlFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/**
 * @author shifeng
 * @since 3/22/21 10:54 AM
 */
@Controller
@RequestMapping(value = "/member")
public class MemberIndexHtml {

    @Autowired
    private HtmlFacade htmlFacade;

    @RequestMapping(value = {"/pc.html"})
    @Permission(required = false)
    public ModelAndView pc(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("/pc/index");
        modelAndView.addAllObjects(htmlFacade.toPcMap());
        modelAndView.addObject("currentUser", new HashMap<>());
        return modelAndView;
    }

    @RequestMapping(value = {"/changePassword.html"})
    @Permission(required = false)
    public ModelAndView reset(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("/pc/reset");
        modelAndView.addAllObjects(htmlFacade.toPcMap());
        modelAndView.addObject("currentUser", new HashMap<>());
        return modelAndView;
    }

    @RequestMapping(value = {"/pcShop.html"})
    @Permission(required = false)
    public ModelAndView pcShop(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("/pc/shop");
        modelAndView.addAllObjects(htmlFacade.toPcMap());
        modelAndView.addObject("currentUser", new HashMap<>());
        return modelAndView;
    }

    @RequestMapping(value = {"/shopBase.html"})
    @Permission(required = false)
    public ModelAndView shopBase(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("/pc/shopBase");
        modelAndView.addAllObjects(htmlFacade.toPcMap());
        modelAndView.addObject("currentUser", new HashMap<>());
        return modelAndView;
    }

    @RequestMapping(value = {"/pcPaySuccess.html"})
    @Permission(required = false)
    public ModelAndView pcPaySuccess(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("/pc/paySuccess");
        modelAndView.addAllObjects(htmlFacade.toPcMap());
        modelAndView.addObject("currentUser", new HashMap<>());
        return modelAndView;
    }

    @RequestMapping(value = {"/equity.html"})
    @Permission(required = false)
    public ModelAndView equity(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("/pc/equity");
        modelAndView.addAllObjects(htmlFacade.toPcMap());
        modelAndView.addObject("currentUser", new HashMap<>());
        return modelAndView;
    }

    @RequestMapping(value = {"/mobile.html"})
    @Permission(required = false)
    public ModelAndView mobile(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("/mobile/index");
        modelAndView.addAllObjects(htmlFacade.toMobileMap());
        modelAndView.addObject("currentUser", new HashMap<>());
        return modelAndView;
    }

    @RequestMapping(value = {"/mobileLogin.html"})
    @Permission(required = false)
    public ModelAndView mobileLogin(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("/mobile/login");
        modelAndView.addAllObjects(htmlFacade.toMobileMap());
        modelAndView.addObject("currentUser", new HashMap<>());
        return modelAndView;
    }

    @RequestMapping(value = {"/mobileShop.html"})
    @Permission(required = false)
    public ModelAndView mobileShop(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("/mobile/shop");
        modelAndView.addAllObjects(htmlFacade.toMobileMap());
        modelAndView.addObject("currentUser", new HashMap<>());
        return modelAndView;
    }


    @RequestMapping(value = {"/mobileMember.html"})
    @Permission(required = false)
    public ModelAndView mobileMember(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("/mobile/member");
        modelAndView.addAllObjects(htmlFacade.toMobileMap());
        modelAndView.addObject("currentUser", new HashMap<>());
        return modelAndView;
    }

    @RequestMapping(value = {"/mobilePaySuccess.html"})
    @Permission(required = false)
    public ModelAndView mobilePaySuccess(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView("/mobile/paySuccess");
        modelAndView.addAllObjects(htmlFacade.toMobileMap());
        modelAndView.addObject("currentUser", new HashMap<>());
        return modelAndView;
    }

}
