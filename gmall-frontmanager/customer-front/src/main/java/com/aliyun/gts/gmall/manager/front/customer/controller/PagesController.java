package com.aliyun.gts.gmall.manager.front.customer.controller;

import com.aliyun.gts.gmall.framework.server.permission.Permission;
import com.aliyun.gts.gmall.middleware.minio.GmallMinioClient;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;


@RestController
@RequestMapping(value = "/pages")
public class PagesController {

    @Autowired(required = false)
    GmallMinioClient publicGmallMinioClient;

    @Value("${minio.bucket.front:}")
    private String bucket;

    @RequestMapping(value = {"/**"})
    @Permission(required = false)
    public void htmlTemplate(HttpServletRequest request, HttpServletResponse response) {
        try {
            String ossFileName = request.getServletPath().substring(1);
            String htmlTemplate = publicGmallMinioClient.getContent(bucket, ossFileName);
            InputStream in  = new ByteArrayInputStream(htmlTemplate.getBytes());
            response.setContentType("text/html");
            try (OutputStream out = response.getOutputStream()) {
                IOUtils.copy(in, out);
                out.flush();
            }
        } catch (Exception e) {

        }
    }

}
