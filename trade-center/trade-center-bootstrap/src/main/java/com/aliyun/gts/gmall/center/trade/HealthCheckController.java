package com.aliyun.gts.gmall.center.trade;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/health")
public class HealthCheckController {

    @RequestMapping(value = "/check")
    public RestResponse<String> check() {
        return RestResponse.ok(I18NMessageUtils.getMessage("request.success"));  //# "请求成功"
    }

}
