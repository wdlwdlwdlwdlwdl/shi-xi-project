package com.aliyun.gts.gmall.manager.front.promotion.facade.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.center.promotion.api.dto.input.PointLotteryFlowReq;
import com.aliyun.gts.gmall.center.promotion.api.dto.model.PlayActivityDTO;
import com.aliyun.gts.gmall.center.promotion.api.dto.model.PlayPrizeRelateDTO;
import com.aliyun.gts.gmall.center.promotion.api.facade.PointLotteryFlowReadFacade;
import com.aliyun.gts.gmall.center.promotion.common.type.LotteryType;
import com.aliyun.gts.gmall.center.promotion.common.type.PlayActivityStatus;
import com.aliyun.gts.gmall.center.promotion.common.type.PrizeType;
import com.aliyun.gts.gmall.framework.api.exception.GmallInvalidArgumentException;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.framework.api.util.ParamUtil;
import com.aliyun.gts.gmall.manager.front.common.util.DateUtils;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.ByCodeCouponQuery;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.CouponInstanceVO;
import com.aliyun.gts.gmall.manager.front.customer.facade.PromotionFacade;
import com.aliyun.gts.gmall.manager.front.promotion.adaptor.PromotionApiAdaptor;
import com.aliyun.gts.gmall.manager.front.promotion.common.converter.PlayActivityConvert;
import com.aliyun.gts.gmall.manager.front.promotion.dto.output.PlayActivityVO;
import com.aliyun.gts.gmall.manager.front.promotion.dto.output.PlayPrizeVo;
import com.aliyun.gts.gmall.manager.front.promotion.facade.PlayActivityFacade;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/9/13 18:40
 */
@Service
public class PlayActivityFacadeImpl implements PlayActivityFacade {
    @Resource
    private PromotionApiAdaptor promotionAdaptor;

    @Resource
    private PlayActivityConvert convert;

    @Resource
    private PromotionFacade promotionFacade;

    @Resource
    PointLotteryFlowReadFacade pointLotteryFlowReadFacade;

    @Override
    public PlayActivityVO queryShareActivity(Long activityId) {
        PlayActivityVO activityVO = queryLotteryActivity(activityId);
        if (CollectionUtils.isEmpty(activityVO.getPlayRule())) {
            return activityVO;
        }
        for (PlayPrizeVo prize : activityVO.getPrizes()) {
            fillCoupon(prize);
            // 如果奖励发放完成后， 不能再分享了
            if (Objects.nonNull(prize.getTotalCnt()) &&
                Objects.nonNull(prize.getSendCnt()) &&
                prize.getSendCnt() >= prize.getTotalCnt()) {
                activityVO.setShareNoPrizeLeft(Boolean.TRUE);
            }
        }
        return activityVO;
    }

    @Override
    public PlayActivityVO queryLotteryActivity(Long activityId) {
        PlayActivityDTO dto = promotionAdaptor.queryById(activityId);
        ParamUtil.nonNull(dto, I18NMessageUtils.getMessage("event.not.exist"));  //# "活动不存在"
        PlayActivityVO activityVO = convert.dto2Vo(dto);
        this.convertStatus(activityVO, dto);
        activityVO.setPrizes(fillPrizes(dto.getPrizes()));
        //积分抽奖
        Integer lotteryType = dto.getPlayRule().getInteger("lotteryType");
        if(LotteryType.POINT.getCode().equals(lotteryType)){
            activityVO.setParticipateCnt(getJfLotteryNum(activityId));
        }
        return activityVO;
    }

    public Long getJfLotteryNum(Long activityId){
        PointLotteryFlowReq pointLotteryFlowReq = new PointLotteryFlowReq();
        pointLotteryFlowReq.setCustId(UserHolder.getUser().getCustId());
        pointLotteryFlowReq.setLotteryTime(getNowDateStr());
        pointLotteryFlowReq.setActivityId(activityId);
        RpcResponse<Long> longRpcResponse = pointLotteryFlowReadFacade.queryCount(pointLotteryFlowReq);
        if(longRpcResponse.isSuccess()){
            return longRpcResponse.getData();
        }
        else{
            throw new GmallInvalidArgumentException(I18NMessageUtils.getMessage("points.event.draw.times.error"));  //# "查询积分活动抽奖次数报错"
        }
    }

    public String getNowDateStr(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(new Date());
    }


    private void convertStatus(PlayActivityVO activityVO, PlayActivityDTO dto) {
        Date now = new Date();
        if (dto.getStatus() < PlayActivityStatus.published.getCode()) {
            return;
        }
        //未开始
        if (DateUtils.small(now, dto.getStartTime())) {
            activityVO.setStatus(PlayActivityStatus.wait_start.getCode());
        }
        //开始中
        if (DateUtils.isBetween(now, dto.getStartTime(), dto.getEndTime())) {
            activityVO.setStatus(PlayActivityStatus.start.getCode());
        }
        //活动结束
        if (DateUtils.isBigThen(now, dto.getEndTime())) {
            activityVO.setStatus(PlayActivityStatus.status_end.getCode());
        }
    }


    /**
     * 优惠券信息
     * @param prize
     */
    private void fillCoupon(PlayPrizeVo prize) {
        if(!PrizeType.COUPON.eq(prize.getType())){
            return;
        }
        ByCodeCouponQuery couponQuery = new ByCodeCouponQuery();
        String couponCode = prize.getPrizeInfo().getString("couponCode");
        couponQuery.setCode(couponCode);
        CouponInstanceVO vo = promotionFacade.queryCoupon(couponQuery);
        prize.setCouponInfo(vo);
        return;
    }

    /**
     *
     */
    protected List<PlayPrizeVo> fillPrizes(List<PlayPrizeRelateDTO> prizes) {
        if (CollectionUtils.isEmpty(prizes)) {
            return new ArrayList<>();
        }
        List<PlayPrizeVo> result = new ArrayList<>();
        for (PlayPrizeRelateDTO prize : prizes) {
            PlayPrizeVo vo = convert.dto2Vo(prize);
            result.add(vo);
        }
        return result;
    }
}
