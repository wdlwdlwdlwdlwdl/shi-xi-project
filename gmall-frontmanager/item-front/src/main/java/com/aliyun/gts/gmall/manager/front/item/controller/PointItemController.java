package com.aliyun.gts.gmall.manager.front.item.controller;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.center.item.api.dto.input.PointItemQueryReq;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.item.dto.output.ItemDetailVO;
import com.aliyun.gts.gmall.manager.front.item.dto.output.PointItemQueryVO;
import com.aliyun.gts.gmall.manager.front.item.facade.PointItemFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 积分商品页面
 */
@Api(value = "积分商品页面", tags = {"item"})
@RestController
public class PointItemController {

    @Autowired
    private PointItemFacade pointItemFacade;

    @ApiOperation("积分商城列表页面接口(查询)")
    @PostMapping(name = "queryPointItemPage", value = "/api/item/queryPointItemPage")
    public @ResponseBody
    RestResponse<PageInfo<PointItemQueryVO>> queryPointItemPage(@RequestBody PointItemQueryReq pointItemQueryReq) {
        ParamUtil.nonNull(pointItemQueryReq.getPageIndex(),I18NMessageUtils.getMessage("pagination.params")+"pageIndex"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "分页参数pageIndex不能为空"
        ParamUtil.nonNull(pointItemQueryReq.getPageSize(),I18NMessageUtils.getMessage("pagination.params")+"pageSize"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "分页参数pageSize不能为空"
        if(pointItemQueryReq.getPageSize()>1024){
            return RestResponse.fail("","pageSize"+I18NMessageUtils.getMessage("can.not.greater.than")+"1024");  //# 不能大于
        }
        return pointItemFacade.queryPointItemPage(pointItemQueryReq);
    }

    @ApiOperation("积分商城详情页面接口")
    @PostMapping(name = "queryPointItemDetail", value = "/api/item/queryPointItemDetail")
    public @ResponseBody
    RestResponse<ItemDetailVO> queryPointItemDetail(@RequestBody PointItemQueryReq pointItemQueryReq) {
        ParamUtil.nonNull(pointItemQueryReq.getBizId(),I18NMessageUtils.getMessage("product")+"bizId"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "商品bizId不能为空"
        return pointItemFacade.queryPointItemDetail(pointItemQueryReq);
    }


    @ApiOperation("优惠券列表页面接口")
    @PostMapping(name = "queryPointCouponPage", value = "/api/item/queryPointCouponPage")
    public @ResponseBody
    RestResponse<PageInfo<PointItemQueryVO>> queryPointCouponPage(@RequestBody PointItemQueryReq pointItemQueryReq) {
        ParamUtil.nonNull(pointItemQueryReq.getPageIndex(),I18NMessageUtils.getMessage("pagination.params")+"pageIndex"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "分页参数pageIndex不能为空"
        ParamUtil.nonNull(pointItemQueryReq.getPageSize(),I18NMessageUtils.getMessage("pagination.params")+"pageSize"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "分页参数pageSize不能为空"
        if(pointItemQueryReq.getPageSize()>1024){
            return RestResponse.fail("","pageSize"+I18NMessageUtils.getMessage("can.not.greater.than")+"1024");  //# 不能大于
        }
        return pointItemFacade.queryPointCouponPage(pointItemQueryReq);
    }


    @ApiOperation("优惠券详情接口")
    @PostMapping(name = "queryPointCouponDetail", value = "/api/item/queryPointCouponDetail")
    public @ResponseBody
    RestResponse<PointItemQueryVO> queryPointCouponDetail(@RequestBody PointItemQueryReq pointItemQueryReq) {
        ParamUtil.nonNull(pointItemQueryReq.getBizId(),I18NMessageUtils.getMessage("product")+"bizId"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "商品bizId不能为空"
        return pointItemFacade.queryPointCouponDetail(pointItemQueryReq);
    }
}
