package com.aliyun.gts.gmall.manager.front.promotion.controller;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.promotion.dto.input.command.PointExchangeCouponQuery;
import com.aliyun.gts.gmall.manager.front.promotion.facade.PointExchangeCouponFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/9/26 10:28
 */
@RestController
@Api(value = "积分兑换", tags = {"pointExchange"})
public class PointexchangeController {
    @Resource
    private PointExchangeCouponFacade pointExchangeCouponFacade;

    @PostMapping(name="award", value = "/api/point/pointExchangeCoupon")
    @ApiOperation("pointExchangeCoupon")
    public RestResponse<Boolean> queryAward(@RequestBody PointExchangeCouponQuery query) {
        ParamUtil.nonNull(query.getCouponId(),I18NMessageUtils.getMessage("points.coupon")+"id"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "积分优惠券id不能为空"
        ParamUtil.nonNull(query.getApp(),"app"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# 不能为空"
        return pointExchangeCouponFacade.pointExchangeCoupon(query);
    }

}
