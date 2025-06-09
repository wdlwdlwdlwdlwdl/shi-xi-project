package com.aliyun.gts.gmall.manager.front.customer.controller;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.common.dto.PageLoginRestQuery;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.CustomerItemVisitHisAdapter;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.AddVisitHisRestCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.customer.ItemVisitHisVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会员商品
 * @author FaberWong
 */
@Api(value = "会员商品", tags = {"item"})
@RestController
public class CustomerItemController {
    
    @Autowired
    private CustomerItemVisitHisAdapter customerItemVisitHisAdapter;

    @ApiOperation(value = "加入浏览历史")
    @PostMapping(name = "addVisitHis", value = "/api/customer/item/addVisitHis/token")
    public @ResponseBody
    RestResponse<Boolean> addVisitHis(@RequestBody AddVisitHisRestCommand command) {
        return RestResponse.okWithoutMsg(customerItemVisitHisAdapter.addVisitHis(command));
    }

    @ApiOperation(value = "浏览历史")
    @PostMapping(name = "listVisitHis", value = "/api/customer/item/listVisitHis/token")
    public @ResponseBody
    RestResponse<PageInfo<ItemVisitHisVO>> listVisitHis(@RequestBody PageLoginRestQuery query) {
        return customerItemVisitHisAdapter.listVisitHis(query);
    }
    
}
