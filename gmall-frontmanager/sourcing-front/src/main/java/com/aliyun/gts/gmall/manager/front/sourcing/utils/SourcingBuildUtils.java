package com.aliyun.gts.gmall.manager.front.sourcing.utils;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gcai.platform.sourcing.common.type.SourcingStatus;
import com.aliyun.gts.gcai.platform.sourcing.common.type.SourcingType;
import com.aliyun.gts.gmall.framework.server.util.DateTimeUtils;
import com.aliyun.gts.gmall.manager.front.b2bcomm.utils.DateUtils;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.SourcingVo;
import com.aliyun.gts.gmall.manager.front.sourcing.vo.StepVo;

import java.util.Date;

import static com.aliyun.gts.gmall.manager.front.sourcing.utils.SourcingBuildUtils.SouringMsgEnum.*;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/6/1 15:01
 */
public class SourcingBuildUtils {

    //临时支持多语言，未来支持i18n时应根据MsgKey+Lang取不同的值
    enum SouringMsgEnum {
        RELEASE("Release("+I18NMessageUtils.getMessage("publish.time")+")"),  //# 发布时间
        END("End("+I18NMessageUtils.getMessage("bid.deadline")+")"),  //# 投标截止
        QUOTING_START("Quoting Start("+I18NMessageUtils.getMessage("quote.start")+")"),  //# 报价开始
        QUOTING_END("Quoting End("+I18NMessageUtils.getMessage("quote.end")+")"),  //# 报价截止
        APPLYING("Applying("+I18NMessageUtils.getMessage("enrollment.in.progress")+")"),  //# 报名中
        APPLYING_END("Applying End("+I18NMessageUtils.getMessage("enrollment.end")+")"),  //# 报名截止
        BID_START("Bid Start("+I18NMessageUtils.getMessage("bidding.start")+")"),  //# 竞价开始
        BID_END("Bid End("+I18NMessageUtils.getMessage("bidding.end")+")"),  //# 竞价结束
        BID_OPENING_START("Bid Opening Start("+I18NMessageUtils.getMessage("bid.open.start")+")"),  //# 开标开始
        BID_OPENING_END("Bid Opening End("+I18NMessageUtils.getMessage("bid.open.end")+")"),  //# 开标结束
        BID_EVALUATING_END("Bid Evaluating End("+I18NMessageUtils.getMessage("bid.evaluation.end")+")"),  //# 评标结束
        RESULT_ANNOUNCED("Results Announced("+I18NMessageUtils.getMessage("result.publish")+")"),  //# 公布结果
        RELEASED("Released("+I18NMessageUtils.getMessage("published")+")"),  //# 已发布
        BID_CLOSED("Bid Closed("+I18NMessageUtils.getMessage("bid.deadline")+")"),  //# 投标截止

        ;

        private String text;


        SouringMsgEnum(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    public static StepVo tenderingStep(SourcingVo vo) {
        StepVo stepVo = new StepVo();
        if (vo == null) {
            return stepVo;
        }
        Date now = new Date();
        stepVo.addStep(RELEASE.getText(), DateTimeUtils.format(vo.getGmtCreate()));
        stepVo.addStep(END.getText(), DateTimeUtils.format(vo.getEndTime()));
        stepVo.addStep(BID_OPENING_START.getText(), DateTimeUtils.format(vo.getFeature().getOpenBidStart()));
        stepVo.addStep(BID_OPENING_END.getText(), DateTimeUtils.format(vo.getFeature().getOpenBidEnd()));
        stepVo.addStep(BID_EVALUATING_END.getText(), DateTimeUtils.format(vo.getFeature().getChooseBidEnd()));
        stepVo.addStep(RESULT_ANNOUNCED.getText(), null);
        if (vo.getViewStatus() < SourcingStatus.pass_approve.getValue()) {
            stepVo.setCurrent(0);
        }
        if (DateUtils.small(now, vo.getStartTime())) {
            stepVo.setCurrent(0);
            stepVo.setCurrentName(RELEASED.getText());
        }
        if (DateUtils.small(vo.getEndTime(), now)) {
            stepVo.setCurrent(1);
            stepVo.setCurrentName(END.getText());
        }
        if (DateUtils.isBig(now, vo.getFeature().getOpenBidStart())) {
            stepVo.setCurrent(2);
            stepVo.setCurrentName(BID_OPENING_START.getText());
        }
        if (DateUtils.isBig(now, vo.getFeature().getOpenBidEnd())) {
            stepVo.setCurrent(3);
            stepVo.setCurrentName(BID_OPENING_END.getText());
        }
        if (DateUtils.isBig(now, vo.getFeature().getChooseBidEnd())) {
            stepVo.setCurrent(4);
            stepVo.setCurrentName(BID_EVALUATING_END.getText());
        }
        return stepVo;
    }

    public static StepVo inquiryStep(SourcingVo vo) {
        StepVo stepVo = new StepVo();
        Date now = new Date();
        stepVo.addStep(RELEASE.getText(), DateUtils.format(vo.getGmtCreate(), DateUtils.format2));
        stepVo.addStep(QUOTING_START.getText(), DateUtils.format(vo.getStartTime(), DateUtils.format2));
        stepVo.addStep(QUOTING_END.getText(), DateUtils.format(vo.getEndTime(), DateUtils.format2));
        stepVo.addStep(RESULT_ANNOUNCED.getText(), null);
        if (vo.getViewStatus() < SourcingStatus.pass_approve.getValue()) {
            stepVo.setCurrent(0);
        } else if (SourcingStatus.wait_apply.eq(vo.getViewStatus())) {
            stepVo.setCurrent(0);
            stepVo.setCurrentName(RELEASED.getText());
        } else if (SourcingStatus.in_quote.eq(vo.getViewStatus())) {
            stepVo.setCurrent(2);
            stepVo.setCurrentName(QUOTING_START.getText());
        } else if (vo.getViewStatus() >= SourcingStatus.wait_bid_award.getValue()) {
            stepVo.setCurrent(3);
            stepVo.setCurrentName(QUOTING_END.getText());
        }
        return stepVo;
    }

    public static StepVo bidingStep(SourcingVo vo) {
        StepVo stepVo = new StepVo();
        Date now = new Date();
        stepVo.addStep(RELEASE.getText(), DateUtils.format(vo.getGmtCreate(), DateUtils.format2));
        stepVo.addStep(APPLYING.getText(), DateUtils.format(vo.getApplyStartTime(), DateUtils.format2));
        stepVo.addStep(APPLYING_END.getText(), DateUtils.format(vo.getApplyEndTime(), DateUtils.format2));
        stepVo.addStep(BID_START.getText(), DateUtils.format(vo.getStartTime(), DateUtils.format2));
        stepVo.addStep(BID_END.getText(), DateUtils.format(vo.getEndTime(), DateUtils.format2));
        stepVo.addStep(RESULT_ANNOUNCED.getText(), null);
        //未审核
        if (vo.getStatus() < SourcingStatus.pass_approve.getValue()) {
            return stepVo;
        } else if (SourcingStatus.wait_apply.eq(vo.getViewStatus())) {
            stepVo.setCurrent(0);
            stepVo.setCurrentName(RELEASED.getText());
        } else if (SourcingStatus.in_apply.eq(vo.getViewStatus())) {
            stepVo.setCurrent(1);
            stepVo.setCurrentName(QUOTING_START.getText());
        } else if (SourcingStatus.wait_quote.eq(vo.getViewStatus())) {
            stepVo.setCurrent(2);
            stepVo.setCurrentName(QUOTING_END.getText());
        } else if (SourcingStatus.in_quote.eq(vo.getViewStatus())) {
            stepVo.setCurrent(3);
            stepVo.setCurrentName(BID_START.getText());
        } else if (vo.getViewStatus() >= SourcingStatus.wait_bid_award.getValue()) {
            stepVo.setCurrent(4);
            stepVo.setCurrentName(BID_END.getText());
        }
        return stepVo;
    }

    /**
     * 状态
     *
     * @param vo
     * @return
     */
    public static StepVo buildAdminStep(SourcingVo vo) {
        if (SourcingType.InquiryPrice.eq(vo.getSourcingType())) {
            buildViewStatus(vo);
            return inquiryStep(vo);
        }
        if (SourcingType.BidPrice.eq(vo.getSourcingType())) {
            buildViewStatus(vo);
            return bidingStep(vo);
        }
        if (SourcingType.Zhao.eq(vo.getSourcingType())) {
            vo.setViewStatus(vo.getStatus());
            return tenderingStep(vo);
        }
        return inquiryStep(vo);
    }

    /**
     * @param vo
     */
    public static SourcingVo buildViewStatus(SourcingVo vo) {
        vo.setViewStatus(vo.getStatus());
        //审核没有通过
        Date now = new Date();
        if (vo.getStatus() < SourcingStatus.pass_approve.getValue()) {
            return vo;
        }
        if (vo.getStatus() >= SourcingStatus.wait_bid_award.getValue()) {
            return vo;
        }
        //等待报名
        if (DateUtils.small(now, vo.getApplyStartTime())) {
            vo.setViewStatus(SourcingStatus.wait_apply.getValue());
        }
        //报名中
        else if (DateUtils.isBetween(vo.getApplyStartTime(), vo.getApplyEndTime(), now)) {
            vo.setViewStatus(SourcingStatus.in_apply.getValue());
        }
        //等待报价
        else if (DateUtils.small(now, vo.getStartTime())) {
            vo.setViewStatus(SourcingStatus.wait_quote.getValue());
        }
        //报价中
        else if (DateUtils.isBetween(vo.getStartTime(), vo.getEndTime(), now)) {
            vo.setViewStatus(SourcingStatus.in_quote.getValue());
        }
        //报价结束;进入决标状态
        else if (DateUtils.isBig(now, vo.getEndTime())) {
            vo.setViewStatus(SourcingStatus.wait_bid_award.getValue());
        }
        return vo;
    }
}
