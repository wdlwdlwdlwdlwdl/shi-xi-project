package com.aliyun.gts.gmall.manager.front.promotion.adaptor;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.center.promotion.api.dto.input.LotteryRequest;
import com.aliyun.gts.gmall.center.promotion.api.dto.model.PlayActivityDTO;
import com.aliyun.gts.gmall.center.promotion.api.dto.model.PlayAwardDTO;
import com.aliyun.gts.gmall.center.promotion.api.dto.model.ShareFissionDTO;
import com.aliyun.gts.gmall.center.promotion.api.dto.model.ShareVoteDTO;
import com.aliyun.gts.gmall.center.promotion.api.facade.PlayActivityReadFacade;
import com.aliyun.gts.gmall.center.promotion.api.facade.PlayAwardFacade;
import com.aliyun.gts.gmall.center.promotion.api.facade.ShareFissionReadFacade;
import com.aliyun.gts.gmall.center.promotion.api.facade.ShareFissionWriteFacade;
import com.aliyun.gts.gmall.center.promotion.common.query.PlayActivityQuery;
import com.aliyun.gts.gmall.center.promotion.common.query.PlayAwardQuery;
import com.aliyun.gts.gmall.center.promotion.common.query.ShareFissionQuery;
import com.aliyun.gts.gmall.center.promotion.common.type.AwardStatus;
import com.aliyun.gts.gmall.center.promotion.common.type.PrizeType;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.common.util.UserHolder;
import com.aliyun.gts.gmall.manager.front.promotion.common.converter.AwardConverter;
import com.aliyun.gts.gmall.manager.front.promotion.dto.input.command.AwardQuery;
import com.aliyun.gts.gmall.platform.promotion.api.dto.model.PromotionConfigDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.GrantIntegralConfigDTO;
import com.aliyun.gts.gmall.platform.promotion.api.facade.admin.PromotionConfigFacade;
import com.aliyun.gts.gmall.platform.promotion.common.constant.ConfigGroups;
import com.aliyun.gts.gmall.platform.promotion.common.constant.PromotionConfigKey;
import com.aliyun.gts.gmall.platform.promotion.common.query.PromConfigQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PromotionApiAdaptor {

    @Resource
    private PlayActivityReadFacade playActivityReadFacade;

    @Resource
    private ShareFissionWriteFacade shareFissionWriteFacade;
    @Resource
    private ShareFissionReadFacade shareFissionReadFacade;

    @Resource
    private PlayAwardFacade playAwardFacade;

    @Autowired
    private AwardConverter awardConverter;

    @Autowired
    private PromotionConfigFacade promotionConfigFacade;


    public PlayActivityDTO queryById(Long activityId) {
        PlayActivityQuery query = new PlayActivityQuery();
        query.setId(activityId);
        query.setIncludePrize(true);
        query.setCustPrimary(UserHolder.getUser().getCustPrimary());
        query.setCustId(UserHolder.getUser().getCustId());
        RpcResponse<PlayActivityDTO> response = playActivityReadFacade.queryById(query);
        return response.getData();
    }

    public ShareFissionDTO queryShareByCode(String shareCode, Long activityId,Boolean includeVote) {
        if(shareCode == null){
            return null;
        }
        ShareFissionQuery query = new ShareFissionQuery();
        query.setShareCode(shareCode);
        query.setActivityId(activityId);
        query.setIncludeVote(includeVote);
        RpcResponse<ShareFissionDTO> response = shareFissionReadFacade.queryByCode(query);
        return response.getData();
    }

    public ShareFissionDTO queryShareByCust(Long custId, Long activityId) {
        ShareFissionQuery query = new ShareFissionQuery();
        query.setActivityId(activityId);
        query.setIncludeVote(true);
        query.setCustId(custId);
        query.setCustPrimary(UserHolder.getUser().getCustPrimary());
        RpcResponse<ShareFissionDTO> response = shareFissionReadFacade.queryByCust(query);
        return response.getData();
    }

    public RpcResponse<ShareFissionDTO> launchShare(ShareFissionDTO shareFissionDTO) {
        return shareFissionWriteFacade.launchShare(shareFissionDTO);
    }

    public RpcResponse<Boolean> voteForShare(ShareVoteDTO voteDTO) {
        return shareFissionWriteFacade.voteForShare(voteDTO);
    }

    /**
     * 抽奖接口
     * @param request
     * @return
     */
    public RpcResponse<PlayAwardDTO> lottery(long activityId, long custId, String outId) {
        LotteryRequest request = new LotteryRequest();
        request.setActivityId(activityId);
        request.setCustId(custId);
        request.setCustPrimary(UserHolder.getUser().getCustPrimary());
        request.setOutId(outId);
        return playAwardFacade.lottery(request);
    }

    /**
     * 查询奖品数量
     * @param query
     * @return
     */
    public RpcResponse<Integer> queryAwardNumber(AwardQuery query) {
        PlayAwardQuery playAwardQuery = awardConverter.toPlayAwardQuery(query);
        playAwardQuery.setAwardStatus(AwardStatus.win.getCode());
        List<Integer> prizeTypeList = new ArrayList<>();
        prizeTypeList.add(PrizeType.COUPON.getCode());
        prizeTypeList.add(PrizeType.ITEM.getCode());
        prizeTypeList.add(PrizeType.POINT.getCode());
        playAwardQuery.setPrizeTypeList(prizeTypeList);
        return playAwardFacade.queryAwardNumber(playAwardQuery);
    }

    /**
     * 查询奖品记录
     * @param query
     * @return
     */
    public RpcResponse<PageInfo<PlayAwardDTO>> queryAward(AwardQuery query) {
        PlayAwardQuery playAwardQuery = awardConverter.toPlayAwardQuery(query);
        List<Integer> prizeTypeList = new ArrayList<>();
        prizeTypeList.add(PrizeType.COUPON.getCode());
        prizeTypeList.add(PrizeType.ITEM.getCode());
        prizeTypeList.add(PrizeType.POINT.getCode());
        playAwardQuery.setPrizeTypeList(prizeTypeList);
        playAwardQuery.setMinStatus(AwardStatus.preWin.getCode());
        return playAwardFacade.queryAward(playAwardQuery);
    }

    /**
     * 领取奖品
     * @param
     * @return
     */
    public RpcResponse<Boolean> takeAward(Long custId, Long awardId) {
        PlayAwardDTO q = new PlayAwardDTO();
        q.setCustId(custId);
        q.setId(awardId);
        return playAwardFacade.sendAward(q);
    }

    /**
     * 分享奖品获取 获取后才能领奖
     * @param
     * @return
     */
    public RpcResponse<ShareFissionDTO> applySharePrize(Long custId, Long activityId, String shareCode) {
        ShareFissionDTO fissionDTO = new ShareFissionDTO();
        fissionDTO.setShareCode(shareCode);
        fissionDTO.setActivityId(activityId);
        fissionDTO.setCustId(custId);
        return shareFissionWriteFacade.applySharePrize(fissionDTO);
    }

    public GrantIntegralConfigDTO queryConfig() {
        PromConfigQuery promConfigQuery = new PromConfigQuery();
        promConfigQuery.setConfigGroup(ConfigGroups.ACCOUNT_GROUP);
        promConfigQuery.setKey(PromotionConfigKey.ACCOUNT_GLOBAL_CONFIG);
        promConfigQuery.setPage(new PageParam());
        RpcResponse<PromotionConfigDTO> promotionConfigDTORpcResponse = promotionConfigFacade.queryByKey(promConfigQuery);
        if (null != promotionConfigDTORpcResponse.getData()) {
            PromotionConfigDTO data = promotionConfigDTORpcResponse.getData();
            GrantIntegralConfigDTO grantIntegralConfig = JSONObject.toJavaObject(data.getBody(), GrantIntegralConfigDTO.class);
            return grantIntegralConfig;
        }
        return null;
    }

}
