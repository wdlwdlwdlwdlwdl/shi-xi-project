package com.aliyun.gts.gmall.manager.front.promotion.controller;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.MessageSendManager;
import com.aliyun.gts.gmall.manager.front.promotion.dto.input.command.ShareFissionCommand;
import com.aliyun.gts.gmall.manager.front.promotion.dto.output.PlayActivityVO;
import com.aliyun.gts.gmall.manager.front.promotion.dto.output.ShareFissionVO;
import com.aliyun.gts.gmall.manager.front.promotion.facade.ShareVoteFacade;
import com.aliyun.gts.gmall.platform.promotion.common.message.VotePrizeMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@Api(value = "分享有礼", tags = {"promotion"})
@Slf4j
public class ShareVoteController {

    //分享topic
    @Value("${front-manager.message.vote.topic}")
    private String voteTopic;

    @Resource
    private ShareVoteFacade shareVoteFacade;

    @Autowired
    private MessageSendManager messageSendManager;

    @PostMapping(name="launch", value="/api/customer/promotion/share/launch")
    @ApiOperation("分享")
    public RestResponse<ShareFissionVO> launchShare(@RequestBody ShareFissionCommand request) {
        Long activityId = request.getActivityId();
        Long custId = request.getCustId();
        RestResponse<ShareFissionVO> response = shareVoteFacade.launchShare(custId, activityId);
        return response;
    }

    @PostMapping(name="queryShare", value = "/api/customer/promotion/share/queryShare")
    @ApiOperation("获取活动详情")
    public RestResponse<PlayActivityVO> queryShare(@RequestBody ShareFissionCommand request) {
        try {
            String shareCode = request.getShareCode();
            Long activityId = request.getActivityId();
            Long custId = request.getCustId();
            PlayActivityVO res = shareVoteFacade.queryShareActivity(shareCode,  custId,  activityId);
            return RestResponse.okWithoutMsg(res);
        }catch (Exception e){
            log.error("queryShare,异常",e.getMessage());
            return RestResponse.fail("1001","queryShare"+e.getMessage());
        }
    }

    @PostMapping(name="vote", value="/api/customer/promotion/share/vote")
    @ApiOperation("助力")
    public RestResponse<Boolean> vote(@RequestBody ShareFissionCommand request) {
        Long custId = request.getCustId();
        RestResponse<Boolean> vote = shareVoteFacade.voteForShare(custId, request.getActivityId(), request.getShareCode());
        // 发送自动领取的消息
        if (request.getActivityId() != null && StringUtils.isNotEmpty(request.getShareCode()) &&
            vote != null && Boolean.TRUE.equals(vote.isSuccess())) {
            VotePrizeMessage votePrizeMessage = new VotePrizeMessage();
            votePrizeMessage.setCustId(custId);
            votePrizeMessage.setActivityId(request.getActivityId());
            votePrizeMessage.setShareCode(request.getShareCode());
            messageSendManager.sendMessage(votePrizeMessage, voteTopic, "vote");
        }
        return vote;
    }

    @PostMapping(name="applyPrize", value="/api/customer/promotion/share/applyPrize")
    @ApiOperation("申请奖品,不发放奖品,去奖品列表发放")
    public RestResponse<ShareFissionVO> applyPrize(@RequestBody ShareFissionCommand request) {
        return shareVoteFacade.applySharePrize(request.getCustId(), request.getActivityId(), request.getShareCode());
    }
}
