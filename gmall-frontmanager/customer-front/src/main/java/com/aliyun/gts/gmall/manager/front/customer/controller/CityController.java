package com.aliyun.gts.gmall.manager.front.customer.controller;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.common.dto.CityRequestQuery;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.CityAdapter;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.CityVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

/**
 * 城市查询
 */
@Slf4j
@RestController
@RequestMapping("/api/city")
public class CityController {

    // 城市
    @Autowired
    private CityAdapter cityAdapter;

    @RequestMapping("/queryAll")
    public RestResponse<CityVO> queryAll(@RequestBody CityRequestQuery cityRequestQuery) {
        return RestResponse.okWithoutMsg(cityAdapter.queryAll(cityRequestQuery));
    }
}
