package com.aliyun.gts.gmall.manager.front.promotion.controller;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.promotion.dto.input.command.AwardQuery;
import com.aliyun.gts.gmall.manager.front.promotion.dto.output.PlayAwardVO;
import com.aliyun.gts.gmall.manager.front.promotion.facade.AwardFacade;
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
@Api(value = "抽奖", tags = {"award"})
public class AwardController {
    @Resource
    private AwardFacade awardFacade;


    @PostMapping(name="award", value = "/api/customer/promotion/award/number")
    @ApiOperation("统计待领奖数量接口")
    public RestResponse<Integer> queryAwardNumber(@RequestBody AwardQuery query) {
        return awardFacade.queryAwardNumber(query);
    }

    @PostMapping(name="award", value = "/api/customer/promotion/award/query")
    @ApiOperation("奖品列表")
    public RestResponse<PageInfo<PlayAwardVO>> queryAward(@RequestBody AwardQuery query) {
        ParamUtil.nonNull(query.getPage(),I18NMessageUtils.getMessage("pagination.required"));  //# "分页信息不能为空"
        return awardFacade.queryAward(query);
    }

    @PostMapping(name="award", value = "/api/customer/promotion/award/take")
    @ApiOperation("领奖接口")
    public RestResponse<Boolean> take(@RequestBody AwardQuery query) {
        ParamUtil.nonNull(query.getPage(),I18NMessageUtils.getMessage("pagination.required"));  //# "分页信息不能为空"
        return awardFacade.take(query);
    }
}
