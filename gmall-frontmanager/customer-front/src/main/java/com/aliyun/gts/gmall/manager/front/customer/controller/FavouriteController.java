package com.aliyun.gts.gmall.manager.front.customer.controller;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.common.dto.PageLoginRestQuery;
import com.aliyun.gts.gmall.manager.front.common.util.ItemUtils;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.FavouriteAdapter;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.AddFavouriteRestCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.ApplyCouponRestCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.BatchApplyCouponRestCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.DeleteFavouriteRestCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.AccountBookRecordRestQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.AccountBookRestQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.ByCodeCouponQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.ByIdCouponQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.CouponAndCampaignQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.CustomerCouponQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.CustomerFavouriteQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.AcBookRecordVO;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.CouponBatchApplyRetVO;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.CouponInstanceReceiveVO;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.CouponInstanceVO;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.PromCampaignVO;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.customer.CustomerFavouriteVO;
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
 * 会员收藏夹
 * @author FaberWong
 */
@Api(value = "会员收藏夹", tags = {"favourite"})
@RestController
public class FavouriteController {
    
    @Autowired
    private FavouriteAdapter favouriteAdapter;

    /**
     * 加入收藏
     * @param command
     * @return
     */
    @ApiOperation(value = "加入收藏")
    @PostMapping(name = "addFavourite", value = "/api/customer/addFavourite/token")
    public @ResponseBody
    RestResponse<Boolean> addFavourite(@RequestBody AddFavouriteRestCommand command) {
        return RestResponse.okWithoutMsg(favouriteAdapter.addFavourite(command));
    }

    /**
     * 收藏列表
     * @param query
     * @return
     */
    @ApiOperation(value = "收藏列表")
    @PostMapping(name = "listFavourite", value = "/api/customer/listFavourite/token")
    public @ResponseBody
    RestResponse<PageInfo<CustomerFavouriteVO>> listFavourite(@RequestBody CustomerFavouriteQuery query) {
        return favouriteAdapter.listFavourite(query);
    }

    /**
     *  移除收藏
     * @param command
     * @return
     */
    @ApiOperation(value = "移除收藏")
    @PostMapping(name = "deleteFavourite", value = "/api/customer/deleteFavourite/token")
    public @ResponseBody
    RestResponse<Boolean> deleteFavourite(@RequestBody DeleteFavouriteRestCommand command) {
        return RestResponse.okWithoutMsg(favouriteAdapter.deleteFavourite(command));
    }
}
