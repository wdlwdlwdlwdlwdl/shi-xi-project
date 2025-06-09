package com.aliyun.gts.gmall.manager.front.customer.controller;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.ShopVipJoinCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.ShopVipQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.customer.ShopVipVO;
import com.aliyun.gts.gmall.manager.front.customer.facade.ShopVipFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "店铺会员操作", tags = {"customer", "token"})
@RestController
public class ShopVipController {

    @Autowired
    private ShopVipFacade shopVipFacade;

    @ApiOperation(value = "查询店铺会员信息")
    @PostMapping(value = "/api/customer/shopvip/query/token")
    public @ResponseBody
    RestResponse<ShopVipVO> queryShopVip(@RequestBody ShopVipQuery query) {
        return RestResponse.okWithoutMsg(shopVipFacade.queryShopVip(query));
    }

    @ApiOperation(value = "加入店铺会员")
    @PostMapping(value = "/api/customer/shopvip/join/token")
    public @ResponseBody
    RestResponse joinShopVip(@RequestBody ShopVipJoinCommand join) {
        shopVipFacade.joinShopVip(join);
        return RestResponse.okWithoutMsg(null);
    }
}
