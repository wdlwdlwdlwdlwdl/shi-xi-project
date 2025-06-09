package com.aliyun.gts.gmall.manager.front.customer.facade.impl;
import com.aliyun.gts.gmall.framework.utils.I18NMessageUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.aliyun.gts.gmall.center.item.api.dto.input.PointItemQueryReq;
import com.aliyun.gts.gmall.center.item.api.dto.output.PointItemQueryDTO;
import com.aliyun.gts.gmall.center.item.api.facade.PointItemQueryFacade;
import com.aliyun.gts.gmall.center.misc.api.utils.StatusEnum;
import com.aliyun.gts.gmall.center.user.api.dto.input.NewCustomerQueryReq;
import com.aliyun.gts.gmall.center.user.api.facade.CustomerReadExtFacade;
import com.aliyun.gts.gmall.framework.api.dto.PageInfo;
import com.aliyun.gts.gmall.framework.api.dto.PageParam;
import com.aliyun.gts.gmall.framework.api.rest.dto.RestResponse;
import com.aliyun.gts.gmall.framework.api.rpc.dto.RpcResponse;
import com.aliyun.gts.gmall.manager.front.common.exception.FrontManagerException;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.CustomerAdapter;
import com.aliyun.gts.gmall.manager.front.customer.adaptor.PromotionAdapter;
import com.aliyun.gts.gmall.manager.front.customer.converter.PromotionConverter;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.ApplyCouponRestCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.command.BatchApplyCouponRestCommand;
import com.aliyun.gts.gmall.manager.front.customer.dto.input.query.*;
import com.aliyun.gts.gmall.manager.front.customer.dto.output.*;
import com.aliyun.gts.gmall.manager.front.customer.dto.utils.ApplyCouponSceneEnum;
import com.aliyun.gts.gmall.manager.front.customer.dto.utils.CustomerFrontResponseCode;
import com.aliyun.gts.gmall.manager.front.customer.dto.utils.LockKeyConstants;
import com.aliyun.gts.gmall.manager.front.customer.enums.PromTargetTypeEnum;
import com.aliyun.gts.gmall.manager.front.customer.facade.PromotionFacade;
import com.aliyun.gts.gmall.manager.utils.PriceUtils;
import com.aliyun.gts.gmall.manager.utils.ResponseUtils;
import com.aliyun.gts.gmall.middleware.api.cache.CacheManager;
import com.aliyun.gts.gmall.middleware.api.cache.lock.DistributedLock;
import com.aliyun.gts.gmall.platform.promotion.api.dto.account.AcBookFailureDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.account.AcBookRecordDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.input.CouponApplyReq;
import com.aliyun.gts.gmall.platform.promotion.api.dto.input.CouponCampQueryReq;
import com.aliyun.gts.gmall.platform.promotion.api.dto.input.CouponQueryReq;
import com.aliyun.gts.gmall.platform.promotion.api.dto.model.PromCampaignDTO;
import com.aliyun.gts.gmall.platform.promotion.api.dto.output.CouponInstanceDTO;
import com.aliyun.gts.gmall.platform.promotion.api.facade.account.AccountBookReadFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.admin.GrBizGroupReadFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.admin.PromCampaignReadFacade;
import com.aliyun.gts.gmall.platform.promotion.api.facade.cust.PromotionCouponFacade;
import com.aliyun.gts.gmall.platform.promotion.common.query.PromCampaignQuery;
import com.aliyun.gts.gmall.platform.promotion.common.query.account.AcBookRecordQuery;
import com.aliyun.gts.gmall.platform.promotion.common.query.account.AccountBookQuery;
import com.aliyun.gts.gmall.platform.promotion.common.type.account.ChangeStepEnum;
import com.aliyun.gts.gmall.platform.user.api.dto.output.CustomerLevelConfigDTO;
import com.aliyun.gts.gmall.searcher.api.dto.input.ItemQueryRequest;
import com.aliyun.gts.gmall.searcher.api.dto.output.item.ItemSearchDTO;
import com.aliyun.gts.gmall.searcher.api.facade.ItemQueryFacade;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.aliyun.gts.gmall.manager.front.customer.dto.utils.CustomerFrontResponseCode.COUPON_ALREADY_APPLY;
import static com.aliyun.gts.gmall.manager.front.customer.dto.utils.CustomerFrontResponseCode.LEVEL_COUPON_FAIL;

/**
 * 营销接口
 *
 * @author GTS
 * @date 2021/03/12
 */
@Slf4j
@Service
public class PromotionFacadeImpl implements PromotionFacade {
    @Autowired
    private PromotionCouponFacade promotionCouponFacade;
    @Autowired
    private PromotionConverter    promotionConverter;
    @Autowired
    private AccountBookReadFacade accountBookReadFacade;
    @Autowired
    private CustomerAdapter       customerAdapter;
    @Autowired
    private PromotionAdapter      promotionAdapter;
    @Autowired
    private PromCampaignReadFacade promCampaignReadFacade;
    @Resource
    private PointItemQueryFacade pointItemQueryFacade;
    @Autowired
    private GrBizGroupReadFacade grBizGroupReadFacade;
    @Autowired
    private ItemQueryFacade itemQueryFacade;
    @Resource
    @Qualifier("cacheManager")
    private CacheManager cacheManager;
    @Autowired
    private CustomerReadExtFacade customerReadExtFacade;

    @NacosValue(value = "${front-manager.promotion.newuser.check.config.enable:false}", autoRefreshed = true)
    private Boolean promotionNewUserCheckConfigEnable;

    @NacosValue(value = "${front-manager.promotion.newuser.couponIds:285,287,309,310}", autoRefreshed = true)
    private String couponIdStrs;
    @Autowired
    private PromotionFacade promotionFacade;

    @NacosValue(value = "${front-manager.promotion.newuser.group.id:100}", autoRefreshed = true)
    private Long SPECIFIC_GROUP_ID;

    @NacosValue(value = "${front-manager.promotion.newuser.limit.day:30}", autoRefreshed = true)
    private Integer limit;

    @Override
    public RestResponse<CouponInstanceVO> applyCoupon(ApplyCouponRestCommand command) {
        this.check(command);
        command.setBizId(command.getCustId() + ":FM:" + System.currentTimeMillis());
        CouponApplyReq rpcReq = promotionConverter.convertApplyCouponRequest(command);
        if (command.getCustId() != null && isNewUser(command.getCustId())) {
            rpcReq.setIsNewUser(true);
            rpcReq.setSpecificGroupId(SPECIFIC_GROUP_ID);
        }
        RpcResponse<CouponInstanceDTO> r = promotionCouponFacade.apply(rpcReq);
        return ResponseUtils.convertVOResponse(r, promotionConverter::convertCouponInstance, true);
    }

    private void check(ApplyCouponRestCommand command) {
        if (!ApplyCouponSceneEnum.AWARDS.getId().equals(command.getApp())) {
            return;
        }
        // check awards 权益领取校验
        // 1. 是否为等级配置的权益
        List<CustomerLevelConfigDTO> customerLevelConfigDTOList = customerAdapter.queryLevelConfig();
        if (CollectionUtils.isEmpty(customerLevelConfigDTOList)) {
            throw new FrontManagerException(LEVEL_COUPON_FAIL);
        }
        Set<Long> couponIdSet = customerLevelConfigDTOList.stream()
            .filter(CustomerLevelConfigDTO->CustomerLevelConfigDTO.getAwards()!=null)
            .map(CustomerLevelConfigDTO::getAwards)
            .flatMap(v -> v.getCoupons().stream())
            .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(couponIdSet)) {
            throw new FrontManagerException(LEVEL_COUPON_FAIL);
        }
        List<CouponInstanceDTO> couponInstanceDTOList = promotionAdapter.queryCouponByIds(couponIdSet);
        List<CouponInstanceDTO> filterList = couponInstanceDTOList.stream()
            .filter(v -> command.getCouponCode().equals(v.getCouponCode()))
            .collect(Collectors.toList());
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

    @Override
    public CouponInstanceVO queryCoupon(ByCodeCouponQuery query) {
        CouponCampQueryReq rpcReq = new CouponCampQueryReq();
        rpcReq.setCouponCode(query.getCode());
        //查询优惠券
        RpcResponse<List<CouponInstanceDTO>> r = promotionCouponFacade.queryCouponCampaign(rpcReq);
        if (r.isSuccess() != true || CollectionUtils.isEmpty(r.getData())) {
            return null;
        }
        CouponInstanceVO vo = promotionConverter.convertCouponInstance(r.getData().get(0));
        return vo;
    }

    /**
     * 根据code查询券
     *
     * @param query
     * @return
     */
    @Override
    public  CouponInstanceDetailVO queryCouponDetail(ByCodeCouponQuery query) {
        CouponCampQueryReq rpcReq = new CouponCampQueryReq();
        rpcReq.setCouponCode(query.getCode());
        //查询优惠券
        RpcResponse<List<CouponInstanceDTO>> r = promotionCouponFacade.queryCouponCampaign(rpcReq);
        if (r.isSuccess() != true || CollectionUtils.isEmpty(r.getData())) {
            return null;
        }
        CouponInstanceDetailVO vo = promotionConverter.convertCouponInstanceDetail(r.getData().get(0));
        return vo;
    }

    @Override
    public RestResponse<PageInfo<CouponInstanceVO>> queryCoupons(CustomerCouponQuery query) {
        CouponQueryReq rpcReq = convertCouponQuery(query);
        RpcResponse<PageInfo<CouponInstanceDTO>> r = promotionCouponFacade.page(rpcReq);
        return ResponseUtils.convertVOPageResponse(r, promotionConverter::convertCouponInstance, false);
    }

    @Override
    public RestResponse<Long> queryAccountBook(AccountBookRestQuery query) {
        RpcResponse<Long> r = accountBookReadFacade.queryTotalAssets(convertAccountBookQuery(query));
        return ResponseUtils.convertVOResponse(r, Function.identity(), false);
    }

    @Override
    public RestResponse<PageInfo<AcBookRecordVO>> queryAcBookRecords(AccountBookRecordRestQuery query) {
        AcBookRecordQuery rpcQuery = new AcBookRecordQuery();
        rpcQuery.setCustId(query.getCustId());
        rpcQuery.setPage(query.getPage());
        rpcQuery.setChangeType(query.getChangeType());
        rpcQuery.setChangeDirection(query.getChangeDirection());
        //排除取消的数据
        rpcQuery.setExcludeChangeStep(ChangeStepEnum.CANCEL.getCode());
        RpcResponse<PageInfo<AcBookRecordDTO>> r = accountBookReadFacade.queryRecords(rpcQuery);
        return ResponseUtils.convertVOPageResponse(r, promotionConverter::convertAcBookRecord, false);
    }

    @Override
    public RestResponse<CouponBatchApplyRetVO> batchApplyCoupon(BatchApplyCouponRestCommand command) {
        DistributedLock lock = null;
        try {
            if (promotionNewUserCheckConfigEnable) {
                if (this.checkCoupon(command.couponMap)) {
                    return RestResponse.fail(CustomerFrontResponseCode.COUPON_APPLY_FAIL.getCode(),
                            CustomerFrontResponseCode.COUPON_APPLY_FAIL.getMessage());
                }
            }
            // 是新人分组
            boolean isNewUser = isNewUser(command.getCustId());
            if (SPECIFIC_GROUP_ID.equals(command.getGroupId())) {
                if (!isNewUser) {
                    return RestResponse.fail(
                        CustomerFrontResponseCode.PROMOTION_IS_JOINED.getCode(),
                        CustomerFrontResponseCode.PROMOTION_IS_JOINED.getMessage()
                    );
                }
            }
            String lockKey = StringUtils.join(LockKeyConstants.NEW_USER_COUPON_KEY_PREFIX, command.getCustId());
            lock = cacheManager.getLock(lockKey);
            if (!lock.tryLock(LockKeyConstants.WAIT_TIME, LockKeyConstants.LEASE_TIME, TimeUnit.SECONDS)) {
                log.warn("获取锁超时或失败,key={}", lockKey);
                return RestResponse.fail(CustomerFrontResponseCode.COUPON_APPLY_FAIL.getCode(), CustomerFrontResponseCode.COUPON_APPLY_FAIL.getMessage());
            }
            List<CouponInstanceVO> couponInstanceVOList = new ArrayList<>();

            // 该权益是否已经领取过
            CouponQueryReq couponQueryReq = new CouponQueryReq();
            couponQueryReq.setCustId(command.getCustId());
            couponQueryReq.setPage(new PageParam());
            couponQueryReq.setCampaignIds(Lists.newArrayList(command.getCouponMap().keySet()));
            RpcResponse<PageInfo<CouponInstanceDTO>> pageInfoRpcResponse = promotionCouponFacade.page(couponQueryReq);
            List<Long> existsCouponIdList = new ArrayList<>();
            if (pageInfoRpcResponse != null && pageInfoRpcResponse.getData() != null && !CollectionUtils.isEmpty(pageInfoRpcResponse.getData().getList())) {
                existsCouponIdList = pageInfoRpcResponse.getData().getList().stream().map(CouponInstanceDTO::getCampaignId).collect(Collectors.toList());
            }
            Set<Map.Entry<Long, String>> entries = command.couponMap.entrySet();
            Iterator<Map.Entry<Long, String>> it = entries.iterator();
            while (it.hasNext()) {
                Map.Entry<Long, String> longStringEntry = it.next();
                CouponInstanceVO couponInstanceVO = new CouponInstanceVO();
                couponInstanceVO.setCouponCode(longStringEntry.getValue());
                couponInstanceVO.setCampaignId(longStringEntry.getKey());
                // 开始领券
                if (CollectionUtils.isEmpty(existsCouponIdList) || !existsCouponIdList.contains(longStringEntry.getKey())) {
                    CouponApplyReq rpcReq = new CouponApplyReq();
                    rpcReq.setApp(command.getApp());
                    rpcReq.setCouponCode(longStringEntry.getValue());
                    rpcReq.setCustId(command.getCustId());
                    rpcReq.setApplyUniqueId(command.getCustId() + ":FM:" + System.currentTimeMillis());
                    if (isNewUser) {
                        rpcReq.setIsNewUser(true);
                        rpcReq.setSpecificGroupId(SPECIFIC_GROUP_ID);
                    }
                    RpcResponse<CouponInstanceDTO> rpcResponse = promotionCouponFacade.apply(rpcReq);
                    if (rpcResponse.isSuccess()) {
                        couponInstanceVO = promotionConverter.convertCouponInstance(rpcResponse.getData());
                        couponInstanceVO.setReceiveMsg(I18NMessageUtils.getMessage("receive.success"));  //# "领取成功"
                        couponInstanceVO.setReceived(true);
                    } else {
                        couponInstanceVO.setReceiveMsg(I18NMessageUtils.getMessage("receive.fail")+"：" + rpcResponse.getFail());  //# "领取失败
                    }
                } else {
                    couponInstanceVO.setReceived(true);
                    couponInstanceVO.setReceiveMsg(I18NMessageUtils.getMessage("receive.fail")+"："+I18NMessageUtils.getMessage("already.received.coupon")+"。");  //# "领取失败：已领过该券
                }
                couponInstanceVOList.add(couponInstanceVO);
            }

            /**
             * 1、全部领取成功--》领取成功  不可再次领取
             * 2、部分领取成功，部分已领取--》领取成功  不可再次领取
             * 3、部分领取成功，部分失败--》部分领取成功，可再次领取
             * 4、全部已领取 -- 》 不能再次领取
             * 5、全部失败--》当前领取人数较多请稍后再试？ 可以再次领取
             */
            TreeSet<Boolean> receiveds = getReceiveds(couponInstanceVOList);
            if (receiveds.size() > 1) {
                logUnsuccess(couponInstanceVOList, command);
                CouponBatchApplyRetVO couponBatchApplyRetVO = new CouponBatchApplyRetVO(
                    CustomerFrontResponseCode.COUPON_APPLY_PART_SUCCESS.getCode(),
                    CustomerFrontResponseCode.COUPON_APPLY_PART_SUCCESS.getMessage()
                );
                return RestResponse.fail(
                    CustomerFrontResponseCode.COUPON_APPLY_PART_SUCCESS.getCode(),
                    CustomerFrontResponseCode.COUPON_APPLY_PART_SUCCESS.getMessage()
                );
            }
            if (receiveds.size() == 1 && receiveds.first()) {
                CouponBatchApplyRetVO couponBatchApplyRetVO = new CouponBatchApplyRetVO(
                    CustomerFrontResponseCode.COUPON_APPLY_SUCCESS.getCode(),
                    CustomerFrontResponseCode.COUPON_APPLY_SUCCESS.getMessage()
                );
                return RestResponse.ok(couponBatchApplyRetVO);
            }
            logUnsuccess(couponInstanceVOList, command);
            return RestResponse.fail(
                CustomerFrontResponseCode.COUPON_APPLY_FAIL.getCode(),
                CustomerFrontResponseCode.COUPON_APPLY_FAIL.getMessage()
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return RestResponse.FAIL_SERVER_ERROR;
        } finally {
            if (lock != null) {
                lock.unLock();
            }
        }
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

    private boolean checkCoupon(Map<Long, String> couponMap) {
        String[] couponIds = couponIdStrs.split(",");
        List<Long> couponIdList = Arrays.asList(couponIds).stream().map(Long::valueOf).collect(Collectors.toList());
        List<Long> longs = Lists.newArrayList(couponMap.keySet());
        for (Long id : longs) {
            if (!couponIdList.contains(id)) {
                return false;
            }
        }
        return true;
    }

    private static void logUnsuccess(List<CouponInstanceVO> couponInstanceVOList, BatchApplyCouponRestCommand command) {
        log.error("batchApplyCoupon log unsuccess; userId = " + command.getCustId() +
                ";couponInstanceVOList = " + JSONObject.toJSONString(couponInstanceVOList));
    }

    private static TreeSet<Boolean> getReceiveds(List<CouponInstanceVO> couponInstanceVOList) {
        TreeSet<Boolean> received = new TreeSet<>();
        couponInstanceVOList.forEach(couponInstanceVO -> {
            received.add(couponInstanceVO.getReceived());
        });
        return received;
    }

    @Override
    public RestResponse<CouponInstanceReceiveVO> queryNewUserCoupons(CouponAndCampaignQuery query) {
        // 可领的券
        CouponCampQueryReq req = new CouponCampQueryReq();
        req.setCampaignIds(query.getCouponIds());
        if (CollectionUtils.isEmpty(query.getCouponIds())) {
            String[] couponIds = couponIdStrs.split(",");
            List<Long> couponIdList = Arrays.asList(couponIds).stream().map(Long::valueOf).collect(Collectors.toList());
            req.setCampaignIds(couponIdList);
        }
        CouponInstanceReceiveVO couponInstanceReceiveVO = new CouponInstanceReceiveVO();
        List<CouponInstanceDTO> couponInstanceDTOList = promotionAdapter.queryCouponByIds(new HashSet<>(req.getCampaignIds()));
        if (CollectionUtils.isEmpty(couponInstanceDTOList)) {
            couponInstanceReceiveVO.setReceived(true);
        } else {
            // 是否已领取
            CouponQueryReq couponQueryReq = new CouponQueryReq();
            couponQueryReq.setCustId(query.getCustId());
            couponQueryReq.setPage(new PageParam());
            couponQueryReq.setCampaignIds(req.getCampaignIds());
            RpcResponse<PageInfo<CouponInstanceDTO>> pageInfoRpcResponse = promotionCouponFacade.page(couponQueryReq);
            final List<Long> existsCouponIdList = new ArrayList<>();
            if (pageInfoRpcResponse != null && pageInfoRpcResponse.getData() != null && !CollectionUtils.isEmpty(pageInfoRpcResponse.getData().getList())) {
                existsCouponIdList.addAll(pageInfoRpcResponse.getData().getList().stream().map(CouponInstanceDTO::getCampaignId).collect(Collectors.toList()));
            }
            couponInstanceDTOList.stream().forEach(e -> {
                CouponInstanceVO couponInstanceVO = promotionConverter.convertCouponInstance(e);
                if (existsCouponIdList.contains(e.getCampaignId())) {
                    couponInstanceVO.setReceived(true);
                } else {
                    couponInstanceReceiveVO.setReceived(false);
                }
                couponInstanceReceiveVO.getCouponInstanceVOList().add(couponInstanceVO);
            });
        }
        return RestResponse.okWithoutMsg(couponInstanceReceiveVO);
    }


    private void setItemInfoWithPredifinedItems(PromCampaignVO promCampaignVO, Map<String, ItemSearchDTO> itemMap) {
        for (PromDetailVO promDetailVO : promCampaignVO.getDetails()) {
            if (promDetailVO.getItemId() == null || promDetailVO.getItemId() <= 0) {
                continue;
            }
            ItemSearchDTO itemSearchDTO = itemMap.get(promDetailVO.getItemId().toString());
            JSONObject object = buildItem(promDetailVO.getItemId(), itemSearchDTO);
            promDetailVO.setItemInfo(object);
        }
    }

    private List<String> getItemIds(List<PromDetailVO> details) {
        List<String> itemIds = Lists.newArrayList();
        for (PromDetailVO promDetailVO : details) {
            if (promDetailVO.getItemId() == null) {
                continue;
            }
            itemIds.add(promDetailVO.getItemId().toString());
        }
        return itemIds;
    }

    private AccountBookQuery convertAccountBookQuery(AccountBookRestQuery query) {
        AccountBookQuery rpcQuery = new AccountBookQuery();
        rpcQuery.setCustId(query.getCustId());
        return rpcQuery;
    }

    private CouponQueryReq convertCouponQuery(CustomerCouponQuery query) {
        CouponQueryReq req = new CouponQueryReq();
        req.setCustId(query.getCustId());
        req.setStatus(query.getStatus());
        req.setPage(query.getPage());
        /**
         * <li>未使用：按照领券时间降序排列。传入"gmt_create"</li>
         * <li>已使用：按照使用时间降序排列。传入"gmt_modified"</li>
         * <li>已过期：按照失效时间降序排列。传入"end_time"</li>
         */
        if (null != query.status){
            switch (query.getStatus()) {
                case 99 : // 已使用列表
                    req.setOrderBy("gmt_modified");
                    break;
                case 9 : // 已失效列表
                    req.setOrderBy("end_time");
                    break;
                case 1 : // 未使用
                    req.setOrderBy("end_time");
                    break;
                default:
                    req.setOrderBy("end_time");
                    break;
            }
        }
        return req;
    }


    @Override
    public RestResponse<PromCampaignVO> queryCouponDetail(ByIdCouponQuery query) {
        PromCampaignQuery promCampaignQuery = new PromCampaignQuery();
        promCampaignQuery.setCampId(query.getCouponId());
        RpcResponse<PromCampaignDTO> promCampaignResp;
        try {
            promCampaignResp = promCampaignReadFacade.queryDetail(promCampaignQuery);
            if (!promCampaignResp.isSuccess()) {
                return RestResponse.fail(promCampaignResp.getFail().getCode(), promCampaignResp.getFail().getMessage());
            }
            PromCampaignDTO promCampaignDTO = promCampaignResp.getData();
            PromCampaignVO promCampaignVO = promotionConverter.toDefaultVO(promCampaignDTO);
            fillItemInfo(promCampaignVO, query);
            //查询该优惠券是否是积分商品优惠券
            PointItemQueryReq pointItemQueryReq=new PointItemQueryReq();
            pointItemQueryReq.setBizId(query.getCouponId());
            RpcResponse<PointItemQueryDTO> pointItemQueryDTORpcResponse=  pointItemQueryFacade.getPointCouponDetail(pointItemQueryReq);
            if(Objects.nonNull(pointItemQueryDTORpcResponse)&&pointItemQueryDTORpcResponse.isSuccess()){
                PointItemQueryDTO pointItemQueryDTO=  pointItemQueryDTORpcResponse.getData();
                if(Objects.nonNull(pointItemQueryDTO)){
                    promCampaignVO.setItemDesc(pointItemQueryDTO.getItemDesc());
                    promCampaignVO.setPicture(pointItemQueryDTO.getPicture());
                }
            }

            return RestResponse.ok(promCampaignVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return RestResponse.FAIL_SERVER_ERROR;
        }
    }

    private void fillItemInfo(PromCampaignVO promCampaignVO, ByIdCouponQuery query) {
        if (CollectionUtils.isEmpty(promCampaignVO.getDetails())) {
            return;
        }

        ItemQueryRequest itemQueryRequest = buildSearchReq(promCampaignVO, query);
        if (itemQueryRequest == null) {
            return;
        }
        Map<String, ItemSearchDTO> itemMap = querySearchWithStrategy(itemQueryRequest);
        List<PromDetailVO> newDetailVOList = Lists.newArrayList();
        for (Map.Entry<String, ItemSearchDTO> entry : itemMap.entrySet()) {
            JSONObject object = buildItem(Long.valueOf(entry.getKey()), entry.getValue());
            PromDetailVO promDetailVO = new PromDetailVO();
            promDetailVO.setItemId(Long.valueOf(entry.getKey()));
            promDetailVO.setItemInfo(object);
            newDetailVOList.add(promDetailVO);
        }
        promCampaignVO.setDetails(newDetailVOList);
    }

    private ItemQueryRequest buildSearchReq(PromCampaignVO promCampaignVO, ByIdCouponQuery query) {
        PromTargetTypeEnum promTargetTypeEnum = PromTargetTypeEnum.find(promCampaignVO.getPromTargetType());
        if (promTargetTypeEnum == null) {
            log.error("illegal PromTargetType = " + promCampaignVO.getPromTargetType());
            return null;
        }
        return promTargetTypeEnum.buildSearchReq(promCampaignVO, query, grBizGroupReadFacade);
    }

    private Map<String, ItemSearchDTO> querySearchWithStrategy(ItemQueryRequest itemQueryRequest) {
        itemQueryRequest.setStatus(StatusEnum.ON.getCode());
        RpcResponse<PageInfo<ItemSearchDTO>> response = itemQueryFacade.queryItem(itemQueryRequest);
        if (response == null || response.isSuccess() == false) {
            return Maps.newHashMap();
        }
        if (response.getData() == null || response.getData().getList() == null) {
            return Maps.newHashMap();
        }
        //接解析结果
        convertPrice(response.getData());
        Map<String, ItemSearchDTO> result = new TreeMap<>();
        for (ItemSearchDTO item : response.getData().getList()) {
            result.put(item.getItemId(), item);
        }
        return result;
    }

    private void convertPrice(PageInfo<ItemSearchDTO> pageInfo) {
        if (pageInfo == null || pageInfo.getList() == null) {
            return;
        }
        for (ItemSearchDTO itemDTO : pageInfo.getList()) {
            itemDTO.setPrice(String.valueOf(itemDTO.getPrice()));
            itemDTO.setMaxPrice(String.valueOf(itemDTO.getMaxPrice()));
        }
    }

    private static JSONObject buildItem(Long itemId, ItemSearchDTO itemDTO) {
        JSONObject object = new JSONObject();
        object.put("itemId", itemId);
        if (itemDTO == null) {
            return object;
        }
        object.put("mainPicture", itemDTO.getMainPicture());
        object.put("itemTitle", itemDTO.getItemTitle());
        object.put("price", itemDTO.getPrice());
        object.put("maxPrice", itemDTO.getMaxPrice());
        object.put("itemId", itemDTO.getItemId());
        return object;
    }

    @Override
    public RestResponse<AcBookFailureDTO> getAccountBookFailure(AccountBookRestQuery query) {
        RpcResponse<AcBookFailureDTO> r = accountBookReadFacade.queryLastFailed(convertAccountBookQuery(query));
        return ResponseUtils.convertVOResponse(r, Function.identity(), false);
    }
}
