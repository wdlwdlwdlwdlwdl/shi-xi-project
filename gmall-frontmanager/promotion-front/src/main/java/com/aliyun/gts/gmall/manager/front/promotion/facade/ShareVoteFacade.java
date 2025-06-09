package com.aliyun.gts.gmall.manager.front.promotion.facade;

import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.promotion.dto.output.PlayActivityVO;
import com.aliyun.gts.gmall.manager.front.promotion.dto.output.ShareFissionVO;

public interface ShareVoteFacade {
    /**
     * 创建分享
     * @param custId
     * @param activityId
     * @return
     */
    RestResponse<ShareFissionVO> launchShare(Long custId, Long activityId);

    /**
     * 助力分享
     * @param voteCustId
     * @param activityId
     * @param shareCode
     * @return
     */
    RestResponse<Boolean> voteForShare(Long voteCustId, Long activityId, String shareCode);
    /**
     * 查询分享活动
     * @param shareCode
     * @param custId
     * @param activityId
     * @return
     */
    PlayActivityVO queryShareActivity(String shareCode, Long custId, Long activityId);

    /**
     * 申请奖品，不发放，award发放到奖品列表
     * @param custId
     * @param activityId
     * @param shareCode
     * @return
     */
    RestResponse<ShareFissionVO> applySharePrize(Long custId, Long activityId, String shareCode);

}
