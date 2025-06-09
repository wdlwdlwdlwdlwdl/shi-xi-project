package com.aliyun.gts.gmall.manager.front.promotion.facade;

import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.manager.front.promotion.dto.input.command.AwardQuery;
import com.aliyun.gts.gmall.manager.front.promotion.dto.input.command.LotteryCommand;
import com.aliyun.gts.gmall.manager.front.promotion.dto.output.PlayAwardVO;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/9/15 10:03
 */
public interface AwardFacade {
    /**
     * 抽奖接口
     * @param request
     * @return
     */
    RestResponse<PlayAwardVO> lottery(LotteryCommand request);

    /**
     * 查询奖品记录
     * @param query
     * @return
     */
    RestResponse<Integer> queryAwardNumber(AwardQuery query);

    /**
     * 查询奖品记录
     * @param query
     * @return
     */
    RestResponse<PageInfo<PlayAwardVO>> queryAward(AwardQuery query);

    /**
     * 领取奖品
     * @param query
     * @return
     */
    RestResponse<Boolean> take(AwardQuery query);
}
