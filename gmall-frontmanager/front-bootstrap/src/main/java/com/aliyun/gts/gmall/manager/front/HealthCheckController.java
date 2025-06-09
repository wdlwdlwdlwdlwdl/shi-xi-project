package com.aliyun.gts.gmall.manager.front;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.common.dto.EmptyRestQuery;
import com.aliyun.gts.gmall.manager.front.trade.facade.OrderFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康检查
 */
@Slf4j
@RestController
@RequestMapping(value = "/health/")
public class HealthCheckController {
    @Autowired
    private OrderFacade orderFacade;

    @GetMapping(value = "check")
    @ResponseBody
    public RestResponse<Boolean> check(EmptyRestQuery query) {
        return RestResponse.ok(orderFacade != null);
    }
}
