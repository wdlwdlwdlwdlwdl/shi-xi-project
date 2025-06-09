package com.aliyun.gts.gmall.manager.front.promotion.facade.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.center.promotion.api.dto.model.ShareFissionDTO;
import com.aliyun.gts.gmall.center.promotion.api.dto.model.ShareVoteDTO;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.CustomerAdapter;
import com.aliyun.gts.gmall.manager.front.promotion.adaptor.PromotionApiAdaptor;
import com.aliyun.gts.gmall.manager.front.promotion.common.converter.ShareFissionConverter;
import com.aliyun.gts.gmall.manager.front.promotion.dto.output.PlayActivityVO;
import com.aliyun.gts.gmall.manager.front.promotion.dto.output.ShareFissionVO;
import com.aliyun.gts.gmall.manager.front.promotion.dto.output.ShareVoteVO;
import com.aliyun.gts.gmall.manager.front.promotion.facade.PlayActivityFacade;
import com.aliyun.gts.gmall.manager.front.promotion.facade.ShareVoteFacade;
import com.aliyun.gts.gmall.manager.utils.ResponseUtils;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ShareVoteFacadeImpl implements ShareVoteFacade {
    @Resource
    private PromotionApiAdaptor promotionApiAdaptor;
    @Resource
    private ShareFissionConverter shareFissionConverter;
    @Resource
    private PlayActivityFacade playActivityFacade;
    @Resource
    private CustomerAdapter customerAdapter;

    @Override
    public RestResponse<ShareFissionVO> launchShare(Long custId, Long activityId) {
        ShareFissionDTO shareDTO = new ShareFissionDTO();
        shareDTO.setCustId(custId);
        shareDTO.setActivityId(activityId);
        CustomerDTO customer = this.getCustomer(custId);
        shareDTO.setFeature(fillUserNick(customer));
        shareDTO.setCustPrimary(UserHolder.getUser().getCustPrimary());
        RpcResponse<ShareFissionDTO> response = promotionApiAdaptor.launchShare(shareDTO);
        return ResponseUtils.convertVOResponse(response, shareFissionConverter::dto2VO, true);
    }

    private CustomerDTO getCustomer(Long custId) {
        return customerAdapter.queryById(custId);
    }

    private JSONObject fillUserNick(CustomerDTO customer) {
        if (customer == null) {
            return null;
        }
        JSONObject feature = new JSONObject();
        feature.put("nickname", customer.getNickname());
        feature.put("headUrl", customer.getHeadUrl());
        return feature;
    }

    @Override
    public RestResponse<Boolean> voteForShare(Long custId, Long activityId, String shareCode) {
        ShareVoteDTO shareDTO = new ShareVoteDTO();
        shareDTO.setCustId(custId);
        shareDTO.setActivityId(activityId);
        shareDTO.setShareCode(shareCode);
        CustomerDTO customer = this.getCustomer(custId);
        shareDTO.setCustPrimary(customer == null ? null : customer.getCustPrimary());
        shareDTO.setFeature(fillUserNick(customer));
        RpcResponse<Boolean> response = promotionApiAdaptor.voteForShare(shareDTO);
        return ResponseUtils.operatorResult(response, true);
    }

    @Override
    public PlayActivityVO queryShareActivity(String shareCode, Long custId, Long activityId) {
        //查询活动
        PlayActivityVO activityVO = playActivityFacade.queryShareActivity(activityId);
        //查询自己的分享
        ShareFissionDTO shareFissionDTO = promotionApiAdaptor.queryShareByCust(custId, activityId);
        ShareFissionVO selfShare = shareFissionConverter.dto2VO(shareFissionDTO);
        if (selfShare != null) {
            //最多显示取助力人数 为  需要助力的人数
            Integer shareCnt = selfShare.getShareCnt();
            Integer voteCnt = selfShare.getVoteCnt();
            if (voteCnt > shareCnt && CollectionUtils.isNotEmpty(selfShare.getVoteList())) {
                List<ShareVoteVO> voteList = selfShare.getVoteList();
                List<ShareVoteVO> collect = voteList.stream().map(shareVoteVO -> {
                    shareVoteVO.setCustPrimary(null);
                    shareVoteVO.setCustId(null);
                    return shareVoteVO;
                }).collect(Collectors.toList());
                selfShare.setVoteList(collect.subList(0, shareCnt));
            }
            activityVO.setShare(selfShare);
        }
        //查询别人的分享
        ShareFissionDTO other = promotionApiAdaptor.queryShareByCode(shareCode, activityId, true);
        ShareFissionVO otherVo = shareFissionConverter.dto2VO(other);
        //code不是自己的
        boolean isMe = false;
        if (other != null) {
            if (StringUtils.isNotBlank(other.getCustPrimary())) {
                isMe = UserHolder.getUser().getCustPrimary().equals(other.getCustPrimary());
            } else {
                isMe = custId.equals(other.getCustId());
            }
        }
        if (!isMe && otherVo != null ) {
            if (otherVo.getVoteList().stream().filter(x -> UserHolder.getUser().getCustPrimary().equals(x.getCustPrimary())).findFirst().orElse(null) != null) {
                //他人分享 我已经助力过了
                otherVo.setOtherShareVoteFlag(true);
            } else {
                otherVo.setOtherShareVoteFlag(false);
            }
            //他人手机号与用户id过滤
            if (CollectionUtils.isNotEmpty(otherVo.getVoteList())) {
                List<ShareVoteVO> collect = otherVo.getVoteList().stream().map(shareVoteVO -> {
                    shareVoteVO.setCustPrimary(null);
                    shareVoteVO.setCustId(null);
                    return shareVoteVO;
                }).collect(Collectors.toList());
                otherVo.setVoteList(collect);
            }
            activityVO.setOtherShare(otherVo);
        }
        return activityVO;
    }

    @Override
    public RestResponse<ShareFissionVO> applySharePrize(Long custId, Long activityId, String shareCode) {
        try {
            RpcResponse<ShareFissionDTO> response = promotionApiAdaptor.applySharePrize(custId, activityId, shareCode);
            return ResponseUtils.convertVOResponse(response, shareFissionConverter::dto2VO, false);
        } catch (Exception e) {
            return RestResponse.fail("1001", e.getMessage());
        }
    }
}
