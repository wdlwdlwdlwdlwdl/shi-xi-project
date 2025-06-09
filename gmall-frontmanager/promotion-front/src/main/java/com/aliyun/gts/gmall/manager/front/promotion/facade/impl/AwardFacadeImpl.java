package com.aliyun.gts.gmall.manager.front.promotion.facade.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.aliyun.gts.gmall.center.promotion.api.dto.model.PlayAwardDTO;
import com.aliyun.gts.gmall.center.promotion.common.type.PrizeType;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.FailInfo;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.promotion.adaptor.PromotionApiAdaptor;
import com.aliyun.gts.gmall.manager.front.promotion.common.converter.LotteryConvert;
import com.aliyun.gts.gmall.manager.front.promotion.dto.input.command.AwardQuery;
import com.aliyun.gts.gmall.manager.front.promotion.dto.input.command.LotteryCommand;
import com.aliyun.gts.gmall.manager.front.promotion.dto.output.PlayAwardVO;
import com.aliyun.gts.gmall.manager.front.promotion.facade.AwardFacade;
import com.aliyun.gts.gmall.manager.utils.ResponseUtils;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.platform.promotion.common.constant.PromResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author haibin.xhb
 * @description: TODO
 * @date 2021/9/15 10:06
 */
@Slf4j
@Service
public class AwardFacadeImpl implements AwardFacade {

    @Resource
    private LotteryConvert convert;

    @Resource
    @Qualifier("cacheManager")
    private CacheManager cacheManager;

    @Resource
    private PromotionApiAdaptor apiAdaptor;

    public static final String bizIdPrefix="lottery_deduct";

    /**
     * 抽奖接口
     * @param request
     * @return
     */
    @Override
    public RestResponse<PlayAwardVO> lottery(LotteryCommand request) {
        request.setOutId(buildBizId());
        try {
            RpcResponse<PlayAwardDTO> response = apiAdaptor.lottery(request.getActivityId(), request.getCustId(),request.getOutId());
            return showMessage(response);
        } catch (Exception e) {
            log.error("lottery", e);
            PlayAwardVO playAwardVO = new PlayAwardVO();
            playAwardVO.setMessage(I18NMessageUtils.getMessage("no.win")+","+I18NMessageUtils.getMessage("unlucky"));  //# "没有中奖,很遗憾哦"
            return RestResponse.okWithoutMsg(playAwardVO);
        }
    }

    /**
     * 查询奖品数量
     * @param query
     * @return
     */
    @Override
    public  RestResponse<Integer> queryAwardNumber(AwardQuery query) {
        Long recordId = cacheManager.get(getPromotionKey(query.getCustId()));
        if (recordId != null) {
            query.setId(recordId);
        }
        RpcResponse<Integer> rpcResponse = apiAdaptor.queryAwardNumber(query);
        if (rpcResponse != null && rpcResponse.isSuccess() && rpcResponse.getData() != null) {
            cacheManager.set(getPlayNumberKey(query.getCustId()), rpcResponse.getData(), 30, TimeUnit.SECONDS);
        }
        return ResponseUtils.convertResponse(rpcResponse, Boolean.TRUE);
    }


    public String buildBizId() {
        StringBuilder builders = new StringBuilder();
        builders.append(bizIdPrefix).append('-').append(UUID.randomUUID());
        return builders.toString();
    }

    /**
     * 查询奖品记录
     * @param query
     * @return
     */
    @Override
    public RestResponse<PageInfo<PlayAwardVO>> queryAward(AwardQuery query){
        RpcResponse<PageInfo<PlayAwardDTO>> response = apiAdaptor.queryAward(query);
        //获取最大的ID
        if (response != null &&response.getData() != null && CollectionUtils.isNotEmpty(response.getData().getList())) {
           Long newRecordId = response.getData().getList().stream().mapToLong(PlayAwardDTO::getId).max().getAsLong();
           Long recordId = cacheManager.get(getPromotionKey(query.getCustId()));
            cacheManager.set(getPromotionKey(query.getCustId()), recordId == null ? newRecordId : newRecordId > recordId ? newRecordId : recordId);
        }
        return ResponseUtils.convertVOPageResponse(response, convert::dto2Vo,false);
    }

    /**
     * 领取奖品
     * @param query
     * @return
     */
    @Override
    public RestResponse<Boolean> take(AwardQuery query) {
        RpcResponse<Boolean> response = apiAdaptor.takeAward(query.getCustId(),query.getId());
        return ResponseUtils.operatorResult(response,true);
    }

    /**
     * 结果文案
     * @param response
     * @return
     */
    RestResponse<PlayAwardVO> showMessage(RpcResponse<PlayAwardDTO> response) {
        //积分抽奖这边报错的时候限定这个错误码才报错，之前的抽奖不进行报错信息提示
        if (!response.isSuccess() && (response.getFail().getCode().equals("1002") || PromResponseCode.LotteryBizFailed.getCode().equals(response.getFail().getCode()))) {
            return RestResponse.fail(response.getFail().getCode(),response.getFail().getMessage());
        }
        PlayAwardDTO dto = response.getData();
        PlayAwardVO playAwardVO = new PlayAwardVO();
        if(dto == null){
            playAwardVO.setMessage(I18NMessageUtils.getMessage("not.winning")+": " + response.getFail().getMessage());  //# "未中奖
            return RestResponse.okWithoutMsg(playAwardVO);
        }
        //返回结果不为空
        playAwardVO.setPlayLimit(dto.getPlayLimit());
        playAwardVO.setPrizeType(dto.getPrizeType());
        playAwardVO.setPrizeDetailId(dto.getPrizeRelateId());
        if (PrizeType.THANKS.eq(dto.getPrizeType())) {
            playAwardVO.setMessage(I18NMessageUtils.getMessage("not.winning") + ": " + dto.getPrizeName());  //# "未中奖
            FailInfo fail = response.getFail();
            //业务错误未中奖
            if(fail != null && PromResponseCode.LotteryBizFailed.eq(fail.getCode())){
                playAwardVO.setMessage(I18NMessageUtils.getMessage("not.winning") + ": " + fail.getMessage());  //# "未中奖
            }
            return RestResponse.okWithoutMsg(playAwardVO);
        } else {
            playAwardVO.setMessage(I18NMessageUtils.getMessage("congratulations") + ": " + dto.getPrizeName());  //# "恭喜获得
            return RestResponse.okWithoutMsg(playAwardVO);
        }
    }

    /**
     * 缓存key
     * @param custId
     * @return
     */
    private String getPromotionKey(Long custId) {
        return "promotion_play_" + custId;
    }

    /**
     * 缓存key
     * @param custId
     * @return
     */
    private String getPlayNumberKey(Long custId) {
        return "promotion_play_cache_" + custId;
    }
}
