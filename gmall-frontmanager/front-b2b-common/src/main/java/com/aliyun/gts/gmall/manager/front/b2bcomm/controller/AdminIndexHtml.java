package com.aliyun.gts.gmall.manager.front.b2bcomm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author gshine
 * @since 3/22/21 10:54 AM
 */
@Controller
public class AdminIndexHtml {

    @RequestMapping(value = {"/home.html", "/index.html", "/"})
    public String index(HttpServletRequest request, HttpServletResponse response) {
        return "redirect:/sourcing/home.html";
    }
}
