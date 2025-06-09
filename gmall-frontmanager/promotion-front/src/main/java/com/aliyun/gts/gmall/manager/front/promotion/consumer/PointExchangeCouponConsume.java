package com.aliyun.gts.gmall.manager.front.promotion.consumer;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.promotion.dto.input.command.PointExchangeCouponQuery;
import com.aliyun.gts.gmall.middleware.api.mq.anns.MQConsumer;
import com.aliyun.gts.gmall.middleware.api.mq.consumer.ConsumeEventProcessor;
import com.aliyun.gts.gmall.middleware.api.mq.model.StandardEvent;
import com.aliyun.gts.gmall.platform.promotion.api.dto.account.AcBookDeductDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.input.CouponQueryReq;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.CouponInstanceDTO;
import com.aliyun.gts.gmall.platform.promotion.api.facade.account.AccountBookWriteFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.cust.PromotionCouponFacade;
import com.aliyun.gts.gmall.platform.promotion.common.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
@Slf4j
@MQConsumer(groupId = "${front-manager.message.pointexchangecoupon.groupId}", topic = "${front-manager.message.pointexchangecoupon.topic}", tag = "pointExchangeCoupon")
public class PointExchangeCouponConsume implements ConsumeEventProcessor {
    @Autowired
    AccountBookWriteFacade accountBookWriteFacade;
    @Autowired
    PromotionCouponFacade promotionCouponFacade;

    @Override
    public boolean process(StandardEvent event) {
        PointExchangeCouponQuery message = (PointExchangeCouponQuery) event.getPayload().getData();
        try {
            //在通过bizId和couponCode来查看领取券看是否成功,如果成功的话说明之前领取成功了，直接往下走，如果是服务调用失败的话就重试，如果是服务调用成功但是没有返回优惠券信息就说明之前没有领取成功，如果是之前失败了，但是这边成功了
            RpcResponse<PageInfo<CouponInstanceDTO>> pageInfoRpcResponse = pagePromotionCoupon(message);
            if (pageInfoRpcResponse.isSuccess()&& CollectionUtils.isNotEmpty(pageInfoRpcResponse.getData().getList())) {
                log.info("根据幂等id查询已经领取优惠券成功,PointExchangeCouponQuery:{},调用查询优惠券返回信息:{}", JSONObject.toJSONString(message), JSONObject.toJSONString(pageInfoRpcResponse));
                //抵扣积分
                return freezeDeduct(message);
            } else if (!pageInfoRpcResponse.isSuccess()) {
                log.error("根据幂等id查询已经领取优惠券失败PointExchangeCouponQuery：{},调用查询优惠券返回信息:{}", JSONObject.toJSONString(message), JSONObject.toJSONString(pageInfoRpcResponse));
                return false;
            }
            //没有领券需要解冻积分
            else if (CollectionUtils.isEmpty(pageInfoRpcResponse.getData().getList())) {
                return unFreeze(message);
            }
            return true;
        } catch (Exception e) {
            log.error("PointLotteryConsumer消费积分消息失败," + JsonUtils.toJSONString(message), e);
            return false;
        }
    }

    public RpcResponse<PageInfo<CouponInstanceDTO>> pagePromotionCoupon(PointExchangeCouponQuery message) {
        RpcResponse<PageInfo<CouponInstanceDTO>> page=null;
        try {
            CouponQueryReq couponQueryReq = new CouponQueryReq();
            couponQueryReq.setApplyUniqueId(message.getBizId());
            couponQueryReq.setCustId(message.getLoginCustId());
            PageParam pageParam = new PageParam();
            pageParam.setPageNo(1);
            pageParam.setPageSize(10);
            couponQueryReq.setPage(pageParam);
            return promotionCouponFacade.page(couponQueryReq);
        } catch (Throwable e) {
            log.error("根据幂等id查询已经领取优惠券失败PointExchangeCouponQuery：{}", JSONObject.toJSONString(message),e);
            return RpcResponse.fail("","根据幂等id查询已经领取优惠券失败");
        }


    }


    /**
     * 抵扣
     *
     * @param message
     * @return
     */
    public Boolean freezeDeduct(PointExchangeCouponQuery message) {
        RpcResponse<Boolean> booleanRpcResponse = null;
        try {
            AcBookDeductDTO acBookDeductDTO = new AcBookDeductDTO();
            acBookDeductDTO.setChangeAssets(message.getAssets());
            acBookDeductDTO.setCustId(message.getLoginCustId());
            acBookDeductDTO.setChangeType(message.getChangeType());
            acBookDeductDTO.setBizId(message.getBizId());
            booleanRpcResponse = accountBookWriteFacade.freezeDeduct(acBookDeductDTO);
            if (booleanRpcResponse.isSuccess()) {
                log.info("抵扣积分成功,PointExchangeCouponQuery:{},调用抵扣接口返回信息:{}", JSONObject.toJSONString(message), JSONObject.toJSONString(booleanRpcResponse));
                return true;
            } else if (!booleanRpcResponse.isSuccess()) {
                log.error("抵扣积分失败PointExchangeCouponQuery：{},调用抵扣接口返回信息:{}", JSONObject.toJSONString(message), JSONObject.toJSONString(booleanRpcResponse));
                //发送积分抵扣消息
                return false;
            }
            return true;
        } catch (Throwable e) {
            log.error("抵扣积分失败PointExchangeCouponQuery", JSONObject.toJSONString(message),e);
            //发送积分抵扣消息
            return false;
        }

    }


    /**
     * 解冻积分
     *
     * @param message
     * @return
     */
    public Boolean unFreeze(PointExchangeCouponQuery message) {
        RpcResponse<Boolean> booleanRpcResponse = null;
        try {
            AcBookDeductDTO acBookDeductDTO = new AcBookDeductDTO();
            acBookDeductDTO.setBizId(message.getBizId());
            acBookDeductDTO.setCustId(message.getLoginCustId());
            acBookDeductDTO.setChangeAssets(message.getAssets());
            acBookDeductDTO.setChangeType(message.getChangeType());
            booleanRpcResponse = accountBookWriteFacade.unFreeze(acBookDeductDTO);
            if (booleanRpcResponse.isSuccess()) {
                log.info("解冻积分成功,PointExchangeCouponQuery:{},调用解冻接口返回信息:{}", JSONObject.toJSONString(message), JSONObject.toJSONString(booleanRpcResponse));
                return true;
            } else if (!booleanRpcResponse.isSuccess()) {
                log.error("解冻积分失败PointExchangeCouponQuery：{},调用解冻接口返回信息:{}", JSONObject.toJSONString(message), JSONObject.toJSONString(booleanRpcResponse));
                return false;
            }
            return true;
        } catch (Throwable e) {
            log.error("解冻积分失败PointExchangeCouponQuery", JSONObject.toJSONString(message),e);
            return false;
        }
    }

}
