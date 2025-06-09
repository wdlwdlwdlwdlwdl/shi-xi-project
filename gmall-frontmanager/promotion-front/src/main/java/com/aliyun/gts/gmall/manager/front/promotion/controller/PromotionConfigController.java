package com.aliyun.gts.gmall.manager.front.promotion.controller;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.promotion.dto.output.PromotionConfigVO;
import com.aliyun.gts.gmall.manager.front.promotion.facade.PromotionConfigFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "营销配置", tags = {"promotionConfig"})
public class PromotionConfigController {

    @Autowired
   private PromotionConfigFacade promotionConfigFacade;

    @PostMapping(name="promotionConfig", value = "/api/customer/promotionConfig/evaluationPoint")
    @ApiOperation("获取活动详情")
    public RestResponse<PromotionConfigVO> evaluationPoint() {
        try {
            PromotionConfigVO vo = promotionConfigFacade.evaluationPoint();
            return RestResponse.okWithoutMsg(vo);
        } catch (Exception e) {
            return RestResponse.fail("101", e.getMessage());
        }
    }


}
