package com.aliyun.gts.gmall.manager.front.promotion.facade;

import com.aliyun.gts.gmall.manager.front.promotion.dto.output.PlayActivityVO;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/9/13 18:37
 */
public interface PlayActivityFacade {
    /**
     * 查询分享有礼活动
     * @return
     */
    public PlayActivityVO queryShareActivity(Long activityId);

    /**
     * 查询抽奖活动
     * @param activityId
     * @return
     */
    public PlayActivityVO queryLotteryActivity(Long activityId);
}
