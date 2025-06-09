package com.aliyun.gts.gmall.manager.front.customer.controller;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.ApplyCouponRestCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.BatchApplyCouponRestCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.CustomerCampSubscribeCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.CustomerCampUnSubscribeCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.*;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.*;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.customer.CustomerCampSubscribeVO;
import com.aliyun.gts.gmall.manager.front.customer.facade.CustomerCampSubscribeFacade;
import com.aliyun.gts.gmall.manager.front.customer.facade.PromotionFacade;
import com.aliyun.gts.gmall.platform.promotion.api.dto.account.AcBookFailureDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * 会员信息操作
 *
 * @author tiansong
 */
@Api(value = "会员信息操作", tags = {"promotion", "token"})
@RestController
public class PromotionController {
    @Autowired
    private PromotionFacade promotionFacade;

    @Autowired
    private CustomerCampSubscribeFacade customerCampSubscribeFacade;

    @ApiOperation(value = "领券接口")
    @PostMapping(name = "applyCoupon", value = "/api/customer/promotion/applyCoupon/token")
    public @ResponseBody
    RestResponse<CouponInstanceVO> applyCoupon(@RequestBody ApplyCouponRestCommand command) {
        return promotionFacade.applyCoupon(command);
    }

    @ApiOperation(value = "我的优惠券列表接口")
    @PostMapping(name = "listCoupons", value = "/api/customer/promotion/listCoupons/token")
    public @ResponseBody
    RestResponse<PageInfo<CouponInstanceVO>> listCoupons(@RequestBody CustomerCouponQuery query) {
        return promotionFacade.queryCoupons(query);
    }

    @ApiOperation(value = "根据code查券信息")
    @PostMapping(name = "queryCoupon", value = "/api/customer/promotion/queryCoupon")
    public @ResponseBody
    RestResponse<CouponInstanceVO> queryCoupon(@RequestBody ByCodeCouponQuery query) {
        CouponInstanceVO vo = promotionFacade.queryCoupon(query);
        return RestResponse.ok(vo);
    }


    @ApiOperation(value = "根据code查券信息")
    @PostMapping(name = "queryCouponDetail", value = "/api/customer/promotion/queryCouponDetail")
    public @ResponseBody
    RestResponse<CouponInstanceDetailVO> queryCouponDetail(@RequestBody ByCodeCouponQuery query) {
        CouponInstanceDetailVO vo = promotionFacade.queryCouponDetail(query);
        return RestResponse.ok(vo);
    }

    @ApiOperation(value = "积分查询")
    @PostMapping(name = "getAccountBookSum", value = "/api/customer/promotion/getAccountBookSum/token")
    public @ResponseBody
    RestResponse<String> getAccountBookSum(@RequestBody AccountBookRestQuery query) {
        RestResponse<Long> response = promotionFacade.queryAccountBook(query);
        String display =  ItemUtils.pointDisplay(response.getData());
        return RestResponse.okWithoutMsg(display);
    }

    @ApiOperation(value = "积分明细查询")
    @PostMapping(name = "listAcBookRecords", value = "/api/customer/promotion/listAcBookRecords/token")
    public @ResponseBody
    RestResponse<PageInfo<AcBookRecordVO>> listAcBookRecords(@RequestBody AccountBookRecordRestQuery query) {
        return promotionFacade.queryAcBookRecords(query);
    }

    @ApiOperation(value = "即将失效积分查询")
    @PostMapping(name = "getAccountBookFailure", value = "/api/customer/promotion/getAccountBookFailure/token")
    public @ResponseBody
    RestResponse<AcBookFailureDTO> getAccountBookFailure(@RequestBody AccountBookRestQuery query) {
        RestResponse<AcBookFailureDTO> response = promotionFacade.getAccountBookFailure(query);
        AcBookFailureDTO data = response.getData();
        if (Objects.isNull(data)){
            return RestResponse.okWithoutMsg(null);
        }
        data.setFailureAssetsStr(ItemUtils.pointDisplay(data.getFailureAssets()));
        return RestResponse.okWithoutMsg(data);
    }

    @ApiOperation(value = "我的新人优惠券列表")
    @PostMapping(name = "listNewUserCoupons", value = "/api/customer/promotion/listNewUserCoupons/token")
    public @ResponseBody
    RestResponse<CouponInstanceReceiveVO> listNewUserCoupons(@RequestBody CouponAndCampaignQuery query) {
        return promotionFacade.queryNewUserCoupons(query);
    }

    @ApiOperation(value = "批量领券接口")
    @PostMapping(name = "batchApplyCoupon", value = "/api/customer/promotion/batchApplyCoupon/token")
    public @ResponseBody
    RestResponse<CouponBatchApplyRetVO> batchApplyCoupon(@RequestBody BatchApplyCouponRestCommand command) {
        return promotionFacade.batchApplyCoupon(command);
    }

    @ApiOperation(value = "查询优惠券详情及规则")
    @PostMapping(name = "queryCouponDetail", value = "/api/customer/promotion/queryCouponDetail/token")
    public @ResponseBody
    RestResponse<PromCampaignVO> queryCouponDetail(@RequestBody ByIdCouponQuery query) {
        return promotionFacade.queryCouponDetail(query);
    }



    @ApiOperation(value = "订阅活动")
    @PostMapping(name = "campSubscribe", value = "/api/customer/promotion/campSubscribe")
    public @ResponseBody
    RestResponse<Boolean> campSubscribe(@RequestBody CustomerCampSubscribeCommand campSubscribeCommand) {
        return customerCampSubscribeFacade.campSubscribe(campSubscribeCommand);
    }


    @ApiOperation(value = "取消订阅活动")
    @PostMapping(name = "campUnSubscribe", value = "/api/customer/promotion/campUnSubscribe")
    public @ResponseBody
    RestResponse<Boolean> campUnSubscribe(@RequestBody CustomerCampUnSubscribeCommand campUnSubscribeCommand) {
        return customerCampSubscribeFacade.campUnSubscribe(campUnSubscribeCommand);
    }




    @ApiOperation(value = "查询订阅信息")
    @PostMapping(name = "getSubscribes", value = "/api/customer/promotion/getSubscribes")
    public @ResponseBody
    RestResponse<PageInfo<CustomerCampSubscribeVO>> getSubscribes(@RequestBody CustomerCampSubscribeQuery subscribeQuery) {
        RestResponse<PageInfo<CustomerCampSubscribeVO>> subscribes = customerCampSubscribeFacade.getSubscribes(subscribeQuery);
        return subscribes;
    }



}
