package com.aliyun.gts.gmall.manager.front.promotion.adaptor;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.aliyun.gts.gmall.center.item.api.dto.input.PointItemQueryReq;
import com.aliyun.gts.gmall.center.item.api.dto.output.PointItemQueryDTO;
import com.aliyun.gts.gmall.center.item.api.facade.PointItemQueryFacade;
import com.aliyun.gts.gmall.center.user.api.dto.input.NewCustomerQueryReq;
import com.aliyun.gts.gmall.center.user.api.facade.CustomerReadExtFacade;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.MessageSendManager;
import com.aliyun.gts.gmall.manager.front.common.exception.FrontManagerException;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.CustomerAdapter;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.PromotionAdapter;
import com.aliyun.gts.gmall.manager.front.customer.dto.utils.ApplyCouponSceneEnum;
import com.aliyun.gts.gmall.manager.front.promotion.common.enums.PointExchangeResponseCode;
import com.aliyun.gts.gmall.manager.front.promotion.dto.input.command.PointExchangeCouponQuery;
import com.aliyun.gts.gmall.platform.promotion.api.dto.account.AcBookDeductDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.input.CouponApplyReq;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.CouponInstanceDTO;
import com.aliyun.gts.gmall.platform.promotion.api.facade.account.AccountBookReadFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.account.AccountBookWriteFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.cust.PromotionCouponFacade;
import com.aliyun.gts.gmall.platform.promotion.common.type.account.ChangeTypeEnum;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerLevelConfigDTO;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.aliyun.gts.gmall.manager.front.customer.dto.utils.CustomerFrontResponseCode.COUPON_ALREADY_APPLY;
import static com.aliyun.gts.gmall.manager.front.customer.dto.utils.CustomerFrontResponseCode.LEVEL_COUPON_FAIL;

@Slf4j
@Service
public class PointExchangeCouponAdaptor {

    @Autowired
    protected AccountBookReadFacade accountBookReadFacade;
    @Autowired
    protected AccountBookWriteFacade accountBookWriteFacade;
    @Value("${front-manager.message.pointexchangecoupon.topic:}")
    private String pointExchangeCouponTopic;
    @Autowired
    private MessageSendManager messageSendManager;
    @Autowired
    PromotionCouponFacade promotionCouponFacade;
    @Autowired
    CustomerAdapter customerAdapter;
    @Autowired
    PromotionAdapter promotionAdapter;
    @Autowired
    private CustomerReadExtFacade customerReadExtFacade;
    @Autowired
    private PointItemQueryFacade pointItemQueryFacade;

    @NacosValue(value = "${front-manager.promotion.newuser.group.id:100}", autoRefreshed = true)
    private Long SPECIFIC_GROUP_ID;
    @NacosValue(value = "${front-manager.promotion.newuser.limit.day:30}", autoRefreshed = true)
    private Integer limit;

    public static final String bizIdPrefix="exchange_coupon_deduct";

    //方法改造，这后面的领券失败后直接发送解冻消息，然后在解冻消息中去调用接口，抵扣的话就直接抵扣如果失败的话就再发送失败消息
    public RestResponse freezePoint(PointExchangeCouponQuery pointExchangeCouponQuery) {
        pointExchangeCouponQuery.setChangeType(ChangeTypeEnum.exchange_coupon_deduct.getCode());
        pointExchangeCouponQuery.setBizId(buildBizId());
        pointExchangeCouponQuery.setLoginCustId(pointExchangeCouponQuery.getCustId());
        //先查询下积分优惠券的信息,积分的话不能从前端传值，因为会有那种修改了积分值然后调用接口的问题
        RpcResponse<PointItemQueryDTO> pointItemQueryDTORpcResponse = null;
        try {
            pointItemQueryDTORpcResponse = queryPointCouponDetail(pointExchangeCouponQuery);
            if (pointItemQueryDTORpcResponse == null || !pointItemQueryDTORpcResponse.isSuccess()||pointItemQueryDTORpcResponse.getData() == null) {
                log.error("查询优惠券信息失败 PointExchangeCouponQuery:{},接口返回信息:{}", JSONObject.toJSONString(pointExchangeCouponQuery), JSONObject.toJSONString(pointItemQueryDTORpcResponse));
                return RestResponse.fail(PointExchangeResponseCode.QUERY_COUPON_ERROR);
            }
            if (CollectionUtils.isEmpty(pointItemQueryDTORpcResponse.getData().getDetails())) {
                log.error("查询优惠券信息信息有误 PointExchangeCouponQuery:{},接口返回信息:{}", JSONObject.toJSONString(pointExchangeCouponQuery), JSONObject.toJSONString(pointItemQueryDTORpcResponse));
                return RestResponse.fail(PointExchangeResponseCode.COUPON_INFO_ERROR);
            }
            if (pointItemQueryDTORpcResponse.getData().getDetails().get(0).getPointCount() == null) {
                log.error("查询优惠券信息信息有误 PointExchangeCouponQuery:{},接口返回信息:{}", JSONObject.toJSONString(pointExchangeCouponQuery), JSONObject.toJSONString(pointItemQueryDTORpcResponse));
                return RestResponse.fail(PointExchangeResponseCode.COUPON_POINT_PRICE_ERROR);
            }
            if (StringUtils.isEmpty(pointItemQueryDTORpcResponse.getData().getPromCampaignDTO().getCampaignCode())) {
                log.error("查询优惠券信息信息有误 PointExchangeCouponQuery:{},接口返回信息:{}", JSONObject.toJSONString(pointExchangeCouponQuery), JSONObject.toJSONString(pointItemQueryDTORpcResponse));
                return RestResponse.fail(PointExchangeResponseCode.COUPON_INFO_ERROR);
            }
        } catch (Throwable e) {
            log.error("查询优惠券信息失败 PointExchangeCouponQuery:{}", JSONObject.toJSONString(pointExchangeCouponQuery), e);
            return RestResponse.fail(PointExchangeResponseCode.QUERY_COUPON_ERROR);
        }
        pointExchangeCouponQuery.setAssets(pointItemQueryDTORpcResponse.getData().getDetails().get(0).getPointCount());
        pointExchangeCouponQuery.setCouponCode(pointItemQueryDTORpcResponse.getData().getPromCampaignDTO().getCampaignCode());
        //改造逻辑 直接冻结积分,然后如果冻结成功就执行领券逻辑
        RpcResponse<Boolean> booleanRpcResponse = null;
        try {
            booleanRpcResponse = freezeAssets(pointExchangeCouponQuery);
            if (!booleanRpcResponse.isSuccess()) {
                log.error("冻结积分失败 PointExchangeCouponQuery:{},调用冻结接口返回信息:{}", JSONObject.toJSONString(pointExchangeCouponQuery), JSONObject.toJSONString(booleanRpcResponse));
                return RestResponse.fail(booleanRpcResponse.getFail().getCode(), booleanRpcResponse.getFail().getMessage());
            }
        } catch (Throwable e) {
            log.error("冻结积分失败 PointExchangeCouponQuery:{}", JSONObject.toJSONString(pointExchangeCouponQuery), e);
            return RestResponse.fail(PointExchangeResponseCode.FREEZE_POINT_ERROR);
        }

        if (booleanRpcResponse.getData().equals(Boolean.TRUE)) {
            return applyCoupon(pointExchangeCouponQuery);
        } else {
            return RestResponse.fail(PointExchangeResponseCode.APPLY_COUPON_ERROR);
        }
    }

    public RpcResponse<PointItemQueryDTO> queryPointCouponDetail(PointExchangeCouponQuery pointExchangeCouponQuery) {
        PointItemQueryReq pointItemQueryReq = new PointItemQueryReq();
        pointItemQueryReq.setBizId(pointExchangeCouponQuery.getCouponId());
        return pointItemQueryFacade.queryPointCouponDetail(pointItemQueryReq);
    }


    public RpcResponse<Boolean> freezeAssets(PointExchangeCouponQuery pointExchangeCouponQuery) {
        //1.冻结积分
        AcBookDeductDTO acBookDeductDTO = new AcBookDeductDTO();
        //幂等id
        acBookDeductDTO.setBizId(pointExchangeCouponQuery.getBizId());
        //这个是积分扣减类型需要在ChangeTypeEnum中加一个积分抽奖消耗的枚举
        acBookDeductDTO.setChangeType(pointExchangeCouponQuery.getChangeType());
        acBookDeductDTO.setCustId(pointExchangeCouponQuery.getCustId());
        acBookDeductDTO.setChangeAssets(pointExchangeCouponQuery.getAssets());
        return accountBookWriteFacade.freezeAssets(acBookDeductDTO);
    }

    public RestResponse applyCoupon(PointExchangeCouponQuery pointExchangeCouponQuery) {
        CouponApplyReq couponApplyReq = null;
        RpcResponse<CouponInstanceDTO> apply = null;
        try {
            //积分优惠券不需要权益配置
            //check(pointExchangeCouponQuery);
            //然后调用领取优惠券接口
            couponApplyReq = new CouponApplyReq();
            couponApplyReq.setCouponCode(pointExchangeCouponQuery.getCouponCode());
            couponApplyReq.setApplyUniqueId(pointExchangeCouponQuery.getBizId());
            couponApplyReq.setCustId(pointExchangeCouponQuery.getCustId());
            couponApplyReq.setApp(pointExchangeCouponQuery.getApp());
            if (isNewUser(pointExchangeCouponQuery.getCustId())) {
                couponApplyReq.setIsNewUser(Boolean.TRUE);
                couponApplyReq.setSpecificGroupId(SPECIFIC_GROUP_ID);
            }
            apply = promotionCouponFacade.apply(couponApplyReq);
        } catch (Throwable e) {
            log.error("领取优惠券失败,pointExchangeCouponQuery:{}", JSONObject.toJSONString(pointExchangeCouponQuery), e);
            sendMsg(pointExchangeCouponQuery);
            if (e instanceof FrontManagerException) {
                return RestResponse.fail(((FrontManagerException) e).getFrontendCare().getCode());
            } else {
                return RestResponse.fail(PointExchangeResponseCode.APPLY_COUPON_ERROR);
            }
        }

        if (apply.isSuccess()) {
            log.info("领取优惠券成功:{},优惠券信息:{}", JSONObject.toJSONString(couponApplyReq), JSONObject.toJSONString(apply));
            freezeDeduct(pointExchangeCouponQuery);
            return RestResponse.ok(I18NMessageUtils.getMessage("coupon.receive.success"));  //# "优惠券领取成功"
            //成功之后抵扣积分
        } else {
            log.error("领取优惠券失败:{},优惠券信息:{}", JSONObject.toJSONString(couponApplyReq), JSONObject.toJSONString(apply));
            //失败之后解冻积分
            sendMsg(pointExchangeCouponQuery);
            return RestResponse.fail(apply.getFail().getCode(), apply.getFail().getMessage());
        }
    }

    /**
     * 抵扣
     *
     * @param message
     * @return
     */
    public RpcResponse<Boolean> freezeDeduct(PointExchangeCouponQuery message) {
        RpcResponse<Boolean> booleanRpcResponse = null;
        try {
            AcBookDeductDTO acBookDeductDTO = new AcBookDeductDTO();
            acBookDeductDTO.setChangeAssets(message.getAssets());
            acBookDeductDTO.setCustId(message.getLoginCustId());
            //这个需要在changeTypeEnum中加入一个积分抽奖的枚举
            acBookDeductDTO.setChangeType(message.getChangeType());
            acBookDeductDTO.setBizId(message.getBizId());
            //这边直接扣减的话会有一个unfreeze流水不能为空的错误 ，也就是在扣减之前还需要先解冻
            booleanRpcResponse = accountBookWriteFacade.freezeDeduct(acBookDeductDTO);
            //因为抵扣和解冻操作是在领取优惠券接口之后所以不用返回错误码异常,只需要打日志
            if (booleanRpcResponse.isSuccess()) {
                log.info("抵扣积分成功,PointExchangeCouponQuery:{},调用抵扣接口返回信息:{}", JSONObject.toJSONString(message), JSONObject.toJSONString(booleanRpcResponse));
            } else {
                log.error("抵扣积分失败PointExchangeCouponQuery：{},调用抵扣接口返回信息:{}", JSONObject.toJSONString(message), JSONObject.toJSONString(booleanRpcResponse));
                //发送积分抵扣消息
                sendMsg(message);
            }
            return booleanRpcResponse;
        } catch (Throwable e) {
            log.error("抵扣积分失败PointExchangeCouponQuery：{}", JSONObject.toJSONString(message), e);
            //发送积分抵扣消息
            sendMsg(message);
        }
        return booleanRpcResponse;
    }


    public void sendMsg(PointExchangeCouponQuery message) {
        messageSendManager.sendMessage(message, pointExchangeCouponTopic, "pointExchangeCoupon");
    }

    private Boolean isNewUser(Long custId) {
        // 判断是否新人
        NewCustomerQueryReq req = new NewCustomerQueryReq();
        req.setLimit(limit);
        req.setId(custId);
        RpcResponse<Boolean> rpcResponse = customerReadExtFacade.queryIsNewCustomer(req);
        if (rpcResponse.isSuccess()) {
            return rpcResponse.getData();
        }
        return false;
    }

    private void check(PointExchangeCouponQuery command) {
        if (!ApplyCouponSceneEnum.AWARDS.getId().equals(command.getApp())) {
            return;
        }
        // check awards 权益领取校验
        // 1. 是否为等级配置的权益
        List<CustomerLevelConfigDTO> customerLevelConfigDTOList = customerAdapter.queryLevelConfig();
        if (CollectionUtils.isEmpty(customerLevelConfigDTOList)) {
            throw new FrontManagerException(LEVEL_COUPON_FAIL);
        }
        Set<Long> couponIdSet = customerLevelConfigDTOList.stream().filter(CustomerLevelConfigDTO -> CustomerLevelConfigDTO.getAwards() != null).map(CustomerLevelConfigDTO::getAwards)
                .flatMap(v -> v.getCoupons().stream()).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(couponIdSet)) {
            throw new FrontManagerException(LEVEL_COUPON_FAIL);
        }
        List<CouponInstanceDTO> couponInstanceDTOList = promotionAdapter.queryCouponByIds(couponIdSet);
        List<CouponInstanceDTO> filterList = couponInstanceDTOList.stream().filter(
                v -> command.getCouponCode().equals(v.getCouponCode())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filterList)) {
            throw new FrontManagerException(LEVEL_COUPON_FAIL);
        }
        // 2. 该权益是否已经领取过
        PageInfo<CouponInstanceDTO> pageInfo = promotionAdapter.queryCustomerCoupon(command.getCustId(),
                Sets.newHashSet(Arrays.asList(filterList.get(0).getCampaignId())));
        if (!pageInfo.isEmpty()) {
            throw new FrontManagerException(COUPON_ALREADY_APPLY);
        }
    }


    /**
     * 唯一幂等ID(用作积分冻结和积分记录抽奖流水，以及冻结消息中解冻和扣除积分)
     *
     * @param
     * @return
     */
    public String buildBizId() {
        StringBuilder builders = new StringBuilder();
        builders.append(bizIdPrefix).append('-')
                .append(UUID.randomUUID());
        return builders.toString();
    }


}
