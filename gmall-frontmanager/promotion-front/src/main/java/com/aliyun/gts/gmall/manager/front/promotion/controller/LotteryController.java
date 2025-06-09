package com.aliyun.gts.gmall.manager.front.promotion.controller;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.promotion.dto.input.command.LotteryCommand;
import com.aliyun.gts.gmall.manager.front.promotion.dto.output.PlayActivityVO;
import com.aliyun.gts.gmall.manager.front.promotion.dto.output.PlayAwardVO;
import com.aliyun.gts.gmall.manager.front.promotion.facade.AwardFacade;
import com.aliyun.gts.gmall.manager.front.promotion.facade.PlayActivityFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/9/15 9:36
 */
@RestController
@Api(value = "抽奖", tags = {"lottery"})
public class LotteryController {
    @Resource
    private PlayActivityFacade playActivityFacade;
    @Resource
    private AwardFacade awardFacade;

    @PostMapping(name="queryLottery", value = "/api/customer/promotion/lottery/queryLottery")
    @ApiOperation("获取活动详情")
    public RestResponse<PlayActivityVO> queryLottery(@RequestBody LotteryCommand request) {
        try {
            Long activityId = request.getActivityId();
            PlayActivityVO res = playActivityFacade.queryLotteryActivity(activityId);
            return RestResponse.okWithoutMsg(res);
        }catch (Exception e){
            return RestResponse.fail("101",e.getMessage());
        }
    }

    @PostMapping(name="lottery", value = "/api/customer/promotion/lottery/do")
    @ApiOperation("抽奖")
    public RestResponse<PlayAwardVO> lottery(@RequestBody LotteryCommand request) {
        ParamUtil.nonNull(request.getActivityId(),I18NMessageUtils.getMessage("event")+"ID"+I18NMessageUtils.getMessage("cannot.be.empty"));  //# "活动ID不能为空"
        return awardFacade.lottery(request);
    }

}
